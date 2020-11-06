// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import org.moosetechnology.model.famix.moosequery.TAssociationMetaLevelDependency;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TAccess")
public interface TAccess  {

        @FameProperty(name = "isReadWriteUnknown", derived = true)
    public Boolean getIsReadWriteUnknown();

    @FameProperty(name = "variable", opposite = "incomingAccesses")
    public TAccessible getVariable();

    public void setVariable(TAccessible variable);

    @FameProperty(name = "accessor", opposite = "accesses")
    public TWithAccesses getAccessor();

    public void setAccessor(TWithAccesses accessor);

    @FameProperty(name = "isRead", derived = true)
    public Boolean getIsRead();

    @FameProperty(name = "isWrite", derived = true)
    public Boolean getIsWrite();



}

