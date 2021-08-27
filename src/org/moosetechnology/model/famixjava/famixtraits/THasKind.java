// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Famix-Traits")
@FameDescription("THasKind")
public interface THasKind {

    @FameProperty(name = "isGetter", derived = true)
    Boolean getIsGetter();

    @FameProperty(name = "isConstant", derived = true)
    Boolean getIsConstant();

    @FameProperty(name = "kind")
    String getKind();

    void setKind(String kind);

    @FameProperty(name = "isConstructor", derived = true)
    Boolean getIsConstructor();

    @FameProperty(name = "isSetter", derived = true)
    Boolean getIsSetter();


}

