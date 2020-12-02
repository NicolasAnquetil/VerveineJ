// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithAnnotationInstances")
public interface TWithAnnotationInstances {

    @FameProperty(name = "numberOfAnnotationInstances", derived = true)
    Number getNumberOfAnnotationInstances();

    @FameProperty(name = "annotationInstances", opposite = "annotatedEntity", derived = true)
    Collection<TAnnotationInstance> getAnnotationInstances();

    void setAnnotationInstances(Collection<? extends TAnnotationInstance> annotationInstances);

    void addAnnotationInstances(TAnnotationInstance one);

    void addAnnotationInstances(TAnnotationInstance one, TAnnotationInstance... many);

    void addAnnotationInstances(Iterable<? extends TAnnotationInstance> many);

    void addAnnotationInstances(TAnnotationInstance[] many);

    int numberOfAnnotationInstances();

    boolean hasAnnotationInstances();


}

