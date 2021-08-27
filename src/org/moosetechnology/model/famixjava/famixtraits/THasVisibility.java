// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Famix-Traits")
@FameDescription("THasVisibility")
public interface THasVisibility {

    @FameProperty(name = "isProtected", derived = true)
    Boolean getIsProtected();

    @FameProperty(name = "visibility")
    String getVisibility();

    void setVisibility(String visibility);

    @FameProperty(name = "isPublic", derived = true)
    Boolean getIsPublic();

    @FameProperty(name = "isPackage", derived = true)
    Boolean getIsPackage();

    @FameProperty(name = "isPrivate", derived = true)
    Boolean getIsPrivate();


}

