// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;


@FamePackage("FamixTraits")
@FameDescription("TAnnotationInstance")
public interface TAnnotationInstance  {

        @FameProperty(name = "annotatedEntity", opposite = "annotationInstances", container = true)
    public TWithAnnotationInstances getAnnotatedEntity();

    public void setAnnotatedEntity(TWithAnnotationInstances annotatedEntity);



}

