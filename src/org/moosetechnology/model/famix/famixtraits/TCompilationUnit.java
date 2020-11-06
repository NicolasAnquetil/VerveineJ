// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TCompilationUnit")
public interface TCompilationUnit  {

        @FameProperty(name = "compilationUnitOwner", opposite = "compilationUnit")
    public TWithCompilationUnit getCompilationUnitOwner();

    public void setCompilationUnitOwner(TWithCompilationUnit compilationUnitOwner);



}

