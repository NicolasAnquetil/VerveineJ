// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import java.util.*;
import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;


@FamePackage("Famix-Traits")
@FameDescription("TConcreteParameterType")
public interface TConcreteParameterType extends TNamedEntity, TEntityMetaLevelDependency, TSourceEntity, TReferenceable, TType {

        @FameProperty(name = "generics", opposite = "concreteParameter", derived = true)
    public Collection<TParameterConcretisation> getGenerics();

    public void setGenerics(Collection<? extends TParameterConcretisation> generics);

    public void addGenerics(TParameterConcretisation one);

    public void addGenerics(TParameterConcretisation one, TParameterConcretisation... many);

    public void addGenerics(Iterable<? extends TParameterConcretisation> many);

    public void addGenerics(TParameterConcretisation[] many);

    public int numberOfGenerics();

    public boolean hasGenerics();

    @FameProperty(name = "concreteEntities", opposite = "concreteParameters", derived = true)
    public Collection<TParametricEntity> getConcreteEntities();

    public void setConcreteEntities(Collection<? extends TParametricEntity> concreteEntities);

    public void addConcreteEntities(TParametricEntity one);

    public void addConcreteEntities(TParametricEntity one, TParametricEntity... many);

    public void addConcreteEntities(Iterable<? extends TParametricEntity> many);

    public void addConcreteEntities(TParametricEntity[] many);

    public int numberOfConcreteEntities();

    public boolean hasConcreteEntities();



}

