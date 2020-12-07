// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TFileSystemEntity")
public interface TFileSystemEntity {

    @FameProperty(name = "parentFolder", opposite = "childrenFileSystemEntities", container = true)
    TFolder getParentFolder();

    void setParentFolder(TFolder parentFolder);

    @FameProperty(name = "numberOfLinesOfText", derived = true)
    Number getNumberOfLinesOfText();


}

