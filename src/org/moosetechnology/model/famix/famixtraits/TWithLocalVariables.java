// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithLocalVariables")
public interface TWithLocalVariables  {

        @FameProperty(name = "localVariables", opposite = "parentBehaviouralEntity", derived = true)
    public Collection<TLocalVariable> getLocalVariables();

    public void setLocalVariables(Collection<? extends TLocalVariable> localVariables);

    public void addLocalVariables(TLocalVariable one);

    public void addLocalVariables(TLocalVariable one, TLocalVariable... many);

    public void addLocalVariables(Iterable<? extends TLocalVariable> many);

    public void addLocalVariables(TLocalVariable[] many);

    public int numberOfLocalVariables();

    public boolean hasLocalVariables();



}

