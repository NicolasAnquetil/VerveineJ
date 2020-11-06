// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;


@FamePackage("FamixTraits")
@FameDescription("TImplicitVariable")
public interface TImplicitVariable  {

        @FameProperty(name = "parentBehaviouralEntity", opposite = "implicitVariables", container = true)
    public TWithImplicitVariables getParentBehaviouralEntity();

    public void setParentBehaviouralEntity(TWithImplicitVariables parentBehaviouralEntity);



}

