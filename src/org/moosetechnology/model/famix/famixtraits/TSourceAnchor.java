// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TSourceAnchor")
public interface TSourceAnchor  {

        @FameProperty(name = "lineCount", derived = true)
    public Number getLineCount();

    @FameProperty(name = "element", opposite = "sourceAnchor")
    public TSourceEntity getElement();

    public void setElement(TSourceEntity element);



}

