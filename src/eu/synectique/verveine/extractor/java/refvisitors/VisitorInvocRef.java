package eu.synectique.verveine.extractor.java.refvisitors;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;

import eu.synectique.verveine.core.Dictionary;
import eu.synectique.verveine.core.gen.famix.BehaviouralEntity;
import eu.synectique.verveine.core.gen.famix.ContainerEntity;
import eu.synectique.verveine.core.gen.famix.ImplicitVariable;
import eu.synectique.verveine.core.gen.famix.Inheritance;
import eu.synectique.verveine.core.gen.famix.Invocation;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.NamedEntity;
import eu.synectique.verveine.core.gen.famix.StructuralEntity;
import eu.synectique.verveine.extractor.java.JavaDictionary;
import eu.synectique.verveine.extractor.java.VerveineJParser;

public class VisitorInvocRef extends AbstractRefVisitor {

	/**
	 * Useful to keep the FamixType created in the specific case of "new SomeClass().someMethod()"
	 */
	private eu.synectique.verveine.core.gen.famix.Type classInstanceCreated = null;

	/**
	 * The source code of the visited AST.
	 * Used to find back the contents of non-javadoc comments
	 */
	protected RandomAccessFile source;

	/**
	 * Whether a variable access is lhs (write) or not
	 */
	protected boolean inAssignmentLHS = false;

	/**
	 * what sourceAnchors to create
	 */
	private String anchors;

	public VisitorInvocRef(JavaDictionary dico, boolean classSummary, String anchors) {
		super(dico, classSummary);
		this.anchors = anchors;
	}

	// VISITOR METHODS

	public boolean visit(CompilationUnit node) {
		visitCompilationUnit(node);
		return super.visit(node);
	}

	public void endVisit(CompilationUnit node) {
		endVisitCompilationUnit(node);
	}

	/*
	 * Can only be a class or interface declaration
	 * Local type: see comment of visit(ClassInstanceCreation node)
	 */
	public boolean visit(TypeDeclaration node) {
		if (visitTypeDeclaration( node) != null) {
			return super.visit(node);
		} else {
			return false;
		}
	}

	public void endVisit(TypeDeclaration node) {
		endVisitTypeDeclaration(node);
	}

	/**
	 * See {@link VerveineVisitor#anonymousSuperType}<br>
	 * We could test if it is a local type (inner/anonymous) and not define it in case it does not make any reference
	 * to anything outside its owner class. But it would be a lot of work for probably little gain.
	 */
	public boolean visit(ClassInstanceCreation node) {
		visitClassInstanceCreation( node);
		return super.visit(node);
	}

