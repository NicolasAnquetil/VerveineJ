// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TParameterizedTypeUser")
public interface TParameterizedTypeUser  {

        @FameProperty(name = "argumentsInParameterizedTypes", opposite = "arguments", derived = true)
    public Collection<TWithParameterizedTypeUsers> getArgumentsInParameterizedTypes();

    public void setArgumentsInParameterizedTypes(Collection<? extends TWithParameterizedTypeUsers> argumentsInParameterizedTypes);

    public void addArgumentsInParameterizedTypes(TWithParameterizedTypeUsers one);

    public void addArgumentsInParameterizedTypes(TWithParameterizedTypeUsers one, TWithParameterizedTypeUsers... many);

    public void addArgumentsInParameterizedTypes(Iterable<? extends TWithParameterizedTypeUsers> many);

    public void addArgumentsInParameterizedTypes(TWithParameterizedTypeUsers[] many);

    public int numberOfArgumentsInParameterizedTypes();

    public boolean hasArgumentsInParameterizedTypes();



}

