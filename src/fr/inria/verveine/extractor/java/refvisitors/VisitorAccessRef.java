package fr.inria.verveine.extractor.java.refvisitors;

import java.util.List;

import org.eclipse.jdt.core.dom.*;

import eu.synectique.verveine.core.gen.famix.Access;
import eu.synectique.verveine.core.gen.famix.Attribute;
import eu.synectique.verveine.core.gen.famix.BehaviouralEntity;
import eu.synectique.verveine.core.gen.famix.ContainerEntity;
import eu.synectique.verveine.core.gen.famix.Enum;
import eu.synectique.verveine.core.gen.famix.ImplicitVariable;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.PrimitiveType;
import eu.synectique.verveine.core.gen.famix.StructuralEntity;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.VerveineJParser;
import fr.inria.verveine.extractor.java.utils.ImplicitVarBinding;

public class VisitorAccessRef extends AbstractRefVisitor {

	/**
	 * Whether a variable access is lhs (write) or not
	 */
	protected boolean inAssignmentLHS = false;

	/**
	 * what sourceAnchors to create
	 */
	private String anchors;

	public VisitorAccessRef(JavaDictionary dico, boolean classSummary, String anchors) {
		super(dico, classSummary);
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

	@Override
	public boolean visit(ClassInstanceCreation node) {
		visitClassInstanceCreation( node);
		return super.visit(node);
	}

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

	public boolean visit(AnnotationTypeMemberDeclaration node) {
		if (visitAnnotationTypeMemberDeclaration( node) != null) {
			return super.visit(node);
		} else {
			return false;
		}
	}

	public void endVisit(AnnotationTypeMemberDeclaration node) {
		this.context.popAnnotationMember();
		super.endVisit(node);
	}

	public boolean visit(MethodDeclaration node) {
		Method fmx = visitMethodDeclaration( node);

		if (fmx != null) {
			if (node.getBody() != null) {
				context.setLastAccess(null);
			}
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

	/**
	 *  FieldDeclaration ::=
	 *     [Javadoc] { ExtendedModifier } Type VariableDeclarationFragment
	 *          { , VariableDeclarationFragment } ;
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(FieldDeclaration node) {
		visitFieldDeclaration(node);  // to recover optional JavaDictionary.INIT_BLOCK_NAME method
		return true;
	}

	@Override
	public void endVisit(FieldDeclaration node) {
		endVisitFieldDeclaration(node);
	}

	public boolean visit(EnumConstantDeclaration node) {
		return visitEnumConstantDeclaration(node);
	}

	public void endVisit(EnumConstantDeclaration node) {
		endVisitEnumConstantDeclaration(node);
	}

	public boolean visit(FieldAccess node) {
		BehaviouralEntity accessor = this.context.topMethod();
		IVariableBinding bnd = node.resolveFieldBinding();
		// FIXME if bnd == null we have a problem
		ensureAccessedStructEntity(bnd, node.getName().getIdentifier(), /*typ*/null, /*owner*/null, accessor);
		Access lastAccess = context.getLastAccess();
		if (anchors.equals(VerveineJParser.ANCHOR_ASSOC)
				// check that lastAccess corresponds to current one
				&& (lastAccess != null) && (lastAccess.getAccessor() == accessor)
				&& (lastAccess.getVariable().getName().equals(node.getName().getIdentifier()))) {
			dico.addSourceAnchor(lastAccess, node, /*oneLineAnchor*/true);
		}
		return super.visit(node);
	}

	/*
	 * Could be a FieldAccess (see JDT javadoc: class FieldAccess) 
	 */
	public boolean visit(QualifiedName node) {
		IBinding bnd = node.resolveBinding();
		if (bnd instanceof IVariableBinding) {
			// could be a field or an enumValue
			BehaviouralEntity accessor = this.context.topMethod();
			ensureAccessedStructEntity((IVariableBinding) bnd, node.getName().getIdentifier(), /*typ*/null,
					/*owner*/null, accessor);
			Access lastAccess = context.getLastAccess();
			if (anchors.equals(VerveineJParser.ANCHOR_ASSOC)
					// check that lastAccess corresponds to current one
					&& (lastAccess != null) && (lastAccess.getAccessor() == accessor)
					&& (lastAccess.getVariable().getName().equals(node.getName().getIdentifier()))) {
				dico.addSourceAnchor(lastAccess, node, /*oneLineAnchor*/true);
			}

		}
		return super.visit(node);
	}

	@SuppressWarnings("unchecked")
	public boolean visit(InfixExpression node) {
		if (node.getLeftOperand() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getLeftOperand());
		}
		if (node.getRightOperand() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getRightOperand());
		}
		if (node.hasExtendedOperands()) {
			for (Expression op : (List<Expression>) node.extendedOperands()) {
				if (op instanceof SimpleName) {
					visitSimpleName((SimpleName) op);
				}
			}
		}

