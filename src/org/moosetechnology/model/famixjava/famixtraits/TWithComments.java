// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithComments")
public interface TWithComments {

    @FameProperty(name = "hasComments", derived = true)
    Boolean getHasComments();

    @FameProperty(name = "comments", opposite = "container", derived = true)
    Collection<TComment> getComments();

    void setComments(Collection<? extends TComment> comments);

    void addComments(TComment one);

    void addComments(TComment one, TComment... many);

    void addComments(Iterable<? extends TComment> many);

    void addComments(TComment[] many);

    int numberOfComments();

    boolean hasComments();

    @FameProperty(name = "numberOfComments", derived = true)
    Number getNumberOfComments();


}

