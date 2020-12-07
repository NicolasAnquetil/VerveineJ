// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TSourceLanguage")
public interface TSourceLanguage {

    @FameProperty(name = "sourcedEntities", opposite = "declaredSourceLanguage", derived = true)
    Collection<TWithSourceLanguage> getSourcedEntities();

    void setSourcedEntities(Collection<? extends TWithSourceLanguage> sourcedEntities);

    void addSourcedEntities(TWithSourceLanguage one);

    void addSourcedEntities(TWithSourceLanguage one, TWithSourceLanguage... many);

    void addSourcedEntities(Iterable<? extends TWithSourceLanguage> many);

    void addSourcedEntities(TWithSourceLanguage[] many);

    int numberOfSourcedEntities();

    boolean hasSourcedEntities();


}

