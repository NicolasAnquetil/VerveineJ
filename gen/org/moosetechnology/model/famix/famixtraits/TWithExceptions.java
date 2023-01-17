// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import java.util.*;


@FamePackage("Famix-Traits")
@FameDescription("TWithExceptions")
public interface TWithExceptions  {

        @FameProperty(name = "thrownExceptions", opposite = "throwingEntities")
    public Collection<TException> getThrownExceptions();

    public void setThrownExceptions(Collection<? extends TException> thrownExceptions);

    public void addThrownExceptions(TException one);

    public void addThrownExceptions(TException one, TException... many);

    public void addThrownExceptions(Iterable<? extends TException> many);

    public void addThrownExceptions(TException[] many);

    public int numberOfThrownExceptions();

    public boolean hasThrownExceptions();

    @FameProperty(name = "caughtExceptions", opposite = "catchingEntities")
    public Collection<TException> getCaughtExceptions();

    public void setCaughtExceptions(Collection<? extends TException> caughtExceptions);

    public void addCaughtExceptions(TException one);

    public void addCaughtExceptions(TException one, TException... many);

    public void addCaughtExceptions(Iterable<? extends TException> many);

    public void addCaughtExceptions(TException[] many);

    public int numberOfCaughtExceptions();

    public boolean hasCaughtExceptions();

    @FameProperty(name = "declaredExceptions", opposite = "declaringEntities")
    public Collection<TException> getDeclaredExceptions();

    public void setDeclaredExceptions(Collection<? extends TException> declaredExceptions);

    public void addDeclaredExceptions(TException one);

    public void addDeclaredExceptions(TException one, TException... many);

    public void addDeclaredExceptions(Iterable<? extends TException> many);

    public void addDeclaredExceptions(TException[] many);

    public int numberOfDeclaredExceptions();

    public boolean hasDeclaredExceptions();



}

