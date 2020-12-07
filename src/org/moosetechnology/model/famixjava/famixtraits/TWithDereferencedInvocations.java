// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithDereferencedInvocations")
public interface TWithDereferencedInvocations {

    @FameProperty(name = "dereferencedInvocations", opposite = "referencer", derived = true)
    Collection<TDereferencedInvocation> getDereferencedInvocations();

    void setDereferencedInvocations(Collection<? extends TDereferencedInvocation> dereferencedInvocations);

    void addDereferencedInvocations(TDereferencedInvocation one);

    void addDereferencedInvocations(TDereferencedInvocation one, TDereferencedInvocation... many);

    void addDereferencedInvocations(Iterable<? extends TDereferencedInvocation> many);

    void addDereferencedInvocations(TDereferencedInvocation[] many);

    int numberOfDereferencedInvocations();

    boolean hasDereferencedInvocations();


}

