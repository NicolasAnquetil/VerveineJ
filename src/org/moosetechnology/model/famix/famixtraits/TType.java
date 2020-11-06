// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;


@FamePackage("FamixTraits")
@FameDescription("TType")
public interface TType  {

        @FameProperty(name = "numberOfAccessesToForeignData", derived = true)
    public Number getNumberOfAccessesToForeignData();

    @FameProperty(name = "typeContainer", opposite = "types", container = true)
    public TWithTypes getTypeContainer();

    public void setTypeContainer(TWithTypes typeContainer);

    @FameProperty(name = "typedEntities", opposite = "declaredType", derived = true)
    public Collection<TTypedEntity> getTypedEntities();

    public void setTypedEntities(Collection<? extends TTypedEntity> typedEntities);

    public void addTypedEntities(TTypedEntity one);

    public void addTypedEntities(TTypedEntity one, TTypedEntity... many);

    public void addTypedEntities(Iterable<? extends TTypedEntity> many);

    public void addTypedEntities(TTypedEntity[] many);

    public int numberOfTypedEntities();

    public boolean hasTypedEntities();

    @FameProperty(name = "numberOfDuplicatedLinesOfCodeInternally", derived = true)
    public Number getNumberOfDuplicatedLinesOfCodeInternally();



}

