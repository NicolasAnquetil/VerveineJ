// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TTraitUsage")
public interface TTraitUsage {

    @FameProperty(name = "trait", opposite = "incomingTraitUsages")
    TTrait getTrait();

    void setTrait(TTrait trait);

    @FameProperty(name = "user", opposite = "outgoingTraitUsages")
    TTraitUser getUser();

    void setUser(TTraitUser user);


}

