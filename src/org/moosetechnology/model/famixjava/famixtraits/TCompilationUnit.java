// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Famix-Traits")
@FameDescription("TCompilationUnit")
public interface TCompilationUnit  {

        @FameProperty(name = "compilationUnitOwner", opposite = "compilationUnit")
    public TWithCompilationUnit getCompilationUnitOwner();

    public void setCompilationUnitOwner(TWithCompilationUnit compilationUnitOwner);



}

