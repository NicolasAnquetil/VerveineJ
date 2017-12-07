package eu.synectique.verveine.extractor.java.defvisitors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
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
import eu.synectique.verveine.core.gen.famix.ParameterType;
import eu.synectique.verveine.core.gen.famix.StructuralEntity;
import eu.synectique.verveine.extractor.java.JavaDictionary;
import eu.synectique.verveine.extractor.java.RefVisitor;
import eu.synectique.verveine.extractor.java.Util;
import eu.synectique.verveine.extractor.java.VerveineJParser;

/**
 * AST Visitor that defines all the (Famix) entities of interest
 * Famix entities are stored in a Map along with the IBindings to which they correspond
 */
public class VisitorVarsDef extends RefVisitor {

	/**
	 * Whether to summarize collected information at the level of classes or produce everything
	 * (see {@link VerveineJParser#classSummary}).
	 */
	private boolean classSummary = false;

	/**
	 * Whether to output all local variables (even those with primitive type or not (default is not).<br>
	 * Note: allLocals = ! classSummary
	 */
	private boolean allLocals = false;

	/**
	 * what sourceAnchors to create
	 */
	private String anchors;

	/**
	 * set in parent of structuralEntity declaration to indicate what kind of structuralentity it is
	 */
	private StructuralEntityKinds structuralType;

	/**
	 * Used when creating a structural entity
	 */
	private enum StructuralEntityKinds {
		ATTRIBUTE, PARAMETER, LOCALVAR, IGNORE;
	}

	public VisitorVarsDef(JavaDictionary dico, boolean classSummary, boolean allLocals, String anchors) {
		super(dico);
		this.classSummary = classSummary;
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
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		IMethodBinding bnd = node.resolveBinding();

		AnnotationTypeAttribute fmx = dico.ensureFamixAnnotationTypeAttribute(bnd, node.getName().getIdentifier(), (AnnotationType) context.topType(), persistClass(null));
		if (fmx != null) {
			fmx.setIsStub(false);
			if (!anchors.equals(VerveineJParser.ANCHOR_NONE)) {
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
		return false;
	}

	@Override
	public boolean visit(EnumConstantDeclaration node) {
		EnumValue ev = dico.ensureFamixEnumValue(node.resolveVariable(), node.getName().getIdentifier(), /*owner*/(Enum)context.topType(), persistClass(((EnumDeclaration)node.getParent()).resolveBinding()));
		ev.setIsStub(false);
		return false;
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		structuralType = StructuralEntityKinds.ATTRIBUTE;

		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationExpression node) {
		// Independently of 'withLocals()', we don't declare (local) variables that have a primitive type
		// because we are assuming that the user is not interested in them (non primitive types are important because of the dependence they create)
		if (allLocals || (!node.getType().isPrimitiveType())) {
			structuralType = StructuralEntityKinds.LOCALVAR;
		}
		else {
			structuralType = StructuralEntityKinds.IGNORE;
		}

		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		// locals: same discussion as for visit(VariableDeclarationExpression node)
		if (allLocals || (!node.getType().isPrimitiveType())) {
			structuralType = StructuralEntityKinds.LOCALVAR;
		}
		else {
			structuralType = StructuralEntityKinds.IGNORE;
		}

		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		createStructuralEntity( structuralType, node, context.top());
		return super.visit(node);
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		// actually, here structuralType must be PARAMETER
		createStructuralEntity( structuralType, node, context.top());
		return super.visit(node);
	}

	// UTILITY METHODS

	private StructuralEntity createStructuralEntity(StructuralEntityKinds structKind, VariableDeclaration varDecl, NamedEntity owner) {
		StructuralEntity fmx;
		IVariableBinding bnd = varDecl.resolveBinding();
		String name = varDecl.getName().getIdentifier();

		switch (structKind) {
		case PARAMETER:	fmx = dico.ensureFamixParameter(bnd, name, (Method) owner, /*persistIt*/!classSummary);										break;
		case ATTRIBUTE: fmx = dico.ensureFamixAttribute(bnd, name, (eu.synectique.verveine.core.gen.famix.Type) owner, /*persistIt*/!classSummary);	break;
		case LOCALVAR: 	fmx = dico.ensureFamixLocalVariable(bnd, name, (Method) owner, /*persistIt*/!classSummary);									break;
		default:		fmx = null;
		}

		if (fmx != null) {
			fmx.setIsStub(false);
			if ((!classSummary) && (!anchors.equals(VerveineJParser.ANCHOR_NONE))) {
				dico.addSourceAnchor(fmx, varDecl.getParent(), /*oneLineAnchor*/true);
			}
		}

		return fmx;
	}

	/**
	 * if {@link VisitorVarsDef#classSummary} is true, we persist only classes that are not defined in methods.
	 * @param bnd -- ITypeBinding for the class that we are checking, might be null and in this case, we check whether there is no method at the top of the context
	 * @return whether to persist the class or its members
	 */
	private boolean persistClass(ITypeBinding bnd) {
		if (bnd != null) {
			if (bnd.isParameterizedType()) {
				// parameterized types seem to never belong to a method even when they are created within one
				// so we kind of "force" persistClass to consider only context by passing a null binding to it
				return persistClass(null);
			} else {
				// let see if it is a type parameter
				NamedEntity t = dico.getEntityByKey(bnd);
				if ((t != null) && (t instanceof ParameterType)) {
					return false;
				}
				// finally, the "normal" case
				return (!classSummary) || (bnd.getDeclaringMethod() == null);
			}
		} else {
			return (!classSummary) || (context.topMethod() == null);
		}

	}

}