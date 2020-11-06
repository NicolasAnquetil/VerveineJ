// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;


@FamePackage("FamixTraits")
@FameDescription("TAnnotationInstanceAttribute")
public interface TAnnotationInstanceAttribute  {

        @FameProperty(name = "parentAnnotationInstance", opposite = "attributes", container = true)
    public TWithAnnotationInstanceAttributes getParentAnnotationInstance();

    public void setParentAnnotationInstance(TWithAnnotationInstanceAttributes parentAnnotationInstance);

    @FameProperty(name = "value")
    public String getValue();

    public void setValue(String value);



}

