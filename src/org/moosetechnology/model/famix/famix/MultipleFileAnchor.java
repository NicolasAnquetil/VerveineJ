// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import org.moosetechnology.model.famix.famixtraits.TSourceAnchor;
import org.moosetechnology.model.famix.famixtraits.TMultipleFileAnchor;
import java.util.*;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TFileAnchor;
import org.moosetechnology.model.famix.famixtraits.TSourceEntity;


@FamePackage("FAMIX")
@FameDescription("MultipleFileAnchor")
public class MultipleFileAnchor extends SourceAnchor implements TSourceAnchor, TMultipleFileAnchor {

    private Collection<TFileAnchor> fileAnchors; 

    private TSourceEntity element;
    


    @FameProperty(name = "fileAnchors")
    public Collection<TFileAnchor> getFileAnchors() {
        if (fileAnchors == null) fileAnchors = new HashSet<TFileAnchor>();
        return fileAnchors;
    }
    
    public void setFileAnchors(Collection<? extends TFileAnchor> fileAnchors) {
        this.getFileAnchors().clear();
        this.getFileAnchors().addAll(fileAnchors);
    }                    

    public void addFileAnchors(TFileAnchor one) {
        this.getFileAnchors().add(one);
    }   
    
    public void addFileAnchors(TFileAnchor one, TFileAnchor... many) {
        this.getFileAnchors().add(one);
        for (TFileAnchor each : many)
            this.getFileAnchors().add(each);
    }   
    
    public void addFileAnchors(Iterable<? extends TFileAnchor> many) {
        for (TFileAnchor each : many)
            this.getFileAnchors().add(each);
    }   
                
    public void addFileAnchors(TFileAnchor[] many) {
        for (TFileAnchor each : many)
            this.getFileAnchors().add(each);
    }
    
    public int numberOfFileAnchors() {
        return getFileAnchors().size();
    }

    public boolean hasFileAnchors() {
        return !getFileAnchors().isEmpty();
    }

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

