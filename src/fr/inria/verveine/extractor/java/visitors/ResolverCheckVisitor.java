package fr.inria.verveine.extractor.java.visitors;


import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

/**
 * This is a simple visitor that checks that some of the main entities declarations have a binding in JDT (ie. <code>node.resolveBinding() != null</code>).
 * This is important because if an entity has no binding, then its uses can not be resolved.
 * This means for example that in <code>public ClassA aMethod( ClassB aParameter) { ... some code; }</code> if <code>ClassA</code> or <code>ClassB</code> have no binding,
 * then the <code>aMethod</code> itself has no binding and JDT is not be able to resolve invocations to it
 * 
 * To use simply call verveineJ normally with option <code>-check</code>.
 * Note: this option prevents creating any entity
 */
public class ResolverCheckVisitor extends ASTVisitor {

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		checkResolved(node.getType(), "type", ((List<VariableDeclaration>)node.fragments()).iterator().next().toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		String name = node.getName().getFullyQualifiedName() + "()";
		checkResolved(node.getReturnType2(), "return type", name);
        for (SingleVariableDeclaration param : (List<SingleVariableDeclaration>) node.parameters()) {
    		checkResolved(param.getType(), "type", name);
        }
		return super.visit(node);
	}

	@Override
	public boolean visit(ParameterizedType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		checkResolved(node.getSuperclassType(), "super-class", node.getName().getFullyQualifiedName());
		for (Type interfc : (List<Type>)node.superInterfaceTypes()) {
			checkResolved(interfc, "super-interface", node.getName().getFullyQualifiedName());
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationExpression node) {
		checkResolved(node.getType(), "type", ((List<VariableDeclaration>)node.fragments()).iterator().next().toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		checkResolved(node.getType(), "type", ((List<VariableDeclaration>)node.fragments()).iterator().next().toString());
		return super.visit(node);
	}

	protected void checkResolved(Type node, String use, String user) {
		if ( (node != null) && (node.resolveBinding() == null) ) {
			badCheck(node.toString(), use, user);
		}
	}

	private void badCheck(String node, String use, String user) {
		System.err.println("** could not resolve `" + node + "' " + use + " of `" + user + "'");
	}

}
