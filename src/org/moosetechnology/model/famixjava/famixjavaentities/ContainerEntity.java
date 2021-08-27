// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.internal.MultivalueSet;
import org.moosetechnology.model.famixjava.famixtraits.*;
import org.moosetechnology.model.famixjava.moosequery.TEntityMetaLevelDependency;
import org.moosetechnology.model.famixjava.moosequery.TOODependencyQueries;

import java.util.Collection;


@FamePackage("Famix-Java-Entities")
@FameDescription("ContainerEntity")
public class ContainerEntity extends NamedEntity implements TEntityMetaLevelDependency, TOODependencyQueries, TWithAnnotationTypes, TWithClasses, TWithTypes {

    private Collection<TAnnotationType> definedAnnotationTypes; 

    private Collection<TType> types; 



    @FameProperty(name = "definedAnnotationTypes", opposite = "annotationTypesContainer", derived = true)
    public Collection<TAnnotationType> getDefinedAnnotationTypes() {
        if (definedAnnotationTypes == null) {
            definedAnnotationTypes = new MultivalueSet<TAnnotationType>() {
                @Override
                protected void clearOpposite(TAnnotationType e) {
                    e.setAnnotationTypesContainer(null);
                }
                @Override
                protected void setOpposite(TAnnotationType e) {
                    e.setAnnotationTypesContainer(ContainerEntity.this);
                }
            };
        }
        return definedAnnotationTypes;
    }
    
    public void setDefinedAnnotationTypes(Collection<? extends TAnnotationType> definedAnnotationTypes) {
        this.getDefinedAnnotationTypes().clear();
        this.getDefinedAnnotationTypes().addAll(definedAnnotationTypes);
    }                    
    
        
    public void addDefinedAnnotationTypes(TAnnotationType one) {
        this.getDefinedAnnotationTypes().add(one);
    }   
    
    public void addDefinedAnnotationTypes(TAnnotationType one, TAnnotationType... many) {
        this.getDefinedAnnotationTypes().add(one);
        for (TAnnotationType each : many)
            this.getDefinedAnnotationTypes().add(each);
    }   
    
    public void addDefinedAnnotationTypes(Iterable<? extends TAnnotationType> many) {
        for (TAnnotationType each : many)
            this.getDefinedAnnotationTypes().add(each);
    }   
                
    public void addDefinedAnnotationTypes(TAnnotationType[] many) {
        for (TAnnotationType each : many)
            this.getDefinedAnnotationTypes().add(each);
    }
    
    public int numberOfDefinedAnnotationTypes() {
        return getDefinedAnnotationTypes().size();
    }

    public boolean hasDefinedAnnotationTypes() {
        return !getDefinedAnnotationTypes().isEmpty();
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
    
    @FameProperty(name = "numberOfClasses", derived = true)
    public Number getNumberOfClasses() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOfDeadChildren", derived = true)
    public Number getNumberOfDeadChildren() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "types", opposite = "typeContainer", derived = true)
    public Collection<TType> getTypes() {
        if (types == null) {
            types = new MultivalueSet<TType>() {
                @Override
                protected void clearOpposite(TType e) {
                    e.setTypeContainer(null);
                }
                @Override
                protected void setOpposite(TType e) {
                    e.setTypeContainer(ContainerEntity.this);
                }
            };
        }
        return types;
    }
    
    public void setTypes(Collection<? extends TType> types) {
        this.getTypes().clear();
        this.getTypes().addAll(types);
    }                    
    
        
    public void addTypes(TType one) {
        this.getTypes().add(one);
    }   
    
    public void addTypes(TType one, TType... many) {
        this.getTypes().add(one);
        for (TType each : many)
            this.getTypes().add(each);
    }   
    
    public void addTypes(Iterable<? extends TType> many) {
        for (TType each : many)
            this.getTypes().add(each);
    }   
                
    public void addTypes(TType[] many) {
        for (TType each : many)
            this.getTypes().add(each);
    }
    
    public int numberOfTypes() {
        return getTypes().size();
    }

    public boolean hasTypes() {
        return !getTypes().isEmpty();
    }



}

