// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TReference")
public interface TReference {

    @FameProperty(name = "referredType", opposite = "incomingReferences")
    TReferenceable getReferredType();

    void setReferredType(TReferenceable referredType);

    @FameProperty(name = "referencer", opposite = "outgoingReferences")
    TWithReferences getReferencer();

    void setReferencer(TWithReferences referencer);


}

