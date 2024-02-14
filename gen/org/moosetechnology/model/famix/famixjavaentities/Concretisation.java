// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.internal.MultivalueSet;
import java.util.*;
import org.moosetechnology.model.famix.famixreplication.Replica;
import org.moosetechnology.model.famix.famixtraits.TAssociation;
import org.moosetechnology.model.famix.famixtraits.TConcretisation;
import org.moosetechnology.model.famix.famixtraits.TParameterConcretisation;
import org.moosetechnology.model.famix.famixtraits.TParametricEntity;
import org.moosetechnology.model.famix.famixtraits.TSourceAnchor;
import org.moosetechnology.model.famix.famixtraits.TSourceEntity;
import org.moosetechnology.model.famix.moosequery.TAssociationMetaLevelDependency;


@FamePackage("Famix-Java-Entities")
@FameDescription("Concretisation")
public class Concretisation extends Entity implements TAssociation, TAssociationMetaLevelDependency, TConcretisation, TSourceEntity {

    private TParametricEntity concreteEntity;
    
    private TParametricEntity genericEntity;
    
    private Boolean isStub;
    
    private TAssociation next;
    
    private Number numberOfLinesOfCode;
    
    private Collection<TParameterConcretisation> parameterConcretisations; 

    private TAssociation previous;
    
    private TSourceAnchor sourceAnchor;
    


    @FameProperty(name = "concreteEntity", opposite = "genericEntity")
    public TParametricEntity getConcreteEntity() {
        return concreteEntity;
    }

    public void setConcreteEntity(TParametricEntity concreteEntity) {
        if (this.concreteEntity == null ? concreteEntity != null : !this.concreteEntity.equals(concreteEntity)) {
            TParametricEntity old_concreteEntity = this.concreteEntity;
            this.concreteEntity = concreteEntity;
            if (old_concreteEntity != null) old_concreteEntity.setGenericEntity(null);
            if (concreteEntity != null) concreteEntity.setGenericEntity(this);
        }
    }
    
    @FameProperty(name = "containsReplicas", derived = true)
    public Boolean getContainsReplicas() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "duplicationRate", derived = true)
    public Number getDuplicationRate() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "genericEntity", opposite = "concretisations")
    public TParametricEntity getGenericEntity() {
        return genericEntity;
    }

    public void setGenericEntity(TParametricEntity genericEntity) {
        if (this.genericEntity != null) {
            if (this.genericEntity.equals(genericEntity)) return;
            this.genericEntity.getConcretisations().remove(this);
        }
        this.genericEntity = genericEntity;
        if (genericEntity == null) return;
        genericEntity.getConcretisations().add(this);
    }
    
    @FameProperty(name = "isStub")
    public Boolean getIsStub() {
        return isStub;
    }

    public void setIsStub(Boolean isStub) {
        this.isStub = isStub;
    }
    
    @FameProperty(name = "next", opposite = "previous", derived = true)
    public TAssociation getNext() {
        return next;
    }

    public void setNext(TAssociation next) {
        if (this.next == null ? next != null : !this.next.equals(next)) {
            TAssociation old_next = this.next;
            this.next = next;
            if (old_next != null) old_next.setPrevious(null);
            if (next != null) next.setPrevious(this);
        }
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
    
    @FameProperty(name = "parameterConcretisations", opposite = "concretisations", derived = true)
    public Collection<TParameterConcretisation> getParameterConcretisations() {
        if (parameterConcretisations == null) {
            parameterConcretisations = new MultivalueSet<TParameterConcretisation>() {
                @Override
                protected void clearOpposite(TParameterConcretisation e) {
                    e.getConcretisations().remove(Concretisation.this);
                }
                @Override
                protected void setOpposite(TParameterConcretisation e) {
                    e.getConcretisations().add(Concretisation.this);
                }
            };
        }
        return parameterConcretisations;
    }
    
    public void setParameterConcretisations(Collection<? extends TParameterConcretisation> parameterConcretisations) {
        this.getParameterConcretisations().clear();
        this.getParameterConcretisations().addAll(parameterConcretisations);
    }
    
    public void addParameterConcretisations(TParameterConcretisation one) {
        this.getParameterConcretisations().add(one);
    }   
    
    public void addParameterConcretisations(TParameterConcretisation one, TParameterConcretisation... many) {
        this.getParameterConcretisations().add(one);
        for (TParameterConcretisation each : many)
            this.getParameterConcretisations().add(each);
    }   
    
    public void addParameterConcretisations(Iterable<? extends TParameterConcretisation> many) {
        for (TParameterConcretisation each : many)
            this.getParameterConcretisations().add(each);
    }   
                
    public void addParameterConcretisations(TParameterConcretisation[] many) {
        for (TParameterConcretisation each : many)
            this.getParameterConcretisations().add(each);
    }
    
    public int numberOfParameterConcretisations() {
        return getParameterConcretisations().size();
    }

    public boolean hasParameterConcretisations() {
        return !getParameterConcretisations().isEmpty();
    }

    @FameProperty(name = "previous", opposite = "next")
    public TAssociation getPrevious() {
        return previous;
    }

    public void setPrevious(TAssociation previous) {
        if (this.previous == null ? previous != null : !this.previous.equals(previous)) {
            TAssociation old_previous = this.previous;
            this.previous = previous;
            if (old_previous != null) old_previous.setNext(null);
            if (previous != null) previous.setNext(this);
        }
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
    


}
