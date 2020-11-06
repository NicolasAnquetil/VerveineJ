// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import org.moosetechnology.model.famix.famixtraits.TParameter;
import org.moosetechnology.model.famix.famixtraits.THasSignature;
import org.moosetechnology.model.famix.famixtraits.TWithDereferencedInvocations;
import ch.akuhn.fame.internal.MultivalueSet;
import org.moosetechnology.model.famix.famixtraits.TLocalVariable;
import org.moosetechnology.model.famix.famixtraits.TReference;
import org.moosetechnology.model.famix.famixtraits.TInvocable;
import org.moosetechnology.model.famix.famixtraits.TComment;
import org.moosetechnology.model.famix.famixtraits.TWithParameters;
import org.moosetechnology.model.famix.famixtraits.TAccess;
import org.moosetechnology.model.famix.famixtraits.TWithComments;
import org.moosetechnology.model.famix.famixtraits.TWithLocalVariables;
import org.moosetechnology.model.famix.famixtraits.TSourceLanguage;
import org.moosetechnology.model.famix.famixtraits.TSourceEntity;
import org.moosetechnology.model.famix.famixtraits.TWithInvocations;
import org.moosetechnology.model.famix.famixtraits.TSourceAnchor;
import ch.akuhn.fame.FamePackage;
import org.moosetechnology.model.famix.famixtraits.TImplicitVariable;
import org.moosetechnology.model.famix.famixtraits.TInvocation;
import org.moosetechnology.model.famix.famixtraits.TWithAccesses;
import org.moosetechnology.model.famix.famixtraits.TWithReferences;
import org.moosetechnology.model.famix.famixtraits.TWithSourceLanguage;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import org.moosetechnology.model.famix.famixtraits.TWithImplicitVariables;
import org.moosetechnology.model.famix.famixtraits.TTypedEntity;
import org.moosetechnology.model.famix.famixtraits.TDereferencedInvocation;
import org.moosetechnology.model.famix.famixtraits.TWithStatements;
import org.moosetechnology.model.famix.famixreplication.Replica;
import org.moosetechnology.model.famix.famixtraits.TType;


@FamePackage("FAMIX")
@FameDescription("BehaviouralEntity")
public class BehaviouralEntity extends ContainerEntity implements TWithReferences, TWithDereferencedInvocations, TSourceEntity, TWithAccesses, TWithImplicitVariables, TWithParameters, TWithInvocations, TInvocable, TWithLocalVariables, TWithSourceLanguage, THasSignature, TTypedEntity, TWithStatements, TWithComments {

    private Collection<TInvocation> incomingInvocations; 

    private Collection<TComment> comments; 

    private Collection<TLocalVariable> localVariables; 

    private Collection<TDereferencedInvocation> dereferencedInvocations; 

    private TType declaredType;
    
    private Collection<TReference> outgoingReferences; 

    private Number numberOfLinesOfCode;
    
    private Collection<TInvocation> outgoingInvocations; 

    private Collection<TParameter> parameters; 

    private TSourceAnchor sourceAnchor;
    
    private Number cyclomaticComplexity;
    
    private Collection<TImplicitVariable> implicitVariables; 

    private Collection<TAccess> accesses; 

    private Boolean isStub;
    
    private String signature;
    
    private TSourceLanguage declaredSourceLanguage;


    private Number numberOfStatements;
    @FameProperty(name = "numberOfStatements", derived = true)
    public Number getNumberOfStatements() {
        return numberOfStatements;
    }
    public void setNumberOfStatements(Number var1) {
        this.numberOfStatements = var1;
    }

    private String bodyHash;
    @FameProperty(name = "bodyHash")
    public String getBodyHash() {
        return this.bodyHash;
    }

    public void setBodyHash(String bodyHash) {
        this.bodyHash = bodyHash;
    }

