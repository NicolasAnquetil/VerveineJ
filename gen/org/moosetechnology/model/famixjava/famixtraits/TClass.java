// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.moosequery.TEntityMetaLevelDependency;
import org.moosetechnology.model.famixjava.moosequery.TOODependencyQueries;


@FamePackage("Famix-Traits")
@FameDescription("TClass")
public interface TClass extends TNamedEntity, TOODependencyQueries, TWithAttributes, TWithMethods, TSourceEntity, TEntityMetaLevelDependency, TReferenceable, TInvocationsReceiver, TWithInheritances, TType, TPackageable, TWithComments {

        @FameProperty(name = "isTestCase", derived = true)
    public Boolean getIsTestCase();

    @FameProperty(name = "weightOfAClass", derived = true)
    public Number getWeightOfAClass();



}

