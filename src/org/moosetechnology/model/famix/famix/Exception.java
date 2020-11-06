// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import org.moosetechnology.model.famix.famixtraits.TWithExceptions;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TException;


@FamePackage("FAMIX")
@FameDescription("Exception")
public class Exception extends Entity implements TException {

    private Method definingMethod;
    
    private TWithExceptions exceptionClass;
    


    @FameProperty(name = "definingMethod")
    public Method getDefiningMethod() {
        return definingMethod;
    }

    public void setDefiningMethod(Method definingMethod) {
        this.definingMethod = definingMethod;
    }
    
    @FameProperty(name = "exceptionClass", opposite = "exceptions")
    public TWithExceptions getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(TWithExceptions exceptionClass) {
        if (this.exceptionClass != null) {
            if (this.exceptionClass.equals(exceptionClass)) return;
            this.exceptionClass.getExceptions().remove(this);
        }
        this.exceptionClass = exceptionClass;
        if (exceptionClass == null) return;
        exceptionClass.getExceptions().add(this);
    }
    


}

