// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TLocalVariable")
public interface TLocalVariable {

    @FameProperty(name = "parentBehaviouralEntity", opposite = "localVariables", container = true)
    TWithLocalVariables getParentBehaviouralEntity();

    void setParentBehaviouralEntity(TWithLocalVariables parentBehaviouralEntity);


}

