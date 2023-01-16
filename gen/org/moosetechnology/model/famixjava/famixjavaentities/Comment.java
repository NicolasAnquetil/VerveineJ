// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import org.moosetechnology.model.famixjava.famixtraits.TComment;
import org.moosetechnology.model.famixjava.famixtraits.TWithComments;


@FamePackage("Famix-Java-Entities")
@FameDescription("Comment")
public class Comment extends SourcedEntity implements TComment {

    private TWithComments container;
    
    private String content;
    


    @FameProperty(name = "container", opposite = "comments")
    public TWithComments getContainer() {
        return container;
    }

    public void setContainer(TWithComments container) {
        if (this.container != null) {
            if (this.container.equals(container)) return;
            this.container.getComments().remove(this);
        }
        this.container = container;
        if (container == null) return;
        container.getComments().add(this);
    }
    
    @FameProperty(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    


}

