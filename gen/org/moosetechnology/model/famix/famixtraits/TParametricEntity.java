// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import java.util.*;


@FamePackage("Famix-Traits")
@FameDescription("TParametricEntity")
public interface TParametricEntity  {

        @FameProperty(name = "concretisations", opposite = "genericEntity", derived = true)
    public Collection<TConcretisation> getConcretisations();

    public void setConcretisations(Collection<? extends TConcretisation> concretisations);

    public void addConcretisations(TConcretisation one);

    public void addConcretisations(TConcretisation one, TConcretisation... many);

    public void addConcretisations(Iterable<? extends TConcretisation> many);

    public void addConcretisations(TConcretisation[] many);

    public int numberOfConcretisations();

    public boolean hasConcretisations();

    @FameProperty(name = "genericEntity", opposite = "concreteEntity", derived = true)
    public Collection<TConcretisation> getGenericEntity();

    public void setGenericEntity(Collection<? extends TConcretisation> genericEntity);

    public void addGenericEntity(TConcretisation one);

    public void addGenericEntity(TConcretisation one, TConcretisation... many);

    public void addGenericEntity(Iterable<? extends TConcretisation> many);

    public void addGenericEntity(TConcretisation[] many);

    public int numberOfGenericEntity();

    public boolean hasGenericEntity();

    @FameProperty(name = "genericParameters", opposite = "genericEntity")
    public Collection<TGenericParameterType> getGenericParameters();

    public void setGenericParameters(Collection<? extends TGenericParameterType> genericParameters);

    public void addGenericParameters(TGenericParameterType one);

    public void addGenericParameters(TGenericParameterType one, TGenericParameterType... many);

    public void addGenericParameters(Iterable<? extends TGenericParameterType> many);

    public void addGenericParameters(TGenericParameterType[] many);

    public int numberOfGenericParameters();

    public boolean hasGenericParameters();

    @FameProperty(name = "concreteParameters", opposite = "concreteEntity")
    public Collection<TConcreteParameterType> getConcreteParameters();

    public void setConcreteParameters(Collection<? extends TConcreteParameterType> concreteParameters);

    public void addConcreteParameters(TConcreteParameterType one);

    public void addConcreteParameters(TConcreteParameterType one, TConcreteParameterType... many);

    public void addConcreteParameters(Iterable<? extends TConcreteParameterType> many);

    public void addConcreteParameters(TConcreteParameterType[] many);

    public int numberOfConcreteParameters();

    public boolean hasConcreteParameters();



}

