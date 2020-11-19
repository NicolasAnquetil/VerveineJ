// Automagically generated code, please do not change
package org.moosetechnology.model.famix.moose;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TSourceLanguage;


@FamePackage("Moose")
@FameDescription("Model")
public class Model extends AbstractGroup  {

    private Number numberOfClassesPerPackage;
    
    private Number numberOfModelMethods;
    
    private Number numberOfMethods;
    
    private Number numberOfLinesOfCode;
    
    private Number numberOfClasses;
    
    private Number numberOfModelClasses;
    
    private Number numberOfLinesOfCodePerPackage;
    
    private Number numberOfLinesOfCodePerMethod;
    
    private TSourceLanguage sourceLanguage;
    
    private Number numberOfLinesOfCodePerClass;
    


    @FameProperty(name = "numberOfClassesPerPackage")
    public Number getNumberOfClassesPerPackage() {
        return numberOfClassesPerPackage;
    }

    public void setNumberOfClassesPerPackage(Number numberOfClassesPerPackage) {
        this.numberOfClassesPerPackage = numberOfClassesPerPackage;
    }
    
    @FameProperty(name = "numberOfModelMethods")
    public Number getNumberOfModelMethods() {
        return numberOfModelMethods;
    }

    public void setNumberOfModelMethods(Number numberOfModelMethods) {
        this.numberOfModelMethods = numberOfModelMethods;
    }
    
    @FameProperty(name = "numberOfMethods")
    public Number getNumberOfMethods() {
        return numberOfMethods;
    }

    public void setNumberOfMethods(Number numberOfMethods) {
        this.numberOfMethods = numberOfMethods;
    }
    
    @FameProperty(name = "averageCyclomaticComplexity", derived = true)
    public Number getAverageCyclomaticComplexity() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOfLinesOfCode")
    public Number getNumberOfLinesOfCode() {
        return numberOfLinesOfCode;
    }

    public void setNumberOfLinesOfCode(Number numberOfLinesOfCode) {
        this.numberOfLinesOfCode = numberOfLinesOfCode;
    }
    
    @FameProperty(name = "numberOfClasses")
    public Number getNumberOfClasses() {
        return numberOfClasses;
    }

    public void setNumberOfClasses(Number numberOfClasses) {
        this.numberOfClasses = numberOfClasses;
    }
    
    @FameProperty(name = "numberOfModelClasses")
    public Number getNumberOfModelClasses() {
        return numberOfModelClasses;
    }

    public void setNumberOfModelClasses(Number numberOfModelClasses) {
        this.numberOfModelClasses = numberOfModelClasses;
    }
    
    @FameProperty(name = "numberOfLinesOfCodePerPackage")
    public Number getNumberOfLinesOfCodePerPackage() {
        return numberOfLinesOfCodePerPackage;
    }

    public void setNumberOfLinesOfCodePerPackage(Number numberOfLinesOfCodePerPackage) {
        this.numberOfLinesOfCodePerPackage = numberOfLinesOfCodePerPackage;
    }
    
    @FameProperty(name = "numberOfLinesOfCodePerMethod")
    public Number getNumberOfLinesOfCodePerMethod() {
        return numberOfLinesOfCodePerMethod;
    }

    public void setNumberOfLinesOfCodePerMethod(Number numberOfLinesOfCodePerMethod) {
        this.numberOfLinesOfCodePerMethod = numberOfLinesOfCodePerMethod;
    }
    
    @FameProperty(name = "sourceLanguage")
    public TSourceLanguage getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(TSourceLanguage sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }
    
    @FameProperty(name = "numberOfLinesOfCodePerClass")
    public Number getNumberOfLinesOfCodePerClass() {
        return numberOfLinesOfCodePerClass;
    }

    public void setNumberOfLinesOfCodePerClass(Number numberOfLinesOfCodePerClass) {
        this.numberOfLinesOfCodePerClass = numberOfLinesOfCodePerClass;
    }
    


}
