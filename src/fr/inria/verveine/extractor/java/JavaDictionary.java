package fr.inria.verveine.extractor.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMemberValuePairBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;

import ch.akuhn.fame.Repository;
import fr.inria.verveine.core.Dictionary;
import fr.inria.verveine.core.gen.famix.Access;
import fr.inria.verveine.core.gen.famix.AnnotationInstanceAttribute;
import fr.inria.verveine.core.gen.famix.AnnotationType;
import fr.inria.verveine.core.gen.famix.AnnotationTypeAttribute;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.BehaviouralEntity;
import fr.inria.verveine.core.gen.famix.Class;
import fr.inria.verveine.core.gen.famix.Comment;
import fr.inria.verveine.core.gen.famix.ContainerEntity;
import fr.inria.verveine.core.gen.famix.Enum;
import fr.inria.verveine.core.gen.famix.EnumValue;
import fr.inria.verveine.core.gen.famix.FileAnchor;
import fr.inria.verveine.core.gen.famix.Inheritance;
import fr.inria.verveine.core.gen.famix.LocalVariable;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.NamedEntity;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.Parameter;
import fr.inria.verveine.core.gen.famix.ParameterType;
import fr.inria.verveine.core.gen.famix.ParameterizableClass;
import fr.inria.verveine.core.gen.famix.ParameterizedType;
import fr.inria.verveine.core.gen.famix.PrimitiveType;
import fr.inria.verveine.core.gen.famix.SourceAnchor;
import fr.inria.verveine.core.gen.famix.SourcedEntity;
import fr.inria.verveine.core.gen.famix.Type;
import fr.inria.verveine.core.gen.famix.UnknownVariable;

/**
 * A {@link fr.inria.verveine.Dictionary} specialized for Java
 * @author anquetil
 */
public class JavaDictionary extends Dictionary<IBinding> {

	// a property added to CompilationUnits to record the name of the source file name they come from
	public static final String SOURCE_FILENAME_PROPERTY = "verveine-source-filename";

	public static final String OBJECT_NAME = "Object";
	public static final String METACLASS_NAME = "Class";
	public static final String OBJECT_PACKAGE_NAME = "java.lang";
	public static final String ARRAYS_NAME = "default[]";
	public static final String INSTANCE_INIT_BLOCK_NAME = "<InstanceInitializer>";
	public static final String STATIC_INIT_BLOCK_NAME = "<StaticInitializer>";

	public void mapKey(IBinding bnd, NamedEntity fmx) {
		mapToKey.put(bnd, fmx);
	}

	/**
	 * @param famixRepo
	 */
	public JavaDictionary(Repository famixRepo) {
		// should check whether there is already one in the repository
		super(famixRepo);
	}

	/**
	 * Returns a Famix Namespace associated with its IPackageBinding and/or name.
	 * The Entity is created if it does not exist (see also {@link Dictionary#ensureFamixNamespace(Object, String)}).
	 * Also creates or recovers recusively it's parent namespaces.<br>
	 * At least one of <b>bnd</b> and <b>name</b> must be non null.
	 * @param bnd -- the JDT Binding that may be used as a uniq key to recover this namespace
	 * @param name -- fully qualified name of the namespace (e.g. 'java.lang')
	 * @return the Famix Namespace found or created. May return null in case of a Famix error
	 */
	public Namespace ensureFamixNamespace(IPackageBinding bnd, String name) {
		Namespace fmx = null;
		Namespace parent = null;

		if ( (name == null) && (bnd != null) ) {
			name = bnd.getName();
		}
		
		if (name.length() > 0) {
			/* Note: Packages are created with their fully-qualified name to simplify recovering them when we don't have a binding
			 * (for example when creating parent packages of a package we have a binding for).
			 * Because the preferred solution in Moose is to give their simple names to packages, they must be post-processed when
			 * all is said and done. */
			fmx = super.ensureFamixNamespace( bnd, name);
			parent = ensureFamixNamespace(null, removeLastName(name));
			// set the parentscope relationship
			if ( (parent != null) && (fmx != null) && (fmx.getParentScope() == null)) {
				parent.addChildScopes(fmx);
			}
		}

		return fmx;
	}

	/**
	 * Recovers or creates a Famix Type (see also {@link Dictionary#ensureFamixType(Object, String, ContainerEntity)}
	 */
	public Type ensureFamixType(ITypeBinding bnd, String name, ContainerEntity owner, ContainerEntity ctxt) {
		Type fmx = null;

		if (bnd == null) {
			if (name == null) {
				return null;
			}
			fmx = searchTypeInContext(name, ctxt); // WildCard Types don't have binding
			if (fmx != null) {
				return fmx;
			}

			if ( (owner != null) && (owner instanceof ParameterizableClass) ) {
				return ensureFamixParameterType(null, name, (ParameterizableClass) owner);
			}
			else {
				return super.ensureFamixType(null, name, owner);
			}
		}

		// bnd != null

		fmx = (Type) getEntityByKey(bnd);
		if (fmx != null) {
			return fmx;
		}

		if (bnd.isArray()) {
			bnd = bnd.getElementType();
		}

		if (bnd.isPrimitive()) {
			return ensureFamixPrimitiveType(bnd, name);
		}

		if (bnd.isEnum()) {
			return ensureFamixEnum(bnd, name, owner);
		}
		
		if (bnd.isRawType() || bnd.isGenericType()) {
			return ensureFamixClass(bnd.getErasure(), name, owner, /*isGeneric*/true);
		}

		if (bnd.isParameterizedType()) {
			return ensureFamixParameterizedType(bnd, name, /*generic*/null, ctxt);
		}

		if (bnd.isAnnotation()) {
			return ensureFamixAnnotationType(bnd, name, owner);
		}

		// it seems wise to test isClass after isGenericType, isParameterizedType, ... ? 
		if (bnd.isClass() || bnd.isInterface()) {
			return ensureFamixClass(bnd, name, owner, /*isGeneric*/false);
		}

		if (name == null) {
			name = bnd.getName();
		}
		
		if (owner == null) {
			owner = ensureOwner(bnd);
		}

		if (bnd.isTypeVariable() ) {
			if (owner instanceof ParameterizableClass) {
				fmx = super.ensureFamixParameterType(bnd, name, owner);
			}
			else {
				// a type defined for a method parameter or return type
				fmx = super.ensureFamixType(bnd, name, owner);
			}			
			return fmx;
		}

		return super.ensureFamixType(bnd, name, owner);
	}

