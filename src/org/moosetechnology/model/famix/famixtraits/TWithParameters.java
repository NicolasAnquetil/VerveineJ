// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithParameters")
public interface TWithParameters  {

        @FameProperty(name = "numberOfParameters", derived = true)
    public Number getNumberOfParameters();

    @FameProperty(name = "parameters", opposite = "parentBehaviouralEntity", derived = true)
    public Collection<TParameter> getParameters();

    public void setParameters(Collection<? extends TParameter> parameters);

    public void addParameters(TParameter one);

    public void addParameters(TParameter one, TParameter... many);

    public void addParameters(Iterable<? extends TParameter> many);

    public void addParameters(TParameter[] many);

    public int numberOfParameters();

    public boolean hasParameters();



}

