// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithThrownExceptions")
public interface TWithThrownExceptions {

    @FameProperty(name = "thrownExceptions", opposite = "definingEntity", derived = true)
    Collection<TThrownException> getThrownExceptions();

    void setThrownExceptions(Collection<? extends TThrownException> thrownExceptions);

    void addThrownExceptions(TThrownException one);

    void addThrownExceptions(TThrownException one, TThrownException... many);

    void addThrownExceptions(Iterable<? extends TThrownException> many);

    void addThrownExceptions(TThrownException[] many);

    int numberOfThrownExceptions();

    boolean hasThrownExceptions();


}

