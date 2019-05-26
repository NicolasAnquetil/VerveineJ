package fr.inria.verveine.extractor.java.defvisitors;

import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import eu.synectique.verveine.core.gen.famix.AnnotationType;
import eu.synectique.verveine.core.gen.famix.AnnotationTypeAttribute;
import eu.synectique.verveine.core.gen.famix.ContainerEntity;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.SummarizingClassesAbstractVisitor;
import fr.inria.verveine.extractor.java.VerveineJParser;
import fr.inria.verveine.extractor.java.VerveineJParser.anchorOptions;
import fr.inria.verveine.extractor.java.utils.Util;

/**
 * AST Visitor that defines all the (Famix) entities of interest
 * Famix entities are stored in a Map along with the IBindings to which they correspond
 */
public class VisitorAnnotationDef extends SummarizingClassesAbstractVisitor {

	/**
	 * what sourceAnchors to create
	 */
	protected anchorOptions anchors;

	public VisitorAnnotationDef(JavaDictionary dico, boolean classSummary, anchorOptions anchors) {
		super( dico, classSummary);
		this.anchors = anchors;
	}

	// VISITOR METHODS

	@Override
	public boolean visit(CompilationUnit node) {
		visitCompilationUnit(node);
		return super.visit(node);
	}

	@Override
	public void endVisit(CompilationUnit node) {
		endVisitCompilationUnit(node);
	}

	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		ITypeBinding bnd = node.resolveBinding();
		AnnotationType fmx = dico.ensureFamixAnnotationType(bnd, node.getName().getIdentifier(), (ContainerEntity) context.top(), persistClass(bnd));
		if (fmx != null) {
			Util.recursivelySetIsStub(fmx, false);
			if (anchors != anchorOptions.none) {
				dico.addSourceAnchor(fmx, node, /*oneLineAnchor*/false);
			}

			context.pushType(fmx);
			return super.visit(node);
		}
		else {
			context.pushType(null);
			return false;
		}
	}

	@Override
	public void endVisit(AnnotationTypeDeclaration node) {
		this.context.popType();
		super.endVisit(node);
	}

	@Override
	public boolean visit(AnnotationTypeMemberDeclaration node) {
//		System.err.println("TRACE, Visiting AnnotationTypeMemberDeclaration: "+node.getName().getIdentifier());
		IMethodBinding bnd = node.resolveBinding();

		AnnotationTypeAttribute fmx = dico.ensureFamixAnnotationTypeAttribute(bnd, node.getName().getIdentifier(), (AnnotationType) context.topType(), persistClass(null));
		if (fmx != null) {
			fmx.setIsStub(false);
			if (anchors != anchorOptions.none) {
				dico.addSourceAnchor(fmx, node, /*oneLineAnchor*/false);
			}

			context.pushAnnotationMember(fmx);
			return super.visit(node);
		} else {
			context.pushAnnotationMember(null);
			return false;
		}
	}

	@Override
	public void endVisit(AnnotationTypeMemberDeclaration node) {
		this.context.popAnnotationMember();
		super.endVisit(node);
	}

}