// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import org.moosetechnology.model.famix.famixtraits.TWithSourceLanguage;
import ch.akuhn.fame.internal.MultivalueSet;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TSourceLanguage;


@FamePackage("FAMIX")
@FameDescription("SourceLanguage")
public class SourceLanguage extends Entity implements TSourceLanguage {

    private Collection<TWithSourceLanguage> sourcedEntities; 



    @FameProperty(name = "name", derived = true)
    public String getName() {
        // TODO: this is a derived property, implement this method manually.
        return "java";
    }
    
    @FameProperty(name = "sourcedEntities", opposite = "declaredSourceLanguage", derived = true)
    public Collection<TWithSourceLanguage> getSourcedEntities() {
        if (sourcedEntities == null) {
            sourcedEntities = new MultivalueSet<TWithSourceLanguage>() {
                @Override
                protected void clearOpposite(TWithSourceLanguage e) {
                    e.setDeclaredSourceLanguage(null);
                }
                @Override
                protected void setOpposite(TWithSourceLanguage e) {
                    e.setDeclaredSourceLanguage(SourceLanguage.this);
                }
            };
        }
        return sourcedEntities;
    }
    
    public void setSourcedEntities(Collection<? extends TWithSourceLanguage> sourcedEntities) {
        this.getSourcedEntities().clear();
        this.getSourcedEntities().addAll(sourcedEntities);
    }                    
    
        
    public void addSourcedEntities(TWithSourceLanguage one) {
        this.getSourcedEntities().add(one);
    }   
    
    public void addSourcedEntities(TWithSourceLanguage one, TWithSourceLanguage... many) {
        this.getSourcedEntities().add(one);
        for (TWithSourceLanguage each : many)
            this.getSourcedEntities().add(each);
    }   
    
    public void addSourcedEntities(Iterable<? extends TWithSourceLanguage> many) {
        for (TWithSourceLanguage each : many)
            this.getSourcedEntities().add(each);
    }   
                
    public void addSourcedEntities(TWithSourceLanguage[] many) {
        for (TWithSourceLanguage each : many)
            this.getSourcedEntities().add(each);
    }
    
    public int numberOfSourcedEntities() {
        return getSourcedEntities().size();
    }

    public boolean hasSourcedEntities() {
        return !getSourcedEntities().isEmpty();
    }



}

