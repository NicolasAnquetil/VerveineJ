// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("Famix-Traits")
@FameDescription("TWithFileIncludes")
public interface TWithFileIncludes  {

        @FameProperty(name = "incomingIncludeRelations", opposite = "target", derived = true)
        Collection<TFileInclude> getIncomingIncludeRelations();

    void setIncomingIncludeRelations(Collection<? extends TFileInclude> incomingIncludeRelations);

    void addIncomingIncludeRelations(TFileInclude one);

    void addIncomingIncludeRelations(TFileInclude one, TFileInclude... many);

    void addIncomingIncludeRelations(Iterable<? extends TFileInclude> many);

    void addIncomingIncludeRelations(TFileInclude[] many);

    int numberOfIncomingIncludeRelations();

    boolean hasIncomingIncludeRelations();

    @FameProperty(name = "outgoingIncludeRelations", opposite = "source", derived = true)
    Collection<TFileInclude> getOutgoingIncludeRelations();

    void setOutgoingIncludeRelations(Collection<? extends TFileInclude> outgoingIncludeRelations);

    void addOutgoingIncludeRelations(TFileInclude one);

    void addOutgoingIncludeRelations(TFileInclude one, TFileInclude... many);

    void addOutgoingIncludeRelations(Iterable<? extends TFileInclude> many);

    void addOutgoingIncludeRelations(TFileInclude[] many);

    int numberOfOutgoingIncludeRelations();

    boolean hasOutgoingIncludeRelations();



}

