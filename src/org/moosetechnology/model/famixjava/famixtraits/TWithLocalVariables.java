// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithLocalVariables")
public interface TWithLocalVariables {

    @FameProperty(name = "localVariables", opposite = "parentBehaviouralEntity", derived = true)
    Collection<TLocalVariable> getLocalVariables();

    void setLocalVariables(Collection<? extends TLocalVariable> localVariables);

    void addLocalVariables(TLocalVariable one);

    void addLocalVariables(TLocalVariable one, TLocalVariable... many);

    void addLocalVariables(Iterable<? extends TLocalVariable> many);

    void addLocalVariables(TLocalVariable[] many);

    int numberOfLocalVariables();

    boolean hasLocalVariables();


}

