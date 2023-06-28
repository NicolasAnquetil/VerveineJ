// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import java.util.*;
import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;


@FamePackage("Famix-Traits")
@FameDescription("TGenericParameterType")
public interface TGenericParameterType extends TEntityMetaLevelDependency, TNamedEntity, TSourceEntity, TReferenceable, TType {

        @FameProperty(name = "concretisations", opposite = "genericParameters", derived = true)
    public Collection<TParameterConcretisation> getConcretisations();

    public void setConcretisations(Collection<? extends TParameterConcretisation> concretisations);

    public void addConcretisations(TParameterConcretisation one);

    public void addConcretisations(TParameterConcretisation one, TParameterConcretisation... many);

    public void addConcretisations(Iterable<? extends TParameterConcretisation> many);

    public void addConcretisations(TParameterConcretisation[] many);

    public int numberOfConcretisations();

    public boolean hasConcretisations();

    @FameProperty(name = "genericEntity", opposite = "genericParameters", derived = true)
    public Collection<TParametricEntity> getGenericEntity();

    public void setGenericEntity(Collection<? extends TParametricEntity> genericEntity);

    public void addGenericEntity(TParametricEntity one);

    public void addGenericEntity(TParametricEntity one, TParametricEntity... many);

    public void addGenericEntity(Iterable<? extends TParametricEntity> many);

    public void addGenericEntity(TParametricEntity[] many);

    public int numberOfGenericEntity();

    public boolean hasGenericEntity();



}

