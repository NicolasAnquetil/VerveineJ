// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TDeclaredException")
public interface TDeclaredException  {

        @FameProperty(name = "definingEntity", opposite = "declaredExceptions")
    public TWithDeclaredExceptions getDefiningEntity();

    public void setDefiningEntity(TWithDeclaredExceptions definingEntity);



}

