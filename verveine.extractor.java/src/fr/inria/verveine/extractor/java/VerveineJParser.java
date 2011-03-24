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

	/**
	 * The arguments that were passed to the parser
	 * Needed to relativize the source file names
	 */
	private Collection<String> sourceFiles;
	
	private ASTParser jdtParser = null;
	
	public VerveineJParser() {
		super();

		jdtParser = ASTParser.newParser(AST.JLS3);
	}
	
	public void setOptions(String[] args) {
		// we assume java 1.5 code for now, this should be configurable
		@SuppressWarnings("unchecked")
		Map<String,String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
		jdtParser.setCompilerOptions(options);

		Collection<String> classPath = new ArrayList<String>();
		Collection<String> sourcePath = new ArrayList<String>();
		sourceFiles = new ArrayList<String>();
		for (int i=0; i < args.length; i++) {
			String current = args[i];
			if (current.equals("-cp")) {
				classPath.add(args[++i]);
			}
			else if (current.endsWith(".java")) {
				sourceFiles.add(current);
			}
			else {
				sourcePath.add(current);
			}
		}
		
		jdtParser.setEnvironment(classPath.toArray(new String[0]), sourcePath.toArray(new String[0]), null, true);
		jdtParser.setResolveBindings(true);
		jdtParser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		collectJavaFiles(sourcePath, sourceFiles);
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
		if (this.linkToExisting()) {
			this.expandNamespacesNames();
		}

		FamixRequestor req = new FamixRequestor(getFamixRepo(), new String[0]);
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
