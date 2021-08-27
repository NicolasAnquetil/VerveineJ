// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Famix-Traits")
@FameDescription("TMethod")
public interface TMethod  {

        @FameProperty(name = "numberOfMessageSends", derived = true)
        Number getNumberOfMessageSends();

    @FameProperty(name = "hasClassScope", derived = true)
    Boolean getHasClassScope();

    @FameProperty(name = "parentType", opposite = "methods", container = true)
    TWithMethods getParentType();

    void setParentType(TWithMethods parentType);



}

