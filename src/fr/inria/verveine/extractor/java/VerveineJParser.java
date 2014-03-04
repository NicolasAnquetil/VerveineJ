package fr.inria.verveine.extractor.java;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

import eu.synectique.licence.LicenceChecker;
import fr.inria.verveine.core.VerveineParser;
import fr.inria.verveine.core.gen.famix.JavaSourceLanguage;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.SourceLanguage;

/**
 * A batch parser inspired from org.eclipse.jdt.internal.compiler.batch.Main (JDT-3.6)
 * run with:
 * java -cp lib/org.eclipse.jdt.core_3.6.0.v_A48.jar:../Fame:/usr/local/share/eclipse/plugins/org.eclipse.equinox.common_3.5.1.R35x_v20090807-1100.jar:/usr/local/share/eclipse/plugins/org.eclipse.equinox.preferences_3.2.301.R35x_v20091117.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.jobs_3.4.100.v20090429-1800.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.contenttype_3.4.1.R35x_v20090826-0451.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.resources_3.5.2.R35x_v20091203-1235.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.runtime_3.5.0.v20090525.jar:/usr/local/share/eclipse/plugins/org.eclipse.osgi_3.5.2.R35x_v20100126.jar:../Fame/lib/akuhn-util-r28011.jar:lib/fame.jar:bin fr.inria.verveine.extractor.java.VerveineJParser [files|directory]_to_parse
 */

public class VerveineJParser extends VerveineParser {

	public static final String DEFAULT_CODE_VERSION = JavaCore.VERSION_1_5;

	/**
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
	private boolean classSummary = false;

	/**
	 * Whether to output all local variables (even those with primitive type or not (default is not).<br>
	 * Note: allLocals => not classSummary
	 */
	private boolean allLocals = false;

	/**
	 * Option: The version of Java expected by the parser 
	 */
	protected String codeVers = null;

	/**
	 * The arguments that were passed to the parser
	 * Needed to relativize the source file names
	 */
	private Collection<String> argPath;
	private Collection<String> argFiles;

	/**
	 * Java parser, provided by JDT
	 */
	private ASTParser jdtParser = null;

	public VerveineJParser() {
		super();

		jdtParser = ASTParser.newParser(AST.JLS3);
	}

	protected SourceLanguage getMyLgge() {
		return new JavaSourceLanguage();
	}

	public static void printFnames(String sDir) {
		File[] faFiles = new File(sDir).listFiles();
		for (File file : faFiles) {
			if (file.getName().matches("^(.*jar)")) {
				System.out.println(file.getAbsolutePath());
			}
			if (file.isDirectory()) {
				printFnames(file.getAbsolutePath());
			}
		}
	}

	public static List<String> collectAllJars(String sDir) {
		File[] faFiles = new File(sDir).listFiles();
		List<String> tmpPath = new ArrayList<String>();
		for (File file : faFiles) {
			if (file.getName().endsWith("jar")) {
				tmpPath.add(file.getAbsolutePath());
				System.out.println(file.getAbsolutePath());
			}
			if (file.isDirectory()) {
				tmpPath.addAll(collectAllJars(file.getAbsolutePath()));
			}
		}
		return tmpPath;
	}

