// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.moosequery.TAssociationMetaLevelDependency;


@FamePackage("Famix-Traits")
@FameDescription("TTraitUsage")
public interface TTraitUsage  {

        @FameProperty(name = "trait", opposite = "incomingTraitUsages")
    public TTrait getTrait();

    public void setTrait(TTrait trait);

    @FameProperty(name = "user", opposite = "outgoingTraitUsages")
    public TTraitUser getUser();

    public void setUser(TTraitUser user);



}

