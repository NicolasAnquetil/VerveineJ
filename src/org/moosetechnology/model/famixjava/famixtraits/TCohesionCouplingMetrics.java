// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TCohesionCouplingMetrics")
public interface TCohesionCouplingMetrics {

    @FameProperty(name = "efferentCoupling", derived = true)
    Number getEfferentCoupling();

    @FameProperty(name = "distance", derived = true)
    Number getDistance();

    @FameProperty(name = "afferentCoupling", derived = true)
    Number getAfferentCoupling();

    @FameProperty(name = "instability", derived = true)
    Number getInstability();

    @FameProperty(name = "abstractness", derived = true)
    Number getAbstractness();

    @FameProperty(name = "martinCohesion", derived = true)
    Number getMartinCohesion();


}

