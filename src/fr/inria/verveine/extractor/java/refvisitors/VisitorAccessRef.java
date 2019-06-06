package fr.inria.verveine.extractor.java.refvisitors;

import java.util.List;

import fr.inria.verveine.extractor.java.utils.StructuralEntityKinds;
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
import fr.inria.verveine.extractor.java.VerveineJParser.anchorOptions;
import fr.inria.verveine.extractor.java.utils.ImplicitVarBinding;

/**
 * A visitor that extracts accesses to variables.
 * One difficulty is that variable are often SimpleName nodes
 * But many other things are also SimpleName nodes (ex: name of an invoked method)
 * So we need to differentiate them. The choice has been made to do this in the parent nodes of the SimpleName nodes
 */
public class VisitorAccessRef extends AbstractRefVisitor {

	/**
	 * Whether a variable access is lhs (write) or not
	 */
	protected boolean inAssignmentLHS = false;

    /**
     * Whether to output all local variables (even those with primitive type or not (default is not).
     */
    private boolean allLocals;

	/**
	 * what sourceAnchors to create
	 */
	private anchorOptions anchors;

	private boolean inLambda;

	public VisitorAccessRef(JavaDictionary dico, boolean classSummary, boolean allLocals, anchorOptions anchors) {
		super(dico, classSummary);
		this.allLocals = allLocals;
		this.anchors = anchors;
		this.inLambda = false;
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
        visitAsSimpleName(node.getExpression());
        for (Object arg : node.arguments()) {
            visitAsSimpleName((ASTNode) arg);
        }
        if (node.getAnonymousClassDeclaration() != null) {
            node.getAnonymousClassDeclaration().accept(this);
        }
		return false;
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
     * MethodInvocation ::=
     *      [ Expression . ]
     *          [ < Type { , Type } > ]
     *          Identifier ( [ Expression { , Expression } ] )
     */
	@Override
	public boolean visit(MethodInvocation node) {
        visitAsSimpleName( node.getExpression());


        for (Object arg : node.arguments()) {
            visitAsSimpleName((ASTNode) arg);

        }
		return false;  // already visited the interesting children
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
		inLambda = true;
		node.getBody().accept(this);
		inLambda = false;
		return false;  // only visit body of lambda
	}

	/**
	 *  FieldDeclaration ::=
	 *     [Javadoc] { ExtendedModifier } Type VariableDeclarationFragment
	 *          { , VariableDeclarationFragment } ;
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(FieldDeclaration node) {
		return visitFieldDeclaration(node);  // recovers optional JavaDictionary.INIT_BLOCK_NAME method
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
		Method accessor = this.context.topMethod();
		IVariableBinding bnd = node.resolveFieldBinding();
		// FIXME if bnd == null we have a problem
		ensureAccessedStructEntity(bnd, node.getName().getIdentifier(), /*typ*/null, /*owner*/null, accessor);
		Access lastAccess = context.getLastAccess();
		if ( (anchors == anchorOptions.assoc)
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
		if ( (bnd != null) && (bnd.getKind() == IBinding.VARIABLE) ) {
			// could be a field or an enumValue
			Method accessor = this.context.topMethod();
			ensureAccessedStructEntity((IVariableBinding) bnd, node.getName().getIdentifier(), /*typ*/null,
					/*owner*/null, accessor);
			Access lastAccess = context.getLastAccess();
			if ( (anchors == anchorOptions.assoc)
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
        visitIfSimpleName( node.getLeftOperand());
        visitIfSimpleName( node.getRightOperand());
        if (node.hasExtendedOperands()) {
            for (Expression op : (List<Expression>) node.extendedOperands()) {
                visitIfSimpleName( op);
            }
        }

        return super.visit(node);
    }

    @Override
    public boolean visit(PrefixExpression node) {
        visitIfSimpleName( node.getOperand());
        return super.visit(node);
    }

    @Override
    public boolean visit(PostfixExpression node) {
        visitIfSimpleName( node.getOperand());
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
        visitIfSimpleName( node.getExpression());
        visitIfSimpleName( node.getMessage());
		return super.visit(node);
	}

	public boolean visit(Assignment node) {
		inAssignmentLHS = true;
        visitAsSimpleName( node.getLeftHandSide());
		inAssignmentLHS = false;

        visitAsSimpleName( node.getRightHandSide());

		return false;
	}

	public boolean visit(ArrayAccess node) {
		// an array might be accessed in writing (see visit(Assignment node) ),
		// but it's index is accessed in reading
		boolean tmp = inAssignmentLHS;

		node.getArray().accept(this);

		inAssignmentLHS = false;
        visitAsSimpleName( node.getIndex());
		inAssignmentLHS = tmp;

		return false;
	}

	public boolean visit(DoStatement node) {
        visitIfSimpleName( node.getExpression());
		return super.visit(node);
	}

	public boolean visit(EnhancedForStatement node) {
        visitIfSimpleName( node.getExpression());
		return super.visit(node);
	}

	public boolean visit(ForStatement node) {
        visitIfSimpleName( node.getExpression());
		return super.visit(node);
	}

	public boolean visit(IfStatement node) {
        visitIfSimpleName( node.getExpression());
		return super.visit(node);
	}

	public boolean visit(ReturnStatement node) {
        visitIfSimpleName( node.getExpression());
		return super.visit(node);
	}

	public boolean visit(SwitchCase node) {
        visitIfSimpleName( node.getExpression());
		return super.visit(node);
	}

	public boolean visit(SwitchStatement node) {
        visitIfSimpleName( node.getExpression());
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(SynchronizedStatement node) {
		visitIfSimpleName(node.getExpression());
		return super.visit(node);
	}

	public boolean visit(WhileStatement node) {
		visitIfSimpleName(node.getExpression());
		return super.visit(node);
	}

	@Override
	public boolean visit(ThrowStatement node) {
		visitIfSimpleName( node.getExpression());
		return super.visit(node);
	}

	@Override
	public boolean visit(ThisExpression node) {
		IBinding bnd = ImplicitVarBinding.getInstance(context.topMethod(), JavaDictionary.SELF_NAME);
		ImplicitVariable fmx = dico.ensureFamixImplicitVariable(bnd, JavaDictionary.SELF_NAME, this.context.topType(), context.topMethod(), /*persistIt*/!classSummary);
		if (fmx != null) {
			Method accessor = this.context.topMethod();

			createAccess(accessor, fmx, inAssignmentLHS);

			Access lastAccess = context.getLastAccess();
			if ( (anchors == anchorOptions.assoc) && (lastAccess != null) ) {
				dico.addSourceAnchor(lastAccess, node.getParent(), /*oneLineAnchor*/true);
			}
		}

		return false;
	}

	// UTILITY METHODS

    /**
     * Visit the parameter as a SimpleName node if it is one, otherwise does nothing
     */
   	private void visitIfSimpleName(ASTNode node) {
        if (node instanceof SimpleName) {
            visitSimpleName((SimpleName) node);
        }
    }

    /**
     * Visit the parameter as a SimpleName node if it is one, otherwise do a "normal" visit
     */
  	private void visitAsSimpleName(ASTNode node) {
        if (node instanceof SimpleName) {
            visitSimpleName((SimpleName) node);
        } else if (node != null) {
            node.accept(this);
        }
    }

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
		if ( (bnd != null) && (bnd.getKind() == IBinding.VARIABLE) && (context.topMethod() != null) ) {
			// could be a variable, a field, an enumValue, ...
			Method accessor = this.context.topMethod();
			ensureAccessedStructEntity((IVariableBinding) bnd, expr.getIdentifier(), /*typ*/null, /*owner*/null,
					accessor);
			Access lastAccess = context.getLastAccess();
			if ( (anchors == anchorOptions.assoc)
					// check that lastAccess corresponds to current one
					&& (lastAccess != null) && (lastAccess.getAccessor() == accessor)
					&& (lastAccess.getVariable().getName().equals(expr.getIdentifier()))) {
				dico.addSourceAnchor(lastAccess, expr, /*oneLineAnchor*/true);
			}
		}

	}

	private StructuralEntity ensureAccessedStructEntity(IVariableBinding bnd, String name,
			eu.synectique.verveine.core.gen.famix.Type typ, ContainerEntity owner, Method accessor) {
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
				}
			}

			if ((accessed != null) && (((Attribute) accessed).getParentType() == null)
					&& (accessed.getName().equals("length"))) {
				// special case: length attribute of arrays in Java
				((Attribute) accessed).setParentType(dico.ensureFamixClassArray());
			}
		} else if (bnd.isParameter() && (! inLambda)) {
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
	private void createAccess(Method accessor, StructuralEntity accessed, boolean isLHS) {
		// create local accesses?
		if ((accessed != null) && (accessor != null)) {
			if (classSummary) {
				//dico.addFamixReference(findHighestType(accessor), findHighestType(accessed), /*lastReference*/null);
			} else if (allLocals || (! localVariable(accessed, accessor)) ) {
				context.setLastAccess(
						dico.addFamixAccess(accessor, accessed, /*isWrite*/isLHS, context.getLastAccess()));
			}
		}
	}

	private boolean localVariable(StructuralEntity accessed, Method accessor) {
		if (accessed.getBelongsTo() == accessor) {
			return true;
		}
		if (accessor.getParentType().getName().startsWith(JavaDictionary.ANONYMOUS_NAME_PREFIX)) {
			return localVariable(accessed, (Method)accessor.getParentType().getContainer());
		}
		return false;
	}

}
