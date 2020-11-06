// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TRelativeSourceAnchor")
public interface TRelativeSourceAnchor  {

        @FameProperty(name = "startPos")
    public Number getStartPos();

    public void setStartPos(Number startPos);

    @FameProperty(name = "relatedAnchor")
    public TSourceAnchor getRelatedAnchor();

    public void setRelatedAnchor(TSourceAnchor relatedAnchor);

    @FameProperty(name = "endPos")
    public Number getEndPos();

    public void setEndPos(Number endPos);



}

