// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixjavaentities;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.internal.MultivalueSet;
import org.moosetechnology.model.famixjava.famixreplication.Replica;
import org.moosetechnology.model.famixjava.famixtraits.*;
import org.moosetechnology.model.famixjava.moosequery.TAssociationMetaLevelDependency;

import java.util.Collection;


@FamePackage("FamixJavaEntities")
@FameDescription("Invocation")
public class Invocation extends Entity implements TAssociation, TSourceEntity, TInvocation, TWithSourceLanguage, THasSignature, TAssociationMetaLevelDependency, TWithComments {

    private TAssociation previous;

    private TInvocationsReceiver receiver;

    private TWithInvocations sender;

    private Number numberOfLinesOfCode;

    private Collection<TComment> comments;

    private Boolean isStub;

    private Collection<TInvocable> candidates;

    private TSourceLanguage declaredSourceLanguage;

    private String signature;

    private TSourceAnchor sourceAnchor;

    private TAssociation next;


    @FameProperty(name = "previous", opposite = "next")
    public TAssociation getPrevious() {
        return previous;
    }

    public void setPrevious(TAssociation previous) {
        if (this.previous == null ? previous != null : !this.previous.equals(previous)) {
            TAssociation old_previous = this.previous;
            this.previous = previous;
            if (old_previous != null) old_previous.setNext(null);
            if (previous != null) previous.setNext(this);
        }
    }

    @FameProperty(name = "receiver", opposite = "receivingInvocations")
    public TInvocationsReceiver getReceiver() {
        return receiver;
    }

    public void setReceiver(TInvocationsReceiver receiver) {
        if (this.receiver != null) {
            if (this.receiver.equals(receiver)) return;
            this.receiver.getReceivingInvocations().remove(this);
        }
        this.receiver = receiver;
        if (receiver == null) return;
        receiver.getReceivingInvocations().add(this);
    }

    @FameProperty(name = "containsReplicas", derived = true)
    public Boolean getContainsReplicas() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfLinesOfCodeWithMoreThanOneCharacter", derived = true)
    public Number getNumberOfLinesOfCodeWithMoreThanOneCharacter() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "sender", opposite = "outgoingInvocations")
    public TWithInvocations getSender() {
        return sender;
    }

    public void setSender(TWithInvocations sender) {
        if (this.sender != null) {
            if (this.sender.equals(sender)) return;
            this.sender.getOutgoingInvocations().remove(this);
        }
        this.sender = sender;
        if (sender == null) return;
        sender.getOutgoingInvocations().add(this);
    }

    @FameProperty(name = "replicas", derived = true)
    public Replica getReplicas() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "numberOfLinesOfCode")
    public Number getNumberOfLinesOfCode() {
        return numberOfLinesOfCode;
    }

    public void setNumberOfLinesOfCode(Number numberOfLinesOfCode) {
        this.numberOfLinesOfCode = numberOfLinesOfCode;
    }

    @FameProperty(name = "numberOfComments", derived = true)
    public Number getNumberOfComments() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "hasComments", derived = true)
    public Boolean getHasComments() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "sourceText", derived = true)
    public String getSourceText() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @FameProperty(name = "comments", opposite = "container", derived = true)
    public Collection<TComment> getComments() {
        if (comments == null) {
            comments = new MultivalueSet<TComment>() {
                @Override
                protected void clearOpposite(TComment e) {
                    e.setContainer(null);
                }

                @Override
                protected void setOpposite(TComment e) {
                    e.setContainer(Invocation.this);
                }
            };
        }
        return comments;
    }

    public void setComments(Collection<? extends TComment> comments) {
        this.getComments().clear();
        this.getComments().addAll(comments);
    }


    public void addComments(TComment one) {
        this.getComments().add(one);
    }

    public void addComments(TComment one, TComment... many) {
        this.getComments().add(one);
        for (TComment each : many)
            this.getComments().add(each);
    }

    public void addComments(Iterable<? extends TComment> many) {
        for (TComment each : many)
            this.getComments().add(each);
    }

    public void addComments(TComment[] many) {
        for (TComment each : many)
            this.getComments().add(each);
    }

    public int numberOfComments() {
        return getComments().size();
    }

    public boolean hasComments() {
        return !getComments().isEmpty();
    }

    @FameProperty(name = "isStub")
    public Boolean getIsStub() {
        return isStub;
    }

    public void setIsStub(Boolean isStub) {
        this.isStub = isStub;
    }

    @FameProperty(name = "candidates", opposite = "incomingInvocations")
    public Collection<TInvocable> getCandidates() {
        if (candidates == null) {
            candidates = new MultivalueSet<TInvocable>() {
                @Override
                protected void clearOpposite(TInvocable e) {
                    e.getIncomingInvocations().remove(Invocation.this);
                }

                @Override
                protected void setOpposite(TInvocable e) {
                    e.getIncomingInvocations().add(Invocation.this);
                }
            };
        }
        return candidates;
    }

    public void setCandidates(Collection<? extends TInvocable> candidates) {
        this.getCandidates().clear();
        this.getCandidates().addAll(candidates);
    }

    public void addCandidates(TInvocable one) {
        this.getCandidates().add(one);
    }

    public void addCandidates(TInvocable one, TInvocable... many) {
        this.getCandidates().add(one);
        for (TInvocable each : many)
            this.getCandidates().add(each);
    }

    public void addCandidates(Iterable<? extends TInvocable> many) {
        for (TInvocable each : many)
            this.getCandidates().add(each);
    }

    public void addCandidates(TInvocable[] many) {
        for (TInvocable each : many)
            this.getCandidates().add(each);
    }

    public int numberOfCandidates() {
        return getCandidates().size();
    }

    public boolean hasCandidates() {
        return !getCandidates().isEmpty();
    }

    @FameProperty(name = "declaredSourceLanguage", opposite = "sourcedEntities")
    public TSourceLanguage getDeclaredSourceLanguage() {
        return declaredSourceLanguage;
    }

    public void setDeclaredSourceLanguage(TSourceLanguage declaredSourceLanguage) {
        if (this.declaredSourceLanguage != null) {
            if (this.declaredSourceLanguage.equals(declaredSourceLanguage)) return;
            this.declaredSourceLanguage.getSourcedEntities().remove(this);
        }
        this.declaredSourceLanguage = declaredSourceLanguage;
        if (declaredSourceLanguage == null) return;
        declaredSourceLanguage.getSourcedEntities().add(this);
    }

    @FameProperty(name = "signature")
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @FameProperty(name = "sourceAnchor", opposite = "element", derived = true)
    public TSourceAnchor getSourceAnchor() {
        return sourceAnchor;
    }

    public void setSourceAnchor(TSourceAnchor sourceAnchor) {
        if (this.sourceAnchor == null ? sourceAnchor != null : !this.sourceAnchor.equals(sourceAnchor)) {
            TSourceAnchor old_sourceAnchor = this.sourceAnchor;
            this.sourceAnchor = sourceAnchor;
            if (old_sourceAnchor != null) old_sourceAnchor.setElement(null);
            if (sourceAnchor != null) sourceAnchor.setElement(this);
        }
    }

    @FameProperty(name = "next", opposite = "previous", derived = true)
    public TAssociation getNext() {
        return next;
    }

    public void setNext(TAssociation next) {
        if (this.next == null ? next != null : !this.next.equals(next)) {
            TAssociation old_next = this.next;
            this.next = next;
            if (old_next != null) old_next.setPrevious(null);
            if (next != null) next.setPrevious(this);
        }
    }

    @FameProperty(name = "duplicationRate", derived = true)
    public Number getDuplicationRate() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");
    }


}

