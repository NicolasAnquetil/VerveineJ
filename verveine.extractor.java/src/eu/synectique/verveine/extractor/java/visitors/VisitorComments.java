package eu.synectique.verveine.extractor.java.visitors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import eu.synectique.verveine.core.EntityStack;
import eu.synectique.verveine.core.gen.famix.AnnotationType;
import eu.synectique.verveine.core.gen.famix.AnnotationTypeAttribute;
import eu.synectique.verveine.core.gen.famix.ContainerEntity;
import eu.synectique.verveine.core.gen.famix.Enum;
import eu.synectique.verveine.core.gen.famix.EnumValue;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.NamedEntity;
import eu.synectique.verveine.core.gen.famix.Namespace;
import eu.synectique.verveine.core.gen.famix.StructuralEntity;
import eu.synectique.verveine.extractor.java.JavaDictionary;
import eu.synectique.verveine.extractor.java.Util;

/**
 * AST Visitor that defines all the (Famix) entities of interest
 * Famix entities are stored in a Map along with the IBindings to which they correspond
 */
public class VisitorComments extends ASTVisitor {

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

	/**
	 * A stack that keeps the current definition context (package/class/method)
	 */
	protected EntityStack context;

	/**
	 * Needed to recover the regular comments
	 */
	private CompilationUnit astRoot;

	/**
	 * set in parent of structuralEntity declaration for the Attribute
	 */
	private Javadoc structuralJavadoc;

	/**
	 * set in parent of structuralEntity declaration to indicate what kind of structuralentity it is
	 */
	private StructuralEntityKinds structuralType;

	/**
	 * if Compilation unit has a comment, then add it to the type
	 */
	private Javadoc compilUnitComment;

	/**
	 * If there is a comment on a VariableDeclaration (which possibly have several "variables"), we keep the comment for the first declaration
	 */
	private boolean varDeclarationFragmentHasComment;

	/**
	 * Used when creating a structural entity
	 */
	private enum StructuralEntityKinds {
		ATTRIBUTE, PARAMETER, LOCALVAR, IGNORE;
	}

	public VisitorComments(JavaDictionary dico) {
		this.dico = dico;
		this.context = new EntityStack();
	}

	// VISITOR METHODS

	public boolean visit(CompilationUnit node) {
		Namespace fmx = null;
		PackageDeclaration pckg = node.getPackage();
		if (pckg == null) {
			fmx = dico.getFamixNamespaceDefault();
		} else {
			fmx = (Namespace) dico.getEntityByKey(pckg.resolveBinding());
		}
		this.context.pushPckg(fmx);
		astRoot = node;

		return super.visit(node);
	}

	public void endVisit(CompilationUnit node) {
		this.context.popPckg();
		super.endVisit(node);
	}

	public boolean visit(PackageDeclaration node) {
		compilUnitComment = node.getJavadoc();
		return false; // no need to visit children of the declaration
	}

	public boolean visit(ImportDeclaration node) {
		return false; // no need to visit children of the declaration	
	}

	/*
	 * Can only be a class or interface declaration
	 * Local type: see comment of visit(ClassInstanceCreation node)
	 */
	public boolean visit(TypeDeclaration node) {
		//		System.err.println("TRACE, Visiting TypeDeclaration: "+node.getName().getIdentifier());
		ITypeBinding bnd = node.resolveBinding();
		// The class should exist, we call ensure... to recover it
		// so only the first 3 parameters are meaningful
		eu.synectique.verveine.core.gen.famix.Class fmx = dico.getFamixClass(bnd, /*name*/node.getName().getIdentifier(), (ContainerEntity) /*owner*/context.top());
		if (fmx != null) {
			this.context.pushType(fmx);
			addRegularOrJavaDocComment(node, fmx, node.getJavadoc());
			if (compilUnitComment != null) {
				dico.createFamixComment(compilUnitComment, fmx);
				compilUnitComment = null;  // in case there are several types defined in the compilation unit
			}
			return super.visit(node);
		} else {
			return false;
		}
	}

