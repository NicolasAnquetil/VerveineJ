// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import java.util.*;


@FamePackage("Famix-Traits")
@FameDescription("TWithPackages")
public interface TWithPackages  {

        @FameProperty(name = "packages", opposite = "packageOwner", derived = true)
    public Collection<TPackage> getPackages();

    public void setPackages(Collection<? extends TPackage> packages);

    public void addPackages(TPackage one);

    public void addPackages(TPackage one, TPackage... many);

    public void addPackages(Iterable<? extends TPackage> many);

    public void addPackages(TPackage[] many);

    public int numberOfPackages();

    public boolean hasPackages();



}

