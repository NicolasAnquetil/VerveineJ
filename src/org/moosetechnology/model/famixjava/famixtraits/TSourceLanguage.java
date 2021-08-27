// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("Famix-Traits")
@FameDescription("TSourceLanguage")
public interface TSourceLanguage  {

        @FameProperty(name = "sourcedEntities", opposite = "declaredSourceLanguage", derived = true)
        Collection<TWithSourceLanguages> getSourcedEntities();

    void setSourcedEntities(Collection<? extends TWithSourceLanguages> sourcedEntities);

    void addSourcedEntities(TWithSourceLanguages one);

    void addSourcedEntities(TWithSourceLanguages one, TWithSourceLanguages... many);

    void addSourcedEntities(Iterable<? extends TWithSourceLanguages> many);

    void addSourcedEntities(TWithSourceLanguages[] many);

    int numberOfSourcedEntities();

    boolean hasSourcedEntities();



}

