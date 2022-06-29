// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import java.util.*;
import org.moosetechnology.model.famixjava.moosequery.TEntityMetaLevelDependency;


@FamePackage("Famix-Traits")
@FameDescription("TPackage")
public interface TPackage  {

        @FameProperty(name = "weightedMethodCount", derived = true)
    public Number getWeightedMethodCount();

    @FameProperty(name = "childEntities", opposite = "parentPackage", derived = true)
    public Collection<TPackageable> getChildEntities();

    public void setChildEntities(Collection<? extends TPackageable> childEntities);

    public void addChildEntities(TPackageable one);

    public void addChildEntities(TPackageable one, TPackageable... many);

    public void addChildEntities(Iterable<? extends TPackageable> many);

    public void addChildEntities(TPackageable[] many);

    public int numberOfChildEntities();

    public boolean hasChildEntities();



}

