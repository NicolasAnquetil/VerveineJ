// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithParameters")
public interface TWithParameters {

    @FameProperty(name = "numberOfParameters", derived = true)
    Number getNumberOfParameters();

    @FameProperty(name = "parameters", opposite = "parentBehaviouralEntity", derived = true)
    Collection<TParameter> getParameters();

    void setParameters(Collection<? extends TParameter> parameters);

    void addParameters(TParameter one);

    void addParameters(TParameter one, TParameter... many);

    void addParameters(Iterable<? extends TParameter> many);

    void addParameters(TParameter[] many);

    int numberOfParameters();

    boolean hasParameters();


}

