package fr.inria.verveine.extractor.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import eu.synectique.verveine.core.EntityStack;
import eu.synectique.verveine.core.gen.famix.AnnotationType;
import eu.synectique.verveine.core.gen.famix.AnnotationTypeAttribute;
import eu.synectique.verveine.core.gen.famix.ContainerEntity;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.extractor.java.utils.Util;

/**
 * This visitor offers methods to recover FamixEntities from the main JDT Entities and may put them in the context Stack<br>
 * To avoid confusion with the ASTVisitor defaults visit(...) methods, and to simplify the choice of using them or not in each subclass,
 * the recovering methods here are named after the visited entities: visit(CompilationUnit node) becomes visitCompilationUnit(CompilationUnit node).
 */
public abstract class GetVisitedEntityAbstractVisitor extends ASTVisitor {

	/**
	 * A stack that keeps the current definition context (package/class/method)
	 */
	protected EntityStack context;
	/** 
	 * A dictionary allowing to recover created FAMIX Entities
	 */
	protected JavaDictionary dico;
	/**
	 * The super type of an anonymous declaration is only available (without resorting to bindings) when 
	 * we are in its parent node: a ClassInstanceCreation.
	 * So we must keep this type from the visit(ClassInstanceCreation) to be used in visit(AnonymousClassDeclaration).<br>
	 * Note that in some special cases one can also have an anonymous class definition without specifying its superclass.
	 */
	protected String anonymousSuperTypeName;

	public GetVisitedEntityAbstractVisitor(JavaDictionary dico) {
		this.dico = dico;
		this.context = new EntityStack();
	}

	// two visit methods never used

	@Override
	public boolean visit(PackageDeclaration node) {
		return false; // no need to visit children of the declaration
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		return false; // no need to visit children of the declaration	
	}

	// CompilationUnit --> FamixNamespace

	protected Namespace visitCompilationUnit(CompilationUnit node) {
		Namespace fmx = null;
		PackageDeclaration pckg = node.getPackage();
		if (pckg == null) {
			fmx = dico.getFamixNamespaceDefault();
		} else {
			fmx = (Namespace) dico.getEntityByKey(pckg.resolveBinding());
		}
		this.context.pushPckg(fmx);

		return fmx;
	}

	protected void endVisitCompilationUnit(CompilationUnit node) {
		this.context.popPckg();
		super.endVisit(node);
	}
	
	// type (class/interface) --> FamixClass

	/*
	 * Can only be a class or interface declaration
	 * Local type: see comment of visit(ClassInstanceCreation node)
	 */
	protected eu.synectique.verveine.core.gen.famix.Class visitTypeDeclaration(TypeDeclaration node) {
		ITypeBinding bnd = node.resolveBinding();

		eu.synectique.verveine.core.gen.famix.Class fmx = dico.getFamixClass(bnd, /*name*/node.getName().getIdentifier(), (ContainerEntity) /*owner*/context.top());
		if (fmx != null) {
			this.context.pushType(fmx);
		}
		return fmx;
	}

	protected void endVisitTypeDeclaration(TypeDeclaration node) {
		if (context.topType() instanceof eu.synectique.verveine.core.gen.famix.Class) {
			context.pop();
		}
		super.endVisit(node);
	}

	/**
	 * See field {@link GetVisitedEntityAbstractVisitor#anonymousSuperTypeName}<br>
	 * We could test if it is a local type (inner/anonymous) and not define it in case it does not make any reference
	 * to anything outside its owner class. But it would be a lot of work for probably little gain.
	 */
	protected void visitClassInstanceCreation(ClassInstanceCreation node) {
		if (node.getAnonymousClassDeclaration() != null) {
			anonymousSuperTypeName = Util.jdtTypeName(node.getType());
		} else {
			anonymousSuperTypeName = null;
		}
	}

