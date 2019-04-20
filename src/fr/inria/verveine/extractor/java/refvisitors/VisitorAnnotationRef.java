package fr.inria.verveine.extractor.java.refvisitors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMemberValuePairBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import eu.synectique.verveine.core.gen.famix.AnnotationInstanceAttribute;
import eu.synectique.verveine.core.gen.famix.AnnotationType;
import eu.synectique.verveine.core.gen.famix.AnnotationTypeAttribute;
import eu.synectique.verveine.core.gen.famix.BehaviouralEntity;
import eu.synectique.verveine.core.gen.famix.ContainerEntity;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.NamedEntity;
import eu.synectique.verveine.core.gen.famix.Reference;
import eu.synectique.verveine.core.gen.famix.StructuralEntity;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.SummarizingClassesAbstractVisitor;
import fr.inria.verveine.extractor.java.VerveineJParser;

/**
 * <p>Recovers annotations from the IBinding of various kind of entities:
 * <ul>
 * <li>Package bindings - annotations on a package declaration.
 * <li>Type bindings - annotations on a class, interface, enum, or annotation type declaration
 * <li>Method bindings - annotations on a method or constructor declaration
 * <li>Variable bindings - annotations on a field, enum constant, or formal parameter declaration
 * </ul></p>
 * <p>This visitor is different from the other in that it works with the binding.
 * So, for example, there is no need to keep the stack of declared entities.
 * Yet it still inherits from SummarizingClassesAbstractVisitor to know if we should summarize model at the level of classes</p>
 * @author anquetil
 */
public class VisitorAnnotationRef extends SummarizingClassesAbstractVisitor {

	public VisitorAnnotationRef(JavaDictionary dico, boolean classSummary) {
		super(dico, classSummary);
	}

	// VISITOR METHODS

	public boolean visit(PackageDeclaration node) {
		createAnnotationInstances(node.resolveBinding());
		return false; // no need to visit children of the declaration (which is the name of the package)
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		createAnnotationInstances(node.resolveBinding());
		return true;
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		createAnnotationInstances(node.resolveBinding());
		return true;
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		createAnnotationInstances(node.resolveBinding());
		return true;
	}

	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		createAnnotationInstances(node.resolveBinding());
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(FieldDeclaration node) {
		for (VariableDeclaration vd : (List<VariableDeclaration>) node.fragments()) {
			createAnnotationInstances(vd.resolveBinding());
		}
		return false;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		createAnnotationInstances(node.resolveBinding());
		return true;
	}

	// UTILITY METHODS

	/**
	 * Adds possible annotation instances to a Famix NamedEntity with the given binding
	 * @param bnd -- IBinding of an entity (possibly null)
	 * @param fmx -- corresponding famix entity (possibly null)
	 * @param persistIt  -- whether to persist or not the type
	 */
	private void createAnnotationInstances(IBinding bnd) {
		NamedEntity fmx;
		if (bnd != null) {
			for (IAnnotationBinding annBnd : bnd.getAnnotations()) {
				// create type of the annotation
				AnnotationType annType = dico.ensureFamixAnnotationType(annBnd.getAnnotationType(), /*name*/null, /*owner*/null, ! classSummary);

				// create all parameters of the annotation instance
				Collection<AnnotationInstanceAttribute> annAtts = new ArrayList<AnnotationInstanceAttribute>();

				for (IMemberValuePairBinding annPV : annBnd.getDeclaredMemberValuePairs()) {
					// maybe should test that return of annInstAtt(...) is not null (and not add it in this case)
					annAtts.add(annInstAtt(annPV, annType));
				}

				// add the annotation instance to the Famix entity, may be if fmx==null we should not even create the AnnotationInstanceType ?
				fmx = dico.getEntityByKey(bnd);
				if (fmx != null) {
					dico.addFamixAnnotationInstance(fmx, annType, annAtts);
				}
			}
		}
	}

	/**
	 * creates an annotationInstance attribute
	 * @param annPV -- Value pair: name of the attribute and its value (may be null)
	 * @param annType -- the annotation type instantiated
	 * @param persistIt -- whether to persist the data
	 * @return the AnnotationInstanceAttribute created or null
	 */
	private AnnotationInstanceAttribute annInstAtt(IMemberValuePairBinding annPV, AnnotationType annType) {
		Object attVal = annPV.getValue();
		String attFamixVal = "";
		if (attVal == null) {
			return null;
		}

		if (attVal.getClass() == Object[].class) {
			int nbVal = 0;
			for (Object val : ((Object[])attVal)) {
				attFamixVal += (nbVal>0?", " : "") + annInstAttValAsString( val );
				nbVal++;
			}
			if (nbVal != 1) {  // '0' => {} ; '>=1' => {val, val, ...}
				attFamixVal = "{" + attFamixVal + "}";
			}
		}
		else {
			attFamixVal = annInstAttValAsString(attVal);
		}
		AnnotationTypeAttribute annoAtt = dico.ensureFamixAnnotationTypeAttribute(annPV.getMethodBinding(), /*name*/annPV.getName(), /*owner*/annType, /*persistIt*/!classSummary);
		return( dico.createFamixAnnotationInstanceAttribute(annoAtt, attFamixVal) );
	}

	/**
	 * represents the value of an AnnotationInstanceAttribute as a String
	 * @param attVal
	 * @return
	 */
	private String annInstAttValAsString(Object attVal) {
		String attFamixVal;
		if (attVal instanceof ITypeBinding) {
			// for Annotation attributes of the form <someclass>.class,
			// attVal may contains the entire declaration of the class
			// we want just its name
			attFamixVal = ((ITypeBinding)attVal).getName() + ".class";
		}
		else {
			attFamixVal = attVal.toString();
		}
		return attFamixVal;
	}

}