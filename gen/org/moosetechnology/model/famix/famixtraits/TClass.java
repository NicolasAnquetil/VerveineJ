// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;


@FamePackage("Famix-Traits")
@FameDescription("TClass")
public interface TClass extends TEntityMetaLevelDependency, TNamedEntity, TWithAttributes, TWithMethods, TSourceEntity, TReferenceable, TInvocationsReceiver, TWithInheritances, TType, TPackageable, TWithComments {

        @FameProperty(name = "isTestCase", derived = true)
    public Boolean getIsTestCase();

    @FameProperty(name = "weightOfAClass", derived = true)
    public Number getWeightOfAClass();



}

