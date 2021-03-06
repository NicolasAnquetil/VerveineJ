// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.moosequery.TEntityMetaLevelDependency;
import org.moosetechnology.model.famixjava.moosequery.TOODependencyQueries;


@FamePackage("Famix-Traits")
@FameDescription("TMethod")
public interface TMethod  {

        @FameProperty(name = "isGetter", derived = true)
    public Boolean getIsGetter();

    @FameProperty(name = "isConstant", derived = true)
    public Boolean getIsConstant();

    @FameProperty(name = "hierarchyNestingLevel", derived = true)
    public Number getHierarchyNestingLevel();

    @FameProperty(name = "kind")
    public String getKind();

    public void setKind(String kind);

    @FameProperty(name = "isConstructor", derived = true)
    public Boolean getIsConstructor();

    @FameProperty(name = "isSetter", derived = true)
    public Boolean getIsSetter();

    @FameProperty(name = "numberOfMessageSends", derived = true)
    public Number getNumberOfMessageSends();

    @FameProperty(name = "hasClassScope", derived = true)
    public Boolean getHasClassScope();

    @FameProperty(name = "isClassSide")
    public Boolean getIsClassSide();

    public void setIsClassSide(Boolean isClassSide);

    @FameProperty(name = "isAbstract")
    public Boolean getIsAbstract();

    public void setIsAbstract(Boolean isAbstract);

    @FameProperty(name = "parentType", opposite = "methods", container = true)
    public TWithMethods getParentType();

    public void setParentType(TWithMethods parentType);



}

