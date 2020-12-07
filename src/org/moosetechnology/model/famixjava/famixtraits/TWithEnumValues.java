// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithEnumValues")
public interface TWithEnumValues {

    @FameProperty(name = "enumValues", opposite = "parentEnum", derived = true)
    Collection<TEnumValue> getEnumValues();

    void setEnumValues(Collection<? extends TEnumValue> enumValues);

    void addEnumValues(TEnumValue one);

    void addEnumValues(TEnumValue one, TEnumValue... many);

    void addEnumValues(Iterable<? extends TEnumValue> many);

    void addEnumValues(TEnumValue[] many);

    int numberOfEnumValues();

    boolean hasEnumValues();


}

