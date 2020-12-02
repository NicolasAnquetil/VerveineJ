// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TAnnotationInstance")
public interface TAnnotationInstance {

    @FameProperty(name = "annotatedEntity", opposite = "annotationInstances", container = true)
    TWithAnnotationInstances getAnnotatedEntity();

    void setAnnotatedEntity(TWithAnnotationInstances annotatedEntity);


}

