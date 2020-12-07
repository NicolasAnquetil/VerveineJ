// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TAccessible")
public interface TAccessible {

    @FameProperty(name = "numberOfAccesses", derived = true)
    Number getNumberOfAccesses();

    @FameProperty(name = "numberOfLocalAccesses", derived = true)
    Number getNumberOfLocalAccesses();

    @FameProperty(name = "accessors", derived = true)
    Collection<TWithAccesses> getAccessors();

    @FameProperty(name = "numberOfAccessingMethods", derived = true)
    Number getNumberOfAccessingMethods();

    @FameProperty(name = "numberOfAccessingClasses", derived = true)
    Number getNumberOfAccessingClasses();

    @FameProperty(name = "numberOfGlobalAccesses", derived = true)
    Number getNumberOfGlobalAccesses();

    @FameProperty(name = "incomingAccesses", opposite = "variable", derived = true)
    Collection<TAccess> getIncomingAccesses();

    void setIncomingAccesses(Collection<? extends TAccess> incomingAccesses);

    void addIncomingAccesses(TAccess one);

    void addIncomingAccesses(TAccess one, TAccess... many);

    void addIncomingAccesses(Iterable<? extends TAccess> many);

    void addIncomingAccesses(TAccess[] many);

    int numberOfIncomingAccesses();

    boolean hasIncomingAccesses();


}

