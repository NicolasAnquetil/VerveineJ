// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import org.moosetechnology.model.famix.moosequery.TAssociationMetaLevelDependency;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Famix-Traits")
@FameDescription("TImplementation")
public interface TImplementation extends TAssociationMetaLevelDependency, TAssociation, TSourceEntity {

        @FameProperty(name = "interface", opposite = "implementations")
    public TImplementable getMyInterface();

    public void setMyInterface(TImplementable myInterface);

    @FameProperty(name = "implementingClass", opposite = "interfaceImplementations")
    public TCanImplement getImplementingClass();

    public void setImplementingClass(TCanImplement implementingClass);



}