    @FameProperty(name = "numberOfAccesses", derived = true)
    public Number getNumberOfAccesses() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "clientBehaviours", derived = true)
    public Collection<BehaviouralEntity> getClientBehaviours() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
        
    @FameProperty(name = "numberOfOutgoingInvocations", derived = true)
    public Number getNumberOfOutgoingInvocations() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "providerBehaviours", derived = true)
    public Collection<BehaviouralEntity> getProviderBehaviours() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
        
    @FameProperty(name = "numberOfConditionals", derived = true)
    public Number getNumberOfConditionals() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOfMessageSends", derived = true)
    public Number getNumberOfMessageSends() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOfComments", derived = true)
    public Number getNumberOfComments() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "incomingInvocations", opposite = "candidates", derived = true)
    public Collection<TInvocation> getIncomingInvocations() {
        if (incomingInvocations == null) {
            incomingInvocations = new MultivalueSet<TInvocation>() {
                @Override
                protected void clearOpposite(TInvocation e) {
                    e.getCandidates().remove(BehaviouralEntity.this);
                }
                @Override
                protected void setOpposite(TInvocation e) {
                    e.getCandidates().add(BehaviouralEntity.this);
                }
            };
        }
        return incomingInvocations;
    }
    
    public void setIncomingInvocations(Collection<? extends TInvocation> incomingInvocations) {
        this.getIncomingInvocations().clear();
        this.getIncomingInvocations().addAll(incomingInvocations);
    }
    
    public void addIncomingInvocations(TInvocation one) {
        this.getIncomingInvocations().add(one);
    }   
    
    public void addIncomingInvocations(TInvocation one, TInvocation... many) {
        this.getIncomingInvocations().add(one);
        for (TInvocation each : many)
            this.getIncomingInvocations().add(each);
    }   
    
    public void addIncomingInvocations(Iterable<? extends TInvocation> many) {
        for (TInvocation each : many)
            this.getIncomingInvocations().add(each);
    }   
                
    public void addIncomingInvocations(TInvocation[] many) {
        for (TInvocation each : many)
            this.getIncomingInvocations().add(each);
    }
    
    public int numberOfIncomingInvocations() {
        return getIncomingInvocations().size();
    }

    public boolean hasIncomingInvocations() {
        return !getIncomingInvocations().isEmpty();
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
                    e.setContainer(BehaviouralEntity.this);
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

    @FameProperty(name = "localVariables", opposite = "parentBehaviouralEntity", derived = true)
    public Collection<TLocalVariable> getLocalVariables() {
        if (localVariables == null) {
            localVariables = new MultivalueSet<TLocalVariable>() {
                @Override
                protected void clearOpposite(TLocalVariable e) {
                    e.setParentBehaviouralEntity(null);
                }
                @Override
                protected void setOpposite(TLocalVariable e) {
                    e.setParentBehaviouralEntity(BehaviouralEntity.this);
                }
            };
        }
        return localVariables;
    }
    
    public void setLocalVariables(Collection<? extends TLocalVariable> localVariables) {
        this.getLocalVariables().clear();
        this.getLocalVariables().addAll(localVariables);
    }                    
    
        
    public void addLocalVariables(TLocalVariable one) {
        this.getLocalVariables().add(one);
    }   
    
    public void addLocalVariables(TLocalVariable one, TLocalVariable... many) {
        this.getLocalVariables().add(one);
        for (TLocalVariable each : many)
            this.getLocalVariables().add(each);
    }   
    
    public void addLocalVariables(Iterable<? extends TLocalVariable> many) {
        for (TLocalVariable each : many)
            this.getLocalVariables().add(each);
    }   
                
    public void addLocalVariables(TLocalVariable[] many) {
        for (TLocalVariable each : many)
            this.getLocalVariables().add(each);
    }
    
    public int numberOfLocalVariables() {
        return getLocalVariables().size();
    }

    public boolean hasLocalVariables() {
        return !getLocalVariables().isEmpty();
    }

    @FameProperty(name = "duplicationRate", derived = true)
    public Number getDuplicationRate() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "dereferencedInvocations", opposite = "referencer", derived = true)
    public Collection<TDereferencedInvocation> getDereferencedInvocations() {
        if (dereferencedInvocations == null) {
            dereferencedInvocations = new MultivalueSet<TDereferencedInvocation>() {
                @Override
                protected void clearOpposite(TDereferencedInvocation e) {
                    e.setReferencer(null);
                }
                @Override
                protected void setOpposite(TDereferencedInvocation e) {
                    e.setReferencer(BehaviouralEntity.this);
                }
            };
        }
        return dereferencedInvocations;
    }
    
    public void setDereferencedInvocations(Collection<? extends TDereferencedInvocation> dereferencedInvocations) {
        this.getDereferencedInvocations().clear();
        this.getDereferencedInvocations().addAll(dereferencedInvocations);
    }                    
    
        
    public void addDereferencedInvocations(TDereferencedInvocation one) {
        this.getDereferencedInvocations().add(one);
    }   
    
    public void addDereferencedInvocations(TDereferencedInvocation one, TDereferencedInvocation... many) {
        this.getDereferencedInvocations().add(one);
        for (TDereferencedInvocation each : many)
            this.getDereferencedInvocations().add(each);
    }   
    
    public void addDereferencedInvocations(Iterable<? extends TDereferencedInvocation> many) {
        for (TDereferencedInvocation each : many)
            this.getDereferencedInvocations().add(each);
    }   
                
    public void addDereferencedInvocations(TDereferencedInvocation[] many) {
        for (TDereferencedInvocation each : many)
            this.getDereferencedInvocations().add(each);
    }
    
    public int numberOfDereferencedInvocations() {
        return getDereferencedInvocations().size();
    }

    public boolean hasDereferencedInvocations() {
        return !getDereferencedInvocations().isEmpty();
    }

    @FameProperty(name = "numberOfLinesOfCodeWithMoreThanOneCharacter", derived = true)
    public Number getNumberOfLinesOfCodeWithMoreThanOneCharacter() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "declaredType", opposite = "typedEntities")
    public TType getDeclaredType() {
        return declaredType;
    }

    public void setDeclaredType(TType declaredType) {
        if (this.declaredType != null) {
            if (this.declaredType.equals(declaredType)) return;
            this.declaredType.getTypedEntities().remove(this);
        }
        this.declaredType = declaredType;
        if (declaredType == null) return;
        declaredType.getTypedEntities().add(this);
    }
    
    @FameProperty(name = "outgoingReferences", opposite = "referencer", derived = true)
    public Collection<TReference> getOutgoingReferences() {
        if (outgoingReferences == null) {
            outgoingReferences = new MultivalueSet<TReference>() {
                @Override
                protected void clearOpposite(TReference e) {
                    e.setReferencer(null);
                }
                @Override
                protected void setOpposite(TReference e) {
                    e.setReferencer(BehaviouralEntity.this);
                }
            };
        }
        return outgoingReferences;
    }
    
    public void setOutgoingReferences(Collection<? extends TReference> outgoingReferences) {
        this.getOutgoingReferences().clear();
        this.getOutgoingReferences().addAll(outgoingReferences);
    }                    
    
        
    public void addOutgoingReferences(TReference one) {
        this.getOutgoingReferences().add(one);
    }   
    
    public void addOutgoingReferences(TReference one, TReference... many) {
        this.getOutgoingReferences().add(one);
        for (TReference each : many)
            this.getOutgoingReferences().add(each);
    }   
    
    public void addOutgoingReferences(Iterable<? extends TReference> many) {
        for (TReference each : many)
            this.getOutgoingReferences().add(each);
    }   
                
    public void addOutgoingReferences(TReference[] many) {
        for (TReference each : many)
            this.getOutgoingReferences().add(each);
    }
    
    public int numberOfOutgoingReferences() {
        return getOutgoingReferences().size();
    }

    public boolean hasOutgoingReferences() {
        return !getOutgoingReferences().isEmpty();
    }

    @FameProperty(name = "numberOfLinesOfCode")
    public Number getNumberOfLinesOfCode() {
        return numberOfLinesOfCode;
    }

    public void setNumberOfLinesOfCode(Number numberOfLinesOfCode) {
        this.numberOfLinesOfCode = numberOfLinesOfCode;
    }
    
    @FameProperty(name = "outgoingInvocations", opposite = "sender", derived = true)
    public Collection<TInvocation> getOutgoingInvocations() {
        if (outgoingInvocations == null) {
            outgoingInvocations = new MultivalueSet<TInvocation>() {
                @Override
                protected void clearOpposite(TInvocation e) {
                    e.setSender(null);
                }
                @Override
                protected void setOpposite(TInvocation e) {
                    e.setSender(BehaviouralEntity.this);
                }
            };
        }
        return outgoingInvocations;
    }
    
    public void setOutgoingInvocations(Collection<? extends TInvocation> outgoingInvocations) {
        this.getOutgoingInvocations().clear();
        this.getOutgoingInvocations().addAll(outgoingInvocations);
    }                    
    
        
    public void addOutgoingInvocations(TInvocation one) {
        this.getOutgoingInvocations().add(one);
    }   
    
    public void addOutgoingInvocations(TInvocation one, TInvocation... many) {
        this.getOutgoingInvocations().add(one);
        for (TInvocation each : many)
            this.getOutgoingInvocations().add(each);
    }   
    
    public void addOutgoingInvocations(Iterable<? extends TInvocation> many) {
        for (TInvocation each : many)
            this.getOutgoingInvocations().add(each);
    }   
                
    public void addOutgoingInvocations(TInvocation[] many) {
        for (TInvocation each : many)
            this.getOutgoingInvocations().add(each);
    }
    
    public int numberOfOutgoingInvocations() {
        return getOutgoingInvocations().size();
    }

    public boolean hasOutgoingInvocations() {
        return !getOutgoingInvocations().isEmpty();
    }

    @FameProperty(name = "parameters", opposite = "parentBehaviouralEntity", derived = true)
    public Collection<TParameter> getParameters() {
        if (parameters == null) {
            parameters = new MultivalueSet<TParameter>() {
                @Override
                protected void clearOpposite(TParameter e) {
                    e.setParentBehaviouralEntity(null);
                }
                @Override
                protected void setOpposite(TParameter e) {
                    e.setParentBehaviouralEntity(BehaviouralEntity.this);
                }
            };
        }
        return parameters;
    }
    
    public void setParameters(Collection<? extends TParameter> parameters) {
        this.getParameters().clear();
        this.getParameters().addAll(parameters);
    }                    
    
        
    public void addParameters(TParameter one) {
        this.getParameters().add(one);
    }   
    
    public void addParameters(TParameter one, TParameter... many) {
        this.getParameters().add(one);
        for (TParameter each : many)
            this.getParameters().add(each);
    }   
    
    public void addParameters(Iterable<? extends TParameter> many) {
        for (TParameter each : many)
            this.getParameters().add(each);
    }   
                
    public void addParameters(TParameter[] many) {
        for (TParameter each : many)
            this.getParameters().add(each);
    }
    
    public int numberOfParameters() {
        return getParameters().size();
    }

    public boolean hasParameters() {
        return !getParameters().isEmpty();
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
    
    @FameProperty(name = "cyclomaticComplexity")
    public Number getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    public void setCyclomaticComplexity(Number cyclomaticComplexity) {
        this.cyclomaticComplexity = cyclomaticComplexity;
    }
    
    @FameProperty(name = "hasComments", derived = true)
    public Boolean getHasComments() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "replicas", derived = true)
    public Replica getReplicas() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOflinesOfDeadCode", derived = true)
    public Number getNumberOflinesOfDeadCode() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "implicitVariables", opposite = "parentBehaviouralEntity", derived = true)
    public Collection<TImplicitVariable> getImplicitVariables() {
        if (implicitVariables == null) {
            implicitVariables = new MultivalueSet<TImplicitVariable>() {
                @Override
                protected void clearOpposite(TImplicitVariable e) {
                    e.setParentBehaviouralEntity(null);
                }
                @Override
                protected void setOpposite(TImplicitVariable e) {
                    e.setParentBehaviouralEntity(BehaviouralEntity.this);
                }
            };
        }
        return implicitVariables;
    }
    
    public void setImplicitVariables(Collection<? extends TImplicitVariable> implicitVariables) {
        this.getImplicitVariables().clear();
        this.getImplicitVariables().addAll(implicitVariables);
    }                    
    
        
    public void addImplicitVariables(TImplicitVariable one) {
        this.getImplicitVariables().add(one);
    }   
    
    public void addImplicitVariables(TImplicitVariable one, TImplicitVariable... many) {
        this.getImplicitVariables().add(one);
        for (TImplicitVariable each : many)
            this.getImplicitVariables().add(each);
    }   
    
    public void addImplicitVariables(Iterable<? extends TImplicitVariable> many) {
        for (TImplicitVariable each : many)
            this.getImplicitVariables().add(each);
    }   
                
    public void addImplicitVariables(TImplicitVariable[] many) {
        for (TImplicitVariable each : many)
            this.getImplicitVariables().add(each);
    }
    
    public int numberOfImplicitVariables() {
        return getImplicitVariables().size();
    }

    public boolean hasImplicitVariables() {
        return !getImplicitVariables().isEmpty();
    }

    @FameProperty(name = "sourceText", derived = true)
    public String getSourceText() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "accesses", opposite = "accessor", derived = true)
    public Collection<TAccess> getAccesses() {
        if (accesses == null) {
            accesses = new MultivalueSet<TAccess>() {
                @Override
                protected void clearOpposite(TAccess e) {
                    e.setAccessor(null);
                }
                @Override
                protected void setOpposite(TAccess e) {
                    e.setAccessor(BehaviouralEntity.this);
                }
            };
        }
        return accesses;
    }
    
    public void setAccesses(Collection<? extends TAccess> accesses) {
        this.getAccesses().clear();
        this.getAccesses().addAll(accesses);
    }                    
    
        
    public void addAccesses(TAccess one) {
        this.getAccesses().add(one);
    }   
    
    public void addAccesses(TAccess one, TAccess... many) {
        this.getAccesses().add(one);
        for (TAccess each : many)
            this.getAccesses().add(each);
    }   
    
    public void addAccesses(Iterable<? extends TAccess> many) {
        for (TAccess each : many)
            this.getAccesses().add(each);
    }   
                
    public void addAccesses(TAccess[] many) {
        for (TAccess each : many)
            this.getAccesses().add(each);
    }
    
    public int numberOfAccesses() {
        return getAccesses().size();
    }

    public boolean hasAccesses() {
        return !getAccesses().isEmpty();
    }

    @FameProperty(name = "isStub")
    public Boolean getIsStub() {
        return isStub;
    }

    public void setIsStub(Boolean isStub) {
        this.isStub = isStub;
    }
    
    @FameProperty(name = "signature")
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
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
    
    @FameProperty(name = "containsReplicas", derived = true)
    public Boolean getContainsReplicas() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    
    @FameProperty(name = "numberOfParameters", derived = true)
    public Number getNumberOfParameters() {
        // TODO: this is a derived property, implement this method manually.
        throw new UnsupportedOperationException("Not yet implemented!");  
    }
    


}

