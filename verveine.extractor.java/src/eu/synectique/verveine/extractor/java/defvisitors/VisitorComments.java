package eu.synectique.verveine.extractor.java.defvisitors;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
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

import eu.synectique.verveine.core.gen.famix.AnnotationTypeAttribute;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.NamedEntity;
import eu.synectique.verveine.core.gen.famix.StructuralEntity;
import eu.synectique.verveine.extractor.java.JavaDictionary;
import eu.synectique.verveine.extractor.java.RefVisitor;

/**
 * AST Visitor that defines all the (Famix) entities of interest
 * Famix entities are stored in a Map along with the IBindings to which they correspond
 */
public class VisitorComments extends RefVisitor {


	/**
	 * Needed to recover the regular comments
	 */
	private CompilationUnit astRoot;

	/**
	 * set in parent of structuralEntity declaration for the Attribute
	 */
	private Javadoc structuralJavadoc;

	/**
	 * set in parent of structuralEntity declaration to indicate what kind of structuralEntity it is
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
		super(dico);
	}
	// VISITOR METHODS

	@Override
	public boolean visit(CompilationUnit node) {
		visitCompilationUnit(node);
		astRoot = node;
		return super.visit(node);
	}

	@Override
	public void endVisit(CompilationUnit node) {
		endVisitCompilationUnit(node);
	}

	public boolean visit(PackageDeclaration node) {
		compilUnitComment = node.getJavadoc();
		return false; // no need to visit children of the declaration
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		eu.synectique.verveine.core.gen.famix.Class fmx = visitTypeDeclaration( node);
		if (fmx != null) {
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

	@Override
	public void endVisit(TypeDeclaration node) {
		endVisitTypeDeclaration(node);
	}

	/**
	 * Sets field {@link RefVisitor#anonymousSuperTypeName}
	 */
	@Override
	public boolean visit(ClassInstanceCreation node) {
		visitClassInstanceCreation( node);
		return super.visit(node);
	}

	/**
	 * Uses field {@link  RefVisitor#anonymousSuperTypeName}
	 */
	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		if (visitAnonymousClassDeclaration( node) != null) {
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	@Override
	public void endVisit(AnonymousClassDeclaration node) {
		endVisitAnonymousClassDeclaration( node);
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		if (visitEnumDeclaration( node) != null) {
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	@Override
	public void endVisit(EnumDeclaration node) {
		endVisitEnumDeclaration( node);
	}

	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		if (visitAnnotationTypeDeclaration( node) != null) {
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	@Override
	public void endVisit(AnnotationTypeDeclaration node) {
		endVisitAnnotationTypeDeclaration(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		Method fmx = visitMethodDeclaration( node);

		if (fmx != null) {
			addRegularOrJavaDocComment(node, fmx, node.getJavadoc());

			structuralType = StructuralEntityKinds.PARAMETER;
			structuralJavadoc = null;  // no javadoc on parameters

			return super.visit(node);
		} else {
			return false;
		}
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		endVisitMethodDeclaration(node);
	}

	public boolean visit(AnnotationTypeMemberDeclaration node) {
		AnnotationTypeAttribute fmx = visitAnnotationTypeMemberDeclaration( node);
		if (fmx != null) {
			addRegularOrJavaDocComment(node, fmx, node.getJavadoc());
			return super.visit(node);
		} else {
			return false;
		}
	}

	public void endVisit(AnnotationTypeMemberDeclaration node) {
		this.context.popAnnotationMember();
		super.endVisit(node);
	}

	@Override
	public boolean visit(Initializer node) {
		Method fmx = dico.getFamixMethod((IMethodBinding) null, JavaDictionary.INIT_BLOCK_NAME, /*paramTypes*/new ArrayList<String>(), context.topType());
		addRegularOrJavaDocComment(node, fmx, node.getJavadoc());
		return super.visit(node);
	}

/*
	public boolean visit(EnumConstantDeclaration node) {
		dico.getFamixEnumValue(node.resolveVariable(), node.getName().getIdentifier(), /*owner* /(Enum)context.topType());
		return false;
	}
*/

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
		createCommentOnStructEntity(node, structuralType, structuralJavadoc);

		return super.visit(node);
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		createCommentOnStructEntity(node, structuralType, structuralJavadoc);

		return super.visit(node);
	}

	// UTILITY METHODS

	protected void createCommentOnStructEntity(VariableDeclaration node, StructuralEntityKinds structuralKind, Javadoc structuralJavadoc) {
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