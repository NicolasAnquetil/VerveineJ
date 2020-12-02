// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.internal.MultivalueSet;
import org.moosetechnology.model.famixjava.famixreplication.Replica;
import org.moosetechnology.model.famixjava.famixtraits.*;
import org.moosetechnology.model.famixjava.moosequery.TEntityMetaLevelDependency;

import java.util.Collection;


@FamePackage("FamixJavaEntities")
@FameDescription("Type")
public class Type extends ContainerEntity implements TNamedEntity, TWithMethods, TSourceEntity, TWithTypeAliases, TEntityMetaLevelDependency, TReferenceable, TType, TWithSourceLanguage, TParameterizedTypeUser, TWithComments {

    private ContainerEntity container;

    private TWithTypes typeContainer;

    private Collection<TReference> incomingReferences;

    private Collection<TComment> comments;

    private Boolean isStub;

    private TSourceAnchor sourceAnchor;

    private Collection<TWithParameterizedTypeUsers> argumentsInParameterizedTypes;

    private Collection<TTypedEntity> typedEntities;

    private Number numberOfLinesOfCode;

    private Collection<TTypeAlias> typeAliases;

    private String name;

    private TSourceLanguage declaredSourceLanguage;

    private Collection<TMethod> methods;


    @FameProperty(name = "container")
    public ContainerEntity getContainer() {
        return container;
    }

    public void setContainer(ContainerEntity container) {
        this.container = container;
    }

