// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithFunctions")
public interface TWithFunctions {

    @FameProperty(name = "functions", opposite = "functionOwner", derived = true)
    Collection<TFunction> getFunctions();

    void setFunctions(Collection<? extends TFunction> functions);

    void addFunctions(TFunction one);

    void addFunctions(TFunction one, TFunction... many);

    void addFunctions(Iterable<? extends TFunction> many);

    void addFunctions(TFunction[] many);

    int numberOfFunctions();

    boolean hasFunctions();


}

