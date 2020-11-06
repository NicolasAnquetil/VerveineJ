// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithDeclaredExceptions")
public interface TWithDeclaredExceptions  {

        @FameProperty(name = "declaredExceptions", opposite = "definingEntity", derived = true)
    public Collection<TDeclaredException> getDeclaredExceptions();

    public void setDeclaredExceptions(Collection<? extends TDeclaredException> declaredExceptions);

    public void addDeclaredExceptions(TDeclaredException one);

    public void addDeclaredExceptions(TDeclaredException one, TDeclaredException... many);

    public void addDeclaredExceptions(Iterable<? extends TDeclaredException> many);

    public void addDeclaredExceptions(TDeclaredException[] many);

    public int numberOfDeclaredExceptions();

    public boolean hasDeclaredExceptions();



}

