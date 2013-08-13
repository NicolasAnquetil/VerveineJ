package fr.inria.verveine.extractor.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import fr.inria.verveine.core.Dictionary;
import fr.inria.verveine.core.EntityStack;
import fr.inria.verveine.core.gen.famix.AnnotationInstance;
import fr.inria.verveine.core.gen.famix.AnnotationInstanceAttribute;
import fr.inria.verveine.core.gen.famix.AnnotationType;
import fr.inria.verveine.core.gen.famix.AnnotationTypeAttribute;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.BehaviouralEntity;
import fr.inria.verveine.core.gen.famix.Class;
import fr.inria.verveine.core.gen.famix.ContainerEntity;
import fr.inria.verveine.core.gen.famix.Enum;
import fr.inria.verveine.core.gen.famix.EnumValue;
import fr.inria.verveine.core.gen.famix.ImplicitVariable;
import fr.inria.verveine.core.gen.famix.Inheritance;
import fr.inria.verveine.core.gen.famix.Invocation;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.NamedEntity;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.ParameterType;
import fr.inria.verveine.core.gen.famix.ParameterizableClass;
import fr.inria.verveine.core.gen.famix.ParameterizedType;
import fr.inria.verveine.core.gen.famix.PrimitiveType;
import fr.inria.verveine.core.gen.famix.Reference;
import fr.inria.verveine.core.gen.famix.StructuralEntity;

/**
 * AST Visitor that defines all the (Famix) entities of interest
 * Famix entities are stored in a Map along with the IBindings to which they correspond
 */
public class VerveineVisitor extends ASTVisitor {

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
	private Type anonymousSuperType;

	/**
	 * Whether to summarize collected information at the level of classes or produce everything
	 * (see {@link VerveineJParser#classSummary}).
	 */
	private boolean classSummary = false;

	/**
	 * A stack that keeps the current definition context (package/class/method)
	 */
	protected EntityStack context;

	/**
	 * Useful to keep the FamixType created in the specific case of "new SomeClass().someMethod()"
	 */
	private fr.inria.verveine.core.gen.famix.Type classInstanceCreated = null;

	/**
	 * The source code of the visited AST.
	 * Used to find back the contents of non-javadoc comments
	 */
	protected RandomAccessFile source;

	/**
	 * Whether a variable access is lhs (write) or not
	 */
	protected boolean inAssignmentLHS = false;
	
	public VerveineVisitor(JavaDictionary dico, boolean classSummary) {
		this.dico = dico;
		this.context = new EntityStack();
		this.classSummary = classSummary;
	}

	// VISITOR METHODS

	public boolean visit(CompilationUnit node) {
//		System.err.println("TRACE, Visiting CompilationUnit: "+node.getProperty(JavaDictionary.SOURCE_FILENAME_PROPERTY));
		
		// As this is the first node visited in an AST, set's the source file for this AST
		try {
			source = new RandomAccessFile( (String) ((CompilationUnit)node).getProperty(JavaDictionary.SOURCE_FILENAME_PROPERTY), "r");
		} catch (FileNotFoundException e) {
			source = null;
			e.printStackTrace();
		}

		Namespace fmx = null;
		PackageDeclaration pckg = node.getPackage();
		if (pckg==null) {
			fmx = dico.ensureFamixNamespaceDefault();
		}
		else {
			fmx = dico.ensureFamixNamespace(pckg.resolveBinding(), pckg.getName().getFullyQualifiedName());
			fmx.setIsStub(false);
		}
		this.context.pushPckg(fmx);

		return super.visit(node);
	}

	public void endVisit(CompilationUnit node) {
		this.context.popPckg();
		if (source != null) {
			try {
				source.close();
			} catch (IOException e) {
				// ignore error
				e.printStackTrace();
			}
		}
		super.endVisit(node);
	}

	public boolean visit(PackageDeclaration node) {
		return false; // no need to visit children of the declaration
	}

	public boolean visit(ImportDeclaration node) {
		return false; // no need to visit children of the declaration	
	}

