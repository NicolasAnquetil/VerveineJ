// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithHeader")
public interface TWithHeader  {

        @FameProperty(name = "header", opposite = "headerOwner", derived = true)
    public THeader getHeader();

    public void setHeader(THeader header);



}

