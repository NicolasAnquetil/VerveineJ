package fr.inria.verveine.extractor.java;

import java.util.Collection;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import ch.akuhn.fame.Repository;

public class FamixRequestor extends FileASTRequestor {

	protected Repository famixRepo;
	
	protected JavaDictionary famixDictionnary;

	/**
	 * Whether to summarize collected information at the level of classes or produce everything
	 * Summarizing at the level of classes does not produce Method, Attributes, or Accesses, Invocation
	 */
	private boolean classSummary = false;

	private Collection<String> argsDir;
	private Collection<String> argsFile;
	
	public FamixRequestor(Repository r, Collection<String> argsDir, Collection<String> argsFile, boolean classSummary) {
		this.famixRepo = r;
		this.argsDir = argsDir;
		this.argsFile = argsFile;
		this.classSummary = classSummary;

		this.famixDictionnary = new JavaDictionary(famixRepo);
	}

	public void acceptAST(String sourceFilePath, CompilationUnit ast) {
		String path = relativePath(sourceFilePath);
		System.out.println("VerveineJ processing file: "+path);

		ast.setProperty(JavaDictionary.SOURCE_FILENAME_PROPERTY, path);
		try {
			ast.accept(new VerveineVisitor(this.famixDictionnary, classSummary));
		}
		catch (Exception e) {
			System.err.println("*** VerveineJ visitor got exception: '"+e.getMessage()+"' while processing file: "+path);
		}
	}

	private String relativePath(String fullPath) {
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
