package fr.inria.verveine.extractor.java.refvisitors;

import java.util.List;

import org.eclipse.jdt.core.dom.*;

import eu.synectique.verveine.core.gen.famix.BehaviouralEntity;
import eu.synectique.verveine.core.gen.famix.ContainerEntity;
import eu.synectique.verveine.core.gen.famix.Invocation;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.NamedEntity;
import eu.synectique.verveine.core.gen.famix.Reference;
import eu.synectique.verveine.core.gen.famix.StructuralEntity;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.VerveineJParser;
import fr.inria.verveine.extractor.java.VerveineJParser.anchorOptions;

public class VisitorTypeRefRef extends AbstractRefVisitor {

	/**
	 * what sourceAnchors to create
	 */
	private anchorOptions anchors;

	public VisitorTypeRefRef(JavaDictionary dico, boolean classSummary, anchorOptions anchors) {
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

	/**
	 * ClassInstanceCreation ::=
        [ Expression . ]
            new [ < Type { , Type } > ]
            Type ( [ Expression { , Expression } ] )
            [ AnonymousClassDeclaration ]
	 */
	@Override
	public boolean visit(ClassInstanceCreation node) {
		if (node.getAnonymousClassDeclaration() == null) {
			Type clazz = node.getType();
			eu.synectique.verveine.core.gen.famix.Type fmx = referedType(clazz, (ContainerEntity) context.top(), true);
			Reference ref = null;
			if (!classSummary) {
				ref = dico.addFamixReference( (BehaviouralEntity) context.top(), fmx, context.getLastReference());
				context.setLastReference(ref);
			}

			if ((anchors != anchorOptions.assoc) && (ref != null) ) {
				dico.addSourceAnchor(ref, node, /*oneLineAnchor*/true);
			}
		}
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

	/**
	 * MethodDeclaration ::=
    [ Javadoc ] { ExtendedModifier } [ < TypeParameter { , TypeParameter } > ] ( Type | void )
        Identifier (
            [ ReceiverParameter , ] [ FormalParameter { , FormalParameter } ]
        ) { Dimension }
        [ throws Type { , Type } ]
        ( Block | ; )
	 */
	@SuppressWarnings("unchecked")
	public boolean visit(MethodDeclaration node) {
		Method fmx = visitMethodDeclaration( node);
		if (fmx != null) {
			if (! node.isConstructor()) {
				fmx.setDeclaredType(referedType(node.getReturnType2(), fmx, false));
			}

			for (SingleVariableDeclaration param : (List<SingleVariableDeclaration>) node.parameters()) {
				StructuralEntity fmxParam = (StructuralEntity) dico.getEntityByKey(param.resolveBinding());
				if (fmxParam != null) {
					fmxParam.setDeclaredType( referedType(param.getType(), fmx, false) );
				}
			}

			return super.visit(node);
		}

		return false;
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		endVisitMethodDeclaration(node);
	}

	/**
	 * Initializer ::=
     *      [ static ] Block
     * Note:
     * VariableDeclarationFragment ::=
     *     Identifier { Dimension } [ = Expression ]
	 */
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

	public boolean visit(InstanceofExpression node) {
		eu.synectique.verveine.core.gen.famix.Type fmx = null;
		Type clazz = node.getRightOperand();
		fmx = referedType(clazz, (ContainerEntity) context.top(), true);

		Reference ref = null;
		if (classSummary) {
			//ref = dico.addFamixReference(findHighestType(context.top()), findHighestType(fmx), /*lastReference*/null);
		}
		else {
			ref = dico.addFamixReference((BehaviouralEntity) context.top(), fmx, context.getLastReference());
			context.setLastReference(ref);
		}
		if (anchors == anchorOptions.assoc) {
			dico.addSourceAnchor(ref, node, /*oneLineAnchor*/true);
		}

		return super.visit(node);
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
		visitVariableDeclaration((List<VariableDeclaration>)node.fragments(), node.getType());   // to create the TypeRefs
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

	/**
	 * VariableDeclarationExpression ::=
     *     { ExtendedModifier } Type VariableDeclarationFragment
     *          { , VariableDeclarationFragment }
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(VariableDeclarationExpression node) {
		return visitVariableDeclaration((List<VariableDeclaration>)node.fragments(), node.getType());
	}

	/**
	 *  VariableDeclarationStatement ::=
     *     { ExtendedModifier } Type VariableDeclarationFragment
     *         { , VariableDeclarationFragment } ;
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(VariableDeclarationStatement node) {
		return visitVariableDeclaration((List<VariableDeclaration>)node.fragments(), node.getType());
	}
	
	/**
	 * same behaviour for VariableDeclarationStatement and VariableDeclarationExpression
     * VariableDeclaration ::=
     *     SingleVariableDeclaration VariableDeclarationFragment
	 */
	private boolean visitVariableDeclaration(List<VariableDeclaration> fragments, Type declType) {
		setVariablesDeclaredType((List<VariableDeclaration>)fragments, referedType(declType, context.topType(), false));
		for (VariableDeclaration varDecl : fragments) {
			varDecl.accept(this);
		}
		return false;
	}

//	public boolean visit(SimpleName node) {
//		IBinding bnd = node.resolveBinding();
//		if ( (bnd != null) && (bnd instanceof ITypeBinding) ) {
//			referedType((ITypeBinding) bnd, (ContainerEntity) context.top(), !((ITypeBinding) bnd).isEnum());

	private void setVariablesDeclaredType(List<VariableDeclaration> vars, eu.synectique.verveine.core.gen.famix.Type varTyp) {
		for (VariableDeclaration var : vars) {
			StructuralEntity fmx = (StructuralEntity) dico.getEntityByKey(var.resolveBinding());
			if (fmx != null) {
				fmx.setDeclaredType(varTyp);
			}
		}
	}

	public boolean visit(MethodInvocation node) {
		Expression callingExpr = node.getExpression();
		if (callingExpr instanceof Name) {
			IBinding bnd = ((Name)callingExpr).resolveBinding();
			if ( (bnd != null) && (bnd instanceof ITypeBinding) ) {
				referedType((ITypeBinding) bnd, (ContainerEntity) context.top(), !((ITypeBinding) bnd).isEnum());
			}
		}

		return super.visit(node);
	}


}
