// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithCaughtExceptions")
public interface TWithCaughtExceptions  {

        @FameProperty(name = "caughtExceptions", opposite = "definingEntity", derived = true)
    public Collection<TCaughtException> getCaughtExceptions();

    public void setCaughtExceptions(Collection<? extends TCaughtException> caughtExceptions);

    public void addCaughtExceptions(TCaughtException one);

    public void addCaughtExceptions(TCaughtException one, TCaughtException... many);

    public void addCaughtExceptions(Iterable<? extends TCaughtException> many);

    public void addCaughtExceptions(TCaughtException[] many);

    public int numberOfCaughtExceptions();

    public boolean hasCaughtExceptions();



}

