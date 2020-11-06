// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TPreprocessorIfdef;


@FamePackage("FAMIX")
@FameDescription("PreprocessorDefine")
public class PreprocessorDefine extends PreprocessorStatement implements TPreprocessorIfdef {

    private String macro;
    


    @FameProperty(name = "macro")
    public String getMacro() {
        return macro;
    }

    public void setMacro(String macro) {
        this.macro = macro;
    }
    


}

