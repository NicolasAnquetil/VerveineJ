// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.famixreplication.Replica;
import org.moosetechnology.model.famixjava.famixtraits.*;
import org.moosetechnology.model.famixjava.moosequery.TAssociationMetaLevelDependency;


@FamePackage("Famix-Java-Entities")
@FameDescription("Inheritance")
public class Inheritance extends Entity implements TAssociation, TAssociationMetaLevelDependency, TInheritance, TSourceEntity {

    private Boolean isStub;
    
    private TAssociation next;
    
    private Number numberOfLinesOfCode;
    
    private TAssociation previous;
    
    private TSourceAnchor sourceAnchor;
    
    private TWithInheritances subclass;
    
    private TWithInheritances superclass;
    


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
    
    @FameProperty(name = "subclass", opposite = "superInheritances")
    public TWithInheritances getSubclass() {
        return subclass;
    }

    public void setSubclass(TWithInheritances subclass) {
        if (this.subclass != null) {
            if (this.subclass.equals(subclass)) return;
            this.subclass.getSuperInheritances().remove(this);
        }
        this.subclass = subclass;
        if (subclass == null) return;
        subclass.getSuperInheritances().add(this);
    }
    
    @FameProperty(name = "superclass", opposite = "subInheritances")
    public TWithInheritances getSuperclass() {
        return superclass;
    }

    public void setSuperclass(TWithInheritances superclass) {
        if (this.superclass != null) {
            if (this.superclass.equals(superclass)) return;
            this.superclass.getSubInheritances().remove(this);
        }
        this.superclass = superclass;
        if (superclass == null) return;
        superclass.getSubInheritances().add(this);
    }
    


}

