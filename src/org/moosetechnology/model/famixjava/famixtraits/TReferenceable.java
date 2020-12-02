// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TReferenceable")
public interface TReferenceable {

    @FameProperty(name = "incomingReferences", opposite = "referredType", derived = true)
    Collection<TReference> getIncomingReferences();

    void setIncomingReferences(Collection<? extends TReference> incomingReferences);

    void addIncomingReferences(TReference one);

    void addIncomingReferences(TReference one, TReference... many);

    void addIncomingReferences(Iterable<? extends TReference> many);

    void addIncomingReferences(TReference[] many);

    int numberOfIncomingReferences();

    boolean hasIncomingReferences();


}

