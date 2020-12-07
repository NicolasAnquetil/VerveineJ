// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.moosequery.TAssociationMetaLevelDependency;


@FamePackage("Famix-Traits")
@FameDescription("TReference")
public interface TReference  {

        @FameProperty(name = "referredType", opposite = "incomingReferences")
    public TReferenceable getReferredType();

    public void setReferredType(TReferenceable referredType);

    @FameProperty(name = "referencer", opposite = "outgoingReferences")
    public TWithReferences getReferencer();

    public void setReferencer(TWithReferences referencer);



}

