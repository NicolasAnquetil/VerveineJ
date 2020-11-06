// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
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

    @FameProperty(name = "methods", opposite = "parentType", derived = true)
    public Collection<TMethod> getMethods();

    public void setMethods(Collection<? extends TMethod> methods);

    public void addMethods(TMethod one);

    public void addMethods(TMethod one, TMethod... many);

    public void addMethods(Iterable<? extends TMethod> many);

    public void addMethods(TMethod[] many);

    public int numberOfMethods();

    public boolean hasMethods();

    @FameProperty(name = "numberOfPublicMethods", derived = true)
    public Number getNumberOfPublicMethods();

    @FameProperty(name = "numberOfAccessorMethods", derived = true)
    public Number getNumberOfAccessorMethods();

    @FameProperty(name = "numberOfAbstractMethods", derived = true)
    public Number getNumberOfAbstractMethods();

    @FameProperty(name = "numberOfConstructorMethods", derived = true)
    public Number getNumberOfConstructorMethods();

    @FameProperty(name = "numberOfMessageSends", derived = true)
    public Number getNumberOfMessageSends();



}

