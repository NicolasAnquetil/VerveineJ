package fr.inria.verveine.extractor.java;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

import fr.inria.verveine.core.VerveineParser;
import fr.inria.verveine.core.gen.famix.Namespace;

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
	
	private ASTParser jdtParser = null;
	
	public VerveineJParser() {
		super();

		jdtParser = ASTParser.newParser(AST.JLS3);
	}
	
	public void setOptions(String[] args) {
		Collection<String> classPath = new ArrayList<String>();
		argPath = new ArrayList<String>();
		argFiles = new ArrayList<String>();

        int i = 0;
        while (i < args.length && args[i].startsWith("-")) {
        	String arg;
            arg = args[i++];

            if (arg.equals("-h")) {
            	usage();
            }
            else if (arg.matches("-1\\.[1-7]") || arg.matches("-[1-7]")) {
            	setCodeVersion(arg);
            }
            else if (arg.equals("-cp")) {
                if (i < args.length) {
                	classPath.add(args[++i]);
                }
                else {
                	System.err.println("-cp requires a classPath");
                }

            }
        }
        while (i < args.length) {
        	String arg;
            arg = args[i++];
        	if ( arg.endsWith(".java") && new File(arg).isFile() ) {
        		argFiles.add(arg);
        	}
        	else {
        		argPath.add(arg);
        	}
        }

		jdtParser.setEnvironment(classPath.toArray(new String[0]), argPath.toArray(new String[0]), null, true);
		jdtParser.setResolveBindings(true);
		jdtParser.setKind(ASTParser.K_COMPILATION_UNIT);

		@SuppressWarnings("unchecked")
		Map<String,String> options = JavaCore.getOptions();
		
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
			System.err.println("Trying to set twice code versions: "+ codeVers + " and "+ arg);
			usage();
		}
		else if (arg.equals("-1.1") || arg.equals("-1")) {
			codeVers = JavaCore.VERSION_1_1;
		}
		else if (arg.equals("-1.2") || arg.equals("-2")) {
			codeVers = JavaCore.VERSION_1_2;
		}
		else if (arg.equals("-1.3") || arg.equals("-3")) {
			codeVers = JavaCore.VERSION_1_3;
		}
		else if (arg.equals("-1.4") || arg.equals("-4")) {
			codeVers = JavaCore.VERSION_1_4;
		}
		else if (arg.equals("-1.5") || arg.equals("-5")) {
			codeVers = JavaCore.VERSION_1_5;
		}
		else if (arg.equals("-1.6") || arg.equals("-6")) {
			codeVers = JavaCore.VERSION_1_6;
		}
		else if (arg.equals("-1.7") || arg.equals("-7")) {
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
		}
		else if (f.isDirectory()){
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
		parser.outputMSE();
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
	protected void compressNamespacesNames() {
		for (Namespace ns : listAll(Namespace.class)) {
			String name = ns.getName();
			int last = name.lastIndexOf('.');
			if (last >= 0) {
				ns.setName(name.substring(last+1));
			}
		}
	}

	/**
	 * @see VerveineJParser.compressNamespacesNames()
	 */
	protected void expandNamespacesNames() {
		for (Namespace ns : listAll(Namespace.class)) {
			expandNamespaceName(ns);
		}		
	}
	
	private void expandNamespaceName(Namespace ns) {
		String name = ns.getName();
		if (name.indexOf('.') > 0) {
			return;
		}
		else {
			Namespace parent = (Namespace) ns.getParentScope();
			if (parent == null) {
				return;
			}
			else {
				expandNamespaceName(parent);
				ns.setName(parent.getName()+"."+ns.getName());
			}
		}
	}
	
}
