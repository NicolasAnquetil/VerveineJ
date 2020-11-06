// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithAnnotationTypes")
public interface TWithAnnotationTypes  {

        @FameProperty(name = "definedAnnotationTypes", opposite = "annotationTypesContainer", derived = true)
    public Collection<TAnnotationType> getDefinedAnnotationTypes();

    public void setDefinedAnnotationTypes(Collection<? extends TAnnotationType> definedAnnotationTypes);

    public void addDefinedAnnotationTypes(TAnnotationType one);

    public void addDefinedAnnotationTypes(TAnnotationType one, TAnnotationType... many);

    public void addDefinedAnnotationTypes(Iterable<? extends TAnnotationType> many);

    public void addDefinedAnnotationTypes(TAnnotationType[] many);

    public int numberOfDefinedAnnotationTypes();

    public boolean hasDefinedAnnotationTypes();



}

