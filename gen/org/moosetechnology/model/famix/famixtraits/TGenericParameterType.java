// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import java.util.*;
import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;


@FamePackage("Famix-Traits")
@FameDescription("TGenericParameterType")
public interface TGenericParameterType extends TNamedEntity, TEntityMetaLevelDependency, TSourceEntity, TReferenceable, TType {

        @FameProperty(name = "concretisations", opposite = "genericParameter", derived = true)
    public Collection<TParameterConcretisation> getConcretisations();

    public void setConcretisations(Collection<? extends TParameterConcretisation> concretisations);

    public void addConcretisations(TParameterConcretisation one);

    public void addConcretisations(TParameterConcretisation one, TParameterConcretisation... many);

    public void addConcretisations(Iterable<? extends TParameterConcretisation> many);

    public void addConcretisations(TParameterConcretisation[] many);

    public int numberOfConcretisations();

    public boolean hasConcretisations();

    @FameProperty(name = "genericEntities", opposite = "genericParameters", derived = true)
    public Collection<TParametricEntity> getGenericEntities();

    public void setGenericEntities(Collection<? extends TParametricEntity> genericEntities);

    public void addGenericEntities(TParametricEntity one);

    public void addGenericEntities(TParametricEntity one, TParametricEntity... many);

    public void addGenericEntities(Iterable<? extends TParametricEntity> many);

    public void addGenericEntities(TParametricEntity[] many);

    public int numberOfGenericEntities();

    public boolean hasGenericEntities();



}

