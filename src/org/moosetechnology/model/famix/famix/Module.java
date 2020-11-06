// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import org.moosetechnology.model.famix.famixtraits.TWithCompilationUnit;
import org.moosetechnology.model.famix.famixtraits.TWithHeader;
import ch.akuhn.fame.internal.MultivalueSet;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famix.famixtraits.THeader;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TDefinedInModule;
import org.moosetechnology.model.famix.famixtraits.TCompilationUnit;
import org.moosetechnology.model.famix.famixtraits.TModule;


@FamePackage("FAMIX")
@FameDescription("Module")
public class Module extends ScopingEntity implements TWithCompilationUnit, TModule, TWithHeader {

    private Collection<TDefinedInModule> moduleEntities; 

    private THeader header;
    
    private TCompilationUnit compilationUnit;
    


    @FameProperty(name = "moduleEntities", opposite = "parentModule", derived = true)
    public Collection<TDefinedInModule> getModuleEntities() {
        if (moduleEntities == null) {
            moduleEntities = new MultivalueSet<TDefinedInModule>() {
                @Override
                protected void clearOpposite(TDefinedInModule e) {
                    e.setParentModule(null);
                }
                @Override
                protected void setOpposite(TDefinedInModule e) {
                    e.setParentModule(Module.this);
                }
            };
        }
        return moduleEntities;
    }
    
    public void setModuleEntities(Collection<? extends TDefinedInModule> moduleEntities) {
        this.getModuleEntities().clear();
        this.getModuleEntities().addAll(moduleEntities);
    }                    
    
        
    public void addModuleEntities(TDefinedInModule one) {
        this.getModuleEntities().add(one);
    }   
    
    public void addModuleEntities(TDefinedInModule one, TDefinedInModule... many) {
        this.getModuleEntities().add(one);
        for (TDefinedInModule each : many)
            this.getModuleEntities().add(each);
    }   
    
    public void addModuleEntities(Iterable<? extends TDefinedInModule> many) {
        for (TDefinedInModule each : many)
            this.getModuleEntities().add(each);
    }   
                
    public void addModuleEntities(TDefinedInModule[] many) {
        for (TDefinedInModule each : many)
            this.getModuleEntities().add(each);
    }
    
    public int numberOfModuleEntities() {
        return getModuleEntities().size();
    }

    public boolean hasModuleEntities() {
        return !getModuleEntities().isEmpty();
    }

    @FameProperty(name = "header", opposite = "headerOwner", derived = true)
    public THeader getHeader() {
        return header;
    }

    public void setHeader(THeader header) {
        if (this.header == null ? header != null : !this.header.equals(header)) {
            THeader old_header = this.header;
            this.header = header;
            if (old_header != null) old_header.setHeaderOwner(null);
            if (header != null) header.setHeaderOwner(this);
        }
    }
    
    @FameProperty(name = "compilationUnit", opposite = "compilationUnitOwner", derived = true)
    public TCompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public void setCompilationUnit(TCompilationUnit compilationUnit) {
        if (this.compilationUnit == null ? compilationUnit != null : !this.compilationUnit.equals(compilationUnit)) {
            TCompilationUnit old_compilationUnit = this.compilationUnit;
            this.compilationUnit = compilationUnit;
            if (old_compilationUnit != null) old_compilationUnit.setCompilationUnitOwner(null);
            if (compilationUnit != null) compilationUnit.setCompilationUnitOwner(this);
        }
    }
    


}

