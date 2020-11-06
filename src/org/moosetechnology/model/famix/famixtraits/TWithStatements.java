// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithStatements")
public interface TWithStatements  {

        @FameProperty(name = "numberOfStatements", derived = true)
    public Number getNumberOfStatements();

    @FameProperty(name = "numberOflinesOfDeadCode", derived = true)
    public Number getNumberOflinesOfDeadCode();

    @FameProperty(name = "cyclomaticComplexity")
    public Number getCyclomaticComplexity();

    public void setCyclomaticComplexity(Number cyclomaticComplexity);



}

