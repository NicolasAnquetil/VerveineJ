// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Famix-Traits")
@FameDescription("TClassMetrics")
public interface TClassMetrics {

    @FameProperty(name = "numberOfLocallyDefinedMethods", derived = true)
    Number getNumberOfLocallyDefinedMethods();

    @FameProperty(name = "numberOfMethodsOverriden", derived = true)
    Number getNumberOfMethodsOverriden();

    @FameProperty(name = "numberOfAttributesInherited", derived = true)
    Number getNumberOfAttributesInherited();

    @FameProperty(name = "totalNumberOfSubclasses", derived = true)
    Number getTotalNumberOfSubclasses();

    @FameProperty(name = "numberOfMethodsInherited", derived = true)
    Number getNumberOfMethodsInherited();

    @FameProperty(name = "numberOfMethodsInHierarchy", derived = true)
    Number getNumberOfMethodsInHierarchy();


}

