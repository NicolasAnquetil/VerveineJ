// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithTypes")
public interface TWithTypes {

    @FameProperty(name = "types", opposite = "typeContainer", derived = true)
    Collection<TType> getTypes();

    void setTypes(Collection<? extends TType> types);

    void addTypes(TType one);

    void addTypes(TType one, TType... many);

    void addTypes(Iterable<? extends TType> many);

    void addTypes(TType[] many);

    int numberOfTypes();

    boolean hasTypes();


}

