// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;


@FamePackage("FamixTraits")
@FameDescription("TAnnotationTypeAttribute")
public interface TAnnotationTypeAttribute  {

        @FameProperty(name = "annotationAttributeInstances", opposite = "annotationTypeAttribute", derived = true)
    public Collection<TTypedAnnotationInstanceAttribute> getAnnotationAttributeInstances();

    public void setAnnotationAttributeInstances(Collection<? extends TTypedAnnotationInstanceAttribute> annotationAttributeInstances);

    public void addAnnotationAttributeInstances(TTypedAnnotationInstanceAttribute one);

    public void addAnnotationAttributeInstances(TTypedAnnotationInstanceAttribute one, TTypedAnnotationInstanceAttribute... many);

    public void addAnnotationAttributeInstances(Iterable<? extends TTypedAnnotationInstanceAttribute> many);

    public void addAnnotationAttributeInstances(TTypedAnnotationInstanceAttribute[] many);

    public int numberOfAnnotationAttributeInstances();

    public boolean hasAnnotationAttributeInstances();



}

