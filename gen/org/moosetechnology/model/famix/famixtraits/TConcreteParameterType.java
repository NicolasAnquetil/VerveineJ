// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import java.util.*;
import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;


@FamePackage("Famix-Traits")
@FameDescription("TConcreteParameterType")
public interface TConcreteParameterType extends TEntityMetaLevelDependency, TNamedEntity, TSourceEntity, TReferenceable, TType {

        @FameProperty(name = "concreteEntity", opposite = "concreteParameters", derived = true)
    public Collection<TParametricEntity> getConcreteEntity();

    public void setConcreteEntity(Collection<? extends TParametricEntity> concreteEntity);

    public void addConcreteEntity(TParametricEntity one);

    public void addConcreteEntity(TParametricEntity one, TParametricEntity... many);

    public void addConcreteEntity(Iterable<? extends TParametricEntity> many);

    public void addConcreteEntity(TParametricEntity[] many);

    public int numberOfConcreteEntity();

    public boolean hasConcreteEntity();

    @FameProperty(name = "generic", opposite = "concreteParameter", derived = true)
    public Collection<TParameterConcretisation> getGeneric();

    public void setGeneric(Collection<? extends TParameterConcretisation> generic);

    public void addGeneric(TParameterConcretisation one);

    public void addGeneric(TParameterConcretisation one, TParameterConcretisation... many);

    public void addGeneric(Iterable<? extends TParameterConcretisation> many);

    public void addGeneric(TParameterConcretisation[] many);

    public int numberOfGeneric();

    public boolean hasGeneric();



}

