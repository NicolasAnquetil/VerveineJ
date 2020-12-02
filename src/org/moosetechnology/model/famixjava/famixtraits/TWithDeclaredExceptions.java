// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithDeclaredExceptions")
public interface TWithDeclaredExceptions {

    @FameProperty(name = "declaredExceptions", opposite = "definingEntity", derived = true)
    Collection<TDeclaredException> getDeclaredExceptions();

    void setDeclaredExceptions(Collection<? extends TDeclaredException> declaredExceptions);

    void addDeclaredExceptions(TDeclaredException one);

    void addDeclaredExceptions(TDeclaredException one, TDeclaredException... many);

    void addDeclaredExceptions(Iterable<? extends TDeclaredException> many);

    void addDeclaredExceptions(TDeclaredException[] many);

    int numberOfDeclaredExceptions();

    boolean hasDeclaredExceptions();


}

