package fr.inria.verveine.extractor.java;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import fr.inria.verveine.core.EntityStack;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.LocalVariable;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.Namespace;

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
//		System.err.println("TRACE, DefVisiting CompilationUnit");
		Namespace fmx = null;
		PackageDeclaration pckg = node.getPackage();
		if (pckg==null) {
			fmx = dico.ensureFamixNamespaceDefault();
		}
		else {
			fmx = dico.ensureFamixNamespace(pckg.resolveBinding());
			if (fmx == null) {
				System.err.println("         Namespace="+pckg.getName().getFullyQualifiedName() + ",  fallback to creating a stub");
				// try again without binding
				fmx = dico.ensureFamixNamespace(pckg.getName().getFullyQualifiedName());
			}
		}
		this.context.pushPckg(fmx);
		return super.visit(node);

	}

	public void endVisit(CompilationUnit node) {
		this.context.popPckg();
		super.endVisit(node);
	}

	public boolean visit(TypeDeclaration node) {
//		System.err.println("TRACE, DefVisiting TypeDeclaration: "+node.getName().getIdentifier());
		fr.inria.verveine.core.gen.famix.Class fmx = dico.ensureFamixClass(node.resolveBinding());
		if (fmx == null) {
			// TODO try to find a binded version corresponding to this stub?
			System.err.println("         Class="+node.getName().getIdentifier() + ",  fallback to creating a stub");
			fmx = dico.ensureFamixClass(node.getName().getIdentifier());
			dico.ensureFamixInheritance(dico.ensureFamixClassObject(null), fmx);
			fmx.setContainer( context.top());
		}
		dico.addSourceAnchor(fmx, node);
		this.context.pushClass(fmx);
		return super.visit(node);
	}

	public void endVisit(TypeDeclaration node) {
		this.context.popClass();
		super.endVisit(node);
	}

	public boolean visit(ClassInstanceCreation node) {
//		System.err.println("TRACE, DefVisiting ClassInstanceCreation");
		AnonymousClassDeclaration decl = node.getAnonymousClassDeclaration(); 
		if (decl != null) {
			fr.inria.verveine.core.gen.famix.Class fmx = this.dico.ensureFamixClass(decl.resolveBinding());
			if (fmx == null) {
				System.err.println("         Class="+"anonymous(??),  fallback to creating a stub");
				fmx = dico.ensureFamixClass("anonymous(??)");
				dico.ensureFamixInheritance(dico.ensureFamixClassObject(null), fmx);
				fmx.setContainer( context.top());
			}
			dico.addSourceAnchor(fmx, node);
			this.context.pushClass(fmx);
		}
		return super.visit(node);
	}

	public void endVisit(AnonymousClassDeclaration node) {
		this.context.popClass();
		super.endVisit(node);
	}

	public boolean visit(MethodDeclaration node) {
//		System.err.println("TRACE, DefVisiting MethodDeclaration: "+node.getName().getIdentifier());
		Method fmx = dico.ensureFamixMethod(node.resolveBinding());
		if (fmx == null) {
			System.err.println("         Method="+node.getName().getIdentifier() + ",  fallback to creating a stub");
			fmx = dico.ensureFamixMethod(node.getName().getIdentifier());
			fmx.setParentType(context.topClass());
			fmx.setSignature(fmx.getName()+" (???)");
			fmx.setDeclaredType( dico.ensureFamixClassObject(null) );
		}
		
		if (fmx != null) {
			@SuppressWarnings("unchecked")
			Iterator<SingleVariableDeclaration> iter = node.parameters().iterator();
			while (iter.hasNext()) {
				SingleVariableDeclaration param = iter.next();
				dico.ensureFamixParameter(param.resolveBinding());
			}
			
			dico.addSourceAnchor(fmx, node);
			this.context.pushMethod(fmx);
		}
		return super.visit(node);	
	}

	public void endVisit(MethodDeclaration node) {
		this.context.popMethod();
		super.endVisit(node);
	}

	@SuppressWarnings({ "unchecked" })
	public boolean visit(FieldDeclaration node) {
//		System.err.println("TRACE, DefVisiting FieldDeclaration");
		for (VariableDeclarationFragment vd : (List<VariableDeclarationFragment>)node.fragments()) {
//			System.err.println("            Field: "+vd.getName().getIdentifier());
			Attribute fmx = dico.ensureFamixAttribute(vd.resolveBinding());
			if (fmx == null) {
				System.err.println("         Attribute="+vd.getName().getFullyQualifiedName() + ",  fallback to creating a stub");
				fmx = dico.ensureFamixAttribute(vd.getName().getFullyQualifiedName());
				fmx.setParentType(context.topClass());
				// should try to find type name from 'node.getType()' ?
				fmx.setDeclaredType( dico.ensureFamixClassObject(null) );
			}
			
			dico.addSourceAnchor(fmx, node);
		}
		return super.visit(node);
	}

	@SuppressWarnings("unchecked")
	public boolean visit(VariableDeclarationExpression node) {
		//System.err.println("TRACE, DefVisiting VariableDeclarationExpression");
		visitVariableDeclaration(node, node.getType(), (List<VariableDeclarationFragment>)node.fragments());
		return super.visit(node);
	}

	@SuppressWarnings("unchecked")
	public boolean visit(VariableDeclarationStatement node) {
		//System.err.println("TRACE, DefVisiting VariableDeclarationStatement");
		visitVariableDeclaration(node, node.getType(), (List<VariableDeclarationFragment>)node.fragments());
		return super.visit(node);
	}

	private void visitVariableDeclaration(ASTNode node, Type nodeTyp, List<VariableDeclarationFragment> fragments) {
		if (nodeTyp.isPrimitiveType()) {
			return;
		}

		for (VariableDeclarationFragment vd : fragments) {
			LocalVariable fmx = dico.ensureFamixLocalVariable(vd.resolveBinding());
			if (fmx == null) {
				System.err.println("         Variable="+vd.getName().getFullyQualifiedName() + ",  fallback to creating a stub");
				fmx = dico.ensureFamixLocalVariable(vd.getName().getFullyQualifiedName());
				fmx.setParentBehaviouralEntity(context.topMethod());
				// should try to find type name from 'node.getType()' ?
				fmx.setDeclaredType( dico.ensureFamixClassObject(null) );

				dico.addSourceAnchor(fmx, node);
			}
		}
	}

}
