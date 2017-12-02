package eu.synectique.verveine.extractor.java.visitors;

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
import eu.synectique.verveine.extractor.java.Util;
import eu.synectique.verveine.extractor.java.VerveineJParser;

/**
 * AST Visitor that defines all the (Famix) entities of interest
 * Famix entities are stored in a Map along with the IBindings to which they correspond
 */
public class VisitorVarsDef extends ASTVisitor {

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
	 * A stack that keeps the current definition context (package/class/method)
	 */
	protected EntityStack context;

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
		this.dico = dico;
		this.context = new EntityStack();
		this.classSummary = classSummary;
		this.allLocals = allLocals;
		this.anchors = anchors;
	}

	// VISITOR METHODS

	@Override
	public boolean visit(CompilationUnit node) {
		//		System.err.println("TRACE, Visiting CompilationUnit: "+node.getProperty(JavaDictionary.SOURCE_FILENAME_PROPERTY));

		Namespace fmx = null;
		PackageDeclaration pckg = node.getPackage();
		if (pckg == null) {
			fmx = dico.getFamixNamespaceDefault();
		} else {
			fmx = (Namespace) dico.getEntityByKey(pckg.resolveBinding());
		}
		this.context.pushPckg(fmx);

		return super.visit(node);
	}

	@Override
	public void endVisit(CompilationUnit node) {
		this.context.popPckg();
		super.endVisit(node);
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		return false; // no need to visit children of the declaration
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		return false; // no need to visit children of the declaration	
	}

	/*
	 * Can only be a class or interface declaration
	 * Local type: see comment of visit(ClassInstanceCreation node)
	 */
	@Override
	public boolean visit(TypeDeclaration node) {
		//		System.err.println("TRACE, Visiting TypeDeclaration: "+node.getName().getIdentifier());
		ITypeBinding bnd = node.resolveBinding();
		// The class should exist, we call ensure... to recover it
		// so only the first 3 parameters are meaningful
		eu.synectique.verveine.core.gen.famix.Class fmx = dico.getFamixClass(bnd, /*name*/node.getName().getIdentifier(), (ContainerEntity) /*owner*/context.top());
		if (fmx != null) {
			this.context.pushType(fmx);
			return super.visit(node);
		} else {
			return false;
		}
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		this.context.popType();
		super.endVisit(node);
	}

	/**
	 * See field {@link VisitorClassMethodDef#anonymousSuperTypeName}<br>
	 * We could test if it is a local type (inner/anonymous) and not define it in case it does not make any reference
	 * to anything outside its owner class. But it would be a lot of work for probably little gain.
	 */
	@Override
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
	@Override
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

	@Override
	public void endVisit(AnonymousClassDeclaration node) {
		super.endVisit(node);
	}

	@Override
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

	@Override
	public void endVisit(EnumDeclaration node) {
		this.context.popType();
		super.endVisit(node);
	}

	@Override
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

	@Override
	public void endVisit(AnnotationTypeDeclaration node) {
		this.context.popType();
		super.endVisit(node);
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

	/**
	 * Local type: same as {@link VisitorVarsDef#visit(ClassInstanceCreation)}, 
	 * we create it even if it is a local method because their are too many ways it can access external things
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean visit(MethodDeclaration node) {
		IMethodBinding bnd = node.resolveBinding();

		Collection<String> paramTypes = new ArrayList<String>();
		for (SingleVariableDeclaration param : (List<SingleVariableDeclaration>) node.parameters()) {
			paramTypes.add( Util.jdtTypeName(param.getType()));
		}

		Method fmx = dico.getFamixMethod(bnd, node.getName().getIdentifier(), paramTypes, /*owner*/context.topType());

		if (fmx != null) {
			fmx.setIsStub(false);
			this.context.pushMethod(fmx);

			structuralType = StructuralEntityKinds.PARAMETER;

			return super.visit(node);
		} else {
			this.context.pushMethod(null);
			return false;
		}
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		context.popMethod();

		super.endVisit(node);
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
	@SuppressWarnings({ "unchecked" })
	public boolean visit(FieldDeclaration node) {
		structuralType = StructuralEntityKinds.ATTRIBUTE;

		return super.visit(node);
	}

	@Override
	@SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
	public boolean visit(VariableDeclarationStatement node) {
		Collection<StructuralEntity> fmxVars;

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