package fr.inria.verveine.extractor.java;

import ch.akuhn.fame.Repository;
import fr.inria.verveine.extractor.java.utils.ImplicitVarBinding;
import fr.inria.verveine.extractor.java.utils.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.*;
import org.moosetechnology.model.famix.famixjavaentities.Access;
import org.moosetechnology.model.famix.famixjavaentities.AnnotationInstance;
import org.moosetechnology.model.famix.famixjavaentities.AnnotationInstanceAttribute;
import org.moosetechnology.model.famix.famixjavaentities.AnnotationType;
import org.moosetechnology.model.famix.famixjavaentities.AnnotationTypeAttribute;
import org.moosetechnology.model.famix.famixjavaentities.Attribute;
import org.moosetechnology.model.famix.famixjavaentities.Class;
import org.moosetechnology.model.famix.famixjavaentities.Comment;
import org.moosetechnology.model.famix.famixjavaentities.ContainerEntity;
import org.moosetechnology.model.famix.famixjavaentities.Entity;
import org.moosetechnology.model.famix.famixjavaentities.Enum;
import org.moosetechnology.model.famix.famixjavaentities.EnumValue;
import org.moosetechnology.model.famix.famixjavaentities.Exception;
import org.moosetechnology.model.famix.famixjavaentities.Implementation;
import org.moosetechnology.model.famix.famixjavaentities.ImplicitVariable;
import org.moosetechnology.model.famix.famixjavaentities.IndexedFileAnchor;
import org.moosetechnology.model.famix.famixjavaentities.Inheritance;
import org.moosetechnology.model.famix.famixjavaentities.Interface;
import org.moosetechnology.model.famix.famixjavaentities.Invocation;
import org.moosetechnology.model.famix.famixjavaentities.LocalVariable;
import org.moosetechnology.model.famix.famixjavaentities.Method;
import org.moosetechnology.model.famix.famixjavaentities.NamedEntity;
import org.moosetechnology.model.famix.famixjavaentities.Package;
import org.moosetechnology.model.famix.famixjavaentities.Parameter;
import org.moosetechnology.model.famix.famixjavaentities.ParameterType;
import org.moosetechnology.model.famix.famixjavaentities.ParameterizableClass;
import org.moosetechnology.model.famix.famixjavaentities.ParameterizableInterface;
import org.moosetechnology.model.famix.famixjavaentities.ParameterizedType;
import org.moosetechnology.model.famix.famixjavaentities.PrimitiveType;
import org.moosetechnology.model.famix.famixjavaentities.Reference;
import org.moosetechnology.model.famix.famixjavaentities.SourceAnchor;
import org.moosetechnology.model.famix.famixjavaentities.SourcedEntity;
import org.moosetechnology.model.famix.famixjavaentities.Type;
import org.moosetechnology.model.famix.famixjavaentities.UnknownVariable;
import org.moosetechnology.model.famix.famixtraits.TAccessible;
import org.moosetechnology.model.famix.famixtraits.TAssociation;
import org.moosetechnology.model.famix.famixtraits.TCanImplement;
import org.moosetechnology.model.famix.famixtraits.TComment;
import org.moosetechnology.model.famix.famixtraits.THasVisibility;
import org.moosetechnology.model.famix.famixtraits.TImplementable;
import org.moosetechnology.model.famix.famixtraits.TImplementation;
import org.moosetechnology.model.famix.famixtraits.TInheritance;
import org.moosetechnology.model.famix.famixtraits.TInvocationsReceiver;
import org.moosetechnology.model.famix.famixtraits.TMethod;
import org.moosetechnology.model.famix.famixtraits.TNamedEntity;
import org.moosetechnology.model.famix.famixtraits.TReference;
import org.moosetechnology.model.famix.famixtraits.TSourceEntity;
import org.moosetechnology.model.famix.famixtraits.TStructuralEntity;
import org.moosetechnology.model.famix.famixtraits.TType;
import org.moosetechnology.model.famix.famixtraits.TWithAccesses;
import org.moosetechnology.model.famix.famixtraits.TWithAttributes;
import org.moosetechnology.model.famix.famixtraits.TWithComments;
import org.moosetechnology.model.famix.famixtraits.TWithInheritances;
import org.moosetechnology.model.famix.famixtraits.TWithLocalVariables;
import org.moosetechnology.model.famix.famixtraits.TWithMethods;
import org.moosetechnology.model.famix.famixtraits.TWithParameterizedTypes;
import org.moosetechnology.model.famix.famixtraits.TWithTypes;

/**
 * A {@link fr.inria.verveine.extractor.java.AbstractDictionary} specialized for Java
 * @author anquetil
 */
public class JavaDictionary {

	/**
	 * A property added to CompilationUnits to record the name of the source file they belong to.
	 * Used to create FileAnchors
	 */
	public static final String SOURCE_FILENAME_PROPERTY = "verveine-source-filename";

	public static final String DEFAULT_PCKG_NAME = "<Default Package>";
	public static final String STUB_METHOD_CONTAINER_NAME = "<StubMethodContainer>";
	public static final String SELF_NAME = "self";
	public static final String SUPER_NAME = "super";
	
	public static final String OBJECT_NAME = "Object";
	public static final String METACLASS_NAME = "Class";
	public static final String OBJECT_PACKAGE_NAME = "java.lang";
	public static final String ARRAYS_NAME = "default[]";
	public static final String INIT_BLOCK_NAME = "<Initializer>";
	public static final String ANONYMOUS_NAME_PREFIX = "_Anonymous";

	public static final int UNKNOWN_MODIFIERS = 0;
	public static final String MODIFIER_ABSTRACT = "abstract";
	public static final String MODIFIER_PUBLIC   = "public";
	public static final String MODIFIER_PRIVATE  = "private";
	public static final String MODIFIER_PROTECTED= "protected";
	public static final String MODIFIER_PACKAGE = "package";
	public static final String MODIFIER_FINAL    = "final";
	public static final String MODIFIER_STATIC    = "static";
	public static final String MODIFIER_TRANSIENT = "transient";
	public static final String MODIFIER_VOLATILE = "volatile";
	public static final String MODIFIER_SYNCHRONIZED = "synchronized";

	/**
	 * An MSE marker for methods
	 */
	public static final String CONSTRUCTOR_KIND_MARKER = "constructor";

	/**
	 * The FAMIX repository where all FAMIX entities are created and stored
	 */
	protected Repository famixRepo;

	/**
	 * A dictionary to map a key (provided by the user) to FAMIX Entity
	 */
	protected Map<IBinding,TNamedEntity> keyToEntity;
	/**
	 * A reverse dictionary (see {@link AbstractDictionary#keyToEntity}) to find the key of an entity.
	 */
	protected Map<TNamedEntity,IBinding> entityToKey;

	/**
	 * Another dictionary to map a name to FAMIX Entities with this name
	 */
	protected Map<String,Collection<TNamedEntity>> nameToEntity;

	/**
	 * Yet another dictionary for implicit variables ('self' and 'super')
	 * Because they are implicit, they may not have a binding provided by the parser,
	 * or may have the same binding than their associated type so they can't be kept easily in {@link AbstractDictionary#keyToEntity}
	 */
	@Deprecated
	protected Map<Type,ImplicitVars> typeToImpVar;

	/**
	 * Used to keep the two possible ImplicitVariable for a given Class binding
	 * @author anquetil
	 */
	@Deprecated
	protected class ImplicitVars {
		public ImplicitVariable self_iv;
		public ImplicitVariable super_iv;
	}
	
	/**
	 * Result of utility methods for checking matching between two entities
	 */
	private enum CheckResult {
		MATCH, UNDECIDED, FAIL;
	}


	/** Constructor taking a FAMIX repository
	 * @param famixRepo
	 */
	public JavaDictionary(Repository famixRepo) {
			this.famixRepo = famixRepo;
			
			this.keyToEntity = new Hashtable<IBinding,TNamedEntity>();
			this.entityToKey = new Hashtable<TNamedEntity,IBinding>();
			this.nameToEntity = new Hashtable<String,Collection<TNamedEntity>>();
			this.typeToImpVar = new Hashtable<Type,ImplicitVars>();
			
			if (! this.famixRepo.isEmpty()) {
				recoverExistingRepository();
			}
		}

  	public void mapKey(IBinding bnd, NamedEntity fmx) {
		mapEntityToKey(bnd, fmx);
	}

	/**
	 * Resets the dictionnary in a proper state after loading entities from an existing MSE file:
	 * <UL>
	 * <li>map all named entities to their names in <b>mapName</b></li>
	 * <li>reset some boolean properties (e.g. <b>isStub</b>) that are false (they are not saved in the mse file and therefore not initialized)</li>
	 * </ul>
	 */
	protected void recoverExistingRepository() {
		for (NamedEntity ent : famixRepo.all(NamedEntity.class)) {
			mapEntityToName( ent.getName(), ent);
			// for the Exception to be raised, the return value must be tested
			try { if (ent.getIsStub()) {} }
			catch (NullPointerException e) { ent.setIsStub(Boolean.FALSE); }
		}

		for (Access acc : famixRepo.all(Access.class)) {
			// for the Exception to be raised, the return value must be tested
			try { if (acc.getIsWrite()) {}	}
			catch (NullPointerException e) { acc.setIsWrite(Boolean.FALSE); }
		}
	}

	protected void mapEntityToName(String name, TNamedEntity ent) {
		Collection<TNamedEntity> l_ent = nameToEntity.get(name);
		if (l_ent == null) {
			l_ent = new LinkedList<TNamedEntity>();
		}
		l_ent.add(ent);
		nameToEntity.put(name, l_ent);
	}

	public void removeEntity( NamedEntity ent) {
		IBinding key;
		key = entityToKey.get(ent);
		entityToKey.remove(ent);
		keyToEntity.remove(key);

		Collection<TNamedEntity> l_ent = nameToEntity.get(ent.getName());
		l_ent.remove(ent);

		famixRepo.getElements().remove(ent);
	}
	
	protected void mapEntityToKey(IBinding key, TNamedEntity ent) {
		TNamedEntity old = keyToEntity.get(key);
		if (old != null) {
			entityToKey.remove(old);
		}
		keyToEntity.put(key, ent);
		entityToKey.put(ent, key);
	}
	
	/**
	 * Returns all the Famix Entity with the given name and class 
	 * @param fmxClass -- the subtype of Famix Entity we are looking for
	 * @param name -- the name of the entity
	 * @return the Collection of Famix Entities with the given name and class (possibly empty)
	 */
	@SuppressWarnings("unchecked")
	public <T extends TNamedEntity> Collection<T> getEntityByName(java.lang.Class<T> fmxClass, String name) {
		Collection<T> ret = new LinkedList<T>();
		Collection<TNamedEntity> l_name = nameToEntity.get(name);
		
		if (l_name != null ) {
			for (TNamedEntity obj : l_name) {
				if (fmxClass.isInstance(obj)) {
					ret.add((T) obj);
				}
			}
		}

		return ret;
	}

	/**
	 * Returns the Famix Entity associated to the given key.
	 * <b>Note</b>: Be careful than ImplicitVariables share the same binding than their associated Class and cannot be retrieved with this method.
	 * In such a case, this method will always retrieve the Class associated to the key.
	 * To get an ImplicitVariable from the key, use {@link AbstractDictionary#getImplicitVariableByBinding(Object, String)}
	 * @param key -- the key
	 * @return the Famix Entity associated to the binding or null if not found
	 */
	public TNamedEntity getEntityByKey(IBinding key) {
		if (key == null) {
			return null;
		}
		else {
			return keyToEntity.get(key);
		}
	}

	/**
	 * Returns the key associated to a Famix Entity.
	 * @param e -- the Named entity
	 * @return the key associated to this entity or null if none
	 */
	public IBinding getEntityKey(TNamedEntity e) {
		return entityToKey.get(e);
	}

	/**
	 * Creates and returns a FAMIX Entity of the type <b>fmxjava.lang.Class</b>.
	 * The Entity is always created (see {@link AbstractDictionary#ensureFamixEntity(Class, Object, String, boolean)}).
	 * @param fmxClass -- the FAMIX class of the instance to create
	 * @param name -- the name of the new instance must not be null (and this is not tested)
	 * @param persistIt -- whether the Entity should be persisted in the Famix repository
	 * @return the FAMIX Entity or null in case of a FAMIX error
	 */
	protected <T extends TNamedEntity & TSourceEntity> T createFamixEntity(java.lang.Class<T> fmxClass, String name, boolean persistIt) {
		T fmx = null;

		if (name == null) {
			return null;
		}
		
		try {
			fmx = fmxClass.getDeclaredConstructor().newInstance();
		} catch (java.lang.Exception e) {
			System.err.println("Unexpected error, could not create a FAMIX entity: "+e.getMessage());
			e.printStackTrace();
		}
		
		if (fmx != null) {
			fmx.setName(name);
			fmx.setIsStub(Boolean.TRUE);

			mapEntityToName(name, fmx);
			
			if (persistIt) {
				// put new entity in Famix repository
				this.famixRepo.add(fmx);
			}
		}

		return fmx;
	}
	
