// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.moosequery.TEntityMetaLevelDependency;
import org.moosetechnology.model.famixjava.moosequery.TOODependencyQueries;


@FamePackage("Famix-Traits")
@FameDescription("TMethod")
public interface TMethod extends TOODependencyQueries, TWithReferences, TMethodMetrics, TSourceEntity, TWithAccesses, TWithImplicitVariables, TWithParameters, TInvocable, TWithInvocations, TWithLocalVariables, TNamedEntity, TEntityMetaLevelDependency, THasSignature, TTypedEntity, TWithStatements {

        @FameProperty(name = "hasClassScope", derived = true)
    public Boolean getHasClassScope();

    @FameProperty(name = "numberOfMessageSends", derived = true)
    public Number getNumberOfMessageSends();

    @FameProperty(name = "parentType", opposite = "methods", container = true)
    public TWithMethods getParentType();

    public void setParentType(TWithMethods parentType);



}

