// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithPackages")
public interface TWithPackages {

    @FameProperty(name = "packages", opposite = "packageOwner", derived = true)
    Collection<TPackage> getPackages();

    void setPackages(Collection<? extends TPackage> packages);

    void addPackages(TPackage one);

    void addPackages(TPackage one, TPackage... many);

    void addPackages(Iterable<? extends TPackage> many);

    void addPackages(TPackage[] many);

    int numberOfPackages();

    boolean hasPackages();


}

