// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import org.moosetechnology.model.famix.famixtraits.TWithAnnotationTypes;
import ch.akuhn.fame.internal.MultivalueSet;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famix.famixtraits.TTypedAnnotationInstance;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TAnnotationType;
import org.moosetechnology.model.famix.famix.ContainerEntity;


@FamePackage("FAMIX")
@FameDescription("AnnotationType")
public class AnnotationType extends Type implements TAnnotationType {

    private Collection<TTypedAnnotationInstance> instances; 

    private TWithAnnotationTypes annotationTypesContainer;

    public AnnotationType() {
    }

    private ContainerEntity container;
    @FameProperty(
            name = "container",
            opposite = "definedAnnotationTypes",
            container = true
    )
    public ContainerEntity getContainer() {
        return this.container;
    }

    public void setContainer(ContainerEntity var1) {
        if (this.container != null) {
            if (this.container.equals(var1)) {
                return;
            }
            this.container.getDefinedAnnotationTypes().remove(this);
        }
        this.container = var1;
        if (var1 != null) {
            var1.getDefinedAnnotationTypes().add(this);
        }
    }


    @FameProperty(name = "instances", opposite = "annotationType", derived = true)
    public Collection<TTypedAnnotationInstance> getInstances() {
        if (instances == null) {
            instances = new MultivalueSet<TTypedAnnotationInstance>() {
                @Override
                protected void clearOpposite(TTypedAnnotationInstance e) {
                    e.setAnnotationType(null);
                }
                @Override
                protected void setOpposite(TTypedAnnotationInstance e) {
                    e.setAnnotationType(AnnotationType.this);
                }
            };
        }
        return instances;
    }
    
    public void setInstances(Collection<? extends TTypedAnnotationInstance> instances) {
        this.getInstances().clear();
        this.getInstances().addAll(instances);
    }                    
    
        
    public void addInstances(TTypedAnnotationInstance one) {
        this.getInstances().add(one);
    }   
    
    public void addInstances(TTypedAnnotationInstance one, TTypedAnnotationInstance... many) {
        this.getInstances().add(one);
        for (TTypedAnnotationInstance each : many)
            this.getInstances().add(each);
    }   
    
    public void addInstances(Iterable<? extends TTypedAnnotationInstance> many) {
        for (TTypedAnnotationInstance each : many)
            this.getInstances().add(each);
    }   
                
    public void addInstances(TTypedAnnotationInstance[] many) {
        for (TTypedAnnotationInstance each : many)
            this.getInstances().add(each);
    }
    
    public int numberOfInstances() {
        return getInstances().size();
    }

    public boolean hasInstances() {
        return !getInstances().isEmpty();
    }

    @FameProperty(name = "annotationTypesContainer", opposite = "definedAnnotationTypes", container = true)
    public TWithAnnotationTypes getAnnotationTypesContainer() {
        return annotationTypesContainer;
    }

    public void setAnnotationTypesContainer(TWithAnnotationTypes annotationTypesContainer) {
        if (this.annotationTypesContainer != null) {
            if (this.annotationTypesContainer.equals(annotationTypesContainer)) return;
            this.annotationTypesContainer.getDefinedAnnotationTypes().remove(this);
        }
        this.annotationTypesContainer = annotationTypesContainer;
        if (annotationTypesContainer == null) return;
        annotationTypesContainer.getDefinedAnnotationTypes().add(this);
    }
    


}

