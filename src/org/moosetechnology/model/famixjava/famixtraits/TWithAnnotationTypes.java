// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithAnnotationTypes")
public interface TWithAnnotationTypes {

    @FameProperty(name = "definedAnnotationTypes", opposite = "annotationTypesContainer", derived = true)
    Collection<TAnnotationType> getDefinedAnnotationTypes();

    void setDefinedAnnotationTypes(Collection<? extends TAnnotationType> definedAnnotationTypes);

    void addDefinedAnnotationTypes(TAnnotationType one);

    void addDefinedAnnotationTypes(TAnnotationType one, TAnnotationType... many);

    void addDefinedAnnotationTypes(Iterable<? extends TAnnotationType> many);

    void addDefinedAnnotationTypes(TAnnotationType[] many);

    int numberOfDefinedAnnotationTypes();

    boolean hasDefinedAnnotationTypes();


}

