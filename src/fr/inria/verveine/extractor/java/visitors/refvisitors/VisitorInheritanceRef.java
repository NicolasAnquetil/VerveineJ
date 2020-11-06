package fr.inria.verveine.extractor.java.visitors.refvisitors;

import java.util.Collection;
import java.util.LinkedList;

import fr.inria.verveine.extractor.java.visitors.SummarizingClassesAbstractVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import org.moosetechnology.model.famix.famix.ContainerEntity;
import org.moosetechnology.model.famix.famix.Inheritance;
import org.moosetechnology.model.famix.famix.Namespace;
import org.moosetechnology.model.famix.famix.ParameterizableClass;
import org.moosetechnology.model.famix.famix.Type;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.utils.Util;

/** A visitor to record inheritance relationships.<br>
 * It is simpler than the other ref visitors
 * @author anquetil
 */
public class VisitorInheritanceRef extends SummarizingClassesAbstractVisitor {

	public VisitorInheritanceRef(JavaDictionary dico, boolean classSummary) {
		super(dico, classSummary);
	}

	public boolean visit(TypeDeclaration node) {
		org.moosetechnology.model.famix.famix.Class fmx = visitTypeDeclaration(node);
		ITypeBinding bnd = node.resolveBinding();
		if ((fmx != null) && (bnd != null)) {
			ensureInheritances(bnd, fmx);

			return super.visit(node);
		} else {
			return false;
		}
	}

	public void endVisit(TypeDeclaration node) {
		endVisitTypeDeclaration(node);
		super.endVisit(node);
	}

	public boolean visit(ClassInstanceCreation node) {
		// used to get the name of the super type of the anonymous class
		visitClassInstanceCreation(node);
		return super.visit(node);
	}

	public boolean visit(AnonymousClassDeclaration node) {

		ITypeBinding bnd = node.resolveBinding();
		org.moosetechnology.model.famix.famix.Class fmx = this.dico.getFamixClass(bnd, Util.stringForAnonymousName(getAnonymousSuperTypeName(), context), /*owner*/(ContainerEntity)context.top());

		if ( (fmx != null) && (bnd != null) && (! classSummary) ){
			ensureInheritances(bnd, fmx);

			this.context.pushType(fmx);
			return super.visit(node);
		} else {
			return false;
		}
	}

	public void endVisit(AnonymousClassDeclaration node) {
		visitAnonymousClassDeclaration(node);
		super.endVisit(node);
	}

	public boolean visit(EnumDeclaration node) {
		ITypeBinding bnd = node.resolveBinding();
		org.moosetechnology.model.famix.famix.Enum fmx = dico.getFamixEnum(bnd, node.getName().getIdentifier(), (ContainerEntity) context.top());

		if ( (fmx != null) && (bnd != null) ){
			// --------------- implicit superclass java.lang.Enum<> cannot use ensureInheritances(bnd,fmx)
			Type sup;
			ITypeBinding supbnd = null;
			if (bnd != null) {
				supbnd = bnd.getSuperclass();
			}
			if (supbnd != null) {
				sup = dico.ensureFamixType(supbnd, /*alwaysPersist*/true);
			}
			else {
				Namespace javaLang = dico.ensureFamixNamespaceJavaLang(null);
				ParameterizableClass generic = (ParameterizableClass) dico.ensureFamixClass(/*bnd*/null, /*name*/"Enum", /*owner*/javaLang, /*isGeneric*/true, /*modifiers*/Modifier.ABSTRACT&Modifier.PUBLIC, /*alwaysPersist*/true);
				sup = dico.ensureFamixParameterizedType(/*bnd*/null, /*name*/"Enum", generic, /*ctxt*/(ContainerEntity)context.top(), /*alwaysPersist*/true);
			}
			dico.ensureFamixInheritance(sup, fmx, /*lastInheritance*/null);

			this.context.pushType(fmx);
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	public void endVisit(EnumDeclaration node) {
		endVisitEnumDeclaration( node);
		super.endVisit(node);
	}

	public boolean visit(AnnotationTypeDeclaration node) {
		if (visitAnnotationTypeDeclaration( node) != null) {
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	public void endVisit(AnnotationTypeDeclaration node) {
		endVisitAnnotationTypeDeclaration( node);
		super.endVisit(node);
	}

	public boolean visit(MethodDeclaration node) {
		if (visitMethodDeclaration( node) != null) {
			return super.visit(node);
		} else {
			return false;
		}
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		endVisitMethodDeclaration(node);
	}

	@Override
	public boolean visit(Initializer node) {
		if (visitInitializer(node) != null) {
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	@Override
	public void endVisit(Initializer node) {
		endVisitInitializer(node);
	}

	// UTILITY METHODS

	protected void ensureInheritances(ITypeBinding bnd, org.moosetechnology.model.famix.famix.Type fmx) {
		Inheritance lastInheritance = null;

		// --------------- superclass
		Collection<Type> sups = new LinkedList<Type>();
		if (! bnd.isInterface()) {
			ITypeBinding supbnd = bnd.getSuperclass();
			if (supbnd != null) {
				sups.add(dico.ensureFamixType(supbnd, /*persistIt)*/true));
			}
			else {
				sups.add( dico.ensureFamixClassObject(null));
			}
		}
		// --------------- interfaces implemented
		for (ITypeBinding intbnd : bnd.getInterfaces()) {
			sups.add( dico.ensureFamixType(intbnd, /*ctxt*/(ContainerEntity)context.top(), /*persistIt)*/true));
		}

		for (Type sup : sups) {
			lastInheritance = dico.ensureFamixInheritance(sup, fmx, lastInheritance);
			// create FileAnchor for each inheritance link ???
		}
	}

}
