// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithParameterizedTypes")
public interface TWithParameterizedTypes  {

        @FameProperty(name = "parameterizedTypes", opposite = "parameterizableClass", derived = true)
    public Collection<TParameterizedType> getParameterizedTypes();

    public void setParameterizedTypes(Collection<? extends TParameterizedType> parameterizedTypes);

    public void addParameterizedTypes(TParameterizedType one);

    public void addParameterizedTypes(TParameterizedType one, TParameterizedType... many);

    public void addParameterizedTypes(Iterable<? extends TParameterizedType> many);

    public void addParameterizedTypes(TParameterizedType[] many);

    public int numberOfParameterizedTypes();

    public boolean hasParameterizedTypes();



}

