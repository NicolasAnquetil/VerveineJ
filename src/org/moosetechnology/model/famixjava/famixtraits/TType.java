// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("Famix-Traits")
@FameDescription("TType")
public interface TType  {

        @FameProperty(name = "typeContainer", opposite = "types", container = true)
        TWithTypes getTypeContainer();

    void setTypeContainer(TWithTypes typeContainer);

    @FameProperty(name = "typedEntities", opposite = "declaredType", derived = true)
    Collection<TTypedEntity> getTypedEntities();

    void setTypedEntities(Collection<? extends TTypedEntity> typedEntities);

    void addTypedEntities(TTypedEntity one);

    void addTypedEntities(TTypedEntity one, TTypedEntity... many);

    void addTypedEntities(Iterable<? extends TTypedEntity> many);

    void addTypedEntities(TTypedEntity[] many);

    int numberOfTypedEntities();

    boolean hasTypedEntities();



}

