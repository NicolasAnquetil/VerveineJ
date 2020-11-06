// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TSourceLanguage")
public interface TSourceLanguage  {

        @FameProperty(name = "sourcedEntities", opposite = "declaredSourceLanguage", derived = true)
    public Collection<TWithSourceLanguage> getSourcedEntities();

    public void setSourcedEntities(Collection<? extends TWithSourceLanguage> sourcedEntities);

    public void addSourcedEntities(TWithSourceLanguage one);

    public void addSourcedEntities(TWithSourceLanguage one, TWithSourceLanguage... many);

    public void addSourcedEntities(Iterable<? extends TWithSourceLanguage> many);

    public void addSourcedEntities(TWithSourceLanguage[] many);

    public int numberOfSourcedEntities();

    public boolean hasSourcedEntities();



}

