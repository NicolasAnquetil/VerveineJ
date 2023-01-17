// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.internal.MultivalueSet;
import java.util.*;

import org.moosetechnology.model.famix.famixreplication.Replica;
import org.moosetechnology.model.famix.famixtraits.TFile;
import org.moosetechnology.model.famix.famixtraits.TSourceAnchor;
import org.moosetechnology.model.famix.famixtraits.TSourceEntity;
import org.moosetechnology.model.famix.famixtraits.TWithFiles;


@FamePackage("Famix-Java-Entities")
@FameDescription("SourcedEntity")
public class SourcedEntity extends Entity implements TSourceEntity, TWithFiles {

    private Collection<TFile> containerFiles; 

    private Boolean isStub;
    
    private Number numberOfLinesOfCode;
    
    private TSourceAnchor sourceAnchor;
    


    @FameProperty(name = "numberOfJavaNullChecks", derived = true)
    public Number getNumberOfJavaNullChecks() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "containerFiles", opposite = "entities")
    public Collection<TFile> getContainerFiles() {
        if (containerFiles == null) {
            containerFiles = new MultivalueSet<TFile>() {
                @Override
                protected void clearOpposite(TFile e) {
                    e.getEntities().remove(SourcedEntity.this);
                }
                @Override
                protected void setOpposite(TFile e) {
                    e.getEntities().add(SourcedEntity.this);
                }
            };
        }
        return containerFiles;
    }
    
    public void setContainerFiles(Collection<? extends TFile> containerFiles) {
        this.getContainerFiles().clear();
        this.getContainerFiles().addAll(containerFiles);
    }
    
    public void addContainerFiles(TFile one) {
        this.getContainerFiles().add(one);
    }   
    
    public void addContainerFiles(TFile one, TFile... many) {
        this.getContainerFiles().add(one);
        for (TFile each : many)
            this.getContainerFiles().add(each);
    }   
    
    public void addContainerFiles(Iterable<? extends TFile> many) {
        for (TFile each : many)
            this.getContainerFiles().add(each);
    }   
                
    public void addContainerFiles(TFile[] many) {
        for (TFile each : many)
            this.getContainerFiles().add(each);
    }
    
    public int numberOfContainerFiles() {
        return getContainerFiles().size();
    }

    public boolean hasContainerFiles() {
        return !getContainerFiles().isEmpty();
    }

    @FameProperty(name = "containsReplicas", derived = true)
    public Boolean getContainsReplicas() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "duplicationRate", derived = true)
    public Number getDuplicationRate() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "isStub")
    public Boolean getIsStub() {
        return isStub;
    }

    public void setIsStub(Boolean isStub) {
        this.isStub = isStub;
    }
    
    @FameProperty(name = "numberOfLinesOfCode")
    public Number getNumberOfLinesOfCode() {
        return numberOfLinesOfCode;
    }

    public void setNumberOfLinesOfCode(Number numberOfLinesOfCode) {
        this.numberOfLinesOfCode = numberOfLinesOfCode;
    }
    
    @FameProperty(name = "numberOfLinesOfCodeWithMoreThanOneCharacter", derived = true)
    public Number getNumberOfLinesOfCodeWithMoreThanOneCharacter() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "replicas", derived = true)
    public Replica getReplicas() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "sourceAnchor", opposite = "element", derived = true)
    public TSourceAnchor getSourceAnchor() {
        return sourceAnchor;
    }

    public void setSourceAnchor(TSourceAnchor sourceAnchor) {
        if (this.sourceAnchor == null ? sourceAnchor != null : !this.sourceAnchor.equals(sourceAnchor)) {
            TSourceAnchor old_sourceAnchor = this.sourceAnchor;
            this.sourceAnchor = sourceAnchor;
            if (old_sourceAnchor != null) old_sourceAnchor.setElement(null);
            if (sourceAnchor != null) sourceAnchor.setElement(this);
        }
    }
    
    @FameProperty(name = "sourceText", derived = true)
    public String getSourceText() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    


}

