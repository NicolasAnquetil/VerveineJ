package eu.synectique.verveine.extractor.java;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import ch.akuhn.fame.Repository;
import eu.synectique.verveine.extractor.java.VerveineVisitor;

public class FamixRequestor extends FileASTRequestor {

	protected Repository famixRepo;

	protected JavaDictionary famixDictionnary;

	/**
	 * Whether to summarize collected information at the level of classes or produce everything
	 * Summarizing at the level of classes does not produce Method, Attributes, or Accesses, Invocation
	 */
	private boolean classSummary = false;

	/**
	 * Whether to output all local variables (even those with primitive type or not (default is not).
	 */
	private boolean allLocals;


	/**
	 * Whether to output the accesses of a local variable inside its own parent
	 */
	private boolean localAccess;

	/**
	 * what sourceAnchors to create
	 */
	private String anchors;


	/**
	 * Maps the arguments (file names or dir names) to their absolute path (well actually it is the other way around)
	 */
	protected Map<String, String> dirMap;
	protected Map<String, String> fileMap;

	public FamixRequestor(Repository r, Collection<String> argsDir, Collection<String> argsFile, boolean classSummary,
						  boolean allLocals, String anchors, boolean localAccess) {
		super();
		this.famixRepo = r;

		this.fileMap = new HashMap<String, String>();
		// initialization of the Map with the absolute paths
		for (String tempArgFile : argsFile)
			this.fileMap.put(new File(tempArgFile).getAbsolutePath(), tempArgFile);

		this.dirMap = new HashMap<String, String>();
		// initialization of the Map with the absolute paths
		for (String tempArgDir : argsDir)
			this.dirMap.put(new File(tempArgDir).getAbsolutePath(), tempArgDir);

		this.classSummary = classSummary;
		this.allLocals = allLocals;
		this.anchors = anchors;
		this.localAccess = localAccess;

		this.famixDictionnary = new JavaDictionary(famixRepo);
	}

	public void acceptAST(String sourceFilePath, CompilationUnit ast) {
		String path = relativePath(sourceFilePath);
		System.out.println("Processing file: " + path);

		ast.setProperty(JavaDictionary.SOURCE_FILENAME_PROPERTY, path);
		try {
			ast.accept(new VerveineVisitor(this.famixDictionnary, classSummary, allLocals, anchors, localAccess));
		} catch (Exception e) {
			System.err.println("*** Visitor got exception: '" + e + "' while processing file: " + path);
			e.printStackTrace(); // for debugging
		}
	}

	/**
	 * Search in the program args the corresponding path of the provided fileAbsolutePath
	 * @param fileAbsolutePath the absolute path of a parsed file
	 * @return the path of the same file relative to the appropriate verveinej argument
	 */
	private String relativePath(String fileAbsolutePath) {
		File file = new File(fileAbsolutePath);
		String fullPath = file.getAbsolutePath();

		if (this.fileMap.containsKey(fullPath)) {
			// parsed file was an argument of verveinej, return the path that was given as argument
			return this.fileMap.get(fullPath);
		}

		// file belongs to a directory that was a verveinej arg
		// need to find back this arg
		while (file != null) {
			String key = file.getAbsolutePath();

			if (this.dirMap.containsKey(key)) {
				// relative path = verveineJ arg + local-path-to-the-file
				if (!this.dirMap.get(key).endsWith(File.separator))
					return this.dirMap.get(key) + "/" + fullPath.substring(key.length() + 1);
				else
					return this.dirMap.get(key) + fullPath.substring(key.length() + 1);
			} else
				file = file.getParentFile();
		}
		return fullPath;
	}
}
