// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithAttributes")
public interface TWithAttributes  {

        @FameProperty(name = "numberOfProtectedAttributes", derived = true)
    public Number getNumberOfProtectedAttributes();

    @FameProperty(name = "numberOfAttributes", derived = true)
    public Number getNumberOfAttributes();

    @FameProperty(name = "numberOfRevealedAttributes", derived = true)
    public Number getNumberOfRevealedAttributes();

    @FameProperty(name = "numberOfPublicAttributes", derived = true)
    public Number getNumberOfPublicAttributes();

    @FameProperty(name = "attributes", opposite = "parentType", derived = true)
    public Collection<TAttribute> getAttributes();

    public void setAttributes(Collection<? extends TAttribute> attributes);

    public void addAttributes(TAttribute one);

    public void addAttributes(TAttribute one, TAttribute... many);

    public void addAttributes(Iterable<? extends TAttribute> many);

    public void addAttributes(TAttribute[] many);

    public int numberOfAttributes();

    public boolean hasAttributes();

    @FameProperty(name = "numberOfPrivateAttributes", derived = true)
    public Number getNumberOfPrivateAttributes();



}

