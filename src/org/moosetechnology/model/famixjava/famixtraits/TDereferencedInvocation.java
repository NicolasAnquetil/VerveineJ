// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.moosequery.TAssociationMetaLevelDependency;


@FamePackage("Famix-Traits")
@FameDescription("TDereferencedInvocation")
public interface TDereferencedInvocation extends TAssociationMetaLevelDependency, TAssociation, TSourceEntity, TInvocation {

        @FameProperty(name = "referencer", opposite = "dereferencedInvocations")
    public TWithDereferencedInvocations getReferencer();

    public void setReferencer(TWithDereferencedInvocations referencer);



}

