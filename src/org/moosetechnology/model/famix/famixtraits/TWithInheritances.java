// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithInheritances")
public interface TWithInheritances  {

        @FameProperty(name = "superInheritances", opposite = "subclass", derived = true)
    public Collection<TInheritance> getSuperInheritances();

    public void setSuperInheritances(Collection<? extends TInheritance> superInheritances);

    public void addSuperInheritances(TInheritance one);

    public void addSuperInheritances(TInheritance one, TInheritance... many);

    public void addSuperInheritances(Iterable<? extends TInheritance> many);

    public void addSuperInheritances(TInheritance[] many);

    public int numberOfSuperInheritances();

    public boolean hasSuperInheritances();

    @FameProperty(name = "subInheritances", opposite = "superclass", derived = true)
    public Collection<TInheritance> getSubInheritances();

    public void setSubInheritances(Collection<? extends TInheritance> subInheritances);

    public void addSubInheritances(TInheritance one);

    public void addSubInheritances(TInheritance one, TInheritance... many);

    public void addSubInheritances(Iterable<? extends TInheritance> many);

    public void addSubInheritances(TInheritance[] many);

    public int numberOfSubInheritances();

    public boolean hasSubInheritances();

    @FameProperty(name = "hierarchyNestingLevel", derived = true)
    public Number getHierarchyNestingLevel();

    @FameProperty(name = "subclassHierarchyDepth", derived = true)
    public Number getSubclassHierarchyDepth();

    @FameProperty(name = "numberOfSubclasses", derived = true)
    public Number getNumberOfSubclasses();

    @FameProperty(name = "totalNumberOfSubclasses", derived = true)
    public Number getTotalNumberOfSubclasses();

    @FameProperty(name = "numberOfDirectSubclasses", derived = true)
    public Number getNumberOfDirectSubclasses();



}

