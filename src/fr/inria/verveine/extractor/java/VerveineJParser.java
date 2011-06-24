package fr.inria.verveine.extractor.java;

import java.io.File;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

import fr.inria.verveine.core.VerveineParser;
import fr.inria.verveine.core.gen.famix.JavaSourceLanguage;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.ParameterizedType;
import fr.inria.verveine.core.gen.famix.SourceLanguage;
import fr.inria.verveine.core.gen.famix.Type;

/**
 * A batch parser inspired from org.eclipse.jdt.internal.compiler.batch.Main (JDT-3.6)
 * run with:
 * java -cp lib/org.eclipse.jdt.core_3.6.0.v_A48.jar:../Fame:/usr/local/share/eclipse/plugins/org.eclipse.equinox.common_3.5.1.R35x_v20090807-1100.jar:/usr/local/share/eclipse/plugins/org.eclipse.equinox.preferences_3.2.301.R35x_v20091117.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.jobs_3.4.100.v20090429-1800.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.contenttype_3.4.1.R35x_v20090826-0451.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.resources_3.5.2.R35x_v20091203-1235.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.runtime_3.5.0.v20090525.jar:/usr/local/share/eclipse/plugins/org.eclipse.osgi_3.5.2.R35x_v20100126.jar:../Fame/lib/akuhn-util-r28011.jar:lib/fame.jar:bin fr.inria.verveine.extractor.java.VerveineJParser [files|directory]_to_parse
 */

public class VerveineJParser extends VerveineParser {

	public static final String DEFAULT_CODE_VERSION = JavaCore.VERSION_1_5;


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
				int j = super.setOption(i, args);
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

		//tracePath("classPath", classPath);
		//tracePath("sourcepath", argPath.toArray(new String[0]));
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
		System.err.println("Usage: VerveineJ [-h] [-cp CLASSPATH] [-1.1 | -1 | -1.2 | -2 | ... | -1.7 | -7] <files-to-parse> | <dirs-to-parse>");
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

	public static void main(String[] args) {
		VerveineJParser parser = new VerveineJParser();
		parser.setOptions(args);
		parser.parse();
		//parser.debug();
		parser.emitMSE(parser.getOutputFileName());
	}

	private void debug() {
		ParameterizedType m = new ParameterizedType();
		Type t = new Type();
		m.setName("toto()");
		m.addArguments(t);
		getFamixRepo().add(m);
		t.setName("Toto");
	}

	public void parse() {
		ArrayList<String> sourceFiles = new ArrayList<String>();

		if (this.linkToExisting()) {
			this.expandNamespacesNames();
		}

		FamixRequestor req = new FamixRequestor(getFamixRepo(), argPath, argFiles);

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


}
