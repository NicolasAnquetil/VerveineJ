// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("Famix-Traits")
@FameDescription("TPackage")
public interface TPackage  {

        @FameProperty(name = "weightedMethodCount", derived = true)
        Number getWeightedMethodCount();

    @FameProperty(name = "childEntities", opposite = "parentPackage", derived = true)
    Collection<TPackageable> getChildEntities();

    void setChildEntities(Collection<? extends TPackageable> childEntities);

    void addChildEntities(TPackageable one);

    void addChildEntities(TPackageable one, TPackageable... many);

    void addChildEntities(Iterable<? extends TPackageable> many);

    void addChildEntities(TPackageable[] many);

    int numberOfChildEntities();

    boolean hasChildEntities();



}

