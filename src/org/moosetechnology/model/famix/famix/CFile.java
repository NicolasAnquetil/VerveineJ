// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import org.moosetechnology.model.famix.famixtraits.TFileInclude;
import ch.akuhn.fame.internal.MultivalueSet;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TWithFileIncludes;


@FamePackage("FAMIX")
@FameDescription("CFile")
public class CFile extends File implements TWithFileIncludes {

    private Collection<TFileInclude> outgoingIncludeRelations; 

    private Collection<TFileInclude> incomingIncludeRelations; 



    @FameProperty(name = "includingFiles", derived = true)
    public Collection<CFile> getIncludingFiles() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
        
    @FameProperty(name = "includedFiles", derived = true)
    public Collection<CFile> getIncludedFiles() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
        
    @FameProperty(name = "outgoingIncludeRelations", opposite = "source", derived = true)
    public Collection<TFileInclude> getOutgoingIncludeRelations() {
        if (outgoingIncludeRelations == null) {
            outgoingIncludeRelations = new MultivalueSet<TFileInclude>() {
                @Override
                protected void clearOpposite(TFileInclude e) {
                    e.setSource(null);
                }
                @Override
                protected void setOpposite(TFileInclude e) {
                    e.setSource(CFile.this);
                }
            };
        }
        return outgoingIncludeRelations;
    }
    
    public void setOutgoingIncludeRelations(Collection<? extends TFileInclude> outgoingIncludeRelations) {
        this.getOutgoingIncludeRelations().clear();
        this.getOutgoingIncludeRelations().addAll(outgoingIncludeRelations);
    }                    
    
        
    public void addOutgoingIncludeRelations(TFileInclude one) {
        this.getOutgoingIncludeRelations().add(one);
    }   
    
    public void addOutgoingIncludeRelations(TFileInclude one, TFileInclude... many) {
        this.getOutgoingIncludeRelations().add(one);
        for (TFileInclude each : many)
            this.getOutgoingIncludeRelations().add(each);
    }   
    
    public void addOutgoingIncludeRelations(Iterable<? extends TFileInclude> many) {
        for (TFileInclude each : many)
            this.getOutgoingIncludeRelations().add(each);
    }   
                
    public void addOutgoingIncludeRelations(TFileInclude[] many) {
        for (TFileInclude each : many)
            this.getOutgoingIncludeRelations().add(each);
    }
    
    public int numberOfOutgoingIncludeRelations() {
        return getOutgoingIncludeRelations().size();
    }

    public boolean hasOutgoingIncludeRelations() {
        return !getOutgoingIncludeRelations().isEmpty();
    }

    @FameProperty(name = "incomingIncludeRelations", opposite = "target", derived = true)
    public Collection<TFileInclude> getIncomingIncludeRelations() {
        if (incomingIncludeRelations == null) {
            incomingIncludeRelations = new MultivalueSet<TFileInclude>() {
                @Override
                protected void clearOpposite(TFileInclude e) {
                    e.setTarget(null);
                }
                @Override
                protected void setOpposite(TFileInclude e) {
                    e.setTarget(CFile.this);
                }
            };
        }
        return incomingIncludeRelations;
    }
    
    public void setIncomingIncludeRelations(Collection<? extends TFileInclude> incomingIncludeRelations) {
        this.getIncomingIncludeRelations().clear();
        this.getIncomingIncludeRelations().addAll(incomingIncludeRelations);
    }                    
    
        
    public void addIncomingIncludeRelations(TFileInclude one) {
        this.getIncomingIncludeRelations().add(one);
    }   
    
    public void addIncomingIncludeRelations(TFileInclude one, TFileInclude... many) {
        this.getIncomingIncludeRelations().add(one);
        for (TFileInclude each : many)
            this.getIncomingIncludeRelations().add(each);
    }   
    
    public void addIncomingIncludeRelations(Iterable<? extends TFileInclude> many) {
        for (TFileInclude each : many)
            this.getIncomingIncludeRelations().add(each);
    }   
                
    public void addIncomingIncludeRelations(TFileInclude[] many) {
        for (TFileInclude each : many)
            this.getIncomingIncludeRelations().add(each);
    }
    
    public int numberOfIncomingIncludeRelations() {
        return getIncomingIncludeRelations().size();
    }

    public boolean hasIncomingIncludeRelations() {
        return !getIncomingIncludeRelations().isEmpty();
    }



}

