package fr.inria.verveine.extractor.java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

import ch.akuhn.fame.Repository;
import eu.synectique.verveine.core.gen.famix.Entity;
import eu.synectique.verveine.core.gen.famix.FAMIXModel;
import eu.synectique.verveine.core.gen.famix.JavaSourceLanguage;
import eu.synectique.verveine.core.gen.famix.Namespace;
import eu.synectique.verveine.core.gen.famix.SourceLanguage;

/**
 * A batch parser inspired from org.eclipse.jdt.internal.compiler.batch.Main (JDT-3.6)
 * run with:
 * java -cp lib/org.eclipse.jdt.core_3.6.0.v_A48.jar:../Fame:/usr/local/share/eclipse/plugins/org.eclipse.equinox.common_3.5.1.R35x_v20090807-1100.jar:/usr/local/share/eclipse/plugins/org.eclipse.equinox.preferences_3.2.301.R35x_v20091117.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.jobs_3.4.100.v20090429-1800.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.contenttype_3.4.1.R35x_v20090826-0451.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.resources_3.5.2.R35x_v20091203-1235.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.runtime_3.5.0.v20090525.jar:/usr/local/share/eclipse/plugins/org.eclipse.osgi_3.5.2.R35x_v20100126.jar:../Fame/lib/akuhn-util-r28011.jar:lib/fame.jar:bin eu.synectique.verveine.extractor.java.VerveineJParser [files|directory]_to_parse
 */

public class VerveineJParser {

	/**
	 * Name of the default file where to put the MSE model
	 */
	public final static String OUTPUT_FILE = "output.mse";
	
	public static final String DEFAULT_CODE_VERSION = JavaCore.VERSION_1_5;

	/**
	 * Possible options for SourceAnchors: no source anchor, only entities [default], entities and associations
	 */
	public enum anchorOptions {
		none, entity, assoc;

		public static anchorOptions getValue(String option) {
			switch (option) {
				case "none": return none;
				case "default":
				case "entity": return entity;
				case "assoc": return assoc;
				default: return null;
			}
		}
	}

	/**
	 * TODO remove ?
	 * Whether to summarize collected information at the level of classes or produce everything.
	 * Summarizing at the level of classes does not produce Method, Attributes, or Accesses, Invocation.<br>
	 * Note: classSummary => not allLocals
	 * <p>The general idea is that we create entities (Attribute, Method) "normally", but we don't persist them in the repository.
	 * Then all associations to these entities need to be uplifted as references between their respective classes
	 * e.g. "A.m1() invokes B.m2()" is uplifted to "A references B".</p>
	 * <p>This is actually a dangerous business, because creating entities outside the repository (e.g. an attribute) that have links
	 * to entities inside (e.g. the Type of the attribute) the repository can lead to errors.
	 * More exactly, the problems occur when the entity inside links back to the entity outside.
	 * And since all association are bidirectional, it can happen very easily.</p>
	 */
	protected boolean classSummary = false;

	/**
	 * Whether to output all local variables (even those with primitive type) or not (default is not).<br>
	 * Note: allLocals => not classSummary
	 */
	protected boolean allLocals = false;

	/**
	 * Option: The version of Java expected by the parser
	 */
	protected String codeVers = null;

	/**
	 * Option: Whether to put Sourceanchor in the entities and/or associations
	 */
	protected anchorOptions anchors = null;

	/**
	 * The arguments that were passed to the parser
	 * Needed to relativize the source file names
	 */
	protected Collection<String> argPath;
	protected Collection<String> argFiles;

	/**
	 * pathnames to exclude from parsing.<br>
	 * Accepts globbing expressions
	 */
	protected Collection<String> excludePaths;

	/**
	 * collection of matchers of file name to process excluding expr (see
	 */
	protected Collection<Pattern> excludeMatchers;

	/**
	 * Java parser, provided by JDT
	 */
	protected ASTParser jdtParser = null;

	/**
	 * Name of the file where to put the MSE model.
	 * Defaults to {@link VerveineParser#OUTPUT_FILE}
	 */
	protected String outputFileName;

