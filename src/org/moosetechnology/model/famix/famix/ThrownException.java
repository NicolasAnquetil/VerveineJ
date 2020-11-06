// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import org.moosetechnology.model.famix.famixtraits.TWithThrownExceptions;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TThrownException;


@FamePackage("FAMIX")
@FameDescription("ThrownException")
public class ThrownException extends Exception implements TThrownException {

    private TWithThrownExceptions definingEntity;
    


    @FameProperty(name = "definingEntity", opposite = "thrownExceptions")
    public TWithThrownExceptions getDefiningEntity() {
        return definingEntity;
    }

    public void setDefiningEntity(TWithThrownExceptions definingEntity) {
        if (this.definingEntity != null) {
            if (this.definingEntity.equals(definingEntity)) return;
            this.definingEntity.getThrownExceptions().remove(this);
        }
        this.definingEntity = definingEntity;
        if (definingEntity == null) return;
        definingEntity.getThrownExceptions().add(this);
    }
    


}

