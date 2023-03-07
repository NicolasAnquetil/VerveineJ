package fr.inria.verveine.extractor.java.visitors.defvisitors;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.moosetechnology.model.famix.famixjavaentities.AnnotationTypeAttribute;
import org.moosetechnology.model.famix.famixjavaentities.Method;
import org.moosetechnology.model.famix.famixtraits.TSourceEntity;
import org.moosetechnology.model.famix.famixtraits.TWithAttributes;
import org.moosetechnology.model.famix.famixtraits.TWithComments;

import fr.inria.verveine.extractor.java.EntityDictionary;
import fr.inria.verveine.extractor.java.VerveineJOptions;
import fr.inria.verveine.extractor.java.visitors.GetVisitedEntityAbstractVisitor;

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
	 * list of all comments in the compilation unit
	 */
	protected List<Comment> allComments;

	/**
	 * Indice in {@link #allComments} of next comment to deal with (ie. associate with a Famix entity).
	 */
	protected int nextComment;

	/**
	 * Whether we are defining a class member or not<br>
	 * Used to differentiate attribute declarations from other variable declarations 
	 */
	protected boolean classMemberDeclarations;

	/**
	 * To handle correctly AnonymousClassDeclaration, we need to keep the start position of methods
	 * (that might contain these anonymous classes)
	 */
	private int methodStartPosition;

	public VisitorComments(EntityDictionary dico, VerveineJOptions options) {
		super(dico, options);
		classMemberDeclarations = false;
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
		TWithComments fmx = (TWithComments) visitTypeDeclaration(node);

		assignCommentsBefore(node, fmx);
		classMemberDeclarations = true;

		return super.visit(node);
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		classMemberDeclarations = false;
		endVisitTypeDeclaration(node);
	}

	/**
	 * AnonymousClassDeclaration appear within a method, we deal first with all "pending" comments
	 * that appear before the AnonymousClassDeclaration and should be associated with the method and not anonymous class
	 */
	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		assignCommentsInInterval( methodStartPosition, node.getStartPosition(), (TWithComments) context.topMethod());
		classMemberDeclarations = true;

		return super.visit(node);
	}

	@Override
	public void endVisit(AnonymousClassDeclaration node) {
		classMemberDeclarations = false;
		endVisitAnonymousClassDeclaration( node);
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		TWithComments fmx = visitEnumDeclaration( node);

		assignCommentsBefore(node, fmx);
		classMemberDeclarations = true;

		return super.visit(node);
	}

	@Override
	public void endVisit(EnumDeclaration node) {
		classMemberDeclarations = false;
		endVisitEnumDeclaration( node);
	}

	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		TWithComments fmx = visitAnnotationTypeDeclaration( node);

		assignCommentsBefore(node, fmx);
		classMemberDeclarations = true;

		return super.visit(node);
	}

	@Override
	public void endVisit(AnnotationTypeDeclaration node) {
		classMemberDeclarations = false;
		endVisitAnnotationTypeDeclaration(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		Method fmx = visitMethodDeclaration( node);

		assignCommentsBefore(node, fmx);
		classMemberDeclarations = false;
		methodStartPosition = node.getStartPosition();

		return super.visit(node);
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		assignCommentsInside(node, (TWithComments) context.popMethod());
		classMemberDeclarations = true;
		endVisitMethodDeclaration(node);
	}

	public boolean visit(AnnotationTypeMemberDeclaration node) {
		AnnotationTypeAttribute fmx = visitAnnotationTypeMemberDeclaration( node);

		assignCommentsBefore(node, fmx);

		return super.visit(node);
	}

	public void endVisit(AnnotationTypeMemberDeclaration node) {
		this.context.popAnnotationMember();
		super.endVisit(node);
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		if (classMemberDeclarations) {
			TWithComments fmx = dico.getFamixAttribute(node.resolveBinding(), node.getName().getIdentifier(), (TWithAttributes) context.topType());
			if ( ! ((TSourceEntity) fmx).getIsStub() ) {
				// if it is a stub, it might have been created by the getFamixAttribute just above
				// Anyway we cannot have a comment on a stub
				assignCommentsBefore(node, fmx);
			}
		}

		return super.visit(node);
	}

/*
	public boolean visit(EnumConstantDeclaration node) {
		dico.getFamixEnumValue(node.resolveVariable(), node.getName().getIdentifier(), /*owner* /(Enum)context.topType());
		return false;
	}
*/

	// UTILITY METHODS

	/**
	 * Assigns all "pending" comments to the Famix entity.<br>
	 * Pending comments are those not treated that appear before the <code>node</code> associated with the Famix entity 
	 */
	protected void assignCommentsBefore(ASTNode node, TWithComments fmx) {
		if (fmx == null) {
			return;
		}

		Comment cmt = allComments.get(nextComment);

		while ( commentIsBefore(cmt, node)) {
			dico.createFamixComment(cmt, fmx, options.commentsAsText());
			nextComment++;
			cmt = allComments.get(nextComment);
		}		
	}

	/**
	 * Assigns all "pending" comments that appear with the Famix entity (actually within the <code>node</code> associated to it) 
	 */
	protected void assignCommentsInside(ASTNode node, TWithComments fmx) {
		assignCommentsInInterval( node.getStartPosition(), node.getStartPosition() + node.getLength(), fmx);
	}
	
	/**
	 * Assigns to the Famix entity all "pending" comments that appear within the interval [start ; stop] 
	 */
	protected void assignCommentsInInterval(int start, int end, TWithComments fmx) {
		if (fmx == null) {
			return;
		}

		Comment cmt = allComments.get(nextComment);

		while ( commentIsInside(cmt, start, end)) {
			dico.createFamixComment(cmt, fmx, options.commentsAsText());
			nextComment++;
			cmt = allComments.get(nextComment);
		}		

	}

	/**
	 * whether <code>cmt</code> appears before <code>node</code> or not 
	 */
	protected boolean commentIsBefore(Comment cmt, ASTNode node) {
		return node.getStartPosition() >= (cmt.getStartPosition() + cmt.getLength());
	}
	
	protected boolean commentIsInside(Comment cmt, int start, int end) {
		return (start <= cmt.getStartPosition()) && (end >= cmt.getStartPosition() + cmt.getLength());
	}

}