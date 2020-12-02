// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TTypeAlias")
public interface TTypeAlias {

    @FameProperty(name = "aliasedType", opposite = "typeAliases")
    TWithTypeAliases getAliasedType();

    void setAliasedType(TWithTypeAliases aliasedType);


}

