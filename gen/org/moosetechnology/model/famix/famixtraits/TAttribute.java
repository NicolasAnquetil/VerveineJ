// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Famix-Traits")
@FameDescription("TAttribute")
public interface TAttribute extends TNamedEntity, TEntityMetaLevelDependency, TSourceEntity, TStructuralEntity, TTypedEntity, TAccessible {

        @FameProperty(name = "hierarchyNestingLevel", derived = true)
    public Number getHierarchyNestingLevel();

    @FameProperty(name = "hasClassScope", derived = true)
    public Boolean getHasClassScope();

    @FameProperty(name = "parentType", opposite = "attributes", container = true)
    public TWithAttributes getParentType();

    public void setParentType(TWithAttributes parentType);



}

