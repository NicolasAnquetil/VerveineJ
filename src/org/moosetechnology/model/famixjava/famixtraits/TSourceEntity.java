// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.famixreplication.Replica;


@FamePackage("FamixTraits")
@FameDescription("TSourceEntity")
public interface TSourceEntity {

    @FameProperty(name = "isStub")
    Boolean getIsStub();

    void setIsStub(Boolean isStub);

    @FameProperty(name = "duplicationRate", derived = true)
    Number getDuplicationRate();

    @FameProperty(name = "sourceAnchor", opposite = "element", derived = true)
    TSourceAnchor getSourceAnchor();

    void setSourceAnchor(TSourceAnchor sourceAnchor);

    @FameProperty(name = "containsReplicas", derived = true)
    Boolean getContainsReplicas();

    @FameProperty(name = "numberOfLinesOfCodeWithMoreThanOneCharacter", derived = true)
    Number getNumberOfLinesOfCodeWithMoreThanOneCharacter();

    @FameProperty(name = "replicas", derived = true)
    Replica getReplicas();

    @FameProperty(name = "numberOfLinesOfCode")
    Number getNumberOfLinesOfCode();

    void setNumberOfLinesOfCode(Number numberOfLinesOfCode);

    @FameProperty(name = "sourceText", derived = true)
    String getSourceText();


}

