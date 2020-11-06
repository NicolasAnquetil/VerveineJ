// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import org.moosetechnology.model.famix.moosequery.TAssociationMetaLevelDependency;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TInheritance")
public interface TInheritance  {

        @FameProperty(name = "superclass", opposite = "subInheritances")
    public TWithInheritances getSuperclass();

    public void setSuperclass(TWithInheritances superclass);

    @FameProperty(name = "subclass", opposite = "superInheritances")
    public TWithInheritances getSubclass();

    public void setSubclass(TWithInheritances subclass);



}

