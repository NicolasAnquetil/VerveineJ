// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TMultipleFileAnchor")
public interface TMultipleFileAnchor  {

        @FameProperty(name = "fileAnchors")
    public Collection<TFileAnchor> getFileAnchors();

    public void setFileAnchors(Collection<? extends TFileAnchor> fileAnchors);

    public void addFileAnchors(TFileAnchor one);

    public void addFileAnchors(TFileAnchor one, TFileAnchor... many);

    public void addFileAnchors(Iterable<? extends TFileAnchor> many);

    public void addFileAnchors(TFileAnchor[] many);

    public int numberOfFileAnchors();

    public boolean hasFileAnchors();



}

