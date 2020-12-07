// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TParameterizedType")
public interface TParameterizedType {

    @FameProperty(name = "parameterizableClass", opposite = "parameterizedTypes")
    TWithParameterizedTypes getParameterizableClass();

    void setParameterizableClass(TWithParameterizedTypes parameterizableClass);


}

