// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import org.moosetechnology.model.famix.moosequery.TOODependencyQueries;
import ch.akuhn.fame.internal.MultivalueSet;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TWithTypes;
import org.moosetechnology.model.famix.moosequery.TEntityMetaLevelDependency;
import org.moosetechnology.model.famix.famixtraits.TFunction;
import org.moosetechnology.model.famix.famixtraits.TWithAnnotationTypes;
import org.moosetechnology.model.famix.famixtraits.TWithFunctions;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import org.moosetechnology.model.famix.famixtraits.TAnnotationType;
import org.moosetechnology.model.famix.famixtraits.TWithClasses;
import org.moosetechnology.model.famix.famixtraits.TType;


@FamePackage("FAMIX")
@FameDescription("ContainerEntity")
public class ContainerEntity extends NamedEntity implements TWithFunctions, TWithClasses, TEntityMetaLevelDependency, TOODependencyQueries, TWithTypes, TWithAnnotationTypes {

    private Collection<TType> types; 

    private Collection<TFunction> functions; 

    private Collection<TAnnotationType> definedAnnotationTypes; 



    @FameProperty(name = "numberOfDeadChildren", derived = true)
    public Number getNumberOfDeadChildren() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "fanIn", derived = true)
    public Number getFanIn() {
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

    @FameProperty(name = "fanOut", derived = true)
    public Number getFanOut() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOfClasses", derived = true)
    public Number getNumberOfClasses() {
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
    
    @FameProperty(name = "functions", opposite = "functionOwner", derived = true)
    public Collection<TFunction> getFunctions() {
        if (functions == null) {
            functions = new MultivalueSet<TFunction>() {
                @Override
                protected void clearOpposite(TFunction e) {
                    e.setFunctionOwner(null);
                }
                @Override
                protected void setOpposite(TFunction e) {
                    e.setFunctionOwner(ContainerEntity.this);
                }
            };
        }
        return functions;
    }
    
    public void setFunctions(Collection<? extends TFunction> functions) {
        this.getFunctions().clear();
        this.getFunctions().addAll(functions);
    }                    
    
        
    public void addFunctions(TFunction one) {
        this.getFunctions().add(one);
    }   
    
    public void addFunctions(TFunction one, TFunction... many) {
        this.getFunctions().add(one);
        for (TFunction each : many)
            this.getFunctions().add(each);
    }   
    
    public void addFunctions(Iterable<? extends TFunction> many) {
        for (TFunction each : many)
            this.getFunctions().add(each);
    }   
                
    public void addFunctions(TFunction[] many) {
        for (TFunction each : many)
            this.getFunctions().add(each);
    }
    
    public int numberOfFunctions() {
        return getFunctions().size();
    }

    public boolean hasFunctions() {
        return !getFunctions().isEmpty();
    }

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



}

