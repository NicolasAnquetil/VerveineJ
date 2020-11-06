// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import org.moosetechnology.model.famix.famixtraits.TFileSystemEntity;
import ch.akuhn.fame.internal.MultivalueSet;
import org.moosetechnology.model.famix.famixtraits.TWithFiles;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TCompilationUnit;
import org.moosetechnology.model.famix.famixtraits.TFolder;
import org.moosetechnology.model.famix.famixtraits.TWithCompilationUnit;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import org.moosetechnology.model.famix.famixtraits.TDefinedInModule;
import org.moosetechnology.model.famix.famixtraits.TFile;
import org.moosetechnology.model.famix.famixtraits.TModule;


@FamePackage("FAMIX")
@FameDescription("CompilationUnit")
public class CompilationUnit extends CFile implements TFileSystemEntity, TFile, TDefinedInModule, TCompilationUnit {

    private TWithCompilationUnit compilationUnitOwner;
    
    private Collection<TWithFiles> entities; 

    private TFolder parentFolder;
    
    private TModule parentModule;
    


    @FameProperty(name = "compilationUnitOwner", opposite = "compilationUnit")
    public TWithCompilationUnit getCompilationUnitOwner() {
        return compilationUnitOwner;
    }

    public void setCompilationUnitOwner(TWithCompilationUnit compilationUnitOwner) {
        if (this.compilationUnitOwner == null ? compilationUnitOwner != null : !this.compilationUnitOwner.equals(compilationUnitOwner)) {
            TWithCompilationUnit old_compilationUnitOwner = this.compilationUnitOwner;
            this.compilationUnitOwner = compilationUnitOwner;
            if (old_compilationUnitOwner != null) old_compilationUnitOwner.setCompilationUnit(null);
            if (compilationUnitOwner != null) compilationUnitOwner.setCompilationUnit(this);
        }
    }
    
    @FameProperty(name = "numberOfLinesOfText", derived = true)
    public Number getNumberOfLinesOfText() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "averageNumberOfCharactersPerLine", derived = true)
    public Number getAverageNumberOfCharactersPerLine() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "entities", opposite = "containerFiles", derived = true)
    public Collection<TWithFiles> getEntities() {
        if (entities == null) {
            entities = new MultivalueSet<TWithFiles>() {
                @Override
                protected void clearOpposite(TWithFiles e) {
                    e.getContainerFiles().remove(CompilationUnit.this);
                }
                @Override
                protected void setOpposite(TWithFiles e) {
                    e.getContainerFiles().add(CompilationUnit.this);
                }
            };
        }
        return entities;
    }
    
    public void setEntities(Collection<? extends TWithFiles> entities) {
        this.getEntities().clear();
        this.getEntities().addAll(entities);
    }
    
    public void addEntities(TWithFiles one) {
        this.getEntities().add(one);
    }   
    
    public void addEntities(TWithFiles one, TWithFiles... many) {
        this.getEntities().add(one);
        for (TWithFiles each : many)
            this.getEntities().add(each);
    }   
    
    public void addEntities(Iterable<? extends TWithFiles> many) {
        for (TWithFiles each : many)
            this.getEntities().add(each);
    }   
                
    public void addEntities(TWithFiles[] many) {
        for (TWithFiles each : many)
            this.getEntities().add(each);
    }
    
    public int numberOfEntities() {
        return getEntities().size();
    }

    public boolean hasEntities() {
        return !getEntities().isEmpty();
    }

    @FameProperty(name = "numberOfCharacters", derived = true)
    public Number getNumberOfCharacters() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOfBytes", derived = true)
    public Number getNumberOfBytes() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "parentFolder", opposite = "childrenFileSystemEntities", container = true)
    public TFolder getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(TFolder parentFolder) {
        if (this.parentFolder != null) {
            if (this.parentFolder.equals(parentFolder)) return;
            this.parentFolder.getChildrenFileSystemEntities().remove(this);
        }
        this.parentFolder = parentFolder;
        if (parentFolder == null) return;
        parentFolder.getChildrenFileSystemEntities().add(this);
    }
    
    @FameProperty(name = "numberOfEmptyLinesOfText", derived = true)
    public Number getNumberOfEmptyLinesOfText() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOfKiloBytes", derived = true)
    public Number getNumberOfKiloBytes() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "totalNumberOfLinesOfText", derived = true)
    public Number getTotalNumberOfLinesOfText() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "parentModule", opposite = "moduleEntities")
    public TModule getParentModule() {
        return parentModule;
    }

    public void setParentModule(TModule parentModule) {
        if (this.parentModule != null) {
            if (this.parentModule.equals(parentModule)) return;
            this.parentModule.getModuleEntities().remove(this);
        }
        this.parentModule = parentModule;
        if (parentModule == null) return;
        parentModule.getModuleEntities().add(this);
    }
    


}

