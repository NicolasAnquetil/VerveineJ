// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TInvocationsReceiver")
public interface TInvocationsReceiver {

    @FameProperty(name = "receivingInvocations", opposite = "receiver", derived = true)
    Collection<TInvocation> getReceivingInvocations();

    void setReceivingInvocations(Collection<? extends TInvocation> receivingInvocations);

    void addReceivingInvocations(TInvocation one);

    void addReceivingInvocations(TInvocation one, TInvocation... many);

    void addReceivingInvocations(Iterable<? extends TInvocation> many);

    void addReceivingInvocations(TInvocation[] many);

    int numberOfReceivingInvocations();

    boolean hasReceivingInvocations();


}

