// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TTraitUser")
public interface TTraitUser {

    @FameProperty(name = "outgoingTraitUsages", opposite = "user", derived = true)
    Collection<TTraitUsage> getOutgoingTraitUsages();

    void setOutgoingTraitUsages(Collection<? extends TTraitUsage> outgoingTraitUsages);

    void addOutgoingTraitUsages(TTraitUsage one);

    void addOutgoingTraitUsages(TTraitUsage one, TTraitUsage... many);

    void addOutgoingTraitUsages(Iterable<? extends TTraitUsage> many);

    void addOutgoingTraitUsages(TTraitUsage[] many);

    int numberOfOutgoingTraitUsages();

    boolean hasOutgoingTraitUsages();


}

