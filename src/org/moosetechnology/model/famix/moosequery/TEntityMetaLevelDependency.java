// Automagically generated code, please do not change
package org.moosetechnology.model.famix.moosequery;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("MooseQuery")
@FameDescription("TEntityMetaLevelDependency")
public interface TEntityMetaLevelDependency  {

        @FameProperty(name = "numberOfDeadChildren", derived = true)
    public Number getNumberOfDeadChildren();

    @FameProperty(name = "fanOut", derived = true)
    public Number getFanOut();

    @FameProperty(name = "fanIn", derived = true)
    public Number getFanIn();

    @FameProperty(name = "numberOfChildren", derived = true)
    public Number getNumberOfChildren();

    @FameProperty(name = "isDead", derived = true)
    public Boolean getIsDead();



}

