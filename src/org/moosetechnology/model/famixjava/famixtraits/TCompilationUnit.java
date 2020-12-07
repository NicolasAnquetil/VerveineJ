// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TCompilationUnit")
public interface TCompilationUnit {

    @FameProperty(name = "compilationUnitOwner", opposite = "compilationUnit")
    TWithCompilationUnit getCompilationUnitOwner();

    void setCompilationUnitOwner(TWithCompilationUnit compilationUnitOwner);


}

