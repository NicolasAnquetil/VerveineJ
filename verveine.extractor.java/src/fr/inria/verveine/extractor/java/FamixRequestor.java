package fr.inria.verveine.extractor.java;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
	
	Map<String, String> dirMap;
	Map<String, String> fileMap;
	
	public FamixRequestor(Repository r, Collection<String> argsDir, Collection<String> argsFile, boolean classSummary) {
		super();
		this.famixRepo = r;
		
		this.fileMap = new HashMap<String, String>();
		// initialization of the Map with the complete absolute path
		for(String tempArgFile : argsFile)
			this.fileMap.put(new File(tempArgFile).getAbsolutePath(), tempArgFile);

		this.dirMap = new HashMap<String, String>();
		// initialization of the Map with the complete absolute path
		for(String tempArgDir : argsDir)
			this.dirMap.put(new File(tempArgDir).getAbsolutePath(), tempArgDir);
		
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
			System.err.println("*** VerveineJ visitor got exception: '"+e+"' while processing file: "+path);
		}
	}

	/**
	 * Search in the program args the corresponding path of the provided fullPath
	 * @param fullPath the provided fullPath
	 * @return the corresponding path of the provided fullPath
	 */
	private String relativePath(String path) {

		String fullPath = new File(path).getAbsolutePath();

		if (this.fileMap.containsKey(fullPath)) {
			return this.fileMap.get(fullPath);
		}

		File file = new File(fullPath);

		while(file != null)
		{

			String key = file.getAbsolutePath();

			if(this.dirMap.containsKey(key)){
				// if key is dot, we add a / behind
				if(this.dirMap.get(key).equals(".")) 
					return "./" + fullPath.substring(key.length() + 1);
				else 
					return this.dirMap.get(key) + fullPath.substring(key.length() + 1);
			}
			else 
				file = file.getParentFile();
		}
		return fullPath;
	}
}
