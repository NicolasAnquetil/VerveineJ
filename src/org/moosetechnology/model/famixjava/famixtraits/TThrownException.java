// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TThrownException")
public interface TThrownException {

    @FameProperty(name = "definingEntity", opposite = "thrownExceptions")
    TWithThrownExceptions getDefiningEntity();

    void setDefiningEntity(TWithThrownExceptions definingEntity);


}