		return super.visit(node);
	}

	// "SomeClass.class"
	public boolean visit(TypeLiteral node) {
		Attribute accessed = dico.getFamixAttribute(null, "class", dico.getFamixMetaClass(null));
		if (accessed != null) {
			createAccess(/*accessor*/context.topMethod(), accessed, inAssignmentLHS);
		}

		return super.visit(node);
	}

	public boolean visit(AssertStatement node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		if (node.getMessage() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getMessage());
		}
		return super.visit(node);
	}

	public boolean visit(Assignment node) {
		inAssignmentLHS = true;
		if (node.getLeftHandSide() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getLeftHandSide());
		} else {
			node.getLeftHandSide().accept(this);
		}
		inAssignmentLHS = false;

		if (node.getRightHandSide() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getRightHandSide());
		} else {
			node.getRightHandSide().accept(this);
		}

		return false;
	}

	public boolean visit(ArrayAccess node) {
		// an array might be accessed in writing (see visit(Assignment node) ),
		// but it's index is accessed in reading
		boolean tmp = inAssignmentLHS;

		node.getArray().accept(this);

		inAssignmentLHS = false;
		if (node.getIndex() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getIndex());
		} else {
			node.getIndex().accept(this);
		}
		inAssignmentLHS = tmp;

		return false;
	}

	public boolean visit(DoStatement node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		return super.visit(node);
	}

	public boolean visit(EnhancedForStatement node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		return super.visit(node);
	}

	public boolean visit(ForStatement node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		return super.visit(node);
	}

	public boolean visit(IfStatement node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		return super.visit(node);
	}

	public boolean visit(ReturnStatement node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		return super.visit(node);
	}

	public boolean visit(SwitchCase node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		return super.visit(node);
	}

	public boolean visit(SwitchStatement node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(SynchronizedStatement node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		return super.visit(node);
	}

	public boolean visit(WhileStatement node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(ThisExpression node) {
		IBinding bnd = ImplicitVarBinding.getInstance(context.topMethod(), JavaDictionary.SELF_NAME);
		ImplicitVariable fmx = dico.ensureFamixImplicitVariable(bnd, JavaDictionary.SELF_NAME, this.context.topType(), context.topMethod(), /*persistIt*/!classSummary);
		if (fmx != null) {
			BehaviouralEntity accessor = this.context.topMethod();

			createAccess(accessor, fmx, inAssignmentLHS);

			Access lastAccess = context.getLastAccess();
			if (anchors.equals(VerveineJParser.ANCHOR_ASSOC) && (lastAccess != null) ) {
				dico.addSourceAnchor(lastAccess, node.getParent(), /*oneLineAnchor*/true);
			}
		}

		return false;
	}

	// UTILITY METHODS

	/**
	 * Deals with expressions that are simple name, e.g. "if (aBool) ..." or "return aVar;"<br>
	 * Tries to create a StruturalEntity access from this.
	 * {@link ASTVisitor#visit(SimpleName node)} cannot be used because it would be called all the time, so we need
	 * to check in all places where such simple expressions may occur and that are of interest to us
	 * @param expr -- the SimpleName expression
	 */
	private void visitSimpleName(SimpleName expr) {
		//		System.err.println("visitSimpleName(): "+expr.getIdentifier() + " inAssignmentLHS=" + inAssignmentLHS);
		IBinding bnd = expr.resolveBinding();
		if ((bnd instanceof IVariableBinding) && (context.topMethod() != null)) {
			// could be a variable, a field, an enumValue, ...
			BehaviouralEntity accessor = this.context.topMethod();
			ensureAccessedStructEntity((IVariableBinding) bnd, expr.getIdentifier(), /*typ*/null, /*owner*/null,
					accessor);
			Access lastAccess = context.getLastAccess();
			if (anchors.equals(VerveineJParser.ANCHOR_ASSOC)
					// check that lastAccess corresponds to current one
					&& (lastAccess != null) && (lastAccess.getAccessor() == accessor)
					&& (lastAccess.getVariable().getName().equals(expr.getIdentifier()))) {
				dico.addSourceAnchor(lastAccess, expr, /*oneLineAnchor*/true);
			}
		}

	}

	private StructuralEntity ensureAccessedStructEntity(IVariableBinding bnd, String name,
			eu.synectique.verveine.core.gen.famix.Type typ, ContainerEntity owner, BehaviouralEntity accessor) {
		StructuralEntity accessed = null;

		if (bnd == null) {
			// no way to know if it should be an attribute, EnumValue, variable, ...
			return null;
		} else {
			bnd = bnd.getVariableDeclaration();
		}

		// could also test: "owner instanceof Enum" in case bnd == null
		if (bnd.isEnumConstant()) {
			accessed = dico.ensureFamixEnumValue(bnd, name, (Enum) owner, /*persistIt*/!classSummary);
		} else if (bnd.isField()) {
			accessed = dico.ensureFamixAttribute(bnd, name, typ, (eu.synectique.verveine.core.gen.famix.Type) owner,
					/*persistIt*/!classSummary);
			if (classSummary) {
				if (!(accessed.getDeclaredType() instanceof PrimitiveType)) {
					//dico.addFamixReference(findHighestType(accessed.getBelongsTo()),
					//		findHighestType(accessed.getDeclaredType()), /*lastReference*/null);
					//   //TODO add FileAnchor to Reference
				}
			}

			if ((accessed != null) && (((Attribute) accessed).getParentType() == null)
					&& (accessed.getName().equals("length"))) {
				// special case: length attribute of arrays in Java
				((Attribute) accessed).setParentType(dico.ensureFamixClassArray());
			}
		} else if (bnd.isParameter()) {
			if (!classSummary) {
				accessed = dico.ensureFamixParameter(bnd, name, typ, (Method) owner, /*persistIt*/!classSummary);
			}
		} else {
			// it seems it is a variable.
			// if it is not already defined, we assume we are not interested
			accessed = (StructuralEntity) dico.getEntityByKey(bnd);
		}

		createAccess(accessor, accessed, inAssignmentLHS);

		return accessed;
	}

	/**
	 * Creates a FamixAccess between an accessor and an accessed. Checks before that we are not in a local access to ignore.
	 * @param accessor -- the method accessing
	 * @param accessed -- the variable accessed
	 * @param isLHS -- whether the access occurs on the LeftHandSide of an assignement (and therefore is a write access)
	 */
	private void createAccess(BehaviouralEntity accessor, StructuralEntity accessed, boolean isLHS) {
		// create local accesses?
		if ((accessed != null) && (accessor != null)) {
			if (classSummary) {
				//dico.addFamixReference(findHighestType(accessor), findHighestType(accessed), /*lastReference*/null);
				//  //TODO set FileAnchor to Reference
			} else if (accessed.getBelongsTo() != accessor) {
				context.setLastAccess(
						dico.addFamixAccess(accessor, accessed, /*isWrite*/isLHS, context.getLastAccess()));
				//TODO set FileAnchor to Access
			}
		}
	}

}
