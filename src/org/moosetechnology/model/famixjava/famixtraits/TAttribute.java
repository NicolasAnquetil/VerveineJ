// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.moosequery.TEntityMetaLevelDependency;


@FamePackage("Famix-Traits")
@FameDescription("TAttribute")
public interface TAttribute  {

        @FameProperty(name = "hierarchyNestingLevel", derived = true)
    public Number getHierarchyNestingLevel();

    @FameProperty(name = "hasClassScope", derived = true)
    public Boolean getHasClassScope();

    @FameProperty(name = "isClassSide")
    public Boolean getIsClassSide();

    public void setIsClassSide(Boolean isClassSide);

    @FameProperty(name = "parentType", opposite = "attributes", container = true)
    public TWithAttributes getParentType();

    public void setParentType(TWithAttributes parentType);



}

