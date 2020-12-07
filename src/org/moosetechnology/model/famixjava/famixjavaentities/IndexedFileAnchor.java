// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.famixtraits.TFile;
import org.moosetechnology.model.famixjava.famixtraits.TFileAnchor;
import org.moosetechnology.model.famixjava.famixtraits.TIndexedFileNavigation;
import org.moosetechnology.model.famixjava.famixtraits.TSourceAnchor;
import org.moosetechnology.model.famixjava.famixtraits.TSourceEntity;


@FamePackage("Famix-Java-Entities")
@FameDescription("IndexedFileAnchor")
public class IndexedFileAnchor extends SourceAnchor implements TIndexedFileNavigation, TFileAnchor, TSourceAnchor {

    private TFile correspondingFile;
    
    private TSourceEntity element;
    
    private String encoding;
    
    private Number endPos;
    
    private String fileName;
    
    private Number startPos;
    


    @FameProperty(name = "correspondingFile")
    public TFile getCorrespondingFile() {
        return correspondingFile;
    }

    public void setCorrespondingFile(TFile correspondingFile) {
        this.correspondingFile = correspondingFile;
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
    
    @FameProperty(name = "encoding")
    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    
    @FameProperty(name = "endPos")
    public Number getEndPos() {
        return endPos;
    }

    public void setEndPos(Number endPos) {
        this.endPos = endPos;
    }
    
    @FameProperty(name = "fileName")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    @FameProperty(name = "lineCount", derived = true)
    public Number getLineCount() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "startPos")
    public Number getStartPos() {
        return startPos;
    }

    public void setStartPos(Number startPos) {
        this.startPos = startPos;
    }
    


}

