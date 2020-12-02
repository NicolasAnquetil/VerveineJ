// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TParameter")
public interface TParameter {

    @FameProperty(name = "parentBehaviouralEntity", opposite = "parameters", container = true)
    TWithParameters getParentBehaviouralEntity();

    void setParentBehaviouralEntity(TWithParameters parentBehaviouralEntity);


}

