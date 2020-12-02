// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TFileInclude")
public interface TFileInclude {

    @FameProperty(name = "source", opposite = "outgoingIncludeRelations")
    TWithFileIncludes getSource();

    void setSource(TWithFileIncludes source);

    @FameProperty(name = "target", opposite = "incomingIncludeRelations")
    TWithFileIncludes getTarget();

    void setTarget(TWithFileIncludes target);


}

