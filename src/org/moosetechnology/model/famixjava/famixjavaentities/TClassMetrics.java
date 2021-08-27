// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Famix-Java-Entities")
@FameDescription("TClassMetrics")
public interface TClassMetrics {

    @FameProperty(name = "numberOfProtectedMethods", derived = true)
    Number getNumberOfProtectedMethods();

    @FameProperty(name = "numberOfPrivateMethods", derived = true)
    Number getNumberOfPrivateMethods();

    @FameProperty(name = "numberOfPublicMethods", derived = true)
    Number getNumberOfPublicMethods();

    @FameProperty(name = "numberOfAccessorMethods", derived = true)
    Number getNumberOfAccessorMethods();

    @FameProperty(name = "numberOfConstructorMethods", derived = true)
    Number getNumberOfConstructorMethods();


}

