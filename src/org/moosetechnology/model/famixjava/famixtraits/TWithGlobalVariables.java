// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithGlobalVariables")
public interface TWithGlobalVariables {

    @FameProperty(name = "globalVariables", opposite = "parentScope", derived = true)
    Collection<TGlobalVariable> getGlobalVariables();

    void setGlobalVariables(Collection<? extends TGlobalVariable> globalVariables);

    void addGlobalVariables(TGlobalVariable one);

    void addGlobalVariables(TGlobalVariable one, TGlobalVariable... many);

    void addGlobalVariables(Iterable<? extends TGlobalVariable> many);

    void addGlobalVariables(TGlobalVariable[] many);

    int numberOfGlobalVariables();

    boolean hasGlobalVariables();


}

