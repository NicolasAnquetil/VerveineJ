// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TTraitUser")
public interface TTraitUser  {

        @FameProperty(name = "outgoingTraitUsages", opposite = "user", derived = true)
    public Collection<TTraitUsage> getOutgoingTraitUsages();

    public void setOutgoingTraitUsages(Collection<? extends TTraitUsage> outgoingTraitUsages);

    public void addOutgoingTraitUsages(TTraitUsage one);

    public void addOutgoingTraitUsages(TTraitUsage one, TTraitUsage... many);

    public void addOutgoingTraitUsages(Iterable<? extends TTraitUsage> many);

    public void addOutgoingTraitUsages(TTraitUsage[] many);

    public int numberOfOutgoingTraitUsages();

    public boolean hasOutgoingTraitUsages();



}

