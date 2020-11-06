// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithAccesses")
public interface TWithAccesses  {

        @FameProperty(name = "accesses", opposite = "accessor", derived = true)
    public Collection<TAccess> getAccesses();

    public void setAccesses(Collection<? extends TAccess> accesses);

    public void addAccesses(TAccess one);

    public void addAccesses(TAccess one, TAccess... many);

    public void addAccesses(Iterable<? extends TAccess> many);

    public void addAccesses(TAccess[] many);

    public int numberOfAccesses();

    public boolean hasAccesses();



}

