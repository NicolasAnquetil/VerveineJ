package fr.inria.verveine.extractor.java.visitors.defvisitors;

import org.eclipse.jdt.core.dom.*;

import eu.synectique.verveine.core.Dictionary;
import fr.inria.verveine.extractor.java.visitors.GetVisitedEntityAbstractVisitor;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.visitors.SummarizingClassesAbstractVisitor;
import fr.inria.verveine.extractor.java.VerveineJParser.anchorOptions;
import fr.inria.verveine.extractor.java.utils.StructuralEntityKinds;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.moosetechnology.model.famix.famix.*;
import org.moosetechnology.model.famix.famix.Enum;

import java.util.List;

/**
 * AST Visitor that defines all the (Famix) entities of interest
 * Famix entities are stored in a Map along with the IBindings to which they correspond
 */
public class VisitorVarsDef extends SummarizingClassesAbstractVisitor {

	/**
	 * Whether to output all local variables (even those with primitive type or not (default is not).<br>
	 * Note: allLocals = ! classSummary
	 */
	private boolean allLocals = false;

	/**
	 * what sourceAnchors to create
	 */
	private anchorOptions anchors;

	/**
	 * set in parent of structuralEntity declaration to indicate what kind of structuralentity it is
	 */
	private StructuralEntityKinds structuralType;

	public VisitorVarsDef(JavaDictionary dico, boolean classSummary, boolean allLocals, anchorOptions anchors) {
		super(dico, classSummary);
		this.allLocals = allLocals;
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
	public boolean visit(TypeDeclaration node) {
		if (visitTypeDeclaration( node) != null) {
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
	public boolean visit(AnnotationTypeMemberDeclaration node) {
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

	@Override
	public boolean visit(MethodDeclaration node) {
		visitMethodDeclaration( node);
		structuralType = StructuralEntityKinds.PARAMETER;

		return super.visit(node);
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		endVisitMethodDeclaration(node);
	}

	@Override
	public boolean visit(Initializer node) {
		visitInitializer(node);
		return super.visit(node);
	}

	@Override
	public void endVisit(Initializer node) {
		endVisitInitializer(node);
	}

	/**
	 * Currently not defining lambdas. Only parse their body and consider their parameters as local variables
	 * of the parent method
	 *
	 *  LambdaExpression:
	 *     Identifier -> Body
	 *     ( [ Identifier { , Identifier } ] ) -> Body
	 *     ( [ FormalParameter { , FormalParameter } ] ) -> Body
	 */
	@Override
	public boolean visit(LambdaExpression node) {
		structuralType = StructuralEntityKinds.LOCALVAR;  // actually, should already be the case since we must be in a method
		return super.visit(node);
	}

	@Override
	public void endVisit(LambdaExpression node) {

	}

	@Override
	public boolean visit(Block node) {
		structuralType = StructuralEntityKinds.LOCALVAR;

		return super.visit(node);
	}

	@Override
	public boolean visit(EnumConstantDeclaration node) {
		EnumValue ev = dico.ensureFamixEnumValue(node.resolveVariable(), node.getName().getIdentifier(), /*owner*/(Enum)context.topType(), persistClass(((EnumDeclaration)node.getParent()).resolveBinding()));
		ev.setIsStub(false);
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		structuralType = StructuralEntityKinds.ATTRIBUTE;

		// creating the attribute(s)
		for (VariableDeclaration vardecl : (List<VariableDeclaration>)node.fragments() ) {
			createStructuralEntity( structuralType, vardecl, context.top());
		}

		// Possible local variables in optional initializer
		if (visitFieldDeclaration(node)) {  // recovers optional JavaDictionary.INIT_BLOCK_NAME method
			structuralType = StructuralEntityKinds.LOCALVAR;
			for (VariableDeclaration vardecl : (List<VariableDeclaration>)node.fragments() ) {
				vardecl.getInitializer().accept(this);
			}
		}

		return false;  // already visited all children
	}

	@Override
	public void endVisit(FieldDeclaration node) {
		endVisitFieldDeclaration(node);
	}

	@Override
	public boolean visit(VariableDeclarationExpression node) {
		// we usually don't declare local variables that have a primitive type
		// because we are assuming that the user is not interested in them
		// note that non primitive types are important because of the dependencies they create (eg invocation receiver)
		if ( ! allLocals && node.getType().isPrimitiveType() && (structuralType == StructuralEntityKinds.LOCALVAR) ) {
			return false;  // FIXME could be a mistake, but not too sure: what about var declaration with complex initialization (eg including an anonymous class)?
		}

		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		// about the same node as VariableDeclarationExpression (but is a statement instead of an expression)

		if ( ! allLocals && node.getType().isPrimitiveType() && (structuralType == StructuralEntityKinds.LOCALVAR) ) {
			return false;  // FIXME could be a mistake, but not too sure: what about var declaration with complex initialization (eg including an anonymous class)?
		}

		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		createStructuralEntity( structuralType, node, context.top());

		return true;  // e.g. with an initialization containing an anonymous class definition
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		if ( allLocals || (! node.getType().isPrimitiveType()) || (structuralType != StructuralEntityKinds.LOCALVAR) ) {
			createStructuralEntity( structuralType, node, context.top());
		}
		return true;  // e.g. with an initialization containing an anonymous class definition
	}

	public boolean visit(SuperMethodInvocation node) {
		dico.ensureFamixImplicitVariable(Dictionary.SUPER_NAME, context.topType(), context.topMethod(), /*persistIt*/!classSummary);
		return super.visit(node);
	}

	public boolean visit(ConstructorInvocation node) {
		if (! classSummary) {
			dico.ensureFamixImplicitVariable(Dictionary.SELF_NAME, context.topType(), context.topMethod(), /*persistIt=true*/!classSummary);
		}

		return super.visit(node);
	}

	public boolean visit(SuperConstructorInvocation node) {
		// access to "super" ???
		if (!classSummary) {
			dico.ensureFamixImplicitVariable(Dictionary.SUPER_NAME, context.topType(), context.topMethod(), /*persistIt=true*/!classSummary);
		}

		return super.visit(node);
	}

	// "SomeClass.class"
	public boolean visit(TypeLiteral node) {
		org.moosetechnology.model.famix.famix.Type javaMetaClass = dico.getFamixMetaClass(null);
		dico.ensureFamixAttribute(null, "class", javaMetaClass, javaMetaClass,	/*persistIt*/!classSummary);

		return super.visit(node);
	}

	// UTILITY METHODS

	private StructuralEntity createStructuralEntity(StructuralEntityKinds structKind, VariableDeclaration varDecl, NamedEntity owner) {
		StructuralEntity fmx;
		IVariableBinding bnd = varDecl.resolveBinding();
		String name = varDecl.getName().getIdentifier();

		switch (structKind) {
		case PARAMETER:	fmx = dico.ensureFamixParameter(bnd, name, (Method) owner, /*persistIt*/!classSummary);										break;
		case ATTRIBUTE: fmx = dico.ensureFamixAttribute(bnd, name, (org.moosetechnology.model.famix.famix.Type) owner, /*persistIt*/!classSummary);	break;
		case LOCALVAR: 	fmx = dico.ensureFamixLocalVariable(bnd, name, (Method) owner, /*persistIt*/!classSummary);									break;
		default:		fmx = null;
		}

		if (fmx != null) {
			fmx.setIsStub(false);
			if ((!classSummary) && (anchors != anchorOptions.none)) {
				dico.addSourceAnchor(fmx, varDecl, /*oneLineAnchor*/true);
			}
		}

		return fmx;
	}

}