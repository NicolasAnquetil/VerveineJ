// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import org.moosetechnology.model.famix.famixtraits.TFileNavigation;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import org.moosetechnology.model.famix.famixtraits.TSourceAnchor;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TFile;
import org.moosetechnology.model.famix.famixtraits.TFileAnchor;
import org.moosetechnology.model.famix.famixtraits.TSourceEntity;


@FamePackage("FAMIX")
@FameDescription("FileAnchor")
public class FileAnchor extends AbstractFileAnchor implements TFileNavigation, TFileAnchor, TSourceAnchor {

    private Number endLine;
    
    private String fileName;
    
    private TFile correspondingFile;
    
    private Number startLine;
    
    private Number endColumn;
    
    private Number startColumn;
    
    private String encoding;
    
    private TSourceEntity element;
    


    @FameProperty(name = "endLine")
    public Number getEndLine() {
        return endLine;
    }

    public void setEndLine(Number endLine) {
        this.endLine = endLine;
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
    
    @FameProperty(name = "correspondingFile")
    public TFile getCorrespondingFile() {
        return correspondingFile;
    }

    public void setCorrespondingFile(TFile correspondingFile) {
        this.correspondingFile = correspondingFile;
    }
    
    @FameProperty(name = "startLine")
    public Number getStartLine() {
        return startLine;
    }

    public void setStartLine(Number startLine) {
        this.startLine = startLine;
    }
    
    @FameProperty(name = "endColumn")
    public Number getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(Number endColumn) {
        this.endColumn = endColumn;
    }
    
    @FameProperty(name = "startColumn")
    public Number getStartColumn() {
        return startColumn;
    }

    public void setStartColumn(Number startColumn) {
        this.startColumn = startColumn;
    }
    
    @FameProperty(name = "encoding")
    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
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

