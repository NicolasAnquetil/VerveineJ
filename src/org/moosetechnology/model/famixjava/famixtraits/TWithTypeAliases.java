// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithTypeAliases")
public interface TWithTypeAliases {

    @FameProperty(name = "typeAliases", opposite = "aliasedType", derived = true)
    Collection<TTypeAlias> getTypeAliases();

    void setTypeAliases(Collection<? extends TTypeAlias> typeAliases);

    void addTypeAliases(TTypeAlias one);

    void addTypeAliases(TTypeAlias one, TTypeAlias... many);

    void addTypeAliases(Iterable<? extends TTypeAlias> many);

    void addTypeAliases(TTypeAlias[] many);

    int numberOfTypeAliases();

    boolean hasTypeAliases();


}

