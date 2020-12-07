// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TMethod")
public interface TMethod {

    @FameProperty(name = "isGetter", derived = true)
    Boolean getIsGetter();

    @FameProperty(name = "isConstant", derived = true)
    Boolean getIsConstant();

    @FameProperty(name = "hierarchyNestingLevel", derived = true)
    Number getHierarchyNestingLevel();

    @FameProperty(name = "kind")
    String getKind();

    void setKind(String kind);

    @FameProperty(name = "isConstructor", derived = true)
    Boolean getIsConstructor();

    @FameProperty(name = "isSetter", derived = true)
    Boolean getIsSetter();

    @FameProperty(name = "numberOfMessageSends", derived = true)
    Number getNumberOfMessageSends();

    @FameProperty(name = "hasClassScope", derived = true)
    Boolean getHasClassScope();

    @FameProperty(name = "isClassSide")
    Boolean getIsClassSide();

    void setIsClassSide(Boolean isClassSide);

    @FameProperty(name = "isAbstract")
    Boolean getIsAbstract();

    void setIsAbstract(Boolean isAbstract);

    @FameProperty(name = "parentType", opposite = "methods", container = true)
    TWithMethods getParentType();

    void setParentType(TWithMethods parentType);


}

