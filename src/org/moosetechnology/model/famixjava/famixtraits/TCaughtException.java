// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TCaughtException")
public interface TCaughtException {

    @FameProperty(name = "definingEntity", opposite = "caughtExceptions")
    TWithCaughtExceptions getDefiningEntity();

    void setDefiningEntity(TWithCaughtExceptions definingEntity);


}