	/**
	 * Returns a FAMIX Entity of the type <b>fmxjava.lang.Class</b> and maps it to its binding <b>bnd</b> (if not null).
	 * The Entity is created if it did not exist.
	 * @param fmxClass -- the FAMIX class of the instance to create
	 * @param bnd -- the binding to map to the new instance
	 * @param name -- the name of the new instance (used if <tt>bnd == null</tt>)
	 * @param persistIt -- whether the Entity should be persisted in the Famix repository
	 * @return the FAMIX Entity or null if <b>bnd</b> was null or in case of a FAMIX error
	 */
	@SuppressWarnings("unchecked")
	protected <T extends TNamedEntity & TSourceEntity> T ensureFamixEntity(java.lang.Class<T> fmxClass, IBinding bnd, String name, boolean persistIt) {
		T fmx = null;
		if (bnd != null) {
			fmx = (T) getEntityByKey(bnd);
			if (fmx != null) {
				return fmx;
			}
		}
		// else
		// Unfortunately different entities with the same name and same type may exist
		// e.g. 2 parameters of 2 different methods but having the same name
		// so we cannot recover just from the name

		fmx = createFamixEntity(fmxClass, name, persistIt);
		if ( (bnd != null) && (fmx != null) ) {
			keyToEntity.put(bnd, fmx);
			entityToKey.put(fmx, bnd);
		}
		
		return fmx;
	}

	/**
	 * Adds an already created Entity to the FAMIX repository
	 * Used mainly for non-NamedEntity, for example relationships
	 * @param e -- the FAMIX entity to add to the repository
	 */
	public void famixRepoAdd(Entity e) {
		this.famixRepo.add(e);
	}


	/**
	 * Returns a FAMIX ParameterizableClass with the given <b>name</b>, creating it if it does not exist yet
	 * In the second case, sets some default properties: not Abstract, not Final, not Private, not Protected, not Public, not Interface
	 * @param name -- the name of the FAMIX Class
	 * @param persistIt -- whether the ParameterizableClass should be persisted in the Famix repository
	 * @return the FAMIX Class or null in case of a FAMIX error
	 */
	public ParameterizableClass ensureFamixParameterizableClass(IBinding key, String name, TWithTypes owner, boolean persistIt) {
		ParameterizableClass fmx = ensureFamixEntity(ParameterizableClass.class, key, name, persistIt);
		fmx.setTypeContainer(owner);
		return fmx;
	}

		/**
	 * Returns a FAMIX ParameterizableInterface with the given <b>name</b>, creating it if it does not exist yet
	 * In the second case, sets some default properties: not Abstract, not Final, not Private, not Protected, not Public, not Interface
	 * @param name -- the name of the FAMIX Class
	 * @param persistIt -- whether the ParameterizableInterface should be persisted in the Famix repository
	 * @return the FAMIX Class or null in case of a FAMIX error
	 */
	public ParameterizableInterface ensureFamixParameterizableInterface(IBinding key, String name, TWithTypes owner, boolean persistIt) {
		ParameterizableInterface fmx = ensureFamixEntity(ParameterizableInterface.class, key, name, persistIt);
		fmx.setTypeContainer(owner);
		return fmx;
	}

	public AnnotationInstanceAttribute createFamixAnnotationInstanceAttribute(AnnotationTypeAttribute att, String value) {
		AnnotationInstanceAttribute fmx = null;
		if ( (att != null) && (value != null) ) {
			fmx = new AnnotationInstanceAttribute();
			fmx.setAnnotationTypeAttribute(att);
			fmx.setValue(value);
			this.famixRepo.add(fmx);
		}
		return fmx;
	}

	public AnnotationInstance addFamixAnnotationInstance(NamedEntity fmx, AnnotationType annType, Collection<AnnotationInstanceAttribute> annAtts) {
		AnnotationInstance inst = null;
		if ( (fmx != null) && (annType != null) ) {
			inst = new AnnotationInstance();
			inst.setAnnotatedEntity(fmx);
			inst.setAnnotationType(annType);
			inst.addAttributes(annAtts);
			this.famixRepo.add(inst);
		}
		return inst;
	}

	///// ensure Famix Relationships /////

	/**
	 * Returns a Famix Inheritance relationship between two Famix Classes creating it if needed
	 * @param sup -- the super class
	 * @param sub -- the sub class
	 * @param prev -- previous inheritance relationship in the same context
	 * @return the Inheritance relationship
	 */
	public Inheritance ensureFamixInheritance(TWithInheritances sup, TWithInheritances sub, TAssociation prev) {
		if ( (sup == null) || (sub == null) ) {
			return null;
		}

		for (TInheritance i : (sup).getSubInheritances()) {
			if (i.getSubclass() == sub) {
				return (Inheritance) i;
			}
		}
		Inheritance inh = new Inheritance();
		inh.setSuperclass(sup);
		inh.setSubclass(sub);
		chainPrevNext(prev, inh);
		famixRepoAdd(inh);
		return inh;
	}

		/**
	 * Returns a Famix Implementation relationship between two Famix Classes creating it if needed
	 * @param sup -- the implemented interface
	 * @param sub -- the implementing class
	 * @param prev -- previous inheritance relationship in the same context
	 * @return the Inheritance relationship
	 */
	public Implementation ensureFamixImplementation(TImplementable myInterface, TCanImplement implementingClass, TAssociation prev) {
		if ( (myInterface == null) || (implementingClass == null) ) {
			return null;
		}

		for (TImplementation imp : myInterface.getImplementations()) {
			if (imp.getImplementingClass() == implementingClass) {
				return (Implementation) imp;
			}
		}
		Implementation implementation = new Implementation();
		implementation.setImplementingClass(implementingClass);
		implementation.setMyInterface(myInterface);
		chainPrevNext(prev, implementation);
		famixRepoAdd(implementation);
		return implementation;
	}

	protected void ensureImplementedInterfaces(ITypeBinding bnd, TType fmx, TWithTypes owner, TAssociation lastAssociation, boolean alwaysPersist) {
		for (ITypeBinding intbnd : bnd.getInterfaces()) {
			TType superTyp = this.ensureFamixInterface(intbnd, null, null, /*isGeneric*/intbnd.isParameterizedType() || intbnd.isRawType(), extractModifierOfTypeFrom(intbnd), alwaysPersist);
			if (bnd.isInterface()) {
				// in Java "subtyping" link between 2 interfaces is call inheritance 
				lastAssociation = ensureFamixInheritance((TWithInheritances)superTyp, (TWithInheritances)fmx, lastAssociation);
			}
			else {
				lastAssociation = ensureFamixImplementation((TImplementable)superTyp, (TCanImplement)fmx, lastAssociation);
			}
		}
	}

	/**
	 * Returns a Famix Reference between two Famix Entities creating it if needed.<br>
	 * If <code>prev == null</code> and a similar reference already exist (same <code>src</code>, same <code>tgt</code>), does not create a new one
	 * @param src -- source of the reference
	 * @param tgt -- target of the reference
	 * @param prev -- previous reference relationship in the same context
	 * @return the FamixReference
	 */
	public Reference addFamixReference(Method src, TType tgt, TAssociation prev) {
		if ( (src == null) || (tgt == null) ) {
			return null;
		}

		if (prev == null) {
			for (TReference ref : src.getOutgoingReferences()) {
				if (ref.getReferredType() == tgt) {
					return (Reference) ref;
				}
			}
		}

		Reference ref = new Reference();
		ref.setReferredType(tgt);
		ref.setReferencer(src);
		chainPrevNext(prev,ref);
		famixRepoAdd(ref);

		return ref;
	}

	/**
	 * Returns a Famix Invocation between two Famix Entities creating it if needed
	 * @param tMethod of the invocation
	 * @param invoked -- method invoked
	 * @param receiver of the invocation
	 * @param signature -- i.e. actual invocation code
	 * @param prev -- previous invocation relationship in the same context
	 * @return the FamixInvocation
	 */
	public Invocation addFamixInvocation(TMethod tMethod, TMethod invoked, TInvocationsReceiver receiver, String signature, TAssociation prev) {
		if ( (tMethod == null) || (invoked == null) ) {
			return null;
		}
		Invocation invok = new Invocation();
		invok.setReceiver(receiver);
		invok.setSender(tMethod);
		invok.setSignature((signature == null) ? invoked.getSignature() : signature);
		invok.addCandidates(invoked);
		chainPrevNext(prev,invok);
		famixRepoAdd(invok);
		
		return invok;
	}

	/**
	 * Returns a Famix Access between two Famix Entities creating it if needed
	 * @param accessor -- the entity (presumably a method) accessing the attribute
	 * @param var -- the variable accessed
	 * @param isWrite -- whether this is an access for reading or writing in the variable
	 * @param prev -- previous access relationship in the same context
	 * @return the FamixAccess
	 */
	public Access addFamixAccess(TWithAccesses accessor, TStructuralEntity var, boolean isWrite, TAssociation prev) {
		if ( (accessor == null) || (var == null) ) {
			return null;
		}
		Access acc = new Access();
		acc.setAccessor(accessor);
		acc.setVariable((TAccessible) var);
		acc.setIsWrite(isWrite);
		chainPrevNext(prev, acc);
		famixRepoAdd(acc);
		
		return acc;
	}

	protected void chainPrevNext(TAssociation prev, TAssociation next) {
		if (prev != null) {
			next.setPrevious(prev);  // not yet implemented in importer
		}
	}
	
	/**
	 * Returns a Famix DeclaredException between a method and an Exception that it declares to throw
	 * @param meth -- the method throwing the exception
	 * @param excep -- the exception declared to be thrown
	 * @return the DeclaredException
	 */
	public Exception createFamixDeclaredException(Method meth, Exception excep) {
		if ( (meth == null) || (excep == null) ) {
			return null;
		}
		//org.moosetechnology.model.famixjava.famixjavaentities.Exception decl = new org.moosetechnology.model.famixjava.famixjavaentities.Exception();
		// decl.setExceptionClass(excep);
		// excep.getDeclaringEntities().add(meth);
		// famixRepoAdd(excep);
		meth.getDeclaredExceptions().add(excep);
		return excep;
	}

	/**
	 * Returns a Famix CaughtException between a method and an Exception that is caught
	 * @param meth -- the method catching the exception
	 * @param excep -- the exception caught
	 * @return the CaughtException
	 */
	public Exception createFamixCaughtException(Method meth, Exception excep) {
		if ( (meth == null) || (excep == null) ) {
			return null;
		}
		// CaughtException decl = new CaughtException();
		// decl.setExceptionClass(excep);
		// decl.setDefiningEntity(meth);
		// famixRepoAdd(decl);
		meth.getCaughtExceptions().add(excep);
		return excep;
	}

	/**
	 * Returns a Famix ThrownException between a method and an Exception that it (actually) throws.
	 * Note: DeclaredException indicates that the method declares it can throw the exception,
	 * here we state that the exception is actually thrown
	 * @param meth -- the method throwing the exception
	 * @param excep -- the exception thrown
	 * @return the ThrownException
	 */
	public Exception createFamixThrownException(Method meth, Exception excep) {
		if ( (meth == null) || (excep == null) ) {
			return null;
		}
		// ThrownException decl = new ThrownException();
		// decl.setExceptionClass(excep);
		// decl.setDefiningEntity(meth);
		// famixRepoAdd(decl);
		meth.getThrownExceptions().add(excep);
		return excep;
	}

	///// Special Case: ImplicitVariables /////

	/**
	 * Returns the Famix ImplicitVariable associated to the given binding and name (self or super).
	 * See also {@link AbstractDictionary#getEntityByKey(Object)}
	 * @param bnd -- the binding
	 * @return the Famix Entity associated to the binding or null if not found
	 */
	@Deprecated
	public ImplicitVariable getImplicitVariableByBinding(IBinding bnd, String iv_name) {
		return getImplicitVariableByType((Class)getEntityByKey(bnd), iv_name);
	}
	
	/**
	 * Returns the Famix ImplicitVariable associated to the given FamixType.
	 * @param type -- the FamixType
	 * @param name -- name of the ImplicitVariable (should be Dictionary.SELF_NAME or Dictionary.SUPER_NAME)
	 * @return the Famix ImplicitVariable associated to the Type or null if not found
	 */
	@Deprecated
	public ImplicitVariable getImplicitVariableByType(Type type, String name) {
		ImplicitVars iv = typeToImpVar.get(type);
		ImplicitVariable ret = null;
		
		if (iv == null) {
			iv = new ImplicitVars();
		}
		
		if (name.equals(SELF_NAME)) {
			ret = iv.self_iv;
		}
		else if (name.equals(SUPER_NAME)) {
			ret = iv.super_iv;
		}

		return ret;
	}

	///// Special Case: "Uniq" Entities /////

	/**
	 * Creates or recovers a Famix Named Entity uniq for the given name.
	 * For some specific entities we don't allow two of them with the same name.
	 * This is the case e.g. for the default package, or the Java class "Object" and its package "java.lang".
	 * @param fmxClass -- the FAMIX class of the instance to create
	 * @param key -- a potential binding for the entity
	 * @param name -- the name of the new instance (used if <tt>bnd == null</tt>)
	 * @return the uniq Famix Entity for this binding and/or name
	 */
	@SuppressWarnings("unchecked")
	public <T extends NamedEntity> T ensureFamixUniqEntity(java.lang.Class<T> fmxClass, IBinding key, String name) {
		T fmx = null;
		
		if (name == null) {
			return null;
		}
		
		if (key != null) {
			fmx = (T) getEntityByKey(key);
		}
		
		if (fmx == null) {
			Collection<T> l = getEntityByName( fmxClass, name);
			if (l.size() > 0) {
				fmx = l.iterator().next();
			}
			else {
				// may be we should be careful not to persist all these special entities?
				fmx = createFamixEntity(fmxClass, name, /*persistIt*/true);
			}
			
			if (key != null) {
				// may happen for example if the entity was first created without binding
				// and we find a binding for it later
				keyToEntity.put(key, fmx);
			}
		}

		return fmx;
	}

