// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import org.moosetechnology.model.famix.famixtraits.TWithParameterizedTypeUsers;
import ch.akuhn.fame.internal.MultivalueSet;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TParameterizedType;
import org.moosetechnology.model.famix.famixtraits.TWithParameterizedTypes;
import org.moosetechnology.model.famix.famixtraits.TParameterizedTypeUser;


@FamePackage("FAMIX")
@FameDescription("ParameterizedType")
public class ParameterizedType extends Type implements TWithParameterizedTypeUsers, TParameterizedType {

    private Collection<TParameterizedTypeUser> arguments; 

    private TWithParameterizedTypes parameterizableClass;
    


    @FameProperty(name = "arguments", opposite = "argumentsInParameterizedTypes")
    public Collection<TParameterizedTypeUser> getArguments() {
        if (arguments == null) {
            arguments = new MultivalueSet<TParameterizedTypeUser>() {
                @Override
                protected void clearOpposite(TParameterizedTypeUser e) {
                    e.getArgumentsInParameterizedTypes().remove(ParameterizedType.this);
                }
                @Override
                protected void setOpposite(TParameterizedTypeUser e) {
                    e.getArgumentsInParameterizedTypes().add(ParameterizedType.this);
                }
            };
        }
        return arguments;
    }
    
    public void setArguments(Collection<? extends TParameterizedTypeUser> arguments) {
        this.getArguments().clear();
        this.getArguments().addAll(arguments);
    }
    
    public void addArguments(TParameterizedTypeUser one) {
        this.getArguments().add(one);
    }   
    
    public void addArguments(TParameterizedTypeUser one, TParameterizedTypeUser... many) {
        this.getArguments().add(one);
        for (TParameterizedTypeUser each : many)
            this.getArguments().add(each);
    }   
    
    public void addArguments(Iterable<? extends TParameterizedTypeUser> many) {
        for (TParameterizedTypeUser each : many)
            this.getArguments().add(each);
    }   
                
    public void addArguments(TParameterizedTypeUser[] many) {
        for (TParameterizedTypeUser each : many)
            this.getArguments().add(each);
    }
    
    public int numberOfArguments() {
        return getArguments().size();
    }

    public boolean hasArguments() {
        return !getArguments().isEmpty();
    }

    @FameProperty(name = "parameterizableClass", opposite = "parameterizedTypes")
    public TWithParameterizedTypes getParameterizableClass() {
        return parameterizableClass;
    }

    public void setParameterizableClass(TWithParameterizedTypes parameterizableClass) {
        if (this.parameterizableClass != null) {
            if (this.parameterizableClass.equals(parameterizableClass)) return;
            this.parameterizableClass.getParameterizedTypes().remove(this);
        }
        this.parameterizableClass = parameterizableClass;
        if (parameterizableClass == null) return;
        parameterizableClass.getParameterizedTypes().add(this);
    }
    


}

