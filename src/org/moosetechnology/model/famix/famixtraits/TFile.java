// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TFile")
public interface TFile  {

        @FameProperty(name = "averageNumberOfCharactersPerLine", derived = true)
    public Number getAverageNumberOfCharactersPerLine();

    @FameProperty(name = "entities", opposite = "containerFiles", derived = true)
    public Collection<TWithFiles> getEntities();

    public void setEntities(Collection<? extends TWithFiles> entities);

    public void addEntities(TWithFiles one);

    public void addEntities(TWithFiles one, TWithFiles... many);

    public void addEntities(Iterable<? extends TWithFiles> many);

    public void addEntities(TWithFiles[] many);

    public int numberOfEntities();

    public boolean hasEntities();

    @FameProperty(name = "totalNumberOfLinesOfText", derived = true)
    public Number getTotalNumberOfLinesOfText();

    @FameProperty(name = "numberOfKiloBytes", derived = true)
    public Number getNumberOfKiloBytes();

    @FameProperty(name = "numberOfCharacters", derived = true)
    public Number getNumberOfCharacters();

    @FameProperty(name = "numberOfEmptyLinesOfText", derived = true)
    public Number getNumberOfEmptyLinesOfText();

    @FameProperty(name = "numberOfBytes", derived = true)
    public Number getNumberOfBytes();



}