	/**
	 * Creates or recovers the Famix Class that will own all stub methods (for which the real owner is unknown)
	 *
	 * @return a Famix class
	 */
	public Class ensureFamixClassStubOwner() {
		Class fmx =  ensureFamixUniqEntity(Class.class, null, STUB_METHOD_CONTAINER_NAME);
		if (fmx != null) {
			fmx.setTypeContainer( ensureFamixPackageDefault());
		}
		ensureFamixInheritance(ensureFamixClassObject(null), fmx, /*prev*/null);

		return fmx;
	}

	public Type searchTypeInContext(String name, TWithTypes ctxt) {
		if (ctxt == null) {
			return null;
		}
		
		for (TType candidate : ctxt.getTypes()) {
			if (((TNamedEntity)candidate).getName().equals(name) ) {
				return (Type) candidate;
			}
		}
		
		return searchTypeInContext(name, Util.getOwner((TNamedEntity)ctxt));
	}

	/**
	 * Returns a Famix Package associated with its IPackageBinding and/or fully qualified name.
	 * The Entity is created if it does not exist.
	 * We assume that Namespaces must be uniq for a given name
	 * Also creates or recovers recusively it's parent namespaces.<br>
	 * At least one of <b>bnd</b> and <b>name</b> must be non null.
	 *
	 * @param bnd  -- the JDT Binding that may be used as a uniq key to recover this namespace
	 * @param name -- fully qualified name of the namespace (e.g. 'java.lang')
	 * @return the Famix Namespace found or created. May return null in case of a Famix error
	 */
	public Package ensureFamixPackage(IPackageBinding bnd, String name) {
		Package fmx = null;
		Package parent = null;

		if ((name == null) && (bnd != null)) {
			name = bnd.getName();
		}

		if ((name == null) || name.equals("")) {
			return ensureFamixPackageDefault();
		} else {
			/* Note: Packages are created with their fully-qualified name to simplify recovering when we don't have a binding
			 * (for example when creating parent packages of a package we have a binding for).
			 * Because the preferred solution in Moose is to give their simple names to packages, they must be post-processed when
			 * all is said and done. */
			fmx = ensureFamixUniqEntity(Package.class, bnd, name);
			String parentName = removeLastName(name);
			if (parentName.length() > 0) {
				parent = ensureFamixPackage(null, parentName);
				// set the parentscope relationship
				if ((parent != null) && (fmx != null) && (fmx.getParentPackage() == null)) {
					parent.addChildEntities(fmx);
				}
			}
		}

		return fmx;
	}

	/**
	 * Creates or recovers a default Famix Package.
	 * Because this package does not really exist, it has no binding.
	 *
	 * @return a Famix Namespace
	 */
	public Package ensureFamixPackageDefault() {
		Package fmx = ensureFamixUniqEntity(Package.class, null, DEFAULT_PCKG_NAME);

		return fmx;
	}

	/**
	 * Creates or recovers a Famix Package for the package of Java class "Object" (i.e. "java.lang").
	 * Because "Object" is the root of the inheritance tree, it needs to be treated differently.
	 *
	 * @param bnd -- a potential binding for the "java.lang" package
	 * @return a Famix Namespace for "java.lang"
	 */
	public Package ensureFamixPackageJavaLang(IPackageBinding bnd) {
		Package fmx = this.ensureFamixPackage(bnd, OBJECT_PACKAGE_NAME);

		return fmx;
	}

	/**
	 * Returns the Package with {@link AbstractDictionary#DEFAULT_PCKG_NAME} or <code>null</code> if not found
	 */
	public Package getFamixPackageDefault() {
		Collection<Package> l = getEntityByName(Package.class, DEFAULT_PCKG_NAME);
		if (l.size() > 0) {
			return l.iterator().next();
		} else {
			return null;
		}
	}

	/**
	 * Returns a FAMIX Type with the given <b>name</b>, creating it if it does not exist yet.
	 * In the second case, sets some default properties: not Abstract, not Final, not Private, not Protected, not Public, not Interface
	 * @param bnd -- binding for the type to create
	 * @param name of the type
	 * @param owner of the type
	 * @param ctxt -- context of use of the type
	 * @param alwaysPersist -- whether the type is unconditionally persisted or if we should check
	 */
	public TType ensureFamixType(ITypeBinding bnd, String name, TWithTypes owner, TWithTypes ctxt, int modifiers, boolean alwaysPersist) {
		TType fmx = null;

		if (bnd == null) {
			if (name == null) {
				return null;
			}
			fmx = searchTypeInContext(name, ctxt); // WildCard Types don't have binding
			if (fmx != null) {
				return fmx;
			}

			if ( (owner != null) && (owner instanceof TWithParameterizedTypes) ) {
				return this.ensureFamixParameterType(null, name, (TWithParameterizedTypes) owner, alwaysPersist);
			}
			else {
				// impossible to decide whether to persist it or not. So we just hope for the best
				fmx = ensureFamixEntity(Type.class, bnd, name, alwaysPersist);
				fmx.setTypeContainer(owner);
				return fmx;
			}
		}

		// bnd != null

		fmx = (TType) getEntityByKey(bnd);
		if (fmx != null) {
			return fmx;
		}

		if (bnd.isArray()) {
			bnd = bnd.getElementType();
		}

		if (bnd.isPrimitive()) {
			return this.ensureFamixPrimitiveType(bnd, name);
		}

		if (bnd.isEnum()) {
			return this.ensureFamixEnum(bnd, name, (ContainerEntity) owner);
		}

		if ((bnd.isRawType() || bnd.isGenericType()) && !bnd.isInterface() ) {
			return this.ensureFamixClass(bnd.getErasure(), name, (TNamedEntity) owner, /*isGeneric*/true, modifiers, alwaysPersist);
		}
		
		if (bnd.isAnnotation()) {
			return this.ensureFamixAnnotationType(bnd, name, (ContainerEntity) owner, alwaysPersist);
		}

		if (bnd.isInterface()) {
			return this.ensureFamixInterface(bnd, name, owner, /*isGeneric*/bnd.isParameterizedType() || bnd.isRawType(), modifiers, alwaysPersist);
		}

		if (bnd.isParameterizedType() || bnd.isRawType()) {
			return this.ensureFamixParameterizedType(bnd, name, /*generic*/null, ctxt, alwaysPersist);
		}
		if (isThrowable(bnd)) {
			return this.ensureFamixException(bnd, name, owner, /*isGeneric*/false, modifiers, alwaysPersist);
		}
		if (bnd.isClass()) {
			return this.ensureFamixClass(bnd, name, (TNamedEntity) owner, /*isGeneric*/false, modifiers, alwaysPersist);
		}

		//otherwise (none of the above)

		if (name == null) {
			name = bnd.getName();
		}

		if (owner == null) {
			owner = (TWithTypes) this.ensureOwner(bnd, alwaysPersist);
		}

		if (bnd.isTypeVariable() ) {
			if (owner instanceof ParameterizableClass) {
				fmx = ensureFamixParameterType(bnd, name, (TWithParameterizedTypes) owner, alwaysPersist);
			}
			else {
				// a type defined for a method parameter or return type
				fmx = ensureFamixEntity(Type.class, bnd, name, alwaysPersist);
				fmx.setTypeContainer(owner);
			}
			return fmx;
		}

		fmx = ensureFamixEntity(Type.class, bnd, name, alwaysPersist);
		fmx.setTypeContainer(owner);
		return fmx;
	}

	public TType ensureFamixType(ITypeBinding bnd, TWithTypes context, boolean alwaysPersist) {
		int modifiers = extractModifierOfTypeFrom(bnd);
		return ensureFamixType(bnd, /*name*/null, /*owner*/null, context, modifiers, alwaysPersist);
	}
	
	public TType ensureFamixType(ITypeBinding bnd, boolean alwaysPersist) {
		return ensureFamixType(bnd, /*ctxt*/null, alwaysPersist);
	}

	private int extractModifierOfTypeFrom(ITypeBinding bnd) {
		int modifiers = (bnd != null) ? bnd.getModifiers() : UNKNOWN_MODIFIERS;
		return modifiers;
	}

	public boolean isThrowable(ITypeBinding bnd) {
		if (bnd == null) {
			return false;
		}
		if (bnd.getQualifiedName().equals("java.lang.Throwable")) {
			return true;
		} else if (bnd.getQualifiedName().equals("java.lang.Object")) {
			return false;
		}
		else {
			return isThrowable(bnd.getSuperclass());
		}
	}

