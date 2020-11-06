// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TPackageable")
public interface TPackageable  {

        @FameProperty(name = "parentPackage", opposite = "childEntities", container = true)
    public TPackage getParentPackage();

    public void setParentPackage(TPackage parentPackage);



}

