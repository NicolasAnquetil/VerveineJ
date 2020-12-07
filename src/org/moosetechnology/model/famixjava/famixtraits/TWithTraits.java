// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithTraits")
public interface TWithTraits {

    @FameProperty(name = "traits", opposite = "traitOwner", derived = true)
    Collection<TTrait> getTraits();

    void setTraits(Collection<? extends TTrait> traits);

    void addTraits(TTrait one);

    void addTraits(TTrait one, TTrait... many);

    void addTraits(Iterable<? extends TTrait> many);

    void addTraits(TTrait[] many);

    int numberOfTraits();

    boolean hasTraits();


}

