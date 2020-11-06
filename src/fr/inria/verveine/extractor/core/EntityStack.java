package fr.inria.verveine.extractor.core;

import org.moosetechnology.model.famix.famix.Access;
import org.moosetechnology.model.famix.famix.AnnotationTypeAttribute;
import org.moosetechnology.model.famix.famix.BehaviouralEntity;
import org.moosetechnology.model.famix.famix.Invocation;
import org.moosetechnology.model.famix.famix.Method;
import org.moosetechnology.model.famix.famix.NamedEntity;
import org.moosetechnology.model.famix.famix.Namespace;
import org.moosetechnology.model.famix.famix.Package;
import org.moosetechnology.model.famix.famix.Reference;
import org.moosetechnology.model.famix.famix.Type;
import java.util.Stack;

public class EntityStack {

    public static final int EMPTY_CYCLO = 0;
    public static final int EMPTY_NOS = 0;
    private Stack<NamedEntity> stack;
    Invocation lastInvocation = null;
    Access lastAccess = null;
    Reference lastReference = null;

    public Access getLastAccess() {
        return this.lastAccess;
    }

    public void setLastAccess(Access var1) {
        this.lastAccess = var1;
    }

    public Reference getLastReference() {
        return this.lastReference;
    }

    public void setLastReference(Reference var1) {
        this.lastReference = var1;
    }

    public Invocation getLastInvocation() {
        return this.lastInvocation;
    }

    public void setLastInvocation(Invocation var1) {
        this.lastInvocation = var1;
    }

    public EntityStack() {
        this.clearPckg();
    }

    public void push(NamedEntity var1) {
        this.stack.push(var1);
    }

    public void pushPckg(Package var1) {
        this.push(var1);
    }

    public void pushPckg(Namespace var1) {
        this.push(var1);
    }

    public void pushType(Type var1) {
        this.push(var1);
    }

    public void pushMethod(Method var1) {
        this.push(var1);
        this.push(new fr.inria.verveine.extractor.core.EntityStack.MetricHolder(var1));
    }

    public void pushBehaviouralEntity(BehaviouralEntity var1) {
        this.push(var1);
        this.push(new fr.inria.verveine.extractor.core.EntityStack.MetricHolder(var1));
    }

    public void pushAnnotationMember(AnnotationTypeAttribute var1) {
        this.push(var1);
    }

    public void clearPckg() {
        this.stack = new Stack();
    }

    public void clearTypes() {
        while(!(this.top() instanceof Namespace)) {
            this.popUpto(Type.class);
        }

    }

    private <T extends NamedEntity> T popUpto(Class<T> var1) {
        NamedEntity var2;
        for(var2 = null; !this.stack.isEmpty() && !var1.isInstance(var2); var2 = this.pop()) {
        }

        return this.stack.isEmpty() ? null : (T) var2;
    }

    private <T extends NamedEntity> T lookUpto(Class<T> var1) {
        int var2;
        for(var2 = this.stack.size() - 1; var2 >= 0 && !var1.isInstance(this.stack.get(var2)); --var2) {
        }

        return var2 < 0 ? null : (T) this.stack.get(var2);
    }

    public NamedEntity pop() {
        if (this.stack.isEmpty()) {
            return null;
        } else {
            NamedEntity var1 = (NamedEntity)this.stack.pop();
            return var1 instanceof fr.inria.verveine.extractor.core.EntityStack.MetricHolder ? (NamedEntity)this.stack.pop() : var1;
        }
    }

    public Package popPckg() {
        return (Package)this.popUpto(Package.class);
    }

    public Type popType() {
        return (Type)this.popUpto(Type.class);
    }

    public Namespace popNamespace() {
        return (Namespace)this.popUpto(Namespace.class);
    }

    public Method popMethod() {
        return (Method)this.popUpto(Method.class);
    }

    public AnnotationTypeAttribute popAnnotationMember() {
        return (AnnotationTypeAttribute)this.popUpto(AnnotationTypeAttribute.class);
    }

