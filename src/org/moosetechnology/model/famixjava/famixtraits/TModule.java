// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TModule")
public interface TModule {

    @FameProperty(name = "moduleEntities", opposite = "parentModule", derived = true)
    Collection<TDefinedInModule> getModuleEntities();

    void setModuleEntities(Collection<? extends TDefinedInModule> moduleEntities);

    void addModuleEntities(TDefinedInModule one);

    void addModuleEntities(TDefinedInModule one, TDefinedInModule... many);

    void addModuleEntities(Iterable<? extends TDefinedInModule> many);

    void addModuleEntities(TDefinedInModule[] many);

    int numberOfModuleEntities();

    boolean hasModuleEntities();


}

