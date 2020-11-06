// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import org.moosetechnology.model.famix.moosequery.TOODependencyQueries;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;


@FamePackage("FamixTraits")
@FameDescription("TClass")
public interface TClass  {

        @FameProperty(name = "numberOfAttributesInherited", derived = true)
    public Number getNumberOfAttributesInherited();

    @FameProperty(name = "numberOfMethodsOverriden", derived = true)
    public Number getNumberOfMethodsOverriden();

    @FameProperty(name = "isTestCase", derived = true)
    public Boolean getIsTestCase();

    @FameProperty(name = "numberOfMethodsAdded", derived = true)
    public Number getNumberOfMethodsAdded();

    @FameProperty(name = "numberOfMethodsInherited", derived = true)
    public Number getNumberOfMethodsInherited();

    @FameProperty(name = "numberOfMethodsInHierarchy", derived = true)
    public Number getNumberOfMethodsInHierarchy();

    @FameProperty(name = "weightOfAClass", derived = true)
    public Number getWeightOfAClass();



}

