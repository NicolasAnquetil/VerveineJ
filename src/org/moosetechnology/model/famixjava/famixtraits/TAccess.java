// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Famix-Traits")
@FameDescription("TAccess")
public interface TAccess  {

        @FameProperty(name = "isReadWriteUnknown", derived = true)
        Boolean getIsReadWriteUnknown();

    @FameProperty(name = "variable", opposite = "incomingAccesses")
    TAccessible getVariable();

    void setVariable(TAccessible variable);

    @FameProperty(name = "accessor", opposite = "accesses")
    TWithAccesses getAccessor();

    void setAccessor(TWithAccesses accessor);

    @FameProperty(name = "isRead", derived = true)
    Boolean getIsRead();

    @FameProperty(name = "isWrite")
    Boolean getIsWrite();

    void setIsWrite(Boolean isWrite);



}

