// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.famixtraits.*;


@FamePackage("FamixJavaEntities")
@FameDescription("IndexedFileAnchor")
public class IndexedFileAnchor extends SourceAnchor implements TIndexedFileNavigation, TFileAnchor, TSourceAnchor {

    private String encoding;

    private TSourceEntity element;

    private TFile correspondingFile;

    private Number startPos;

    private Number endPos;

    private String fileName;


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

    @FameProperty(name = "correspondingFile")
    public TFile getCorrespondingFile() {
        return correspondingFile;
    }

    public void setCorrespondingFile(TFile correspondingFile) {
        this.correspondingFile = correspondingFile;
    }

    @FameProperty(name = "startPos")
    public Number getStartPos() {
        return startPos;
    }

    public void setStartPos(Number startPos) {
        this.startPos = startPos;
    }

    @FameProperty(name = "endPos")
    public Number getEndPos() {
        return endPos;
    }

    public void setEndPos(Number endPos) {
        this.endPos = endPos;
    }

    @FameProperty(name = "lineCount", derived = true)
    public Number getLineCount() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "fileName")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


}

