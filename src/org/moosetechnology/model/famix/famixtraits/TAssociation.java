// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import org.moosetechnology.model.famix.moosequery.TAssociationMetaLevelDependency;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TAssociation")
public interface TAssociation  {

        @FameProperty(name = "next", opposite = "previous", derived = true)
    public TAssociation getNext();

    public void setNext(TAssociation next);

    @FameProperty(name = "previous", opposite = "next")
    public TAssociation getPrevious();

    public void setPrevious(TAssociation previous);



}