	protected boolean incrementalParsing = false;

	/**
	 * Famix repository where the entities are stored
	 */
	protected Repository famixRepo;

	protected String[] classPathOptions;

	public VerveineJParser() {
		setOutputFileName(OUTPUT_FILE);
		setFamixRepo(new Repository(FAMIXModel.metamodel()));
		jdtParser = ASTParser.newParser(AST.JLS8);
	}

	protected SourceLanguage getMyLgge() {
		return new JavaSourceLanguage();
	}

	protected static List<String> collectAllJars(String sDir) {
		File[] faFiles = new File(sDir).listFiles();
		List<String> tmpPath = new ArrayList<String>();
		for (File file : faFiles) {
			if (file.getName().endsWith("jar")) {
				tmpPath.add(file.getAbsolutePath());
			}
			if (file.isDirectory()) {
				tmpPath.addAll(collectAllJars(file.getAbsolutePath()));
			}
		}
		return tmpPath;
	}

	/** Reads all jar in classpath from a file, one per line
	 * @param filename of the file containing the jars of the classpath
	 * @return the collection of jar paths
	 */
	protected List<String> readAllJars(String filename) {
		List<String> tmpPath = new ArrayList<String>();
		try {
			BufferedReader fcp = new BufferedReader(new FileReader(filename));
			String jarname = fcp.readLine();
			while (jarname != null) {
				tmpPath.add(jarname);
				jarname = fcp.readLine();
			}
			fcp.close();
		} catch (FileNotFoundException e) {
			System.err.println("** Error classpath file " + filename + " not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("** Error reading classpath file: " + filename);
			e.printStackTrace();
		}
		return tmpPath;
	}

	protected void setOptions(String[] args) {
		classPathOptions = new String[] {};
		argPath = new ArrayList<String>();
		argFiles = new ArrayList<String>();
		excludePaths = new ArrayList<String>();

		int i = 0;
		while (i < args.length && args[i].trim().startsWith("-")) {
			try {
				i += setOption(args, i);
			}
			catch (IllegalArgumentException e) {
				System.err.println(e.getMessage());
				usage();
			}
		}

		while (i < args.length) {
			String arg = args[i++].trim();
			if (arg.endsWith(".java") && new File(arg).isFile()) {
				argFiles.add(arg);
			} else {
				argPath.add(arg);
			}
		}

		jdtParser.setEnvironment(classPathOptions, /*sourcepathEntries*/argPath.toArray(new String[0]), /*encodings*/null, /*includeRunningVMBootclasspath*/true);
		jdtParser.setResolveBindings(true);
		jdtParser.setKind(ASTParser.K_COMPILATION_UNIT);

		Map<String, String> options = JavaCore.getOptions();

		if (codeVers == null) {
			codeVers = DEFAULT_CODE_VERSION;
		}
		if (anchors == null) {
			anchors = anchorOptions.getValue("default");
		}
		options.put(JavaCore.COMPILER_COMPLIANCE, codeVers);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, codeVers);
		options.put(JavaCore.COMPILER_SOURCE, codeVers);

		jdtParser.setCompilerOptions(options);
	}

	/**
	 * treats 1 argument or more starting at position <code>i</code> in the array of arguments <code>args</code>
	 * @return The number of argument(s) treated
	 */
	protected int setOption(String[] args, int i) throws IllegalArgumentException {
		String arg = args[i].trim();
		int argumentsTreated = 1;

		if (arg.equals("-h")) {
			usage();
		}
		else if (arg.matches("-1\\.[1-7]") || arg.matches("-[1-7]")) {
			setCodeVersion(arg);
		}
		else if (arg.equals("-summary")) {
			this.classSummary = true;
			this.allLocals = false;
		}
		else if (arg.equals("-alllocals")) {
			this.classSummary = false;
			this.allLocals = true;
		}
		else if ( (arg.charAt(0) == '-') && (arg.endsWith("cp")) ) {
			classPathOptions = setOptionClassPath(classPathOptions, args, i);
			argumentsTreated++;
		}
		else if (arg.equals("-anchor")) {
			setOptionAnchor(args, i);
			argumentsTreated++;
		}
		else if (arg.equals("-excludepath")) {
			if (i < args.length) {
				excludePaths.add(args[i+1]);
				argumentsTreated++;
			} else {
				throw new IllegalArgumentException("-excludepath requires a globbing expression");
			}
		}
		else if (arg.equals("-o")) {
			if (i < args.length) {
				outputFileName = args[i+1].trim();
				argumentsTreated++;
			} else {
				throw new IllegalArgumentException("-o requires a filename");
			}
		}
		else if (arg.equals("-i")) {
			this.incrementalParsing = true;
		}
		else {
			throw new IllegalArgumentException("** Unrecognized option: " + arg);
		}

		return argumentsTreated;
	}

