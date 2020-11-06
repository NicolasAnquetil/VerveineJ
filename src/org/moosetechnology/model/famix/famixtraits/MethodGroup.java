// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.moose.SpecializedGroup;


@FamePackage("FamixTraits")
@FameDescription("MethodGroup")
public class MethodGroup extends SpecializedGroup  {

    private Number averageNumberOfLinesOfCode;
    
    private Number averageNumberOfInvocations;
    
    private Number averageNumberOfParameters;
    


    @FameProperty(name = "averageNumberOfLinesOfCode")
    public Number getAverageNumberOfLinesOfCode() {
        return averageNumberOfLinesOfCode;
    }

    public void setAverageNumberOfLinesOfCode(Number averageNumberOfLinesOfCode) {
        this.averageNumberOfLinesOfCode = averageNumberOfLinesOfCode;
    }
    
    @FameProperty(name = "averageNumberOfInvocations")
    public Number getAverageNumberOfInvocations() {
        return averageNumberOfInvocations;
    }

    public void setAverageNumberOfInvocations(Number averageNumberOfInvocations) {
        this.averageNumberOfInvocations = averageNumberOfInvocations;
    }
    
    @FameProperty(name = "averageNumberOfParameters")
    public Number getAverageNumberOfParameters() {
        return averageNumberOfParameters;
    }

    public void setAverageNumberOfParameters(Number averageNumberOfParameters) {
        this.averageNumberOfParameters = averageNumberOfParameters;
    }
    


}

