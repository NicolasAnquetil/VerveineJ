// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("Famix-Traits")
@FameDescription("TMethod")
public interface TMethod extends TWithReferences, TEntityMetaLevelDependency, TMethodMetrics, TSourceEntity, TWithAccesses, TWithImplicitVariables, TWithParameters, TInvocable, TWithInvocations, TWithLocalVariables, TNamedEntity, THasSignature, TTypedEntity, TWithStatements {

        @FameProperty(name = "hasClassScope", derived = true)
    public Boolean getHasClassScope();

    @FameProperty(name = "parentType", opposite = "methods", container = true)
    public TWithMethods getParentType();

    public void setParentType(TWithMethods parentType);



}

