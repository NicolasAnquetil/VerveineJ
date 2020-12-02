// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TFileAnchor")
public interface TFileAnchor {

    @FameProperty(name = "fileName")
    String getFileName();

    void setFileName(String fileName);

    @FameProperty(name = "correspondingFile")
    TFile getCorrespondingFile();

    void setCorrespondingFile(TFile correspondingFile);

    @FameProperty(name = "encoding")
    String getEncoding();

    void setEncoding(String encoding);


}

