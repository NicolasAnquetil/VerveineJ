// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;


@FamePackage("Famix-Traits")
@FameDescription("TEnumValue")
public interface TEnumValue extends TNamedEntity, TEntityMetaLevelDependency, TSourceEntity, TStructuralEntity, TTypedEntity, TAccessible {

        @FameProperty(name = "parentEnum", opposite = "enumValues", container = true)
    public TWithEnumValues getParentEnum();

    public void setParentEnum(TWithEnumValues parentEnum);



}

