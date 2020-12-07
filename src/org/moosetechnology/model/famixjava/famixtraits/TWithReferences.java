// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithReferences")
public interface TWithReferences {

    @FameProperty(name = "outgoingReferences", opposite = "referencer", derived = true)
    Collection<TReference> getOutgoingReferences();

    void setOutgoingReferences(Collection<? extends TReference> outgoingReferences);

    void addOutgoingReferences(TReference one);

    void addOutgoingReferences(TReference one, TReference... many);

    void addOutgoingReferences(Iterable<? extends TReference> many);

    void addOutgoingReferences(TReference[] many);

    int numberOfOutgoingReferences();

    boolean hasOutgoingReferences();


}

