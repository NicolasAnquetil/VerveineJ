// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import org.moosetechnology.model.famix.moosequery.TAssociationMetaLevelDependency;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Famix-Traits")
@FameDescription("TTraitUsage")
public interface TTraitUsage extends TAssociationMetaLevelDependency, TAssociation, TSourceEntity {

        @FameProperty(name = "trait", opposite = "incomingTraitUsages")
    public TTrait getTrait();

    public void setTrait(TTrait trait);

    @FameProperty(name = "user", opposite = "outgoingTraitUsages")
    public TTraitUser getUser();

    public void setUser(TTraitUser user);



}

