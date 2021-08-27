// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("Famix-Traits")
@FameDescription("TFolder")
public interface TFolder  {

        @FameProperty(name = "numberOfFiles", derived = true)
        Number getNumberOfFiles();

    @FameProperty(name = "childrenFileSystemEntities", opposite = "parentFolder", derived = true)
    Collection<TFileSystemEntity> getChildrenFileSystemEntities();

    void setChildrenFileSystemEntities(Collection<? extends TFileSystemEntity> childrenFileSystemEntities);

    void addChildrenFileSystemEntities(TFileSystemEntity one);

    void addChildrenFileSystemEntities(TFileSystemEntity one, TFileSystemEntity... many);

    void addChildrenFileSystemEntities(Iterable<? extends TFileSystemEntity> many);

    void addChildrenFileSystemEntities(TFileSystemEntity[] many);

    int numberOfChildrenFileSystemEntities();

    boolean hasChildrenFileSystemEntities();

    @FameProperty(name = "numberOfFolders", derived = true)
    Number getNumberOfFolders();

    @FameProperty(name = "totalNumberOfLinesOfText", derived = true)
    Number getTotalNumberOfLinesOfText();

    @FameProperty(name = "numberOfEmptyLinesOfText", derived = true)
    Number getNumberOfEmptyLinesOfText();



}

