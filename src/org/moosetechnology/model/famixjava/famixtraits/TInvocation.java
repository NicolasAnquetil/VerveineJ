// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TInvocation")
public interface TInvocation {

    @FameProperty(name = "candidates", opposite = "incomingInvocations")
    Collection<TInvocable> getCandidates();

    void setCandidates(Collection<? extends TInvocable> candidates);

    void addCandidates(TInvocable one);

    void addCandidates(TInvocable one, TInvocable... many);

    void addCandidates(Iterable<? extends TInvocable> many);

    void addCandidates(TInvocable[] many);

    int numberOfCandidates();

    boolean hasCandidates();

    @FameProperty(name = "receiver", opposite = "receivingInvocations")
    TInvocationsReceiver getReceiver();

    void setReceiver(TInvocationsReceiver receiver);

    @FameProperty(name = "sender", opposite = "outgoingInvocations")
    TWithInvocations getSender();

    void setSender(TWithInvocations sender);


}

