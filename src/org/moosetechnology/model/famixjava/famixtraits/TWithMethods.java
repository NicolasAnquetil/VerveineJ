// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("Famix-Traits")
@FameDescription("TWithMethods")
public interface TWithMethods  {

        @FameProperty(name = "tightClassCohesion", derived = true)
        Number getTightClassCohesion();

    @FameProperty(name = "numberOfMethods", derived = true)
    Number getNumberOfMethods();

    @FameProperty(name = "weightedMethodCount", derived = true)
    Number getWeightedMethodCount();

    @FameProperty(name = "methods", opposite = "parentType", derived = true)
    Collection<TMethod> getMethods();

    void setMethods(Collection<? extends TMethod> methods);

    void addMethods(TMethod one);

    void addMethods(TMethod one, TMethod... many);

    void addMethods(Iterable<? extends TMethod> many);

    void addMethods(TMethod[] many);

    int numberOfMethods();

    boolean hasMethods();

    @FameProperty(name = "numberOfAbstractMethods", derived = true)
    Number getNumberOfAbstractMethods();

    @FameProperty(name = "numberOfMessageSends", derived = true)
    Number getNumberOfMessageSends();



}

