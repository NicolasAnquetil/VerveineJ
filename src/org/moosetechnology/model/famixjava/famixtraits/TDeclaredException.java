// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TDeclaredException")
public interface TDeclaredException {

    @FameProperty(name = "definingEntity", opposite = "declaredExceptions")
    TWithDeclaredExceptions getDefiningEntity();

    void setDefiningEntity(TWithDeclaredExceptions definingEntity);


}

