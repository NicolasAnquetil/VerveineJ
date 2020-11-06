// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import ch.akuhn.fame.internal.MultivalueSet;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TTraitUsage;
import org.moosetechnology.model.famix.famixtraits.TTrait;
import org.moosetechnology.model.famix.famixtraits.TWithTraits;


@FamePackage("FAMIX")
@FameDescription("Trait")
public class Trait extends Type implements TTrait {

    private Collection<TTraitUsage> incomingTraitUsages; 

    private TWithTraits traitOwner;
    


    @FameProperty(name = "incomingTraitUsages", opposite = "trait", derived = true)
    public Collection<TTraitUsage> getIncomingTraitUsages() {
        if (incomingTraitUsages == null) {
            incomingTraitUsages = new MultivalueSet<TTraitUsage>() {
                @Override
                protected void clearOpposite(TTraitUsage e) {
                    e.setTrait(null);
                }
                @Override
                protected void setOpposite(TTraitUsage e) {
                    e.setTrait(Trait.this);
                }
            };
        }
        return incomingTraitUsages;
    }
    
    public void setIncomingTraitUsages(Collection<? extends TTraitUsage> incomingTraitUsages) {
        this.getIncomingTraitUsages().clear();
        this.getIncomingTraitUsages().addAll(incomingTraitUsages);
    }                    
    
        
    public void addIncomingTraitUsages(TTraitUsage one) {
        this.getIncomingTraitUsages().add(one);
    }   
    
    public void addIncomingTraitUsages(TTraitUsage one, TTraitUsage... many) {
        this.getIncomingTraitUsages().add(one);
        for (TTraitUsage each : many)
            this.getIncomingTraitUsages().add(each);
    }   
    
    public void addIncomingTraitUsages(Iterable<? extends TTraitUsage> many) {
        for (TTraitUsage each : many)
            this.getIncomingTraitUsages().add(each);
    }   
                
    public void addIncomingTraitUsages(TTraitUsage[] many) {
        for (TTraitUsage each : many)
            this.getIncomingTraitUsages().add(each);
    }
    
    public int numberOfIncomingTraitUsages() {
        return getIncomingTraitUsages().size();
    }

    public boolean hasIncomingTraitUsages() {
        return !getIncomingTraitUsages().isEmpty();
    }

    @FameProperty(name = "traitOwner", opposite = "traits")
    public TWithTraits getTraitOwner() {
        return traitOwner;
    }

    public void setTraitOwner(TWithTraits traitOwner) {
        if (this.traitOwner != null) {
            if (this.traitOwner.equals(traitOwner)) return;
            this.traitOwner.getTraits().remove(this);
        }
        this.traitOwner = traitOwner;
        if (traitOwner == null) return;
        traitOwner.getTraits().add(this);
    }
    


}

