// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.moosequery.TEntityMetaLevelDependency;


@FamePackage("Famix-Traits")
@FameDescription("TAnnotationInstanceAttribute")
public interface TAnnotationInstanceAttribute  {

        @FameProperty(name = "parentAnnotationInstance", opposite = "attributes", container = true)
    public TWithAnnotationInstanceAttributes getParentAnnotationInstance();

    public void setParentAnnotationInstance(TWithAnnotationInstanceAttributes parentAnnotationInstance);

    @FameProperty(name = "value")
    public String getValue();

    public void setValue(String value);



}

