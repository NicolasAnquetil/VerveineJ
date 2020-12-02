// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TFileNavigation")
public interface TFileNavigation {

    @FameProperty(name = "endLine")
    Number getEndLine();

    void setEndLine(Number endLine);

    @FameProperty(name = "endColumn")
    Number getEndColumn();

    void setEndColumn(Number endColumn);

    @FameProperty(name = "startColumn")
    Number getStartColumn();

    void setStartColumn(Number startColumn);

    @FameProperty(name = "startLine")
    Number getStartLine();

    void setStartLine(Number startLine);


}

