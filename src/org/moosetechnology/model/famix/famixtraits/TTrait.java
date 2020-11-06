// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TTrait")
public interface TTrait  {

        @FameProperty(name = "traitOwner", opposite = "traits")
    public TWithTraits getTraitOwner();

    public void setTraitOwner(TWithTraits traitOwner);

    @FameProperty(name = "incomingTraitUsages", opposite = "trait", derived = true)
    public Collection<TTraitUsage> getIncomingTraitUsages();

    public void setIncomingTraitUsages(Collection<? extends TTraitUsage> incomingTraitUsages);

    public void addIncomingTraitUsages(TTraitUsage one);

    public void addIncomingTraitUsages(TTraitUsage one, TTraitUsage... many);

    public void addIncomingTraitUsages(Iterable<? extends TTraitUsage> many);

    public void addIncomingTraitUsages(TTraitUsage[] many);

    public int numberOfIncomingTraitUsages();

    public boolean hasIncomingTraitUsages();



}

