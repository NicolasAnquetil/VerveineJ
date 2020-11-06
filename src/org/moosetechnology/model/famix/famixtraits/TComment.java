// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TComment")
public interface TComment  {

        @FameProperty(name = "container", opposite = "comments")
    public TWithComments getContainer();

    public void setContainer(TWithComments container);

    @FameProperty(name = "content")
    public String getContent();

    public void setContent(String content);



}

