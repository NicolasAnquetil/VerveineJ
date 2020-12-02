// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TAnnotationInstanceAttribute")
public interface TAnnotationInstanceAttribute {

    @FameProperty(name = "parentAnnotationInstance", opposite = "attributes", container = true)
    TWithAnnotationInstanceAttributes getParentAnnotationInstance();

    void setParentAnnotationInstance(TWithAnnotationInstanceAttributes parentAnnotationInstance);

    @FameProperty(name = "value")
    String getValue();

    void setValue(String value);


}

