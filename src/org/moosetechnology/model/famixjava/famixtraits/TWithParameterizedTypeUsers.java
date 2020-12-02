// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithParameterizedTypeUsers")
public interface TWithParameterizedTypeUsers {

    @FameProperty(name = "arguments", opposite = "argumentsInParameterizedTypes")
    Collection<TParameterizedTypeUser> getArguments();

    void setArguments(Collection<? extends TParameterizedTypeUser> arguments);

    void addArguments(TParameterizedTypeUser one);

    void addArguments(TParameterizedTypeUser one, TParameterizedTypeUser... many);

    void addArguments(Iterable<? extends TParameterizedTypeUser> many);

    void addArguments(TParameterizedTypeUser[] many);

    int numberOfArguments();

    boolean hasArguments();


}

