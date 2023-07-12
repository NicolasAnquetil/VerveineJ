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
@FameDescription("ParametricClass")
public class ParametricClass extends Class implements TParametricEntity {

    private Collection<TConcreteParameterType> concreteParameters; 

    private Collection<TConcretisation> concretisations; 

    private Collection<TConcretisation> genericEntities; 

    private Collection<TGenericParameterType> genericParameters; 



    @FameProperty(name = "concreteParameters", opposite = "concreteEntity")
    public Collection<TConcreteParameterType> getConcreteParameters() {
        if (concreteParameters == null) {
            concreteParameters = new MultivalueSet<TConcreteParameterType>() {
                @Override
                protected void clearOpposite(TConcreteParameterType e) {
                    e.getConcreteEntity().remove(ParametricClass.this);
                }
                @Override
                protected void setOpposite(TConcreteParameterType e) {
                    e.getConcreteEntity().add(ParametricClass.this);
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
                    e.setGenericEntity(ParametricClass.this);
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

    @FameProperty(name = "genericEntities", opposite = "concreteEntity", derived = true)
    public Collection<TConcretisation> getGenericEntities() {
        if (genericEntities == null) {
            genericEntities = new MultivalueSet<TConcretisation>() {
                @Override
                protected void clearOpposite(TConcretisation e) {
                    e.setConcreteEntity(null);
                }
                @Override
                protected void setOpposite(TConcretisation e) {
                    e.setConcreteEntity(ParametricClass.this);
                }
            };
        }
        return genericEntities;
    }
    
    public void setGenericEntities(Collection<? extends TConcretisation> genericEntities) {
        this.getGenericEntities().clear();
        this.getGenericEntities().addAll(genericEntities);
    }                    
    
        
    public void addGenericEntities(TConcretisation one) {
        this.getGenericEntities().add(one);
    }   
    
    public void addGenericEntities(TConcretisation one, TConcretisation... many) {
        this.getGenericEntities().add(one);
        for (TConcretisation each : many)
            this.getGenericEntities().add(each);
    }   
    
    public void addGenericEntities(Iterable<? extends TConcretisation> many) {
        for (TConcretisation each : many)
            this.getGenericEntities().add(each);
    }   
                
    public void addGenericEntities(TConcretisation[] many) {
        for (TConcretisation each : many)
            this.getGenericEntities().add(each);
    }
    
    public int numberOfGenericEntities() {
        return getGenericEntities().size();
    }

    public boolean hasGenericEntities() {
        return !getGenericEntities().isEmpty();
    }

    @FameProperty(name = "genericParameters", opposite = "genericEntity")
    public Collection<TGenericParameterType> getGenericParameters() {
        if (genericParameters == null) {
            genericParameters = new MultivalueSet<TGenericParameterType>() {
                @Override
                protected void clearOpposite(TGenericParameterType e) {
                    e.getGenericEntity().remove(ParametricClass.this);
                }
                @Override
                protected void setOpposite(TGenericParameterType e) {
                    e.getGenericEntity().add(ParametricClass.this);
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