	public void setOptions(String[] args) {

		String[] classPath = new String[] { };
		argPath = new ArrayList<String>();
		argFiles = new ArrayList<String>();

		int i = 0;
		while (i < args.length && args[i].startsWith("-")) {
			String arg = args[i++];

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
			else if (arg.equals("-autocp")) {
				if (i < args.length) {
					List<String> tmpPath = collectAllJars(args[i++]);
					int oldlength = classPath.length;
					int newlength = oldlength + tmpPath.size();
					classPath = Arrays.copyOf(classPath, newlength);
					for (int p = oldlength; p < newlength; p++) {
						classPath[p] = tmpPath.get(p - oldlength);
					}
				} else {
					System.err.println("-autocp requires a root folder");
				}
			}
			else if (arg.equals("-cp")) {
				if (i < args.length) {
					String[] tmpPath = args[i++].split(System.getProperty("path.separator"));
					int oldlength = classPath.length;
					int newlength = oldlength + tmpPath.length;
					classPath = Arrays.copyOf(classPath, newlength);
					for (int p=oldlength; p < newlength; p++) {
						classPath[p] = tmpPath[p-oldlength];
					}
				}
				else {
					System.err.println("-cp requires a classPath");
				}	
			}
			else {
				int j = super.setOption(i - 1, args);
				if (j > 0) {     // j is the number of args consumed
					i += (j-1);  // 1 more will be added at the beginning of the loop ("args[i++]")
				}
			}
		}
		while (i < args.length) {
			String arg;
			arg = args[i++];
			if (arg.endsWith(".java") && new File(arg).isFile()) {
				argFiles.add(arg);
			} else {
				argPath.add(arg);
			}
		}

		jdtParser.setEnvironment(classPath, argPath.toArray(new String[0]), null, true);
		jdtParser.setResolveBindings(true);
		jdtParser.setKind(ASTParser.K_COMPILATION_UNIT);

		@SuppressWarnings("unchecked")
		Map<String, String> options = JavaCore.getOptions();

		if (codeVers == null) {
			codeVers = DEFAULT_CODE_VERSION;
		}
		options.put(JavaCore.COMPILER_COMPLIANCE, codeVers);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, codeVers);
		options.put(JavaCore.COMPILER_SOURCE, codeVers);

		jdtParser.setCompilerOptions(options);
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
		
		System.err.println("Usage: VerveineJ [-h] [-i] [-o <output-file-name>] [-summary] [-alllocals] [-cp CLASSPATH | -autocp DIR] [-1.1 | -1 | -1.2 | -2 | ... | -1.7 | -7] <files-to-parse> | <dirs-to-parse>");
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

	private void collectJavaFiles(Collection<String> paths, Collection<String> files) {
		for (String p : paths) {
			collectJavaFiles(new File(p), files);
		}
	}

	private void collectJavaFiles(File f, Collection<String> files) {
		if (f.isFile() && f.getName().endsWith(".java")) {
			files.add(f.getAbsolutePath());
		} else if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				collectJavaFiles(child, files);
			}
		}
		// else ignore it?

	}

	public void parse() {
		ArrayList<String> sourceFiles = new ArrayList<String>();

		if (this.linkToExisting()) {
			this.expandNamespacesNames();
		}

		FamixRequestor req = new FamixRequestor(getFamixRepo(), argPath, argFiles, classSummary, allLocals);

		sourceFiles.addAll(argFiles);
		collectJavaFiles(argPath, sourceFiles);
		jdtParser.createASTs(sourceFiles.toArray(new String[0]), null, new String[0], req, null);

		this.compressNamespacesNames();
	}

	/**
	 * As explained in JavaDictionary, Namespaces are created with their fully qualified name.
	 * We need now to give them their simple name
	 */
	private void compressNamespacesNames() {
		for (Namespace ns : listAll(Namespace.class)) {
			String name = ns.getName();
			int last = name.lastIndexOf('.');
			if (last >= 0) {
				ns.setName(name.substring(last + 1));
			}
		}
	}

	/**
	 * @see VerveineJParser.compressNamespacesNames()
	 */
	private void expandNamespacesNames() {
		for (Namespace ns : listAll(Namespace.class)) {
			expandNamespaceName(ns);
		}
	}

	private void expandNamespaceName(Namespace ns) {
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

	public static void main(String[] args) {
		LicenceChecker checker = new LicenceChecker();
		int licenceCheck = checker.checkLicence();
		
		if (licenceCheck != LicenceChecker.OK) {
			cannotContinue(checker, licenceCheck);
		}
		
		VerveineJParser parser = new VerveineJParser();
		parser.setOptions(args);
		parser.parse();
		//parser.debug();
		parser.emitMSE();
	}

	/**
	 * @param checker
	 * @param licenceCheck 
	 */
	private static void cannotContinue(LicenceChecker checker, int licenceCheck) {
		System.err.println("Authentication failure VerveineJ cannot continue");
		if (licenceCheck == LicenceChecker.WRONG_LICENCE) {
			System.err.println("    " + checker.getLineRead());
		}
		else {
			System.err.println("    error " + licenceCheck);			
		}
		
		System.exit(0);
	}

}
