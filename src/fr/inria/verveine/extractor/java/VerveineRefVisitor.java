package fr.inria.verveine.extractor.java;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
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
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;

import fr.inria.verveine.core.EntityStack;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.BehaviouralEntity;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.NamedEntity;
import fr.inria.verveine.core.gen.famix.Namespace;

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
		PackageDeclaration pckg = node.getPackage();
		if (pckg==null) {
			this.context.pushPckg( dico.ensureFamixNamespaceDefault() );
		}
		else {
			this.context.pushPckg( (Namespace)dico.getEntityByBinding(pckg.resolveBinding()) );
		}
		return super.visit(node);

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
		if (importBnd instanceof IMethodBinding)  {
			importBnd = ((IMethodBinding)importBnd).getDeclaringClass().getPackage();
		}
		else if (importBnd instanceof IVariableBinding)  {
			importBnd = ((IVariableBinding)importBnd).getDeclaringClass().getPackage();
		}
		else if (importBnd instanceof ITypeBinding)  {
			importBnd = ((ITypeBinding)importBnd).getPackage();
		}

		this.dico.ensureFamixReference(fmxSrc, dico.ensureFamixNamespace( (IPackageBinding) importBnd));
		
		return super.visit(node);
	}

	public boolean visit(TypeDeclaration node) {
		this.context.pushClass((fr.inria.verveine.core.gen.famix.Class)dico.getEntityByBinding(node.resolveBinding()));

		return super.visit(node);
	}

	public void endVisit(TypeDeclaration node) {
		this.context.popClass();
		super.endVisit(node);
	}

	public boolean visit(ClassInstanceCreation node) {
		AnonymousClassDeclaration decl = node.getAnonymousClassDeclaration(); 
		if (decl != null) {
			this.context.pushClass((fr.inria.verveine.core.gen.famix.Class)dico.getEntityByBinding(decl.resolveBinding()));
		}
		return super.visit(node);
	}

	public void endVisit(AnonymousClassDeclaration node) {
		this.context.popClass();
		super.endVisit(node);
	}

	public boolean visit(MethodDeclaration node) {
		this.context.pushMethod((Method)dico.ensureFamixMethod(node.resolveBinding()));
		return super.visit(node);	
	}

	public void endVisit(MethodDeclaration node) {
		this.context.popMethod();
		super.endVisit(node);
	}

	public boolean visit(MethodInvocation node) {
		methodInvocation(node.resolveMethodBinding(), node.getName().getFullyQualifiedName(), getReceiver(node.getExpression()));

		return super.visit(node);
	}

	@SuppressWarnings("static-access")
	public boolean visit(SuperMethodInvocation node) {
		methodInvocation(node.resolveMethodBinding(), node.getName().getFullyQualifiedName(), this.dico.ensureFamixImplicitVariable(this.context.topClass(), dico.SUPER_NAME));
		return super.visit(node);
	}

	public boolean visit(FieldAccess node) {
		fieldAccess(node.resolveFieldBinding());
		
		return super.visit(node);
	}

	/* Could be a FieldAccess (see JDT javadoc: class FieldAccess) 
	 */
	public boolean visit(QualifiedName node) {
		IBinding bnd = node.resolveBinding();
		if (bnd instanceof IVariableBinding) {
			// apparently this is a field
			fieldAccess((IVariableBinding) bnd);
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
			return this.dico.ensureFamixImplicitVariable(this.context.topClass(), dico.SELF_NAME);
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
			Attribute ret = null;
			IVariableBinding bnd = ((FieldAccess) expr).resolveFieldBinding();
			if (bnd == null) {
				ret = dico.ensureFamixAttribute(((FieldAccess)expr).getName().getIdentifier());
			}
			else {
				ret = this.dico.ensureFamixAttribute(bnd);
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
			NamedEntity ret = null;
			// can be a class or a variable name
			IBinding bnd = ((Name) expr).resolveBinding();
			if (bnd != null) {
				if (bnd instanceof ITypeBinding) {
					// msg() is a static method of Name
					ret = dico.createFamixUnknownVariable( dico.ensureFamixType((ITypeBinding)bnd), bnd.getName());
				}
				else if (bnd instanceof IVariableBinding) {
					if ( ((IVariableBinding)bnd).isField() ) {
						ret = dico.ensureFamixAttribute( (IVariableBinding)bnd);
					}
					else if ( ((IVariableBinding)bnd).isParameter() ) {
						ret = dico.ensureFamixParameter( (IVariableBinding)bnd);
					}
					else { // suppose it's a local variable
						ret = dico.ensureFamixLocalVariable( (IVariableBinding)bnd);
					}
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
			Attribute ret = null;
			IVariableBinding bnd = ((SuperFieldAccess) expr).resolveFieldBinding();
			if (bnd == null) {
				ret = dico.ensureFamixAttribute(((SuperFieldAccess)expr).getName().getIdentifier());
			}
			else {
				ret = this.dico.ensureFamixAttribute(bnd);
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
			return this.dico.ensureFamixImplicitVariable(this.context.topClass(), dico.SELF_NAME);
		}

		// type.class.msg()
		else if (expr instanceof TypeLiteral) {
			// may be could specify: ensureFamixClass ??
			return dico.ensureFamixType( expr.resolveTypeBinding());
		}

		// ... OTHER POSSIBLE EXPRESSIONS ?
		else  {
			System.err.println("WARNING: Unexpected receiver expression: "+expr.getClass().getName()+" (method called is" + expr.getClass().getName() + ".aMethod(...))");
		}

		return null;
	}

	/**
	 * Handles an access to a field by creating the corresponding Famix Entity
	 * @param bnd -- a binding for the field (i.e attribute)
	 */
	private void fieldAccess(IVariableBinding bnd) {
		BehaviouralEntity accessor = this.context.topMethod();
		if (accessor != null) {
			Attribute accessed = this.dico.ensureFamixAttribute(bnd);
			if (accessed != null) {
				dico.ensureFamixAccess(accessor, accessed);
				if ( (accessed.getParentType() == null) && (accessed.getName().equals("length")) ) {
					accessed.setParentType(dico.ensureFamixClassArray());
				}
			}
		}
	}

	/**
	 * Handles an invocation of a method by creating the corresponding Famix Entity
	 * @param bnd -- a binding for the method
	 * @param name of the method invoked
	 * @param receiver of the call, i.e. the object to which the message is sent
	 */
	private void methodInvocation(IMethodBinding bnd, String name, NamedEntity receiver) {
		BehaviouralEntity sender = this.context.topMethod();
		if (sender != null) {
			Method invoked = this.dico.ensureFamixMethod(bnd);
			if (invoked == null) {
				invoked = this.dico.ensureFamixStubMethod(name);
			}
			/* An example of how to include location information to an invocation
			 * But would require to pass the ASTNode in parameter to this method */
			/*dico.addSourceAnchor(*/ dico.ensureFamixInvocation(sender, invoked, receiver) /*,node)*/ ;
		}
	}


}
