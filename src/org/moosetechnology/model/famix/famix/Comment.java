// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TWithComments;
import org.moosetechnology.model.famix.famixtraits.TComment;


@FamePackage("FAMIX")
@FameDescription("Comment")
public class Comment extends SourcedEntity implements TComment {

    private String content;
    
    private TWithComments container;
    


    @FameProperty(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
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
    


}

