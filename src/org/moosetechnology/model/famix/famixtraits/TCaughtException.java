// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TCaughtException")
public interface TCaughtException  {

        @FameProperty(name = "definingEntity", opposite = "caughtExceptions")
    public TWithCaughtExceptions getDefiningEntity();

    public void setDefiningEntity(TWithCaughtExceptions definingEntity);



}

