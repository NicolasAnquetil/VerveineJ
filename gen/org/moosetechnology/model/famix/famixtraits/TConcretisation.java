// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import java.util.*;
import org.moosetechnology.model.famix.moosequery.TAssociationMetaLevelDependency;


@FamePackage("Famix-Traits")
@FameDescription("TConcretisation")
public interface TConcretisation extends TAssociationMetaLevelDependency, TAssociation, TSourceEntity {

        @FameProperty(name = "genericEntity", opposite = "concretisations")
    public TParametricEntity getGenericEntity();

    public void setGenericEntity(TParametricEntity genericEntity);

    @FameProperty(name = "concreteEntity", opposite = "genericEntities")
    public TParametricEntity getConcreteEntity();

    public void setConcreteEntity(TParametricEntity concreteEntity);

    @FameProperty(name = "parameterConcretisation", opposite = "concretisation", derived = true)
    public Collection<TParameterConcretisation> getParameterConcretisation();

    public void setParameterConcretisation(Collection<? extends TParameterConcretisation> parameterConcretisation);

    public void addParameterConcretisation(TParameterConcretisation one);

    public void addParameterConcretisation(TParameterConcretisation one, TParameterConcretisation... many);

    public void addParameterConcretisation(Iterable<? extends TParameterConcretisation> many);

    public void addParameterConcretisation(TParameterConcretisation[] many);

    public int numberOfParameterConcretisation();

    public boolean hasParameterConcretisation();



}

