// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithExceptions")
public interface TWithExceptions {

    @FameProperty(name = "exceptions", opposite = "exceptionClass", derived = true)
    Collection<TException> getExceptions();

    void setExceptions(Collection<? extends TException> exceptions);

    void addExceptions(TException one);

    void addExceptions(TException one, TException... many);

    void addExceptions(Iterable<? extends TException> many);

    void addExceptions(TException[] many);

    int numberOfExceptions();

    boolean hasExceptions();


}