	/**
	 * See field {@link GetVisitedEntityAbstractVisitor#anonymousSuperTypeName}
	 */
	protected eu.synectique.verveine.core.gen.famix.Class visitAnonymousClassDeclaration(AnonymousClassDeclaration node) {
		eu.synectique.verveine.core.gen.famix.Class fmx = null;

		ITypeBinding bnd = node.resolveBinding();
		fmx = this.dico.getFamixClass(bnd, Util.stringForAnonymousName(anonymousSuperTypeName,context), /*owner*/(ContainerEntity)context.top());
		if (fmx != null) {
			this.context.pushType(fmx);
		}
		return fmx;
	}

	protected void endVisitAnonymousClassDeclaration(AnonymousClassDeclaration node) {
		if (context.top()  instanceof  eu.synectique.verveine.core.gen.famix.Class) {
			context.pop();
		}
		anonymousSuperTypeName = null;
	}

	protected eu.synectique.verveine.core.gen.famix.Enum visitEnumDeclaration(EnumDeclaration node) {
		eu.synectique.verveine.core.gen.famix.Enum fmx = dico.getFamixEnum(node.resolveBinding(), node.getName().getIdentifier(), (ContainerEntity) context.top());
		if (fmx != null) {
			this.context.pushType(fmx);
		}
		return fmx;
	}

	protected void endVisitEnumDeclaration(EnumDeclaration node) {
		if (context.top() instanceof eu.synectique.verveine.core.gen.famix.Enum) {
			this.context.popType();
		}
		super.endVisit(node);
	}

	protected AnnotationType visitAnnotationTypeDeclaration(AnnotationTypeDeclaration node) {
		ITypeBinding bnd = node.resolveBinding();
		AnnotationType fmx = dico.getFamixAnnotationType(bnd, node.getName().getIdentifier(), (ContainerEntity) context.top());
		if (fmx != null) {
			context.pushType(fmx);
		}
		return fmx;
	}

	protected void endVisitAnnotationTypeDeclaration(AnnotationTypeDeclaration node) {
		if (context.topType() instanceof AnnotationType) {
			context.pop();
		}
		super.endVisit(node);
	}

	// MethodDeclaration --> FamixMethod

	/**
	 * Local type: same as {@link GetVisitedEntityAbstractVisitor#visitClassInstanceCreation(ClassInstanceCreation)}, 
	 * we create it even if it is a local method because their are too many ways it can access external things
	 */
	@SuppressWarnings("unchecked")
	protected Method visitMethodDeclaration(MethodDeclaration node) {
		IMethodBinding bnd = node.resolveBinding();

		Collection<String> paramTypes = new ArrayList<String>();
		for (SingleVariableDeclaration param : (List<SingleVariableDeclaration>) node.parameters()) {
			paramTypes.add( Util.jdtTypeName(param.getType()));
		}

		Method fmx = dico.getFamixMethod(bnd, node.getName().getIdentifier(), paramTypes, /*owner*/context.topType());

		context.pushMethod(fmx);  // whether fmx==null or not
		return fmx;
	}

	protected void endVisitMethodDeclaration(MethodDeclaration node) {
		context.popMethod();
		super.endVisit(node);
	}

	public Method visitInitializer(Initializer node) {
		Method fmx = dico.getFamixMethod((IMethodBinding) null, JavaDictionary.INIT_BLOCK_NAME, /*paramTypes*/new ArrayList<String>(), context.topType());

		if (fmx != null) {
			context.pushMethod(fmx);
		}
		return fmx;
	}

	public void endVisitInitializer(Initializer node) {
		if ( (context.top() instanceof Method) && ( context.top().getName().equals(JavaDictionary.INIT_BLOCK_NAME)) ) {
			this.context.pop();
		}
		super.endVisit(node);
	}

	public AnnotationTypeAttribute visitAnnotationTypeMemberDeclaration(AnnotationTypeMemberDeclaration node) {
		IMethodBinding bnd = node.resolveBinding();

		AnnotationTypeAttribute fmx = dico.getFamixAnnotationTypeAttribute(bnd, node.getName().getIdentifier(), (AnnotationType) context.topType());

		context.pushAnnotationMember(fmx);  // whether fmx==null or not
		return fmx;
	}

	public void endVisitAnnotationTypeMemberDeclaration(AnnotationTypeMemberDeclaration node) {
		this.context.popAnnotationMember();
		super.endVisit(node);
	}

}
