package fr.inria.verveine.extractor.core;


import ch.akuhn.fame.Repository;
import org.moosetechnology.model.famix.famix.Access;
import org.moosetechnology.model.famix.famix.AnnotationInstance;
import org.moosetechnology.model.famix.famix.AnnotationInstanceAttribute;
import org.moosetechnology.model.famix.famix.AnnotationType;
import org.moosetechnology.model.famix.famix.AnnotationTypeAttribute;
import org.moosetechnology.model.famix.famixtraits.TAssociation;
import org.moosetechnology.model.famix.famix.Attribute;
import org.moosetechnology.model.famix.famix.BehaviouralEntity;
import org.moosetechnology.model.famix.famix.CaughtException;
import org.moosetechnology.model.famix.famix.Comment;
import org.moosetechnology.model.famix.famix.ContainerEntity;
import org.moosetechnology.model.famix.famix.DeclaredException;
import org.moosetechnology.model.famix.famix.Entity;
import org.moosetechnology.model.famix.famix.Enum;
import org.moosetechnology.model.famix.famix.EnumValue;
import org.moosetechnology.model.famix.famix.Function;
import org.moosetechnology.model.famix.famix.ImplicitVariable;
import org.moosetechnology.model.famix.famix.Inheritance;
import org.moosetechnology.model.famix.famix.Invocation;
import org.moosetechnology.model.famix.famix.LocalVariable;
import org.moosetechnology.model.famix.famix.Method;
import org.moosetechnology.model.famix.famix.NamedEntity;
import org.moosetechnology.model.famix.famix.Namespace;
import org.moosetechnology.model.famix.famix.Parameter;
import org.moosetechnology.model.famix.famix.ParameterType;
import org.moosetechnology.model.famix.famix.ParameterizableClass;
import org.moosetechnology.model.famix.famix.ParameterizedType;
import org.moosetechnology.model.famix.famix.PrimitiveType;
import org.moosetechnology.model.famix.famix.Reference;
import org.moosetechnology.model.famix.famix.SourcedEntity;
import org.moosetechnology.model.famix.famix.StructuralEntity;
import org.moosetechnology.model.famix.famix.ThrownException;
import org.moosetechnology.model.famix.famix.Type;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class Dictionary<B> {
    public static final String DEFAULT_PCKG_NAME = "<Default Package>";
    public static final String STUB_METHOD_CONTAINER_NAME = "<StubMethodContainer>";
    public static final String SELF_NAME = "self";
    public static final String SUPER_NAME = "super";
    public static final String CONSTRUCTOR_KIND_MARKER = "constructor";
    protected Repository famixRepo;
    protected Map<B, NamedEntity> keyToEntity;
    protected Map<NamedEntity, B> entityToKey;
    protected Map<String, Collection<NamedEntity>> nameToEntity;
    /** @deprecated */
    @Deprecated
    protected Map<Type, Dictionary<B>.ImplicitVars> typeToImpVar;

    public Dictionary(Repository var1) {
        this.famixRepo = var1;
        this.keyToEntity = new Hashtable();
        this.entityToKey = new Hashtable();
        this.nameToEntity = new Hashtable();
        this.typeToImpVar = new Hashtable();
        if (!this.famixRepo.isEmpty()) {
            this.recoverExistingRepository();
        }

    }

    protected void recoverExistingRepository() {
        Iterator var1 = this.famixRepo.all(NamedEntity.class).iterator();

        while(var1.hasNext()) {
            NamedEntity var2 = (NamedEntity)var1.next();
            this.mapEntityToName(var2.getName(), var2);

            try {
                if (var2.getIsStub()) {
                }
            } catch (NullPointerException var5) {
                var2.setIsStub(Boolean.FALSE);
            }
        }

        var1 = this.famixRepo.all(Access.class).iterator();

        while(var1.hasNext()) {
            Access var6 = (Access)var1.next();

            try {
                if (var6.getIsWrite()) {
                }
            } catch (NullPointerException var4) {
                var6.setIsWrite(Boolean.FALSE);
            }
        }

    }

    protected void mapEntityToName(String var1, NamedEntity var2) {
        Object var3 = (Collection)this.nameToEntity.get(var1);
        if (var3 == null) {
            var3 = new LinkedList();
        }

        ((Collection)var3).add(var2);
        this.nameToEntity.put(var1, (Collection<NamedEntity>) var3);
    }

    public void removeEntity(NamedEntity var1) {
        Object var2 = this.entityToKey.get(var1);
        this.entityToKey.remove(var1);
        this.keyToEntity.remove(var2);
        Collection var3 = (Collection)this.nameToEntity.get(var1.getName());
        var3.remove(var1);
        this.famixRepo.getElements().remove(var1);
    }

    protected void mapEntityToKey(B var1, NamedEntity var2) {
        NamedEntity var3 = (NamedEntity)this.keyToEntity.get(var1);
        if (var3 != null) {
            this.entityToKey.remove(var3);
        }

        this.keyToEntity.put(var1, var2);
        this.entityToKey.put(var2, var1);
    }

    public <T extends NamedEntity> Collection<T> getEntityByName(Class<T> var1, String var2) {
        LinkedList var3 = new LinkedList();
        Collection var4 = (Collection)this.nameToEntity.get(var2);
        if (var4 != null) {
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
                NamedEntity var6 = (NamedEntity)var5.next();
                if (var1.isInstance(var6)) {
                    var3.add(var6);
                }
            }
        }

        return var3;
    }

    public NamedEntity getEntityByKey(B var1) {
        return var1 == null ? null : (NamedEntity)this.keyToEntity.get(var1);
    }

    public B getEntityKey(NamedEntity var1) {
        return this.entityToKey.get(var1);
    }

    protected <T extends NamedEntity> T createFamixEntity(Class<T> var1, String var2, boolean var3) {
        NamedEntity var4 = null;
        if (var2 == null) {
            return null;
        } else {
            try {
                var4 = (NamedEntity)var1.newInstance();
            } catch (Exception var6) {
                System.err.println("Unexpected error, could not create a FAMIX entity: " + var6.getMessage());
                var6.printStackTrace();
            }

            if (var4 != null) {
                var4.setName(var2);
                var4.setIsStub(Boolean.TRUE);
                this.mapEntityToName(var2, var4);
                if (var3) {
                    this.famixRepo.add(var4);
                }
            }

            return (T) var4;
        }
    }

    protected <T extends NamedEntity> T ensureFamixEntity(Class<T> var1, B var2, String var3, boolean var4) {
        NamedEntity var5 = null;
        if (var2 != null) {
            var5 = this.getEntityByKey(var2);
            if (var5 != null) {
                return (T) var5;
            }
        }

        var5 = this.createFamixEntity(var1, var3, var4);
        if (var2 != null && var5 != null) {
            this.keyToEntity.put(var2, var5);
            this.entityToKey.put(var5, var2);
        }

        return (T) var5;
    }

    public void famixRepoAdd(Entity var1) {
        this.famixRepo.add(var1);
    }

    public Type ensureFamixType(B var1, String var2, ContainerEntity var3, boolean var4) {
        Type var5 = (Type)this.ensureFamixEntity(Type.class, var1, var2, var4);
        var5.setContainer(var3);
        return var5;
    }

    public org.moosetechnology.model.famix.famix.Class ensureFamixClass(B var1, String var2, ContainerEntity var3, boolean var4) {
        org.moosetechnology.model.famix.famix.Class var5 = (org.moosetechnology.model.famix.famix.Class)this.ensureFamixEntity(org.moosetechnology.model.famix.famix.Class.class, var1, var2, var4);
        var5.setContainer(var3);
        return var5;
    }

    public ParameterizableClass ensureFamixParameterizableClass(B var1, String var2, ContainerEntity var3, boolean var4) {
        ParameterizableClass var5 = (ParameterizableClass)this.ensureFamixEntity(ParameterizableClass.class, var1, var2, var4);
        var5.setContainer(var3);
        return var5;
    }

    public ParameterizedType ensureFamixParameterizedType(B var1, String var2, ParameterizableClass var3, ContainerEntity var4, boolean var5) {
        ParameterizedType var6 = (ParameterizedType)this.ensureFamixEntity(ParameterizedType.class, var1, var2, var5);
        var6.setContainer(var4);
        var6.setParameterizableClass(var3);
        return var6;
    }

    public ParameterType ensureFamixParameterType(B var1, String var2, ContainerEntity var3, boolean var4) {
        ParameterType var5 = (ParameterType)this.ensureFamixEntity(ParameterType.class, var1, var2, var4);
        var5.setContainer(var3);
        return var5;
    }

    public Enum ensureFamixEnum(B var1, String var2, ContainerEntity var3, boolean var4) {
        Enum var5 = (Enum)this.ensureFamixEntity(Enum.class, var1, var2, var4);
        var5.setContainer(var3);
        return var5;
    }

    public EnumValue ensureFamixEnumValue(B var1, String var2, Enum var3, boolean var4) {
        EnumValue var5 = (EnumValue)this.ensureFamixEntity(EnumValue.class, var1, var2, var4);
        var5.setParentEnum(var3);
        return var5;
    }

    public AnnotationType ensureFamixAnnotationType(B var1, String var2, ContainerEntity var3, boolean var4) {
        AnnotationType var5 = (AnnotationType)this.ensureFamixEntity(AnnotationType.class, var1, var2, var4);
        var5.setContainer(var3);
        return var5;
    }

    public AnnotationTypeAttribute ensureFamixAnnotationTypeAttribute(B var1, String var2, AnnotationType var3, boolean var4) {
        AnnotationTypeAttribute var5 = (AnnotationTypeAttribute)this.ensureFamixEntity(AnnotationTypeAttribute.class, var1, var2, var4);
        var5.setParentType(var3);
        return var5;
    }

    public AnnotationInstanceAttribute createFamixAnnotationInstanceAttribute(AnnotationTypeAttribute var1, String var2) {
        AnnotationInstanceAttribute var3 = null;
        if (var1 != null && var2 != null) {
            var3 = new AnnotationInstanceAttribute();
            var3.setAnnotationTypeAttribute(var1);
            var3.setValue(var2);
            this.famixRepo.add(var3);
        }

        return var3;
    }

    public AnnotationInstance addFamixAnnotationInstance(NamedEntity var1, AnnotationType var2, Collection<AnnotationInstanceAttribute> var3) {
        AnnotationInstance var4 = null;
        if (var1 != null && var2 != null) {
            var4 = new AnnotationInstance();
            var4.setAnnotatedEntity(var1);
            var4.setAnnotationType(var2);
            var4.addAttributes(var3);
            this.famixRepo.add(var4);
        }

        return var4;
    }

    public PrimitiveType ensureFamixPrimitiveType(B var1, String var2) {
        return (PrimitiveType)this.ensureFamixUniqEntity(PrimitiveType.class, var1, var2);
    }

    public Method ensureFamixMethod(B var1, String var2, String var3, Type var4, Type var5, boolean var6) {
        Method var7 = (Method)this.ensureFamixEntity(Method.class, var1, var2, var6);
        var7.setSignature(var3);
        var7.setDeclaredType(var4);
        var7.setParentType(var5);
        return var7;
    }

    public Function ensureFamixFunction(B var1, String var2, String var3, Type var4, ContainerEntity var5, boolean var6) {
        Function var7 = (Function)this.ensureFamixEntity(Function.class, var1, var2, var6);
        var7.setSignature(var3);
        var7.setDeclaredType(var4);
        var7.setContainer(var5);
        return var7;
    }

    public Attribute ensureFamixAttribute(B var1, String var2, Type var3, Type var4, boolean var5) {
        Attribute var6 = (Attribute)this.ensureFamixEntity(Attribute.class, var1, var2, var5);
        var6.setParentType(var4);
        var6.setDeclaredType(var3);
        return var6;
    }

    public LocalVariable ensureFamixLocalVariable(B var1, String var2, Type var3, BehaviouralEntity var4, boolean var5) {
        LocalVariable var6 = (LocalVariable)this.ensureFamixEntity(LocalVariable.class, var1, var2, var5);
        var6.setParentBehaviouralEntity(var4);
        var6.setDeclaredType(var3);
        return var6;
    }

    public Comment createFamixComment(String var1) {
        Comment var2 = null;
        if (var1 != null) {
            var2 = new Comment();
            var2.setContent(var1);
            this.famixRepo.add(var2);
        }

        return var2;
    }

    public Comment createFamixComment(String var1, SourcedEntity var2) {
        Comment var3 = null;
        if (var1 != null && var2 != null) {
            var3 = this.createFamixComment(var1);
            var3.setContainer(var2);
        }

        return var3;
    }

    public Parameter createFamixParameter(B var1, String var2, Type var3, BehaviouralEntity var4, boolean var5) {
        Parameter var6 = (Parameter)this.ensureFamixEntity(Parameter.class, var1, var2, var5);
        var6.setParentBehaviouralEntity(var4);
        var6.setDeclaredType(var3);
        return var6;
    }

    public Inheritance ensureFamixInheritance(Type var1, Type var2, TAssociation var3) {
        if (var1 != null && var2 != null) {
            Iterator var4 = var1.getSubInheritances().iterator();

            Inheritance var5;
            do {
                if (!var4.hasNext()) {
                    Inheritance var6 = new Inheritance();
                    var6.setSuperclass(var1);
                    var6.setSubclass(var2);
                    this.chainPrevNext(var3, var6);
                    this.famixRepoAdd(var6);
                    return var6;
                }

                var5 = (Inheritance)var4.next();
            } while(var5.getSubclass() != var2);

            return var5;
        } else {
            return null;
        }
    }

    public Reference addFamixReference(BehaviouralEntity var1, Type var2, TAssociation var3) {
        if (var1 != null && var2 != null) {
            if (var3 == null) {
                Iterator var4 = var1.getOutgoingReferences().iterator();

                while(var4.hasNext()) {
                    Reference var5 = (Reference)var4.next();
                    if (var5.getReferredType() == var2) {
                        return var5;
                    }
                }
            }

            Reference var6 = new Reference();
            var6.setReferredType(var2);
            var6.setReferencer(var1);
            this.chainPrevNext(var3, var6);
            this.famixRepoAdd(var6);
            return var6;
        } else {
            return null;
        }
    }

    public Invocation addFamixInvocation(BehaviouralEntity var1, BehaviouralEntity var2, NamedEntity var3, String var4, TAssociation var5) {
        if (var1 != null && var2 != null) {
            Invocation var6 = new Invocation();
            var6.setReceiver(var3);
            var6.setSender(var1);
            var6.setSignature(var4 == null ? var2.getSignature() : var4);
            var6.addCandidates(var2);
            this.chainPrevNext(var5, var6);
            this.famixRepoAdd(var6);
            return var6;
        } else {
            return null;
        }
    }

    public Access addFamixAccess(BehaviouralEntity var1, StructuralEntity var2, boolean var3, TAssociation var4) {
        if (var1 != null && var2 != null) {
            Access var5 = new Access();
            var5.setAccessor(var1);
            var5.setVariable(var2);
            var5.setIsWrite(var3);
            this.chainPrevNext(var4, var5);
            this.famixRepoAdd(var5);
            return var5;
        } else {
            return null;
        }
    }

    protected void chainPrevNext(TAssociation var1, TAssociation var2) {
        if (var1 != null) {
            var2.setPrevious(var1);
        }

    }

    public DeclaredException createFamixDeclaredException(Method var1, org.moosetechnology.model.famix.famix.Class var2) {
        if (var1 != null && var2 != null) {
            DeclaredException var3 = new DeclaredException();
            var3.setExceptionClass(var2);
            var3.setDefiningEntity(var1);
            var3.setDefiningMethod(var1); // Should be removed for FamixJava but keep it in Compatibility
            this.famixRepoAdd(var3);
            return var3;
        } else {
            return null;
        }
    }

    public CaughtException createFamixCaughtException(Method var1, org.moosetechnology.model.famix.famix.Class var2) {
        if (var1 != null && var2 != null) {
            CaughtException var3 = new CaughtException();
            var3.setExceptionClass(var2);
            var3.setDefiningEntity(var1);
            var3.setDefiningMethod(var1); // Should be removed for FamixJava but keep it in Compatibility
            this.famixRepoAdd(var3);
            return var3;
        } else {
            return null;
        }
    }

    public ThrownException createFamixThrownException(Method var1, org.moosetechnology.model.famix.famix.Class var2) {
        if (var1 != null && var2 != null) {
            ThrownException var3 = new ThrownException();
            var3.setExceptionClass(var2);
            var3.setDefiningEntity(var1);
            var3.setDefiningMethod(var1); // Should be removed for FamixJava but keep it in Compatibility
            this.famixRepoAdd(var3);
            return var3;
        } else {
            return null;
        }
    }

    /** @deprecated */
    @Deprecated
    public ImplicitVariable getImplicitVariableByBinding(B var1, String var2) {
        return this.getImplicitVariableByType((org.moosetechnology.model.famix.famix.Class)this.getEntityByKey(var1), var2);
    }

    /** @deprecated */
    @Deprecated
    public ImplicitVariable getImplicitVariableByType(Type var1, String var2) {
        Dictionary.ImplicitVars var3 = (Dictionary.ImplicitVars)this.typeToImpVar.get(var1);
        ImplicitVariable var4 = null;
        if (var3 == null) {
            var3 = new Dictionary.ImplicitVars();
        }

        if (var2.equals("self")) {
            var4 = var3.self_iv;
        } else if (var2.equals("super")) {
            var4 = var3.super_iv;
        }

        return var4;
    }

    public ImplicitVariable ensureFamixImplicitVariable(B var1, String var2, Type var3, Method var4, boolean var5) {
        ImplicitVariable var6 = (ImplicitVariable)this.ensureFamixEntity(ImplicitVariable.class, var1, var2, var5);
        var6.setParentBehaviouralEntity(var4);
        return var6;
    }

    public <T extends NamedEntity> T ensureFamixUniqEntity(Class<T> var1, B var2, String var3) {
        NamedEntity var4 = null;
        if (var3 == null) {
            return null;
        } else {
            if (var2 != null) {
                var4 = this.getEntityByKey(var2);
            }

            if (var4 == null) {
                Collection var5 = this.getEntityByName(var1, var3);
                if (var5.size() > 0) {
                    var4 = (NamedEntity)var5.iterator().next();
                } else {
                    var4 = this.createFamixEntity(var1, var3, true);
                }

                if (var2 != null) {
                    this.keyToEntity.put(var2, var4);
                }
            }

            return (T) var4;
        }
    }

    public Namespace ensureFamixNamespace(B var1, String var2) {
        return (Namespace)this.ensureFamixUniqEntity(Namespace.class, var1, var2);
    }

    public Namespace ensureFamixNamespaceDefault() {
        Namespace var1 = (Namespace)this.ensureFamixUniqEntity(Namespace.class, (B) null, "<Default Package>");
        return var1;
    }

    public org.moosetechnology.model.famix.famix.Class ensureFamixClassStubOwner() {
        org.moosetechnology.model.famix.famix.Class var1 = (org.moosetechnology.model.famix.famix.Class)this.ensureFamixUniqEntity(org.moosetechnology.model.famix.famix.Class.class, (B) null, "<StubMethodContainer>");
        if (var1 != null) {
            var1.setContainer(this.ensureFamixNamespaceDefault());
        }

        return var1;
    }

    public Type searchTypeInContext(String var1, ContainerEntity var2) {
        if (var2 == null) {
            return null;
        } else {
            Iterator var3 = var2.getTypes().iterator();

            Type var4;
            do {
                if (!var3.hasNext()) {
                    return this.searchTypeInContext(var1, var2.getBelongsTo());
                }

                var4 = (Type)var3.next();
            } while(!var4.getName().equals(var1));

            return var4;
        }
    }

    /** @deprecated */
    @Deprecated
    protected class ImplicitVars {
        public ImplicitVariable self_iv;
        public ImplicitVariable super_iv;

        protected ImplicitVars() {
        }
    }
}
