// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import java.util.*;
import org.moosetechnology.model.famix.moosequery.TAssociationMetaLevelDependency;


@FamePackage("Famix-Traits")
@FameDescription("TParameterConcretisation")
public interface TParameterConcretisation extends TAssociationMetaLevelDependency, TAssociation, TSourceEntity {

        @FameProperty(name = "concreteParameter", opposite = "generic")
    public TConcreteParameterType getConcreteParameter();

    public void setConcreteParameter(TConcreteParameterType concreteParameter);

    @FameProperty(name = "concretisation", opposite = "parameterConcretisation")
    public Collection<TConcretisation> getConcretisation();

    public void setConcretisation(Collection<? extends TConcretisation> concretisation);

    public void addConcretisation(TConcretisation one);

    public void addConcretisation(TConcretisation one, TConcretisation... many);

    public void addConcretisation(Iterable<? extends TConcretisation> many);

    public void addConcretisation(TConcretisation[] many);

    public int numberOfConcretisation();

    public boolean hasConcretisation();

    @FameProperty(name = "genericParameter", opposite = "concretisations")
    public TGenericParameterType getGenericParameter();

    public void setGenericParameter(TGenericParameterType genericParameter);



}

