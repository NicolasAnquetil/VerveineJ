// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.moosequery;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Moose-Query")
@FameDescription("TEntityMetaLevelDependency")
public interface TEntityMetaLevelDependency  {

        @FameProperty(name = "numberOfDeadChildren", derived = true)
        Number getNumberOfDeadChildren();

    @FameProperty(name = "fanOut", derived = true)
    Number getFanOut();

    @FameProperty(name = "fanIn", derived = true)
    Number getFanIn();

    @FameProperty(name = "numberOfChildren", derived = true)
    Number getNumberOfChildren();

    @FameProperty(name = "isDead", derived = true)
    Boolean getIsDead();



}

