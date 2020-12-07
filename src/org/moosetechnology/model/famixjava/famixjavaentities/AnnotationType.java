// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.internal.MultivalueSet;
import org.moosetechnology.model.famixjava.famixtraits.*;

import java.util.Collection;


@FamePackage("FamixJavaEntities")
@FameDescription("AnnotationType")
public class AnnotationType extends Type implements TWithAttributes, TAnnotationType {

    private Collection<TAttribute> attributes;

    private Collection<TTypedAnnotationInstance> instances;

    private TWithAnnotationTypes annotationTypesContainer;


    @FameProperty(name = "attributes", opposite = "parentType", derived = true)
    public Collection<TAttribute> getAttributes() {
        if (attributes == null) {
            attributes = new MultivalueSet<TAttribute>() {
                @Override
                protected void clearOpposite(TAttribute e) {
                    e.setParentType(null);
                }

                @Override
                protected void setOpposite(TAttribute e) {
                    e.setParentType(AnnotationType.this);
                }
            };
        }
        return attributes;
    }

    public void setAttributes(Collection<? extends TAttribute> attributes) {
        this.getAttributes().clear();
        this.getAttributes().addAll(attributes);
    }


    public void addAttributes(TAttribute one) {
        this.getAttributes().add(one);
    }

    public void addAttributes(TAttribute one, TAttribute... many) {
        this.getAttributes().add(one);
        for (TAttribute each : many)
            this.getAttributes().add(each);
    }

    public void addAttributes(Iterable<? extends TAttribute> many) {
        for (TAttribute each : many)
            this.getAttributes().add(each);
    }

    public void addAttributes(TAttribute[] many) {
        for (TAttribute each : many)
            this.getAttributes().add(each);
    }

    public int numberOfAttributes() {
        return getAttributes().size();
    }

    public boolean hasAttributes() {
        return !getAttributes().isEmpty();
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

    @FameProperty(name = "numberOfProtectedAttributes", derived = true)
    public Number getNumberOfProtectedAttributes() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfRevealedAttributes", derived = true)
    public Number getNumberOfRevealedAttributes() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfPrivateAttributes", derived = true)
    public Number getNumberOfPrivateAttributes() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
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

    @FameProperty(name = "numberOfAttributes", derived = true)
    public Number getNumberOfAttributes() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfPublicAttributes", derived = true)
    public Number getNumberOfPublicAttributes() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }


}

