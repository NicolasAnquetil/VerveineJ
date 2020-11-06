package fr.inria.verveine.extractor.core;

import ch.akuhn.fame.Repository;
import org.moosetechnology.model.famix.famix.Entity;
import org.moosetechnology.model.famix.famix.FAMIXModel;
import org.moosetechnology.model.famix.famix.SourceLanguage;
import org.moosetechnology.model.famix.famixreplication.FamixReplicationModel;

import java.io.*;
import java.util.Collection;

public abstract class VerveineParser {

    public static final String OUTPUT_FILE = "output.mse";
    private String outputFileName;
    private boolean incrementalParsing = false;
    private Repository famixRepo;

    public VerveineParser() {
        this.setOutputFileName("output.mse");
        this.setFamixRepo(new Repository(FAMIXModel.metamodel()));
    }

    public void setOutputFileName(String var1) {
        this.outputFileName = var1;
    }

    public boolean linkToExisting() {
        File var1 = new File("output.mse");
        if (var1.exists() && this.incrementalParsing) {
            this.getFamixRepo().importMSEFile("output.mse");
            return true;
        } else {
            return false;
        }
    }

    protected abstract SourceLanguage getMyLgge();

    public void emitMSE() {
        this.emitMSE(this.outputFileName);
    }

    public void emitMSE(String var1) {
        try {
            this.emitMSE((OutputStream)(new FileOutputStream(var1)));
        } catch (FileNotFoundException var3) {
            var3.printStackTrace();
        }

    }

    public void emitMSE(OutputStream var1) {
        if (this.listAll(SourceLanguage.class).size() == 0 && this.getMyLgge() != null) {
            this.getFamixRepo().add(this.getMyLgge());
        }

        try {
            this.famixRepo.exportMSE(new BufferedWriter(new OutputStreamWriter(var1, "UTF8")));
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public int setOption(int var1, String[] var2) {
        String var3 = var2[var1];
        if (var3.equals("-o")) {
            if (var1 < var2.length) {
                this.outputFileName = var2[var1 + 1];
                return 2;
            }

            System.err.println("-o requires a filename");
        } else if (var3.equals("-i")) {
            this.incrementalParsing = true;
            return 1;
        }

        return 0;
    }

    public <T extends Entity> Collection<T> listAll(Class<T> var1) {
        return this.getFamixRepo().all(var1);
    }

    public Repository getFamixRepo() {
        return this.famixRepo;
    }

    public void setFamixRepo(Repository var1) {
        this.famixRepo = var1;
    }

    public String getOutputFileName() {
        return this.outputFileName;
    }
}
