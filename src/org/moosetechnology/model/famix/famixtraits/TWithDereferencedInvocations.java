// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithDereferencedInvocations")
public interface TWithDereferencedInvocations  {

        @FameProperty(name = "dereferencedInvocations", opposite = "referencer", derived = true)
    public Collection<TDereferencedInvocation> getDereferencedInvocations();

    public void setDereferencedInvocations(Collection<? extends TDereferencedInvocation> dereferencedInvocations);

    public void addDereferencedInvocations(TDereferencedInvocation one);

    public void addDereferencedInvocations(TDereferencedInvocation one, TDereferencedInvocation... many);

    public void addDereferencedInvocations(Iterable<? extends TDereferencedInvocation> many);

    public void addDereferencedInvocations(TDereferencedInvocation[] many);

    public int numberOfDereferencedInvocations();

    public boolean hasDereferencedInvocations();



}

