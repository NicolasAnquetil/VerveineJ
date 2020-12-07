// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;

import java.util.Collection;


@FamePackage("FamixTraits")
@FameDescription("TWithAccesses")
public interface TWithAccesses {

    @FameProperty(name = "accesses", opposite = "accessor", derived = true)
    Collection<TAccess> getAccesses();

    void setAccesses(Collection<? extends TAccess> accesses);

    void addAccesses(TAccess one);

    void addAccesses(TAccess one, TAccess... many);

    void addAccesses(Iterable<? extends TAccess> many);

    void addAccesses(TAccess[] many);

    int numberOfAccesses();

    boolean hasAccesses();


}

