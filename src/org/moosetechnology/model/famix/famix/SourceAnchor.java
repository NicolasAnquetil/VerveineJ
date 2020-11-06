// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import org.moosetechnology.model.famix.famixtraits.TSourceAnchor;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TSourceEntity;


@FamePackage("FAMIX")
@FameDescription("SourceAnchor")
public class SourceAnchor extends Entity implements TSourceAnchor {

    private TSourceEntity element;
    


    @FameProperty(name = "lineCount", derived = true)
    public Number getLineCount() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "element", opposite = "sourceAnchor")
    public TSourceEntity getElement() {
        return element;
    }

    public void setElement(TSourceEntity element) {
        if (this.element == null ? element != null : !this.element.equals(element)) {
            TSourceEntity old_element = this.element;
            this.element = element;
            if (old_element != null) old_element.setSourceAnchor(null);
            if (element != null) element.setSourceAnchor(this);
        }
    }
    


}

