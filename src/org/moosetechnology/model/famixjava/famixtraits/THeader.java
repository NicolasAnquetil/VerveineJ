// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("THeader")
public interface THeader {

    @FameProperty(name = "headerOwner", opposite = "header")
    TWithHeader getHeaderOwner();

    void setHeaderOwner(TWithHeader headerOwner);


}

