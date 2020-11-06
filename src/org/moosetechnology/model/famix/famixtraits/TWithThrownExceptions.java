// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithThrownExceptions")
public interface TWithThrownExceptions  {

        @FameProperty(name = "thrownExceptions", opposite = "definingEntity", derived = true)
    public Collection<TThrownException> getThrownExceptions();

    public void setThrownExceptions(Collection<? extends TThrownException> thrownExceptions);

    public void addThrownExceptions(TThrownException one);

    public void addThrownExceptions(TThrownException one, TThrownException... many);

    public void addThrownExceptions(Iterable<? extends TThrownException> many);

    public void addThrownExceptions(TThrownException[] many);

    public int numberOfThrownExceptions();

    public boolean hasThrownExceptions();



}

