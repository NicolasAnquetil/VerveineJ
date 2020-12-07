// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.moosequery.TEntityMetaLevelDependency;
import org.moosetechnology.model.famixjava.moosequery.TOODependencyQueries;


@FamePackage("Famix-Traits")
@FameDescription("TClass")
public interface TClass  {

        @FameProperty(name = "numberOfMethodsOverriden", derived = true)
    public Number getNumberOfMethodsOverriden();

    @FameProperty(name = "numberOfAttributesInherited", derived = true)
    public Number getNumberOfAttributesInherited();

    @FameProperty(name = "isTestCase", derived = true)
    public Boolean getIsTestCase();

    @FameProperty(name = "numberOfMethodsAdded", derived = true)
    public Number getNumberOfMethodsAdded();

    @FameProperty(name = "numberOfMethodsInherited", derived = true)
    public Number getNumberOfMethodsInherited();

    @FameProperty(name = "weightOfAClass", derived = true)
    public Number getWeightOfAClass();

    @FameProperty(name = "numberOfMethodsInHierarchy", derived = true)
    public Number getNumberOfMethodsInHierarchy();



}

