// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TParameterizedTypeUser")
public interface TParameterizedTypeUser {

    @FameProperty(name = "argumentsInParameterizedTypes", opposite = "arguments", derived = true)
    Collection<TWithParameterizedTypeUsers> getArgumentsInParameterizedTypes();

    void setArgumentsInParameterizedTypes(Collection<? extends TWithParameterizedTypeUsers> argumentsInParameterizedTypes);

    void addArgumentsInParameterizedTypes(TWithParameterizedTypeUsers one);

    void addArgumentsInParameterizedTypes(TWithParameterizedTypeUsers one, TWithParameterizedTypeUsers... many);

    void addArgumentsInParameterizedTypes(Iterable<? extends TWithParameterizedTypeUsers> many);

    void addArgumentsInParameterizedTypes(TWithParameterizedTypeUsers[] many);

    int numberOfArgumentsInParameterizedTypes();

    boolean hasArgumentsInParameterizedTypes();


}

