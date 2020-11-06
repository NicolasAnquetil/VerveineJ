// Automagically generated code, please do not change
package org.moosetechnology.model.famix.tagging;

import org.moosetechnology.model.famix.moose.Entity;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("Tagging")
@FameDescription("Association")
public class Association extends Entity  {

    private Number tagId;
    
    private Entity entity;
    


    @FameProperty(name = "tagId")
    public Number getTagId() {
        return tagId;
    }

    public void setTagId(Number tagId) {
        this.tagId = tagId;
    }
    
    @FameProperty(name = "tag", derived = true)
    public Tag getTag() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "entity", opposite = "allTagAssociations")
    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        if (this.entity != null) {
            if (this.entity.equals(entity)) return;
            this.entity.getAllTagAssociations().remove(this);
        }
        this.entity = entity;
        if (entity == null) return;
        entity.getAllTagAssociations().add(this);
    }
    


}

