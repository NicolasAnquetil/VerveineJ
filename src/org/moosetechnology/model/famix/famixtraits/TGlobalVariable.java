// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;


@FamePackage("FamixTraits")
@FameDescription("TGlobalVariable")
public interface TGlobalVariable  {

        @FameProperty(name = "parentScope", opposite = "globalVariables", container = true)
    public TWithGlobalVariables getParentScope();

    public void setParentScope(TWithGlobalVariables parentScope);



}

