// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TTypedAnnotationInstance")
public interface TTypedAnnotationInstance  {

        @FameProperty(name = "annotationType", opposite = "instances")
    public TAnnotationType getAnnotationType();

    public void setAnnotationType(TAnnotationType annotationType);



}