	/*
	 * Can only be a class or interface declaration
	 * Local type: see comment of visit(ClassInstanceCreation node)
	 */
	public boolean visit(TypeDeclaration node) {
//		System.err.println("TRACE, Visiting TypeDeclaration: "+node.getName().getIdentifier());
		ITypeBinding bnd = node.resolveBinding();
		@SuppressWarnings("unchecked")
		List<TypeParameter> tparams = (List<TypeParameter>)node.typeParameters();
		
		boolean persistIt = persistClass(bnd);
		// may be could use this.refereredType instead of dico.ensureFamixClass ?
		fr.inria.verveine.core.gen.famix.Class fmx = dico.ensureFamixClass(bnd, /*name*/node.getName().getIdentifier(), /*owner*/context.top(), /*isGeneric*/tparams.size()>0, node.getModifiers(), /*alwaysPersist?*/persistIt);
		if (fmx != null) {
			fmx.setIsStub(false);
			
			// if it is a generic and some parameterizedTypes were created for it
			// they are marked as stub which is not right
			if (tparams.size()>0) {
				for (ParameterizedType candidate : dico.getEntityByName(ParameterizedType.class, node.getName().getIdentifier())) {
					candidate.setIsStub(false);
				}
			}

			this.context.pushType(fmx);

			if (persistIt) {
				dico.addSourceAnchor(fmx, node, /*oneLineAnchor*/false);
				dico.addFamixAnnotationInstances(bnd, fmx, persistIt);
				addUnboundAnnotationInstances(node, fmx, persistIt);
				
				//comments
				if (dico.createFamixComment(node.getJavadoc(), fmx, source) == null) {
					CompilationUnit astRoot = (CompilationUnit) node.getRoot();
					int iCmt = astRoot.firstLeadingCommentIndex(node);
					if ( (source != null) && (iCmt > -1) ) {
						dico.createFamixComment((Comment)astRoot.getCommentList().get(iCmt), fmx, source);
					}
				}
			}
			else {
				for (Inheritance inh : fmx.getSuperInheritances()) {
					Reference ref = dico.addFamixReference(findHighestType(context.top()), findHighestType(inh.getSuperclass()), null);
					dico.addSourceAnchor(ref, node, /*oneLineAnchor*/true);
				}
			}

			for (TypeParameter tp : tparams) {
				// if there is a type parameter, then fmx will be a Famix ParameterizableClass
				// note: owner of the ParameterType is the ParameterizableClass
				ParameterType fmxParam = dico.ensureFamixParameterType( tp.resolveBinding(), tp.getName().getIdentifier(), (ParameterizableClass)fmx, /*persistIt*/!classSummary);
				if (fmxParam != null) {
					fmxParam.setIsStub(false);
				}
			}
			
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private void addUnboundAnnotationInstances(TypeDeclaration typDecl, fr.inria.verveine.core.gen.famix.Class fmx,	boolean persistIt) {
		for (IExtendedModifier mod : (List<IExtendedModifier>)typDecl.modifiers()) {
			// create type of the annotation
			if (mod.isAnnotation()) {
				Annotation ann = (Annotation)mod;
				// see if it was not already created by any chance
				boolean found = false;
				for (AnnotationInstance a : fmx.getAnnotationInstances()) {
					if (a.getAnnotationType().getName().equals(ann.getTypeName().getFullyQualifiedName())) {
						found = true;
					}
				}
				if (! found) {
					AnnotationType annType = dico.ensureFamixAnnotationType(/*bnd*/null, ann.getTypeName().getFullyQualifiedName(), /*owner*/dico.ensureFamixNamespaceDefault(), persistIt);	

					// create all parameters of the annotation instance
					Collection<AnnotationInstanceAttribute> annAtts = new ArrayList<AnnotationInstanceAttribute>(); 
					if (ann instanceof SingleMemberAnnotation) {
						annAtts.add( dico.createFamixAnnotationInstanceAttribute( dico.ensureFamixAnnotationTypeAttribute(/*bnd*/null, "value", annType, persistIt),
									 ( ((SingleMemberAnnotation)ann).getValue() != null) ? ((SingleMemberAnnotation)ann).getValue().toString() : ""));
					}
					else if (ann instanceof NormalAnnotation) {
						for (MemberValuePair annPV : (List<MemberValuePair>) ((NormalAnnotation)ann).values()) {
							annAtts.add( dico.createFamixAnnotationInstanceAttribute( dico.ensureFamixAnnotationTypeAttribute(/*bnd*/null, annPV.getName().getIdentifier(), annType, persistIt),
																					  (annPV.getValue() != null) ? annPV.getValue().toString() : ""));
						}
					}

					// create the annotation instance
					dico.addFamixAnnotationInstance(fmx, annType, annAtts);
				}
			}

		}

	}

	public void endVisit(TypeDeclaration node) {
		this.context.popType();
		super.endVisit(node);
	}

	/**
	 * See {@link VerveineVisitor#anonymousSuperType}<br>
	 * We could test if it is a local type (inner/anonymous) and not define it in case it does not make any reference
	 * to anything outside its owner class. But it would be a lot of work for probably little gain.
	 */
	@SuppressWarnings("unchecked")
	public boolean visit(ClassInstanceCreation node) {
//		System.err.println("TRACE, Visiting ClassInstanceCreation: " + node);
		if (node.getAnonymousClassDeclaration() != null) {
			anonymousSuperType = node.getType();
		}
		else {
			anonymousSuperType = null;

			// treat the expression 'new A(...)' by creating a Reference to 'A'
			fr.inria.verveine.core.gen.famix.Type fmx = null;
			Type clazz = node.getType();
			fmx = referedType(clazz, context.top(), true);
			this.classInstanceCreated = fmx;
			
			if (classSummary) {
				Reference ref = dico.addFamixReference(findHighestType(context.top()), findHighestType(fmx), null);
				dico.addSourceAnchor(ref, node, /*oneLineAnchor*/true);
			}
			else {
				Reference lastRef = context.getLastReference();
				dico.addFamixReference(context.top(), fmx, lastRef);
				// create an invocation to the constructor
				methodInvocation(node.resolveConstructorBinding(), findTypeName(clazz), /*receiver*/null, /*methOwner*/fmx, node.arguments());
			}
		}
		return super.visit(node);
	}

	/**
	 * See {@link VerveineVisitor#anonymousSuperType}
	 */
	public boolean visit(AnonymousClassDeclaration node) {
//		System.err.println("TRACE, Visiting AnonymousClassDeclaration");
		fr.inria.verveine.core.gen.famix.Class fmx = null;
		ITypeBinding bnd = node.resolveBinding();
		String anonSuperTypeName = (anonymousSuperType != null) ? findTypeName(anonymousSuperType) : context.topType().getName();
		int modifiers = (bnd != null) ? bnd.getModifiers() : JavaDictionary.UNKNOWN_MODIFIERS;
		fmx = this.dico.ensureFamixClass(bnd, /*name*/"anonymous("+anonSuperTypeName+")", /*owner*/context.top(), /*isGeneric*/false, modifiers, /*alwaysPersist?*/!classSummary);
		if (fmx != null) {
			fmx.setIsStub(false);

			if (classSummary) {
				for (Inheritance inh : fmx.getSuperInheritances()) {
					Reference ref = dico.addFamixReference(findHighestType(context.top()), findHighestType(inh.getSuperclass()), null);
					dico.addSourceAnchor(ref, node, /*oneLineAnchor*/true);
				}
			}
			else {
				dico.addFamixAnnotationInstances(bnd, fmx, /*persistIt=true*/!classSummary);
				dico.addSourceAnchor(fmx, node, /*oneLineAnchor*/false);
			}
			this.context.pushType(fmx);
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	public void endVisit(AnonymousClassDeclaration node) {
		anonymousSuperType = null;
		this.context.popType();
		super.endVisit(node);
	}

	@SuppressWarnings("unchecked")
	public boolean visit(EnumDeclaration node) {
//		System.err.println("TRACE, Visiting EnumDeclaration: "+node.getName().getIdentifier());

		fr.inria.verveine.core.gen.famix.Enum fmx = dico.ensureFamixEnum(node.resolveBinding(), node.getName().getIdentifier(), context.top());
		if (fmx != null) {
			fmx.setIsStub(Boolean.FALSE);

			this.context.pushType(fmx);
			dico.addSourceAnchor(fmx, node, /*oneLineAnchor*/false);

			// possibly not persisting the enum's memebrs, i.e. enum-values
			for (EnumConstantDeclaration ecst : (List<EnumConstantDeclaration>)node.enumConstants()) {
				EnumValue ev = dico.ensureFamixEnumValue(ecst.resolveVariable(), ecst.getName().getIdentifier(), fmx, persistClass(node.resolveBinding()));
				ev.setIsStub(Boolean.FALSE);
			}
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	public void endVisit(EnumDeclaration node) {
		this.context.popType();
		super.endVisit(node);
	}

	public boolean visit(AnnotationTypeDeclaration node) {
//		System.err.println("TRACE, Visiting AnnotationTypeDeclaration: "+node.getName().getIdentifier());

		ITypeBinding bnd = node.resolveBinding();
		AnnotationType fmx = dico.ensureFamixAnnotationType(bnd, node.getName().getIdentifier(), context.top(), persistClass(bnd));
		if (fmx != null) {
			fmx.setIsStub(Boolean.FALSE);
			dico.addSourceAnchor(fmx, node, /*oneLineAnchor*/false);

			context.pushType(fmx);
			return super.visit(node);
		}
		else {
			context.pushType(null);
			return false;
		}
	}

	public void endVisit(AnnotationTypeDeclaration node) {
		this.context.popType();
		super.endVisit(node);
	}

	public boolean visit(AnnotationTypeMemberDeclaration node) {
//		System.err.println("TRACE, Visiting AnnotationTypeMemberDeclaration: "+node.getName().getIdentifier());
		IMethodBinding bnd = node.resolveBinding();

		AnnotationTypeAttribute fmx = dico.ensureFamixAnnotationTypeAttribute(bnd, node.getName().getIdentifier(), (AnnotationType) context.topType(), persistClass(null));
		if (fmx != null) {
			fmx.setIsStub(false);
			dico.addSourceAnchor(fmx, node, /*oneLineAnchor*/false);

			context.pushAnnotationMember(fmx);
			return super.visit(node);
		}
		else {
			context.pushAnnotationMember(null);
			return false;
		}
	}

	public void endVisit(AnnotationTypeMemberDeclaration node) {
		this.context.popAnnotationMember();
		super.endVisit(node);
	}

	/**
	 * Local type: same as {@link VerveineVisitor#visit(ClassInstanceCreation)}, 
	 * we create it even if it is a local method because their are too many ways it can access external things
	 */
	@SuppressWarnings("unchecked")
	public boolean visit(MethodDeclaration node) {
//		System.err.println("TRACE, Visiting MethodDeclaration: "+node.getName().getIdentifier());

		// some info needed to create the Famix Method
		IMethodBinding bnd = node.resolveBinding();

		Collection<String> paramTypes = new ArrayList<String>();
		for (SingleVariableDeclaration param : (List<SingleVariableDeclaration>)node.parameters()) {
				paramTypes.add(findTypeName(param.getType()));
		}

		fr.inria.verveine.core.gen.famix.Type fmxRetTyp = null;
		if (! node.isConstructor()) {
			// we will create the method with a fake return type (Object) because we might need this FamixMethod to create the return type (if it is a ParameterizedType)
			// we reset the return type to its proper value later
			fmxRetTyp = dico.ensureFamixClassObject(null);
		}
		Method fmx = dico.ensureFamixMethod(bnd, node.getName().getIdentifier(), paramTypes, /*retType*/fmxRetTyp, /*owner*/context.topType(), node.getModifiers(), /*persitIt*/!classSummary);

		if (fmx != null) {
			fmx.setIsStub(false);
			this.context.pushMethod(fmx);

			// now will recompute the actual returnType
			if (node.isConstructor()) {
				fmx.setKind(JavaDictionary.CONSTRUCTOR_KIND_MARKER);
			}
			else {
				fmxRetTyp = referedType(node.getReturnType2(), fmx, false);
				fmx.setDeclaredType(fmxRetTyp);

				if ( classSummary && (! (fmxRetTyp instanceof PrimitiveType)) ) {
					Reference ref = dico.addFamixReference(findHighestType(fmx.getParentType()), findHighestType(fmxRetTyp), null);
					dico.addSourceAnchor(ref, node, /*oneLineAnchor*/true);
				}
			}

			if (! classSummary) {
				dico.addSourceAnchor(fmx, node, /*oneLineAnchor*/false);
				dico.addFamixAnnotationInstances(bnd, fmx, /*persistIt=true*/!classSummary);

				//comments
				if (dico.createFamixComment(node.getJavadoc(), fmx, source) == null) {
					CompilationUnit astRoot = (CompilationUnit) node.getRoot();
					int iCmt = astRoot.firstLeadingCommentIndex(node);
					if ( (source != null) && (iCmt > -1) ) {
						dico.createFamixComment((Comment)astRoot.getCommentList().get(iCmt), fmx, source);
					}
				}
			}

			if (node.getBody() != null) {
				context.setTopMethodCyclo(1);
			}

			// creating the method's parameters
			// unless classSummary is true in which case we might need to create References between classes
			List<VariableDeclaration> paramAsVarList;
			for (SingleVariableDeclaration param : (List<SingleVariableDeclaration>)node.parameters()) {
				// Note: method and ParamTyp bindings are null for ParameterType :-(
				paramAsVarList = new ArrayList<VariableDeclaration>(1);
				paramAsVarList.add(param);

				fr.inria.verveine.core.gen.famix.Type varTyp = referedType(param.getType(), fmx, false);
				visitVariablesDeclarations(node, varTyp, paramAsVarList, fmx);
			}

			// Exceptions
			for (Name excepName : (List<Name>)node.thrownExceptions()) {
				fr.inria.verveine.core.gen.famix.Class excepFmx = (Class) this.referedType(excepName.resolveTypeBinding(), context.topType(), true);
				if (excepFmx != null) {
					if (classSummary) {
						dico.addFamixReference(findHighestType(fmx), findHighestType(excepFmx), null);
					}
					else {
						dico.createFamixDeclaredException(fmx, excepFmx);
					}
				}
			}

			return super.visit(node);
		}
		else {
			this.context.pushMethod(null);
			return false;
		}
	}

	public void endVisit(MethodDeclaration node) {
		closeMethodDeclaration();
		super.endVisit(node);
	}

	@Override
	public boolean visit(Initializer node) {
//		System.err.println("TRACE, Visiting Initializer: ");
		
		Method fmx = dico.ensureFamixMethod((IMethodBinding)null, JavaDictionary.INIT_BLOCK_NAME, /*paramTypes*/new ArrayList<String>(), /*retType*/null, context.topType(), node.getModifiers(), /*persistIt*/!classSummary);
		// init-block don't have return type so no need to create a reference from this class to the "declared return type" class when classSummary is TRUE
		// also no parameters specified here, so no references to create either

		if (fmx != null) {
			fmx.setIsStub(false);

			pushInitBlockMethod(fmx);
			if (! classSummary) {
				dico.addSourceAnchor(fmx, node, /*oneLineAnchor*/false);
				dico.createFamixComment(node.getJavadoc(), fmx, source);
			}

			if (node.getBody() != null) {
				context.setTopMethodCyclo(1);
			}

			return super.visit(node);
		}
		else {
			this.context.pushMethod(null);
			return false;
		}
	}

	@Override
	public void endVisit(Initializer node) {
		closeMethodDeclaration();
		super.endVisit(node);
	}

	@SuppressWarnings({ "unchecked" })
	public boolean visit(FieldDeclaration node) {
//		System.err.println("TRACE, Visiting FieldDeclaration");

		fr.inria.verveine.core.gen.famix.Type varTyp = referedType(node.getType(), context.topType(), false);

		for (StructuralEntity att : visitVariablesDeclarations(node, varTyp, (List<VariableDeclaration>)node.fragments(), context.topType()) ) {
			if (! classSummary) {
				dico.addSourceAnchor(att, node, /*oneLineAnchor*/false);

				//comments
				if (dico.createFamixComment(node.getJavadoc(), att, source) == null) {
					CompilationUnit astRoot = (CompilationUnit) node.getRoot();
					int iCmt = astRoot.firstLeadingCommentIndex(node);
					if ( (source != null) && (iCmt > -1) ) {
						dico.createFamixComment((Comment)astRoot.getCommentList().get(iCmt), att, source);
					}
				}
			}
		}

		return super.visit(node);
	}

	public void endVisit(FieldDeclaration node) {
		Method ctxtMeth = this.context.topMethod();
		if 	( (ctxtMeth != null) && (ctxtMeth.getName().equals(JavaDictionary.INIT_BLOCK_NAME)) ) {
			closeMethodDeclaration();
		}
	}

	@SuppressWarnings("unchecked")
	public boolean visit(VariableDeclarationExpression node) {
//		System.err.println("TRACE, Visiting VariableDeclarationExpression: "+((VariableDeclaration)node.fragments().iterator().next()).getName().getIdentifier()+" (...)");

		// Independently of 'withLocals()', we don't declare (local) variables that have a primitive type
		// because we are assuming that the user is not interested in them 
		if (! node.getType().isPrimitiveType()) {
			fr.inria.verveine.core.gen.famix.Type varTyp = referedType(node.getType(), context.topMethod(), false);

			for (StructuralEntity att : visitVariablesDeclarations(node, varTyp, (List<VariableDeclaration>)node.fragments(), context.topMethod())) {
				if (! classSummary) {
					dico.addSourceAnchor(att, node, /*oneLineAnchor*/false);
				}
			}
		}

		return super.visit(node);
	}

	@SuppressWarnings("unchecked")
	public boolean visit(VariableDeclarationStatement node) {
//		System.err.println("TRACE, Visiting VariableDeclarationStatement: "+((VariableDeclaration)node.fragments().iterator().next()).getName().getIdentifier()+" (...)");

		// locals: same discussion as for visit(VariableDeclarationExpression node)
		if (! node.getType().isPrimitiveType()) {
			fr.inria.verveine.core.gen.famix.Type varTyp = referedType(node.getType(), context.topMethod(), false);
			for (StructuralEntity var : visitVariablesDeclarations(node, varTyp, (List<VariableDeclaration>)node.fragments(), context.topMethod())) {
				if (! classSummary) {
					dico.addSourceAnchor(var, node, /*oneLineAnchor*/true);
				}
			}
		}

		return super.visit(node);
	}

	@SuppressWarnings("unchecked")
	public boolean visit(MethodInvocation node) {
//		System.err.println("TRACE, Visiting MethodInvocation");

		Expression callingExpr = node.getExpression();
		NamedEntity receiver = getReceiver(callingExpr);
		IMethodBinding bnd = node.resolveMethodBinding();
		if (bnd == null) {
			methodInvocation(bnd, node.getName().getFullyQualifiedName(), receiver, getInvokedMethodOwner(callingExpr, receiver), node.arguments());
		}
		else {
			methodInvocation(bnd, node.getName().getFullyQualifiedName(), receiver, /*owner*/null, node.arguments());
		}
		if (callingExpr instanceof SimpleName) {
			visitSimpleName((SimpleName) callingExpr);
		}

		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	@SuppressWarnings("unchecked")
	public boolean visit(SuperMethodInvocation node) {
		NamedEntity receiver = this.dico.ensureFamixImplicitVariable(Dictionary.SUPER_NAME, this.context.topType(), context.topMethod(), /*persistIt*/!classSummary);
		IMethodBinding bnd = node.resolveMethodBinding();
		if (bnd == null) {
			Iterator<Inheritance> iter = this.context.topType().getSuperInheritances().iterator();
			fr.inria.verveine.core.gen.famix.Type superClass = iter.next().getSuperclass();
			while ( (superClass instanceof fr.inria.verveine.core.gen.famix.Class) &&
					(((fr.inria.verveine.core.gen.famix.Class)superClass).getIsInterface()) &&
					iter.hasNext() ) {
				iter.next().getSuperclass();
			}
			methodInvocation(bnd, node.getName().getFullyQualifiedName(), receiver, superClass, node.arguments());
		}
		else {
			methodInvocation(bnd, node.getName().getFullyQualifiedName(), receiver, /*methOwner*/null, node.arguments());
		}

		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(ConstructorInvocation node) {
//		System.err.println("TRACE, Visiting ConstructorInvocation: ");
		this.context.addTopMethodNOS(1);
		
		// ConstructorInvocation (i.e. 'this(...)' ) happen in constructor, so the name is the same
		String name = context.topMethod().getName();
		int modifiers = (node.resolveConstructorBinding() != null) ? node.resolveConstructorBinding().getModifiers() : JavaDictionary.UNKNOWN_MODIFIERS;
		Method invoked = dico.ensureFamixMethod(node.resolveConstructorBinding(), name, /*paramTypes*/(Collection<String>)null, /*retType*/null, /*owner*/context.topType(), modifiers, /*persistIt*/!classSummary);
		// constructor don't have return type so no need to create a reference from this class to the "declared return type" class when classSummary is TRUE
		// also no parameters specified here, so no references to create for them either

		if (classSummary) {
			// a reference of the class to itself
			Reference ref = dico.addFamixReference(findHighestType(context.topType()), findHighestType(context.topType()), null);
			dico.addSourceAnchor(ref, node, /*oneLineAnchor*/true);
		}
		else {
			String signature = node.toString();
			if (signature.endsWith("\n")) {
				signature = signature.substring(0, signature.length()-1);
			}
			if (signature.endsWith(";")) {
				signature = signature.substring(0, signature.length()-1);
			}
			ImplicitVariable receiver = dico.ensureFamixImplicitVariable(Dictionary.SELF_NAME, (Class) context.topType(), context.topMethod(), /*persistIt=true*/!classSummary);
			Invocation invok = dico.addFamixInvocation(context.topMethod(), invoked, receiver, signature, context.getLastInvocation());
			context.setLastInvocation( invok );
		}

		return super.visit(node);
	}

	public boolean visit(SuperConstructorInvocation node) {
		this.context.addTopMethodNOS(1);
		
		// ConstructorInvocation (i.e. 'super(...)' ) happen in constructor, so the name is that of the superclass
		Method invoked = this.dico.ensureFamixMethod(node.resolveConstructorBinding(), /*persistIt*/!classSummary);
		// constructor don't have return type so no need to create a reference from this class to the "declared return type" class when classSummary is TRUE
		// also no parameters specified here, so no references to create either

		if (classSummary) {
			Reference ref = dico.addFamixReference(findHighestType(context.topMethod()), findHighestType(invoked), null);
			dico.addSourceAnchor(ref, node, /*oneLineAnchor*/true);
		}
		else {
			String signature = node.toString();
			if (signature.endsWith("\n")) {
				signature = signature.substring(0, signature.length()-1);
			}
			if (signature.endsWith(";")) {
				signature = signature.substring(0, signature.length()-1);
			}
			ImplicitVariable receiver = dico.ensureFamixImplicitVariable(Dictionary.SUPER_NAME, (Class) context.topType(), context.topMethod(), /*persistIt=true*/!classSummary);
			Invocation invok = dico.addFamixInvocation(context.topMethod(), invoked, receiver, signature, context.getLastInvocation());
			context.setLastInvocation( invok );
		}

		return super.visit(node);
	}

	public boolean visit(FieldAccess node) {
//		System.err.println("TRACE, Visiting FieldAccess: "+node.getName().getIdentifier() + " inAssignmentLHS=" + inAssignmentLHS);
		BehaviouralEntity accessor = this.context.topMethod();
		IVariableBinding bnd = node.resolveFieldBinding();
		// FIXME if bnd == null we have a problem
		ensureAccessedStructEntity(bnd, node.getName().getIdentifier(), /*typ*/null, /*owner*/null, accessor);

		return super.visit(node);
	}

	/*
	 * Could be a FieldAccess (see JDT javadoc: class FieldAccess) 
	 */
	public boolean visit(QualifiedName node) {
//		System.err.println("TRACE, Visiting QualifiedName: "+node.getName().getIdentifier());

		IBinding bnd = node.resolveBinding();
		if (bnd instanceof IVariableBinding) {
			// could be a field or an enumValue
			BehaviouralEntity accessor = this.context.topMethod();
			ensureAccessedStructEntity((IVariableBinding)bnd, node.getName().getIdentifier(), /*typ*/null, /*owner*/null, accessor);
		}
		return super.visit(node);
	}

	@SuppressWarnings("unchecked")
	public boolean visit(InfixExpression node) {
//		System.err.println("TRACE, Visiting InfixExpression: "+node);

		if (node.getLeftOperand() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getLeftOperand());
		}
		if (node.getRightOperand() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getRightOperand());
		}
		if (node.hasExtendedOperands()) {
			for (Expression op : (List<Expression>)node.extendedOperands()) {
				if (op instanceof SimpleName) {
					visitSimpleName((SimpleName) op);
				}
			}
		}

		return super.visit(node);
	}

	/* 
	 * Another FieldAccess in disguise: SomeClass.class
	 */
	public boolean visit(TypeLiteral node) {
		fr.inria.verveine.core.gen.famix.Type javaMetaClass = dico.ensureFamixMetaClass(null); 
		BehaviouralEntity accessor = this.context.topMethod();

		Attribute accessed =  dico.ensureFamixAttribute(null, "class", javaMetaClass, javaMetaClass, /*persistIt*/!classSummary);
		createAccess(accessor, accessed, inAssignmentLHS);

		return super.visit(node);
	}

	@Override
	public boolean visit(CatchClause node) {
		Method meth = this.context.topMethod();
		Type excepClass = node.getException().getType();
		if (meth != null) {
			fr.inria.verveine.core.gen.famix.Class excepFmx = null;
			if ( (excepClass instanceof SimpleType) || (excepClass instanceof QualifiedType) ) {
				excepFmx = (Class) referedType(excepClass, meth, true);
			}
			if (excepFmx != null) {
				if (classSummary) {
					Reference ref = dico.addFamixReference(findHighestType(meth), findHighestType(excepFmx), null);
					dico.addSourceAnchor(ref, node, /*oneLineAnchor*/true);
				}
				else {
					dico.createFamixCaughtException(meth, excepFmx);
				}
			}
		}

		return super.visit(node);
	}

	@Override
	public boolean visit(ThrowStatement node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		this.context.addTopMethodNOS(1);

		Method meth = this.context.topMethod();
		fr.inria.verveine.core.gen.famix.Class excepFmx = (Class) this.referedType(node.getExpression().resolveTypeBinding(), context.topType(), true);
		if (excepFmx != null) {
			if (classSummary) {
				Reference ref = dico.addFamixReference(findHighestType(meth), findHighestType(excepFmx), null);
				dico.addSourceAnchor(ref, node, /*oneLineAnchor*/true);
			}
			else {
				dico.createFamixThrownException(meth, excepFmx);
			}
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
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(Assignment node) {
//		System.err.println("TRACE, Visiting Assignment: "+node + " inAssignmentLHS=" + inAssignmentLHS);


		inAssignmentLHS = true;
		if (node.getLeftHandSide() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getLeftHandSide());
		}
		else {
			node.getLeftHandSide().accept(this);
		}
		inAssignmentLHS = false;
		
		if (node.getRightHandSide() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getRightHandSide());
		}	
		else {
			node.getRightHandSide().accept(this);
		}
		this.context.addTopMethodNOS(1);

		return false;
	}
	
	public boolean visit(ArrayAccess node) {
//		System.err.println("TRACE, Visiting ArrayAccess: "+node + " inAssignmentLHS=" + inAssignmentLHS);
		// an array might be accessed in writing (see visit(Assignment node) ),
		// but it's index is accessed in reading
		boolean tmp = inAssignmentLHS;
		
		node.getArray().accept(this);

		inAssignmentLHS = false;
		if (node.getIndex() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getIndex());
		}
		else {
			node.getIndex().accept(this);
		}
		inAssignmentLHS = tmp;
		
		return false;
	}
	
	public boolean visit(ContinueStatement node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(DoStatement node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(EnhancedForStatement node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(ForStatement node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(IfStatement node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(ReturnStatement node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(SwitchCase node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
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
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(TryStatement node) {
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(WhileStatement node) {
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	// UTILITY METHODS

	/**
	 * Special method InitBlock may be "created" in various steps,
	 * mainly when attributes are declared+initialized with the result of a method call.<br>
	 * In such a case, we need to recover the previous metric values to add to them
	 * @param fmx -- the InitBlock FamixMethod
	 */
	private void pushInitBlockMethod(Method fmx) {
		int nos   = (fmx.getNumberOfStatements() == null) ? 0 : fmx.getNumberOfStatements().intValue();
		int cyclo = (fmx.getCyclomaticComplexity() == null) ? 0 : fmx.getCyclomaticComplexity().intValue();
		this.context.pushMethod(fmx);
		if ( (nos != 0) || (cyclo != 0) ) {
			context.setTopMethodNOS(nos);
			context.setTopMethodCyclo(cyclo);
		}
	}
	
	/**
	 * When closing a method declaration, we need to take care of some metrics that are also collected
	 */
	private void closeMethodDeclaration() {
		if (context.topMethod() != null) {
			int cyclo = context.getTopMethodCyclo();
			int nos = context.getTopMethodNOS();
			Method fmx = this.context.popMethod();
			if (fmx != null) {
				fmx.setNumberOfStatements(nos);
				fmx.setCyclomaticComplexity(cyclo);
			}
		}
	}

	/**
	 * Handles an invocation of a method by creating the corresponding Famix Entity.
	 * That may be a reference between classes depending on {@link VerveineVisitor#classSummary}.
	 * @param calledBnd -- a binding for the method invoked
	 * @param calledName of the method invoked
	 * @param receiver of the call, i.e. the object to which the message is sent
	 * @param methOwner -- owner of the method invoked. Might be a subtype of the receiver's type
	 * @param l_args -- list of the method's parameters
	 * TODO Why are Invocations, Accesses and References not created through a method in JavaDictionnary ?
	 */
	private void methodInvocation(IMethodBinding calledBnd, String calledName, NamedEntity receiver, fr.inria.verveine.core.gen.famix.Type methOwner, Collection<Expression> l_args) {
		BehaviouralEntity sender = this.context.topMethod();
		Method invoked = null;

		if (calledBnd != null) {
			// for parameterized methods there is a level of indirection, for other methods doesn't change anything
			calledBnd = calledBnd.getMethodDeclaration();
		}
		
		if ( (receiver != null) && (receiver.getName().equals("class")) && (calledBnd != null) && (calledBnd.getDeclaringClass()==null)) {
			/*bug with JDT apparently has to do with invoking a method of a meta-class */
			// Note: if classSummary is true, do we persist the meta-class and the references to it ?
			// for now we ignore it, meaning we persist the meta-class, but don't record references to it
			invoked = (Method) dico.getEntityByKey(calledBnd);
			if ( (invoked==null) && (sender != null) ) {
				fr.inria.verveine.core.gen.famix.Class javaMetaClass = dico.ensureFamixMetaClass(null);  
				for (Method m : javaMetaClass.getMethods()) {
					if (m.getName().equals(calledName)) {
						invoked = m;
						break;
					}
				}
				if (invoked == null) {
					fr.inria.verveine.core.gen.famix.Type retType = this.referedType(calledBnd.getReturnType(), javaMetaClass, false);
					Collection<String> paramTypes = new ArrayList<String>();
					// types of the method's parameters
					for (ITypeBinding pt : calledBnd.getParameterTypes()) {
							paramTypes.add(pt.getName());
					}
					invoked = this.dico.ensureFamixMethod(null, calledName, paramTypes, /*retType*/retType, /*owner*/javaMetaClass, /*modifiers*/JavaDictionary.UNKNOWN_MODIFIERS, /*persisitIt*/!classSummary);
					dico.mapKey(calledBnd, invoked);
				}
			}
			// humm ... we never create the FamixInvocation ? Why is it so?
		}
		else if ( (calledBnd != null) && (calledBnd.isAnnotationMember()) ) {
			// if this is not an AnnotationType member, it is similar to creating a FamixAttribute access
			AnnotationTypeAttribute accessed =  dico.ensureFamixAnnotationTypeAttribute(calledBnd, calledName, /*owner*/null, /*persistIt*/!classSummary);
			createAccess(sender, accessed, inAssignmentLHS);
		}
		else {
			Collection<String> unkwnArgs = new ArrayList<String>();
			if (l_args != null) {
				for (@SuppressWarnings("unused") Expression a : l_args) {
					unkwnArgs.add("?");
				}
			}
			
			if (sender != null) {
				int modifiers = (calledBnd != null) ? calledBnd.getModifiers() : JavaDictionary.UNKNOWN_MODIFIERS;
				if ( (receiver != null) && (receiver instanceof StructuralEntity) ) {
					invoked = this.dico.ensureFamixMethod(calledBnd, calledName, unkwnArgs, /*retType*/null, methOwner, modifiers, /*persistIt*/!classSummary);
				}
				else {
					fr.inria.verveine.core.gen.famix.Type owner;
					
					if(receiver != null)
						owner = (fr.inria.verveine.core.gen.famix.Type) receiver;
					else
						owner = methOwner;
					//  static method called on the class (or null receiver)
					invoked = this.dico.ensureFamixMethod(calledBnd, calledName, unkwnArgs, /*retType*/null, /*owner*/owner, modifiers, /*persistIt*/!classSummary);
				}
				if (classSummary) {
					dico.addFamixReference(findHighestType(sender), findHighestType(methOwner), null);
				}
				else {
					String signature = calledName + "(";
					boolean first = true;
					for (Expression a : l_args) {
						if (first) {
							signature += a.toString();
							first = false;
						}
						else {
							signature += "," + a.toString();
						}
					}
					signature += ")";
					Invocation invok = dico.addFamixInvocation(sender, invoked, receiver, signature, context.getLastInvocation());
					context.setLastInvocation( invok );
				}
			}
		}

		for (Expression a : l_args) {
			if (a instanceof SimpleName) {
				visitSimpleName((SimpleName) a);
			}
		}
	}

	private Collection<StructuralEntity> visitVariablesDeclarations(ASTNode node, fr.inria.verveine.core.gen.famix.Type varTyp, List<VariableDeclaration> fragments, ContainerEntity ctxt) {
		Collection<StructuralEntity> ret = new ArrayList<StructuralEntity>();

		// we can declare the variables ...
		for (VariableDeclaration vd : fragments) {
			StructuralEntity fmx;
			IVariableBinding bnd = vd.resolveBinding();
			String name = vd.getName().getIdentifier();

			if (node instanceof MethodDeclaration) {
				// creating the parameters of a method. In this case, 'fragment' is aList<SingleVariableDeclarationFragment> and 'varType' is null
				fmx = dico.ensureFamixParameter(bnd, name, varTyp, (Method)ctxt, /*persistIt*/!classSummary);
			}
			else if (node instanceof FieldDeclaration) {
				// creating a class' field
				fmx = dico.ensureFamixAttribute(bnd, name, varTyp, (fr.inria.verveine.core.gen.famix.Type) ctxt, /*persistIt*/!classSummary);

				// putting field's initialization code in an INIT_BLOCK_NAME method
				Method ctxtMeth = this.context.topMethod();
				if ( (vd.getInitializer() != null) && 
					 ( (ctxtMeth == null) || (! ctxtMeth.getName().equals(JavaDictionary.INIT_BLOCK_NAME)) ) ) {
					ctxtMeth = dico.ensureFamixMethod((IMethodBinding)null, JavaDictionary.INIT_BLOCK_NAME, new ArrayList<String>(), /*retType*/null, context.topType(), /*modifiers*/JavaDictionary.UNKNOWN_MODIFIERS, /*persistIt*/!classSummary);
					ctxtMeth.setIsStub(false);
					// initialization block doesn't have return type so no need to create a reference from its class to the "declared return type" class when classSummary is TRUE
					pushInitBlockMethod(ctxtMeth);
				}
			}
			else if ( (node instanceof VariableDeclarationExpression) || (node instanceof VariableDeclarationStatement) ) {
				// creating a method's local variable
				fmx = dico.ensureFamixLocalVariable(bnd, name, varTyp, (Method) ctxt, /*persistIt*/!classSummary);
			}
			else {
				fmx = null;
			}

			if (fmx != null) {
				fmx.setIsStub(false);
				if (! classSummary) {
					dico.addFamixAnnotationInstances(bnd, fmx, /*persistIt=true*/!classSummary);
				}
				ret.add(fmx);
			}
		}
		
		// if classSummarise is true, we might need to create a reference from the class "owning" the variables to the class of the varTyp
		if (classSummary && (! (varTyp instanceof PrimitiveType)) ) {
			Reference ref = dico.addFamixReference(findHighestType(ctxt), findHighestType(varTyp), null);
			dico.addSourceAnchor(ref, node, /*oneLineAnchor*/true);
		}

		return ret;
	}

	/**
	 * Ensures the proper creation of a FamixType for JDT typ in the given context.
	 * Useful for parameterizedTypes, or classInstance.
	 * @param isClass we are sure that the type is actually a class
	 * @return a famix type or null
	 */
	private fr.inria.verveine.core.gen.famix.Type referedType(Type typ, ContainerEntity ctxt, boolean isClass) {
		if (typ == null) {
			return null;
		}
		else if (typ.resolveBinding() != null) {
			return this.referedType(typ.resolveBinding(), ctxt, isClass);
		}
		// from here, we assume the owner is the context
		else if(isClass) {
				return dico.ensureFamixClass((ITypeBinding)null, findTypeName(typ), /*owner*/ctxt, /*isGeneric*/false, JavaDictionary.UNKNOWN_MODIFIERS, /*alwaysPersist?*/persistClass(typ.resolveBinding()));
			}
		else {
			while (typ.isArrayType()) {
				typ = ((ArrayType)typ).getElementType();
			}

			if (typ.isPrimitiveType()) {
				return dico.ensureFamixPrimitiveType((ITypeBinding)null, findTypeName(typ));
			}
			else {
				return dico.ensureFamixType((ITypeBinding)null, findTypeName(typ), /*owner*/ctxt, ctxt, JavaDictionary.UNKNOWN_MODIFIERS, /*alwaysPersist?*/persistClass(typ.resolveBinding()));
			}
		}
	}

	/**
	 * Same as {@link VerveineVisitor#referedType(Type, ContainerEntity, boolean)} but with a type binding as first argument instead of a Type
	 */
	private fr.inria.verveine.core.gen.famix.Type referedType(ITypeBinding bnd, ContainerEntity ctxt, boolean isClass) {
		fr.inria.verveine.core.gen.famix.Type fmxTyp = null;

		if (bnd == null) {
			return null;
		}
		
		String name;
		if (bnd.isArray() ) {
			bnd = bnd.getElementType();
		}
		name  = bnd.getName();

		if (bnd.isParameterizedType()) {
			int i = name.indexOf('<');
			if (i > 0) {
				name = name.substring(0, i);
			}
			ITypeBinding parameterizableBnd = bnd.getErasure();
			int modifiers = (parameterizableBnd != null) ? parameterizableBnd.getModifiers() : JavaDictionary.UNKNOWN_MODIFIERS;
			ParameterizableClass generic = (ParameterizableClass) dico.ensureFamixClass(parameterizableBnd, name, /*owner*/null, /*isGeneric*/true, modifiers, /*alwaysPersist?*/persistClass(parameterizableBnd));
			if (bnd == parameterizableBnd) {
				// JDT bug?
				fmxTyp = dico.ensureFamixParameterizedType(null, name, generic, /*owner*/ctxt, persistClass(null));
			}
			else {
				fmxTyp = dico.ensureFamixParameterizedType(bnd, name, generic, /*owner*/ctxt, persistClass(bnd));
			}

			for (ITypeBinding targ : bnd.getTypeArguments()) {
				fr.inria.verveine.core.gen.famix.Type fmxTArg = this.referedType(targ, ctxt, false);
				if ( (fmxTArg != null) && persistClass(targ) ) {
					((fr.inria.verveine.core.gen.famix.ParameterizedType)fmxTyp).addArguments(fmxTArg);
				}
			}
		}
		else {
			fmxTyp = dico.ensureFamixType(bnd, name, /*owner*/null, ctxt, bnd.getModifiers(), /*alwaysPersist?*/persistClass(bnd));
		}

		return fmxTyp;
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
		if ( (bnd instanceof IVariableBinding) && (context.topMethod() != null) ) {
			// could be a variable, a field, an enumValue, ...
			BehaviouralEntity accessor = this.context.topMethod();
			ensureAccessedStructEntity((IVariableBinding)bnd, expr.getIdentifier(), /*typ*/null, /*owner*/null, accessor);
		}
	
	}
	
	/**
	 * Finds and/or create the Famix Entity receiving a message
	 * Can be: ImplicitVariable (this, super), GlobalVariable, LocalVariable, Attribute, UnknownVariable, Parameter
	 * @param expr -- the Java expression describing the receiver
	 * @return the Famix Entity or null if could not find it
	 */
	@SuppressWarnings("static-access")
	private NamedEntity getReceiver(Expression expr) {
		// msg(), same as ThisExpression
		if (expr == null) {
			return this.dico.ensureFamixImplicitVariable(dico.SELF_NAME, this.context.topType(), context.topMethod(), /*persistIt*/!classSummary);
		}

		// array[i].msg()
		else if (expr instanceof ArrayAccess) {
			return getReceiver(((ArrayAccess) expr).getArray());
		}

		// new type[].msg()
		else if (expr instanceof ArrayCreation) {
			//System.err.println("WARNING: Ignored receiver expression in method call: ArrayCreation");
			return null;
		}

		// (variable = value).msg()
		else if (expr instanceof Assignment) {
			return getReceiver(((Assignment) expr).getLeftHandSide());
		}

		// ((type)expr).msg()
		else if (expr instanceof CastExpression) {
			return getReceiver(((CastExpression) expr).getExpression());
		}

		// new Class().msg()
		else if (expr instanceof ClassInstanceCreation) {
			//System.err.println("WARNING: Ignored receiver expression in method call: ClassInstanceCreation");
			return null;
		}

		// (cond-expr ? then-expr : else-expr).msg()
		else if (expr instanceof ConditionalExpression) {
			// can be one or the other (then-expr/else-expr) so we choose one
			NamedEntity ret = getReceiver(((ConditionalExpression) expr).getThenExpression());
			if (ret == null) {
				// can as well try the other
				ret = getReceiver(((ConditionalExpression) expr).getElseExpression());
			}
			return ret;
		}

		// field.msg()
		else if (expr instanceof FieldAccess) {
			return ensureAccessedStructEntity(((FieldAccess) expr).resolveFieldBinding(), ((FieldAccess) expr).getName().getIdentifier(), /*type*/null, /*owner*/null, /*accessor*/null);
		}

		// (left-expr oper right-expr).msg()
		else if (expr instanceof InfixExpression) {
			// anonymous receiver
			return null;
		}

		// msg1().msg()
		else if (expr instanceof MethodInvocation) {
			//System.err.println("WARNING: Ignored receiver expression in method call: MethodInvocation");
			return null;
		}

		// name.msg()
		else if (expr instanceof Name) {
			// can be a class or a variable name
			IBinding bnd = ((Name) expr).resolveBinding();
			if (bnd == null) {
				return null;
			}
			NamedEntity ret = null;
			if (bnd instanceof ITypeBinding) {
				// msg() is a static method of Name so name should be a class, except if its an Enum
				ret = referedType((ITypeBinding)bnd, context.top(), ! ((ITypeBinding) bnd).isEnum());
			}
			else if (bnd instanceof IVariableBinding) {
				String varName = ( ((Name)expr).isSimpleName() ? ((SimpleName)expr).getFullyQualifiedName() : ((QualifiedName)expr).getName().getIdentifier());
				if ( ((IVariableBinding)bnd).isField() ) {
					return ensureAccessedStructEntity((IVariableBinding)bnd, varName, /*typ*/null, /*owner*/null, /*accessor*/null);
				}
				else if ( ((IVariableBinding)bnd).isParameter() ) {
					ret = dico.ensureFamixParameter( (IVariableBinding)bnd, varName, null, context.topMethod(), /*persistIt*/!classSummary);
				}
				else { // suppose it's a local variable
					ret = dico.ensureFamixLocalVariable( (IVariableBinding)bnd, varName, null, context.topMethod(), /*persistIt*/!classSummary);
				}
			}
			
			return ret;
		}

		// (expr).msg()
		else if (expr instanceof ParenthesizedExpression) {
			return getReceiver(((ParenthesizedExpression) expr).getExpression());
		}

		// "string".msg()
		else if (expr instanceof StringLiteral) {
			//System.err.println("WARNING: Ignored receiver expression in method call: StringLiteral");
			return null;
		}

		// super.field.msg()
		else if (expr instanceof SuperFieldAccess) {
			return ensureAccessedStructEntity(((SuperFieldAccess) expr).resolveFieldBinding(), ((SuperFieldAccess) expr).getName().getIdentifier(), /*typ*/null, /*owner*/null, /*accessor*/null);
		}

		// super.msg1().msg()
		else if (expr instanceof SuperMethodInvocation) {
			//System.err.println("WARNING: Ignored receiver expression in method call: SuperMethodInvocation");
			
			return null;
		}
		
		// this.msg()
		else if (expr instanceof ThisExpression) {
			return this.dico.ensureFamixImplicitVariable(dico.SELF_NAME, this.context.topType(), context.topMethod(), /*persistIt*/!classSummary);
		}

		// type.class.msg()
		else if (expr instanceof TypeLiteral) {
			// similar to a field access
			Attribute ret;
			fr.inria.verveine.core.gen.famix.Type javaMetaClass = dico.ensureFamixMetaClass(null); 
			ret =  dico.ensureFamixAttribute(null, "class", javaMetaClass, javaMetaClass, /*persistIt*/!classSummary);

			return ret;
		}

		// ... OTHER POSSIBLE EXPRESSIONS ?
		else  {
			System.err.println("WARNING: Unexpected receiver expression: "+expr.getClass().getName()+" (method called is" + expr.getClass().getName() + ".aMethod(...))");
		}

		return null;
	}
	
	/**
	 * Tries its best to find the type of a receiver without using the bindings.
	 * Most of the time, the type is that of the receiver, but not always (if there is a cast or if receiver is null)
	 * @param expr -- the Java expression describing the receiver
	 * @param receiver -- the FAMIX Entity describing the receiver
	 * @return the Famix Entity or null if could not find it
	 */
	private fr.inria.verveine.core.gen.famix.Type getInvokedMethodOwner(Expression expr, NamedEntity receiver) {
		// ((type)expr).msg()
		if (expr instanceof CastExpression) {
			Type tcast = ((CastExpression) expr).getType();
			return referedType(tcast, this.context.top(), true);
		}

		// new Class().msg()
		else if (expr instanceof ClassInstanceCreation) {
			return this.classInstanceCreated;
		}

		// msg1().msg()
		else if (expr instanceof MethodInvocation) {
			IMethodBinding callerBnd = ((MethodInvocation) expr).resolveMethodBinding();
			if (callerBnd != null) {
				return referedType(callerBnd.getReturnType(), this.context.top(), true);
			}
			else {
				return null;
			}
		}

		// (expr).msg()
		else if (expr instanceof ParenthesizedExpression) {
			return getInvokedMethodOwner(((ParenthesizedExpression) expr).getExpression(), receiver);
		}

		// "string".msg()
		else if (expr instanceof StringLiteral) {
			return dico.ensureFamixType(null, "String", dico.ensureFamixNamespaceJavaLang(null), /*alwaysPersist?*/true);  // creating FamixClass java.lang.String
		}

		// super.msg1().msg()
		else if (expr instanceof SuperMethodInvocation) {
			IMethodBinding superBnd = ((SuperMethodInvocation) expr).resolveMethodBinding();
			if (superBnd != null) {
				return  this.referedType(superBnd.getReturnType(), context.topType(),true);
			}
			else {
				return null;
			}
		}

		// everything else, see the receiver
		else {
			if (receiver == null) {
				return null;
			}
/*			else if (receiver instanceof ImplicitVariable) {
				if (receiver.getName().equals(JavaDictionary.SELF_NAME)) {
					return context.topType();
				}
				else { // receiver.getName().equals(JavaDictionary.SUPER_NAME)
					return context.topType().getSuperInheritances().iterator().next().getSuperclass();
				}
			}*/
			else if (receiver instanceof StructuralEntity) {
				return ((StructuralEntity)receiver).getDeclaredType();
			}
			else if (receiver instanceof fr.inria.verveine.core.gen.famix.Type)  {
				return (fr.inria.verveine.core.gen.famix.Type) receiver;
			}
			// ... what else ?
			else  {
				return null;
			}
		}
	}

	private StructuralEntity ensureAccessedStructEntity(IVariableBinding bnd, String name, fr.inria.verveine.core.gen.famix.Type typ, ContainerEntity owner, BehaviouralEntity accessor) {
		StructuralEntity accessed = null;

		if (bnd == null) {
			// no way to know if it should be an attribute, EnumValue, variable, ...
			return null;
		}
		else {
			bnd = bnd.getVariableDeclaration();
		}

		// could also test: "owner instanceof Enum" in case bnd == null
		if (bnd.isEnumConstant()) {
			accessed =  dico.ensureFamixEnumValue(bnd, name, (Enum) owner, /*persistIt*/!classSummary);
		}
		else if (bnd.isField()) {
			accessed =  dico.ensureFamixAttribute(bnd, name, typ, (fr.inria.verveine.core.gen.famix.Type) owner, /*persistIt*/!classSummary);
			if (classSummary) {
				if (! (accessed.getDeclaredType() instanceof PrimitiveType)) {
					dico.addFamixReference(findHighestType(accessed.getBelongsTo()), findHighestType(accessed.getDeclaredType()), null);
				}
			}

			if ( (accessed != null) && (((Attribute) accessed).getParentType() == null) && (accessed.getName().equals("length")) ) {
				// special case: length attribute of arrays in Java
				((Attribute) accessed).setParentType(dico.ensureFamixClassArray());
			}
		}
		else if (bnd.isParameter()) {
			if (! classSummary) {
				accessed =  dico.ensureFamixParameter(bnd, name, typ, (Method) owner, /*persistIt*/!classSummary);
			}
		}
		else {
			// it seems it is a variable.
			// if it is not already defined, we assume we are not interested
			accessed =  (StructuralEntity) dico.getEntityByKey(bnd);
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
		if ( (accessed != null) && (accessor != null)) {
			if (classSummary) {
				dico.addFamixReference(findHighestType(accessor), findHighestType(accessed), null);
			}
			else if (accessed.getBelongsTo() != accessor) {
				context.setLastAccess( dico.addFamixAccess(accessor, accessed, /*isWrite*/isLHS, context.getLastAccess()) );
			}
		}
	}

	private String findTypeName(org.eclipse.jdt.core.dom.Type t) {
		if (t == null) {
			return null;
		}
		
		if (t.isPrimitiveType()) {
			return t.toString();
		}
		else if (t.isSimpleType()) {
			return ((SimpleType)t).getName().getFullyQualifiedName();
		}
		else if (t.isQualifiedType()) {
			return ((QualifiedType)t).getName().getIdentifier();
		}
		else if (t.isArrayType()) {
			return findTypeName( ((ArrayType)t).getElementType() );
		}
		else if (t.isParameterizedType()) {
			return findTypeName(((org.eclipse.jdt.core.dom.ParameterizedType)t).getType());
		}
		else { // it is a WildCardType
			if ( ((org.eclipse.jdt.core.dom.WildcardType)t).isUpperBound() ) {
				return findTypeName( ((org.eclipse.jdt.core.dom.WildcardType)t).getBound() );
			}
			else {
				return JavaDictionary.OBJECT_NAME;
			}
		}
	}

	/**
	 * if {@link VerveineVisitor#classSummary} is true, we persist only classes that are not defined in methods.
	 * @param bnd -- ITypeBinding for the class that we are checking, might be null and in this case, we check whether there is no method at the top of the context
	 * @return whether to persist the class or its members
	 */
	private boolean persistClass(ITypeBinding bnd) {
		if (bnd != null) {
			if (bnd.isParameterizedType()) {
				// parameterized types seem to never belong to a method even when they are created within one
				// so we kind of "force" persistClass to consider only context by passing a null binding to it
				return persistClass(null);
			}
			else {
				// let see if it is a type parameter
				NamedEntity t = dico.getEntityByKey(bnd);
				if ( (t != null) && (t instanceof ParameterType) ) {
					return false; 
				}
				// finally, the "normal" case
				return (! classSummary) || (bnd.getDeclaringMethod() == null);
			}
		}
		else {
			return (! classSummary) || (context.topMethod() == null);
		}
		
	}

	/**
	 *  find highest level type containing entity
	 * @param e
	 * @return
	 */
	private fr.inria.verveine.core.gen.famix.Type findHighestType (NamedEntity e) {
		fr.inria.verveine.core.gen.famix.Type ret = null;

		while (! (e instanceof Namespace) ) {
			if (e instanceof ParameterizedType) {
				e = ((ParameterizedType)e).getParameterizableClass();
			}
			else if (e instanceof fr.inria.verveine.core.gen.famix.Type) {
				ret = (fr.inria.verveine.core.gen.famix.Type)e;
			}
			e = e.getBelongsTo();
		}			
		return ret;
	}

}