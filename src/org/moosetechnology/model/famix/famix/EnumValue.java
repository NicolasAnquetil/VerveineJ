// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import org.moosetechnology.model.famix.famixtraits.TEnumValue;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TWithEnumValues;


@FamePackage("FAMIX")
@FameDescription("EnumValue")
public class EnumValue extends StructuralEntity implements TEnumValue {

    private TWithEnumValues parentEnum;
    
    public ContainerEntity getBelongsTo() {
        return (ContainerEntity) this.getParentEnum();
    }

    @FameProperty(name = "parentEnum", opposite = "enumValues", container = true)
    public TWithEnumValues getParentEnum() {
        return parentEnum;
    }

    public void setParentEnum(TWithEnumValues parentEnum) {
        if (this.parentEnum != null) {
            if (this.parentEnum.equals(parentEnum)) return;
            this.parentEnum.getEnumValues().remove(this);
        }
        this.parentEnum = parentEnum;
        if (parentEnum == null) return;
        parentEnum.getEnumValues().add(this);
    }
    


}

