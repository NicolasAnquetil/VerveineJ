package fr.inria.verveine.extractor.java;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import ch.akuhn.fame.Repository;
import fr.inria.verveine.extractor.java.defvisitors.VisitorAnnotationDef;
import fr.inria.verveine.extractor.java.defvisitors.VisitorClassMethodDef;
import fr.inria.verveine.extractor.java.defvisitors.VisitorComments;
import fr.inria.verveine.extractor.java.defvisitors.VisitorPackageDef;
import fr.inria.verveine.extractor.java.defvisitors.VisitorVarsDef;
import fr.inria.verveine.extractor.java.refvisitors.VisitorAccessRef;
import fr.inria.verveine.extractor.java.refvisitors.VisitorAnnotationRef;
import fr.inria.verveine.extractor.java.refvisitors.VisitorInheritanceRef;
import fr.inria.verveine.extractor.java.refvisitors.VisitorInvocRef;
import fr.inria.verveine.extractor.java.refvisitors.VisitorTypeRefRef;

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
	 * what sourceAnchors to create
	 */
	private String anchors;

	/**
	 * Maps the arguments (file names or dir names) to their absolute path (well actually it is the other way around)
	 */
	protected Map<String, String> dirMap;
	protected Map<String, String> fileMap;

	public FamixRequestor(Repository r, Collection<String> argsDir, Collection<String> argsFile, boolean classSummary,
			boolean allLocals, String anchors) {
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

		this.famixDictionnary = new JavaDictionary(famixRepo);
	}

	public void acceptAST(String sourceFilePath, CompilationUnit ast) {
		String visitorName = "";
		String path = relativePath(sourceFilePath);
		System.out.println("Processing file: " + path);

		ast.setProperty(JavaDictionary.SOURCE_FILENAME_PROPERTY, path);
		try {
			visitorName = "PackageDef"; // for debugging (see catch clause)
			ast.accept(new VisitorPackageDef(this.famixDictionnary, allLocals, anchors));
			visitorName = "Class&MethodDef"; // for debugging (see catch clause)
			ast.accept(new VisitorClassMethodDef(this.famixDictionnary, classSummary, allLocals, anchors));
			visitorName = "AnnotationDef"; // for debugging (see catch clause)
			ast.accept(new VisitorAnnotationDef(this.famixDictionnary, classSummary, allLocals, anchors));
			visitorName = "VarDef"; // for debugging (see catch clause)
			ast.accept(new VisitorVarsDef(this.famixDictionnary, classSummary, allLocals, anchors));
			visitorName = "Comments"; // for debugging (see catch clause)
			ast.accept(new VisitorComments(this.famixDictionnary));

			visitorName = "Inheritances"; // for debugging (see catch clause)
			ast.accept(new VisitorInheritanceRef(this.famixDictionnary));
			visitorName = "TypeReferences"; // for debugging (see catch clause)
			ast.accept(new VisitorTypeRefRef(this.famixDictionnary, classSummary, anchors));
			visitorName = "Accesses"; // for debugging (see catch clause)
			ast.accept(new VisitorAccessRef(this.famixDictionnary, classSummary, anchors));
			visitorName = "Invocations"; // for debugging (see catch clause)
			ast.accept(new VisitorInvocRef(this.famixDictionnary, classSummary, anchors));
			visitorName = "AnnotationRef"; // for debugging (see catch clause)
			ast.accept(new VisitorAnnotationRef(this.famixDictionnary, classSummary));

		} catch (Exception e) {
			System.err.println("*** Visitor "+ visitorName + " got exception: '" + e + "' while processing file: " + path);
			e.printStackTrace();
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
