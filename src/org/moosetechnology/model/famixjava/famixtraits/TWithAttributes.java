// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithAttributes")
public interface TWithAttributes {

    @FameProperty(name = "numberOfProtectedAttributes", derived = true)
    Number getNumberOfProtectedAttributes();

    @FameProperty(name = "numberOfAttributes", derived = true)
    Number getNumberOfAttributes();

    @FameProperty(name = "numberOfRevealedAttributes", derived = true)
    Number getNumberOfRevealedAttributes();

    @FameProperty(name = "numberOfPublicAttributes", derived = true)
    Number getNumberOfPublicAttributes();

    @FameProperty(name = "attributes", opposite = "parentType", derived = true)
    Collection<TAttribute> getAttributes();

    void setAttributes(Collection<? extends TAttribute> attributes);

    void addAttributes(TAttribute one);

    void addAttributes(TAttribute one, TAttribute... many);

    void addAttributes(Iterable<? extends TAttribute> many);

    void addAttributes(TAttribute[] many);

    int numberOfAttributes();

    boolean hasAttributes();

    @FameProperty(name = "numberOfPrivateAttributes", derived = true)
    Number getNumberOfPrivateAttributes();


}

