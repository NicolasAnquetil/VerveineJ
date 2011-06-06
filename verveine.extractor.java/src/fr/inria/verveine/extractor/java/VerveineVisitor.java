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
import org.eclipse.jdt.core.dom.ArrayType;
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
import org.eclipse.jdt.core.dom.Initializer;
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
import fr.inria.verveine.core.gen.famix.Enum;
import fr.inria.verveine.core.gen.famix.EnumValue;
import fr.inria.verveine.core.gen.famix.ImplicitVariable;
import fr.inria.verveine.core.gen.famix.Invocation;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.NamedEntity;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.ParameterType;
import fr.inria.verveine.core.gen.famix.ParameterizableClass;
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
	 * A stack that keeps the current definition context (package/class/method)
	 */
	protected EntityStack context;

	public VerveineVisitor(JavaDictionary dico) {
		this.dico = dico;
		this.context = new EntityStack();
	}

	// VISITOR METHODS

	public boolean visit(CompilationUnit node) {
//		System.out.println("TRACE, Visiting CompilationUnit: "+node.getProperty(JavaDictionary.SOURCE_FILENAME_PROPERTY));
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
		super.endVisit(node);
	}

	public boolean visit(PackageDeclaration node) {
		return false; // no need to visit children of the declaration
	}

	/**
	 * creating reference from package of this compilation unit to imported package
	 * not sure it is a good idea ?!?
	 */
	public boolean visit(ImportDeclaration node) {

		Namespace fmxSrc = this.context.topPckg();  // could access it through recursive node.getParent() ?

		IBinding importBnd = node.resolveBinding();
		String importName = node.getName().getFullyQualifiedName();
		importName = dico.removeLastName(importName);  // remove Class name or '*'

		if (importBnd == null) {
			if (node.isStatic()) {
				importName = dico.removeLastName(importName);  // remove Class name or '*'
			}
		}
		else if (importBnd instanceof IMethodBinding)  {
			importBnd = ((IMethodBinding)importBnd).getDeclaringClass().getPackage();
			importName = dico.removeLastName(importName);
		}
		else if (importBnd instanceof IVariableBinding)  {
			importBnd = ((IVariableBinding)importBnd).getDeclaringClass().getPackage();
			importName = dico.removeLastName(importName);
		}
		else if (importBnd instanceof ITypeBinding)  {
			importBnd = ((ITypeBinding)importBnd).getPackage();
		}
		Namespace fmxDest = dico.ensureFamixNamespace( (IPackageBinding)importBnd, importName);
		context.setLastReference( dico.addFamixReference(fmxSrc, fmxDest, context.getLastReference()) );
		
		return false; // don't visit children
	}

	/*
	 * Can only be a class or interface declaration
	 */
	public boolean visit(TypeDeclaration node) {
//		System.out.println("TRACE, Visiting TypeDeclaration: "+node.getName().getIdentifier());
		ITypeBinding bnd = node.resolveBinding();
		@SuppressWarnings("unchecked")
		List<TypeParameter> tparams = (List<TypeParameter>)node.typeParameters();
		fr.inria.verveine.core.gen.famix.Class fmx = dico.ensureFamixClass(bnd, /*name*/node.getName().getIdentifier(), /*owner*/context.top(), tparams.size()>0);
		if (fmx != null) {
			fmx.setIsStub(false);

			this.context.pushType(fmx);

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
		this.context.popType();
		super.endVisit(node);
	}

	/**
	 * The super type of an anonymous declaration is only available (without resorting to bindings) when 
	 * we are in its parent node: a ClassInstanceCreation.
	 * So we must keep this type from the visit(ClassInstanceCreation) to be used in visit(AnonymousClassDeclaration).<br>
	 * Note that in some special cases one can also have an anonymous class definition without specifying its superclass.
	 */
	private Type anonymousSuperType;

	/**
	 * See {@link VerveineVisitor#anonymousSuperType}
	 */
	@SuppressWarnings("unchecked")
	public boolean visit(ClassInstanceCreation node) {
		//System.err.println("TRACE, Visiting ClassInstanceCreation");
		if (node.getAnonymousClassDeclaration() != null) {
			anonymousSuperType = node.getType();
		}
		else {
			anonymousSuperType = null;

			// treat the expression 'new A(...)' by creating a Reference to 'A'
			fr.inria.verveine.core.gen.famix.Type fmx = null;
			Type clazz = node.getType();
			fmx = referedType(clazz, context.top());
			Reference lastRef = context.getLastReference();
			dico.addFamixReference(context.top(), fmx, lastRef);
			
			// create an invocation of the constructor
			methodInvocation(node.resolveConstructorBinding(), findTypeName(clazz), /*receiver*/null, node.arguments());
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
		fmx = this.dico.ensureFamixClass(bnd, /*name*/"anonymous("+anonSuperTypeName+")", /*owner*/context.top(), /*isGeneric*/false);
		if (fmx != null) {
			fmx.setIsStub(false);

			dico.addFamixAnnotationInstances(bnd, fmx);
			dico.addSourceAnchor(fmx, node);
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

	public void endVisit(EnumDeclaration node) {
		this.context.popType();
		super.endVisit(node);
	}

	public boolean visit(AnnotationTypeDeclaration node) {
//		System.err.println("TRACE, Visiting AnnotationTypeDeclaration: "+node.getName().getIdentifier());
		ITypeBinding bnd = node.resolveBinding();
		AnnotationType fmx = dico.ensureFamixAnnotationType(bnd, node.getName().getIdentifier(), context.top());
		if (fmx != null) {
			fmx.setIsStub(Boolean.FALSE);
			
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
		AnnotationTypeAttribute fmx = dico.ensureFamixAnnotationTypeAttribute(bnd, node.getName().getIdentifier(), (AnnotationType) context.topType());
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
	public boolean visit(MethodDeclaration node) {
//		System.out.println("TRACE, Visiting MethodDeclaration: "+node.getName().getIdentifier();

		// some info needed to create the Famix Method
		IMethodBinding bnd = node.resolveBinding();

		Collection<String> paramTypes = new ArrayList<String>();
		for (SingleVariableDeclaration param : (List<SingleVariableDeclaration>)node.parameters()) {
				paramTypes.add(findTypeName(param.getType()));
		}

		fr.inria.verveine.core.gen.famix.Type fmxRetTyp = null;
		if (! node.isConstructor()) {
			// creates the method with a fake return type because we might need this FamixMethod to create the return type (if it is a ParameterizedType)
			// we reset the return type to its proper value later
			fmxRetTyp = dico.ensureFamixClassObject(null);
		}
		Method fmx = dico.ensureFamixMethod(bnd, node.getName().getIdentifier(), paramTypes, /*retType*/fmxRetTyp, context.topType());

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
		closeMethodDeclaration();
		super.endVisit(node);
	}

	@Override
	public boolean visit(Initializer node) {
//		System.out.println("TRACE, Visiting Initializer: ");
		
		Collection<String> paramTypes = new ArrayList<String>();

		Method fmx = dico.ensureFamixMethod((IMethodBinding)null, JavaDictionary.INIT_BLOCK_NAME, paramTypes, /*retType*/null, context.topType());

		if (fmx != null) {
			fmx.setIsStub(false);

			pushInitBlockMethod(fmx);
			dico.addSourceAnchor(fmx, node);
			dico.createFamixComment(node.getJavadoc(), fmx);

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

	/**
	 * Special method InitBlock may be "created" in various steps,
	 * mainly when attributes are declared+initialized with the result of a method call.<br>
	 * In such a case, we need to recover the previous metric values to add to them
	 * @param fmx -- the InitBlock FamixMethod
	 */
	private void pushInitBlockMethod(Method fmx) {
		this.context.pushMethod(fmx);
		if ( (fmx.getNOS() != 0) || (fmx.getCyclo() != 0) ) {
			context.setTopMethodNOS(fmx.getNOS());
			context.setTopMethodCyclo(fmx.getCyclo());
		}
	}
	
	@Override
	public void endVisit(Initializer node) {
		closeMethodDeclaration();
		super.endVisit(node);
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
				fmx.setNOS(nos);
				fmx.setCyclo(cyclo);
			}
		}
	}

	@SuppressWarnings({ "unchecked" })
	public boolean visit(FieldDeclaration node) {
//		System.err.println("TRACE, Visiting FieldDeclaration");

		fr.inria.verveine.core.gen.famix.Type varTyp = referedType(node.getType(), context.topType());
		for (StructuralEntity att : visitVariablesDeclarations(node, varTyp, (List<VariableDeclaration>)node.fragments(), context.topType()) ) {
			dico.addSourceAnchor(att, node);
			dico.createFamixComment(node.getJavadoc(), att);
		}
		return super.visit(node);
	}

	@SuppressWarnings("unchecked")
	public boolean visit(VariableDeclarationExpression node) {
//		System.err.println("TRACE, Visiting VariableDeclarationExpression: "+((VariableDeclaration)node.fragments().iterator().next()).getName().getIdentifier()+" (...)");

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
//		System.err.println("TRACE, Visiting VariableDeclarationStatement: "+((VariableDeclaration)node.fragments().iterator().next()).getName().getIdentifier()+" (...)");

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
				fmx = dico.ensureFamixAttribute(bnd, name, varTyp, (fr.inria.verveine.core.gen.famix.Type) ctxt);
			}
			else if ( (node instanceof VariableDeclarationExpression) || (node instanceof VariableDeclarationStatement) ) {
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
			String tname = findTypeName(typ);
			ParameterizableClass generic = null;
			generic = (ParameterizableClass) dico.ensureFamixClass(parameterizableBnd, tname, /*owner*/null, /*isGeneric*/true);

			fmxTyp = dico.ensureFamixParameterizedType(parameterizedBnd, tname, generic, /*owner*/ctxt);
			for (Type targ : (List<Type>) ((ParameterizedType)typ).typeArguments()) {
				fr.inria.verveine.core.gen.famix.Type fmxTArg = dico.ensureFamixType(targ.resolveBinding(), findTypeName(targ), /*owner*/null, ctxt);
				((fr.inria.verveine.core.gen.famix.ParameterizedType)fmxTyp).addArguments(fmxTArg);
			}
		}
		else if ( typ.isSimpleType() && (typ.resolveBinding()==null) && (ctxt instanceof Method)) {
			// might be a wildcardType with no previous "definition"
			// in this case the owner is this very method
			fmxTyp = dico.ensureFamixType((ITypeBinding)null, findTypeName(typ), /*owner*/ctxt, ctxt);
		}
		else {
			fmxTyp = dico.ensureFamixType(typ.resolveBinding(), findTypeName(typ), /*owner*/null, ctxt);
		}
		return fmxTyp;
	}

	@SuppressWarnings("unchecked")
	public boolean visit(MethodInvocation node) {
		boolean fieldInit = false;
		Expression callingExpr = node.getExpression();
		if (callingExpr == null) {
			if (context.topMethod() == null) {
				// probably a method call to initialize a field when declaring it
				fieldInit = true;
				Method ctxt = dico.ensureFamixMethod((IMethodBinding)null, JavaDictionary.INIT_BLOCK_NAME, new ArrayList<String>(), /*retType*/null, context.topType());
				pushInitBlockMethod(ctxt);
			}
		}
		methodInvocation(node.resolveMethodBinding(), node.getName().getFullyQualifiedName(), getReceiver(callingExpr), node.arguments());
		if (callingExpr instanceof SimpleName) {
			visitSimpleName((SimpleName) callingExpr);
		}
		
		if (fieldInit) {
			closeMethodDeclaration();
		}

		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(SuperMethodInvocation node) {
		NamedEntity receiver = this.dico.ensureFamixImplicitVariable(Dictionary.SUPER_NAME, this.context.topType(), context.topMethod());
		methodInvocation(node.resolveMethodBinding(), node.getName().getFullyQualifiedName(), receiver, node.arguments());

		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(ConstructorInvocation node) {
		this.context.addTopMethodNOS(1);
		
		// ConstructorInvocation (i.e. 'this(...)' ) happen in constructor, so the name is the same
		String name = context.topMethod().getName();
		Method invoked = dico.ensureFamixMethod(node.resolveConstructorBinding(), name, (Collection<String>)null, /*retType*/null, /*owner*/context.topType());  // cast needed to desambiguate the call
		ImplicitVariable receiver = dico.ensureFamixImplicitVariable(Dictionary.SELF_NAME, (Class) context.topType(), context.topMethod());
		Invocation invok = dico.addFamixInvocation(context.topMethod(), invoked, receiver, context.getLastInvocation());
		context.setLastInvocation( invok );

		return super.visit(node);
	}

	public boolean visit(SuperConstructorInvocation node) {
		this.context.addTopMethodNOS(1);
		
		// ConstructorInvocation (i.e. 'super(...)' ) happen in constructor, so the name is that of the superclass
		Method invoked = this.dico.ensureFamixMethod(node.resolveConstructorBinding(), /*name*/null, /*paramTypes*/(Collection<String>)null, /*retType*/null, /*owner*/null);  // cast needed to desambiguate the call
		ImplicitVariable receiver = dico.ensureFamixImplicitVariable(Dictionary.SUPER_NAME, (Class) context.topType(), context.topMethod());
		Invocation invok = dico.addFamixInvocation(context.topMethod(), invoked, receiver, context.getLastInvocation());
		context.setLastInvocation( invok );

		return super.visit(node);
	}

	/**
	 * Handles an invocation of a method by creating the corresponding Famix Entity
	 * @param calledBnd -- a binding for the method
	 * @param calledName of the method invoked
	 * @param receiver of the call, i.e. the object to which the message is sent
	 */
	private void methodInvocation(IMethodBinding calledBnd, String calledName, NamedEntity receiver, Collection<Expression> l_args) {
		BehaviouralEntity sender = this.context.topMethod();
		Method invoked = null;

		if (calledBnd != null) {
			// for parameterized methods there is a level of indirection, for other methods don't change anything
			calledBnd = calledBnd.getMethodDeclaration();
		}
		
		if ( (receiver != null) && (receiver.getName().equals("class")) && (calledBnd != null) && (calledBnd.getDeclaringClass()==null)) {
			/*bug with JDT*/
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
					fr.inria.verveine.core.gen.famix.Type retType = dico.ensureFamixType(calledBnd.getReturnType(), /*name*/null, /*owner*/null, /*ctxt*/javaMetaClass);
					Collection<String> paramTypes = new ArrayList<String>();
					for (ITypeBinding pt : calledBnd.getParameterTypes()) {
							paramTypes.add(pt.getName());
					}
					invoked = this.dico.ensureFamixMethod(null, calledName, paramTypes, /*retType*/retType, /*owner*/javaMetaClass);
					dico.mapKey(calledBnd, invoked);
				}
			}
		}
		else if ( (calledBnd != null) && (calledBnd.isAnnotationMember()) ) {
			// checks whether this is not an AnnotationType member
			// similar to creating a FamixAttribute access
			AnnotationTypeAttribute accessed =  dico.ensureFamixAnnotationTypeAttribute(calledBnd, calledName, /*owner*/null);
			if ( (accessed != null) && (sender != null) ) {
				context.setLastAccess( dico.addFamixAccess(sender, accessed, /*isWrite*/false, context.getLastAccess()) );
			}
		}
		else {
			if (sender != null) {
				if ( (receiver != null) && (receiver instanceof StructuralEntity) ) {
					fr.inria.verveine.core.gen.famix.Type varTyp = ((StructuralEntity)receiver).getDeclaredType();
					invoked = this.dico.ensureFamixMethod(calledBnd, calledName, (Collection<String>)null, /*retType*/null, /*owner*/varTyp);  // cast needed to desambiguate the call
				}
				else {
					//  static method called on the class (or null receiver)
					invoked = this.dico.ensureFamixMethod(calledBnd, calledName, (Collection<String>)null, /*retType*/null, /*owner*/(fr.inria.verveine.core.gen.famix.Type)receiver);  // cast needed to desambiguate the call
				}
				Invocation invok = dico.addFamixInvocation(sender, invoked, receiver, context.getLastInvocation());
				context.setLastInvocation( invok );
			}
		}

		for (Expression a : l_args) {
			if (a instanceof SimpleName) {
				visitSimpleName((SimpleName) a);
			}
		}
	}

	public boolean visit(FieldAccess node) {
//		System.out.println("TRACE, Visiting FieldAccess: "+node.getName().getIdentifier());
		BehaviouralEntity accessor = this.context.topMethod();
		IVariableBinding bnd = node.resolveFieldBinding();
		// FIXME if bnd == null we have a problem
		createAccessedStructEntity(bnd, node.getName().getIdentifier(), /*typ*/null, /*owner*/null, accessor);

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
			createAccessedStructEntity((IVariableBinding)bnd, node.getName().getIdentifier(), /*typ*/null, /*owner*/null, accessor);
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

		Attribute accessed =  dico.ensureFamixAttribute(null, "class", javaMetaClass, javaMetaClass);
		if ( (accessed != null) && (accessor != null) ) {
			context.setLastAccess( dico.addFamixAccess(accessor, accessed, /*isWrite*/false, context.getLastAccess()) );
		}

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
		if (node.getExpression() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getExpression());
		}
		this.context.addTopMethodNOS(1);

		Method meth = this.context.topMethod();
		fr.inria.verveine.core.gen.famix.Class excepFmx = this.dico.ensureFamixClass(node.getExpression().resolveTypeBinding(), /*name*/(String)null, /*owner*/(ContainerEntity)null, /*isGeneric*/false);
		if (excepFmx != null) {
			dico.createFamixThrownException(meth, excepFmx);
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
		if (node.getLeftHandSide() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getLeftHandSide());
		}
		if (node.getRightHandSide() instanceof SimpleName) {
			visitSimpleName((SimpleName) node.getRightHandSide());
		}		
		this.context.addTopMethodNOS(1);
		return super.visit(node);
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

	/**
	 * Deals with expressions that are simple name, e.g. "if (aBool) ..." or "return aVar;"<br>
	 * Tries to create a StruturalEntity access from this.
	 * {@link ASTVisitor#visit(SimpleName node)} cannot be used because it would be called all the time, so we need
	 * to check in all places where such simple expressions may occur and that are of interest to us
	 * @param expr -- the SimpleName expression
	 */
	private void visitSimpleName(SimpleName expr) {
//		System.out.println("visitSimpleName(): "+expr.getIdentifier());
		IBinding bnd = expr.resolveBinding();
		if ( (bnd instanceof IVariableBinding) && (context.topMethod() != null) ) {
			// could be a variable, a field, an enumValue, ...
			BehaviouralEntity accessor = this.context.topMethod();
			createAccessedStructEntity((IVariableBinding)bnd, expr.getIdentifier(), /*typ*/null, /*owner*/null, accessor);
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
			return this.dico.ensureFamixImplicitVariable(dico.SELF_NAME, this.context.topType(), context.topMethod());
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
			return createAccessedStructEntity(((FieldAccess) expr).resolveFieldBinding(), ((FieldAccess) expr).getName().getIdentifier(), /*type*/null, /*owner*/null, /*accessor*/null);
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
				ret = dico.ensureFamixType((ITypeBinding)bnd, /*name*/null, /*owner*/null, /*ctxt*/context.top());
			}
			else if (bnd instanceof IVariableBinding) {
				String varName = ( ((Name)expr).isSimpleName() ? ((SimpleName)expr).getFullyQualifiedName() : ((QualifiedName)expr).getName().getIdentifier());
				if ( ((IVariableBinding)bnd).isField() ) {
					return createAccessedStructEntity((IVariableBinding)bnd, varName, /*typ*/null, /*owner*/null, /*accessor*/null);
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
			return createAccessedStructEntity(((SuperFieldAccess) expr).resolveFieldBinding(), ((SuperFieldAccess) expr).getName().getIdentifier(), /*typ*/null, /*owner*/null, /*accessor*/null);
		}

		// super.msg1().msg() -- TODO similar to ClassInstanceCreation, 'msg()' is sent to the object returned by 'msg1()'
		else if (expr instanceof SuperMethodInvocation) {
			//System.err.println("WARNING: Ignored receiver expression in method call: SuperMethodInvocation");
			
			return null;
		}
		
		// this.msg()
		else if (expr instanceof ThisExpression) {
			return this.dico.ensureFamixImplicitVariable(dico.SELF_NAME, this.context.topType(), context.topMethod());
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

	private StructuralEntity createAccessedStructEntity(IVariableBinding bnd, String name, fr.inria.verveine.core.gen.famix.Type typ, ContainerEntity owner, BehaviouralEntity accessor) {
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
			accessed =  dico.ensureFamixEnumValue(bnd, name, (Enum) owner);
		}
		else if (bnd.isField()) {
			accessed =  dico.ensureFamixAttribute(bnd, name, typ, (fr.inria.verveine.core.gen.famix.Type) owner);
			// special case: length attribute of arrays in Java
			if ( (accessed != null) && (((Attribute) accessed).getParentType() == null) && (accessed.getName().equals("length")) ) {
				((Attribute) accessed).setParentType(dico.ensureFamixClassArray());
			}
		}
		else if (bnd.isParameter()) {
			accessed =  dico.ensureFamixParameter(bnd, name, typ, (Method) owner);
		}
		else {
			// it seems it is a variable.
			// if it is not already defined, we assume we are not interested
			accessed =  (StructuralEntity) dico.getEntityByKey(bnd);
		}
		
		// We don't create local accesses
		if ( (accessed != null) && (accessor != null) && (accessed.getBelongsTo() != accessor)) {
			context.setLastAccess( dico.addFamixAccess(accessor, accessed, /*isWrite*/false, context.getLastAccess()) );
		}
		return accessed;
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

}