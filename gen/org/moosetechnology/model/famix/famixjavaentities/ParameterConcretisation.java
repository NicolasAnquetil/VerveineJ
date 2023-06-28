// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.internal.MultivalueSet;
import java.util.*;
import org.moosetechnology.model.famix.famixreplication.Replica;
import org.moosetechnology.model.famix.famixtraits.TAssociation;
import org.moosetechnology.model.famix.famixtraits.TConcreteParameterType;
import org.moosetechnology.model.famix.famixtraits.TConcretisation;
import org.moosetechnology.model.famix.famixtraits.TGenericParameterType;
import org.moosetechnology.model.famix.famixtraits.TParameterConcretisation;
import org.moosetechnology.model.famix.famixtraits.TSourceAnchor;
import org.moosetechnology.model.famix.famixtraits.TSourceEntity;
import org.moosetechnology.model.famix.moosequery.TAssociationMetaLevelDependency;


@FamePackage("Famix-Java-Entities")
@FameDescription("ParameterConcretisation")
public class ParameterConcretisation extends Entity implements TAssociation, TAssociationMetaLevelDependency, TParameterConcretisation, TSourceEntity {

    private TConcreteParameterType concreteParameters;
    
    private Collection<TConcretisation> concretisation; 

    private TGenericParameterType genericParameters;
    
    private Boolean isStub;
    
    private TAssociation next;
    
    private Number numberOfLinesOfCode;
    
    private TAssociation previous;
    
    private TSourceAnchor sourceAnchor;
    


    @FameProperty(name = "concreteParameters", opposite = "generic")
    public TConcreteParameterType getConcreteParameters() {
        return concreteParameters;
    }

    public void setConcreteParameters(TConcreteParameterType concreteParameters) {
        if (this.concreteParameters != null) {
            if (this.concreteParameters.equals(concreteParameters)) return;
            this.concreteParameters.getGeneric().remove(this);
        }
        this.concreteParameters = concreteParameters;
        if (concreteParameters == null) return;
        concreteParameters.getGeneric().add(this);
    }
    
    @FameProperty(name = "concretisation", opposite = "parameterConcretisation")
    public Collection<TConcretisation> getConcretisation() {
        if (concretisation == null) {
            concretisation = new MultivalueSet<TConcretisation>() {
                @Override
                protected void clearOpposite(TConcretisation e) {
                    e.getParameterConcretisation().remove(ParameterConcretisation.this);
                }
                @Override
                protected void setOpposite(TConcretisation e) {
                    e.getParameterConcretisation().add(ParameterConcretisation.this);
                }
            };
        }
        return concretisation;
    }
    
    public void setConcretisation(Collection<? extends TConcretisation> concretisation) {
        this.getConcretisation().clear();
        this.getConcretisation().addAll(concretisation);
    }
    
    public void addConcretisation(TConcretisation one) {
        this.getConcretisation().add(one);
    }   
    
    public void addConcretisation(TConcretisation one, TConcretisation... many) {
        this.getConcretisation().add(one);
        for (TConcretisation each : many)
            this.getConcretisation().add(each);
    }   
    
    public void addConcretisation(Iterable<? extends TConcretisation> many) {
        for (TConcretisation each : many)
            this.getConcretisation().add(each);
    }   
                
    public void addConcretisation(TConcretisation[] many) {
        for (TConcretisation each : many)
            this.getConcretisation().add(each);
    }
    
    public int numberOfConcretisation() {
        return getConcretisation().size();
    }

    public boolean hasConcretisation() {
        return !getConcretisation().isEmpty();
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
    
    @FameProperty(name = "genericParameters", opposite = "concretisations")
    public TGenericParameterType getGenericParameters() {
        return genericParameters;
    }

    public void setGenericParameters(TGenericParameterType genericParameters) {
        if (this.genericParameters != null) {
            if (this.genericParameters.equals(genericParameters)) return;
            this.genericParameters.getConcretisations().remove(this);
        }
        this.genericParameters = genericParameters;
        if (genericParameters == null) return;
        genericParameters.getConcretisations().add(this);
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

