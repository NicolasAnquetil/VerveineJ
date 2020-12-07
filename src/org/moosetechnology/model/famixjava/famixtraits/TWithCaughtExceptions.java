// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithCaughtExceptions")
public interface TWithCaughtExceptions {

    @FameProperty(name = "caughtExceptions", opposite = "definingEntity", derived = true)
    Collection<TCaughtException> getCaughtExceptions();

    void setCaughtExceptions(Collection<? extends TCaughtException> caughtExceptions);

    void addCaughtExceptions(TCaughtException one);

    void addCaughtExceptions(TCaughtException one, TCaughtException... many);

    void addCaughtExceptions(Iterable<? extends TCaughtException> many);

    void addCaughtExceptions(TCaughtException[] many);

    int numberOfCaughtExceptions();

    boolean hasCaughtExceptions();


}

