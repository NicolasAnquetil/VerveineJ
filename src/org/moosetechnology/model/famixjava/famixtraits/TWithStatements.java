// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Famix-Traits")
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

