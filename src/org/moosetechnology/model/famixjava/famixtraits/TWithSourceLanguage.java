// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;


@FamePackage("FamixTraits")
@FameDescription("TWithSourceLanguage")
public interface TWithSourceLanguage {

    @FameProperty(name = "declaredSourceLanguage", opposite = "sourcedEntities")
    TSourceLanguage getDeclaredSourceLanguage();

    void setDeclaredSourceLanguage(TSourceLanguage declaredSourceLanguage);


}

