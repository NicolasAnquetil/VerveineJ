// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.internal.MultivalueSet;
import java.util.*;
import org.moosetechnology.model.famixjava.famixreplication.Replica;
import org.moosetechnology.model.famixjava.famixtraits.TComment;
import org.moosetechnology.model.famixjava.famixtraits.TGlobalVariable;
import org.moosetechnology.model.famixjava.famixtraits.TNamedEntity;
import org.moosetechnology.model.famixjava.famixtraits.TPackage;
import org.moosetechnology.model.famixjava.famixtraits.TPackageable;
import org.moosetechnology.model.famixjava.famixtraits.TSourceAnchor;
import org.moosetechnology.model.famixjava.famixtraits.TSourceEntity;
import org.moosetechnology.model.famixjava.famixtraits.TSourceLanguage;
import org.moosetechnology.model.famixjava.famixtraits.TWithComments;
import org.moosetechnology.model.famixjava.famixtraits.TWithGlobalVariables;
import org.moosetechnology.model.famixjava.famixtraits.TWithPackages;
import org.moosetechnology.model.famixjava.famixtraits.TWithSourceLanguage;
import org.moosetechnology.model.famixjava.moosequery.TEntityMetaLevelDependency;


@FamePackage("Famix-Java-Entities")
@FameDescription("Package")
public class Package extends ContainerEntity implements TNamedEntity, TPackage, TWithGlobalVariables, TSourceEntity, TEntityMetaLevelDependency, TWithSourceLanguage, TWithComments {

    private Collection<TPackageable> childEntities; 

    private Collection<TComment> comments; 

    private TSourceLanguage declaredSourceLanguage;
    
    private Collection<TGlobalVariable> globalVariables; 

    private Boolean isStub;
    
    private String name;
    
    private Number numberOfLinesOfCode;
    
    private TWithPackages packageOwner;
    
    private TSourceAnchor sourceAnchor;
    


    @FameProperty(name = "abstractness", derived = true)
    public Number getAbstractness() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "afferentCoupling", derived = true)
    public Number getAfferentCoupling() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "bunchCohesion", derived = true)
    public Number getBunchCohesion() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "distance", derived = true)
    public Number getDistance() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "efferentCoupling", derived = true)
    public Number getEfferentCoupling() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "instability", derived = true)
    public Number getInstability() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "martinCohesion", derived = true)
    public Number getMartinCohesion() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOfClientPackages", derived = true)
    public Number getNumberOfClientPackages() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOfMethods", derived = true)
    public Number getNumberOfMethods() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "relativeImportanceForSystem", derived = true)
    public Number getRelativeImportanceForSystem() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "childEntities", opposite = "parentPackage", derived = true)
    public Collection<TPackageable> getChildEntities() {
        if (childEntities == null) {
            childEntities = new MultivalueSet<TPackageable>() {
                @Override
                protected void clearOpposite(TPackageable e) {
                    e.setParentPackage(null);
                }
                @Override
                protected void setOpposite(TPackageable e) {
                    e.setParentPackage(Package.this);
                }
            };
        }
        return childEntities;
    }
    
    public void setChildEntities(Collection<? extends TPackageable> childEntities) {
        this.getChildEntities().clear();
        this.getChildEntities().addAll(childEntities);
    }                    
    
        
    public void addChildEntities(TPackageable one) {
        this.getChildEntities().add(one);
    }   
    
    public void addChildEntities(TPackageable one, TPackageable... many) {
        this.getChildEntities().add(one);
        for (TPackageable each : many)
            this.getChildEntities().add(each);
    }   
    
    public void addChildEntities(Iterable<? extends TPackageable> many) {
        for (TPackageable each : many)
            this.getChildEntities().add(each);
    }   
                
    public void addChildEntities(TPackageable[] many) {
        for (TPackageable each : many)
            this.getChildEntities().add(each);
    }
    
    public int numberOfChildEntities() {
        return getChildEntities().size();
    }

    public boolean hasChildEntities() {
        return !getChildEntities().isEmpty();
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
                    e.setContainer(Package.this);
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

    @FameProperty(name = "containsReplicas", derived = true)
    public Boolean getContainsReplicas() {
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
    
    @FameProperty(name = "duplicationRate", derived = true)
    public Number getDuplicationRate() {
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
                    e.setParentScope(Package.this);
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

    @FameProperty(name = "hasComments", derived = true)
    public Boolean getHasComments() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "isDead", derived = true)
    public Boolean getIsDead() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "isStub")
    public Boolean getIsStub() {
        return isStub;
    }

    public void setIsStub(Boolean isStub) {
        this.isStub = isStub;
    }
    
    @FameProperty(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @FameProperty(name = "numberOfChildren", derived = true)
    public Number getNumberOfChildren() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOfComments", derived = true)
    public Number getNumberOfComments() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOfDeadChildren", derived = true)
    public Number getNumberOfDeadChildren() {
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
    
    @FameProperty(name = "numberOfLinesOfCodeWithMoreThanOneCharacter", derived = true)
    public Number getNumberOfLinesOfCodeWithMoreThanOneCharacter() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "packageOwner", opposite = "packages")
    public TWithPackages getPackageOwner() {
        return packageOwner;
    }

    public void setPackageOwner(TWithPackages packageOwner) {
        if (this.packageOwner != null) {
            if (this.packageOwner.equals(packageOwner)) return;
            this.packageOwner.getPackages().remove(this);
        }
        this.packageOwner = packageOwner;
        if (packageOwner == null) return;
        packageOwner.getPackages().add(this);
    }
    
    @FameProperty(name = "replicas", derived = true)
    public Replica getReplicas() {
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
    
    @FameProperty(name = "sourceText", derived = true)
    public String getSourceText() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "weightedMethodCount", derived = true)
    public Number getWeightedMethodCount() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    


}

