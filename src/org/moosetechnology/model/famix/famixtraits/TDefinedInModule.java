// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TDefinedInModule")
public interface TDefinedInModule  {

        @FameProperty(name = "parentModule", opposite = "moduleEntities")
    public TModule getParentModule();

    public void setParentModule(TModule parentModule);



}

