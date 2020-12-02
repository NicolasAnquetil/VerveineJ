// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithTemplates")
public interface TWithTemplates {

    @FameProperty(name = "templates", opposite = "templateOwner", derived = true)
    Collection<TTemplate> getTemplates();

    void setTemplates(Collection<? extends TTemplate> templates);

    void addTemplates(TTemplate one);

    void addTemplates(TTemplate one, TTemplate... many);

    void addTemplates(Iterable<? extends TTemplate> many);

    void addTemplates(TTemplate[] many);

    int numberOfTemplates();

    boolean hasTemplates();


}

