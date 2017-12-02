package eu.synectique.verveine.extractor.java.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import eu.synectique.verveine.core.EntityStack;
import eu.synectique.verveine.core.gen.famix.Namespace;
import eu.synectique.verveine.extractor.java.JavaDictionary;

/**
 * AST Visitor that defines all the (Famix) entities of interest
 * Famix entities are stored in a Map along with the IBindings to which they correspond
 */
public class VisitorPackageDef extends ASTVisitor {

	/** 
	 * A dictionary allowing to recover created FAMIX Entities
	 */
	protected JavaDictionary dico;

	/**
	 * A stack that keeps the current definition context (package/class/method)
	 */
	protected EntityStack context;

	/**
	 * Whether a variable access is lhs (write) or not
	 */
	protected boolean inAssignmentLHS = false;

	public VisitorPackageDef(JavaDictionary dico, boolean classSummary, boolean allLocals, String anchors) {
		this.dico = dico;
		this.context = new EntityStack();
	}

	// VISITOR METHODS

	@Override
	public boolean visit(CompilationUnit node) {
		//System.err.println("TRACE, Visiting CompilationUnit: "+node.getProperty(JavaDictionary.SOURCE_FILENAME_PROPERTY));

		Namespace fmx = null;
		PackageDeclaration pckg = node.getPackage();
		if (pckg == null) {
			fmx = dico.ensureFamixNamespaceDefault();
		} else {
			fmx = dico.ensureFamixNamespace(pckg.resolveBinding(), pckg.getName().getFullyQualifiedName());
		}
		this.context.pushPckg(fmx);

		return super.visit(node);
	}

	@Override
	public void endVisit(CompilationUnit node) {
		this.context.popPckg();
		super.endVisit(node);
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		return false; // no need to visit children of the declaration
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		return false; // no need to visit children of the declaration	
	}
	@Override
	public boolean visit(TypeDeclaration node) {
		return false; // no need to visit children of the declaration
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		return false; // no need to visit children of the declaration
	}

}