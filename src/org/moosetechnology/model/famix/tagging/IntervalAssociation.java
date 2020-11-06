// Automagically generated code, please do not change
package org.moosetechnology.model.famix.tagging;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import org.moosetechnology.model.famix.famixtraits.TSourceAnchor;
import ch.akuhn.fame.FamePackage;


@FamePackage("Tagging")
@FameDescription("IntervalAssociation")
public class IntervalAssociation extends Association  {

    private TSourceAnchor sourceAnchor;
    
    private Number stop;
    
    private Number start;
    


    @FameProperty(name = "sourceAnchor")
    public TSourceAnchor getSourceAnchor() {
        return sourceAnchor;
    }

    public void setSourceAnchor(TSourceAnchor sourceAnchor) {
        this.sourceAnchor = sourceAnchor;
    }
    
    @FameProperty(name = "stop")
    public Number getStop() {
        return stop;
    }

    public void setStop(Number stop) {
        this.stop = stop;
    }
    
    @FameProperty(name = "start")
    public Number getStart() {
        return start;
    }

    public void setStart(Number start) {
        this.start = start;
    }
    


}