	protected String[] setOptionClassPath(String[] classPath, String[] args, int i) throws IllegalArgumentException {
		if (args[i].equals("-autocp")) {
			if (i < args.length) {
				return addToClassPath(classPath,collectAllJars(args[i+1]) );
			} else {
				throw new IllegalArgumentException("-autocp requires a root folder");
			}
		}
		else if (args[i].equals("-filecp")) {
			if (i < args.length) {
				return addToClassPath(classPath, readAllJars(args[i+1]));
			} else {
				throw new IllegalArgumentException("-filecp requires a filename");
			}
		}
		else if (args[i].equals("-cp")) {
			if (i < args.length) {
				return addToClassPath(classPath,  Arrays.asList(args[i+1].split(System.getProperty("path.separator"))));
			}
			else {
				throw new IllegalArgumentException("-cp requires a classPath");
			}	
		}
		return classPath;
	}

	protected void setOptionAnchor(String[] args, int i) {
		if (i < args.length) {
			String anchor = args[i+1].trim();
			this.anchors = anchorOptions.getValue(anchor);
			if (this.anchors == null) {
				throw new IllegalArgumentException("unknown option to -anchor: "+anchor);
			}
		} else {
			throw new IllegalArgumentException("-anchor requires an option (none|default|assoc)");
		}
	}


	/**
	 * @param classPath
	 * @param tmpPath
	 * @return
	 */
	protected String[] addToClassPath(String[] classPath, List<String> tmpPath) {
		int oldlength = classPath.length;
		int newlength = oldlength + tmpPath.size();
		classPath = Arrays.copyOf(classPath, newlength);
		for (int p = oldlength; p < newlength; p++) {
			classPath[p] = tmpPath.get(p - oldlength);
		}
		return classPath;
	}

	protected void usage() {
		/* possible enhancements:
		 * (1) allow to not generate some info
		 * -nodep = do not create dependencies (access, reference, invocation)
		 * -novar (or -noleaf) = do not create "variables", including attributes. Implies not creating accesses
		 * -nobehavior = do not create methods. Implies not creating invocations
		 * (2) allow to summarize some info
		 * -classdep = generate dependencies between classes not between their members. Implies not creating accesses, reference, invocation but instead
		 *   some new relation: classdep
		 */
		
		System.err.println("Usage: VerveineJ [-h] [-i] [-o <output-file-name>] [-summary] [-alllocals] [-anchor (none|default|assoc)] [-cp CLASSPATH | -autocp DIR] [-1.1 | -1 | -1.2 | -2 | ... | -1.7 | -7] <files-to-parse> | <dirs-to-parse>");
		System.err.println("      [-h] prints this message");
		System.err.println("      [-i] toggles incremental parsing on (can parse a project in parts that are added to the output file)");
		System.err.println("      [-o <output-file-name>] specifies the name of the output file (default: "+OUTPUT_FILE+")");
		System.err.println("      [-summary] toggles summarization of information at the level of classes.");
		System.err.println("                 Summarizing at the level of classes does not produce Methods, Attributes, Accesses, and Invocations");
		System.err.println("                 Everything is represented as references between classes: e.g. \"A.m1() invokes B.m2()\" is uplifted to \"A references B\"");	
		System.err.println("      [-alllocals] Forces outputing all local variables, even those with primitive type (incompatible with \"-summary\")");
		System.err.println("      [-anchor (none|entity|default|assoc)] options for source anchor information:\n" +
				   "                                     - no entity\n" +
				   "                                     - only named entities [default]\n" +
				   "                                     - named entities+associations (i.e. accesses, invocations, references)");
		System.err.println("      [-cp CLASSPATH] classpath where to look for stubs");
		System.err.println("      [-autocp DIR] gather all jars in DIR and put them in the classpath");
		System.err.println("      [-filecp FILE] gather all jars listed in FILE (absolute paths) and put them in the classpath");
		System.err.println("      [-excludepath GLOBBINGEXPR] A globbing expression of file path to exclude from parsing");
		System.err.println("      [-1.1 | -1 | -1.2 | -2 | ... | -1.7 | -7] specifies version of Java");
		System.err.println("      <files-to-parse>|<dirs-to-parse> list of source files to parse or directories to search for source files");
		System.exit(0);

	}

