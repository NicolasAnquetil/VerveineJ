package fr.inria.verveine.extractor.java.visitors.defvisitors;

import fr.inria.verveine.extractor.java.EntityDictionary;
import fr.inria.verveine.extractor.java.VerveineJOptions;
import fr.inria.verveine.extractor.java.utils.StructuralEntityKinds;
import fr.inria.verveine.extractor.java.visitors.GetVisitedEntityAbstractVisitor;
import java.util.List;
import org.eclipse.jdt.core.dom.*;
import org.moosetechnology.model.famix.famixjavaentities.AnnotationTypeAttribute;
import org.moosetechnology.model.famix.famixjavaentities.Attribute;
import org.moosetechnology.model.famix.famixjavaentities.Method;
import org.moosetechnology.model.famix.famixjavaentities.Parameter;
import org.moosetechnology.model.famix.famixtraits.TSourceEntity;
import org.moosetechnology.model.famix.famixtraits.TStructuralEntity;
import org.moosetechnology.model.famix.famixtraits.TWithAttributes;
import org.moosetechnology.model.famix.famixtraits.TWithComments;

/**
 * A class to collect all comments.
 * Some important details on comment in JDT:
 * <ul>
 * <li>only JavaDoc comment are associated to any entity node in the AST (the entity immediately after the javadoc)</li>
 * <li>line and block comment are recorded in a list, but we have to decide to what entity they will be associated.
 *   We put them in the method containing them or in the method or attribute immediately after them if they are outside a method</li>
 * <li>content (text) of block or line comments is not stored, only their position and length.
 *   We have to recover them from the source file</li> 
 * </ul>
 * 
 * This class assumes the AST is visited in the file position order of the nodes (nodes appearing first in the file
 * are visited first).
 * It visit the AST in parallel with "visiting" the list of comments and for each comment,
 * <ul>
 * <li>if it is a JavaDoc, gets its associated node</li>
 * <li>if not javadoc, finds out if it is in a method or not
 * 	<ul>
 *  <li>if not, it find the class, method, or attribute that comes immediately after the comment</li>
 *  <li>if in a method, it associates the comment to this method</li>
 *  </ul>
 * </ul>
 * 
 * @author anquetil
 */
public class VisitorComments extends GetVisitedEntityAbstractVisitor {

	/**
	 * list of all comments
	 */
	protected List<Comment> allComments;

	protected int nextComment;

	public VisitorComments(EntityDictionary dico, VerveineJOptions options) {
		super(dico, options);
	}
	// VISITOR METHODS

	@Override
	public boolean visit(CompilationUnit node) {
		allComments = node.getCommentList();
		if (allComments.size() == 0) {
			// no comment, not visiting
			return false;
		}
		else {
			nextComment = 0;
		}
		return super.visit(node);
	}

	@Override
	public void endVisit(CompilationUnit node) {
		endVisitCompilationUnit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		Comment cmt;
		TWithComments fmx = (TWithComments) visitTypeDeclaration(node);

		while ( (cmt = commentBefore(node)) != null) {
			dico.createFamixComment(cmt, fmx, options.commentsAsText());
		}
		return super.visit(node);
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		endVisitTypeDeclaration(node);
	}

	/**
	 * Uses field {@link  GetVisitedEntityAbstractVisitor#anonymousSuperTypeName}
	 */
	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		if (visitAnonymousClassDeclaration( node) != null) {
			hasCommentsBefore(node);
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
		return super.visit(node);
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		endVisitMethodDeclaration(node);
	}

	public boolean visit(AnnotationTypeMemberDeclaration node) {
		AnnotationTypeAttribute fmx = visitAnnotationTypeMemberDeclaration( node);
			return super.visit(node);
	}

	public void endVisit(AnnotationTypeMemberDeclaration node) {
		this.context.popAnnotationMember();
		super.endVisit(node);
	}

/*
	public boolean visit(EnumConstantDeclaration node) {
		dico.getFamixEnumValue(node.resolveVariable(), node.getName().getIdentifier(), /*owner* /(Enum)context.topType());
		return false;
	}
*/

	@Override
	public boolean visit(FieldDeclaration node) {

		return super.visit(node);
	}

	// UTILITY METHODS

	/**
	 * whether the <code>nextComment</code> to treat is before <code>node</code>
	 */
	protected boolean hasCommentsBefore(AnonymousClassDeclaration node) {
		Comment cmt = allComments.get(nextComment);
		return node.getStartPosition() >= (cmt.getStartPosition() + cmt.getLength());
	}

}