// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TAnnotationTypeAttribute")
public interface TAnnotationTypeAttribute {

    @FameProperty(name = "annotationAttributeInstances", opposite = "annotationTypeAttribute", derived = true)
    Collection<TTypedAnnotationInstanceAttribute> getAnnotationAttributeInstances();

    void setAnnotationAttributeInstances(Collection<? extends TTypedAnnotationInstanceAttribute> annotationAttributeInstances);

    void addAnnotationAttributeInstances(TTypedAnnotationInstanceAttribute one);

    void addAnnotationAttributeInstances(TTypedAnnotationInstanceAttribute one, TTypedAnnotationInstanceAttribute... many);

    void addAnnotationAttributeInstances(Iterable<? extends TTypedAnnotationInstanceAttribute> many);

    void addAnnotationAttributeInstances(TTypedAnnotationInstanceAttribute[] many);

    int numberOfAnnotationAttributeInstances();

    boolean hasAnnotationAttributeInstances();


}