	/**
	 * Returns a Famix Class associated with the ITypeBinding.
	 * The Entity is created if it does not exist.
	 * @param key to which the entity will be mapped (may be null, but then it will be difficult to recover the entity)
	 * @param name -- the name of the FAMIX Method (MUST NOT be null, but this is not checked)
	 * @param owner -- type defining the method (should not be null, but it will work if it is) 
	 * @param persistIt -- whether the Class should be persisted in the Famix repository
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	@SuppressWarnings("deprecation")
	public Class ensureFamixClass(ITypeBinding bnd, String name, TNamedEntity owner, boolean isGeneric, int modifiers, boolean alwaysPersist) {
		Class fmx = null;

		// --------------- some special cases
		if (bnd != null) {
			if (bnd.isArray()) {
				bnd = bnd.getElementType();
			}

			// for inner classes defined in generics !!! For others should not change anything
			bnd = bnd.getErasure();
		}

		// ---------------- to avoid useless computations if we can
		fmx = (Class) getEntityByKey(bnd);
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			if (bnd == null) {
				return null;  // not much we can do
			} else if (!bnd.isAnonymous()) {
				name = bnd.getErasure().getName();  // for generics, will give the "core" type name, for normal type, won't change anything
			} else { // anonymous class
				if (bnd.getSuperclass() != null) {
					name = bnd.getSuperclass().getName();
				}
				if ((name == null) || name.equals(OBJECT_NAME)) {
					ITypeBinding[] intfcs = bnd.getInterfaces();
					if ((intfcs != null) && (intfcs.length > 0)) {
						name = bnd.getInterfaces()[0].getName();
					}
					else {
						name = "???";
					}
				}
				name = ANONYMOUS_NAME_PREFIX + "(" + name + ")";
			}
		}

		if (name.equals(OBJECT_NAME)) { // TODO && owner == java.lang
			return ensureFamixClassObject(bnd);
		}

		// --------------- owner
		if (owner == null) {
			if (bnd != null) {
				owner = ensureOwner(bnd, alwaysPersist);
			}
			/*				owner = ensureFamixPackageDefault();
			} else {*/
		}

		// --------------- recover from name ?
		if (owner != null) {
			for (Class candidate : this.getEntityByName(Class.class, name)) {
				if (matchAndMapClass(bnd, name, (ContainerEntity) owner, candidate)) {
					fmx = candidate;
					break;
				}
			}
		}

		// ---------------- create
		boolean persistIt = alwaysPersist || (! (owner instanceof Method));
		if (fmx == null) {
			if (isGeneric) {
				fmx = ensureFamixParameterizableClass(bnd, name, (ContainerEntity) owner, persistIt);
			}
			else {
				fmx = ensureFamixEntity(Class.class, bnd, name, persistIt);
				fmx.setTypeContainer((ContainerEntity)owner);
			}
		}

		if (fmx!=null) {
			// we just created it or it was not bound, so we make sure it has the right information in it
			if (bnd != null) {
				setClassModifiers(fmx, bnd.getDeclaredModifiers());
			}
			if (persistIt) {
				TAssociation lastAssoc = null;
				Collection<Type> sups = new LinkedList<Type>();
				if (bnd != null) {
					ITypeBinding supbnd = bnd.getSuperclass();
					if (supbnd != null) {
						lastAssoc = ensureFamixInheritance((TWithInheritances) ensureFamixType(supbnd, alwaysPersist), fmx, lastAssoc);
					}
					else {
						lastAssoc = ensureFamixInheritance((TWithInheritances) ensureFamixClassObject(null), fmx, lastAssoc);
					}
					ensureImplementedInterfaces(bnd, fmx, (ContainerEntity) owner, lastAssoc, alwaysPersist);
				}
			}
		}

		return fmx;
	}

	/**
	 * Returns a Famix Exception associated with the ITypeBinding.
	 * The Entity is created if it does not exist.
	 *
	 * @param key to which the entity will be mapped (may be null, but then it will be difficult to recover the entity)
	 * @param name -- the name of the FAMIX Method (MUST NOT be null, but this is not checked)
	 * @param owner -- type defining the method (should not be null, but it will work if it is) 
	 * @param alwaysPersist -- whether the type is unconditionally persisted or if we should check
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	@SuppressWarnings("deprecation")
	public <T extends TWithTypes & TNamedEntity> Exception ensureFamixException(ITypeBinding bnd, String name, TWithTypes owner, boolean isGeneric, int modifiers, boolean alwaysPersist) {
		Exception fmx = null;

		// --------------- some special cases
		if (bnd != null) {
			if (bnd.isArray()) {
				bnd = bnd.getElementType();
			}

			// for inner classes defined in generics !!! For others should not change anything
			bnd = bnd.getErasure();
		}

		// ---------------- to avoid useless computations if we can
		fmx = (Exception) getEntityByKey(bnd);
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			if (bnd == null) {
				return null;  // not much we can do
			} else if (!bnd.isAnonymous()) {
				name = bnd.getErasure().getName();  // for generics, will give the "core" type name, for normal type, won't change anything
			} else { // anonymous class
				if (bnd.getSuperclass() != null) {
					name = bnd.getSuperclass().getName();
				}
				if ((name == null) || name.equals(OBJECT_NAME)) {
					ITypeBinding[] intfcs = bnd.getInterfaces();
					if ((intfcs != null) && (intfcs.length > 0)) {
						name = bnd.getInterfaces()[0].getName();
					}
					else {
						name = "???";
					}
				}
				name = ANONYMOUS_NAME_PREFIX + "(" + name + ")";
			}
		}

		// --------------- owner
		if (owner == null) {
			if (bnd == null) {
				owner = ensureFamixPackageDefault();
			} else {
				owner = (TWithTypes) ensureOwner(bnd, alwaysPersist);
			}
		}

		// --------------- recover from name ?
		for (Exception candidate : this.getEntityByName(Exception.class, name)) {
			if (matchAndMapClass(bnd, name, (T) owner, candidate)) {
				fmx = candidate;
				break;
			}
		}

		// ---------------- create
		boolean persistIt = alwaysPersist || (! (owner instanceof Method));
		if (fmx == null) {
			fmx = ensureFamixEntity(Exception.class, bnd, name, /*alwaysPersist?*/persistIt);
			fmx.setTypeContainer(owner);
		}

		if (fmx!=null) {
			// we just created it or it was not bound, so we make sure it has the right information in it
			if (bnd != null) {
				setClassModifiers(fmx, bnd.getDeclaredModifiers());
			}
			if (persistIt) {
				TAssociation lastAssoc = null;
				Collection<Type> sups = new LinkedList<Type>();
				if (bnd != null) {
					ITypeBinding supbnd = bnd.getSuperclass();
					if (supbnd != null) {
						lastAssoc = ensureFamixInheritance((TWithInheritances) ensureFamixType(supbnd, alwaysPersist), fmx, lastAssoc);
					}
					else {
						lastAssoc = ensureFamixInheritance((TWithInheritances) ensureFamixClassObject(null), fmx, lastAssoc);
					}
					ensureImplementedInterfaces(bnd, fmx, owner, lastAssoc, alwaysPersist);
				}
			}
		}

		return fmx;
	}

	/**
	 * Returns a FAMIX Interface with the given <b>name</b>, creating it if it does not exist yet.
	 * @param key to which the entity will be mapped (may be null, but then it will be difficult to recover the entity)
	 * @param name -- the name of the FAMIX Method (MUST NOT be null, but this is not checked)
	 * @param owner -- type defining the method (should not be null, but it will work if it is) 
	 * @param persistIt -- whether the Class should be persisted in the Famix repository
	 * @return the FAMIX Class or null in case of a FAMIX error
	 */
	public <T extends TWithTypes & TNamedEntity> Interface ensureFamixInterface(ITypeBinding bnd, String name, TWithTypes owner, boolean isGeneric, int modifiers, boolean alwaysPersist) {
		Interface fmx = null;

		// --------------- some special cases
		if (bnd != null) {
			if (bnd.isArray()) {
				bnd = bnd.getElementType();
			}

			// for inner classes defined in generics !!! For others should not change anything
			bnd = bnd.getErasure();
		}

		// ---------------- to avoid useless computations if we can
		fmx = (Interface) getEntityByKey(bnd);
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			if (bnd == null) {
				return null;  // not much we can do
			} else if (!bnd.isAnonymous()) {
				name = bnd.getErasure().getName();  // for generics, will give the "core" type name, for normal type, won't change anything
			} else { // anonymous class
				if (bnd.getSuperclass() != null) {
					name = bnd.getSuperclass().getName();
				}
				if ((name == null) || name.equals(OBJECT_NAME)) {
					ITypeBinding[] intfcs = bnd.getInterfaces();
					if ((intfcs != null) && (intfcs.length > 0)) {
						name = bnd.getInterfaces()[0].getName();
					}
					else {
						name = "???";
					}
				}
				name = ANONYMOUS_NAME_PREFIX + "(" + name + ")";
			}
		}

		// --------------- owner
		if (owner == null) {
			if (bnd == null) {
				owner = ensureFamixPackageDefault();
			} else {
				owner = (TWithTypes) ensureOwner(bnd, alwaysPersist);
			}
		}

		// --------------- recover from name ?
		for (Interface candidate : this.getEntityByName(Interface.class, name)) {
			if (matchAndMapInterface(bnd, name, (T) owner, candidate)) {
				fmx = candidate;
				break;
			}
		}

		// ---------------- create
		boolean persistIt = alwaysPersist || (! (owner instanceof Method));
		if (fmx == null) {
			if (isGeneric) {
				fmx = ensureFamixParameterizableInterface(bnd, name, owner, persistIt);
			}
			else {
				fmx = ensureFamixEntity(Interface.class, bnd, name, persistIt);
				fmx.setTypeContainer(owner);
			}
		}

		if (fmx!=null) {
			// we just created it or it was not bound, so we make sure it has the right information in it
			if (bnd != null) {
				setClassModifiers(fmx, bnd.getDeclaredModifiers());
			}
			if (persistIt) {
				TAssociation lastAssociation = null;
				Collection<Type> sups = new LinkedList<Type>();
				if (bnd != null) {
					ensureImplementedInterfaces(bnd, fmx, owner, lastAssociation, alwaysPersist);
				}
			}
		}
		return fmx;
	}

	public TType asClass(TType excepFmx) {
		Class tmp = null;
		IBinding key = null;
		try {
			TWithTypes owner = (TWithTypes) Util.getOwner(excepFmx);
			owner.getTypes().remove(excepFmx);
			removeEntity((NamedEntity) excepFmx);

			key = entityToKey.get((NamedEntity) excepFmx);
			tmp = ensureFamixEntity(Class.class, key, excepFmx.getName(), /*alwaysPersist?*/true);
			tmp.setTypeContainer((ContainerEntity)owner);

			tmp.addMethods(((TWithMethods) excepFmx).getMethods());
			if (excepFmx instanceof TWithAttributes) {
				tmp.addAttributes(((TWithAttributes) excepFmx).getAttributes());
			}
			//tmp.addModifiers(excepFmx.getModifiers());

			if (key != null) {
				setClassModifiers(tmp, key.getModifiers());
			}

			if (excepFmx instanceof TWithInheritances) {
				tmp.addSuperInheritances(((TWithInheritances) excepFmx).getSuperInheritances());
				tmp.addSubInheritances(((TWithInheritances) excepFmx).getSubInheritances());
			}
			tmp.setSourceAnchor(excepFmx.getSourceAnchor());
			tmp.addAnnotationInstances(((NamedEntity) excepFmx).getAnnotationInstances());
			// tmp.addComments(excepFmx.getComments());
			tmp.addIncomingReferences(excepFmx.getIncomingReferences());
			tmp.setIsStub(excepFmx.getIsStub());
			tmp.addTypes(((ContainerEntity) excepFmx).getTypes());
		}
		catch( ConcurrentModificationException e) {
			e.printStackTrace();
		}

		return tmp;
	}

	public Exception asException(TType excepFmx) {
		if (excepFmx instanceof Exception) {
			return (Exception) excepFmx;
		};
		Exception tmp = null;
		IBinding key = null;
		try {
			TWithTypes owner = (TWithTypes) Util.getOwner(excepFmx);
			owner.getTypes().remove(excepFmx);
			removeEntity((NamedEntity) excepFmx);

			key = entityToKey.get((NamedEntity) excepFmx);
			tmp = ensureFamixException((ITypeBinding) key, excepFmx.getName(), owner, /*isGeneric*/false, UNKNOWN_MODIFIERS, /*alwaysPersist?*/true);

			tmp.addMethods(((TWithMethods) excepFmx).getMethods());
			if (excepFmx instanceof TWithAttributes) {
				tmp.addAttributes(((TWithAttributes) excepFmx).getAttributes());
			}
			//tmp.addModifiers(excepFmx.getModifiers());

			if (key != null) {
				setClassModifiers(tmp, key.getModifiers());
			}

			if (excepFmx instanceof TWithInheritances) {
				tmp.addSuperInheritances(((TWithInheritances) excepFmx).getSuperInheritances());
				tmp.addSubInheritances(((TWithInheritances) excepFmx).getSubInheritances());
			}
			tmp.setSourceAnchor(excepFmx.getSourceAnchor());
			tmp.addAnnotationInstances(((NamedEntity) excepFmx).getAnnotationInstances());
			// tmp.addComments(excepFmx.getComments());
			tmp.addIncomingReferences(excepFmx.getIncomingReferences());
			tmp.setIsStub(excepFmx.getIsStub());
			tmp.addTypes(((ContainerEntity) excepFmx).getTypes());
		}
		catch( ConcurrentModificationException e) {
			e.printStackTrace();
		}

		return tmp;
	}

	/**
	 * helper method, we know the type exists, ensureFamixClass will recover it
	 */
	public Class getFamixClass(ITypeBinding bnd, String name, ContainerEntity owner) {
		return ensureFamixClass(bnd, name, owner, /*isGeneric*/false, UNKNOWN_MODIFIERS, /*alwaysPersist*/false);
	}

	/**
	 * helper method, we know the type exists, ensureFamixInterface will recover it
	 */
	public Interface getFamixInterface(ITypeBinding bnd, String name, ContainerEntity owner) {
		return ensureFamixInterface(bnd, name, owner, /*isGeneric*/false, UNKNOWN_MODIFIERS, /*alwaysPersist*/false);
	}

	/**
	 * helper method, we know the type exists, ensureFamixInterface will recover it
	 */
	public Exception getFamixException(ITypeBinding bnd, String name, ContainerEntity owner) {
		return ensureFamixException(bnd, name, owner, /*isGeneric*/false, UNKNOWN_MODIFIERS, /*alwaysPersist*/false);
	}

	/**
	 * Ensures a famix entity for the owner of a binding.<br>
	 * This owner can be a method, a class or a namespace
	 * @param bnd -- binding for the owned entity
	 * @param persistIt  -- whether to persist or not the type
	 * @return a famix entity for the owner
	 */
	private TNamedEntity ensureOwner(ITypeBinding bnd, boolean persistIt) {
		TNamedEntity owner = null;
		IMethodBinding parentMtd = bnd.getDeclaringMethod();
		if (parentMtd != null) {
			owner = this.ensureFamixMethod(parentMtd, persistIt);  // cast needed to desambiguate the call
		}
		else {
			ITypeBinding parentClass = bnd.getDeclaringClass();
			if (parentClass != null) {
				TType tmpOwn = this.ensureFamixType(parentClass, /*alwaysPersist?*/persistIt);
				if (tmpOwn instanceof ParameterizedType) {
					owner =  (TNamedEntity) ((ParameterizedType) tmpOwn).getParameterizableClass();
				}
				else {
					owner = tmpOwn;
				}
			}
			else {
				IPackageBinding parentPckg = bnd.getPackage();
				if (parentPckg != null) {
					owner = this.ensureFamixPackage(parentPckg, null);
				} else {
					owner = this.ensureFamixPackageDefault();
				}
			}
		}
		return owner;
	}


	/**
	 * Returns a FAMIX ParameterizableType with the given <b>name</b>, creating it if it does not exist yet
	 * @param name -- the name of the FAMIX Type
	 * @param persistIt -- whether the ParameterizableClass should be persisted in the Famix repository
	 * @return the FAMIX ParameterizableType or null in case of a FAMIX error
	 */
	public <T extends TWithTypes & TNamedEntity> ParameterizedType ensureFamixParameterizedType(ITypeBinding bnd, String name, TWithParameterizedTypes generic, TWithTypes owner, boolean alwaysPersist) {
		ParameterizedType fmx = null;

		// --------------- to avoid useless computations if we can
		fmx = (ParameterizedType)getEntityByKey(bnd);
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			if (bnd == null) {
				return null;
			}
			else {
				name = bnd.getName();
			}
			// remove parameter types from name
			// could also use "bnd.getErasure().getName()"
			int i = name.indexOf('<');
			if (i > 0) {
				name = name.substring(0, i);
			}
		}

		// --------------- generic
		if ((generic == null) && (bnd != null)) {
			int modifiers = (bnd.getErasure() != null) ? bnd.getErasure().getModifiers() : UNKNOWN_MODIFIERS;
			if(bnd.isInterface()) {
				generic = (ParameterizableInterface) ensureFamixInterface(bnd.getErasure(), name, /*owner*/null, /*isGeneric*/true, modifiers, alwaysPersist);
			} else {
				generic = (ParameterizableClass) ensureFamixClass(bnd.getErasure(), name, /*owner*/null, /*isGeneric*/true, modifiers, alwaysPersist);
			}
		}

		// --------------- owner
		owner = ((Type) generic).getTypeContainer();
		/* Old behavior, see issue 868
		   if (owner == null) {
			if (bnd == null) {
				owner = ensureFamixNamespaceDefault();  // not really sure what to do here
			}
			else {
				owner = ensureOwner(bnd, alwaysPersist);
			}
		}*/

		// --------------- recover from name ?
		for (ParameterizedType candidate : getEntityByName(ParameterizedType.class, name) ) {
			if ( matchAndMapType(bnd, name, (T) owner, candidate) ) {
				fmx = candidate;
				break;
			}
		}

		// --------------- create
		boolean persistIt = alwaysPersist || (! (owner instanceof Method));
		if (fmx == null) {
			fmx = ensureFamixEntity(ParameterizedType.class, bnd, name, persistIt);
			fmx.setTypeContainer(owner);
			fmx.setParameterizableClass(generic);
		}

		// --------------- stub: same as ParameterizableClass
		if ( (generic != null) && (fmx != null) ) {
			fmx.setIsStub(((Type)generic).getIsStub());
		}

		return fmx;
	}

	/**
	 * Returns a FAMIX PrimitiveType with the given <b>name</b>, creating it if it does not exist yet
	 * We assume that PrimitiveType must be uniq for a given name
	 * @param name -- the name of the FAMIX PrimitiveType
	 * @return the FAMIX PrimitiveType or null in case of a FAMIX error
	 */
	public PrimitiveType ensureFamixPrimitiveType(ITypeBinding bnd, String name) {
		if (name == null) {
			if (bnd == null) {
				return null;
			} else {
				name = bnd.getName();
			}
		}
		return ensureFamixUniqEntity(PrimitiveType.class, bnd, name);
	}

	public <T extends TWithTypes & TNamedEntity> org.moosetechnology.model.famix.famixjavaentities.Enum ensureFamixEnum(ITypeBinding bnd, String name, TWithTypes owner) {
		org.moosetechnology.model.famix.famixjavaentities.Enum fmx = null;

		// --------------- to avoid useless computations if we can
		fmx = (org.moosetechnology.model.famix.famixjavaentities.Enum) getEntityByKey(bnd);
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			if (bnd == null) {
				return null;
			}
			else {
				name = bnd.getName();
			}
		}

		// --------------- owner
		if (owner == null) {
			if (bnd == null) {
				owner = ensureFamixPackageDefault();  // not really sure what to do here
			} else {
				owner = (TWithTypes) ensureOwner(bnd, /*persistIt*/true); // owner should be a class or package so yes persist it
			}
		}

		// --------------- recover from name ?
		for (org.moosetechnology.model.famix.famixjavaentities.Enum candidate : getEntityByName(org.moosetechnology.model.famix.famixjavaentities.Enum.class, name)) {
			if (matchAndMapType(bnd, name, (T) owner, candidate)) {
				fmx = candidate;
				break;
			}
		}

		if (fmx == null) {
			fmx = ensureFamixEntity(Enum.class, bnd, name, /*persistIt*/true);
			fmx.setTypeContainer(owner);
		}

		if ((fmx != null) && (bnd != null) ) {
			setVisibility(fmx, bnd.getModifiers());
		}

		return fmx;
	}

	/**
	 * helper method, we know the type exists, ensureFamixEnum will recover it
	 */
	public org.moosetechnology.model.famix.famixjavaentities.Enum getFamixEnum(ITypeBinding bnd, String name, TWithTypes owner) {
		return ensureFamixEnum(bnd, name, owner);
	}

	public EnumValue ensureFamixEnumValue(IVariableBinding bnd,	String name, Enum owner, boolean persistIt) {
		EnumValue fmx = null;

		// --------------- to avoid useless computations if we can
		fmx = (EnumValue)getEntityByKey(bnd);
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			if (bnd == null) {
				return null;
			}
			else {
				name = bnd.getName();
			}
		}

		// --------------- owner
		if (owner == null) {
			if (bnd == null) {
				return null;  // what would be the interest of creating an EnumValue without a declaring Enum type?
			}
			else {
				owner = ensureFamixEnum(bnd.getDeclaringClass(), null, null);
			}
		}

		// --------------- recover from name ?
		for (EnumValue candidate : getEntityByName(EnumValue.class, name) ) {
			if ( matchAndMapVariable(bnd, name, owner, candidate) ) {
				fmx = candidate;
				break;
			}
		}
		if (fmx == null) {
			fmx = ensureFamixEntity(EnumValue.class, bnd, name, persistIt);
			fmx.setParentEnum(owner);
		}

		if (fmx!=null) {
			fmx.setParentEnum(owner);
		}

		return fmx;
	}

	/**
	 * helper method, we know the type enumValue, ensureFamixEnumValue will recover it
	 */
	public EnumValue getFamixEnumValue(IVariableBinding bnd, String name, Enum owner) {
		return ensureFamixEnumValue(bnd, name, owner, /*persistIt*/false);
	}

	/**
	 * e.g. see {@link JavaDictionary#ensureFamixClass}
	 */
	public AnnotationType ensureFamixAnnotationType(ITypeBinding bnd, String name, ContainerEntity owner, boolean alwaysPersist) {
		AnnotationType fmx = null;

		// --------------- to avoid useless computations if we can
		fmx = (AnnotationType)getEntityByKey(bnd);
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			if (bnd == null) {
				return null;
			}
			else {
				name = bnd.getName();
			}
		}

		// --------------- owner
		if (owner == null) {
			if (bnd == null) {
				owner = ensureFamixPackageDefault();
			}
			else {
				IPackageBinding parentPckg = bnd.getPackage();
				if (parentPckg != null) {
					owner = this.ensureFamixPackage(parentPckg, null);
				} else {
					owner = this.ensureFamixPackageDefault();
				}
			}
		}

		// --------------- recover from name ?
		for (AnnotationType candidate : getEntityByName(AnnotationType.class, name) ) {
			if ( matchAndMapType(bnd, name, owner, candidate) ) {
				fmx = candidate;
				break;
			}
		}

		// --------------- create
		boolean persistIt = alwaysPersist || (! (owner instanceof Method));
		if (fmx == null) {
			fmx = ensureFamixEntity(AnnotationType.class, bnd, name, persistIt);
			fmx.setAnnotationTypesContainer(owner);
		}

		if ( (fmx!=null) && (bnd != null) ) {
			// Not supported in Famix

			// setVisibility(fmx, bnd.getModifiers());
		}

		return fmx;
	}

	/**
	 * helper method, we know the type exists, ensureFamixAnnotationType will recover it
	 */
	public AnnotationType getFamixAnnotationType(ITypeBinding bnd, String name, ContainerEntity owner) {
		return ensureFamixAnnotationType(bnd, name, owner, /*alwaysPersist*/false);
	}

	public AnnotationTypeAttribute ensureFamixAnnotationTypeAttribute(IMethodBinding bnd, String name, AnnotationType owner, boolean persistIt) {
		AnnotationTypeAttribute fmx = null;

		// --------------- to avoid useless computations if we can
		fmx = (AnnotationTypeAttribute)getEntityByKey(bnd);
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			if (bnd == null) {
				return null;
			}
			else {
				name = bnd.getName();
			}
		}

		// --------------- owner
		if (owner == null) {
			if (bnd == null) {
				return null;  // what would be the use of an AnnotationTypeAttribute without AnnotationType ?
			}
			else {
				ITypeBinding parentType = bnd.getDeclaringClass();
				if (parentType != null) {
					owner = this.ensureFamixAnnotationType(parentType, null, null, persistIt);
				}
				else  {
					return null;  // what would be the use of an AnnotationTypeAttribute without AnnotationType ?
				}
			}
		}

		// --------------- recover from name ?
		for (AnnotationTypeAttribute candidate : getEntityByName(AnnotationTypeAttribute.class, name) ) {
			// JDT treats annotation type attributes as methods ...
			// checkAndMapMethod wants a signature as 2nd argument so we add empty param list
			if ( (bnd != null) && matchAndMapMethod(bnd, name+"()", null, owner, candidate) ) {
				fmx = candidate;
				break;
			}
			// if the binding is null, the annotationTypeAttribute migth have been created
			else if ( (bnd == null) && matchAndMapVariable(null, name, owner, candidate)) {
				fmx = candidate;
				break;
			}
		}

		if (fmx == null) {
			fmx = ensureFamixEntity(AnnotationTypeAttribute.class, bnd, name, persistIt);
			fmx.setParentType(owner);
		}

		if ( (fmx!=null) && (bnd != null) ) {
			// Not suopp

			// setVisibility(fmx, bnd.getModifiers());
		}

		return fmx;
	}

	/**
	 * helper method, we know the attribute exists, ensureFamixAnnotationTypeAttribute will recover it
	 */
	public AnnotationTypeAttribute getFamixAnnotationTypeAttribute(IMethodBinding bnd, String name, AnnotationType owner) {
		return ensureFamixAnnotationTypeAttribute( bnd, name, owner, /*persistIt*/false);
	}

	/**
	 * Returns a FAMIX ParameterType (created by a FAMIX ParameterizableClass) with the given <b>name</b>, creating it if it does not exist yet
	 * In the second case, sets some default properties: not Abstract, not Final, not Private, not Protected, not Public
	 * @param name -- the name of the FAMIX ParameterType
	 * @param persistIt -- whether the ParameterType should be persisted in the Famix repository
	 * @return the FAMIX ParameterType or null in case of a FAMIX error
	 */
	public ParameterType ensureFamixParameterType(ITypeBinding bnd,	String name, TWithParameterizedTypes owner, boolean persistIt) {
		ParameterType fmx = null;

		// --------------- to avoid useless computations if we can
		fmx = (ParameterType)getEntityByKey(bnd);
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			if (bnd == null) {
				return null;
			}
			else {
				name = bnd.getName();
			}
		}

		// --------------- owner
		if (owner == null) {
			if (bnd == null) {
				owner = null;  // not really sure what to do here
			}
			else {
				ITypeBinding parentClass = bnd.getDeclaringClass();
				if (parentClass != null) {
					owner = (ParameterizableClass) this.ensureFamixType(parentClass, /*alwaysPersist?*/persistIt);
				}
				else {
					owner = null;  // not really sure what to do here
				}
			}
		}

		// --------------- recover from name ?
		for (Type candidate : this.getEntityByName(Type.class, name)) {
			if ( matchAndMapType(bnd, name, (ContainerEntity) owner, candidate) ) {
				fmx = (ParameterType) candidate;
				break;
			}
		}

		// --------------- create
		if (fmx == null) {
			fmx = ensureFamixEntity(ParameterType.class, bnd, name, persistIt);
			fmx.setTypeContainer((ContainerEntity) owner);
		}

		return fmx;
	}

	/**
	 * Checks whether the existing unmapped Famix Namespace matches the binding.
	 * Checks that the candidate has the same name as the JDT bound package, and checks recursively that owners also match.
	 *
	 * @param bnd       -- a JDT binding that we are trying to match to the candidate
	 * @param name      of the package
	 * @param owner     of the package
	 * @param candidate -- a Famix Entity
	 * @return whether the binding matches the candidate (if <b>true</b>, the mapping is recorded)
	 */
	private boolean matchAndMapPackage(IPackageBinding bnd, String name, Package owner, NamedEntity candidate) {
		if (!(candidate instanceof Package)) {
			return false;
		}

		// check whether bnd and candidate are already bound
		CheckResult res = checkKeyMatch(bnd, candidate);
		if (res == CheckResult.MATCH) {
			return true;
		} else if (res == CheckResult.FAIL) {
			return false;
		}

		if (checkNameMatch(bnd, name, candidate) == CheckResult.FAIL) {
			return false;
		}

		// names match, not need to look at owner because names of Namespaces are their fully qualified name
		conditionalMapToKey(bnd, candidate);
		return true;
	}

	/**
	 * Checks whether the existing unmapped Famix Type matches the binding.
	 * Checks that the candidate has the same name as the JDT bound type, and checks recursively that owners also match.
	 * We also check that the actual class of the candidate matches (can be a sub-class of FamixType).
	 * @param bnd -- a JDT binding that we are trying to match to the candidate
	 * @param name of the type
	 * @param owner of the type
	 * @param candidate -- a Famix NamedEntity (Class, Type, PrimitiveType, Enum, AnnotationType)
	 * @return whether the binding matches the candidate (if <b>true</b>, the mapping is recorded)
	 */
	private <T extends TWithTypes & TNamedEntity> boolean matchAndMapType(ITypeBinding bnd, String name, TNamedEntity owner, TNamedEntity candidate) {
		if (! (candidate instanceof Type) ) {
			return false;
		}

		// check whether bnd and candidate are already bound
		CheckResult res = checkKeyMatch(bnd, candidate);
		if (res == CheckResult.MATCH) {
			return true;
		}
		else if (res == CheckResult.FAIL) {
			return false;
		}

		if ( (bnd != null) && (bnd.isArray()) ) {
				bnd = bnd.getElementType();
		}

		// checking names
		if ( (bnd != null) && (bnd.isParameterizedType() || bnd.isRawType()) ) {
			name = bnd.getErasure().getName();
		}
		else if (bnd != null) {
			name = bnd.getName();
		}
		// else name = name
		if (checkNameMatch(null, name, candidate) == CheckResult.FAIL) {
			return false;
		}

		// special case of primitive types
		if (candidate instanceof PrimitiveType) {
			if ( (bnd != null) && bnd.isPrimitive() ) {
				// names are equal so it's OK
				conditionalMapToKey(bnd, candidate);
				return true;
			}
			else if ( (bnd == null) && (owner == null) ) {
				return true;
			}
		}

		// check owners without bnd
		if (bnd == null) {
			return matchAndMapTypeOwner(bnd, owner, (Type) candidate);
		}

		// check owners with bnd
		// type is an annotation
		if (bnd.isAnnotation() && (candidate instanceof AnnotationType)) {
			if (matchAndMapPackage(bnd.getPackage(), owner.getName(), (Package) Util.getOwner(owner), Util.getOwner(candidate))) {
				conditionalMapToKey(bnd, candidate);
				return true;
			} else {
				return false;
			}
		}

		// check owners with bnd
		// type is a Parameterized type
		if ((bnd.isParameterizedType() || bnd.isRawType()) && (candidate instanceof ParameterizedType)) {
			return matchAndMapTypeOwner(bnd, owner, (Type) candidate);
		}

		// check owners with bnd
		// type is an Enum
		if (bnd.isEnum() && (candidate instanceof Enum)) {
			return matchAndMapTypeOwner(bnd, owner, (Type) candidate);
		}

		// check owners with bnd
		// type is something elae (a class or interface)
		// Annotation are interfaces too, so we should check this one after isAnnotation
		if ( bnd.isClass()) {
			return matchAndMapClass(bnd, name, owner, (Type) candidate);
		}

		if(bnd.isInterface()) {
			return matchAndMapInterface(bnd, name, owner, (Type) candidate);
		}

		return false;
	}

	/**
	 * Checks whether the existing unmapped Famix Class (or Interface) matches the binding.
	 * Checks that the candidate has the same name as the JDT bound type, and checks recursively that owners also match.
	 * @param bnd -- a JDT binding that we are trying to match to the candidate
	 * @param name of the class
	 * @param owner of the class
	 * @param candidate -- a Famix Entity
	 * @return whether the binding matches the candidate (if <b>true</b>, the mapping is recorded)
	 */
	private boolean matchAndMapClass(ITypeBinding bnd, String name, TNamedEntity owner, TType candidate) {
		if (!(candidate instanceof Class)) {
			return false;
		}

		// check whether bnd and candidate are already bound
		CheckResult res = checkKeyMatch(bnd, (NamedEntity) candidate);
		if (res == CheckResult.MATCH) {
			return true;
		} else if (res == CheckResult.FAIL) {
			return false;
		}

		if (checkNameMatch(bnd, name, (NamedEntity) candidate) == CheckResult.FAIL) {
			return false;
		}

		// checking owner
		return matchAndMapTypeOwner(bnd, owner, (Type) candidate);
	}

	/**
	 * Checks whether the existing unmapped Famix Class (or Interface) matches the binding.
	 * Checks that the candidate has the same name as the JDT bound type, and checks recursively that owners also match.
	 * @param bnd -- a JDT binding that we are trying to match to the candidate
	 * @param name of the class
	 * @param owner of the class
	 * @param candidate -- a Famix Entity
	 * @return whether the binding matches the candidate (if <b>true</b>, the mapping is recorded)
	 */
	private boolean matchAndMapInterface(ITypeBinding bnd, String name, TNamedEntity owner, Type candidate) {
		if (!(candidate instanceof Interface)) {
			return false;
		}

		// check whether bnd and candidate are already bound
		CheckResult res = checkKeyMatch(bnd, candidate);
		if (res == CheckResult.MATCH) {
			return true;
		} else if (res == CheckResult.FAIL) {
			return false;
		}

		if (checkNameMatch(bnd, name, candidate) == CheckResult.FAIL) {
			return false;
		}

		// checking owner
		return matchAndMapTypeOwner(bnd, owner, candidate);
	}

	/**
	 * Checks whether the existing unmapped Famix "Method" matches the binding.
	 * Checks that the candidate has the same name and same signature as the JDT bound method, and checks recursively that owners also match.
	 * Note that AnnotationTypeAttribute are treated as methods by JDT, so they are checked here.
	 * @param bnd -- a JDT binding that we are trying to match to the candidate
	 * @param sig -- signature of the method
	 * @param retTyp -- return type of the method
	 * @param owner of the method
	 * @param candidate -- a Famix Entity (regular Method or AnnotationTypeAttribute)
	 * @return whether the binding matches the candidate (if <b>true</b>, the mapping is recorded)
	 */
	private  boolean matchAndMapMethod(IMethodBinding bnd, String sig, TType retTyp, TNamedEntity owner, NamedEntity candidate) {
		if (! (candidate instanceof Method) ) {
			return false;
		}

		// check whether bnd and candidate are already bound
		CheckResult res = checkKeyMatch(bnd, candidate);
		if (res == CheckResult.MATCH) {
			return true;
		}
		else if (res == CheckResult.FAIL) {
			return false;
		}

		// checking names
		String name = (sig != null) ? sig.substring(0, sig.indexOf('(')) : null;
		if (checkNameMatch(bnd, name, candidate) == CheckResult.FAIL) {
			return false;
		}

		// for methods, the name is not enough, we must test the signature also
		// but not for AnnotationTypeAttribute

			if (bnd != null) {
				sig = bnd.getName() + "(" + signatureParamsFromBinding(bnd) + ")";
			}
			if (! ((Method) candidate).getSignature().equals(sig)) {
				return false;
			}

			// and still for method, must also check the return type
			if (bnd != null) {
				if (bnd.isConstructor()) {
					if ( ((Method) candidate).getDeclaredType() != null ) {
						return false;
					}
					// else OK for now
				}
				else { // not a constructor
					if ( ((Method) candidate).getDeclaredType() == null ) {
						return false;
					}
					else if (! matchAndMapType(bnd.getReturnType(), null, null, ((Method) candidate).getDeclaredType()) ) {
						return false;
					}
					// else OK for now
				}
			}
			else {  // bnd == null
				if (retTyp == null) { // similar to (bnd.isConstructor())
					if ( ((Method) candidate).getDeclaredType() != null ) {
						return false;
					}
					// else OK for now
				} else { // (ret != null)  i.e. not a constructor
					if (((Method) candidate).getDeclaredType() == null) {
						return false;
					} else if (!matchAndMapType(null, retTyp.getName(), Util.getOwner(retTyp), (NamedEntity) ((Method) candidate).getDeclaredType())) {
						return false;
					}
					// else OK for now
				}
			}


		// check owner
		if (matchAndMapOwnerAsType(((bnd != null) ? bnd.getDeclaringClass() : null), owner, Util.getOwner(candidate)) == CheckResult.MATCH) {
			conditionalMapToKey(bnd, candidate);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks whether the candidate (an existing unmapped Famix "Variable" like Attribute, Parameter, ...) matches the binding.
	 * Checks that the candidate has the same name as the JDT bound variable, and checks recursively that owners also match.
	 * The Famix candidate is a NamedEntity and not a StructuralEntity to allow dealing with Famix EnumValue that JDT treats as variables
	 * @param bnd -- a JDT binding that we are trying to match to the candidate
	 * @param name of the variable
	 * @param owner of the variable
	 * @param candidate -- a Famix Entity (a StructuralEntity or an EnumValue)
	 * @return whether the binding matches the candidate (if <b>true</b>, the mapping is recorded)
	 */
	private boolean matchAndMapVariable(IVariableBinding bnd, String name, TNamedEntity owner, TNamedEntity candidate) {
		if (!(candidate instanceof TStructuralEntity)) {
			return false;
		}

		// check whether bnd and candidate are already bound
		CheckResult keyMatch = checkKeyMatch(bnd, candidate);
		if (keyMatch == CheckResult.MATCH) {
			return true;
		} else if (keyMatch == CheckResult.FAIL) {
			return false;
		}

		if (checkNameMatch(bnd, name, candidate) == CheckResult.FAIL) {
			return false;
		}

		// check owner
		TNamedEntity candidateOwner = Util.getOwner(candidate);

		// local variable or parameter ?
		// owner is a Method? (for example in case of an anonymous class)
		CheckResult res = matchAndMapOwnerAsMethod(((bnd != null) ? bnd.getDeclaringMethod() : null), owner, candidateOwner);
		if (res == CheckResult.FAIL) {
			return false;
		} else if (res == CheckResult.MATCH) {
			conditionalMapToKey(bnd, candidate);
			return true;
		}

		// check owner
		// <anArray>.length field?
		if (name.equals("length")) {
			boolean isArrayLengthField = ((bnd != null) && (bnd.getDeclaringClass() == null)) ||
										 ((bnd == null) && (owner.getName().equals(JavaDictionary.ARRAYS_NAME)));
			if (isArrayLengthField) {
				if (candidateOwner.getName().equals(JavaDictionary.ARRAYS_NAME)) {
					conditionalMapToKey(bnd, candidate);
					return true;
				}
				else {
					return false;
				}
			}
		}

		// check owner
		// "normal" field?
		res = matchAndMapOwnerAsType( ((bnd != null) ? bnd.getDeclaringClass() : null), owner, candidateOwner);
		if (res == CheckResult.MATCH) {
			conditionalMapToKey(bnd, candidate);
			return true;
		}
		return false;
	}

	/**
	 * Checks whether the existing unmapped Famix Type's parent (or owner) matches the binding's owner.
	 * Checks that the candidate has the same name as the JDT bound type, and checks recursively that owners also match.
	 * @param bnd -- a JDT binding whose owner we are trying to match to the candidate's owner
	 * @param owner -- the owner of the type
	 * @param candidate -- a Famix Entity
	 * @return whether we found a match (if <b>true</b>, the mapping is recorded)
	 */
	private boolean matchAndMapTypeOwner(ITypeBinding bnd, TNamedEntity owner, Type candidate) {
		ContainerEntity candidateOwner = Util.getOwner(candidate);

		// owner is a Method? (for example in case of an anonymous class)
		CheckResult res = matchAndMapOwnerAsMethod(((bnd != null) ? bnd.getDeclaringMethod() : null), owner, candidate);
		if (res == CheckResult.MATCH) {
			conditionalMapToKey(bnd, candidate);
			return true;
		} else if (res == CheckResult.FAIL) {
			return false;
		}

		// owner is a class ?
		res = matchAndMapOwnerAsType(((bnd != null) ? bnd.getDeclaringClass() : null), owner, candidateOwner);
		if (res == CheckResult.MATCH) {
			conditionalMapToKey(bnd, candidate);
			return true;
		}
		else if (res == CheckResult.FAIL) {
			return false;
		}

		// owner must be a package
		if (matchAndMapOwnerAsNamespace( ((bnd != null)?bnd.getPackage():null), owner, candidateOwner) == CheckResult.MATCH) {
			conditionalMapToKey(bnd, candidate);
			return true;
		}
		return false;
	}

	/**
	 * Check whether the owner of candidates is a method macthinf either methBnd or owner
	 * @param methBnd
	 * @param owner
	 * @param candidateOwner
	 * @return a {@link CheckResult}
	 */
	private  <T extends TNamedEntity> CheckResult matchAndMapOwnerAsMethod(IMethodBinding methBnd, T owner, T candidateOwner) {
		if ((methBnd != null) || ((owner != null) && (owner instanceof Method))) {
			if (!(candidateOwner instanceof Method)) {
				return CheckResult.FAIL;
			}

			ContainerEntity ownerOwner = (owner != null) ? (ContainerEntity) Util.getOwner(owner) : null;
			String ownerSig = (owner != null) ? ((Method) owner).getSignature() : null;
			Type ownerReturn = (owner != null) ? (Type) ((Method) owner).getDeclaredType() : null;

			if (matchAndMapMethod(methBnd, ownerSig, ownerReturn, ownerOwner, (Method) candidateOwner)) {
				return CheckResult.MATCH;
			} else {
				return CheckResult.FAIL;
			}
		}
		return CheckResult.UNDECIDED;
	}

	/**
	 * @param typBnd
	 * @param owner
	 * @param candidateOwner
	 * @return a {@link CheckResult}
	 */
	private CheckResult matchAndMapOwnerAsType(ITypeBinding typBnd, TNamedEntity owner, TNamedEntity candidateOwner) {
		if ((typBnd != null) || ((owner != null) && (owner instanceof Type))) {
			if (!(candidateOwner instanceof Type)) {
				return CheckResult.FAIL;
			}

			TNamedEntity ownerOwner = (owner != null) ? Util.getOwner(owner) : null;
			String ownerName = (owner != null) ? ((Type) owner).getName() : null;

			if (matchAndMapType(typBnd, ownerName, ownerOwner, candidateOwner)) {
				return CheckResult.MATCH;
			} else {
				return CheckResult.FAIL;
			}
		}
		return CheckResult.UNDECIDED;
	}

	private CheckResult matchAndMapOwnerAsNamespace(IPackageBinding pckgBnd, TNamedEntity owner, ContainerEntity candidateOwner) {
		if ((pckgBnd != null) || ((owner != null) && (owner instanceof Package))) {
			if (!(candidateOwner instanceof Package)) {
				return CheckResult.FAIL;
			}

			Package ownerOwner = (owner != null) ? (Package) Util.getOwner(owner) : null;
			String ownerName = (owner != null) ? ((Package) owner).getName() : null;

			if (matchAndMapPackage(pckgBnd, ownerName, ownerOwner, candidateOwner)) {
				return CheckResult.MATCH;
			} else {
				return CheckResult.FAIL;
			}
		}
		return CheckResult.UNDECIDED;
	}

	/**
	 * Checks whether the name and the candidate matches the name of the entity (given either by 'bnd' or 'name')<br>
	 * 'name' and 'bnd' cannot be null together
	 * @param bnd -- binding associated with the entity may be null
	 * @param name -- name of the entity may be null
	 * @param candidate
	 * @return true if names match, false if not
	 */
	private CheckResult checkNameMatch(IBinding bnd, String name, TNamedEntity candidate) {
		if ( (bnd != null) && (! bnd.getName().equals(candidate.getName())) ) {
			return CheckResult.FAIL;
		}
		else if ( (bnd == null) && (name != null) && (! name.equals(candidate.getName())) ) {
			return CheckResult.FAIL;
		}
		else {
			return CheckResult.MATCH;
		}
	}

	/**
	 * Check whether key and candidate are already bound together, whether either is bound to something else, or whether none is bound
	 * @param key
	 * @param candidate
	 * @return <ul><li><b>-1</b>, if either is bound to something else</li><li><b>0</b>, if none is bound (or key is null)</li><li><b>1</b>, if they are bound to each other</li></ul>
	 */
	private CheckResult checkKeyMatch(IBinding key, TNamedEntity candidate) {
		if (key == null) {
			return CheckResult.UNDECIDED;
		}

		NamedEntity bound = (NamedEntity)getEntityByKey(key);
		if (bound == candidate) {
			return CheckResult.MATCH;
		}
		else if (bound != null) {
			return CheckResult.FAIL;
		}
		else if (getEntityKey(candidate) != null) {
			// candidate already bound, and not to this binding
			return CheckResult.FAIL;
		}
		else {
			return CheckResult.UNDECIDED;
		}
	}

	private void conditionalMapToKey(IBinding bnd, TNamedEntity ent) {
		if (bnd != null) {
			mapEntityToKey(bnd, ent);
		}
	}

	public Method ensureFamixMethod(IMethodBinding bnd, boolean persistIt) {
		return ensureFamixMethod(
				bnd,
				/*name*/null,
				/*paramsType*/(Collection<String>)null,
				/*returnType*/null,
				/*owner*/null,
				(bnd == null) ? UNKNOWN_MODIFIERS : bnd.getModifiers(),
				persistIt);
	}

	/**
	 * Returns a Famix Method associated with the IMethodBinding. The Entity is created if it does not exist.
	 * The Entity is created if it does not exist.
	 * @param key to which the entity will be mapped (may be null, but then it will be difficult to recover the entity)
	 * @param name -- the name of the FAMIX Method (MUST NOT be null, but this is not checked)
	 * @param sig -- method's signature, including type of parameters and return type (should not be null, but it will work if it is)
	 * @param ret -- Famix Type returned by the method (ideally should only be null in case of a constructor, but will accept it in any case)
	 * @param owner -- type defining the method (should not be null, but it will work if it is)
	 * @param persistIt -- whether the Method should be persisted in the Famix repository
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public Method ensureFamixMethod(IMethodBinding bnd, String name, Collection<String> paramTypes, TType ret, TWithMethods owner, int modifiers, boolean persistIt) {
		Method fmx = null;
		String sig;
		boolean delayedRetTyp;

		// --------------- to avoid useless computations if we can
		fmx = (Method)getEntityByKey(bnd);
		if (fmx != null) {
			return fmx;
		}

		if ( (bnd != null) && bnd.isParameterizedMethod() ) {
			bnd = bnd.getMethodDeclaration();
		}

		// --------------- name
		if (name == null) {
			if (bnd == null) {
				return null;
			}
			else {
				name = bnd.getName();
			}
		}

		// --------------- signature
		sig = name + "(";
		 if (bnd != null) {
	            sig += signatureParamsFromBinding(bnd);
	        }
        else if (paramTypes != null) {
			sig += signatureParamsFromStringCollection(paramTypes);
		}
		else {
			sig += "???";
		}
		sig += ")";

		// --------------- return type
		delayedRetTyp = false;
		ITypeBinding retTypBnd = null;
		if (ret == null) {
			if (bnd == null) {
				ret = null;  // what else ?
			}
			else {
				if (bnd.isConstructor()) {
					ret = null;
				}
				else {

					// must create the return type
					// but for method like "<T> T mtd()" where T belongs to mtd and mtd returns T,
					// we need T to create the method and the method to create T ...
					// so we need to test the situation and deal with it
					retTypBnd = bnd.getReturnType();
					if (retTypBnd == null) {
						ret = null;
					}
					else if (retTypBnd.isArray()) {
						retTypBnd = retTypBnd.getElementType();
					}

					if ( (retTypBnd != null) && retTypBnd.isTypeVariable() && (retTypBnd.getDeclaringMethod() == bnd) ) {
						ret = null;
						delayedRetTyp = true;
					}
					else {
						ret = this.ensureFamixType(retTypBnd,(TWithTypes) /*ctxt*/owner, /*alwaysPersist?*/persistIt);
					}
				}
			}
		}

		// --------------- owner
		if (owner == null) {
			if (bnd == null) {
				owner = ensureFamixClassStubOwner();
			}
			else {
				ITypeBinding classBnd = bnd.getDeclaringClass();
				if (classBnd != null) {
					TType tmpOwn = ensureFamixType(classBnd, /*alwaysPersist?*/persistIt);
					if (tmpOwn instanceof ParameterizedType) {
						owner =  (TWithMethods) ((ParameterizedType) tmpOwn).getParameterizableClass();
					}
					else {
						owner = (TWithMethods) tmpOwn;
					}
				}
				else {
					owner = ensureFamixClassStubOwner();
				}
			}
		}

		// --------------- recover from name ?
		for (Method candidate : this.getEntityByName(Method.class, name)) {
			if (matchAndMapMethod(bnd, sig, ret, (TNamedEntity) owner, candidate)) {
				fmx = candidate;
				break;
			}
		}

		if (fmx == null) {
			fmx = ensureFamixEntity(Method.class, bnd, name, persistIt);
			fmx.setSignature(sig);
			fmx.setDeclaredType(ret);
			fmx.setParentType(owner);
		}

		if (fmx != null) {
			setMethodModifiers(fmx, modifiers);
			// if it's a constructor
			if (fmx.getName().equals(Util.getOwner(fmx).getName())) {
				fmx.setKind(CONSTRUCTOR_KIND_MARKER);
			}
		}

		if ((fmx != null) && delayedRetTyp) {
			int retTypModifiers = (retTypBnd != null) ? retTypBnd.getModifiers() : UNKNOWN_MODIFIERS;
			fmx.setDeclaredType(this.ensureFamixType(retTypBnd, /*name*/null, /*owner*/fmx, /*ctxt*/(ContainerEntity) owner, retTypModifiers, /*alwaysPersist?*/persistIt));
		}

		return fmx;
	}

	/**
	 * Creates or recovers a stub Famix Method
	 * @param name of the method
	 * @return the Famix Method
	 */
	public Method ensureFamixStubMethod(String name) {
		return ensureFamixMethod(null, name, /*paramType*/(Collection<String>)null, /*returnType*/null, ensureFamixClassStubOwner(), /*modifiers*/0, false);  // cast needed to desambiguate the call
	}

	/**
	 * Sets the visibility of a FamixNamedEntity
	 *
	 * @param fmx -- the FamixNamedEntity
	 * @param mod -- a description of the modifiers as understood by org.eclipse.jdt.core.dom.Modifier
	 */
	public void setVisibility(THasVisibility fmx, int mod) {
		if (Modifier.isPublic(mod)) {
			fmx.setVisibility(MODIFIER_PUBLIC);
		} else if (Modifier.isPrivate(mod)) {
			fmx.setVisibility(MODIFIER_PRIVATE);
		} else if (Modifier.isProtected(mod)) {
			fmx.setVisibility(MODIFIER_PROTECTED);
		} else {
			fmx.setVisibility(MODIFIER_PACKAGE);
		}
	}

	public void setAttributeModifiers(Attribute fmx, int mod) {
		setVisibility(fmx, mod);
		fmx.setIsVolatile(Modifier.isVolatile(mod));
		fmx.setIsTransient(Modifier.isTransient(mod));
		fmx.setIsFinal(Modifier.isFinal(mod));
		fmx.setIsClassSide(Modifier.isStatic(mod));
	}

	public void setMethodModifiers(Method fmx, int mod) {
		setVisibility(fmx, mod);
		fmx.setIsSynchronized(Modifier.isSynchronized(mod));
		fmx.setIsAbstract(Modifier.isAbstract(mod));
		fmx.setIsFinal(Modifier.isFinal(mod));
		fmx.setIsClassSide(Modifier.isStatic(mod));
	}

	public void setClassModifiers(Class fmx, int mod) {
		fmx.setIsAbstract(Modifier.isAbstract(mod));
		fmx.setIsFinal(Modifier.isFinal(mod));
		fmx.setIsClassSide(Modifier.isStatic(mod));
		setVisibility(fmx, mod);
	}

	public void setClassModifiers(Exception fmx, int mod) {
	}

	public void setClassModifiers(Interface fmx, int mod) {
		fmx.setIsFinal(Modifier.isFinal(mod));
		fmx.setIsClassSide(Modifier.isStatic(mod));
		setVisibility(fmx, mod);
	}

	/**
	 * Returns a Famix Attribute associated with the IVariableBinding.
	 * The Entity is created if it does not exist.<br>
	 * @param key to which the entity will be mapped (may be null, but then it will be difficult to recover the entity)
	 * @param name -- the name of the FAMIX Attribute (MUST NOT be null, but this is not checked)
	 * @param type -- Famix Type of the Attribute (should not be null, but it will work if it is)
	 * @param owner -- type defining the Attribute (should not be null, but it will work if it is)
	 * @param persistIt -- whether the Attribute should be persisted in the Famix repository
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public Attribute ensureFamixAttribute(IVariableBinding bnd, String name, Type type, TWithAttributes owner, boolean persistIt) {
		Attribute fmx = null;

		// --------------- to avoid useless computations if we can
		fmx = (Attribute)getEntityByKey(bnd);
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			if (bnd == null) {
				return null;
			}
			else {
				name = bnd.getName();
			}
		}
/*
		// --------------- type
		if (type == null) {
			if (bnd == null) {
				type = null;  // what else ?
			}
			else {
				type = this.ensureFamixType(bnd.getType(), /*ctxt* /owner, /*alwaysPersist?* /persistIt);
			}
		}
*/
		// --------------- owner
		if (owner == null) {
			if (bnd == null) {
				return null;  // what would be the interest of creating an attribute for which we ignore the declaring class?
			}
			else {
				ITypeBinding classBnd = bnd.getDeclaringClass();
				if (classBnd != null) {
					TType tmpOwn = ensureFamixType(classBnd, /*alwaysPersist?*/persistIt);
					if (tmpOwn instanceof ParameterizedType) {
						owner = (TWithAttributes) ((ParameterizedType) tmpOwn).getParameterizableClass();
					} else {
						owner = (TWithAttributes) tmpOwn;
					}
				} else {
					return null;  // what would be the interest of creating an attribute for which we ignore the declaring class?
				}
			}
		}

		// --------------- recover from name ?
		for (Attribute candidate : getEntityByName(Attribute.class, name)) {
			if (matchAndMapVariable(bnd, name, (TNamedEntity) owner, candidate)) {
				fmx = candidate;
				break;
			}
		}

		if (fmx == null) {
			fmx = ensureFamixEntity(Attribute.class, bnd, name, persistIt);
			fmx.setParentType( owner);
		}

		if (fmx != null) {
			fmx.setParentType((TWithAttributes) owner);
			fmx.setDeclaredType(type);
			if (bnd != null) {
				int mod = bnd.getModifiers();
				setAttributeModifiers(fmx, mod);
			}
		}

		return fmx;
	}

	public Attribute ensureFamixAttribute(IVariableBinding bnd, String name, TWithAttributes owner, boolean persistIt) {
		return ensureFamixAttribute(bnd, name, /*declared type*/null, owner, persistIt);
	}

	/**
	 * helper method, we know the var exists, ensureFamixAttribute will recover it
	 */
	public Attribute getFamixAttribute(IVariableBinding bnd, String name, TWithAttributes owner) {
		return ensureFamixAttribute(bnd, name, /*declared type*/null, owner, /*persistIt*/false);
	}

	/**
	 * Returns a Famix Parameter associated with the IVariableBinding.
	 * The Entity is created if it does not exist.<br>
	 * @param persistIt -- whether to persist or not the entity eventually created
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public Parameter ensureFamixParameter(IVariableBinding bnd, String name, Type typ, TMethod tMethod, boolean persistIt) {
		Parameter fmx = null;

		// --------------- to avoid useless computations if we can
		fmx = (Parameter)getEntityByKey(bnd);
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			if (bnd == null) {
				return null;
			}
			else {
				name = bnd.getName();
			}
		}

		// --------------- owner
		if (tMethod == null) {
			if (bnd == null) {
				tMethod = ensureFamixStubMethod("<"+name+"_owner>");
			}
			else {
				tMethod = ensureFamixMethod(bnd.getDeclaringMethod(), persistIt);
			}
		}
/*
		// --------------- type
		if (typ == null) {
			if (bnd == null) {
				typ = null;  // what else ?
			}
			else {
				typ = this.ensureFamixType(bnd.getType(), /*ctxt* /owner.getParentType(), /*alwaysPersist?* /persistIt);  // context of the parameter def = the class definition
			}
		}
*/
		// --------------- recover from name ?
		for (Parameter candidate : getEntityByName(Parameter.class, name) ) {
			if ( matchAndMapVariable(bnd, name, tMethod, candidate) ) {
				fmx = candidate;
				break;
			}
		}

		if (fmx == null) {
			fmx = ensureFamixEntity(Parameter.class, bnd, name, persistIt);
			fmx.setParentBehaviouralEntity(tMethod);
		}

		if (fmx != null) {
			fmx.setParentBehaviouralEntity(tMethod);
			fmx.setDeclaredType(typ);
		}

		return fmx;
	}

	/**
	 * helper method, we know the var exists, ensureFamixParameter will recover it
	 */
	public Parameter getFamixParameter(IVariableBinding bnd, String name, TMethod tMethod) {
		return ensureFamixParameter(bnd, name, /*declared type*/null, tMethod, /*persistIt*/false);
	}

	/**
	 * Returns a Famix LocalVariable associated with the IVariableBinding.
	 * The Entity is created if it does not exist.<br>
	 * @param name -- the name of the FAMIX LocalVariable
	 * @param persistIt  -- whether to persist or not the entity eventually created
	 * @return the Famix Entity found or created. May return null if <b>bnd</b> and <b>name</b> are null, or <b>bnd</b> and <b>owner</b> are null, or in case of a Famix error
	 */
	public LocalVariable ensureFamixLocalVariable(IVariableBinding bnd, String name, TWithLocalVariables owner, boolean persistIt) {
		LocalVariable fmx = null;

		// --------------- to avoid useless computations if we can
		fmx = (LocalVariable)getEntityByKey(bnd);
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			if (bnd == null) {
				return null;
			}
			else {
				name = bnd.getName();
			}
		}
