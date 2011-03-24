package fr.inria.verveine.extractor.java;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import ch.akuhn.fame.Repository;

public class FamixRequestor extends FileASTRequestor {

	protected Repository famixRepo;
	
	protected JavaDictionary famixDictionnary;

	private String[] initialArgs;
	
	public FamixRequestor(Repository r, String[] initialArgs) {
		this.famixRepo = r;
		this.initialArgs = initialArgs;

		this.famixDictionnary = new JavaDictionary(famixRepo);
	}

	public void acceptAST(String sourceFilePath, CompilationUnit ast) {
//		System.out.println("  ******* TRACE, Requestor is visiting : "+sourceFilePath+" *******");
		
		ast.setProperty(JavaDictionary.SOURCE_FILENAME_PROPERTY, relativePath(sourceFilePath));
		ast.accept(new VerveineDefVisitor(this.famixDictionnary));
		ast.accept(new VerveineRefVisitor(this.famixDictionnary));
	}

	private Object relativePath(String sourceFilePath) {
		for (String arg : initialArgs) {
			int i = sourceFilePath.indexOf(arg);
			if (i >= 0) {
				return sourceFilePath.substring(i);
			}
		}
		return sourceFilePath;
	}
}
