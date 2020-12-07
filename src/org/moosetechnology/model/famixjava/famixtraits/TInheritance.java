// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TInheritance")
public interface TInheritance {

    @FameProperty(name = "superclass", opposite = "subInheritances")
    TWithInheritances getSuperclass();

    void setSuperclass(TWithInheritances superclass);

    @FameProperty(name = "subclass", opposite = "superInheritances")
    TWithInheritances getSubclass();

    void setSubclass(TWithInheritances subclass);


}

