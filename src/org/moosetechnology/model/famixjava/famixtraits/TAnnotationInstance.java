// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.moosequery.TEntityMetaLevelDependency;


@FamePackage("Famix-Traits")
@FameDescription("TAnnotationInstance")
public interface TAnnotationInstance  {

        @FameProperty(name = "annotatedEntity", opposite = "annotationInstances", container = true)
    public TWithAnnotationInstances getAnnotatedEntity();

    public void setAnnotatedEntity(TWithAnnotationInstances annotatedEntity);



}

