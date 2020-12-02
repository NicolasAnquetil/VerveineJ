// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithImplicitVariables")
public interface TWithImplicitVariables {

    @FameProperty(name = "implicitVariables", opposite = "parentBehaviouralEntity", derived = true)
    Collection<TImplicitVariable> getImplicitVariables();

    void setImplicitVariables(Collection<? extends TImplicitVariable> implicitVariables);

    void addImplicitVariables(TImplicitVariable one);

    void addImplicitVariables(TImplicitVariable one, TImplicitVariable... many);

    void addImplicitVariables(Iterable<? extends TImplicitVariable> many);

    void addImplicitVariables(TImplicitVariable[] many);

    int numberOfImplicitVariables();

    boolean hasImplicitVariables();


}

