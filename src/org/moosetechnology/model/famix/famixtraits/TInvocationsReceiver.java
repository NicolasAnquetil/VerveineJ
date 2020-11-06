// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TInvocationsReceiver")
public interface TInvocationsReceiver  {

        @FameProperty(name = "receivingInvocations", opposite = "receiver", derived = true)
    public Collection<TInvocation> getReceivingInvocations();

    public void setReceivingInvocations(Collection<? extends TInvocation> receivingInvocations);

    public void addReceivingInvocations(TInvocation one);

    public void addReceivingInvocations(TInvocation one, TInvocation... many);

    public void addReceivingInvocations(Iterable<? extends TInvocation> many);

    public void addReceivingInvocations(TInvocation[] many);

    public int numberOfReceivingInvocations();

    public boolean hasReceivingInvocations();



}

