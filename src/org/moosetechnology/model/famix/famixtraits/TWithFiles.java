// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithFiles")
public interface TWithFiles  {

        @FameProperty(name = "containerFiles", opposite = "entities")
    public Collection<TFile> getContainerFiles();

    public void setContainerFiles(Collection<? extends TFile> containerFiles);

    public void addContainerFiles(TFile one);

    public void addContainerFiles(TFile one, TFile... many);

    public void addContainerFiles(Iterable<? extends TFile> many);

    public void addContainerFiles(TFile[] many);

    public int numberOfContainerFiles();

    public boolean hasContainerFiles();



}

