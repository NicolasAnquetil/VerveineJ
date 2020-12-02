// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TClass")
public interface TClass {

    @FameProperty(name = "numberOfMethodsOverriden", derived = true)
    Number getNumberOfMethodsOverriden();

    @FameProperty(name = "numberOfAttributesInherited", derived = true)
    Number getNumberOfAttributesInherited();

    @FameProperty(name = "isTestCase", derived = true)
    Boolean getIsTestCase();

    @FameProperty(name = "numberOfMethodsAdded", derived = true)
    Number getNumberOfMethodsAdded();

    @FameProperty(name = "numberOfMethodsInherited", derived = true)
    Number getNumberOfMethodsInherited();

    @FameProperty(name = "weightOfAClass", derived = true)
    Number getWeightOfAClass();

    @FameProperty(name = "numberOfMethodsInHierarchy", derived = true)
    Number getNumberOfMethodsInHierarchy();


}

