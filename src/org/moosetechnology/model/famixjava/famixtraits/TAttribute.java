// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Famix-Traits")
@FameDescription("TAttribute")
public interface TAttribute  {

        @FameProperty(name = "hierarchyNestingLevel", derived = true)
        Number getHierarchyNestingLevel();

    @FameProperty(name = "hasClassScope", derived = true)
    Boolean getHasClassScope();

    @FameProperty(name = "parentType", opposite = "attributes", container = true)
    TWithAttributes getParentType();

    void setParentType(TWithAttributes parentType);



}