	/**
	 * Returns a Famix Class associated with the ITypeBinding.
	 * The Entity is created if it does not exist.<br>
	 * Params: see {@link Dictionary#ensureFamixClass(Object, String, ContainerEntity)}.
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public fr.inria.verveine.core.gen.famix.Class ensureFamixClass(ITypeBinding bnd, String name, ContainerEntity owner, boolean isGeneric) {
		fr.inria.verveine.core.gen.famix.Class fmx = null;

		// if Binding is null, we can only rely on provided parameters, so lets do it now and return
		if (bnd == null) {
			if (name == null) {
				return null;
			}
			if (owner == null) {
				owner = ensureFamixNamespaceDefault();
			}
			
			if (isGeneric) {
				return super.ensureFamixParameterizableClass(null, name, owner);
			}
			else {
				return super.ensureFamixClass(null, name, owner);
			}
		}

		// --------------- some special cases
		if (bnd.isArray()) {
			bnd = bnd.getElementType();
		}

		if (bnd.isPrimitive()) {
			// should have called ensureFamixPrimitiveType(bnd). Why are we here ?
			return null;
		}
		// for inner classes defined in generics !!!
		bnd = bnd.getErasure();
		
		fmx = (fr.inria.verveine.core.gen.famix.Class)getEntityByKey(bnd);	// to avoid useless computations if we can
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			if (! bnd.isAnonymous()) {
				name = bnd.getErasure().getName();  // for generics, will give the "core" type name, for normal type, won't change anything
			}
			else { // anonymous class
				if (bnd.getSuperclass() != null) {
					name = bnd.getSuperclass().getName();
				}
				if ( (name == null) || name.equals(OBJECT_NAME)) {
					ITypeBinding[] intfcs = bnd.getInterfaces();
					if ( (intfcs != null) && (intfcs.length > 0) ) {
						name = bnd.getInterfaces()[0].getName();
					}
					else {
						name = "???";
					}
				}
				name = "anonymous(" + name + ")";
			}
		}

		if (name.equals(OBJECT_NAME)) { // TODO && owner == java.lang
			return ensureFamixClassObject(bnd);
		}

		// --------------- recover from name ?
		for (Type candidate : this.getEntityByName(fr.inria.verveine.core.gen.famix.Class.class, name)) {
			if ( checkAndMapClass(bnd, candidate) ) {
				fmx = (Class) candidate;
				break;
			}
		}

		// --------------- owner
		if (owner == null) {
			owner = ensureOwner(bnd);
		}

		// --------------- superclasses (including interfaces)
		Collection<Type> sups = new LinkedList<Type>();
		if (! bnd.isInterface()) {
			ITypeBinding supbnd = bnd.getSuperclass();
			if (supbnd != null) {
				sups.add(ensureFamixType(supbnd, /*name*/null, /*owner*/null, /*ctxt*/null));
			}
			else {
				sups.add( ensureFamixClassObject(null));
			}
		}
		for (ITypeBinding intbnd : bnd.getInterfaces()) {
			sups.add( ensureFamixType(intbnd, /*name*/null, /*owner*/null, /*ctxt*/owner));
		}

		// ---------------- create 
		if (fmx == null) {
			if (isGeneric) {
				fmx = super.ensureFamixParameterizableClass(bnd, name, owner);
			}
			else {
				fmx = super.ensureFamixClass(bnd, name, owner);
			}
		}
		
		if (fmx!=null) {
			// we just created it or it was not bound, so we make sure it has the right information in it
			fmx.setIsInterface(bnd.isInterface());
			Inheritance lastInheritance = null;
			for (Type sup : sups) {
				lastInheritance = ensureFamixInheritance(sup, fmx, lastInheritance);
			}
			setNamedEntityModifiers(fmx, bnd.getDeclaredModifiers());
			if (fmx.getIsAbstract()) {
				// don't know why there must be two different ways to mark abstract classes !!! But this is a pain!
				fmx.addModifiers("abstract");
			}
		}
	
		return fmx;
	}

	/**
	 * Ensures a famix entity for a owner that can be a method, a class or a namespace
	 * @param bnd -- binding for the owned entity
	 * @return a famix entity for the owner
	 */
	private ContainerEntity ensureOwner(ITypeBinding bnd) {
		ContainerEntity owner = null;
		IMethodBinding parentMtd = bnd.getDeclaringMethod();
		if (parentMtd != null) {
			owner = this.ensureFamixMethod(parentMtd, /*name*/null, /*paramTypes*/(Collection<String>)null, /*retTyp*/null, /*owner*/null);  // cast needed to desambiguate the call
		}
		else {
			ITypeBinding parentClass = bnd.getDeclaringClass();
			if (parentClass != null) {
				Type tmpOwn = this.ensureFamixType(parentClass, /*name*/null, /*owner*/null, /*ctxt*/null);
				if (tmpOwn instanceof ParameterizedType) {
					owner = ((ParameterizedType) tmpOwn).getParameterizableClass(); 
				}
				else {
					owner = tmpOwn;
				}
			}
			else {
				IPackageBinding parentPckg = bnd.getPackage();
				if (parentPckg != null) {
					owner = this.ensureFamixNamespace(parentPckg, null);
				}
				else {
					owner = this.ensureFamixNamespaceDefault();
				}
			}
		}
		return owner;
	}

	public ParameterizedType ensureFamixParameterizedType(ITypeBinding bnd, String name, ParameterizableClass generic, ContainerEntity owner) {
			ParameterizedType fmx = null;

		// if Binding is null, we can only rely on provided parameters, so lets do it now and return
		if (bnd == null) {
			if (name == null) {
				return null;
			}
			return super.ensureFamixParameterizedType(null, name, generic, owner);
		}

		fmx = (ParameterizedType)getEntityByKey(bnd);	// to avoid useless computations if we can
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			name = bnd.getName();
		}
		// remove parameter types from name
		// could also use "bnd.getErasure().getName()"
		int i = name.indexOf('<');
		if (i > 0) {
			name = name.substring(0, i);
		}

		// --------------- owner
		if (owner == null) {
			owner = ensureOwner(bnd);
		}

		// --------------- generic
		if (generic == null) {
//			System.out.println("ensureParameterized, trying to recover generic: "+name);
			generic = (ParameterizableClass) ensureFamixClass(bnd.getErasure(), name, /*owner*/null, /*isGeneric*/true);
		}

		// --------------- recover from name ?
		for (ParameterizedType candidate : getEntityByName(ParameterizedType.class, name) ) {
			if ( checkAndMapType(bnd, candidate) ) {
				fmx = candidate;
				break;
			}
		}

		if (fmx == null) {
			fmx = super.ensureFamixParameterizedType(bnd, name, generic, owner);
		}

		return fmx;
	}

	public PrimitiveType ensureFamixPrimitiveType(ITypeBinding bnd, String name) {
		if (name == null) {
			if (bnd == null) {
				return null;
			}
			else {
				name = bnd.getName();
			}
		}
		return super.ensureFamixPrimitiveType(bnd, name);
	}

	public fr.inria.verveine.core.gen.famix.Enum ensureFamixEnum(ITypeBinding bnd, String name, ContainerEntity owner) {
		fr.inria.verveine.core.gen.famix.Enum fmx = null;

		// if Binding is null, we can only rely on provided parameters, so lets do it now and return
		if (bnd == null) {
			if (name == null) {
				return null;
			}
			return super.ensureFamixEnum(null, name, owner);
		}

		fmx = (fr.inria.verveine.core.gen.famix.Enum)getEntityByKey(bnd);	// to avoid useless computations if we can
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			name = bnd.getName();
		}

		// --------------- owner
		if (owner == null) {
			owner = ensureOwner(bnd);
		}

		// --------------- recover from name ?
		for (fr.inria.verveine.core.gen.famix.Enum candidate : getEntityByName(fr.inria.verveine.core.gen.famix.Enum.class, name) ) {
			if ( checkAndMapType(bnd, candidate) ) {
				fmx = candidate;
				break;
			}
		}

		if (fmx == null) {
			fmx = super.ensureFamixEnum(bnd, name, owner);
		}
		
		if (fmx!=null) {
			// we just created it or it was not bound, so we make sure it has the right information in it
			setNamedEntityModifiers(fmx, bnd.getModifiers());
		}

		return fmx;
	}

	public EnumValue ensureFamixEnumValue(IVariableBinding bnd,	String name, Enum owner) {
		EnumValue fmx = null;

		// if Binding is null, we can only rely on provided parameters, so lets do it now and return
		if (bnd == null) {
			if (name == null) {
				return null;
			}
			if (owner == null) {
				return null;  // what would be the interest of creating an EnumValue without a declaring Enum type? 
			}
			return super.ensureFamixEnumValue(null, name, owner);
		}

		fmx = (EnumValue)getEntityByKey(bnd);	// to avoid useless computations if we can
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			name = bnd.getName();
		}

		// --------------- owner
		if (owner == null) {
			owner = ensureFamixEnum(bnd.getDeclaringClass(), null, null);
		}
		
		// --------------- recover from name ?
		for (EnumValue candidate : getEntityByName(EnumValue.class, name) ) {
			if ( checkAndMapVariable(bnd, candidate) ) {
				fmx = candidate;
				break;
			}
		}
		if (fmx == null) {
			fmx = super.ensureFamixEnumValue(bnd, name, owner);
		}
		
		if (fmx!=null) {
			// we just created it or it was not bound, so we make sure it has the right information in it
			fmx.setParentEnum(owner);
		}

		return fmx;
	}

	public AnnotationType ensureFamixAnnotationType(ITypeBinding bnd, String name, ContainerEntity owner) {
		AnnotationType fmx = null;

		// if Binding is null, we can only rely on provided parameters, so lets do it now and return
		if (bnd == null) {
			if (name == null) {
				return null;
			}
			return super.ensureFamixAnnotationType(null, name, owner);
		}

		fmx = (AnnotationType)getEntityByKey(bnd);	// to avoid useless computations if we can
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			name = bnd.getName();
		}

		// --------------- owner
		if (owner == null) {
			IPackageBinding parentPckg = bnd.getPackage();
			if (parentPckg != null) {
				owner = this.ensureFamixNamespace(parentPckg, null);
			}
			else {
				owner = this.ensureFamixNamespaceDefault();
			}
		}

		// --------------- recover from name ?
		for (AnnotationType candidate : getEntityByName(AnnotationType.class, name) ) {
			if ( checkAndMapType(bnd, candidate) ) {
				fmx = candidate;
				break;
			}
		}

		if (fmx == null) {
			fmx = super.ensureFamixAnnotationType(bnd, name, owner);
		}
		
		if (fmx!=null) {
			// we just created it or it was not bound, so we make sure it has the right information in it
			setNamedEntityModifiers(fmx, bnd.getModifiers());
		}

		return fmx;
	}

	public AnnotationTypeAttribute ensureFamixAnnotationTypeAttribute(IMethodBinding bnd, String name, AnnotationType owner) {
		AnnotationTypeAttribute fmx = null;

		// if Binding is null, we can only rely on provided parameters, so lets do it now and return
		if (bnd == null) {
			if (name == null) {
				return null;
			}
			return super.ensureFamixAnnotationTypeAttribute(null, name, owner);
		}

		fmx = (AnnotationTypeAttribute)getEntityByKey(bnd);	// to avoid useless computations if we can
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			name = bnd.getName();
		}

		// --------------- owner
		if (owner == null) {
			ITypeBinding parentType = bnd.getDeclaringClass();
			if (parentType != null) {
				owner = this.ensureFamixAnnotationType(parentType, null, null);
			}
		}

		// --------------- recover from name ?
		for (AnnotationTypeAttribute candidate : getEntityByName(AnnotationTypeAttribute.class, name) ) {
			if ( checkAndMapMethod(bnd, candidate) ) {
				fmx = candidate;
				break;
			}
		}

		if (fmx == null) {
			fmx = super.ensureFamixAnnotationTypeAttribute(bnd, name, owner);
		}
		
		if (fmx!=null) {
			// we just created it or it was not bound, so we make sure it has the right information in it
			setNamedEntityModifiers(fmx, bnd.getModifiers());
		}

		return fmx;
	}

	/**
	 * Adds possible annotation instances to a Famix NamedEntity with the given binding
	 * @param bnd
	 * @param fmx
	 */
	public void addFamixAnnotationInstances(IBinding bnd, NamedEntity fmx) {
		if (bnd != null) {
			for (IAnnotationBinding annBnd : bnd.getAnnotations()) {
				AnnotationType annType = ensureFamixAnnotationType(annBnd.getAnnotationType(), null, null);

				Collection<AnnotationInstanceAttribute> annAtts = new ArrayList<AnnotationInstanceAttribute>(); 
				for (IMemberValuePairBinding annPV : annBnd.getDeclaredMemberValuePairs()) {
					annAtts.add( createFamixAnnotationInstanceAttribute(ensureFamixAnnotationTypeAttribute(annPV.getMethodBinding(), annPV.getName(), annType), annPV.getValue().toString()));
				}

				super.addFamixAnnotationInstance(fmx, annType, annAtts);
			}
		}
	}

	public ParameterType ensureFamixParameterType(ITypeBinding bnd,	String name, ParameterizableClass owner) {
		ParameterType fmx = null;

		// if Binding is null, we can only rely on provided parameters, so lets do it now and return
		if (bnd == null) {
			if (name == null) {
				return null;
			}
			if (owner == null) {
				return super.ensureFamixParameterType(null, name, null);
			}
			
			return super.ensureFamixParameterType(null, name, owner);
		}

		fmx = (ParameterType)getEntityByKey(bnd);	// to avoid useless computations if we can
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			name = bnd.getName();
		}

		// --------------- owner
		if (owner == null) {
			ITypeBinding parentClass = bnd.getDeclaringClass();
			if (parentClass != null) {
				owner = (ParameterizableClass) this.ensureFamixType(parentClass, /*name*/null, /*owner*/null, /*ctxt*/null);  // isGeneric=true
			}
		}

		// --------------- recover from name ?
		for (Type candidate : this.getEntityByName(Type.class, name)) {
			if ( checkAndMapType(bnd, candidate) ) {
				fmx = (ParameterType) candidate;
				break;
			}
		}
		if (fmx == null) {
			fmx = super.ensureFamixParameterType(bnd, name, owner);
		}

		return fmx;
	}

	/**
	 * Checks whether the existing unmapped Famix Namespace matches the binding.
	 * Checks that the candidate has the same name as the JDT bound package, and checks recursively that owners also match.
	 * @param bnd -- a JDT binding that we are trying to match to the candidate
	 * @param candidate -- a Famix Entity
	 * @return whether the binding matches the candidate (if <b>true</b>, the mapping is recorded)
	 */
	private boolean checkAndMapNamespace(IPackageBinding bnd, Namespace candidate) {
		if (! bnd.getName().equals(candidate.getName())) {
			return false;
		}

		NamedEntity bound = getEntityByKey(bnd); 
		if (bound == candidate) {
			return true;
		}
		else if (bound != null) {
			// already bound to something else
			// May be should continue to see if we need to merge two FamixEntities representing the same thing .... ?
			// Not sure it does actually happen
			return false;
		}
		else if (mapToKey.containsValue(candidate)) {
			// candidate already bound, and not to this binding
			return false;
		}
		
		// names are equals and bnd is not mapped, so let's do it
		mapToKey.put(bnd, candidate);
		return true;
	}

	/**
	 * Checks whether the existing unmapped Famix Type matches the binding.
	 * Checks that the candidate has the same name as the JDT bound type, and checks recursively that owners also match.
	 * We also check that the actual class of the candidate matches (can be a sub-class of FamixType). 
	 * @param bnd -- a JDT binding that we are trying to match to the candidate
	 * @param candidate -- a Famix NamedEntity (Class, Type, PrimitiveType, Enum, AnnotationType)
	 * @return whether the binding matches the candidate (if <b>true</b>, the mapping is recorded)
	 */
	private boolean checkAndMapType(ITypeBinding bnd, NamedEntity candidate) {
		if (bnd.isArray()) {
			bnd = bnd.getElementType();
		}

		String bndName;
		if (bnd.isParameterizedType()) {
			bndName = bnd.getErasure().getName();
		}
		else {
			bndName = bnd.getName();
		}
		if (! bndName.equals(candidate.getName())) {
			return false;
		}

		NamedEntity bound = getEntityByKey(bnd); 
		if (bound == candidate) {
			return true;
		}
		else if (bound != null) {
			// already bound to something else
			// May be should continue to see if we need to merge two FamixEntities representing the same thing .... ?
			// Not sure it does actually happen
			return false;
		}
		else if (mapToKey.containsValue(candidate)) {
			// candidate already bound, and not to this binding
			return false;
		}

		if ( bnd.isPrimitive() && (candidate instanceof PrimitiveType) ) {
			// names are equal so it's OK
			mapToKey.put(bnd, candidate);
			return true;
		}

		if (bnd.isAnnotation() && (candidate instanceof AnnotationType) ) {
			if (checkAndMapNamespace(bnd.getPackage(), (Namespace) candidate.getBelongsTo())) {
				mapToKey.put(bnd, candidate);
				return true;
			}
			else {
				return false;
			}
		}

		if (bnd.isParameterizedType() && (candidate instanceof ParameterizedType)) {
			return checkAndMapTypeOwner(bnd, (Type) candidate);
		}

		if (bnd.isEnum() && (candidate instanceof Enum)) {
			return checkAndMapTypeOwner(bnd, (Type) candidate);
		}

		// Annotation are interfaces too, so we should check this one after isAnnotation
		if ( bnd.isClass() || bnd.isInterface() || bnd.isEnum() ) {
			return checkAndMapClass(bnd, (Type) candidate);
		}

		return false;
	}

	/**
	 * Checks whether the existing unmapped Famix Class (or Interface or Enum) matches the binding.
	 * Checks that the candidate has the same name as the JDT bound type, and checks recursively that owners also match.
	 * @param bnd -- a JDT binding that we are trying to match to the candidate
	 * @param candidate -- a Famix Entity
	 * @return whether the binding matches the candidate (if <b>true</b>, the mapping is recorded)
	 */
	private boolean checkAndMapClass(ITypeBinding bnd, Type candidate) {
		if (! bnd.getName().equals(candidate.getName())) {
			return false;
		}
		
		NamedEntity bound = getEntityByKey(bnd); 
		if (bound == candidate) {
			if ( (bnd.isClass() || bnd.isInterface()) && (! (candidate instanceof fr.inria.verveine.core.gen.famix.Class)) ) {
				System.err.println("JavaDictionary.checkAndMapClass() found a FamixType that should be a FamixClass: "+candidate.getName());
				return false;
			}
			else if ( bnd.isEnum() && (! (candidate instanceof fr.inria.verveine.core.gen.famix.Enum)) ) {
				System.err.println("JavaDictionary.checkAndMapClass() found a FamixType that should be a FamixEnum: "+candidate.getName());
				return false;
			}
			else {
				return true;
			}
		}
		else if (bound != null) {
			// already bound to something else
			// May be should continue to see if we need to merge two FamixEntities representing the same thing .... ?
			// Not sure it does actually happen
			return false;
		}
		else if (mapToKey.containsValue(candidate)) {
			// candidate already bound, and not to this binding
			return false;
		}

			return checkAndMapTypeOwner(bnd, candidate);
	}

	/**
	 * Checks whether the existing unmapped Famix "Method" matches the binding.
	 * Checks that the candidate has the same name and same signature as the JDT bound method, and checks recursively that owners also match.
	 * Note that AnnotationTypeAttribute are treated as methods by JDT, so they are checked here.
	 * @param bnd -- a JDT binding that we are trying to match to the candidate
	 * @param candidate -- a Famix Entity (regular Method or AnnotationTypeAttribute)
	 * @return whether the binding matches the candidate (if <b>true</b>, the mapping is recorded)
	 */
	private boolean checkAndMapMethod(IMethodBinding bnd, NamedEntity candidate) {
		if (! bnd.getName().equals(candidate.getName())) {
			return false;
		}

		NamedEntity bound = getEntityByKey(bnd); 
		if (bound == candidate) {
			return true;
		}
		else if (bound != null) {
			// already bound to something else
			// May be should continue to see if we need to merge two FamixEntities representing the same thing .... ?
			// Not sure it does actually happen
			return false;
		}
		else if (mapToKey.containsValue(candidate)) {
			// candidate already bound, and not to this binding
			return false;
		}

		// for methods, the name is not enough, we must test the signature also ...
		// for AnnotationTypeAttribute, we don't need this
		if (candidate instanceof Method) {
			String sig = bnd.getName() + "(";
			boolean first = true;
			for (ITypeBinding parBnd : bnd.getParameterTypes()) {
				if (! first) {
					sig += "," + parBnd.getName();
				}
				else {
					sig += parBnd.getName();
					first = false;
				}
			}
			sig += ")";
			if (! ((Method) candidate).getSignature().equals(sig)) {
				return false;
			}

			// ... and the signature should include the return type
			if (bnd.isConstructor()) {
				if ( ((Method) candidate).getDeclaredType() != null) {
					return false;
				}
			}
			else {
				if ( ((Method) candidate).getDeclaredType() == null) {
					return false;
				}
				if (! checkAndMapType(bnd.getReturnType(), ((Method) candidate).getDeclaredType()) ) {
					return false;
				}
			}
		}

		// finally let's check the owners
		ITypeBinding ownerBnd = bnd.getDeclaringClass();
		NamedEntity candidateOwner = candidate.getBelongsTo();
		if (checkAndMapType(ownerBnd, (Type)candidateOwner)) {
			mapToKey.put(bnd, candidate);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Checks whether the existing unmapped Famix "Variable" (Attribute, Parameter, ...) matches the binding.
	 * Checks that the candidate has the same name as the JDT bound variable, and checks recursively that owners also match.
	 * The Famix candidate is a NamedEntity and not a StructuralEntity to allow dealing with Famix EnumValue that JDT treats as variables
	 * @param bnd -- a JDT binding that we are trying to match to the candidate
	 * @param candidate -- a Famix Entity (a StructuralEntity or an EnumValue)
	 * @return whether the binding matches the candidate (if <b>true</b>, the mapping is recorded)
	 */
	private boolean checkAndMapVariable(IVariableBinding bnd, NamedEntity candidate) {
		if (! bnd.getName().equals(candidate.getName())) {
			return false;
		}

		NamedEntity bound = (NamedEntity)getEntityByKey(bnd); 
		if (bound == candidate) {
			return true;
		}
		else if (bound != null) {
			return false;
		}
		else if (mapToKey.containsValue(candidate)) {
			// candidate already bound, and not to this binding
			return false;
		}

		ContainerEntity candidateOwner = candidate.getBelongsTo();
		IMethodBinding methBnd = bnd.getDeclaringMethod();
		if ( (methBnd != null) && (candidateOwner instanceof Method) ) {
			if ( checkAndMapMethod(methBnd, (Method)candidateOwner) ) {
				mapToKey.put(bnd, candidate);
				return true;
			}
			else {
				return false;
			}
		}

		ITypeBinding typBnd = bnd.getDeclaringClass();
		// in case of anArray.length ...
		if ( (typBnd == null) && (candidateOwner.getName().equals(JavaDictionary.ARRAYS_NAME)) ) {
			mapToKey.put(bnd, candidate);
			return true;
		}
		else if ( (candidateOwner instanceof Type) &&
				  (checkAndMapType(typBnd, (Type)candidateOwner)) ) {
			mapToKey.put(bnd, candidate);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Checks whether the existing unmapped Famix Type's parent (or owner) matches the binding's owner.
	 * Checks that the candidate has the same name as the JDT bound type, and checks recursively that owners also match.
	 * @param bnd -- a JDT binding whose owner we are trying to match to the candidate's owner
	 * @param candidate -- a Famix Entity
	 * @return whether we found a match (if <b>true</b>, the mapping is recorded)
	 */
	private boolean checkAndMapTypeOwner(ITypeBinding bnd, Type candidate) {
		// we don't check the names because we are only interested in the Type's owner

		// owner is a Method?
		ContainerEntity candidateOwner = candidate.getBelongsTo();
		IMethodBinding methBnd = bnd.getDeclaringMethod(); // for classes, can other types be declared in methods?
		if ( (methBnd != null) && (candidateOwner instanceof Method) ) {
			if ( checkAndMapMethod(methBnd, (Method)candidateOwner) ) {
				mapToKey.put(bnd, candidate);
				return true;
			}
			else {
				return false;
			}
		}

		// owner is a class ?
		ITypeBinding classBnd = bnd.getDeclaringClass();
		if ( (classBnd != null) && (candidateOwner instanceof fr.inria.verveine.core.gen.famix.Class) ) {
			if ( checkAndMapClass(classBnd, (Type)candidateOwner) ) {
				mapToKey.put(bnd, candidate);
				return true;
			}
			else {
				return false;
			}
		}

		// owner must be a package
		IPackageBinding pckgBnd = bnd.getPackage();
		if ( (candidateOwner instanceof Namespace) &&
			 (checkAndMapNamespace(pckgBnd, (Namespace)candidateOwner)) ) {
			mapToKey.put(bnd, candidate);
			return true;
		}
		else {
			return false;
		}

	}

	/**
	 * Returns a Famix Method associated with the IMethodBinding. The Entity is created if it does not exist.
	 * The Entity is created if it does not exist.
	 * Params: see {@link Dictionary#ensureFamixMethod(Object, String, String, Type, Type)}.
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public Method ensureFamixMethod(IMethodBinding bnd, String name, Collection<String> paramTypes, Type ret, Type owner) {
		Method fmx = null;
		String sig;
		boolean first;
		boolean delayedRetTyp;

		// signature is dealt in two places
		// here we try to use the parameter of ensureFamixMethod to compute the list of parameter types of the FamixMethod
		// if it does not work and binding is not null, we will have another chance to compute this list later
		sig = "(";
		first = true;
		if (paramTypes != null) {
			for (String t : paramTypes) {
				if (! first) {
					sig += "," + t;
				}
				else {
					sig += t;
					first = false;
				}
			}
			
		}
		sig += ")";

		// if Binding is null, we can only rely on provided parameters, so lets do it now and return
		if (bnd == null) {
			if (name == null) {
				return null;
			}
			if (paramTypes == null) {
				sig = name + "(???)";
			}
			else {
				sig = name + sig;
			}
			if (owner == null) {
				owner= ensureFamixClassStubOwner();
			}
			return super.ensureFamixMethod(null, name, sig, ret, owner);
		}

		if (bnd.isParameterizedMethod()) {
			bnd = bnd.getMethodDeclaration();
		}

		fmx = (Method)getEntityByKey(bnd);	// to avoid useless computations if we can
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			name = bnd.getName();
		}

		// --------------- signature
		if (paramTypes == null) {
			sig = "(";
			first = true;
			for (ITypeBinding parBnd : bnd.getParameterTypes()) {
				if (! first) {
					sig += "," + parBnd.getName();
				}
				else {
					sig += parBnd.getName();
					first = false;
				}
			}
			sig += ")";
		}
		sig = name + sig;

		// --------------- return type
		delayedRetTyp = false;
		ITypeBinding retTypBnd = null;
		if ( (ret == null) && (! bnd.isConstructor()) ) {
			// must create the return type
			// but for method like "<T> T mtd()", i.e. T belongs to mtd and mtd returns T
			// we need T to create the method and the method to create T ...
			// so we need to test the situation and deal with it
			retTypBnd = bnd.getReturnType();
			if (retTypBnd.isArray()) {
				retTypBnd = retTypBnd.getElementType();
			}
			if ( (retTypBnd != null) && retTypBnd.isTypeVariable() && (retTypBnd.getDeclaringMethod() == bnd) ) {
				ret = null;
				delayedRetTyp = true;
			}
			else {
				ret = this.ensureFamixType(retTypBnd, /*name*/null, /*owner*/null, /*ctxt*/owner);
			}
		}
		// else leave it to null ...

		// --------------- owner
		if (owner == null) {
			ITypeBinding classBnd = bnd.getDeclaringClass();
			if (classBnd != null) {
				Type tmpOwn = ensureFamixType(classBnd, /*name*/null, /*owner*/null, /*ctxt*/null);
				if (tmpOwn instanceof ParameterizedType) {
					owner = ((ParameterizedType) tmpOwn).getParameterizableClass();
				}
				else {
					owner = tmpOwn; 
				}
			}
		}
		
		// --------------- recover from name ?
		for (Method candidate : this.getEntityByName(Method.class, name)) {
			if ( checkAndMapMethod(bnd, candidate) ) {
				fmx = candidate;
				break;
			}
		}
		if (fmx == null) {
			fmx = ensureFamixMethod(bnd, name, sig, ret, owner);
		}
		else {
			// apparently we just recovered an unbound method, so we make sure it has the right information in it
			fmx.setDeclaredType(ret);
			fmx.setSignature(sig);
		}

		if (fmx!=null) {
			setNamedEntityModifiers(fmx, bnd.getModifiers());
			if (delayedRetTyp) {
				fmx.setDeclaredType(this.ensureFamixType(retTypBnd, /*name*/null, /*owner*/fmx, /*ctxt*/owner));
			}
		}

		return fmx;
	}

	/**
	 * Returns a Famix Attribute associated with the IVariableBinding.
	 * The Entity is created if it does not exist.<br>
	 * Params: see {@link Dictionary#ensureFamixAttribute(Object, String, Type, Type)}.
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public Attribute ensureFamixAttribute(IVariableBinding bnd, String name, Type type, Type owner) {
		Attribute fmx = null;

		// if Binding is null, we can only rely on provided parameters, so lets do it now and return
		if (bnd == null) {
			if (name == null) {
				return null;
			}
			if (owner == null) {
				return null;  // what would be the interest of creating an attribute for which we ignore the declaring class? 
			}
			return super.ensureFamixAttribute(null, name, type, owner);
		}

		fmx = (Attribute)getEntityByKey(bnd);	// to avoid useless computations if we can
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			name = bnd.getName();
		}

		// --------------- return type
		if (type == null) {
			type = this.ensureFamixType(bnd.getType(), null, null, owner);
		}

		// --------------- owner
		if (owner == null) {
			ITypeBinding classBnd = bnd.getDeclaringClass();
			if (classBnd != null) {
				Type tmpOwn = ensureFamixType(classBnd, /*name*/null, /*owner*/null, /*ctxt*/null);
				if (tmpOwn instanceof ParameterizedType) {
					owner = ((ParameterizedType) tmpOwn).getParameterizableClass(); 
				}
				else {
					owner = tmpOwn;
				}
			}
		}
		
		// --------------- recover from name ?
		for (Attribute candidate : getEntityByName(Attribute.class, name) ) {
			if ( checkAndMapVariable(bnd, candidate) ) {
				fmx = candidate;
				break;
			}
		}
		if (fmx == null) {
			fmx = super.ensureFamixAttribute(bnd, name, type, owner);
		}
		
		if (fmx!=null) {
			// we just created it or it was not bound, so we make sure it has the right information in it
			fmx.setParentType(owner);
			fmx.setDeclaredType(type);	
			setNamedEntityModifiers(fmx, bnd.getModifiers());
		}

		return fmx;
	}

	/** Sets the modifiers (abstract, public, ...) of a FamixNamedEntity
	 * @param fmx -- the FamixNamedEntity
	 * @param mod -- a description of the modifiers as understood by org.eclipse.jdt.core.dom.Modifier
	 */
	private void setNamedEntityModifiers(NamedEntity fmx, int mod) {
		if (Modifier.isAbstract(mod)) {
			// don't know why there are two different ways to mark abstract classes !!!
			// But this is a pain!
			fmx.addModifiers("abstract");
		}
		fmx.setIsAbstract(new Boolean(Modifier.isAbstract(mod)));
		fmx.setIsFinal(new Boolean(Modifier.isFinal(mod)));
		fmx.setIsPrivate(new Boolean(Modifier.isPrivate(mod)));
		fmx.setIsProtected(new Boolean(Modifier.isProtected(mod)));
		fmx.setIsPublic(new Boolean(Modifier.isPublic(mod)));
	}

	/**
	 * Returns a Famix Parameter associated with the IVariableBinding.
	 * The Entity is created if it does not exist.<br>
	 * Params: see {@link Dictionary#ensureFamixParameter(Object, String, Type, fr.inria.verveine.core.gen.famix.BehaviouralEntity)}.
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public Parameter ensureFamixParameter(IVariableBinding bnd, String name, Type typ, Method owner) {
		Parameter fmx = null;

		if (bnd == null) {
			if (name == null) {
				return null;
			}
			if (owner == null) {
				owner = ensureFamixStubMethod("<"+name+"_owner>");
			}
			return super.createFamixParameter(null, name, typ, owner);
		}

		fmx = (Parameter)getEntityByKey(bnd);	// to avoid useless computations if we can
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			name = bnd.getName();
		}

		// --------------- return type
		if (typ == null) {
			typ = this.ensureFamixType(bnd.getType(), null, null, owner.getParentType());  // context of the parameter def = the class definition
		}

		// --------------- owner
		if (owner == null) {
			owner = ensureFamixMethod(bnd.getDeclaringMethod(), null, (Collection<String>)null, null, null);  // cast needed to desambiguate the call
		}
		
		// --------------- recover from name ?
		for (Parameter candidate : getEntityByName(Parameter.class, name) ) {
			if ( checkAndMapVariable(bnd, candidate) ) {
				fmx = candidate;
				break;
			}
		}
		if (fmx == null) {
			fmx = super.createFamixParameter(bnd, name, typ, owner);
		}
		
		if (fmx != null) {
			// we just created it or it was not bound, so we make sure it has the right information in it
			fmx.setParentBehaviouralEntity(owner);
			fmx.setDeclaredType(typ);	
		}

		return fmx;
	}
	
	/**
	 * Returns a Famix LocalVariable associated with the IVariableBinding.
	 * The Entity is created if it does not exist.<br>
	 * Params: see {@link Dictionary#ensureFamixLocalVariable(Object, String, Type, fr.inria.verveine.core.gen.famix.BehaviouralEntity)}
	 * @return the Famix Entity found or created. May return null if <b>bnd</b> and <b>name</b> are null, or <b>bnd</b> and <b>owner</b> are null, or in case of a Famix error
	 */
	public LocalVariable ensureFamixLocalVariable(IVariableBinding bnd, String name, Type typ, Method owner) {
		LocalVariable fmx = null;

		if (bnd == null) {
			if (name == null) {
				return null;
			}
			if (owner == null) {
				return null;  // what would be the interest of a local variable for which we ignore the declaring method?
			}
			return super.ensureFamixLocalVariable(null, name, typ, owner);
		}

		fmx = (LocalVariable)getEntityByKey(bnd);	// to avoid useless computations if we can
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			name = bnd.getName();
		}

		// --------------- return type
		if (typ == null) {
			typ = this.ensureFamixType(bnd.getType(), null, null, owner);
		}

		// --------------- owner
		if (owner == null) {
			owner = ensureFamixMethod(bnd.getDeclaringMethod(), null, (Collection<String>)null, null, null);  // cast needed to desambiguate the call
		}
		
		// --------------- recover from name ?
		for (LocalVariable candidate : getEntityByName(LocalVariable.class, name) ) {
			if ( checkAndMapVariable(bnd, candidate) ) {
				fmx = candidate;
				break;
			}
		}
		if (fmx == null) {
			fmx = super.ensureFamixLocalVariable(bnd, name, typ, owner);
		}
		
		if (fmx != null) {
			// we just created it or it was not bound, so we make sure it has the right information in it
			fmx.setParentBehaviouralEntity(owner);
			fmx.setDeclaredType(typ);	
		}

		return fmx;
	}
	
	/**
	 * Creates and returns a Famix UnknownVariable.
	 */
	public UnknownVariable createFamixUnknownVariable(Type type, String name) {
//		System.err.println("TRACE -- createFamixUnknownVariable: "+name);
		UnknownVariable fmx = (UnknownVariable) createFamixEntity(UnknownVariable.class, name);
		if (fmx!=null) {
			fmx.setDeclaredType(type);
		}
		return fmx;
	}

	public Comment createFamixComment(org.eclipse.jdt.core.dom.Comment jdoc, NamedEntity fmx) {
		Comment cmt = null;
		if (jdoc != null) {
			cmt = createFamixComment(jdoc.toString(), fmx);
			addSourceAnchor(cmt, jdoc);
		}
		return cmt;
	}

	/**
	 * Adds location information to a Famix Entity.
	 * Location informations are: <b>name</b> of the source file and <b>line</b> position in this file. They are found in the JDT ASTNode: ast.
	 * This method also creates some basic links between the entity and others (e.g. declaring container, return type, ...)
	 * @param fmx -- Famix Entity to add the anchor to
	 * @param ast -- JDT ASTNode, where the information are extracted
	 * @return the Famix SourceAnchor added to fmx. May be null in case of incorrect parameter ('fmx' or 'ast' == null) 
	 */
	public SourceAnchor addSourceAnchor(SourcedEntity fmx, ASTNode ast) {
		FileAnchor fa = null;
		
		if ( (fmx != null) && (ast != null) ) {
			// position in source file
			int beg = ast.getStartPosition();
			int end = beg + ast.getLength();
			// find source file
			while ( ! (ast instanceof CompilationUnit) ) {
				ASTNode tmp = ast.getParent();
				if ( (ast == null) || (tmp == ast) ) {
					// if we are here, then we reached the top node without finding a CompilationUnit. This should not happen
					return null;
				}
				else {
					ast = tmp;
				}
			}
			// now create the Famix SourceAnchor
			fa = new FileAnchor();
			fa.setFileName((String) ((CompilationUnit)ast).getProperty(SOURCE_FILENAME_PROPERTY));
			fa.setStartLine(((CompilationUnit)ast).getLineNumber(beg));
			fa.setEndLine(((CompilationUnit)ast).getLineNumber(end));
			fmx.setSourceAnchor(fa);
			famixRepo.add(fa);
		}
		
		return fa;
	}

	public Access fieldAccess(BehaviouralEntity accessor, Attribute accessed, boolean isWrite, Access lastAccess) {
		Access ret = null;
		// TODO could be accessing annotation type attribute
		if ( (accessor != null) && (accessed != null) ) {
			ret = super.addFamixAccess(accessor, accessed, isWrite, lastAccess);
		}
		return ret;
	}

	/**
	 * Creates or recovers a stub Famix Method
	 * @param name of the method
	 * @return the Famix Method
	 */
	public Method ensureFamixStubMethod(String name) {
		return ensureFamixMethod(null, name, (Collection<String>)null, null, ensureFamixClassStubOwner());  // cast needed to desambiguate the call
	}

	/**
	 * Creates or recovers a Famix Namespace for the package of Java class "Object" (i.e. "java.lang").
	 * Because "Object" is the root of the inheritance tree, it needs to be treated differently.
	 * @param bnd -- a potential binding for the "java.lang" package
	 * @return a Famix Namespace for "java.lang"
	 */
	public Namespace ensureFamixNamespaceJavaLang(IPackageBinding bnd) {
		Namespace fmx = this.ensureFamixNamespace(bnd, OBJECT_PACKAGE_NAME);

		return fmx;
	}

	/**
	 * Creates or recovers the Famix Class for "Object".
	 * @param bnd -- a potential binding for the java "Object" class
	 * @return a Famix class for "Object"
	 */
	public fr.inria.verveine.core.gen.famix.Class ensureFamixClassObject(ITypeBinding bnd) {
		fr.inria.verveine.core.gen.famix.Class fmx =  ensureFamixUniqEntity(fr.inria.verveine.core.gen.famix.Class.class, bnd, OBJECT_NAME);
		
		if (fmx != null) {
			fmx.setContainer( ensureFamixNamespaceJavaLang(null));
		}
		// Note: "Object" has no superclass

		return fmx;
	}

	public fr.inria.verveine.core.gen.famix.Class ensureFamixMetaClass(ITypeBinding bnd) {
		Namespace javaLang = ensureFamixNamespaceJavaLang( (bnd == null) ? null : bnd.getPackage());
		fr.inria.verveine.core.gen.famix.Class fmx =  ensureFamixClass(bnd, METACLASS_NAME, javaLang, /*isGeneric*/true);

		if ( (fmx != null) && (fmx.getSuperInheritances() == null) ) {
			ensureFamixInheritance(ensureFamixClassObject(null), fmx, null);
		}

		return fmx;
	}
	
	/**
	 * Creates or recovers the Famix Class that will own all stub methods (for which the real owner is unknown)
	 * @return a Famix class
	 */
	public fr.inria.verveine.core.gen.famix.Class ensureFamixClassStubOwner() {
		fr.inria.verveine.core.gen.famix.Class fmx = super.ensureFamixClassStubOwner();
		ensureFamixInheritance(ensureFamixClassObject(null), fmx, /*prev*/null);

		return fmx;
	}

	/**
	 * Creates or recovers the Famix Class for all arrays (<some-type> [])
	 * In java arrays or objects of special classes (i.e. "I[" for an array of int).
	 * JDT does not create a binding for these classes, so we create a stub one here. 
	 * @return a Famix class
	 */
	public fr.inria.verveine.core.gen.famix.Class ensureFamixClassArray() {
		fr.inria.verveine.core.gen.famix.Class fmx = ensureFamixUniqEntity(fr.inria.verveine.core.gen.famix.Class.class, null, ARRAYS_NAME);
		if (fmx != null) {
			ensureFamixInheritance(ensureFamixClassObject(null), fmx, /*prev*/null);
			fmx.setContainer( ensureFamixNamespaceDefault());
			
			fmx.setIsAbstract(Boolean.FALSE);
			fmx.setIsFinal(Boolean.FALSE);
			fmx.setIsInterface(Boolean.FALSE);
			fmx.setIsPrivate(Boolean.FALSE);
			fmx.setIsProtected(Boolean.FALSE);
			fmx.setIsPublic(Boolean.TRUE);
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

}