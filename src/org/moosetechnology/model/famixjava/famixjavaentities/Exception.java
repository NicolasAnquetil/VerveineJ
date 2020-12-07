// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.famixtraits.TException;
import org.moosetechnology.model.famixjava.famixtraits.TWithExceptions;


@FamePackage("FamixJavaEntities")
@FameDescription("Exception")
public class Exception extends Entity implements TException {

    private TWithExceptions exceptionClass;


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

