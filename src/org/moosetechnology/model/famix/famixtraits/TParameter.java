// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;


@FamePackage("FamixTraits")
@FameDescription("TParameter")
public interface TParameter  {

        @FameProperty(name = "parentBehaviouralEntity", opposite = "parameters", container = true)
    public TWithParameters getParentBehaviouralEntity();

    public void setParentBehaviouralEntity(TWithParameters parentBehaviouralEntity);



}

