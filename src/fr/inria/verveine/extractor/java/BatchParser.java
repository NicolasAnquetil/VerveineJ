package fr.inria.verveine.extractor.java;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Map;

import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.internal.compiler.batch.FileSystem.Classpath;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

import ch.akuhn.fame.Repository;

import fr.inria.verveine.core.gen.famix.FAMIXModel;


//import fr.inria.verveine.NonCloseablePrintStream;

/**
 * A batch parser inspired from org.eclipse.jdt.internal.compiler.batch.Main (JDT-3.6)
 * run with:
 * java -cp lib/org.eclipse.jdt.core_3.6.0.v_A48.jar:../Fame:/usr/local/share/eclipse/plugins/org.eclipse.equinox.common_3.5.1.R35x_v20090807-1100.jar:/usr/local/share/eclipse/plugins/org.eclipse.equinox.preferences_3.2.301.R35x_v20091117.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.jobs_3.4.100.v20090429-1800.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.contenttype_3.4.1.R35x_v20090826-0451.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.resources_3.5.2.R35x_v20091203-1235.jar:/usr/local/share/eclipse/plugins/org.eclipse.core.runtime_3.5.0.v20090525.jar:/usr/local/share/eclipse/plugins/org.eclipse.osgi_3.5.2.R35x_v20100126.jar:../Fame/lib/akuhn-util-r28011.jar:lib/fame.jar:bin fr.inria.verveine.parser.BatchParser [files|directory]_to_parse
 */

public class BatchParser extends Main {
	
	private Repository famixRepo;

	public static void main(String[] args) {
		new BatchParser(new PrintWriter(System.out), new PrintWriter(System.err), true/*systemExit*/, null/*options*/, null/*progress*/).compile(args);
	}

	public BatchParser(PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished, @SuppressWarnings("rawtypes") Map customDefaultOptions, CompilationProgress compilationProgress) {
		super(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, compilationProgress);
	}

	/*
	 *  Low-level API performing the actual parsing
	 *  Overwrite the one in org.eclipse.jdt.internal.compiler.batch.Main;
	 */
	public void performCompilation() {

		famixRepo = new Repository(FAMIXModel.metamodel());

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
				/*requestor*/new FamixRequestor(famixRepo), 
				/*monitor*/null);
		// NA --- end of parsing code --------------------------------------------------

		/* 
		 * NA --- Outputting --------------------------------------------------
		 * To stdout
		 * Repository.exportMSE() closes the stream when it finishes, we don't want this
		 * The NonCloseOutputStream class prevents that by redefining an empty close() that does nothing
		 * (particularly not closing the underlying System.out)
		 */
		//famixRepo.exportMSE( new NonCloseablePrintStream(System.out));
		//System.out.println();
		
		
		// outputting to a file
		try {
			famixRepo.exportMSE(new FileWriter("output.mse"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Repository getFamixRepo() {
		return famixRepo;
	}
	
}
