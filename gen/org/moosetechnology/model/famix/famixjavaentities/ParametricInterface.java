// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.internal.MultivalueSet;
import java.util.*;
import org.moosetechnology.model.famix.famixtraits.TConcreteParameterType;
import org.moosetechnology.model.famix.famixtraits.TConcretisation;
import org.moosetechnology.model.famix.famixtraits.TGenericParameterType;
import org.moosetechnology.model.famix.famixtraits.TParametricEntity;


@FamePackage("Famix-Java-Entities")
@FameDescription("ParametricInterface")
public class ParametricInterface extends Interface implements TParametricEntity {

    private Collection<TConcreteParameterType> concreteParameters; 

    private Collection<TConcretisation> concretisations; 

    private Collection<TConcretisation> genericEntity; 

    private Collection<TGenericParameterType> genericParameters; 



    @FameProperty(name = "concreteParameters", opposite = "concreteEntity")
    public Collection<TConcreteParameterType> getConcreteParameters() {
        if (concreteParameters == null) {
            concreteParameters = new MultivalueSet<TConcreteParameterType>() {
                @Override
                protected void clearOpposite(TConcreteParameterType e) {
                    e.getConcreteEntity().remove(ParametricInterface.this);
                }
                @Override
                protected void setOpposite(TConcreteParameterType e) {
                    e.getConcreteEntity().add(ParametricInterface.this);
                }
            };
        }
        return concreteParameters;
    }
    
    public void setConcreteParameters(Collection<? extends TConcreteParameterType> concreteParameters) {
        this.getConcreteParameters().clear();
        this.getConcreteParameters().addAll(concreteParameters);
    }
    
    public void addConcreteParameters(TConcreteParameterType one) {
        this.getConcreteParameters().add(one);
    }   
    
    public void addConcreteParameters(TConcreteParameterType one, TConcreteParameterType... many) {
        this.getConcreteParameters().add(one);
        for (TConcreteParameterType each : many)
            this.getConcreteParameters().add(each);
    }   
    
    public void addConcreteParameters(Iterable<? extends TConcreteParameterType> many) {
        for (TConcreteParameterType each : many)
            this.getConcreteParameters().add(each);
    }   
                
    public void addConcreteParameters(TConcreteParameterType[] many) {
        for (TConcreteParameterType each : many)
            this.getConcreteParameters().add(each);
    }
    
    public int numberOfConcreteParameters() {
        return getConcreteParameters().size();
    }

    public boolean hasConcreteParameters() {
        return !getConcreteParameters().isEmpty();
    }

    @FameProperty(name = "concretisations", opposite = "genericEntity", derived = true)
    public Collection<TConcretisation> getConcretisations() {
        if (concretisations == null) {
            concretisations = new MultivalueSet<TConcretisation>() {
                @Override
                protected void clearOpposite(TConcretisation e) {
                    e.setGenericEntity(null);
                }
                @Override
                protected void setOpposite(TConcretisation e) {
                    e.setGenericEntity(ParametricInterface.this);
                }
            };
        }
        return concretisations;
    }
    
    public void setConcretisations(Collection<? extends TConcretisation> concretisations) {
        this.getConcretisations().clear();
        this.getConcretisations().addAll(concretisations);
    }                    
    
        
    public void addConcretisations(TConcretisation one) {
        this.getConcretisations().add(one);
    }   
    
    public void addConcretisations(TConcretisation one, TConcretisation... many) {
        this.getConcretisations().add(one);
        for (TConcretisation each : many)
            this.getConcretisations().add(each);
    }   
    
    public void addConcretisations(Iterable<? extends TConcretisation> many) {
        for (TConcretisation each : many)
            this.getConcretisations().add(each);
    }   
                
    public void addConcretisations(TConcretisation[] many) {
        for (TConcretisation each : many)
            this.getConcretisations().add(each);
    }
    
    public int numberOfConcretisations() {
        return getConcretisations().size();
    }

    public boolean hasConcretisations() {
        return !getConcretisations().isEmpty();
    }

    @FameProperty(name = "genericEntity", opposite = "concreteEntity", derived = true)
    public Collection<TConcretisation> getGenericEntity() {
        if (genericEntity == null) {
            genericEntity = new MultivalueSet<TConcretisation>() {
                @Override
                protected void clearOpposite(TConcretisation e) {
                    e.setConcreteEntity(null);
                }
                @Override
                protected void setOpposite(TConcretisation e) {
                    e.setConcreteEntity(ParametricInterface.this);
                }
            };
        }
        return genericEntity;
    }
    
    public void setGenericEntity(Collection<? extends TConcretisation> genericEntity) {
        this.getGenericEntity().clear();
        this.getGenericEntity().addAll(genericEntity);
    }                    
    
        
    public void addGenericEntity(TConcretisation one) {
        this.getGenericEntity().add(one);
    }   
    
    public void addGenericEntity(TConcretisation one, TConcretisation... many) {
        this.getGenericEntity().add(one);
        for (TConcretisation each : many)
            this.getGenericEntity().add(each);
    }   
    
    public void addGenericEntity(Iterable<? extends TConcretisation> many) {
        for (TConcretisation each : many)
            this.getGenericEntity().add(each);
    }   
                
    public void addGenericEntity(TConcretisation[] many) {
        for (TConcretisation each : many)
            this.getGenericEntity().add(each);
    }
    
    public int numberOfGenericEntity() {
        return getGenericEntity().size();
    }

    public boolean hasGenericEntity() {
        return !getGenericEntity().isEmpty();
    }

    @FameProperty(name = "genericParameters", opposite = "genericEntity")
    public Collection<TGenericParameterType> getGenericParameters() {
        if (genericParameters == null) {
            genericParameters = new MultivalueSet<TGenericParameterType>() {
                @Override
                protected void clearOpposite(TGenericParameterType e) {
                    e.getGenericEntity().remove(ParametricInterface.this);
                }
                @Override
                protected void setOpposite(TGenericParameterType e) {
                    e.getGenericEntity().add(ParametricInterface.this);
                }
            };
        }
        return genericParameters;
    }
    
    public void setGenericParameters(Collection<? extends TGenericParameterType> genericParameters) {
        this.getGenericParameters().clear();
        this.getGenericParameters().addAll(genericParameters);
    }
    
    public void addGenericParameters(TGenericParameterType one) {
        this.getGenericParameters().add(one);
    }   
    
    public void addGenericParameters(TGenericParameterType one, TGenericParameterType... many) {
        this.getGenericParameters().add(one);
        for (TGenericParameterType each : many)
            this.getGenericParameters().add(each);
    }   
    
    public void addGenericParameters(Iterable<? extends TGenericParameterType> many) {
        for (TGenericParameterType each : many)
            this.getGenericParameters().add(each);
    }   
                
    public void addGenericParameters(TGenericParameterType[] many) {
        for (TGenericParameterType each : many)
            this.getGenericParameters().add(each);
    }
    
    public int numberOfGenericParameters() {
        return getGenericParameters().size();
    }

    public boolean hasGenericParameters() {
        return !getGenericParameters().isEmpty();
    }



}

