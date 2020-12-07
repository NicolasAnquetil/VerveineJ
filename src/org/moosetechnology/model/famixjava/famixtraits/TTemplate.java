// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TTemplate")
public interface TTemplate {

    @FameProperty(name = "templateUsers", opposite = "template", derived = true)
    Collection<TTemplateUser> getTemplateUsers();

    void setTemplateUsers(Collection<? extends TTemplateUser> templateUsers);

    void addTemplateUsers(TTemplateUser one);

    void addTemplateUsers(TTemplateUser one, TTemplateUser... many);

    void addTemplateUsers(Iterable<? extends TTemplateUser> many);

    void addTemplateUsers(TTemplateUser[] many);

    int numberOfTemplateUsers();

    boolean hasTemplateUsers();

    @FameProperty(name = "templateOwner", opposite = "templates")
    TWithTemplates getTemplateOwner();

    void setTemplateOwner(TWithTemplates templateOwner);


}

