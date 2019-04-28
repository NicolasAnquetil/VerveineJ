package fr.inria.verveine.extractor.java.defvisitors;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

import eu.synectique.verveine.core.gen.famix.ContainerEntity;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.ParameterType;
import eu.synectique.verveine.core.gen.famix.ParameterizableClass;
import eu.synectique.verveine.core.gen.famix.ParameterizedType;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.SummarizingClassesAbstractVisitor;
import fr.inria.verveine.extractor.java.VerveineJParser;
import fr.inria.verveine.extractor.java.utils.Util;

/**
 * AST Visitor that defines all the (Famix) entities of interest
 * Famix entities are stored in a Map along with the IBindings to which they correspond
 */
public class VisitorClassMethodDef extends SummarizingClassesAbstractVisitor {

	/**
	 * what sourceAnchors to create
	 */
	protected String anchors;

	public VisitorClassMethodDef(JavaDictionary dico, boolean classSummary, boolean allLocals, String anchors) {
		super( dico, classSummary);
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

	/*
	 * Can only be a class or interface declaration
	 * Local type: see comment of visit(ClassInstanceCreation node)
	 */
	@Override
	public boolean visit(TypeDeclaration node) {
		//		System.err.println("TRACE, Visiting TypeDeclaration: "+node.getName().getIdentifier());
		ITypeBinding bnd = node.resolveBinding();
		@SuppressWarnings("unchecked")
		List<TypeParameter> tparams = (List<TypeParameter>) node.typeParameters();

		boolean persistIt = persistClass(bnd);
		// may be could use this.refereredType instead of dico.ensureFamixClass ?
		eu.synectique.verveine.core.gen.famix.Class fmx = dico.ensureFamixClass(bnd, /*name*/node.getName().getIdentifier(), (ContainerEntity) /*owner*/context.top(), /*isGeneric*/tparams.size()>0, node.getModifiers(), /*alwaysPersist?*/persistIt);
		if (fmx != null) {
			Util.recursivelySetIsStub(fmx, false);

			// if it is a generic and some parameterizedTypes were created for it
			// they are marked as stub which is not right
			if (tparams.size() > 0) {
				for (ParameterizedType candidate : dico.getEntityByName(ParameterizedType.class,
						node.getName().getIdentifier())) {
					candidate.setIsStub(false);
				}
			}

			this.context.pushType(fmx);

			if (persistIt) {
				if (!anchors.equals(VerveineJParser.ANCHOR_NONE)) {
					dico.addSourceAnchor(fmx, node, /*oneLineAnchor*/false);
				}
			}

			for (TypeParameter tp : tparams) {
				// if there is a type parameter, then fmx will be a Famix ParameterizableClass
				// note: owner of the ParameterType is the ParameterizableClass
				ParameterType fmxParam = dico.ensureFamixParameterType(tp.resolveBinding(),
						tp.getName().getIdentifier(), (ParameterizableClass) fmx, /*persistIt*/!classSummary);
				if (fmxParam != null) {
					fmxParam.setIsStub(false);
				}
			}

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
		//		System.err.println("TRACE, Visiting ClassInstanceCreation: " + node);
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
		//		System.err.println("TRACE, Visiting AnonymousClassDeclaration");
		eu.synectique.verveine.core.gen.famix.Class fmx = null;
		ITypeBinding bnd = node.resolveBinding();
		int modifiers = (bnd != null) ? bnd.getModifiers() : JavaDictionary.UNKNOWN_MODIFIERS;
		fmx = this.dico.ensureFamixClass(bnd, Util.stringForAnonymousName(anonymousSuperTypeName, context), (ContainerEntity) /*owner*/context.top(), /*isGeneric*/false, modifiers, /*alwaysPersist?*/!classSummary);
		if (fmx != null) {
			Util.recursivelySetIsStub(fmx, false);

			if (! classSummary) {
				if ((!anchors.equals(VerveineJParser.ANCHOR_NONE)) && (fmx != null)) {
					dico.addSourceAnchor(fmx, node, /*oneLineAnchor*/false);
				}
			}
			this.context.pushType(fmx);
			return super.visit(node);
		} else {
			return false;
		}
	}

	@Override
	public void endVisit(AnonymousClassDeclaration node) {
		anonymousSuperTypeName = null;
		this.context.popType();
		super.endVisit(node);
	}

	@Override
	public boolean visit(EnumDeclaration node) {
//		System.err.println("TRACE, Visiting EnumDeclaration: "+node.getName().getIdentifier());

		eu.synectique.verveine.core.gen.famix.Enum fmx = dico.ensureFamixEnum(node.resolveBinding(), node.getName().getIdentifier(), (ContainerEntity) context.top());
		if (fmx != null) {
			Util.recursivelySetIsStub(fmx, false);

			this.context.pushType(fmx);
			if ( ! anchors.equals(VerveineJParser.ANCHOR_NONE) ) {
				dico.addSourceAnchor(fmx, node, /*oneLineAnchor*/false);
			}
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

	/**
	 * Local type: same as {@link VisitorClassMethodDef#visit(ClassInstanceCreation)}, 
	 * we create it even if it is a local method because their are too many ways it can access external things
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		IMethodBinding bnd = node.resolveBinding();

		Method fmx = dico.ensureFamixMethod(bnd, node.getName().getIdentifier(), /*paramTypes*/null, /*owner*/context.topType(), node.getModifiers(), /*persitIt*/!classSummary);

		if (fmx != null) {
			fmx.setIsStub(false);
			// TODO change FamixMethod to add setBodyHash
			//fmx.setBodyHash(this.computeHashForMethodBody(node));

			this.context.pushMethod(fmx);

			if (node.isConstructor()) {
				fmx.setKind(JavaDictionary.CONSTRUCTOR_KIND_MARKER);
			}

			if (!classSummary) {
				if (!anchors.equals(VerveineJParser.ANCHOR_NONE)) {
					dico.addSourceAnchor(fmx, node, /*oneLineAnchor*/false);
				}
			}

			if (node.getBody() != null) {
				context.setTopMethodCyclo(1);
			}
			return super.visit(node);
		} else {
			this.context.pushMethod(null);
			return false;
		}
	}
	
/* TODO change FamixMethod to add setBodyHash
	private String computeHashForMethodBody(MethodDeclaration node) {
		// not optimized but will work in a first version
		Block body = node.getBody();
		if (body == null)
			return "0";
		return DigestUtils.md5Hex(node.getBody().toString().replaceAll("\\r|\\n|\\t", ""));
	}
*/

	@Override
	public void endVisit(MethodDeclaration node) {
		closeMethodDeclaration();
		super.endVisit(node);
	}

    /**
     * BodyDeclaration ::=
     *                [ ... ]
     *                 FieldDeclaration
     *                 Initializer
     *                 MethodDeclaration (for methods and constructors)
     * Initializer ::=
     *      [ static ] Block
     */
    @Override
	public boolean visit(Initializer node) {
		//		System.err.println("TRACE, Visiting Initializer: ");

		Method fmx = createInitBlock();
		// init-block don't have return type so no need to create a reference from this class to the "declared return type" class when classSummary is TRUE
		// also no parameters specified here, so no references to create either

		if (fmx != null) {
            dico.setMethodModifiers(fmx, node.getModifiers());
			if (!classSummary) {
				if (!anchors.equals(VerveineJParser.ANCHOR_NONE)) {
					dico.addSourceAnchor(fmx, node, /*oneLineAnchor*/false);
				}
			}

			if (node.getBody() != null) {
				context.setTopMethodCyclo(1);
			}

			return super.visit(node);
		} else {
			this.context.pushMethod(null);
			return false;
		}
	}

	@Override
	public void endVisit(Initializer node) {
		closeMethodDeclaration();
		super.endVisit(node);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(EnumConstantDeclaration node) {
		for (Expression expr : (List<Expression>)node.arguments()) {
			if (expr != null) {
				createInitBlock();
				break;  // we created the INIT_BLOCK, no need to look for other declaration that would only ensure the same creation
			}
		}
		return super.visit(node);
	
	}

    public void endVisit(EnumConstantDeclaration node) {
        closeOptionalInitBlock();
    }

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(FieldDeclaration node) {
		boolean hasInitBlock = false;
		for (VariableDeclaration vardecl : (List<VariableDeclaration>)node.fragments() ) {
			if (vardecl.getInitializer() != null) {
				createInitBlock();
				hasInitBlock = true;
				break;  // we created the INIT_BLOCK, no need to look for other declaration that would only ensure the same creation
			}
		}
		return hasInitBlock;
	}

    public void endVisit(FieldDeclaration node) {
        closeOptionalInitBlock();
    }

	@Override
	public boolean visit(ConstructorInvocation node) {
		//		System.err.println("TRACE, Visiting ConstructorInvocation: ");
		this.context.addTopMethodNOS(1);
		return false;
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		this.context.addTopMethodNOS(1);
		return false;
	}

	// "SomeClass.class"
	public boolean visit(TypeLiteral node) {
		dico.ensureFamixMetaClass(null);
		return false;
	}

	@Override
	public boolean visit(ThrowStatement node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	@Override
	public boolean visit(AssertStatement node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	@Override
	public boolean visit(Assignment node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	@Override
	public boolean visit(ContinueStatement node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	@Override
	public boolean visit(DoStatement node) {
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	@Override
	public boolean visit(ForStatement node) {
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	@Override
	public boolean visit(IfStatement node) {
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	@Override
	public boolean visit(ReturnStatement node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	@Override
	public boolean visit(SwitchCase node) {
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	@Override
	public boolean visit(SwitchStatement node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	@Override
	public boolean visit(TryStatement node) {
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	@Override
	public boolean visit(WhileStatement node) {
		this.context.addTopMethodCyclo(1);
		this.context.addTopMethodNOS(1);
		return super.visit(node);
	}

	// UTILITY METHODS

    /**
     * REnsures the creation of the fake method: {@link JavaDictionary#INIT_BLOCK_NAME}
     *
     * Used in the case of instance/class initializer and initializing expressions of FieldDeclarations and EnumConstantDeclarations
	 */
	protected Method createInitBlock() {
		// putting field's initialization code in an INIT_BLOCK_NAME method
		Method ctxtMeth = this.context.topMethod();
		if (ctxtMeth != null && !ctxtMeth.getName().equals(JavaDictionary.INIT_BLOCK_NAME)) {
			ctxtMeth = null;
		} else {
			if (ctxtMeth != null && ctxtMeth.getParentType() != context.topType()) {
				/* apparently we are in a field initialization, in an (anonymous class) which is created as another field initialization:
				 * class Class1 {
				 *   Class2 aField1 = new Class2() {
				 *     Class3 aField2 = xyz;
				 *   }}
				 */
				ctxtMeth = null;
			}
		}
		if (ctxtMeth == null) {
			ctxtMeth = dico.ensureFamixMethod((IMethodBinding) null, JavaDictionary.INIT_BLOCK_NAME, new ArrayList<String>(), context.topType(),
					/*modifiers*/JavaDictionary.UNKNOWN_MODIFIERS, /*persistIt*/!classSummary);
			ctxtMeth.setIsStub(false);
			// initialization block doesn't have return type so no need to create a reference from its class to the "declared return type" class when classSummary is TRUE
			pushInitBlockMethod(ctxtMeth);
		}

		return ctxtMeth;
	}

	/**
	 * Special method InitBlock may be "created" in various steps,
	 * mainly when attributes are declared+initialized with the result of a method call.<br>
	 * In such a case, we need to recover the previous metric values to add to them
	 * @param fmx -- the InitBlock FamixMethod
	 */
	protected void pushInitBlockMethod(Method fmx) {
		int nos = (fmx.getNumberOfStatements() == null) ? 0 : fmx.getNumberOfStatements().intValue();
		int cyclo = (fmx.getCyclomaticComplexity() == null) ? 0 : fmx.getCyclomaticComplexity().intValue();
		this.context.pushMethod(fmx);
		if ((nos != 0) || (cyclo != 0)) {
			context.setTopMethodNOS(nos);
			context.setTopMethodCyclo(cyclo);
		}
	}

	protected void closeOptionalInitBlock() {
		Method ctxtMeth = this.context.topMethod();
		if ((ctxtMeth != null) && (ctxtMeth.getName().equals(JavaDictionary.INIT_BLOCK_NAME))) {
			closeMethodDeclaration();
		}
	}

	/**
	 * When closing a method declaration, we need to take care of some metrics that are also collected
	 */
	protected void closeMethodDeclaration() {
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

}