// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithParameterizedTypes")
public interface TWithParameterizedTypes {

    @FameProperty(name = "parameterizedTypes", opposite = "parameterizableClass", derived = true)
    Collection<TParameterizedType> getParameterizedTypes();

    void setParameterizedTypes(Collection<? extends TParameterizedType> parameterizedTypes);

    void addParameterizedTypes(TParameterizedType one);

    void addParameterizedTypes(TParameterizedType one, TParameterizedType... many);

    void addParameterizedTypes(Iterable<? extends TParameterizedType> many);

    void addParameterizedTypes(TParameterizedType[] many);

    int numberOfParameterizedTypes();

    boolean hasParameterizedTypes();


}

