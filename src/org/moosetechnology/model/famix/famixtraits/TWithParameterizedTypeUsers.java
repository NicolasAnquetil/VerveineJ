// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithParameterizedTypeUsers")
public interface TWithParameterizedTypeUsers  {

        @FameProperty(name = "arguments", opposite = "argumentsInParameterizedTypes")
    public Collection<TParameterizedTypeUser> getArguments();

    public void setArguments(Collection<? extends TParameterizedTypeUser> arguments);

    public void addArguments(TParameterizedTypeUser one);

    public void addArguments(TParameterizedTypeUser one, TParameterizedTypeUser... many);

    public void addArguments(Iterable<? extends TParameterizedTypeUser> many);

    public void addArguments(TParameterizedTypeUser[] many);

    public int numberOfArguments();

    public boolean hasArguments();



}

