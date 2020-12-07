// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithFiles")
public interface TWithFiles {

    @FameProperty(name = "containerFiles", opposite = "entities")
    Collection<TFile> getContainerFiles();

    void setContainerFiles(Collection<? extends TFile> containerFiles);

    void addContainerFiles(TFile one);

    void addContainerFiles(TFile one, TFile... many);

    void addContainerFiles(Iterable<? extends TFile> many);

    void addContainerFiles(TFile[] many);

    int numberOfContainerFiles();

    boolean hasContainerFiles();


}

