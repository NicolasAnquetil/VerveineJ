package eu.synectique.verveine.extractor.java;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import ch.akuhn.fame.Repository;
import eu.synectique.verveine.extractor.java.visitors.VerveineVisitor;

public class FamixRequestor extends FileASTRequestor {

	protected Repository famixRepo;

	/**
	 * Maps the arguments (dir names) to their absolute path<br>
	 * (well actually it is the other way around).<br>
	 * See also {@link #fileMap}
	 */
	protected Map<String, String> dirMap;

	/**
	 * Maps the arguments (file names) to their absolute path<br>
	 * (well actually it is the other way around).<br>
	 * See also {@link #dirMap}
	 */
	protected Map<String, String> fileMap;

	protected JavaDictionary famixDictionnary;
	
	/**
	 * A map of file path -> ASTs.<br>
	 * The requestor fills this map in and ASTs are latter visited.
	 */
	Map<String,CompilationUnit> asts;
	
	public FamixRequestor(Collection<String> argsDir, Collection<String> argsFile, Map<String,CompilationUnit> asts) {
		super();
		this.asts = asts;

		this.fileMap = new HashMap<String, String>();
		// initialization of the Map with the absolute paths
		for (String tempArgFile : argsFile)
			this.fileMap.put(new File(tempArgFile).getAbsolutePath(), tempArgFile);

		this.dirMap = new HashMap<String, String>();
		// initialization of the Map with the absolute paths
		for (String tempArgDir : argsDir)
			this.dirMap.put(new File(tempArgDir).getAbsolutePath(), tempArgDir);
	}

	public void acceptAST(String sourceFilePath, CompilationUnit ast) {
		String path = relativePath(sourceFilePath);
		System.out.println("Parsing file: " + path);

		ast.setProperty(JavaDictionary.SOURCE_FILENAME_PROPERTY, path);
		asts.put(path, ast);
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