    @FameProperty(name = "isJUnit4TestCase", derived = true)
    public Boolean getIsJUnit4TestCase() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "isTestCase", derived = true)
    public Boolean getIsTestCase() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "isAbstract", derived = true)
    public Boolean getIsAbstract() {
        return this.hasModifier("abstract");
    }

    @FameProperty(name = "isInnerClass", derived = true)
    public Boolean getIsInnerClass() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "tightClassCohesion", derived = true)
    public Number getTightClassCohesion() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfDeadChildren", derived = true)
    public Number getNumberOfDeadChildren() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "typeContainer", opposite = "types", container = true)
    public TWithTypes getTypeContainer() {
        return typeContainer;
    }

    public void setTypeContainer(TWithTypes typeContainer) {
        if (this.typeContainer != null) {
            if (this.typeContainer.equals(typeContainer)) return;
            this.typeContainer.getTypes().remove(this);
        }
        this.typeContainer = typeContainer;
        if (typeContainer == null) return;
        typeContainer.getTypes().add(this);
    }

    @FameProperty(name = "incomingReferences", opposite = "referredType", derived = true)
    public Collection<TReference> getIncomingReferences() {
        if (incomingReferences == null) {
            incomingReferences = new MultivalueSet<TReference>() {
                @Override
                protected void clearOpposite(TReference e) {
                    e.setReferredType(null);
                }

                @Override
                protected void setOpposite(TReference e) {
                    e.setReferredType(Type.this);
                }
            };
        }
        return incomingReferences;
    }

    public void setIncomingReferences(Collection<? extends TReference> incomingReferences) {
        this.getIncomingReferences().clear();
        this.getIncomingReferences().addAll(incomingReferences);
    }


    public void addIncomingReferences(TReference one) {
        this.getIncomingReferences().add(one);
    }

    public void addIncomingReferences(TReference one, TReference... many) {
        this.getIncomingReferences().add(one);
        for (TReference each : many)
            this.getIncomingReferences().add(each);
    }

    public void addIncomingReferences(Iterable<? extends TReference> many) {
        for (TReference each : many)
            this.getIncomingReferences().add(each);
    }

    public void addIncomingReferences(TReference[] many) {
        for (TReference each : many)
            this.getIncomingReferences().add(each);
    }

    public int numberOfIncomingReferences() {
        return getIncomingReferences().size();
    }

    public boolean hasIncomingReferences() {
        return !getIncomingReferences().isEmpty();
    }

    @FameProperty(name = "containsReplicas", derived = true)
    public Boolean getContainsReplicas() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfChildren", derived = true)
    public Number getNumberOfChildren() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "replicas", derived = true)
    public Replica getReplicas() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfComments", derived = true)
    public Number getNumberOfComments() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfPrivateMethods", derived = true)
    public Number getNumberOfPrivateMethods() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfConstructorMethods", derived = true)
    public Number getNumberOfConstructorMethods() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "sourceText", derived = true)
    public String getSourceText() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "comments", opposite = "container", derived = true)
    public Collection<TComment> getComments() {
        if (comments == null) {
            comments = new MultivalueSet<TComment>() {
                @Override
                protected void clearOpposite(TComment e) {
                    e.setContainer(null);
                }

                @Override
                protected void setOpposite(TComment e) {
                    e.setContainer(Type.this);
                }
            };
        }
        return comments;
    }

    public void setComments(Collection<? extends TComment> comments) {
        this.getComments().clear();
        this.getComments().addAll(comments);
    }


    public void addComments(TComment one) {
        this.getComments().add(one);
    }

    public void addComments(TComment one, TComment... many) {
        this.getComments().add(one);
        for (TComment each : many)
            this.getComments().add(each);
    }

    public void addComments(Iterable<? extends TComment> many) {
        for (TComment each : many)
            this.getComments().add(each);
    }

    public void addComments(TComment[] many) {
        for (TComment each : many)
            this.getComments().add(each);
    }

    public int numberOfComments() {
        return getComments().size();
    }

    public boolean hasComments() {
        return !getComments().isEmpty();
    }

    @FameProperty(name = "isStub")
    public Boolean getIsStub() {
        return isStub;
    }

    public void setIsStub(Boolean isStub) {
        this.isStub = isStub;
    }

    @FameProperty(name = "numberOfMethods", derived = true)
    public Number getNumberOfMethods() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "fanIn", derived = true)
    public Number getFanIn() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "fanOut", derived = true)
    public Number getFanOut() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfDuplicatedLinesOfCodeInternally", derived = true)
    public Number getNumberOfDuplicatedLinesOfCodeInternally() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "sourceAnchor", opposite = "element", derived = true)
    public TSourceAnchor getSourceAnchor() {
        return sourceAnchor;
    }

    public void setSourceAnchor(TSourceAnchor sourceAnchor) {
        if (this.sourceAnchor == null ? sourceAnchor != null : !this.sourceAnchor.equals(sourceAnchor)) {
            TSourceAnchor old_sourceAnchor = this.sourceAnchor;
            this.sourceAnchor = sourceAnchor;
            if (old_sourceAnchor != null) old_sourceAnchor.setElement(null);
            if (sourceAnchor != null) sourceAnchor.setElement(this);
        }
    }

    @FameProperty(name = "duplicationRate", derived = true)
    public Number getDuplicationRate() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "weightedMethodCount", derived = true)
    public Number getWeightedMethodCount() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "argumentsInParameterizedTypes", opposite = "arguments", derived = true)
    public Collection<TWithParameterizedTypeUsers> getArgumentsInParameterizedTypes() {
        if (argumentsInParameterizedTypes == null) {
            argumentsInParameterizedTypes = new MultivalueSet<TWithParameterizedTypeUsers>() {
                @Override
                protected void clearOpposite(TWithParameterizedTypeUsers e) {
                    e.getArguments().remove(Type.this);
                }

                @Override
                protected void setOpposite(TWithParameterizedTypeUsers e) {
                    e.getArguments().add(Type.this);
                }
            };
        }
        return argumentsInParameterizedTypes;
    }

    public void setArgumentsInParameterizedTypes(Collection<? extends TWithParameterizedTypeUsers> argumentsInParameterizedTypes) {
        this.getArgumentsInParameterizedTypes().clear();
        this.getArgumentsInParameterizedTypes().addAll(argumentsInParameterizedTypes);
    }

    public void addArgumentsInParameterizedTypes(TWithParameterizedTypeUsers one) {
        this.getArgumentsInParameterizedTypes().add(one);
    }

    public void addArgumentsInParameterizedTypes(TWithParameterizedTypeUsers one, TWithParameterizedTypeUsers... many) {
        this.getArgumentsInParameterizedTypes().add(one);
        for (TWithParameterizedTypeUsers each : many)
            this.getArgumentsInParameterizedTypes().add(each);
    }

    public void addArgumentsInParameterizedTypes(Iterable<? extends TWithParameterizedTypeUsers> many) {
        for (TWithParameterizedTypeUsers each : many)
            this.getArgumentsInParameterizedTypes().add(each);
    }

    public void addArgumentsInParameterizedTypes(TWithParameterizedTypeUsers[] many) {
        for (TWithParameterizedTypeUsers each : many)
            this.getArgumentsInParameterizedTypes().add(each);
    }

    public int numberOfArgumentsInParameterizedTypes() {
        return getArgumentsInParameterizedTypes().size();
    }

    public boolean hasArgumentsInParameterizedTypes() {
        return !getArgumentsInParameterizedTypes().isEmpty();
    }

    @FameProperty(name = "typedEntities", opposite = "declaredType", derived = true)
    public Collection<TTypedEntity> getTypedEntities() {
        if (typedEntities == null) {
            typedEntities = new MultivalueSet<TTypedEntity>() {
                @Override
                protected void clearOpposite(TTypedEntity e) {
                    e.setDeclaredType(null);
                }

                @Override
                protected void setOpposite(TTypedEntity e) {
                    e.setDeclaredType(Type.this);
                }
            };
        }
        return typedEntities;
    }

    public void setTypedEntities(Collection<? extends TTypedEntity> typedEntities) {
        this.getTypedEntities().clear();
        this.getTypedEntities().addAll(typedEntities);
    }


    public void addTypedEntities(TTypedEntity one) {
        this.getTypedEntities().add(one);
    }

    public void addTypedEntities(TTypedEntity one, TTypedEntity... many) {
        this.getTypedEntities().add(one);
        for (TTypedEntity each : many)
            this.getTypedEntities().add(each);
    }

    public void addTypedEntities(Iterable<? extends TTypedEntity> many) {
        for (TTypedEntity each : many)
            this.getTypedEntities().add(each);
    }

    public void addTypedEntities(TTypedEntity[] many) {
        for (TTypedEntity each : many)
            this.getTypedEntities().add(each);
    }

    public int numberOfTypedEntities() {
        return getTypedEntities().size();
    }

    public boolean hasTypedEntities() {
        return !getTypedEntities().isEmpty();
    }

    @FameProperty(name = "numberOfAccessesToForeignData", derived = true)
    public Number getNumberOfAccessesToForeignData() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfLinesOfCodeWithMoreThanOneCharacter", derived = true)
    public Number getNumberOfLinesOfCodeWithMoreThanOneCharacter() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfAbstractMethods", derived = true)
    public Number getNumberOfAbstractMethods() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfLinesOfCode")
    public Number getNumberOfLinesOfCode() {
        return numberOfLinesOfCode;
    }

    public void setNumberOfLinesOfCode(Number numberOfLinesOfCode) {
        this.numberOfLinesOfCode = numberOfLinesOfCode;
    }

    @FameProperty(name = "isDead", derived = true)
    public Boolean getIsDead() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "typeAliases", opposite = "aliasedType", derived = true)
    public Collection<TTypeAlias> getTypeAliases() {
        if (typeAliases == null) {
            typeAliases = new MultivalueSet<TTypeAlias>() {
                @Override
                protected void clearOpposite(TTypeAlias e) {
                    e.setAliasedType(null);
                }

                @Override
                protected void setOpposite(TTypeAlias e) {
                    e.setAliasedType(Type.this);
                }
            };
        }
        return typeAliases;
    }

    public void setTypeAliases(Collection<? extends TTypeAlias> typeAliases) {
        this.getTypeAliases().clear();
        this.getTypeAliases().addAll(typeAliases);
    }


    public void addTypeAliases(TTypeAlias one) {
        this.getTypeAliases().add(one);
    }

    public void addTypeAliases(TTypeAlias one, TTypeAlias... many) {
        this.getTypeAliases().add(one);
        for (TTypeAlias each : many)
            this.getTypeAliases().add(each);
    }

    public void addTypeAliases(Iterable<? extends TTypeAlias> many) {
        for (TTypeAlias each : many)
            this.getTypeAliases().add(each);
    }

    public void addTypeAliases(TTypeAlias[] many) {
        for (TTypeAlias each : many)
            this.getTypeAliases().add(each);
    }

    public int numberOfTypeAliases() {
        return getTypeAliases().size();
    }

    public boolean hasTypeAliases() {
        return !getTypeAliases().isEmpty();
    }

    @FameProperty(name = "hasComments", derived = true)
    public Boolean getHasComments() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfProtectedMethods", derived = true)
    public Number getNumberOfProtectedMethods() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @FameProperty(name = "numberOfAccessorMethods", derived = true)
    public Number getNumberOfAccessorMethods() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "declaredSourceLanguage", opposite = "sourcedEntities")
    public TSourceLanguage getDeclaredSourceLanguage() {
        return declaredSourceLanguage;
    }

    public void setDeclaredSourceLanguage(TSourceLanguage declaredSourceLanguage) {
        if (this.declaredSourceLanguage != null) {
            if (this.declaredSourceLanguage.equals(declaredSourceLanguage)) return;
            this.declaredSourceLanguage.getSourcedEntities().remove(this);
        }
        this.declaredSourceLanguage = declaredSourceLanguage;
        if (declaredSourceLanguage == null) return;
        declaredSourceLanguage.getSourcedEntities().add(this);
    }

    @FameProperty(name = "methods", opposite = "parentType", derived = true)
    public Collection<TMethod> getMethods() {
        if (methods == null) {
            methods = new MultivalueSet<TMethod>() {
                @Override
                protected void clearOpposite(TMethod e) {
                    e.setParentType(null);
                }

                @Override
                protected void setOpposite(TMethod e) {
                    e.setParentType(Type.this);
                }
            };
        }
        return methods;
    }

    public void setMethods(Collection<? extends TMethod> methods) {
        this.getMethods().clear();
        this.getMethods().addAll(methods);
    }


    public void addMethods(TMethod one) {
        this.getMethods().add(one);
    }

    public void addMethods(TMethod one, TMethod... many) {
        this.getMethods().add(one);
        for (TMethod each : many)
            this.getMethods().add(each);
    }

    public void addMethods(Iterable<? extends TMethod> many) {
        for (TMethod each : many)
            this.getMethods().add(each);
    }

    public void addMethods(TMethod[] many) {
        for (TMethod each : many)
            this.getMethods().add(each);
    }

    public int numberOfMethods() {
        return getMethods().size();
    }

    public boolean hasMethods() {
        return !getMethods().isEmpty();
    }

    @FameProperty(name = "numberOfMessageSends", derived = true)
    public Number getNumberOfMessageSends() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfPublicMethods", derived = true)
    public Number getNumberOfPublicMethods() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }


}

