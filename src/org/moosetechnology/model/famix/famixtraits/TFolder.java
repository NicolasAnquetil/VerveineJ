// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TFolder")
public interface TFolder  {

        @FameProperty(name = "numberOfFiles", derived = true)
    public Number getNumberOfFiles();

    @FameProperty(name = "childrenFileSystemEntities", opposite = "parentFolder", derived = true)
    public Collection<TFileSystemEntity> getChildrenFileSystemEntities();

    public void setChildrenFileSystemEntities(Collection<? extends TFileSystemEntity> childrenFileSystemEntities);

    public void addChildrenFileSystemEntities(TFileSystemEntity one);

    public void addChildrenFileSystemEntities(TFileSystemEntity one, TFileSystemEntity... many);

    public void addChildrenFileSystemEntities(Iterable<? extends TFileSystemEntity> many);

    public void addChildrenFileSystemEntities(TFileSystemEntity[] many);

    public int numberOfChildrenFileSystemEntities();

    public boolean hasChildrenFileSystemEntities();

    @FameProperty(name = "totalNumberOfLinesOfText", derived = true)
    public Number getTotalNumberOfLinesOfText();

    @FameProperty(name = "numberOfFolders", derived = true)
    public Number getNumberOfFolders();

    @FameProperty(name = "numberOfEmptyLinesOfText", derived = true)
    public Number getNumberOfEmptyLinesOfText();



}