	protected void setCodeVersion(String arg) {
		if (codeVers != null) {
			System.err.println("Trying to set twice code versions: " + codeVers + " and " + arg);
			usage();
		} else if (arg.equals("-1.1") || arg.equals("-1")) {
			codeVers = JavaCore.VERSION_1_1;
		} else if (arg.equals("-1.2") || arg.equals("-2")) {
			codeVers = JavaCore.VERSION_1_2;
		} else if (arg.equals("-1.3") || arg.equals("-3")) {
			codeVers = JavaCore.VERSION_1_3;
		} else if (arg.equals("-1.4") || arg.equals("-4")) {
			codeVers = JavaCore.VERSION_1_4;
		} else if (arg.equals("-1.5") || arg.equals("-5")) {
			codeVers = JavaCore.VERSION_1_5;
		} else if (arg.equals("-1.6") || arg.equals("-6")) {
			codeVers = JavaCore.VERSION_1_6;
		} else if (arg.equals("-1.7") || arg.equals("-7")) {
			codeVers = JavaCore.VERSION_1_7;
		}

	}

	protected void collectJavaFiles(Collection<String> paths, Collection<String> files) {
		excludeMatchers = new ArrayList<>(excludePaths.size());
		for (String expr : excludePaths) {
			excludeMatchers.add(createMatcher(expr));
		}
		for (String p : paths) {
			collectJavaFiles(new File(p), files);
		}
	}

