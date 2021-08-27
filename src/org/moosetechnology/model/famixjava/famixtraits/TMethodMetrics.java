// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Famix-Traits")
@FameDescription("TMethodMetrics")
public interface TMethodMetrics {

    @FameProperty(name = "hierarchyNestingLevel", derived = true)
    Number getHierarchyNestingLevel();

    @FameProperty(name = "numberOfAnnotationInstances", derived = true)
    Number getNumberOfAnnotationInstances();

    @FameProperty(name = "cyclomaticComplexity")
    Number getCyclomaticComplexity();

    void setCyclomaticComplexity(Number cyclomaticComplexity);


}

