// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import java.util.*;


@FamePackage("Famix-Traits")
@FameDescription("TWithMethods")
public interface TWithMethods  {

        @FameProperty(name = "tightClassCohesion", derived = true)
    public Number getTightClassCohesion();

    @FameProperty(name = "numberOfProtectedMethods", derived = true)
    public Number getNumberOfProtectedMethods();

    @FameProperty(name = "numberOfMethods", derived = true)
    public Number getNumberOfMethods();

    @FameProperty(name = "numberOfPrivateMethods", derived = true)
    public Number getNumberOfPrivateMethods();

    @FameProperty(name = "weightedMethodCount", derived = true)
    public Number getWeightedMethodCount();

    @FameProperty(name = "numberOfPublicMethods", derived = true)
    public Number getNumberOfPublicMethods();

    @FameProperty(name = "methods", opposite = "parentType", derived = true)
    public Collection<TMethod> getMethods();

    public void setMethods(Collection<? extends TMethod> methods);

    public void addMethods(TMethod one);

    public void addMethods(TMethod one, TMethod... many);

    public void addMethods(Iterable<? extends TMethod> many);

    public void addMethods(TMethod[] many);

    public int numberOfMethods();

    public boolean hasMethods();

    @FameProperty(name = "numberOfAccessorMethods", derived = true)
    public Number getNumberOfAccessorMethods();

    @FameProperty(name = "numberOfConstructorMethods", derived = true)
    public Number getNumberOfConstructorMethods();

    @FameProperty(name = "numberOfAbstractMethods", derived = true)
    public Number getNumberOfAbstractMethods();

    @FameProperty(name = "numberOfMessageSends", derived = true)
    public Number getNumberOfMessageSends();



}

