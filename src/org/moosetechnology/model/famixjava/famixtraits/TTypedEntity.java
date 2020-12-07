// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TTypedEntity")
public interface TTypedEntity {

    @FameProperty(name = "declaredType", opposite = "typedEntities")
    TType getDeclaredType();

    void setDeclaredType(TType declaredType);


}

