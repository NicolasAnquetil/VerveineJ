// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import org.moosetechnology.model.famix.famixtraits.TTypeAlias;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TWithTypeAliases;


@FamePackage("FAMIX")
@FameDescription("TypeAlias")
public class TypeAlias extends Type implements TTypeAlias {

    private TWithTypeAliases aliasedType;
    


    @FameProperty(name = "aliasedType", opposite = "typeAliases")
    public TWithTypeAliases getAliasedType() {
        return aliasedType;
    }

    public void setAliasedType(TWithTypeAliases aliasedType) {
        if (this.aliasedType != null) {
            if (this.aliasedType.equals(aliasedType)) return;
            this.aliasedType.getTypeAliases().remove(this);
        }
        this.aliasedType = aliasedType;
        if (aliasedType == null) return;
        aliasedType.getTypeAliases().add(this);
    }
    


}

