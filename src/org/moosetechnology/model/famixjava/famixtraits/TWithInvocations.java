// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("Famix-Traits")
@FameDescription("TWithInvocations")
public interface TWithInvocations  {

        @FameProperty(name = "numberOfOutgoingInvocations", derived = true)
        Number getNumberOfOutgoingInvocations();

    @FameProperty(name = "outgoingInvocations", opposite = "sender", derived = true)
    Collection<TInvocation> getOutgoingInvocations();

    void setOutgoingInvocations(Collection<? extends TInvocation> outgoingInvocations);

    void addOutgoingInvocations(TInvocation one);

    void addOutgoingInvocations(TInvocation one, TInvocation... many);

    void addOutgoingInvocations(Iterable<? extends TInvocation> many);

    void addOutgoingInvocations(TInvocation[] many);

    int numberOfOutgoingInvocations();

    boolean hasOutgoingInvocations();



}

