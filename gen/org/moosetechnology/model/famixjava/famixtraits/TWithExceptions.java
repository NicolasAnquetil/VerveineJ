// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import java.util.*;


@FamePackage("Famix-Traits")
@FameDescription("TWithExceptions")
public interface TWithExceptions  {

        @FameProperty(name = "exceptions", opposite = "exceptionClass", derived = true)
    public Collection<TException> getExceptions();

    public void setExceptions(Collection<? extends TException> exceptions);

    public void addExceptions(TException one);

    public void addExceptions(TException one, TException... many);

    public void addExceptions(Iterable<? extends TException> many);

    public void addExceptions(TException[] many);

    public int numberOfExceptions();

    public boolean hasExceptions();



}

