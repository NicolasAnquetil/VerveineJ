// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TTrait")
public interface TTrait {

    @FameProperty(name = "traitOwner", opposite = "traits")
    TWithTraits getTraitOwner();

    void setTraitOwner(TWithTraits traitOwner);

    @FameProperty(name = "incomingTraitUsages", opposite = "trait", derived = true)
    Collection<TTraitUsage> getIncomingTraitUsages();

    void setIncomingTraitUsages(Collection<? extends TTraitUsage> incomingTraitUsages);

    void addIncomingTraitUsages(TTraitUsage one);

    void addIncomingTraitUsages(TTraitUsage one, TTraitUsage... many);

    void addIncomingTraitUsages(Iterable<? extends TTraitUsage> many);

    void addIncomingTraitUsages(TTraitUsage[] many);

    int numberOfIncomingTraitUsages();

    boolean hasIncomingTraitUsages();


}

