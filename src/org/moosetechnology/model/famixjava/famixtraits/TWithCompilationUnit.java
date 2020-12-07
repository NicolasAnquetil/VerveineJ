// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Famix-Traits")
@FameDescription("TWithCompilationUnit")
public interface TWithCompilationUnit  {

        @FameProperty(name = "compilationUnit", opposite = "compilationUnitOwner", derived = true)
    public TCompilationUnit getCompilationUnit();

    public void setCompilationUnit(TCompilationUnit compilationUnit);



}

