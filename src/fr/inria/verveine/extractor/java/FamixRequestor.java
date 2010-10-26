package fr.inria.verveine.extractor.java;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import ch.akuhn.fame.Repository;

public class FamixRequestor extends FileASTRequestor {

	protected Repository famixRepo;
	
	protected JavaDictionary famixDictionnary;
	
	public FamixRequestor(Repository r) {
		this.famixRepo = r;
		this.famixDictionnary = new JavaDictionary(famixRepo);
	}

	public void acceptAST(String sourceFilePath, CompilationUnit ast) {
		ast.setProperty(JavaDictionary.SOURCE_FILENAME_PROPERTY, sourceFilePath);
		//System.out.println("  ******* DOING : "+sourceFilePath+" *******");
		ast.accept(new VerveineDefVisitor(this.famixDictionnary));
		ast.accept(new VerveineRefVisitor(this.famixDictionnary));
	}
}
