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

        @FameProperty(name = "concretisations", opposite = "parameterConcretisations")
    public Collection<TConcretisation> getConcretisations();

    public void setConcretisations(Collection<? extends TConcretisation> concretisations);

    public void addConcretisations(TConcretisation one);

    public void addConcretisations(TConcretisation one, TConcretisation... many);

    public void addConcretisations(Iterable<? extends TConcretisation> many);

    public void addConcretisations(TConcretisation[] many);

    public int numberOfConcretisations();

    public boolean hasConcretisations();

    @FameProperty(name = "concreteParameter", opposite = "generics")
    public TConcreteParameterType getConcreteParameter();

    public void setConcreteParameter(TConcreteParameterType concreteParameter);

    @FameProperty(name = "genericParameter", opposite = "concretisations")
    public TGenericParameterType getGenericParameter();

    public void setGenericParameter(TGenericParameterType genericParameter);



}

