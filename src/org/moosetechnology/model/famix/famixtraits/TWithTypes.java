// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithTypes")
public interface TWithTypes  {

        @FameProperty(name = "types", opposite = "typeContainer", derived = true)
    public Collection<TType> getTypes();

    public void setTypes(Collection<? extends TType> types);

    public void addTypes(TType one);

    public void addTypes(TType one, TType... many);

    public void addTypes(Iterable<? extends TType> many);

    public void addTypes(TType[] many);

    public int numberOfTypes();

    public boolean hasTypes();



}

