// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.internal.MultivalueSet;
import java.util.*;
import org.moosetechnology.model.famixjava.famixtraits.TAnnotationInstance;
import org.moosetechnology.model.famixjava.famixtraits.TAnnotationInstanceAttribute;
import org.moosetechnology.model.famixjava.famixtraits.TAnnotationType;
import org.moosetechnology.model.famixjava.famixtraits.TTypedAnnotationInstance;
import org.moosetechnology.model.famixjava.famixtraits.TWithAnnotationInstanceAttributes;
import org.moosetechnology.model.famixjava.famixtraits.TWithAnnotationInstances;
import org.moosetechnology.model.famixjava.moosequery.TEntityMetaLevelDependency;


@FamePackage("Famix-Java-Entities")
@FameDescription("AnnotationInstance")
public class AnnotationInstance extends SourcedEntity implements TTypedAnnotationInstance, TEntityMetaLevelDependency, TAnnotationInstance, TWithAnnotationInstanceAttributes {

    private TWithAnnotationInstances annotatedEntity;
    
    private TAnnotationType annotationType;
    
    private Collection<TAnnotationInstanceAttribute> attributes; 



    @FameProperty(name = "annotatedEntity", opposite = "annotationInstances", container = true)
    public TWithAnnotationInstances getAnnotatedEntity() {
        return annotatedEntity;
    }

    public void setAnnotatedEntity(TWithAnnotationInstances annotatedEntity) {
        if (this.annotatedEntity != null) {
            if (this.annotatedEntity.equals(annotatedEntity)) return;
            this.annotatedEntity.getAnnotationInstances().remove(this);
        }
        this.annotatedEntity = annotatedEntity;
        if (annotatedEntity == null) return;
        annotatedEntity.getAnnotationInstances().add(this);
    }
    
    @FameProperty(name = "annotationType", opposite = "instances")
    public TAnnotationType getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(TAnnotationType annotationType) {
        if (this.annotationType != null) {
            if (this.annotationType.equals(annotationType)) return;
            this.annotationType.getInstances().remove(this);
        }
        this.annotationType = annotationType;
        if (annotationType == null) return;
        annotationType.getInstances().add(this);
    }
    
    @FameProperty(name = "attributes", opposite = "parentAnnotationInstance", derived = true)
    public Collection<TAnnotationInstanceAttribute> getAttributes() {
        if (attributes == null) {
            attributes = new MultivalueSet<TAnnotationInstanceAttribute>() {
                @Override
                protected void clearOpposite(TAnnotationInstanceAttribute e) {
                    e.setParentAnnotationInstance(null);
                }
                @Override
                protected void setOpposite(TAnnotationInstanceAttribute e) {
                    e.setParentAnnotationInstance(AnnotationInstance.this);
                }
            };
        }
        return attributes;
    }
    
    public void setAttributes(Collection<? extends TAnnotationInstanceAttribute> attributes) {
        this.getAttributes().clear();
        this.getAttributes().addAll(attributes);
    }                    
    
        
    public void addAttributes(TAnnotationInstanceAttribute one) {
        this.getAttributes().add(one);
    }   
    
    public void addAttributes(TAnnotationInstanceAttribute one, TAnnotationInstanceAttribute... many) {
        this.getAttributes().add(one);
        for (TAnnotationInstanceAttribute each : many)
            this.getAttributes().add(each);
    }   
    
    public void addAttributes(Iterable<? extends TAnnotationInstanceAttribute> many) {
        for (TAnnotationInstanceAttribute each : many)
            this.getAttributes().add(each);
    }   
                
    public void addAttributes(TAnnotationInstanceAttribute[] many) {
        for (TAnnotationInstanceAttribute each : many)
            this.getAttributes().add(each);
    }
    
    public int numberOfAttributes() {
        return getAttributes().size();
    }

    public boolean hasAttributes() {
        return !getAttributes().isEmpty();
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
    
    @FameProperty(name = "isDead", derived = true)
    public Boolean getIsDead() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOfChildren", derived = true)
    public Number getNumberOfChildren() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOfDeadChildren", derived = true)
    public Number getNumberOfDeadChildren() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    


}