	public void endVisit(TypeDeclaration node) {
		this.context.popType();
		super.endVisit(node);
	}

	/**
	 * See field {@link VisitorClassMethodDef#anonymousSuperTypeName}<br>
	 * We could test if it is a local type (inner/anonymous) and not define it in case it does not make any reference
	 * to anything outside its owner class. But it would be a lot of work for probably little gain.
	 */
	public boolean visit(ClassInstanceCreation node) {
		if (node.getAnonymousClassDeclaration() != null) {
			anonymousSuperTypeName = Util.jdtTypeName(node.getType());
		} else {
			anonymousSuperTypeName = null;
		}
		return super.visit(node);
	}

	/**
	 * See field {@link VisitorClassMethodDef#anonymousSuperTypeName}
	 */
	public boolean visit(AnonymousClassDeclaration node) {
		eu.synectique.verveine.core.gen.famix.Class fmx = null;

		ITypeBinding bnd = node.resolveBinding();
		fmx = this.dico.getFamixClass(bnd, Util.makeAnonymousName(anonymousSuperTypeName,context), /*owner*/(ContainerEntity)context.top());
		if (fmx != null) {
			this.context.pushType(fmx);
			return super.visit(node);
		} else {
			return false;
		}
	}

	public void endVisit(AnonymousClassDeclaration node) {
		super.endVisit(node);
	}

