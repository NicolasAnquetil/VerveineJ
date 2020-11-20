package fr.inria.verveine.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;

import ch.akuhn.fame.Repository;
import eu.synectique.verveine.core.gen.famix.Entity;
import eu.synectique.verveine.core.gen.famix.FAMIXModel;
import eu.synectique.verveine.core.gen.famix.SourceLanguage;

public abstract class VerveineParser {

	/**
	 * Name of the default file where to put the MSE model
	 */
	public final static String OUTPUT_FILE = "output.mse";
	
	/**
	 * Name of the file where to put the MSE model.
	 * Defaults to {@link VerveineParser#OUTPUT_FILE}
	 */
	private String outputFileName;   // name of the MSE file to output the model
	private boolean incrementalParsing = false;   // if true we add the model to an existing one (found in 'outputFileName')


	/**
	 * Famix repository where the entities are stored
	 */
	private Repository famixRepo;

	public VerveineParser() {
		setOutputFileName(OUTPUT_FILE);
		setFamixRepo(new Repository(FAMIXModel.metamodel()));
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public boolean linkToExisting() {
		File existingMSE = new File(OUTPUT_FILE);
		if (existingMSE.exists() && this.incrementalParsing) {
			this.getFamixRepo().importMSEFile(OUTPUT_FILE);
			return true;
		}
		else {
			return false;
		}
	}
	
	abstract protected SourceLanguage getMyLgge();

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

	/** Try to deal with the "current" argument in 'args'
	 * @param i -- the indice in 'args' of the current argument
	 * @param args -- an array of the arguments
	 * @return how many arguments were consumed (accepted)
	 */
	public int setOption(int i, String[] args) {
		String arg = args[i];
		if (arg.equals("-o")) {
			if (i < args.length) {
				outputFileName = args[i+1];
				return 2;
			} else {
				System.err.println("-o requires a filename");
			}
		}
		else if (arg.equals("-i")) {
			this.incrementalParsing = true;
			return 1;
		}

		return 0;  // no argument consumed
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