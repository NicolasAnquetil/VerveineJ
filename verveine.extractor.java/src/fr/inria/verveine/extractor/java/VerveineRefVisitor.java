package fr.inria.verveine.extractor.java;

import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import fr.inria.verveine.core.EntityStack;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.BehaviouralEntity;
import fr.inria.verveine.core.gen.famix.Class;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.NamedEntity;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.StructuralEntity;

/**
 * AST Visitor that defines all the (Famix) entities of interest and links betgween them
 * Famix entities are stored in a Map along with the (AST) IBindings to which they correspond
 */
public class VerveineRefVisitor extends ASTVisitor {

	/** 
	 * A dictionary allowing to recover created FAMIX Entities
	 */
	protected JavaDictionary dico;

	/**
	 * A stack that keeps the current definition context (package/class/method)
	 */
	protected EntityStack context;

	public VerveineRefVisitor(JavaDictionary dico) {
		this.dico = dico;
		this.context = new EntityStack();
	}

	// VISITOR METHODS

	public boolean visit(CompilationUnit node) {
//		System.err.println("TRACE, RefVisiting CompilationUnit");

		PackageDeclaration pckg = node.getPackage();
		Namespace fmx = null;
		if (pckg==null) {
			 fmx = dico.ensureFamixNamespaceDefault();
		}
		else {
			fmx = (Namespace)dico.getEntityByKey(pckg.resolveBinding());
		}
		if (pckg != null) {
			this.context.pushPckg(fmx);
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

	public boolean visit(TypeDeclaration node) {
//		System.err.println("TRACE, RefVisiting TypeDeclaration: "+node.getName().getIdentifier());
		ITypeBinding bnd = node.resolveBinding();
		fr.inria.verveine.core.gen.famix.Class fmx = (fr.inria.verveine.core.gen.famix.Class)dico.getEntityByKey(bnd);
		if (fmx != null) {
			this.context.pushClass(fmx );

			dico.addFamixAnnotationInstances(bnd, fmx);

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
//		System.err.println("TRACE, RefVisiting ClassInstanceCreation");
		AnonymousClassDeclaration decl = node.getAnonymousClassDeclaration(); 
		if (decl != null) {
			ITypeBinding bnd = decl.resolveBinding();
			fr.inria.verveine.core.gen.famix.Class fmx = (fr.inria.verveine.core.gen.famix.Class)dico.getEntityByKey(bnd); 
			if (fmx != null) {
				this.context.pushClass(fmx);
				dico.addFamixAnnotationInstances(bnd, fmx);
				return super.visit(node);
			}
			else {
				this.context.pushClass(null );
				return false;
			}
		}
		else {// TODO appears to be a new: class reference
			return super.visit(node);
		}
	}

	public void endVisit(AnonymousClassDeclaration node) {
		this.context.popClass();
		super.endVisit(node);
	}

	@SuppressWarnings("unchecked")
	public boolean visit(MethodDeclaration node) {
//		System.err.println("TRACE, RefVisiting MethodDeclaration: "+node.getName().getIdentifier());

		IMethodBinding bnd = node.resolveBinding();
		Method meth = (Method) dico.getEntityByKey(bnd);

		if (meth != null) {
			this.context.pushMethod(meth);

			dico.addFamixAnnotationInstances(bnd, meth);

			// Exceptions
			for (Name excepName : (List<Name>)node.thrownExceptions()) {
				fr.inria.verveine.core.gen.famix.Class excepFmx = this.dico.ensureFamixClass(excepName.resolveTypeBinding(), excepName.getFullyQualifiedName(), null);
				if (excepFmx != null) {
					dico.ensureFamixDeclaredException(meth, excepFmx);
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
		this.context.popMethod();
		super.endVisit(node);
	}

	@SuppressWarnings({ "unchecked" })
	public boolean visit(FieldDeclaration node) {
//		System.err.println("TRACE, RefVisiting FieldDeclaration: ");
		for (VariableDeclarationFragment vd : (List<VariableDeclarationFragment>)node.fragments()) {
			IVariableBinding bnd = vd.resolveBinding();
			Attribute fmx = (Attribute) dico.getEntityByKey(bnd);

			if (fmx != null) {
				dico.addFamixAnnotationInstances(bnd, fmx);
			}
		}
		return super.visit(node);
	}

	public boolean visit(MethodInvocation node) {
		methodInvocation(node.resolveMethodBinding(), node.getName().getFullyQualifiedName(), getReceiver(node.getExpression()));
		return super.visit(node);
	}

	@SuppressWarnings({ "static-access" })
	public boolean visit(SuperMethodInvocation node) {
		methodInvocation(node.resolveMethodBinding(), node.getName().getFullyQualifiedName(), this.dico.ensureFamixImplicitVariable(dico.SUPER_NAME, this.context.topClass(), context.top()));
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
			Method invoked = this.dico.ensureFamixMethod(calledBnd, calledName, (Collection<org.eclipse.jdt.core.dom.Type>)null, /*retType*/null, /*owner*/null);  // cast needed to desambiguate the call
			context.setLastInvocation( dico.addFamixInvocation(sender, invoked, receiver, context.getLastInvocation()) );
		}
	}

	public boolean visit(FieldAccess node) {
		BehaviouralEntity accessor = this.context.topMethod();
		Attribute accessed = this.dico.ensureFamixAttribute(node.resolveFieldBinding(), node.getName().getIdentifier(), null, null);
		if ( (accessed != null) && (accessed.getParentType() == null) && (accessed.getName().equals("length")) ) {
			accessed.setParentType(dico.ensureFamixClassArray());
		}
		context.setLastAccess( dico.addFamixAccess(accessor, accessed, /*isWrite*/false, context.getLastAccess()) );

		return super.visit(node);
	}

	/* Could be a FieldAccess (see JDT javadoc: class FieldAccess) 
	 */
	public boolean visit(QualifiedName node) {
		IBinding bnd = node.resolveBinding();
		if (bnd instanceof IVariableBinding) {
			// apparently this is a field
			BehaviouralEntity accessor = this.context.topMethod();
			Attribute accessed = this.dico.ensureFamixAttribute(bnd, node.getName().getIdentifier(), null, null);
			if ( (accessed != null) && (accessed.getParentType() == null) && (accessed.getName().equals("length")) ) {
				accessed.setParentType(dico.ensureFamixClassArray());
			}
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
				excepFmx = this.dico.ensureFamixClass(excepClass.resolveBinding(), ((SimpleType) excepClass).getName().getFullyQualifiedName(), null);
			}
			else if (excepClass instanceof QualifiedType) {
				excepFmx = this.dico.ensureFamixClass(excepClass.resolveBinding(), ((QualifiedType) excepClass).getName().getIdentifier(), null);
			}
			if (excepFmx != null) {
				dico.ensureFamixCaughtException(meth, excepFmx);
			}
		}

		return super.visit(node);
	}

	@Override
	public boolean visit(ThrowStatement node) {
		Method meth = this.context.topMethod();
		fr.inria.verveine.core.gen.famix.Class excepFmx = this.dico.ensureFamixClass(node.getExpression().resolveTypeBinding(), null, null);
		if (excepFmx != null) {
			dico.ensureFamixThrownException(meth, excepFmx);
		}
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
			BehaviouralEntity accessor = this.context.topMethod();
			Attribute ret = dico.ensureFamixAttribute(((FieldAccess) expr).resolveFieldBinding(), ((FieldAccess) expr).getName().getIdentifier(), null, null);
			if ( (ret != null) && (ret.getParentType() == null) && (ret.getName().equals("length")) ) {
				ret.setParentType(dico.ensureFamixClassArray());
			}
			context.setLastAccess( dico.addFamixAccess(accessor, ret, /*isWrite*/false, context.getLastAccess()) );

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
				ret = dico.ensureFamixUnknownVariable( dico.ensureFamixType((ITypeBinding)bnd, null, null, context.top()), bnd.getName());
			}
			else if (bnd instanceof IVariableBinding) {
				String varName = ( ((Name)expr).isSimpleName() ? ((SimpleName)expr).getFullyQualifiedName() : ((QualifiedName)expr).getName().getIdentifier());
				if ( ((IVariableBinding)bnd).isField() ) {
					ret = dico.ensureFamixAttribute((IVariableBinding)bnd, varName, null, null);
					BehaviouralEntity accessor = this.context.topMethod();
					if ( (ret != null) && (((Attribute) ret).getParentType() == null) && (ret.getName().equals("length")) ) {
						((Attribute) ret).setParentType(dico.ensureFamixClassArray());
					}
					context.setLastAccess( dico.addFamixAccess(accessor, (Attribute) ret, /*isWrite*/false, context.getLastAccess()) );

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
			BehaviouralEntity accessor = this.context.topMethod();
			Attribute ret = dico.ensureFamixAttribute(((SuperFieldAccess) expr).resolveFieldBinding(), ((SuperFieldAccess) expr).getName().getIdentifier(), null, null);
			if ( (ret != null) && (ret.getParentType() == null) && (ret.getName().equals("length")) ) {
				ret.setParentType(dico.ensureFamixClassArray());
			}
			context.setLastAccess( dico.addFamixAccess(accessor, ret, /*isWrite*/false, context.getLastAccess()) );

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
			BehaviouralEntity accessor = this.context.topMethod();
			ret =  dico.ensureFamixAttribute(null, "class", javaMetaClass, javaMetaClass);
			if ( (ret != null) && (ret.getParentType() == null) && (ret.getName().equals("length")) ) {
				ret.setParentType(dico.ensureFamixClassArray());
			}
			context.setLastAccess( dico.addFamixAccess(accessor, ret, /*isWrite*/false, context.getLastAccess()) );

			return ret;
		}

		// ... OTHER POSSIBLE EXPRESSIONS ?
		else  {
			System.err.println("WARNING: Unexpected receiver expression: "+expr.getClass().getName()+" (method called is" + expr.getClass().getName() + ".aMethod(...))");
		}

		return null;
	}

}