	/**
	 * See {@link VerveineVisitor#anonymousSuperType}
	 */
	public boolean visit(AnonymousClassDeclaration node) {
		if (visitAnonymousClassDeclaration( node) != null) {
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	public void endVisit(AnonymousClassDeclaration node) {
		endVisitAnonymousClassDeclaration( node);
	}

	public boolean visit(EnumDeclaration node) {
		if (visitEnumDeclaration( node) != null) {
			return super.visit(node);
		}
		else {
			return false;
		}
	}

	public void endVisit(EnumDeclaration node) {
		endVisitEnumDeclaration( node);
	}

	public boolean visit(AnnotationTypeDeclaration node) {
		if (visitAnnotationTypeDeclaration( node) != null) {
			return super.visit(node);
		}
		else {
			return false;
		}
	}

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
				context.setLastInvocation(null);
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

	@SuppressWarnings("unchecked")
	public boolean visit(MethodInvocation node) {
		Expression callingExpr = node.getExpression();
		
		NamedEntity receiver = getReceiver(callingExpr);
		IMethodBinding bnd = node.resolveMethodBinding();
		String calledName = node.getName().getFullyQualifiedName();
		
		if (bnd == null) {
			methodInvocation(bnd, calledName, receiver, getInvokedMethodOwner(callingExpr, receiver), node.arguments());
		} else {
			methodInvocation(bnd, calledName, receiver, /*owner*/null, node.arguments());
		}//context

		Invocation lastInvok = context.getLastInvocation();
		if (anchors.equals(VerveineJParser.ANCHOR_ASSOC)
				// check that lastInvocation correspond to current one
				&& (lastInvok != null) && (lastInvok.getSender() == context.topMethod())
				&& (lastInvok.getReceiver() == receiver) && (lastInvok.getSignature().startsWith(calledName))) {
			dico.addSourceAnchor(lastInvok, node, /*oneLineAnchor*/true);
		}
		
		/* really needed ?
		if (callingExpr instanceof SimpleName) {
			visitSimpleName((SimpleName) callingExpr);
		}*/

		return super.visit(node);
	}

	@SuppressWarnings("unchecked")
	public boolean visit(SuperMethodInvocation node) {
		// ConstructorInvocation (i.e. 'this(...)' ) happen in constructor, so the name is the same
		NamedEntity receiver = this.dico.ensureFamixImplicitVariable(Dictionary.SUPER_NAME, this.context.topType(), context.topMethod(), /*persistIt*/!classSummary);
		IMethodBinding bnd = node.resolveMethodBinding();
		String calledName = node.getName().getFullyQualifiedName();

		if (bnd == null) {
			Iterator<Inheritance> iter = this.context.topType().getSuperInheritances().iterator();
			eu.synectique.verveine.core.gen.famix.Type superClass = iter.next().getSuperclass();
			/* This code does not seem to do anything worthwhile
			  while ((superClass instanceof eu.synectique.verveine.core.gen.famix.Class)
					&& (((eu.synectique.verveine.core.gen.famix.Class) superClass).getIsInterface())
					&& iter.hasNext()) {
				iter.next().getSuperclass();
			}*/
			methodInvocation(bnd, calledName, receiver, superClass, node.arguments());
		} else {
			methodInvocation(bnd, calledName, receiver, /*owner*/null, node.arguments());
		}

		Invocation lastInvok = context.getLastInvocation();
		if (anchors.equals(VerveineJParser.ANCHOR_ASSOC)
				// check that lastInvocation correspond to current one
				&& (lastInvok != null) && (lastInvok.getSender() == context.topMethod())
				&& (lastInvok.getReceiver() == receiver) && (lastInvok.getSignature().startsWith(calledName))) {
			dico.addSourceAnchor(lastInvok, node, /*oneLineAnchor*/true);
		}
		
		return super.visit(node);
	}

	public boolean visit(ConstructorInvocation node) {
		// ConstructorInvocation (i.e. 'this(...)' ) happen in constructor, so the name is the same
		
		int modifiers = (node.resolveConstructorBinding() != null) ? node.resolveConstructorBinding().getModifiers() : JavaDictionary.UNKNOWN_MODIFIERS;

		String name = context.topMethod().getName();
		Method invoked = dico.ensureFamixMethod(node.resolveConstructorBinding(), name,
				/*paramTypes*/(Collection<String>) null, /*retType*/null, /*owner*/context.topType(), modifiers,
				/*persistIt*/!classSummary);
		// constructor don't have return type so no need to create a reference from this class to the "declared return type" class when classSummary is TRUE
		// also no parameters specified here, so no references to create for them either

		if (!classSummary) {
			String signature = node.toString();
			if (signature.endsWith("\n")) {
				signature = signature.substring(0, signature.length() - 1);
			}
			if (signature.endsWith(";")) {
				signature = signature.substring(0, signature.length() - 1);
			}
			ImplicitVariable receiver = dico.ensureFamixImplicitVariable(Dictionary.SELF_NAME, context.topType(), context.topMethod(), /*persistIt=true*/!classSummary);
			
			Invocation invok = dico.addFamixInvocation(context.topMethod(), invoked, receiver, signature,
					context.getLastInvocation());
			context.setLastInvocation(invok);
			
			if (anchors.equals(VerveineJParser.ANCHOR_ASSOC) && (invok != null)) {
				dico.addSourceAnchor(invok, node, /*oneLineAnchor*/true);
			}
		}

		return super.visit(node);
	}

	public boolean visit(SuperConstructorInvocation node) {
		// ConstructorInvocation (i.e. 'super(...)' ) happen in constructor, so the name is that of the superclass
		Method invoked = this.dico.ensureFamixMethod(node.resolveConstructorBinding(), /*persistIt*/!classSummary);
		// constructor don't have return type so no need to create a reference from this class to the "declared return type" class when classSummary is TRUE
		// also no parameters specified here, so no references to create either

		if (! classSummary) {
			String signature = node.toString();
			if (signature.endsWith("\n")) {
				signature = signature.substring(0, signature.length() - 1);
			}
			if (signature.endsWith(";")) {
				signature = signature.substring(0, signature.length() - 1);
			}
			ImplicitVariable receiver = dico.ensureFamixImplicitVariable(Dictionary.SUPER_NAME, context.topType(), context.topMethod(), /*persistIt=true*/!classSummary);
			Invocation invok = dico.addFamixInvocation(context.topMethod(), invoked, receiver, signature,
					context.getLastInvocation());
			context.setLastInvocation(invok);
			if (anchors.equals(VerveineJParser.ANCHOR_ASSOC)) {
				dico.addSourceAnchor(invok, node, /*oneLineAnchor*/true);
			}
		}

		return super.visit(node);
	}

	// UTILITY METHODS

	/**
	 * Handles an invocation of a method by creating the corresponding Famix Entity.
	 * @param calledBnd -- a binding for the method invoked
	 * @param calledName of the method invoked
	 * @param receiver of the call, i.e. the object to which the message is sent
	 * @param methOwner -- owner of the method invoked. Might be a subtype of the receiver's type
	 * @param l_args -- list of the method's parameters
	 * TODO Why are Invocations, Accesses and References not created through a method in JavaDictionnary ?
	 */
	private Invocation methodInvocation(IMethodBinding calledBnd, String calledName, NamedEntity receiver,
			eu.synectique.verveine.core.gen.famix.Type methOwner, Collection<Expression> l_args) {
		BehaviouralEntity sender = this.context.topMethod();
		Method invoked = null;
		Invocation invok = null;

		if (calledBnd != null) {
			// for parameterized methods there is a level of indirection, for other methods doesn't change anything
			calledBnd = calledBnd.getMethodDeclaration();
		}

		if ((receiver != null) && (receiver.getName().equals("class")) && (calledBnd != null)
				&& (calledBnd.getDeclaringClass() == null)) {
			/*bug with JDT apparently has to do with invoking a method of a meta-class */
			// humm ... we do not create the FamixInvocation ? Seems like a bug ...
		} else if ((calledBnd != null) && (calledBnd.isAnnotationMember())) {
			// if this is not an AnnotationType member, it is similar to creating a FamixAttribute access
		} else {
			Collection<String> unkwnArgs = new ArrayList<String>();
			if (l_args != null) {
				for (@SuppressWarnings("unused")
				Expression a : l_args) {
					unkwnArgs.add("?");
				}
			}

			if (sender != null) {
				int modifiers = (calledBnd != null) ? calledBnd.getModifiers() : JavaDictionary.UNKNOWN_MODIFIERS;
				if ((receiver != null) && (receiver instanceof StructuralEntity)) {
					invoked = this.dico.ensureFamixMethod(calledBnd, calledName, unkwnArgs, /*retType*/null, methOwner,
							modifiers, /*persistIt*/!classSummary);
				} else {
					eu.synectique.verveine.core.gen.famix.Type owner;

					if (receiver != null)
						owner = (eu.synectique.verveine.core.gen.famix.Type) receiver;
					else
						owner = methOwner;
					//  static method called on the class (or null receiver)
					invoked = this.dico.ensureFamixMethod(calledBnd, calledName, unkwnArgs, /*retType*/null,
							/*owner*/owner, modifiers, /*persistIt*/!classSummary);
				}
				
				if (! classSummary) {
					String signature = calledName + "(";
					boolean first = true;
					for (Expression a : l_args) {
						if (first) {
							signature += a.toString();
							first = false;
						} else {
							signature += "," + a.toString();
						}
					}
					signature += ")";
					invok = dico.addFamixInvocation(sender, invoked, receiver, signature, context.getLastInvocation());
					//TODO add FileAnchor to Invocation
					context.setLastInvocation(invok);
				}
			}
		}

		return invok;
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
			return this.dico.ensureFamixImplicitVariable(dico.SELF_NAME, this.context.topType(), context.topMethod(),
					/*persistIt*/!classSummary);
		}

		// array[i].msg()
		if (expr instanceof ArrayAccess) {
			return getReceiver(((ArrayAccess) expr).getArray());
		}

		// new type[].msg()
		if (expr instanceof ArrayCreation) {
			//System.err.println("WARNING: Ignored receiver expression in method call: ArrayCreation");
			return null;
		}

		// (variable = value).msg()
		if (expr instanceof Assignment) {
			return getReceiver(((Assignment) expr).getLeftHandSide());
		}

		// ((type)expr).msg()
		if (expr instanceof CastExpression) {
			return getReceiver(((CastExpression) expr).getExpression());
		}

		// new Class().msg()
		if (expr instanceof ClassInstanceCreation) {
			return null;
		}

		// (cond-expr ? then-expr : else-expr).msg()
		if (expr instanceof ConditionalExpression) {
			// can be one or the other (then-expr/else-expr) so we choose one
			NamedEntity ret = getReceiver(((ConditionalExpression) expr).getThenExpression());
			if (ret == null) {
				// can as well try the other
				ret = getReceiver(((ConditionalExpression) expr).getElseExpression());
			}
			return ret;
		}

		// field.msg()
		if (expr instanceof FieldAccess) {
			IVariableBinding bnd = ((FieldAccess) expr).resolveFieldBinding();
			StructuralEntity fld = (StructuralEntity) dico.getEntityByKey(bnd);
			/*StructuralEntity fld = ensureAccessedStructEntity(bnd, ((FieldAccess) expr).getName().getIdentifier(),
					/*type* /null, /*owner* /null, /*accessor* /null);*/
			return fld;
		}

		// (left-expr oper right-expr).msg()
		if (expr instanceof InfixExpression) {
			// anonymous receiver
			return null;
		}

		// msg1().msg()
		if (expr instanceof MethodInvocation) {
			return null;
		}

		// name.msg()
		if (expr instanceof Name) {
			// can be a class or a variable name
			IBinding bnd = ((Name) expr).resolveBinding();
			if (bnd == null) {
				return null;
			}
			NamedEntity ret = null;
			if (bnd instanceof ITypeBinding) {
				// msg() is a static method of Name so name should be a class, except if its an Enum
				ret = dico.getEntityByKey(bnd);
			}

			if (bnd instanceof IVariableBinding) {
				return dico.getEntityByKey(bnd);
			}

			return ret;
		}

		// (expr).msg()
		if (expr instanceof ParenthesizedExpression) {
			return getReceiver(((ParenthesizedExpression) expr).getExpression());
		}

		// "string".msg()
		if (expr instanceof StringLiteral) {
			return null;
		}

		// super.field.msg()
		if (expr instanceof SuperFieldAccess) {
			return dico.getEntityByKey(((SuperFieldAccess) expr).resolveFieldBinding());
			/*return ensureAccessedStructEntity(((SuperFieldAccess) expr).resolveFieldBinding(),
					((SuperFieldAccess) expr).getName().getIdentifier(), /*typ* /null, /*owner* /null, /*accessor* /null);*/
		}

		// super.msg1().msg()
		if (expr instanceof SuperMethodInvocation) {
			return null;
		}

		// this.msg()
		if (expr instanceof ThisExpression) {
			return this.dico.ensureFamixImplicitVariable(Dictionary.SELF_NAME, context.topType(), context.topMethod(), /*persistIt*/! classSummary);
		}

		// type.class.msg()
		if (expr instanceof TypeLiteral) {
			// similar to a field access
			return dico.getFamixAttribute(null, "class", dico.ensureFamixMetaClass(null));
		}

		// ... OTHER POSSIBLE EXPRESSIONS ?
		System.err.println("WARNING: Unexpected receiver expression: " + expr.getClass().getName()
							+ " (method called is" + expr.getClass().getName() + ".aMethod(...))");
		return null;
	}

	/**
	 * Tries its best to find the type of a receiver without using the bindings.
	 * Most of the time, the type is that of the receiver, but not always (if there is a cast or if receiver is null)
	 * @param expr -- the Java expression describing the receiver
	 * @param receiver -- the FAMIX Entity describing the receiver
	 * @return the Famix Entity or null if could not find it
	 */
	private eu.synectique.verveine.core.gen.famix.Type getInvokedMethodOwner(Expression expr, NamedEntity receiver) {
		// ((type)expr).msg()
		if (expr instanceof CastExpression) {
			Type tcast = ((CastExpression) expr).getType();
			return referedType(tcast, (ContainerEntity) this.context.top(), true);
		}

		// new Class().msg()
		else if (expr instanceof ClassInstanceCreation) {
			return this.classInstanceCreated;
		}

		// msg1().msg()
		else if (expr instanceof MethodInvocation) {
			IMethodBinding callerBnd = ((MethodInvocation) expr).resolveMethodBinding();
			if (callerBnd != null) {
				return referedType(callerBnd.getReturnType(), (ContainerEntity) this.context.top(), true);
			} else {
				return null;
			}
		}

		// (expr).msg()
		else if (expr instanceof ParenthesizedExpression) {
			return getInvokedMethodOwner(((ParenthesizedExpression) expr).getExpression(), receiver);
		}

		// "string".msg()
		else if (expr instanceof StringLiteral) {
			return dico.ensureFamixType(null, "String", dico.ensureFamixNamespaceJavaLang(null),
					/*alwaysPersist?*/true); // creating FamixClass java.lang.String
		}

		// super.msg1().msg()
		else if (expr instanceof SuperMethodInvocation) {
			IMethodBinding superBnd = ((SuperMethodInvocation) expr).resolveMethodBinding();
			if (superBnd != null) {
				return this.referedType(superBnd.getReturnType(), context.topType(), true);
			} else {
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
				return ((StructuralEntity) receiver).getDeclaredType();
			} else if (receiver instanceof eu.synectique.verveine.core.gen.famix.Type) {
				return (eu.synectique.verveine.core.gen.famix.Type) receiver;
			}
			// ... what else ?
			else {
				return null;
			}
		}
	}

}