	protected void collectJavaFiles(File f, Collection<String> files) {
		for (Pattern filter : excludeMatchers) {
			if (filter.matcher(f.getName()).matches()) {
				return;
			}
		}
		if (f.isFile() && f.getName().endsWith(".java")) {
			files.add(f.getAbsolutePath());
		} else if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				collectJavaFiles(child, files);
			}
		}
	}

	/**
	 * Creates a regexp matcher form a globbing expression<br>
	 * Glob to Regexp algorithm from <a href="https://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns">https://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns</a>
	 */
	protected Pattern createMatcher(String expr) {
		expr = expr.trim();
		int strLen = expr.length();
		StringBuilder sb = new StringBuilder(strLen);
		sb.append('^');
		if (! expr.startsWith("/")) {
			// not absolute path, start with ".*"
			if (! expr.startsWith("*")) {
				sb.append(".*");
			}
		}
		boolean escaping = false;
		int inCurlies = 0;
		for (char currentChar : expr.toCharArray()) {
			switch (currentChar) {
				case '*':
					if (escaping)
						sb.append("\\*");
					else
						sb.append(".*");
					escaping = false;
					break;
				case '?':
					if (escaping)
						sb.append("\\?");
					else
						sb.append('.');
					escaping = false;
					break;
				case '.':
				case '(':
				case ')':
				case '+':
				case '|':
				case '^':
				case '$':
				case '@':
				case '%':
					sb.append('\\');
					sb.append(currentChar);
					escaping = false;
					break;
				case '\\':
					if (escaping) {
						sb.append("\\\\");
						escaping = false;
					}
					else
						escaping = true;
					break;
				case '{':
					if (escaping) {
						sb.append("\\{");
					}
					else {
						sb.append('(');
						inCurlies++;
					}
					escaping = false;
					break;
				case '}':
					if (inCurlies > 0 && !escaping) {
						sb.append(')');
						inCurlies--;
					}
					else if (escaping)
						sb.append("\\}");
					else
						sb.append("}");
					escaping = false;
					break;
				case ',':
					if (inCurlies > 0 && !escaping) {
						sb.append('|');
					}
					else if (escaping)
						sb.append("\\,");
					else
						sb.append(",");
					break;
				default:
					escaping = false;
					sb.append(currentChar);
			}
		}

		if (! expr.endsWith("*")) {
			sb.append(".*$");
		}
		else {
			sb.append('$');
		}
		return Pattern.compile(sb.toString());
	}


	public void parse() {
		ArrayList<String> sourceFiles = new ArrayList<String>();

		if (this.linkToExisting()) {
			this.expandNamespacesNames();
		}

		FamixRequestor req = new FamixRequestor(getFamixRepo(), argPath, argFiles, classSummary, allLocals, anchors);

		sourceFiles.addAll(argFiles);
		collectJavaFiles(argPath, sourceFiles);

		try {
			jdtParser.createASTs(sourceFiles.toArray(new String[0]), /*encodings*/null, /*bindingKeys*/new String[0], /*requestor*/req, /*monitor*/null);
		}
		catch (java.lang.IllegalStateException e) {
			System.out.println("VerveineJ could not launch parser, received error: " + e.getMessage());
		}

		this.compressNamespacesNames();
	}

	/**
	 * As explained in JavaDictionary, Namespaces are created with their fully qualified name.
	 * We need now to give them their simple name
	 */
	protected void compressNamespacesNames() {
		for (Namespace ns : listAll(Namespace.class)) {
			String name = ns.getName();
			int last = name.lastIndexOf('.');
			if (last >= 0) {
				ns.setName(name.substring(last + 1));
			}
		}
	}

	/**
	 * @see VerveineJParser#compressNamespacesNames()
	 */
	protected void expandNamespacesNames() {
		for (Namespace ns : listAll(Namespace.class)) {
			expandNamespaceName(ns);
		}
	}

	protected void expandNamespaceName(Namespace ns) {
		String name = ns.getName();
		if (name.indexOf('.') > 0) {
			return;
		} else {
			Namespace parent = (Namespace) ns.getParentScope();
			if (parent == null) {
				return;
			} else {
				expandNamespaceName(parent);
				ns.setName(parent.getName() + "." + ns.getName());
			}
		}
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	protected boolean linkToExisting() {
		File existingMSE = new File(OUTPUT_FILE);
		if (existingMSE.exists() && this.incrementalParsing) {
			this.getFamixRepo().importMSEFile(OUTPUT_FILE);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * "closes" the repository, by adding to it a SourceLanguage entity if their is none.
	 * The SourceLanguage entity is the one returned by getMyLgge().
	 * Also outputs repository to a MSE file
	 */
	public void emitMSE() {
		this.emitMSE(this.outputFileName);
	}

	public void emitMSE(String outputFile) {
		try {
			emitMSE(new FileOutputStream(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void emitMSE(OutputStream output) {
		// Adds default SourceLanguage for the repository
		if ( (listAll(SourceLanguage.class).size() == 0) && (getMyLgge() != null) ) {
			getFamixRepo().add( getMyLgge());
		}
	
		// Outputting to a file
		try {
			//famixRepo.exportMSE(new FileWriter(OUTPUT_FILE));
			famixRepo.exportMSE(new BufferedWriter(new OutputStreamWriter(output,"UTF8")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a Collection of all FAMIXEntities in the repository of the given fmxClass
	 */
	public <T extends Entity> Collection<T> listAll(Class<T> fmxClass) {
		return getFamixRepo().all(fmxClass);
	}

	public Repository getFamixRepo() {
		return famixRepo;
	}

	public void setFamixRepo(Repository famixRepo) {
		this.famixRepo = famixRepo;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

}
