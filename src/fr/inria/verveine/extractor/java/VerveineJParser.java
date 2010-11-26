package fr.inria.verveine.extractor.java;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.internal.compiler.batch.FileSystem.Classpath;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

import fr.inria.verveine.core.VerveineParser;
import fr.inria.verveine.core.gen.famix.Namespace;

/**
 * A batch parser inspired from org.eclipse.jdt.internal.compiler.batch.Main (JDT-3.6)
 * run with:
 * java -cp lib/org.eclipse.jdt.core_3.6.0.v_A48.jar:../Fame:/usr/local/share/eclipse/plugins/org.eclipse.equinox.common_3.5.1.R35x_v20090807-1100.jar:/usr/local/share/eclipse/plugins/org.eclipse.equinox.preferences_3.2.301.R35x_v20091117.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.jobs_3.4.100.v20090429-1800.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.contenttype_3.4.1.R35x_v20090826-0451.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.resources_3.5.2.R35x_v20091203-1235.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.runtime_3.5.0.v20090525.jar:/usr/local/share/eclipse/plugins/org.eclipse.osgi_3.5.2.R35x_v20100126.jar:../Fame/lib/akuhn-util-r28011.jar:lib/fame.jar:bin fr.inria.verveine.extractor.java.VerveineJParser [files|directory]_to_parse
 */

public class VerveineJParser extends VerveineParser {

	public static void main(String[] args) {
		VerveineJParser parser = new VerveineJParser();
		parser.compile(args);
		parser.renameNamespaces();
		parser.outputMSE();
	}

	/**
	 * As explained in JavaDictionary, Namespaces are created with their fully qualified name
	 * We need now to give them their simple name
	 */
	public void renameNamespaces() {
		for (Namespace ns : listAll(Namespace.class)) {
			String name = ns.getName();
			int last = name.lastIndexOf('.');
			if (last >= 0) {
				ns.setName(name.substring(last+1));
			}
		}
	}

	/*
	 *  Low-level API performing the actual parsing
	 *  Overwrite the one in org.eclipse.jdt.internal.compiler.batch.Main;
	 *  SHOULD NOT USE an internal JDT class. But I don't know how to do it otherwise
	 */
	public void performCompilation() {

		this.compilerOptions = new CompilerOptions(this.options);
		this.compilerOptions.performMethodsFullRecovery = false;
		this.compilerOptions.performStatementsRecovery = false;

		// NA --- beginning of parsing code --------------------------------------------------
		String[] tmpclasspath=null;
		if (this.checkedClasspaths!=null) {
			tmpclasspath = new String[this.checkedClasspaths.length];
			int i = 0;
			for (Classpath cp : this.checkedClasspaths) {
				tmpclasspath[i++] = cp.getPath();
			}
		}

		ASTParser pars = ASTParser.newParser(AST.JLS3);
		pars.setEnvironment(/*classpathEntries*/tmpclasspath,
				/*sourcepathEntries*/ new String[0],  // TODO this might be wrong. What if the user specifies some "-sourcepath" when calling Verveine?
				/*encodings*/null, 
				/*includeRunningVMBootclasspath*/true);
		pars.setResolveBindings(true);
		pars.setKind(ASTParser.K_COMPILATION_UNIT);
		pars.createASTs(/*sourceFilePaths*/this.filenames, 
				/*encodings*/this.encodings, 
				/*bindingKeys*/new String[0], 
				/*requestor*/new FamixRequestor(getFamixRepo()), 
				/*monitor*/null);
		// NA --- end of parsing code --------------------------------------------------
	}
	
}
