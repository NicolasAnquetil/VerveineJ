// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TTypedAnnotationInstanceAttribute")
public interface TTypedAnnotationInstanceAttribute {

    @FameProperty(name = "annotationTypeAttribute", opposite = "annotationAttributeInstances")
    TAnnotationTypeAttribute getAnnotationTypeAttribute();

    void setAnnotationTypeAttribute(TAnnotationTypeAttribute annotationTypeAttribute);


}

