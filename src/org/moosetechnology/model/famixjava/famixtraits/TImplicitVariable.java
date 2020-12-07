// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TImplicitVariable")
public interface TImplicitVariable {

    @FameProperty(name = "parentBehaviouralEntity", opposite = "implicitVariables", container = true)
    TWithImplicitVariables getParentBehaviouralEntity();

    void setParentBehaviouralEntity(TWithImplicitVariables parentBehaviouralEntity);


}

