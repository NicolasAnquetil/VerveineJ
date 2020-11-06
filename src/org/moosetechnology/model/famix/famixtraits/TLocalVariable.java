// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;


@FamePackage("FamixTraits")
@FameDescription("TLocalVariable")
public interface TLocalVariable  {

        @FameProperty(name = "parentBehaviouralEntity", opposite = "localVariables", container = true)
    public TWithLocalVariables getParentBehaviouralEntity();

    public void setParentBehaviouralEntity(TWithLocalVariables parentBehaviouralEntity);



}

