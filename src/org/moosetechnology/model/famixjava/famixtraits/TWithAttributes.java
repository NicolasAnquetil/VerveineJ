// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("Famix-Traits")
@FameDescription("TWithAttributes")
public interface TWithAttributes  {

        @FameProperty(name = "numberOfAttributes", derived = true)
        Number getNumberOfAttributes();

    @FameProperty(name = "attributes", opposite = "parentType", derived = true)
    Collection<TAttribute> getAttributes();

    void setAttributes(Collection<? extends TAttribute> attributes);

    void addAttributes(TAttribute one);

    void addAttributes(TAttribute one, TAttribute... many);

    void addAttributes(Iterable<? extends TAttribute> many);

    void addAttributes(TAttribute[] many);

    int numberOfAttributes();

    boolean hasAttributes();



}