/*
		// --------------- type
		if (typ == null) {
			if (bnd == null) {
				typ = null;  // what else ?
			}
			else {
				typ = this.ensureFamixType(bnd.getType(), /*ctxt* /owner, /*alwaysPersist?* /persistIt);
			}
		}
*/
		// --------------- owner
		if (owner == null) {
			if (bnd == null) {
				return null;  // what would be the interest of a local variable for which we ignore the declaring method?
			}
			else {
				owner = ensureFamixMethod(bnd.getDeclaringMethod(), false);
			}
		}

		// --------------- recover from name ?
		for (LocalVariable candidate : getEntityByName(LocalVariable.class, name) ) {
			if ( matchAndMapVariable(bnd, name, (TNamedEntity) owner, candidate) ) {
				fmx = candidate;
				break;
			}
		}

		if (fmx == null) {
			fmx = ensureFamixEntity(LocalVariable.class, bnd, name, persistIt);
			fmx.setParentBehaviouralEntity(owner);
		}

		if (fmx != null) {
			// we just created it or it was not bound, so we make sure it has the right information in it
			fmx.setParentBehaviouralEntity(owner);
		}

		return fmx;
	}

	/**
	 * helper method, we know the var exists, ensureFamixLocalVariable will recover it
	 */
	public LocalVariable getFamixLocalVariable(IVariableBinding bnd, String name, TWithLocalVariables owner) {
		return ensureFamixLocalVariable(bnd, name, owner, /*persistIt*/false);
	}

	/**
	 * Returns a FAMIX ImplicitVariable with the given <b>name</b> ("self" or "super") and corresponding to the <b>type</b>.
	 * If this ImplicitVariable does not exist yet, it is created
	 * @param name -- the name of the FAMIX ImplicitVariable (should be Dictionary.SELF_NAME or Dictionary.SUPER_NAME)
	 * @param type -- the Famix Type for this ImplicitVariable (should not be null)
	 * @param tMethod -- the ContainerEntity where the implicit variable appears (should be a method inside <b>type</b>)
	 * @param persistIt -- whether the ImplicitVariable should be persisted in the Famix repository
	 * @return the FAMIX ImplicitVariable or null in case of a FAMIX error
	 */
	public ImplicitVariable ensureFamixImplicitVariable(IBinding key, String name, TType type, TMethod tMethod, boolean persistIt) {
		ImplicitVariable fmx;
		fmx = ensureFamixEntity(ImplicitVariable.class, key, name, persistIt);
		fmx.setParentBehaviouralEntity(tMethod);
		return fmx;
	}

	public ImplicitVariable ensureFamixImplicitVariable(String name, TType tType, TMethod tMethod, boolean persistIt) {
		IBinding bnd = ImplicitVarBinding.getInstance(tMethod, name);
		return ensureFamixImplicitVariable(bnd, name, tType, tMethod, persistIt);
	}

	/**
	 * Creates and returns a FAMIX Comment and associates it with an Entity (ex: for Javadocs)
	 * @param jCmt -- the content (String) of the comment 
	 * @param owner -- the entity that is commented
	 * @return the FAMIX Comment
	 */
	public Comment createFamixComment(org.eclipse.jdt.core.dom.Comment jCmt, TWithComments owner) {
		Comment cmt = null;

		if ( (jCmt != null) && (owner != null) && (! commentAlreadyRecorded(owner, jCmt)) ) {
			
			cmt = new Comment();
			addSourceAnchor(cmt, jCmt, /*oneLineAnchor*/jCmt.isLineComment());
			famixRepoAdd(cmt);
			cmt.setCommentedEntity(owner);
		}

		return cmt;
	}

	private boolean commentAlreadyRecorded(TWithComments fmx, org.eclipse.jdt.core.dom.Comment jCmt) {
		int startPos = jCmt.getStartPosition();
		boolean found = false;

		for (TComment cmt : fmx.getComments()) {
			Comment cmt2 = (Comment)cmt;
			if (((IndexedFileAnchor)cmt2.getSourceAnchor()).getStartPos().intValue() == startPos) {
				found = true;
				break;
			}
		}

		return found;
	}

	/**
	 * Adds location information to a Famix Entity.
	 * Location informations are: <b>name</b> of the source file and <b>line</b> position in this file. They are found in the JDT ASTNode: ast.
	 * This method also creates some basic links between the entity and others (e.g. declaring container, return type, ...)
	 * @param fmx -- Famix Entity to add the anchor to
	 * @param node -- JDT ASTNode, where the information is extracted
	 * @param oneLineAnchor -- whether to consider that endLine = beginLine (oneLineAnchor) or not. Created to add anchor to some TAssociation happening within <b>ast</b>
	 * @return the Famix SourceAnchor added to fmx. May be null in case of incorrect parameter ('fmx' or 'ast' == null)
	 */
	public SourceAnchor addSourceAnchor(TSourceEntity fmx, ASTNode node, boolean oneLineAnchor) {
		IndexedFileAnchor fa = null;

		if ((fmx != null) && (node != null)) {
			// position in source file
			int beg = node.getStartPosition() + 1; // Java starts at 0, Moose at 1
			int end = beg + node.getLength() - 1;

			// find source Compilation Unit
			// there is a special case for the JDT Comment Nodes
			if (node instanceof org.eclipse.jdt.core.dom.Comment) {
				node = ((org.eclipse.jdt.core.dom.Comment) node).getAlternateRoot();
			} else {
				node = node.getRoot();
			}

			fa = new IndexedFileAnchor();
			((IndexedFileAnchor)fa).setStartPos(beg);
			((IndexedFileAnchor)fa).setEndPos(end);

			fa.setFileName((String) ((CompilationUnit)node).getProperty(SOURCE_FILENAME_PROPERTY));
			fmx.setSourceAnchor(fa);
			famixRepoAdd(fa);
		}

		return fa;
	}

	/**
	 * Adds location information to a Famix Entity.
	 * Location informations are: <b>name</b> of the source file and <b>line</b> position in this file. They are found in the JDT ASTNode: ast.
	 * This method also creates some basic links between the entity and others (e.g. declaring container, return type, ...)
	 * @param fmx -- Famix Entity to add the anchor to
	 * @param node -- JDT ASTNode, where the information is extracted
	 * @param oneLineAnchor -- whether to consider that endLine = beginLine (oneLineAnchor) or not. Created to add anchor to some TAssociation happening within <b>ast</b>
	 * @return the Famix SourceAnchor added to fmx. May be null in case of incorrect parameter ('fmx' or 'ast' == null)
	 */
	public SourceAnchor addSourceAnchor(Method fmx, MethodDeclaration node, boolean oneLineAnchor) {
		IndexedFileAnchor fa = null;

		if ((fmx != null) && (node != null)) {
			// position in source file

			//Collect the possible beginner
			List<ASTNode> methodDeclarationModifiers = new ArrayList<>();
			methodDeclarationModifiers.addAll(node.modifiers());
			if (node.getName() != null) {
				methodDeclarationModifiers.add(node.getName());
			}
			if (node.getReturnType2() != null) {
				methodDeclarationModifiers.add(node.getReturnType2());
			}
			int beg = (methodDeclarationModifiers.stream().mapToInt(el -> el.getStartPosition()).min().getAsInt()) + 1;
			int end = node.getStartPosition() + node.getLength();


			fa = new IndexedFileAnchor();
			((IndexedFileAnchor)fa).setStartPos(beg);
			((IndexedFileAnchor)fa).setEndPos(end);

			fa.setFileName((String) node.getRoot().getProperty(SOURCE_FILENAME_PROPERTY));
			fmx.setSourceAnchor(fa);
			famixRepoAdd(fa);
		}

		return fa;
	}

	/**
	 * Creates or recovers the Famix Class for "Object".
	 * Because "Object" is the root of the inheritance tree, it needs to be treated differently.
	 *
	 * @param bnd -- a potential binding for the java "Object" class
	 * @return a Famix class for "Object"
	 */
	public Class ensureFamixClassObject(ITypeBinding bnd) {
		Class fmx = ensureFamixUniqEntity(Class.class, bnd, OBJECT_NAME);

		if (fmx != null) {
			fmx.setTypeContainer(ensureFamixPackageJavaLang(null));
		}
		// Note: "Object" has no superclass

		return fmx;
	}

	/**
	 * Ensures the Java meta-class: java.lang.Class<>
	 */
	public Class ensureFamixMetaClass(ITypeBinding bnd) {
		Package javaLang = ensureFamixPackageJavaLang((bnd == null) ? null : bnd.getPackage());
		// always persist the MetaClass whatever the value of VerveineJParser.classSummary
		Class fmx = this.ensureFamixClass(null, METACLASS_NAME, javaLang, /*isGeneric*/true, Modifier.PUBLIC & Modifier.FINAL, /*alwaysPersist?*/true);

		if ((fmx != null) && (fmx.getSuperInheritances() == null)) {
			ensureFamixInheritance(ensureFamixClassObject(null), fmx, null);
		}

		return fmx;
	}

	public Class getFamixMetaClass(ITypeBinding bnd) {
		Package javaLang = ensureFamixPackageJavaLang((bnd == null) ? null : bnd.getPackage());
		return this.ensureFamixClass(null, METACLASS_NAME, javaLang, /*isGeneric*/true, UNKNOWN_MODIFIERS, /*alwaysPersist?*/false);
	}

	/**
	 * Creates or recovers the Famix Class for all arrays (<some-type> [])
	 * In java arrays or objects of special classes (i.e. "I[" for an array of int).
	 * JDT does not create a binding for these classes, so we create a stub one here.
	 *
	 * @return a Famix class
	 */
	public Class ensureFamixClassArray() {
		Class fmx = ensureFamixUniqEntity(Class.class, null, ARRAYS_NAME);
		if (fmx != null) {
			ensureFamixInheritance(ensureFamixClassObject(null), fmx, /*prev*/null);
			fmx.setContainer(ensureFamixPackageDefault());

			// may be not needed anymore now that we use modifiers
			/*fmx.setIsAbstract(Boolean.FALSE);
			fmx.setIsFinal(Boolean.FALSE);
			fmx.setIsInterface(Boolean.FALSE);
			fmx.setIsPrivate(Boolean.FALSE);
			fmx.setIsProtected(Boolean.FALSE);*/
			fmx.setVisibility(MODIFIER_PUBLIC);
		}

		return fmx;
	}

	public String removeLastName(String qualifiedName) {
		String ret = null;
		int last = qualifiedName.lastIndexOf('.');
		if (last > 0) {
			// recursively creating the parent
			ret = qualifiedName.substring(0, last);
		}
		else {
			ret = "";
		}

		return ret;
	}

	/** Generates the list of parameters for a method signature
	 * @return a string
	 */
	protected String signatureParamsFromBinding(IMethodBinding bnd) {
		boolean first = true;
		String sig = new String();

		for (ITypeBinding parBnd : bnd.getParameterTypes()) {
			if (first) {
				sig = parBnd.getName();
				first = false;
			}
			else {
				sig += "," + parBnd.getName();
			}
		}
		return sig;
	}

	private String signatureParamsFromStringCollection(Collection<String> paramTypes) {
		boolean first = true;
		String sig = new String();

		for (String t : paramTypes) {
			if (first) {
				sig = t;
				first = false;
			}
			else {
				sig += "," + t;
			}
		}
		return sig;
	}

}
