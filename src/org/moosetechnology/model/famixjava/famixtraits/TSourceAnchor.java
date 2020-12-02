// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TSourceAnchor")
public interface TSourceAnchor {

    @FameProperty(name = "lineCount", derived = true)
    Number getLineCount();

    @FameProperty(name = "element", opposite = "sourceAnchor")
    TSourceEntity getElement();

    void setElement(TSourceEntity element);


}

