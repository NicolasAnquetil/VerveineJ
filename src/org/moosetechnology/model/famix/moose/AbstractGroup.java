// Automagically generated code, please do not change
package org.moosetechnology.model.famix.moose;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("Moose")
@FameDescription("AbstractGroup")
public class AbstractGroup extends Object  {

    private Number numberOfAssociations;
    
    private Number numberOfItems;
    
    private Number numberOfEntities;
    
    private Number numberOfLinesOfCode;
    
    private Number numberOfPackages;
    


    @FameProperty(name = "numberOfAssociations")
    public Number getNumberOfAssociations() {
        return numberOfAssociations;
    }

    public void setNumberOfAssociations(Number numberOfAssociations) {
        this.numberOfAssociations = numberOfAssociations;
    }
    
    @FameProperty(name = "numberOfItems")
    public Number getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(Number numberOfItems) {
        this.numberOfItems = numberOfItems;
    }
    
    @FameProperty(name = "numberOfEntities")
    public Number getNumberOfEntities() {
        return numberOfEntities;
    }

    public void setNumberOfEntities(Number numberOfEntities) {
        this.numberOfEntities = numberOfEntities;
    }
    
    @FameProperty(name = "numberOfLinesOfCode")
    public Number getNumberOfLinesOfCode() {
        return numberOfLinesOfCode;
    }

    public void setNumberOfLinesOfCode(Number numberOfLinesOfCode) {
        this.numberOfLinesOfCode = numberOfLinesOfCode;
    }
    
    @FameProperty(name = "numberOfPackages")
    public Number getNumberOfPackages() {
        return numberOfPackages;
    }

    public void setNumberOfPackages(Number numberOfPackages) {
        this.numberOfPackages = numberOfPackages;
    }
    


}

