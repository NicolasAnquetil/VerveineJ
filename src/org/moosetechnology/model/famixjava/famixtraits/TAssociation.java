// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TAssociation")
public interface TAssociation {

    @FameProperty(name = "next", opposite = "previous", derived = true)
    TAssociation getNext();

    void setNext(TAssociation next);

    @FameProperty(name = "previous", opposite = "next")
    TAssociation getPrevious();

    void setPrevious(TAssociation previous);


}

