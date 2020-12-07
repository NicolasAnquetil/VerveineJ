// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("THasModifiers")
public interface THasModifiers {

    @FameProperty(name = "isProtected", derived = true)
    Boolean getIsProtected();

    @FameProperty(name = "isPublic", derived = true)
    Boolean getIsPublic();

    @FameProperty(name = "isPackage", derived = true)
    Boolean getIsPackage();

    @FameProperty(name = "isPrivate", derived = true)
    Boolean getIsPrivate();

    @FameProperty(name = "isFinal", derived = true)
    Boolean getIsFinal();

    @FameProperty(name = "modifiers")
    Collection<String> getModifiers();

    void setModifiers(Collection<? extends String> modifiers);

    void addModifiers(String one);

    void addModifiers(String one, String... many);

    void addModifiers(Iterable<? extends String> many);

    void addModifiers(String[] many);

    int numberOfModifiers();

    boolean hasModifiers();

    @FameProperty(name = "isAbstract", derived = true)
    Boolean getIsAbstract();


}

