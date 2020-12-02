// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TInvocable")
public interface TInvocable {

    @FameProperty(name = "incomingInvocations", opposite = "candidates", derived = true)
    Collection<TInvocation> getIncomingInvocations();

    void setIncomingInvocations(Collection<? extends TInvocation> incomingInvocations);

    void addIncomingInvocations(TInvocation one);

    void addIncomingInvocations(TInvocation one, TInvocation... many);

    void addIncomingInvocations(Iterable<? extends TInvocation> many);

    void addIncomingInvocations(TInvocation[] many);

    int numberOfIncomingInvocations();

    boolean hasIncomingInvocations();


}

