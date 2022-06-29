// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.moosequery.TAssociationMetaLevelDependency;


@FamePackage("Famix-Traits")
@FameDescription("TAccess")
public interface TAccess  {

        @FameProperty(name = "isReadWriteUnknown", derived = true)
    public Boolean getIsReadWriteUnknown();

    @FameProperty(name = "isRead", derived = true)
    public Boolean getIsRead();

    @FameProperty(name = "accessor", opposite = "accesses")
    public TWithAccesses getAccessor();

    public void setAccessor(TWithAccesses accessor);

    @FameProperty(name = "variable", opposite = "incomingAccesses")
    public TAccessible getVariable();

    public void setVariable(TAccessible variable);

    @FameProperty(name = "isWrite")
    public Boolean getIsWrite();

    public void setIsWrite(Boolean isWrite);



}

