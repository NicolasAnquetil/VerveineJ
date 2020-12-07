// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TAttribute")
public interface TAttribute {

    @FameProperty(name = "hierarchyNestingLevel", derived = true)
    Number getHierarchyNestingLevel();

    @FameProperty(name = "hasClassScope", derived = true)
    Boolean getHasClassScope();

    @FameProperty(name = "isClassSide")
    Boolean getIsClassSide();

    void setIsClassSide(Boolean isClassSide);

    @FameProperty(name = "parentType", opposite = "attributes", container = true)
    TWithAttributes getParentType();

    void setParentType(TWithAttributes parentType);


}

