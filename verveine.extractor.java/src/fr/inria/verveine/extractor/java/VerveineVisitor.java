package fr.inria.verveine.extractor.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
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
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
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
import fr.inria.verveine.core.gen.famix.AnnotationType;
import fr.inria.verveine.core.gen.famix.AnnotationTypeAttribute;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.BehaviouralEntity;
import fr.inria.verveine.core.gen.famix.Class;
import fr.inria.verveine.core.gen.famix.ContainerEntity;
import fr.inria.verveine.core.gen.famix.EnumValue;
import fr.inria.verveine.core.gen.famix.ImplicitVariable;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.NamedEntity;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.ParameterType;
import fr.inria.verveine.core.gen.famix.ParameterizableClass;
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
	 * A stack that keeps the current definition context (package/class/method)
	 */
	protected EntityStack context;

	public VerveineVisitor(JavaDictionary dico) {
		this.dico = dico;
		this.context = new EntityStack();
	}

	// VISITOR METHODS

	public boolean visit(CompilationUnit node) {
//		System.err.println("TRACE, DefVisiting CompilationUnit: "+node.getProperty(dico.SOURCE_FILENAME_PROPERTY));
		Namespace fmx = null;
		PackageDeclaration pckg = node.getPackage();
		if (pckg==null) {
			fmx = dico.ensureFamixNamespaceDefault();
		}
		else {
			fmx = dico.ensureFamixNamespace(pckg.resolveBinding(), pckg.getName().getFullyQualifiedName());
			fmx.setIsStub(false);
		}

		if (pckg != null) {
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	public void endVisit(CompilationUnit node) {
		this.context.popPckg();
		super.endVisit(node);
	}

	/**
	 * creating reference from package of this compilation unit to imported package
	 * not sure it is a good idea ?!?
	 */
	public boolean visit(ImportDeclaration node) {

		Namespace fmxSrc = this.context.topPckg();  // could access it through recursive node.getParent() ?

		IBinding importBnd = node.resolveBinding();
		String importName = node.getName().getFullyQualifiedName();
		if (importBnd instanceof IMethodBinding)  {
			importBnd = ((IMethodBinding)importBnd).getDeclaringClass().getPackage();
			importName = dico.removeLastName(importName);
			importName = dico.removeLastName(importName);
		}
		else if (importBnd instanceof IVariableBinding)  {
			importBnd = ((IVariableBinding)importBnd).getDeclaringClass().getPackage();
			importName = dico.removeLastName(importName);
			importName = dico.removeLastName(importName);
		}
		else if (importBnd instanceof ITypeBinding)  {
			importBnd = ((ITypeBinding)importBnd).getPackage();
			importName = dico.removeLastName(importName);
		}
		Namespace fmxDest = dico.ensureFamixNamespace( (IPackageBinding)importBnd, importName);
		context.setLastReference( dico.addFamixReference(fmxSrc, fmxDest, context.getLastReference()) );
		
		return super.visit(node);
	}

	/*
	 * Can only be a class or interface declaration
	 */
	public boolean visit(TypeDeclaration node) {
//		System.err.println("TRACE, DefVisiting TypeDeclaration: "+node.getName().getIdentifier());
		ITypeBinding bnd = node.resolveBinding();
		@SuppressWarnings("unchecked")
		List<TypeParameter> tparams = (List<TypeParameter>)node.typeParameters();
		fr.inria.verveine.core.gen.famix.Class fmx = dico.ensureFamixClass(bnd, /*name*/node.getName().getIdentifier(), /*owner*/context.top(), tparams.size()>0);
		if (fmx != null) {
			fmx.setIsStub(false);

			this.context.pushClass(fmx);

			dico.addSourceAnchor(fmx, node);
			dico.addFamixAnnotationInstances(bnd, fmx);
			dico.createFamixComment(node.getJavadoc(), fmx);
			
			for (TypeParameter tp : tparams) {
				// if there is a type parameter, then fmx will be a Famix ParameterizableClass
				ParameterType fmxParam = dico.ensureFamixParameterType( tp.resolveBinding(), tp.getName().getIdentifier(), (ParameterizableClass)fmx); // note: owner of the ParameterType is the ParameterizableClass
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

	public void endVisit(TypeDeclaration node) {
		this.context.popClass();
		super.endVisit(node);
	}

	public boolean visit(ClassInstanceCreation node) {
//		System.err.println("TRACE, DefVisiting ClassInstanceCreation");
		fr.inria.verveine.core.gen.famix.Class fmx = null;
		AnonymousClassDeclaration decl = node.getAnonymousClassDeclaration(); 
		if (decl != null) {
			ITypeBinding bnd = decl.resolveBinding();
			fmx = (Class) this.dico.ensureFamixType(bnd, /*name*/"anonymous("+dico.findTypeName(node.getType())+")", /*owner*/context.top(), /*ctxt*/context.top());  //  isGeneric = false
			if (fmx != null) {
				fmx.setIsStub(false);

				dico.addFamixAnnotationInstances(bnd, fmx);
				dico.addSourceAnchor(fmx, node);
				this.context.pushClass(fmx);
				return super.visit(node);
			}
			else {
				return false;
			}
		}
		return super.visit(node);
	}

	/* 
	 * intended to close 'visit(ClassInstanceCreation node)' for just this specific case
	 */
	public void endVisit(AnonymousClassDeclaration node) {
		this.context.popClass();
		super.endVisit(node);
	}

	public boolean visit(AnnotationTypeDeclaration node) {
//		System.err.println("TRACE, DefVisiting AnnotationTypeDeclaration: "+node.getName().getIdentifier());
		ITypeBinding bnd = node.resolveBinding();
		AnnotationType fmx = dico.ensureFamixAnnotationType(bnd, node.getName().getIdentifier(), context.top());
		if (fmx != null) {
			fmx.setIsStub(Boolean.FALSE);
			
			context.pushAnnotationType(fmx);
			return super.visit(node);
		}
		else {
			context.pushAnnotationType(null);
			return false;
		}
	}

	public void endVisit(AnnotationTypeDeclaration node) {
		this.context.popAnnotationType();
		super.endVisit(node);
	}

	public boolean visit(AnnotationTypeMemberDeclaration node) {
//		System.err.println("TRACE, DefVisiting AnnotationTypeMemberDeclaration: "+node.getName().getIdentifier());
		IMethodBinding bnd = node.resolveBinding();
		AnnotationTypeAttribute fmx = dico.ensureFamixAnnotationTypeAttribute(bnd, node.getName().getIdentifier(), context.topAnnotationType());
		if (fmx != null) {
			fmx.setIsStub(false);
			
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

	@SuppressWarnings("unchecked")
	public boolean visit(EnumDeclaration node) {
//		System.err.println("TRACE, DefVisiting EnumDeclaration: "+node.getName().getIdentifier());
		fr.inria.verveine.core.gen.famix.Enum fmx = dico.ensureFamixEnum(node.resolveBinding(), node.getName().getIdentifier(), context.top());
		if (fmx != null) {
			fmx.setIsStub(Boolean.FALSE);
			
			for (EnumConstantDeclaration ecst : (List<EnumConstantDeclaration>)node.enumConstants()) {
				EnumValue ev = dico.ensureFamixEnumValue(ecst.resolveVariable(), ecst.getName().getIdentifier(), fmx);
				ev.setIsStub(Boolean.FALSE);
			}
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean visit(MethodDeclaration node) {
//		System.err.println("TRACE, DefVisiting MethodDeclaration: "+node.getName().getIdentifier());
		
		// some info needed to create the Famix Method
		IMethodBinding bnd = node.resolveBinding();

		Collection<Type> paramTypes = new ArrayList<Type>();
		for (SingleVariableDeclaration param : (List<SingleVariableDeclaration>)node.parameters()) {
				paramTypes.add(param.getType());
		}

		fr.inria.verveine.core.gen.famix.Type fmxRetTyp = null;
		if (! node.isConstructor()) {
			// creates the method with a fake return type because we might need this FamixMethod to create the return type (if it is a ParameterizedType)
			// we reset the return type to its proper value later
			fmxRetTyp = dico.ensureFamixClassObject(null);
		}
		Method fmx = dico.ensureFamixMethod(bnd, node.getName().getIdentifier(), paramTypes, /*retType*/fmxRetTyp, context.topClass());

		if (fmx != null) {
			fmx.setIsStub(false);
			
			// now will recompute the actual returnType
			this.context.pushMethod(fmx);
			if (! node.isConstructor()) {
				fmxRetTyp = referedType(node.getReturnType2(), fmx);
				fmx.setDeclaredType(fmxRetTyp);
			}

			dico.addSourceAnchor(fmx, node);
			dico.addFamixAnnotationInstances(bnd, fmx);
			dico.createFamixComment(node.getJavadoc(), fmx);

			if (node.getBody() != null) {
				context.setTopMethodCyclo(1);
			}

			// creating the method's parameters
			List<VariableDeclaration> paramAsVarList;
			for (SingleVariableDeclaration param : (List<SingleVariableDeclaration>)node.parameters()) {
				// Note: method and ParamTyp bindings are null for ParameterType :-(
				paramAsVarList = new ArrayList<VariableDeclaration>(1);
				paramAsVarList.add(param);
				fr.inria.verveine.core.gen.famix.Type varTyp = referedType(param.getType(), context.topMethod());
				visitVariablesDeclarations(node, varTyp, paramAsVarList, context.topMethod());
			}

			// Exceptions
			for (Name excepName : (List<Name>)node.thrownExceptions()) {
				fr.inria.verveine.core.gen.famix.Class excepFmx = this.dico.ensureFamixClass(excepName.resolveTypeBinding(), excepName.getFullyQualifiedName(), /*owner*/null, /*isGeneric*/false);
				if (excepFmx != null) {
					dico.createFamixDeclaredException(fmx, excepFmx);
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
		int cyclo = 0;
		int nos = 0;
		if (context.topMethod() != null) {
			cyclo = context.getTopMethodCyclo();
			nos = context.getTopMethodNOS();
		}
		Method fmx = this.context.popMethod();
		if (fmx != null) {
			fmx.setNOS(nos);
			fmx.setCyclo(cyclo);
		}
		super.endVisit(node);
	}

	@SuppressWarnings({ "unchecked" })
	public boolean visit(FieldDeclaration node) {
//		System.err.println("TRACE, DefVisiting FieldDeclaration");

		fr.inria.verveine.core.gen.famix.Type varTyp = referedType(node.getType(), context.topClass());
		for (StructuralEntity att : visitVariablesDeclarations(node, varTyp, (List<VariableDeclaration>)node.fragments(), context.topClass()) ) {
			dico.addSourceAnchor(att, node);
			dico.createFamixComment(node.getJavadoc(), att);
		}
		return super.visit(node);
	}

	@SuppressWarnings("unchecked")
	public boolean visit(VariableDeclarationExpression node) {
//		System.err.println("TRACE, DefVisiting VariableDeclarationExpression");

		// we don't declare (local) variables that have a primitive type
		// because we are assuming that the user is not interested in them 
		if (! node.getType().isPrimitiveType()) {
			fr.inria.verveine.core.gen.famix.Type varTyp = referedType(node.getType(), context.topMethod());
			for (StructuralEntity att : visitVariablesDeclarations(node, varTyp, (List<VariableDeclaration>)node.fragments(), context.topMethod())) {
				dico.addSourceAnchor(att, node);
			}
		}

		return super.visit(node);
	}

	@SuppressWarnings("unchecked")
	public boolean visit(VariableDeclarationStatement node) {
//		System.err.println("TRACE, DefVisiting VariableDeclarationStatement");

		// we don't declare (local) variables that have a primitive type
		// because we are assuming that the user is not interested in them 
		if (! node.getType().isPrimitiveType()) {
			fr.inria.verveine.core.gen.famix.Type varTyp = referedType(node.getType(), context.topMethod());
			for (StructuralEntity att : visitVariablesDeclarations(node, varTyp, (List<VariableDeclaration>)node.fragments(), context.topMethod())) {
				dico.addSourceAnchor(att, node);
			}
		}

		return super.visit(node);
	}

	private Collection<StructuralEntity> visitVariablesDeclarations(ASTNode node, fr.inria.verveine.core.gen.famix.Type varTyp, List<VariableDeclaration> fragments, ContainerEntity ctxt) {
		Collection<StructuralEntity> ret = new ArrayList<StructuralEntity>();

		for (VariableDeclaration vd : fragments) {
			StructuralEntity fmx;
			IVariableBinding bnd = vd.resolveBinding();
			String name = vd.getName().getIdentifier();

			if (node instanceof MethodDeclaration) {
				// creating the parameters of a method. In this case, 'fragment' is aList<SingleVariableDeclarationFragment> and 'varType' is null
				fmx = dico.ensureFamixParameter(bnd, name, varTyp, (Method)ctxt);
			}
			else if (node instanceof FieldDeclaration) {
				// creating a class' field
				fmx = dico.ensureFamixAttribute(bnd, name, varTyp, (fr.inria.verveine.core.gen.famix.Class) ctxt);
			}
			else if (node instanceof VariableDeclarationExpression) {
				// creating a method's local variable
				fmx = dico.ensureFamixLocalVariable(bnd, name, varTyp, (Method) ctxt);
			}
			else if (node instanceof VariableDeclarationExpression) {
				// creating a method's local variable
				fmx = dico.ensureFamixLocalVariable(bnd, name, varTyp, (Method) ctxt);
			}
			else {
				fmx = null;
			}

			if (fmx != null) {
				fmx.setIsStub(false);
				dico.addFamixAnnotationInstances(bnd, fmx);
				ret.add(fmx);
			}
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	private fr.inria.verveine.core.gen.famix.Type referedType(Type typ, ContainerEntity ctxt) {
		fr.inria.verveine.core.gen.famix.Type fmxTyp = null;

		if (typ == null) {
			return null;
		}

		if (typ.isParameterizedType()) {
			ITypeBinding parameterizedBnd = typ.resolveBinding();
			ITypeBinding parameterizableBnd = (parameterizedBnd == null) ? null : parameterizedBnd.getErasure();
			String tname = dico.findTypeName(typ);
			ParameterizableClass generic = null;
			generic = (ParameterizableClass) dico.ensureFamixClass(parameterizableBnd, tname, /*owner*/null, /*isGeneric*/true);

			fmxTyp = dico.ensureFamixParameterizedType(parameterizedBnd, tname, generic, /*owner*/ctxt);
			for (Type targ : (List<Type>) ((ParameterizedType)typ).typeArguments()) {
				fr.inria.verveine.core.gen.famix.Type fmxTArg = dico.ensureFamixType(targ.resolveBinding(), dico.findTypeName(targ), /*owner*/null, ctxt);
				((fr.inria.verveine.core.gen.famix.ParameterizedType)fmxTyp).addArguments(fmxTArg);
			}
		}
		else if ( typ.isSimpleType() && (typ.resolveBinding()==null) && (ctxt instanceof Method)) {
			// might be a wildcardType with no previous "definition"
			// in this case the owner is this very method
			fmxTyp = dico.ensureFamixType((ITypeBinding)null, dico.findTypeName(typ), /*owner*/ctxt, ctxt);
		}
		else {
			fmxTyp = dico.ensureFamixType(typ.resolveBinding(), dico.findTypeName(typ), /*owner*/null, ctxt);
		}
		return fmxTyp;
	}

	public boolean visit(MethodInvocation node) {
		Expression callingExpr = node.getExpression();
		methodInvocation(node.resolveMethodBinding(), node.getName().getFullyQualifiedName(), getReceiver(callingExpr));
		if (callingExpr instanceof SimpleName) {
			// we might have a hidden FieldAccess here
			IBinding bnd = ((Name) callingExpr).resolveBinding();
			if ( (bnd != null) && (bnd instanceof IVariableBinding) && ((IVariableBinding)bnd).isField() ){
				BehaviouralEntity accessor = this.context.topMethod();
				// note: using a field without anything before, owner must be the currently parsed class
				createAccessedAttribute((IVariableBinding)bnd, ((SimpleName)callingExpr).getIdentifier(), null, /*owner*/context.topClass(), accessor);
			}
		}

		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(SuperMethodInvocation node) {
		methodInvocation(node.resolveMethodBinding(), node.getName().getFullyQualifiedName(), this.dico.ensureFamixImplicitVariable(Dictionary.SUPER_NAME, this.context.topClass(), context.top()));

		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(ConstructorInvocation node) {
		this.context.addTopMethodNOS(1);
		
		// ConstructorInvocation (i.e. 'this(...)' ) happen in constructor, so the name is the same
		String name = context.topMethod().getName();
		Method invoked = this.dico.ensureFamixMethod(node.resolveConstructorBinding(), name, (Collection<org.eclipse.jdt.core.dom.Type>)null, /*retType*/null, /*owner*/context.topClass());  // cast needed to desambiguate the call
		ImplicitVariable receiver = dico.ensureFamixImplicitVariable(Dictionary.SELF_NAME, context.topClass(), context.topMethod());
		context.setLastInvocation( dico.addFamixInvocation(context.topMethod(), invoked, receiver, context.getLastInvocation()) );

		return super.visit(node);
	}

	public boolean visit(SuperConstructorInvocation node) {
		this.context.addTopMethodNOS(1);
		
		// ConstructorInvocation (i.e. 'super(...)' ) happen in constructor, so the name is that of the superclass
		Method invoked = this.dico.ensureFamixMethod(node.resolveConstructorBinding(), null, (Collection<org.eclipse.jdt.core.dom.Type>)null, /*retType*/null, /*owner*/context.topClass());  // cast needed to desambiguate the call
		ImplicitVariable receiver = dico.ensureFamixImplicitVariable(Dictionary.SUPER_NAME, context.topClass(), context.topMethod());
		context.setLastInvocation( dico.addFamixInvocation(context.topMethod(), invoked, receiver, context.getLastInvocation()) );

		return super.visit(node);
	}

	/**
	 * Handles an invocation of a method by creating the corresponding Famix Entity
	 * @param calledBnd -- a binding for the method
	 * @param calledName of the method invoked
	 * @param receiver of the call, i.e. the object to which the message is sent
	 */
	private void methodInvocation(IMethodBinding calledBnd, String calledName, NamedEntity receiver) {
		BehaviouralEntity sender = this.context.topMethod();
		if (sender != null) {
			Method invoked = null;
			if (receiver instanceof StructuralEntity) {
				fr.inria.verveine.core.gen.famix.Type varTyp = ((StructuralEntity)receiver).getDeclaredType();
				invoked = this.dico.ensureFamixMethod(calledBnd, calledName, (Collection<org.eclipse.jdt.core.dom.Type>)null, /*retType*/null, /*owner*/varTyp);  // cast needed to desambiguate the call
			}
			else {
				// method static of a class called on the class
				invoked = this.dico.ensureFamixMethod(calledBnd, calledName, (Collection<org.eclipse.jdt.core.dom.Type>)null, /*retType*/null, /*owner*/(fr.inria.verveine.core.gen.famix.Type)receiver);  // cast needed to desambiguate the call
			}
			context.setLastInvocation( dico.addFamixInvocation(sender, invoked, receiver, context.getLastInvocation()) );
		}
	}

	public boolean visit(FieldAccess node) {
		BehaviouralEntity accessor = this.context.topMethod();
		IVariableBinding bnd = node.resolveFieldBinding();
		// FIXME if bnd == null we have a problem
		createAccessedAttribute(bnd, node.getName().getIdentifier(), null, null, accessor);

		return super.visit(node);
	}

	/*
	 * Could be a FieldAccess (see JDT javadoc: class FieldAccess) 
	 */
	public boolean visit(QualifiedName node) {
		IBinding bnd = node.resolveBinding();
		if (bnd instanceof IVariableBinding) {
			// apparently this is a field
			BehaviouralEntity accessor = this.context.topMethod();
			createAccessedAttribute((IVariableBinding)bnd, node.getName().getIdentifier(), null, null, accessor);
		}
		return super.visit(node);
	}

	/* 
	 * Another FieldAccess in disguise: SomeClass.class
	 */
	public boolean visit(TypeLiteral node) {
		fr.inria.verveine.core.gen.famix.Type javaMetaClass = dico.ensureFamixMetaClass(null); 
		BehaviouralEntity accessor = this.context.topMethod();
		createAccessedAttribute(null, "class", javaMetaClass, javaMetaClass, accessor);
		return super.visit(node);
	}

	@Override
	public boolean visit(CatchClause node) {
		Method meth = this.context.topMethod();
		Type excepClass = node.getException().getType();
		if (meth != null) {
			fr.inria.verveine.core.gen.famix.Class excepFmx = null;
			if (excepClass instanceof SimpleType) {
				excepFmx = this.dico.ensureFamixClass(excepClass.resolveBinding(), ((SimpleType) excepClass).getName().getFullyQualifiedName(), /*owner*/null, /*isGeneric*/false);
			}
			else if (excepClass instanceof QualifiedType) {
				excepFmx = this.dico.ensureFamixClass(excepClass.resolveBinding(), ((QualifiedType) excepClass).getName().getIdentifier(), /*owner*/null, /*isGeneric*/false);
			}
			if (excepFmx != null) {
				dico.createFamixCaughtException(meth, excepFmx);
			}
		}

		return super.visit(node);
	}

	@Override
	public boolean visit(ThrowStatement node) {
		this.context.addTopMethodNOS(1);

		Method meth = this.context.topMethod();
		fr.inria.verveine.core.gen.famix.Class excepFmx = this.dico.ensureFamixClass(node.getExpression().resolveTypeBinding(), /*name*/(String)null, /*owner*/(ContainerEntity)null, /*isGeneric*/false);
		if (excepFmx != null) {
			dico.createFamixThrownException(meth, excepFmx);
		}
		return super.visit(node);
	}

	public boolean visit(AssertStatement node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(Assignment node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(ContinueStatement node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(DoStatement node) {
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(EnhancedForStatement node) {
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(ForStatement node) {
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(IfStatement node) {
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(ReturnStatement node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(SwitchCase node) {
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(SwitchStatement node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(SynchronizedStatement node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(TryStatement node) {
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(WhileStatement node) {
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
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
			return this.dico.ensureFamixImplicitVariable(dico.SELF_NAME, this.context.topClass(), context.top());
		}

		// array[i].msg()
		else if (expr instanceof ArrayAccess) {
			return getReceiver(((ArrayAccess) expr).getArray());
		}

		// new type[].msg() -- TODO similar to ClassInstanceCreation
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

		// new Class().msg() -- TODO anonymous object of a known class ...
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
			Attribute ret = createAccessedAttribute(((FieldAccess) expr).resolveFieldBinding(), ((FieldAccess) expr).getName().getIdentifier(), /*type*/null, /*owner*/null, /*accessor*/null);
			if ( (ret != null) && (ret.getParentType() == null) && (ret.getName().equals("length")) ) {
				ret.setParentType(dico.ensureFamixClassArray());
			}

			return ret;
		}

		// (left-expr oper right-expr).msg()
		else if (expr instanceof InfixExpression) {
			// anonymous receiver
			return null;
		}

		// msg1().msg() -- TODO similar to ClassInstanceCreation, 'msg()' is sent to the object returned by 'msg1()'
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
				// msg() is a static method of Name
				//TODO why returning a variable here? Should not it be the class itself?
				ret = dico.createFamixUnknownVariable( dico.ensureFamixType((ITypeBinding)bnd, null, null, context.top()), bnd.getName());
			}
			else if (bnd instanceof IVariableBinding) {
				String varName = ( ((Name)expr).isSimpleName() ? ((SimpleName)expr).getFullyQualifiedName() : ((QualifiedName)expr).getName().getIdentifier());
				if ( ((IVariableBinding)bnd).isField() ) {
					ret = createAccessedAttribute((IVariableBinding)bnd, varName, /*typ*/null, /*owner*/null, /*accessor*/null);
					if ( (ret != null) && (((Attribute) ret).getParentType() == null) && (ret.getName().equals("length")) ) {
						((Attribute) ret).setParentType(dico.ensureFamixClassArray());
					}

					return ret;
				}
				else if ( ((IVariableBinding)bnd).isParameter() ) {
					ret = dico.ensureFamixParameter( (IVariableBinding)bnd, varName, null, context.topMethod());
				}
				else { // suppose it's a local variable
					ret = dico.ensureFamixLocalVariable( (IVariableBinding)bnd, varName, null, context.topMethod());
				}
			}
			
			return ret;
		}

		// (expr).msg()
		else if (expr instanceof ParenthesizedExpression) {
			return getReceiver(((ParenthesizedExpression) expr).getExpression());
		}

		// "string".msg() -- TODO similar to ClassInstanceCreation, anonymous String object
		else if (expr instanceof StringLiteral) {
			//System.err.println("WARNING: Ignored receiver expression in method call: StringLiteral");
			return null;
		}

		// super.field.msg()
		else if (expr instanceof SuperFieldAccess) {
			Attribute ret = createAccessedAttribute(((SuperFieldAccess) expr).resolveFieldBinding(), ((SuperFieldAccess) expr).getName().getIdentifier(), /*typ*/null, /*owner*/null, /*accessor*/null);
			if ( (ret != null) && (ret.getParentType() == null) && (ret.getName().equals("length")) ) {
				ret.setParentType(dico.ensureFamixClassArray());
			}

			return ret;
		}

		// super.msg1().msg() -- TODO similar to ClassInstanceCreation, 'msg()' is sent to the object returned by 'msg1()'
		else if (expr instanceof SuperMethodInvocation) {
			//System.err.println("WARNING: Ignored receiver expression in method call: SuperMethodInvocation");
			
			return null;
		}
		
		// this.msg()
		else if (expr instanceof ThisExpression) {
			return this.dico.ensureFamixImplicitVariable(dico.SELF_NAME, this.context.topClass(), context.top());
		}

		// type.class.msg()
		else if (expr instanceof TypeLiteral) {
			// similar to a field access
			Attribute ret;
			fr.inria.verveine.core.gen.famix.Type javaMetaClass = dico.ensureFamixMetaClass(null); 
			ret =  dico.ensureFamixAttribute(null, "class", javaMetaClass, javaMetaClass);

			return ret;
		}

		// ... OTHER POSSIBLE EXPRESSIONS ?
		else  {
			System.err.println("WARNING: Unexpected receiver expression: "+expr.getClass().getName()+" (method called is" + expr.getClass().getName() + ".aMethod(...))");
		}

		return null;
	}

	private Attribute createAccessedAttribute(IVariableBinding bnd, String attName, fr.inria.verveine.core.gen.famix.Type typ, fr.inria.verveine.core.gen.famix.Type owner, BehaviouralEntity accessor) {
		Attribute accessed;
		if (bnd != null) {
			bnd = bnd.getVariableDeclaration();
		}
		accessed =  dico.ensureFamixAttribute(bnd, attName, typ, owner);
		if ( (accessed != null) && (accessed.getParentType() == null) && (accessed.getName().equals("length")) ) {
			accessed.setParentType(dico.ensureFamixClassArray());
		}
		
		if ( (accessed != null) && (accessor != null) ) {
			context.setLastAccess( dico.addFamixAccess(accessor, accessed, /*isWrite*/false, context.getLastAccess()) );
		}
		return accessed;
	}

}