// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import ch.akuhn.fame.internal.MultivalueSet;
import org.moosetechnology.model.famix.famixtraits.TWithFiles;
import org.moosetechnology.model.famix.famixtraits.TSourceAnchor;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TComment;
import org.moosetechnology.model.famix.famixtraits.TWithSourceLanguage;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import org.moosetechnology.model.famix.famixtraits.TWithComments;
import org.moosetechnology.model.famix.famixtraits.TSourceLanguage;
import org.moosetechnology.model.famix.famixtraits.TFile;
import org.moosetechnology.model.famix.famixtraits.TSourceEntity;
import org.moosetechnology.model.famix.famixreplication.Replica;


@FamePackage("FAMIX")
@FameDescription("SourcedEntity")
public class SourcedEntity extends Entity implements TWithFiles, TSourceEntity, TWithSourceLanguage, TWithComments {

    private Collection<TComment> comments; 

    private Collection<TFile> containerFiles; 

    private Number numberOfLinesOfCode;
    
    private Boolean isStub;
    
    private TSourceLanguage declaredSourceLanguage;
    
    private TSourceAnchor sourceAnchor;
    


    @FameProperty(name = "numberOfJavaNullChecks", derived = true)
    public Number getNumberOfJavaNullChecks() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "comments", opposite = "container", derived = true)
    public Collection<TComment> getComments() {
        if (comments == null) {
            comments = new MultivalueSet<TComment>() {
                @Override
                protected void clearOpposite(TComment e) {
                    e.setContainer(null);
                }
                @Override
                protected void setOpposite(TComment e) {
                    e.setContainer(SourcedEntity.this);
                }
            };
        }
        return comments;
    }
    
    public void setComments(Collection<? extends TComment> comments) {
        this.getComments().clear();
        this.getComments().addAll(comments);
    }                    
    
        
    public void addComments(TComment one) {
        this.getComments().add(one);
    }   
    
    public void addComments(TComment one, TComment... many) {
        this.getComments().add(one);
        for (TComment each : many)
            this.getComments().add(each);
    }   
    
    public void addComments(Iterable<? extends TComment> many) {
        for (TComment each : many)
            this.getComments().add(each);
    }   
                
    public void addComments(TComment[] many) {
        for (TComment each : many)
            this.getComments().add(each);
    }
    
    public int numberOfComments() {
        return getComments().size();
    }

    public boolean hasComments() {
        return !getComments().isEmpty();
    }

    @FameProperty(name = "numberOfComments", derived = true)
    public Number getNumberOfComments() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "duplicationRate", derived = true)
    public Number getDuplicationRate() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "hasComments", derived = true)
    public Boolean getHasComments() {
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

    @FameProperty(name = "replicas", derived = true)
    public Replica getReplicas() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOfLinesOfCodeWithMoreThanOneCharacter", derived = true)
    public Number getNumberOfLinesOfCodeWithMoreThanOneCharacter() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOfLinesOfCode")
    public Number getNumberOfLinesOfCode() {
        return numberOfLinesOfCode;
    }

    public void setNumberOfLinesOfCode(Number numberOfLinesOfCode) {
        this.numberOfLinesOfCode = numberOfLinesOfCode;
    }
    
    @FameProperty(name = "sourceText", derived = true)
    public String getSourceText() {
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
    
    @FameProperty(name = "declaredSourceLanguage", opposite = "sourcedEntities")
    public TSourceLanguage getDeclaredSourceLanguage() {
        return declaredSourceLanguage;
    }

    public void setDeclaredSourceLanguage(TSourceLanguage declaredSourceLanguage) {
        if (this.declaredSourceLanguage != null) {
            if (this.declaredSourceLanguage.equals(declaredSourceLanguage)) return;
            this.declaredSourceLanguage.getSourcedEntities().remove(this);
        }
        this.declaredSourceLanguage = declaredSourceLanguage;
        if (declaredSourceLanguage == null) return;
        declaredSourceLanguage.getSourcedEntities().add(this);
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
    
    @FameProperty(name = "containsReplicas", derived = true)
    public Boolean getContainsReplicas() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    


}

