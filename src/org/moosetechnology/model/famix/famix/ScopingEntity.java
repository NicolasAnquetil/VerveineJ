// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import ch.akuhn.fame.internal.MultivalueSet;
import org.moosetechnology.model.famix.famixtraits.TWithGlobalVariables;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TGlobalVariable;


@FamePackage("FAMIX")
@FameDescription("ScopingEntity")
public class ScopingEntity extends ContainerEntity implements TWithGlobalVariables {

    private ScopingEntity parentScope;
    
    private Collection<ScopingEntity> childScopes; 

    private Collection<TGlobalVariable> globalVariables; 



    @FameProperty(name = "parentScope", opposite = "childScopes", container = true)
    public ScopingEntity getParentScope() {
        return parentScope;
    }

    public void setParentScope(ScopingEntity parentScope) {
        if (this.parentScope != null) {
            if (this.parentScope.equals(parentScope)) return;
            this.parentScope.getChildScopes().remove(this);
        }
        this.parentScope = parentScope;
        if (parentScope == null) return;
        parentScope.getChildScopes().add(this);
    }
    
    @FameProperty(name = "childScopes", opposite = "parentScope", derived = true)
    public Collection<ScopingEntity> getChildScopes() {
        if (childScopes == null) {
            childScopes = new MultivalueSet<ScopingEntity>() {
                @Override
                protected void clearOpposite(ScopingEntity e) {
                    e.setParentScope(null);
                }
                @Override
                protected void setOpposite(ScopingEntity e) {
                    e.setParentScope(ScopingEntity.this);
                }
            };
        }
        return childScopes;
    }
    
    public void setChildScopes(Collection<? extends ScopingEntity> childScopes) {
        this.getChildScopes().clear();
        this.getChildScopes().addAll(childScopes);
    }                    
    
        
    public void addChildScopes(ScopingEntity one) {
        this.getChildScopes().add(one);
    }   
    
    public void addChildScopes(ScopingEntity one, ScopingEntity... many) {
        this.getChildScopes().add(one);
        for (ScopingEntity each : many)
            this.getChildScopes().add(each);
    }   
    
    public void addChildScopes(Iterable<? extends ScopingEntity> many) {
        for (ScopingEntity each : many)
            this.getChildScopes().add(each);
    }   
                
    public void addChildScopes(ScopingEntity[] many) {
        for (ScopingEntity each : many)
            this.getChildScopes().add(each);
    }
    
    public int numberOfChildScopes() {
        return getChildScopes().size();
    }

    public boolean hasChildScopes() {
        return !getChildScopes().isEmpty();
    }

    @FameProperty(name = "globalVariables", opposite = "parentScope", derived = true)
    public Collection<TGlobalVariable> getGlobalVariables() {
        if (globalVariables == null) {
            globalVariables = new MultivalueSet<TGlobalVariable>() {
                @Override
                protected void clearOpposite(TGlobalVariable e) {
                    e.setParentScope(null);
                }
                @Override
                protected void setOpposite(TGlobalVariable e) {
                    e.setParentScope(ScopingEntity.this);
                }
            };
        }
        return globalVariables;
    }
    
    public void setGlobalVariables(Collection<? extends TGlobalVariable> globalVariables) {
        this.getGlobalVariables().clear();
        this.getGlobalVariables().addAll(globalVariables);
    }                    
    
        
    public void addGlobalVariables(TGlobalVariable one) {
        this.getGlobalVariables().add(one);
    }   
    
    public void addGlobalVariables(TGlobalVariable one, TGlobalVariable... many) {
        this.getGlobalVariables().add(one);
        for (TGlobalVariable each : many)
            this.getGlobalVariables().add(each);
    }   
    
    public void addGlobalVariables(Iterable<? extends TGlobalVariable> many) {
        for (TGlobalVariable each : many)
            this.getGlobalVariables().add(each);
    }   
                
    public void addGlobalVariables(TGlobalVariable[] many) {
        for (TGlobalVariable each : many)
            this.getGlobalVariables().add(each);
    }
    
    public int numberOfGlobalVariables() {
        return getGlobalVariables().size();
    }

    public boolean hasGlobalVariables() {
        return !getGlobalVariables().isEmpty();
    }

    public ContainerEntity getBelongsTo() {
        return this.getParentScope();
    }

    public void setBelongsTo(ContainerEntity var1) {
        if (var1 instanceof ScopingEntity) {
            this.setParentScope((ScopingEntity)var1);
        }

    }


}

