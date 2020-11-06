// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TModule")
public interface TModule  {

        @FameProperty(name = "moduleEntities", opposite = "parentModule", derived = true)
    public Collection<TDefinedInModule> getModuleEntities();

    public void setModuleEntities(Collection<? extends TDefinedInModule> moduleEntities);

    public void addModuleEntities(TDefinedInModule one);

    public void addModuleEntities(TDefinedInModule one, TDefinedInModule... many);

    public void addModuleEntities(Iterable<? extends TDefinedInModule> many);

    public void addModuleEntities(TDefinedInModule[] many);

    public int numberOfModuleEntities();

    public boolean hasModuleEntities();



}