	public boolean visit(EnumDeclaration node) {
//		System.err.println("TRACE, Visiting EnumDeclaration: "+node.getName().getIdentifier());

		eu.synectique.verveine.core.gen.famix.Enum fmx = dico.getFamixEnum(node.resolveBinding(), node.getName().getIdentifier(), (ContainerEntity) context.top());
		if (fmx != null) {
			this.context.pushType(fmx);
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	public void endVisit(EnumDeclaration node) {
		this.context.popType();
		super.endVisit(node);
	}

	public boolean visit(AnnotationTypeDeclaration node) {
		ITypeBinding bnd = node.resolveBinding();
		AnnotationType fmx = dico.getFamixAnnotationType(bnd, node.getName().getIdentifier(), (ContainerEntity) context.top());
		if (fmx != null) {
			context.pushType(fmx);
			return super.visit(node);
		}
		else {
			context.pushType(null);
			return false;
		}
	}

	public void endVisit(AnnotationTypeDeclaration node) {
		this.context.popType();
		super.endVisit(node);
	}

	public boolean visit(AnnotationTypeMemberDeclaration node) {
		IMethodBinding bnd = node.resolveBinding();

		AnnotationTypeAttribute fmx = dico.getFamixAnnotationTypeAttribute(bnd, node.getName().getIdentifier(), (AnnotationType) context.topType());
		if (fmx != null) {
			addRegularOrJavaDocComment(node, fmx, node.getJavadoc());
			context.pushAnnotationMember(fmx);
			return super.visit(node);
		} else {
			context.pushAnnotationMember(null);
			return false;
		}
	}

	public void endVisit(AnnotationTypeMemberDeclaration node) {
		this.context.popAnnotationMember();
		super.endVisit(node);
	}

	/**
	 * Local type: same as {@link VisitorComments#visit(ClassInstanceCreation)}, 
	 * we create it even if it is a local method because their are too many ways it can access external things
	 */
	@SuppressWarnings("unchecked")
	public boolean visit(MethodDeclaration node) {
		IMethodBinding bnd = node.resolveBinding();

		Collection<String> paramTypes = new ArrayList<String>();
		for (SingleVariableDeclaration param : (List<SingleVariableDeclaration>) node.parameters()) {
			paramTypes.add( Util.jdtTypeName(param.getType()));
		}

		Method fmx = dico.getFamixMethod(bnd, node.getName().getIdentifier(), paramTypes, /*owner*/context.topType());

		if (fmx != null) {
			addRegularOrJavaDocComment(node, fmx, node.getJavadoc());

			this.context.pushMethod(fmx);
			structuralType = StructuralEntityKinds.PARAMETER;
			structuralJavadoc = null;  // no javadoc on parameters

			return super.visit(node);
		} else {
			this.context.pushMethod(null);
			return false;
		}
	}

	public void endVisit(MethodDeclaration node) {
		context.popMethod();
		super.endVisit(node);
	}

	@Override
	public boolean visit(Initializer node) {
		Method fmx = dico.getFamixMethod((IMethodBinding) null, JavaDictionary.INIT_BLOCK_NAME, /*paramTypes*/new ArrayList<String>(), context.topType());
		addRegularOrJavaDocComment(node, fmx, node.getJavadoc());
		return super.visit(node);
	}

	public boolean visit(EnumConstantDeclaration node) {
		EnumValue ev = dico.getFamixEnumValue(node.resolveVariable(), node.getName().getIdentifier(), /*owner*/(Enum)context.topType());
		ev.setIsStub(false);
		return false;
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		structuralType = StructuralEntityKinds.ATTRIBUTE;
		structuralJavadoc = node.getJavadoc();
		varDeclarationFragmentHasComment = (nodeHasComment(node) != null);
 
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationExpression node) {
		structuralType = StructuralEntityKinds.LOCALVAR;
		structuralJavadoc = null;  // no javadoc on local variables
		varDeclarationFragmentHasComment = (nodeHasComment(node) != null);

		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		structuralType = StructuralEntityKinds.LOCALVAR;
		structuralJavadoc = null;  // no javadoc on local variables
		varDeclarationFragmentHasComment = (nodeHasComment(node) != null);
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		createStructuralComment(node, structuralType, structuralJavadoc);

		return super.visit(node);
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		createStructuralComment(node, structuralType, structuralJavadoc);

		return super.visit(node);
	}

	// UTILITY METHODS

	protected void createStructuralComment(VariableDeclaration node, StructuralEntityKinds structuralKind, Javadoc structuralJavadoc) {
		StructuralEntity fmx;
		IVariableBinding bnd = node.resolveBinding();
		String name = node.getName().getIdentifier();

		// recover the famix entity
		switch (structuralKind) {
		case ATTRIBUTE:
			fmx = dico.getFamixAttribute(bnd, name, context.topType());
			if (varDeclarationFragmentHasComment) {
				// if there is a comment on a FieldDeclaration (which possibly have several fields), we keep the comment for the first field
				addRegularOrJavaDocComment(node.getParent(), fmx, structuralJavadoc);
				varDeclarationFragmentHasComment = false;
			}
			else {
				addRegularOrJavaDocComment(node, fmx, structuralJavadoc);
			}
			break;

		case PARAMETER:
			fmx = dico.getFamixParameter(bnd, name, context.topMethod());
			addRegularOrJavaDocComment(node, fmx, structuralJavadoc);
			break;

		case LOCALVAR:
			fmx = dico.getFamixParameter(bnd, name, context.topMethod());
			if (varDeclarationFragmentHasComment) {
				// if there is a comment on a FieldDeclaration (which possibly have several fields), we keep the comment for the first field
				addRegularOrJavaDocComment(node.getParent(), fmx, structuralJavadoc);
				varDeclarationFragmentHasComment = false;
			}
			else {
				addRegularOrJavaDocComment(node, fmx, structuralJavadoc);
			}
			break;

		default:
			/*nothing*/
		}

	}

	protected void addRegularOrJavaDocComment(ASTNode node, NamedEntity fmx, Javadoc structuralJavadoc) {
		if (fmx == null) {
			return;
		}

		if (structuralJavadoc != null) {
			dico.createFamixComment(structuralJavadoc, fmx);
			return;  // cannot have both javadoc and regular comment, can it ???
		}

		Comment cmt = nodeHasComment(node);
		if (cmt != null) {
			dico.createFamixComment( cmt, fmx);
		}
	}

	private Comment nodeHasComment(ASTNode node) {
		int iCmt = astRoot.firstLeadingCommentIndex(node);
		if (iCmt > -1) {
			return (Comment) astRoot.getCommentList().get(iCmt);
		}
		else {
			return null;
		}
	}

}