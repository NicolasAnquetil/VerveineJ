// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("Famix-Traits")
@FameDescription("TFile")
public interface TFile  {

        @FameProperty(name = "averageNumberOfCharactersPerLine", derived = true)
        Number getAverageNumberOfCharactersPerLine();

    @FameProperty(name = "entities", opposite = "containerFiles", derived = true)
    Collection<TWithFiles> getEntities();

    void setEntities(Collection<? extends TWithFiles> entities);

    void addEntities(TWithFiles one);

    void addEntities(TWithFiles one, TWithFiles... many);

    void addEntities(Iterable<? extends TWithFiles> many);

    void addEntities(TWithFiles[] many);

    int numberOfEntities();

    boolean hasEntities();

    @FameProperty(name = "totalNumberOfLinesOfText", derived = true)
    Number getTotalNumberOfLinesOfText();

    @FameProperty(name = "numberOfKiloBytes", derived = true)
    Number getNumberOfKiloBytes();

    @FameProperty(name = "numberOfEmptyLinesOfText", derived = true)
    Number getNumberOfEmptyLinesOfText();

    @FameProperty(name = "numberOfCharacters", derived = true)
    Number getNumberOfCharacters();

    @FameProperty(name = "numberOfBytes", derived = true)
    Number getNumberOfBytes();



}

