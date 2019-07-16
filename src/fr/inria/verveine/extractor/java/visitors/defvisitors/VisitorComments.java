package fr.inria.verveine.extractor.java.visitors.defvisitors;

import eu.synectique.verveine.core.gen.famix.NamedEntity;
import fr.inria.verveine.extractor.java.visitors.SummarizingClassesAbstractVisitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
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
import eu.synectique.verveine.core.gen.famix.StructuralEntity;
import fr.inria.verveine.extractor.java.visitors.GetVisitedEntityAbstractVisitor;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.utils.StructuralEntityKinds;

/**
 * AST Visitor that defines all the (Famix) entities of interest
 * Famix entities are stored in a Map along with the IBindings to which they correspond
 */
/**
 * @author anquetil
 *
 */
public class VisitorComments extends SummarizingClassesAbstractVisitor {

	/**
	 * Needed to recover the regular comments
	 */
	private CompilationUnit astRoot;

	/**
	 * Contains a possible JavaDoc comment for a famix entity declared.
	 *<br>
	 * Needed because in the case of variables, javadoc may not be easily accessible from the AST node  
	 */
	private Javadoc entityJavadoc;

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

	public VisitorComments(JavaDictionary dico, boolean classSummary) {
		super(dico, classSummary);
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
			entityJavadoc = node.getJavadoc();
			commentOnEntity(node, fmx);
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
	 * Sets field {@link GetVisitedEntityAbstractVisitor#anonymousSuperTypeName}
	 */
	@Override
	public boolean visit(ClassInstanceCreation node) {
		visitClassInstanceCreation( node);
		return super.visit(node);
	}

	/**
	 * Uses field {@link  GetVisitedEntityAbstractVisitor#anonymousSuperTypeName}
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

		if ( (fmx != null) && (! classSummary) ){
			entityJavadoc = node.getJavadoc();
			commentOnEntity(node, fmx);

			structuralType = StructuralEntityKinds.PARAMETER;
			entityJavadoc = null;  // no javadoc on parameters

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
	public boolean visit(Block node) {
		structuralType = StructuralEntityKinds.LOCALVAR;
		entityJavadoc = null;  // no javadoc on local variables

		return super.visit(node);
	}

	public boolean visit(AnnotationTypeMemberDeclaration node) {
		AnnotationTypeAttribute fmx = visitAnnotationTypeMemberDeclaration( node);
		if ( (fmx != null) && (! classSummary) ) {
			entityJavadoc = node.getJavadoc();
			commentOnEntity(node, fmx);
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
		Method fmx = visitInitializer(node);
		if ( (fmx != null) && (! classSummary) ) {
			entityJavadoc = node.getJavadoc();
			commentOnEntity(node, fmx);
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

/*
	public boolean visit(EnumConstantDeclaration node) {
		dico.getFamixEnumValue(node.resolveVariable(), node.getName().getIdentifier(), /*owner* /(Enum)context.topType());
		return false;
	}
*/

	@Override
	public boolean visit(FieldDeclaration node) {
		structuralType = StructuralEntityKinds.ATTRIBUTE;
		entityJavadoc = node.getJavadoc();
		varDeclarationFragmentHasComment = (nodeOptionalComment(node) != null);

		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationExpression node) {
		structuralType = StructuralEntityKinds.LOCALVAR;

		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		structuralType = StructuralEntityKinds.LOCALVAR;
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		commentOnStructuralEntity(node, structuralType);

		return super.visit(node);
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		commentOnStructuralEntity(node, structuralType);

		return super.visit(node);
	}

	// UTILITY METHODS

    protected void commentOnEntity(ASTNode node, NamedEntity fmx) {
		Comment cmt = null;

		if (fmx == null) {
			return;
        }

		if (entityJavadoc != null) {
			cmt = entityJavadoc;
		} else {
			cmt = nodeOptionalComment(node);
		}

		if (cmt != null) {
		    dico.createFamixComment(cmt, fmx);
        }
    }

	protected void commentOnStructuralEntity(VariableDeclaration node, StructuralEntityKinds structuralKind) {
		StructuralEntity fmx = null;
		Comment cmt = null;

		if (classSummary) {
			return;
		}

		if (entityJavadoc != null) {
			cmt = entityJavadoc;
		} else {
			cmt = variableOptionalComment(node, structuralKind);
		}

		if (cmt != null) {
			IVariableBinding bnd = node.resolveBinding();
			String name = node.getName().getIdentifier();

			// recover the famix entity
			switch (structuralKind) {
				case ATTRIBUTE:
					fmx = dico.getFamixAttribute(bnd, name, context.topType());
					break;

				case PARAMETER:
					fmx = dico.getFamixParameter(bnd, name, context.topMethod());
					break;

				case LOCALVAR:
					fmx = dico.getFamixLocalVariable(bnd, name, context.topMethod());
					break;
			}

			if (! fmx.getIsStub()) {
			    // if it is a stub, it might have been created by the getFamixXXX just above
                // or something very strange happened
                // Anyway we cannot have a comment on a stub
			    dico.createFamixComment(cmt, fmx);
            }
		}
	}

	private Comment variableOptionalComment(ASTNode node, StructuralEntityKinds structuralKind) {
		switch (structuralKind) {
			case ATTRIBUTE:
			case LOCALVAR:
				return nodeOptionalComment(node.getParent());

			case PARAMETER:
				return nodeOptionalComment(node);

            default:
                return null;
		}
	}

	private Comment nodeOptionalComment(ASTNode node) {
        int iCmt = astRoot.firstLeadingCommentIndex(node);
        if (iCmt > -1) {
            return (Comment) astRoot.getCommentList().get(iCmt);
        }
        else {
            return null;
        }
    }

}