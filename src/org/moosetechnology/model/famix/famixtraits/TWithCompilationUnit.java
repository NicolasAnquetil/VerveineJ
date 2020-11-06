// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithCompilationUnit")
public interface TWithCompilationUnit  {

        @FameProperty(name = "compilationUnit", opposite = "compilationUnitOwner", derived = true)
    public TCompilationUnit getCompilationUnit();

    public void setCompilationUnit(TCompilationUnit compilationUnit);



}

