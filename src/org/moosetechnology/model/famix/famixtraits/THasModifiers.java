// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("THasModifiers")
public interface THasModifiers  {

        @FameProperty(name = "isProtected", derived = true)
    public Boolean getIsProtected();

    @FameProperty(name = "isPublic", derived = true)
    public Boolean getIsPublic();

    @FameProperty(name = "isPackage", derived = true)
    public Boolean getIsPackage();

    @FameProperty(name = "isPrivate", derived = true)
    public Boolean getIsPrivate();

    @FameProperty(name = "isFinal", derived = true)
    public Boolean getIsFinal();

    @FameProperty(name = "modifiers")
    public Collection<String> getModifiers();

    public void setModifiers(Collection<? extends String> modifiers);

    public void addModifiers(String one);

    public void addModifiers(String one, String... many);

    public void addModifiers(Iterable<? extends String> many);

    public void addModifiers(String[] many);

    public int numberOfModifiers();

    public boolean hasModifiers();

    @FameProperty(name = "isAbstract", derived = true)
    public Boolean getIsAbstract();



}

