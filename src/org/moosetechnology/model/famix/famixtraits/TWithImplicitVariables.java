// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithImplicitVariables")
public interface TWithImplicitVariables  {

        @FameProperty(name = "implicitVariables", opposite = "parentBehaviouralEntity", derived = true)
    public Collection<TImplicitVariable> getImplicitVariables();

    public void setImplicitVariables(Collection<? extends TImplicitVariable> implicitVariables);

    public void addImplicitVariables(TImplicitVariable one);

    public void addImplicitVariables(TImplicitVariable one, TImplicitVariable... many);

    public void addImplicitVariables(Iterable<? extends TImplicitVariable> many);

    public void addImplicitVariables(TImplicitVariable[] many);

    public int numberOfImplicitVariables();

    public boolean hasImplicitVariables();



}

