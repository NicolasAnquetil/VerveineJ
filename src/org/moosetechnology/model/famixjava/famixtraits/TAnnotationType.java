// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TAnnotationType")
public interface TAnnotationType {

    @FameProperty(name = "instances", opposite = "annotationType", derived = true)
    Collection<TTypedAnnotationInstance> getInstances();

    void setInstances(Collection<? extends TTypedAnnotationInstance> instances);

    void addInstances(TTypedAnnotationInstance one);

    void addInstances(TTypedAnnotationInstance one, TTypedAnnotationInstance... many);

    void addInstances(Iterable<? extends TTypedAnnotationInstance> many);

    void addInstances(TTypedAnnotationInstance[] many);

    int numberOfInstances();

    boolean hasInstances();

    @FameProperty(name = "annotationTypesContainer", opposite = "definedAnnotationTypes", container = true)
    TWithAnnotationTypes getAnnotationTypesContainer();

    void setAnnotationTypesContainer(TWithAnnotationTypes annotationTypesContainer);


}

