// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithInvocations")
public interface TWithInvocations  {

        @FameProperty(name = "outgoingInvocations", opposite = "sender", derived = true)
    public Collection<TInvocation> getOutgoingInvocations();

    public void setOutgoingInvocations(Collection<? extends TInvocation> outgoingInvocations);

    public void addOutgoingInvocations(TInvocation one);

    public void addOutgoingInvocations(TInvocation one, TInvocation... many);

    public void addOutgoingInvocations(Iterable<? extends TInvocation> many);

    public void addOutgoingInvocations(TInvocation[] many);

    public int numberOfOutgoingInvocations();

    public boolean hasOutgoingInvocations();



}

