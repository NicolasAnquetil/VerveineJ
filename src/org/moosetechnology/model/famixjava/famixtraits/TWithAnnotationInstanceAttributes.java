// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithAnnotationInstanceAttributes")
public interface TWithAnnotationInstanceAttributes {

    @FameProperty(name = "attributes", opposite = "parentAnnotationInstance", derived = true)
    Collection<TAnnotationInstanceAttribute> getAttributes();

    void setAttributes(Collection<? extends TAnnotationInstanceAttribute> attributes);

    void addAttributes(TAnnotationInstanceAttribute one);

    void addAttributes(TAnnotationInstanceAttribute one, TAnnotationInstanceAttribute... many);

    void addAttributes(Iterable<? extends TAnnotationInstanceAttribute> many);

    void addAttributes(TAnnotationInstanceAttribute[] many);

    int numberOfAttributes();

    boolean hasAttributes();


}

