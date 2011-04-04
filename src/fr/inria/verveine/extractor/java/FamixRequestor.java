package fr.inria.verveine.extractor.java;

import java.util.Collection;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import ch.akuhn.fame.Repository;

public class FamixRequestor extends FileASTRequestor {

	protected Repository famixRepo;
	
	protected JavaDictionary famixDictionnary;

	private Collection<String> argsDir;
	private Collection<String> argsFile;
	
	public FamixRequestor(Repository r, Collection<String> argsDir, Collection<String> argsFile) {
		this.famixRepo = r;
		this.argsDir = argsDir;
		this.argsFile = argsFile;

		this.famixDictionnary = new JavaDictionary(famixRepo);
	}

	public void acceptAST(String sourceFilePath, CompilationUnit ast) {
//		System.out.println("  ******* TRACE, Requestor is visiting : "+sourceFilePath+" *******");
		
		ast.setProperty(JavaDictionary.SOURCE_FILENAME_PROPERTY, relativePath(sourceFilePath));
		ast.accept(new VerveineVisitor(this.famixDictionnary));
	}

	private Object relativePath(String fullPath) {
		for (String f : argsFile) {
			if (fullPath.endsWith(f)) {
				return f;
			}
		}

		for (String d : argsDir) {
			int i = fullPath.indexOf(d);
			if (i >= 0) {  // if fullPath contains d
				return fullPath.substring(i);
			}
		}
		return fullPath;
	}
}
