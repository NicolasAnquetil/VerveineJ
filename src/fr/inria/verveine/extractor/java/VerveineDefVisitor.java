package fr.inria.verveine.extractor.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import fr.inria.verveine.core.EntityStack;
import fr.inria.verveine.core.gen.famix.AnnotationType;
import fr.inria.verveine.core.gen.famix.AnnotationTypeAttribute;
import fr.inria.verveine.core.gen.famix.Class;
import fr.inria.verveine.core.gen.famix.ContainerEntity;
import fr.inria.verveine.core.gen.famix.EnumValue;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.ParameterType;
import fr.inria.verveine.core.gen.famix.ParameterizableClass;
import fr.inria.verveine.core.gen.famix.StructuralEntity;

/**
 * AST Visitor that defines all the (Famix) entities of interest
 * Famix entities are stored in a Map along with the IBindings to which they correspond
 */
public class VerveineDefVisitor extends ASTVisitor {

	/** 
	 * A dictionary allowing to recover created FAMIX Entities
	 */
	protected JavaDictionary dico;

	/**
	 * A stack that keeps the current definition context (package/class/method)
	 */
	protected EntityStack context;

	public VerveineDefVisitor(JavaDictionary dico) {
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
			this.context.pushPckg(null);
			return false;
		}
	}

	public void endVisit(CompilationUnit node) {
		this.context.popPckg();
		super.endVisit(node);
	}

	/*
	 * Can only be a class or interface declaration
	 */
	public boolean visit(TypeDeclaration node) {
//		System.err.println("TRACE, DefVisiting TypeDeclaration: "+node.getName().getIdentifier());
		ITypeBinding bnd = node.resolveBinding();
		@SuppressWarnings("unchecked")
		List<TypeParameter> tparams = (List<TypeParameter>)node.typeParameters();
		fr.inria.verveine.core.gen.famix.Class fmx = dico.ensureFamixClass(bnd, /*name*/node.getName().getIdentifier(), /*owner*/context.top(), tparams.size()>0); //   /*ctxt*/context.top());
		if (fmx != null) {
			fmx.setIsStub(false);

			this.context.pushClass(fmx);

			dico.addSourceAnchor(fmx, node);
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
			this.context.pushClass(null);
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
			fmx = (Class) this.dico.ensureFamixType(decl.resolveBinding(), /*name*/"anonymous("+dico.findTypeName(node.getType())+")", /*owner*/context.top(), /*ctxt*/context.top());  //  isGeneric = false
			if (fmx != null) {
				fmx.setIsStub(false);

				dico.addSourceAnchor(fmx, node);
				this.context.pushClass(fmx);
				return super.visit(node);
			}
			else {
				this.context.pushClass(null);
				return false;
			}
		}
		return super.visit(node);
	}

	public void endVisit(ClassInstanceCreation node) {
		if (node.getAnonymousClassDeclaration() != null) {
			this.context.popClass();
		}
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

		// creating/recovering it
		// creates it with a fake return type because we might need this FamixMethod to create the return type (if it is a ParameterizedType)
		// we reset the return type to its proper value later
		Method fmx = dico.ensureFamixMethod(bnd, node.getName().getIdentifier(), paramTypes, /*retType*/dico.ensureFamixClassObject(null), context.topClass());

		if (fmx != null) {
			fmx.setIsStub(false);
			
			this.context.pushMethod(fmx);
			fr.inria.verveine.core.gen.famix.Type fmxRetTyp = referedType(node.getReturnType2(), fmx);
			fmx.setDeclaredType(fmxRetTyp);
			if (node.getBody() != null) {
				context.setTopMethodCyclo(1);
			}

			dico.addSourceAnchor(fmx, node);
			dico.createFamixComment(node.getJavadoc(), fmx);

			// creating the method's parameters
			List<VariableDeclaration> paramAsVarList;
			for (SingleVariableDeclaration param : (List<SingleVariableDeclaration>)node.parameters()) {
				// Note: method and ParamTyp bindings are null for ParameterType :-(
				paramAsVarList = new ArrayList<VariableDeclaration>(1);
				paramAsVarList.add(param);
				fr.inria.verveine.core.gen.famix.Type varTyp = referedType(param.getType(), context.topMethod());
				visitVariablesDeclarations(node, varTyp, paramAsVarList, context.topMethod());
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
			if (node instanceof MethodDeclaration) {
				// creating the parameters of a method. In this case, 'fragment' is aList<SingleVariableDeclarationFragment> and 'varType' is null
				fmx = dico.ensureFamixParameter(vd.resolveBinding(), vd.getName().getIdentifier(), varTyp, (Method)ctxt);
			}
			else if (node instanceof FieldDeclaration) {
				// creating a class' field
				fmx = dico.ensureFamixAttribute(vd.resolveBinding(), vd.getName().getIdentifier(), varTyp, (fr.inria.verveine.core.gen.famix.Class) ctxt);
			}
			else if (node instanceof VariableDeclarationExpression) {
				// creating a method's local variable
				fmx = dico.ensureFamixLocalVariable(vd.resolveBinding(), vd.getName().getIdentifier(), varTyp, (Method) ctxt);
			}
			else if (node instanceof VariableDeclarationExpression) {
				// creating a method's local variable
				fmx = dico.ensureFamixLocalVariable(vd.resolveBinding(), vd.getName().getIdentifier(), varTyp, (Method) ctxt);
			}
			else {
				fmx = null;
			}

			if (fmx != null) {
				fmx.setIsStub(false);
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
		else {
			fmxTyp = dico.ensureFamixType(typ.resolveBinding(), dico.findTypeName(typ), /*owner*/null, ctxt);
		}
		return fmxTyp;
	}

	// METRICS: CYCLO, NOS
	
	public boolean visit(AssertStatement node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(Assignment node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(ConstructorInvocation node) {
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

	public boolean visit(MethodInvocation node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(ReturnStatement node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(SuperConstructorInvocation node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	public boolean visit(SuperMethodInvocation node) {
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

	public boolean visit(ThrowStatement node) {
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

}