    public NamedEntity top() {
        if (this.stack.isEmpty()) {
            return null;
        } else {
            NamedEntity var1 = (NamedEntity)this.stack.peek();
            return (NamedEntity)(var1 instanceof fr.inria.verveine.extractor.core.EntityStack.MetricHolder ? ((fr.inria.verveine.extractor.core.EntityStack.MetricHolder)var1).getEntity() : var1);
        }
    }

    public Package topPckg() {
        return (Package)this.lookUpto(Package.class);
    }

    public Type topType() {
        return (Type)this.lookUpto(Type.class);
    }

    public Namespace topNamespace() {
        return (Namespace)this.lookUpto(Namespace.class);
    }

    public BehaviouralEntity topBehaviouralEntity() {
        return (BehaviouralEntity)this.lookUpto(BehaviouralEntity.class);
    }

    public Method topMethod() {
        return (Method)this.lookUpto(Method.class);
    }

    public AnnotationTypeAttribute topAnnotationMember() {
        return (AnnotationTypeAttribute)this.lookUpto(AnnotationTypeAttribute.class);
    }

    public int getTopMethodCyclo() {
        fr.inria.verveine.extractor.core.EntityStack.MetricHolder var1 = (fr.inria.verveine.extractor.core.EntityStack.MetricHolder)this.lookUpto(fr.inria.verveine.extractor.core.EntityStack.MetricHolder.class);
        return var1 != null ? var1.getCyclo() : 0;
    }

    public int getTopMethodNOS() {
        fr.inria.verveine.extractor.core.EntityStack.MetricHolder var1 = (fr.inria.verveine.extractor.core.EntityStack.MetricHolder)this.lookUpto(fr.inria.verveine.extractor.core.EntityStack.MetricHolder.class);
        return var1 != null ? var1.getNos() : 0;
    }

    public void setTopMethodCyclo(int var1) {
        fr.inria.verveine.extractor.core.EntityStack.MetricHolder var2 = (fr.inria.verveine.extractor.core.EntityStack.MetricHolder)this.lookUpto(fr.inria.verveine.extractor.core.EntityStack.MetricHolder.class);
        if (var2 != null) {
            var2.setCyclo(var1);
        }

    }

    public void setTopMethodNOS(int var1) {
        fr.inria.verveine.extractor.core.EntityStack.MetricHolder var2 = (fr.inria.verveine.extractor.core.EntityStack.MetricHolder)this.lookUpto(fr.inria.verveine.extractor.core.EntityStack.MetricHolder.class);
        if (var2 != null) {
            var2.setNos(var1);
        }

    }

    public void addTopMethodCyclo(int var1) {
        fr.inria.verveine.extractor.core.EntityStack.MetricHolder var2 = (fr.inria.verveine.extractor.core.EntityStack.MetricHolder)this.lookUpto(fr.inria.verveine.extractor.core.EntityStack.MetricHolder.class);
        if (var2 != null) {
            var2.setCyclo(var2.getCyclo() + var1);
        }

    }

    public void addTopMethodNOS(int var1) {
        fr.inria.verveine.extractor.core.EntityStack.MetricHolder var2 = (fr.inria.verveine.extractor.core.EntityStack.MetricHolder)this.lookUpto(fr.inria.verveine.extractor.core.EntityStack.MetricHolder.class);
        if (var2 != null) {
            var2.setNos(var2.getNos() + var1);
        }

    }

    private class MetricHolder extends NamedEntity {
        private int metric_cyclo = 0;
        private int metric_nos = 0;
        private BehaviouralEntity ent;

        protected MetricHolder(BehaviouralEntity var2) {
            this.ent = var2;
        }

        protected int getCyclo() {
            return this.metric_cyclo;
        }

        protected void setCyclo(int var1) {
            this.metric_cyclo = var1;
        }

        protected int getNos() {
            return this.metric_nos;
        }

        protected void setNos(int var1) {
            this.metric_nos = var1;
        }

        protected BehaviouralEntity getEntity() {
            return this.ent;
        }
    }

}
