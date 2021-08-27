// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("Famix-Traits")
@FameDescription("TWithInheritances")
public interface TWithInheritances  {

        @FameProperty(name = "superInheritances", opposite = "subclass", derived = true)
        Collection<TInheritance> getSuperInheritances();

    void setSuperInheritances(Collection<? extends TInheritance> superInheritances);

    void addSuperInheritances(TInheritance one);

    void addSuperInheritances(TInheritance one, TInheritance... many);

    void addSuperInheritances(Iterable<? extends TInheritance> many);

    void addSuperInheritances(TInheritance[] many);

    int numberOfSuperInheritances();

    boolean hasSuperInheritances();

    @FameProperty(name = "subInheritances", opposite = "superclass", derived = true)
    Collection<TInheritance> getSubInheritances();

    void setSubInheritances(Collection<? extends TInheritance> subInheritances);

    void addSubInheritances(TInheritance one);

    void addSubInheritances(TInheritance one, TInheritance... many);

    void addSubInheritances(Iterable<? extends TInheritance> many);

    void addSubInheritances(TInheritance[] many);

    int numberOfSubInheritances();

    boolean hasSubInheritances();

    @FameProperty(name = "hierarchyNestingLevel", derived = true)
    Number getHierarchyNestingLevel();

    @FameProperty(name = "subclassHierarchyDepth", derived = true)
    Number getSubclassHierarchyDepth();

    @FameProperty(name = "numberOfSubclasses", derived = true)
    Number getNumberOfSubclasses();

    @FameProperty(name = "numberOfDirectSubclasses", derived = true)
    Number getNumberOfDirectSubclasses();



}

