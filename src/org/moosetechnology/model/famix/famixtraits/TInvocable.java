// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TInvocable")
public interface TInvocable  {

        @FameProperty(name = "incomingInvocations", opposite = "candidates", derived = true)
    public Collection<TInvocation> getIncomingInvocations();

    public void setIncomingInvocations(Collection<? extends TInvocation> incomingInvocations);

    public void addIncomingInvocations(TInvocation one);

    public void addIncomingInvocations(TInvocation one, TInvocation... many);

    public void addIncomingInvocations(Iterable<? extends TInvocation> many);

    public void addIncomingInvocations(TInvocation[] many);

    public int numberOfIncomingInvocations();

    public boolean hasIncomingInvocations();



}

