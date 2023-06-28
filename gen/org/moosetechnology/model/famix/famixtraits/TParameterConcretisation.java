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

        @FameProperty(name = "concretisation", opposite = "parameterConcretisation")
    public Collection<TConcretisation> getConcretisation();

    public void setConcretisation(Collection<? extends TConcretisation> concretisation);

    public void addConcretisation(TConcretisation one);

    public void addConcretisation(TConcretisation one, TConcretisation... many);

    public void addConcretisation(Iterable<? extends TConcretisation> many);

    public void addConcretisation(TConcretisation[] many);

    public int numberOfConcretisation();

    public boolean hasConcretisation();

    @FameProperty(name = "genericParameters", opposite = "concretisations")
    public TGenericParameterType getGenericParameters();

    public void setGenericParameters(TGenericParameterType genericParameters);

    @FameProperty(name = "concreteParameters", opposite = "generic")
    public TConcreteParameterType getConcreteParameters();

    public void setConcreteParameters(TConcreteParameterType concreteParameters);



}

