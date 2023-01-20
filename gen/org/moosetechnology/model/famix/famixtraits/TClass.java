// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Famix-Traits")
@FameDescription("TClass")
public interface TClass extends TNamedEntity, TEntityMetaLevelDependency, TWithAttributes, TWithMethods, TSourceEntity, TReferenceable, TWithInheritances, TInvocationsReceiver, TType, TPackageable, TWithComments {

        @FameProperty(name = "isTestCase", derived = true)
    public Boolean getIsTestCase();

    @FameProperty(name = "weightOfAClass", derived = true)
    public Number getWeightOfAClass();



}

