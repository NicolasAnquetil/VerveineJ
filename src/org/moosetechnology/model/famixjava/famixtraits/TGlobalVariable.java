// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TGlobalVariable")
public interface TGlobalVariable {

    @FameProperty(name = "parentScope", opposite = "globalVariables", container = true)
    TWithGlobalVariables getParentScope();

    void setParentScope(TWithGlobalVariables parentScope);


}

