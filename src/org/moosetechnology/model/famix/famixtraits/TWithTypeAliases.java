// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithTypeAliases")
public interface TWithTypeAliases  {

        @FameProperty(name = "typeAliases", opposite = "aliasedType", derived = true)
    public Collection<TTypeAlias> getTypeAliases();

    public void setTypeAliases(Collection<? extends TTypeAlias> typeAliases);

    public void addTypeAliases(TTypeAlias one);

    public void addTypeAliases(TTypeAlias one, TTypeAlias... many);

    public void addTypeAliases(Iterable<? extends TTypeAlias> many);

    public void addTypeAliases(TTypeAlias[] many);

    public int numberOfTypeAliases();

    public boolean hasTypeAliases();



}

