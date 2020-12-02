// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.internal.MultivalueSet;
import org.moosetechnology.model.famixjava.famixreplication.Replica;
import org.moosetechnology.model.famixjava.famixtraits.*;
import org.moosetechnology.model.famixjava.moosequery.TEntityMetaLevelDependency;
import org.moosetechnology.model.famixjava.moosequery.TOODependencyQueries;

import java.util.Collection;


@FamePackage("FamixJavaEntities")
@FameDescription("Namespace")
public class Namespace extends ContainerEntity implements TNamedEntity, TWithGlobalVariables, TSourceEntity, TEntityMetaLevelDependency, TOODependencyQueries, TNamespace, TWithSourceLanguage, TWithComments {

    private Collection<Namespace> childNamespaces;

    private Namespace parentNamespace;

    private Number numberOfLinesOfCode;

    private Collection<TGlobalVariable> globalVariables;

    private String name;

    private Collection<TComment> comments;

    private Boolean isStub;

    private TSourceLanguage declaredSourceLanguage;

    private TSourceAnchor sourceAnchor;


    @FameProperty(name = "efferentCoupling", derived = true)
    public Number getEfferentCoupling() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfAttributes", derived = true)
    public Number getNumberOfAttributes() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfMethods", derived = true)
    public Number getNumberOfMethods() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "distance", derived = true)
    public Number getDistance() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "afferentCoupling", derived = true)
    public Number getAfferentCoupling() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "childNamespaces", opposite = "parentNamespace", derived = true)
    public Collection<Namespace> getChildNamespaces() {
        if (childNamespaces == null) {
            childNamespaces = new MultivalueSet<Namespace>() {
                @Override
                protected void clearOpposite(Namespace e) {
                    e.setParentNamespace(null);
                }

                @Override
                protected void setOpposite(Namespace e) {
                    e.setParentNamespace(Namespace.this);
                }
            };
        }
        return childNamespaces;
    }

    public void setChildNamespaces(Collection<? extends Namespace> childNamespaces) {
        this.getChildNamespaces().clear();
        this.getChildNamespaces().addAll(childNamespaces);
    }


    public void addChildNamespaces(Namespace one) {
        this.getChildNamespaces().add(one);
    }

    public void addChildNamespaces(Namespace one, Namespace... many) {
        this.getChildNamespaces().add(one);
        for (Namespace each : many)
            this.getChildNamespaces().add(each);
    }

    public void addChildNamespaces(Iterable<? extends Namespace> many) {
        for (Namespace each : many)
            this.getChildNamespaces().add(each);
    }

    public void addChildNamespaces(Namespace[] many) {
        for (Namespace each : many)
            this.getChildNamespaces().add(each);
    }

    public int numberOfChildNamespaces() {
        return getChildNamespaces().size();
    }

    public boolean hasChildNamespaces() {
        return !getChildNamespaces().isEmpty();
    }

    @FameProperty(name = "instability", derived = true)
    public Number getInstability() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "parentNamespace", opposite = "childNamespaces", container = true)
    public Namespace getParentNamespace() {
        return parentNamespace;
    }

    public void setParentNamespace(Namespace parentNamespace) {
        if (this.parentNamespace != null) {
            if (this.parentNamespace.equals(parentNamespace)) return;
            this.parentNamespace.getChildNamespaces().remove(this);
        }
        this.parentNamespace = parentNamespace;
        if (parentNamespace == null) return;
        parentNamespace.getChildNamespaces().add(this);
    }

    @FameProperty(name = "numberOfNonInterfacesClasses", derived = true)
    public Number getNumberOfNonInterfacesClasses() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "abstractness", derived = true)
    public Number getAbstractness() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "martinCohesion", derived = true)
    public Number getMartinCohesion() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "bunchCohesion", derived = true)
    public Number getBunchCohesion() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfDeadChildren", derived = true)
    public Number getNumberOfDeadChildren() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "containsReplicas", derived = true)
    public Boolean getContainsReplicas() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfLinesOfCodeWithMoreThanOneCharacter", derived = true)
    public Number getNumberOfLinesOfCodeWithMoreThanOneCharacter() {
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

    @FameProperty(name = "numberOfLinesOfCode")
    public Number getNumberOfLinesOfCode() {
        return numberOfLinesOfCode;
    }

    public void setNumberOfLinesOfCode(Number numberOfLinesOfCode) {
        this.numberOfLinesOfCode = numberOfLinesOfCode;
    }

    @FameProperty(name = "numberOfComments", derived = true)
    public Number getNumberOfComments() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "globalVariables", opposite = "parentScope", derived = true)
    public Collection<TGlobalVariable> getGlobalVariables() {
        if (globalVariables == null) {
            globalVariables = new MultivalueSet<TGlobalVariable>() {
                @Override
                protected void clearOpposite(TGlobalVariable e) {
                    e.setParentScope(null);
                }

                @Override
                protected void setOpposite(TGlobalVariable e) {
                    e.setParentScope(Namespace.this);
                }
            };
        }
        return globalVariables;
    }

    public void setGlobalVariables(Collection<? extends TGlobalVariable> globalVariables) {
        this.getGlobalVariables().clear();
        this.getGlobalVariables().addAll(globalVariables);
    }


    public void addGlobalVariables(TGlobalVariable one) {
        this.getGlobalVariables().add(one);
    }

    public void addGlobalVariables(TGlobalVariable one, TGlobalVariable... many) {
        this.getGlobalVariables().add(one);
        for (TGlobalVariable each : many)
            this.getGlobalVariables().add(each);
    }

    public void addGlobalVariables(Iterable<? extends TGlobalVariable> many) {
        for (TGlobalVariable each : many)
            this.getGlobalVariables().add(each);
    }

    public void addGlobalVariables(TGlobalVariable[] many) {
        for (TGlobalVariable each : many)
            this.getGlobalVariables().add(each);
    }

    public int numberOfGlobalVariables() {
        return getGlobalVariables().size();
    }

    public boolean hasGlobalVariables() {
        return !getGlobalVariables().isEmpty();
    }

    @FameProperty(name = "isDead", derived = true)
    public Boolean getIsDead() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "hasComments", derived = true)
    public Boolean getHasComments() {
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
                    e.setContainer(Namespace.this);
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


}

