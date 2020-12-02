// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TRelativeSourceAnchor")
public interface TRelativeSourceAnchor {

    @FameProperty(name = "relatedAnchor")
    TSourceAnchor getRelatedAnchor();

    void setRelatedAnchor(TSourceAnchor relatedAnchor);

    @FameProperty(name = "startPos")
    Number getStartPos();

    void setStartPos(Number startPos);

    @FameProperty(name = "endPos")
    Number getEndPos();

    void setEndPos(Number endPos);


}

