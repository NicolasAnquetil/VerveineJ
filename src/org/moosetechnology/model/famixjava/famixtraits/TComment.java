// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TComment")
public interface TComment {

    @FameProperty(name = "container", opposite = "comments")
    TWithComments getContainer();

    void setContainer(TWithComments container);

    @FameProperty(name = "content")
    String getContent();

    void setContent(String content);


}

