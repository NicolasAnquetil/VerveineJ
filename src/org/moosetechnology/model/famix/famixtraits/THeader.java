// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("THeader")
public interface THeader  {

        @FameProperty(name = "headerOwner", opposite = "header")
    public TWithHeader getHeaderOwner();

    public void setHeaderOwner(TWithHeader headerOwner);



}

