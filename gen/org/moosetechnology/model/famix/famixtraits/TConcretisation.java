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

        @FameProperty(name = "parameterConcretisations", opposite = "concretisations", derived = true)
    public Collection<TParameterConcretisation> getParameterConcretisations();

    public void setParameterConcretisations(Collection<? extends TParameterConcretisation> parameterConcretisations);

    public void addParameterConcretisations(TParameterConcretisation one);

    public void addParameterConcretisations(TParameterConcretisation one, TParameterConcretisation... many);

    public void addParameterConcretisations(Iterable<? extends TParameterConcretisation> many);

    public void addParameterConcretisations(TParameterConcretisation[] many);

    public int numberOfParameterConcretisations();

    public boolean hasParameterConcretisations();

    @FameProperty(name = "genericEntity", opposite = "concretisations")
    public TParametricEntity getGenericEntity();

    public void setGenericEntity(TParametricEntity genericEntity);

    @FameProperty(name = "concreteEntity", opposite = "genericEntity")
    public TParametricEntity getConcreteEntity();

    public void setConcreteEntity(TParametricEntity concreteEntity);



}

