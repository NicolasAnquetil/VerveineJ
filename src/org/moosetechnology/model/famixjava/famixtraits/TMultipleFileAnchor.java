// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TMultipleFileAnchor")
public interface TMultipleFileAnchor {

    @FameProperty(name = "fileAnchors")
    Collection<TFileAnchor> getFileAnchors();

    void setFileAnchors(Collection<? extends TFileAnchor> fileAnchors);

    void addFileAnchors(TFileAnchor one);

    void addFileAnchors(TFileAnchor one, TFileAnchor... many);

    void addFileAnchors(Iterable<? extends TFileAnchor> many);

    void addFileAnchors(TFileAnchor[] many);

    int numberOfFileAnchors();

    boolean hasFileAnchors();


}

