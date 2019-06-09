package fr.inria.verveine.extractor.java;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.inria.verveine.extractor.java.visitors.refvisitors.*;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import ch.akuhn.fame.Repository;
import fr.inria.verveine.extractor.java.visitors.defvisitors.VisitorClassMethodDef;
import fr.inria.verveine.extractor.java.visitors.defvisitors.VisitorComments;
import fr.inria.verveine.extractor.java.visitors.defvisitors.VisitorPackageDef;
import fr.inria.verveine.extractor.java.visitors.defvisitors.VisitorVarsDef;

public class FamixRequestor extends FileASTRequestor {

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
	 * what sourceAnchors to create
	 */
	private VerveineJParser.anchorOptions anchors;

	/**
	 * Maps the arguments (file names or dir names) to their absolute path (well actually it is the other way around)
	 */
	protected Map<String, String> dirMap;
	protected Map<String, String> fileMap;

	public FamixRequestor(Repository repo, Collection<String> argsDir, Collection<String> argsFile, boolean classSummary,
						  boolean allLocals, VerveineJParser.anchorOptions anchors) {
		super();

		this.fileMap = new HashMap<String, String>();
		// initialization of the Map with the absolute paths
		for (String tempArgFile : argsFile)
			this.fileMap.put(new File(tempArgFile).getAbsolutePath(), tempArgFile);

		this.dirMap = new HashMap<String, String>();
		// initialization of the Map with the absolute paths
		for (String tempArgDir : argsDir) {
			this.dirMap.put(new File(tempArgDir).getAbsolutePath(), tempArgDir);
		}

		this.classSummary = classSummary;
		this.allLocals = allLocals;
		this.anchors = anchors;

		this.famixDictionnary = new JavaDictionary(repo);
	}

	public void acceptAST(String sourceFilePath, CompilationUnit ast) {
		String path = relativePath(sourceFilePath);
		System.out.println("Processing file: " + path);

		ast.setProperty(JavaDictionary.SOURCE_FILENAME_PROPERTY, path);
		try {
			ast.accept(new VisitorPackageDef(this.famixDictionnary));
			ast.accept(new VisitorClassMethodDef(this.famixDictionnary, classSummary, anchors));
			ast.accept(new VisitorVarsDef(this.famixDictionnary, classSummary, allLocals, anchors));
			ast.accept(new VisitorComments(this.famixDictionnary, classSummary));

			ast.accept(new VisitorInheritanceRef(this.famixDictionnary, classSummary));
			ast.accept(new VisitorTypeRefRef(this.famixDictionnary, classSummary, anchors));
			ast.accept(new VisitorAccessRef(this.famixDictionnary, classSummary, allLocals, anchors));
			ast.accept(new VisitorInvocRef(this.famixDictionnary, classSummary, anchors));
			ast.accept(new VisitorAnnotationRef(this.famixDictionnary, classSummary));
			ast.accept(new VisitorExceptionRef(this.famixDictionnary, classSummary));

		} catch (Exception err) {
			System.err.println("*** " + getVisitorName(err, path) + " got exception: '" + err + "' while processing file: " + path);
		}
	}

	private String getVisitorName(Exception err, String path) {
		String visitorName = "";
		StackTraceElement[] stack = err.getStackTrace();
		if (stack.length == 0) {
			return "Unknown Class";
		}

		for (int i = 0; (! visitorName.startsWith("Visitor")) && (i < stack.length); i++) {
			visitorName = stack[i].getClassName() + ":" + stack[i].getLineNumber();
			int dot = visitorName.lastIndexOf('.');
			if (dot > 0) {
				visitorName = visitorName.substring(dot + 1);
			}

		}
		if (visitorName.startsWith("Visitor")) {
			return visitorName;
		}
		else {
			return stack[0].getClassName();  // did not find the Visitor in the stack, return the top frame
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
