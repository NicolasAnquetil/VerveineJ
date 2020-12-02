// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TWithCompilationUnit")
public interface TWithCompilationUnit {

    @FameProperty(name = "compilationUnit", opposite = "compilationUnitOwner", derived = true)
    TCompilationUnit getCompilationUnit();

    void setCompilationUnit(TCompilationUnit compilationUnit);


}

