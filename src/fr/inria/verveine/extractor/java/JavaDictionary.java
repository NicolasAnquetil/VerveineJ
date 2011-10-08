package fr.inria.verveine.extractor.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMemberValuePairBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;

import ch.akuhn.fame.Repository;
import fr.inria.verveine.core.Dictionary;
import fr.inria.verveine.core.gen.famix.Access;
import fr.inria.verveine.core.gen.famix.AnnotationInstanceAttribute;
import fr.inria.verveine.core.gen.famix.AnnotationType;
import fr.inria.verveine.core.gen.famix.AnnotationTypeAttribute;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.BehaviouralEntity;
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
import fr.inria.verveine.core.gen.famix.StructuralEntity;
import fr.inria.verveine.core.gen.famix.Type;
import fr.inria.verveine.core.gen.famix.UnknownVariable;

/**
 * A {@link fr.inria.verveine.Dictionary} specialized for Java
 * @author anquetil
 */
public class JavaDictionary extends Dictionary<IBinding> {

	/**
	 * A property added to CompilationUnits to record the name of the source file they belong to.
	 * Used to create FileAnchors
	 */
	public static final String SOURCE_FILENAME_PROPERTY = "verveine-source-filename";

	public static final String OBJECT_NAME = "Object";
	public static final String METACLASS_NAME = "Class";
	public static final String OBJECT_PACKAGE_NAME = "java.lang";
	public static final String ARRAYS_NAME = "default[]";
	public static final String INIT_BLOCK_NAME = "<Initializer>";

	/**
	 * Result of utility methods for checking matching between two entities
	 */
	private enum CheckResult {
		MATCH, UNDECIDED, FAIL;
	}

  	public void mapKey(IBinding bnd, NamedEntity fmx) {
		super.mapEntityToKey(bnd, fmx);
	}

	/**
	 * @param famixRepo
	 */
	public JavaDictionary(Repository famixRepo) {
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

		if (name.equals("")) {
			return ensureFamixNamespaceDefault();
		}
		else {
			/* Note: Packages are created with their fully-qualified name to simplify recovering when we don't have a binding
			 * (for example when creating parent packages of a package we have a binding for).
			 * Because the preferred solution in Moose is to give their simple names to packages, they must be post-processed when
			 * all is said and done. */
			fmx = super.ensureFamixNamespace( bnd, name);
			String parentName = removeLastName(name);
			if (parentName.length() > 0) {
				parent = ensureFamixNamespace(null, parentName);
				// set the parentscope relationship
				if ( (parent != null) && (fmx != null) && (fmx.getParentScope() == null)) {
					parent.addChildScopes(fmx);
				}
			}
		}

		return fmx;
	}

	/**
	 * Recovers or creates a Famix Type (see also {@link Dictionary#ensureFamixType(Object, String, ContainerEntity, boolean)}
	 * @param bnd -- binding for the type to create
	 * @param name of the type
	 * @param owner of the type
	 * @param ctxt -- context of use of the type
	 * @param alwaysPersist -- whether the type is unconditionally persisted or if we should check
	 */
	public Type ensureFamixType(ITypeBinding bnd, String name, ContainerEntity owner, ContainerEntity ctxt, boolean alwaysPersist) {
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
				return this.ensureFamixParameterType(null, name, (ParameterizableClass) owner, alwaysPersist);
			}
			else {
				// impossible to decide whether to persist it or not. So we just hope for the best
				return super.ensureFamixType(null, name, owner, alwaysPersist);
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
			return this.ensureFamixPrimitiveType(bnd, name);
		}

		if (bnd.isEnum()) {
			return this.ensureFamixEnum(bnd, name, owner);
		}
		
		if (bnd.isRawType() || bnd.isGenericType()) {
			return this.ensureFamixClass(bnd.getErasure(), name, owner, /*isGeneric*/true, alwaysPersist);
		}

		if (bnd.isParameterizedType()) {
			return this.ensureFamixParameterizedType(bnd, name, /*generic*/null, ctxt, alwaysPersist);
		}

		if (bnd.isAnnotation()) {
			return this.ensureFamixAnnotationType(bnd, name, owner, alwaysPersist);
		}

		// it seems wise to test isClass after isGenericType, isParameterizedType, ... ? 
		if (bnd.isClass() || bnd.isInterface()) {
			return this.ensureFamixClass(bnd, name, owner, /*isGeneric*/false, alwaysPersist);
		}

		if (name == null) {
			name = bnd.getName();
		}
		
		if (owner == null) {
			owner = this.ensureOwner(bnd, alwaysPersist);
		}

		if (bnd.isTypeVariable() ) {
			if (owner instanceof ParameterizableClass) {
				fmx = super.ensureFamixParameterType(bnd, name, owner, alwaysPersist);
			}
			else {
				// a type defined for a method parameter or return type
				fmx = super.ensureFamixType(bnd, name, owner, alwaysPersist);
			}			
			return fmx;
		}

		return super.ensureFamixType(bnd, name, owner, alwaysPersist);
	}

	/**
	 * Returns a Famix Class associated with the ITypeBinding.
	 * The Entity is created if it does not exist.
	 * Params: see {@link Dictionary#ensureFamixClass(Object, String, ContainerEntity, boolean)}.
	 * @param alwaysPersist -- whether the type is unconditionally persisted or if we should check
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public fr.inria.verveine.core.gen.famix.Class ensureFamixClass(ITypeBinding bnd, String name, ContainerEntity owner, boolean isGeneric, boolean alwaysPersist) {
		fr.inria.verveine.core.gen.famix.Class fmx = null;

		// --------------- some special cases
		if (bnd!=null) {
			if (bnd.isArray()) {
				bnd = bnd.getElementType();
			}

			// for inner classes defined in generics !!! For others should not change anything
			bnd = bnd.getErasure();
		}

		// ---------------- to avoid useless computations if we can
		fmx = (fr.inria.verveine.core.gen.famix.Class)getEntityByKey(bnd);	
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			if (bnd == null) {
				return null;  // not much we can do
			}
			else if (! bnd.isAnonymous()) {
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

		// --------------- owner
		if (owner == null) {
			if (bnd == null) {
				owner = ensureFamixNamespaceDefault();
			}
			else {
				owner = ensureOwner(bnd, alwaysPersist);
			}
		}

		// --------------- recover from name ?
		for (fr.inria.verveine.core.gen.famix.Class candidate : this.getEntityByName(fr.inria.verveine.core.gen.famix.Class.class, name)) {
			if ( matchAndMapClass(bnd, name, owner, candidate) ) {
				fmx = candidate;
				break;
			}
		}

		// --------------- superclasses (including interfaces)
		Collection<Type> sups = new LinkedList<Type>();
		if (bnd != null) {
			if (! bnd.isInterface()) {
				ITypeBinding supbnd = bnd.getSuperclass();
				if (supbnd != null) {
					sups.add(ensureFamixType(supbnd, /*name*/null, /*owner*/null, /*ctxt*/null, alwaysPersist));
				}
				else {
					sups.add( ensureFamixClassObject(null));
				}
			}
			for (ITypeBinding intbnd : bnd.getInterfaces()) {
				sups.add( ensureFamixType(intbnd, /*name*/null, /*owner*/null, /*ctxt*/owner, alwaysPersist));
			}
		}

		// ---------------- create
		boolean persistIt = alwaysPersist || (! (owner instanceof Method));
		if (fmx == null) {
			if (isGeneric) {
				fmx = super.ensureFamixParameterizableClass(bnd, name, owner, persistIt);
			}
			else {
				fmx = super.ensureFamixClass(bnd, name, owner, /*alwaysPersist?*/persistIt);
			}
		}
		
		if (fmx!=null) {
			// we just created it or it was not bound, so we make sure it has the right information in it
			if (bnd != null) {
				fmx.setIsInterface( bnd.isInterface());
				setNamedEntityModifiers(fmx, bnd.getDeclaredModifiers());
				if (fmx.getIsAbstract()) {
					// don't know why there must be two different ways to mark abstract classes !!! But this is a pain!
					fmx.addModifiers("abstract");
				}
			}
			if (persistIt) {
				Inheritance lastInheritance = null;
				for (Type sup : sups) {
					lastInheritance = ensureFamixInheritance(sup, fmx, lastInheritance);
				}
			}
		}
	
		return fmx;
	}

	/**
	 * Ensures a famix entity for the owner of a binding.<br>
	 * This owner can be a method, a class or a namespace
	 * @param bnd -- binding for the owned entity
	 * @param persistIt  -- whether to persist or not the type
	 * @return a famix entity for the owner
	 */
	private ContainerEntity ensureOwner(ITypeBinding bnd, boolean persistIt) {
		ContainerEntity owner = null;
		IMethodBinding parentMtd = bnd.getDeclaringMethod();
		if (parentMtd != null) {
			owner = this.ensureFamixMethod(parentMtd, /*name*/null, /*paramTypes*/(Collection<String>)null, /*retTyp*/null, /*owner*/null, persistIt);  // cast needed to desambiguate the call
		}
		else {
			ITypeBinding parentClass = bnd.getDeclaringClass();
			if (parentClass != null) {
				Type tmpOwn = this.ensureFamixType(parentClass, /*name*/null, /*owner*/null, /*ctxt*/null, /*alwaysPersist?*/persistIt);
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

	/**
	 * e.g. see {@link JavaDictionary#ensureFamixClass}
	 */
	public ParameterizedType ensureFamixParameterizedType(ITypeBinding bnd, String name, ParameterizableClass generic, ContainerEntity owner, boolean alwaysPersist) {
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

		// --------------- owner
		if (owner == null) {
			if (bnd == null) {
				owner = ensureFamixNamespaceDefault();  // not really sure what to do here
			}
			else {
				owner = ensureOwner(bnd, alwaysPersist);
			}
		}

		// --------------- generic
		if ( (generic == null) && (bnd != null) ) {
			generic = (ParameterizableClass) ensureFamixClass(bnd.getErasure(), name, /*owner*/null, /*isGeneric*/true, alwaysPersist);
		}

		// --------------- recover from name ?
		for (ParameterizedType candidate : getEntityByName(ParameterizedType.class, name) ) {
			if ( matchAndMapType(bnd, name, owner, candidate) ) {
				fmx = candidate;
				break;
			}
		}

		// --------------- create
		boolean persistIt = alwaysPersist || (! (owner instanceof Method));
		if (fmx == null) {
			fmx = super.ensureFamixParameterizedType(bnd, name, generic, owner, persistIt);
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

		// --------------- to avoid useless computations if we can
		fmx = (fr.inria.verveine.core.gen.famix.Enum)getEntityByKey(bnd);
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
				owner = ensureFamixNamespaceDefault();  // not really sure what to do here
			}
			else {
				owner = ensureOwner(bnd, /*persistIt*/true); // owner should be a class or package so yes persist it
			}
		}

		// --------------- recover from name ?
		for (fr.inria.verveine.core.gen.famix.Enum candidate : getEntityByName(fr.inria.verveine.core.gen.famix.Enum.class, name) ) {
			if ( matchAndMapType(bnd, name, owner, candidate) ) {
				fmx = candidate;
				break;
			}
		}

		if (fmx == null) {
			fmx = super.ensureFamixEnum(bnd, name, owner, /*persistIt*/true);
		}
		
		if ( (fmx!=null) && (bnd != null) ) {
			setNamedEntityModifiers(fmx, bnd.getModifiers());
		}

		return fmx;
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
			fmx = super.ensureFamixEnumValue(bnd, name, owner, persistIt);
		}
		
		if (fmx!=null) {
			fmx.setParentEnum(owner);
		}

		return fmx;
	}

	/**
	 * e.g. see {@link JavaDictionary#ensureFamixClass}
	 */
	public AnnotationType ensureFamixAnnotationType(ITypeBinding bnd, String name, ContainerEntity owner, boolean alwaysPersist) {
		AnnotationType fmx = null;

		// --------------- to avoid useless computations if we can
		fmx = (AnnotationType)getEntityByKey(bnd);	// to avoid useless computations if we can
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
				owner = ensureFamixNamespaceDefault();
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
			fmx = super.ensureFamixAnnotationType(bnd, name, owner, persistIt);
		}
		
		if ( (fmx!=null) && (bnd != null) ) {
			setNamedEntityModifiers(fmx, bnd.getModifiers());
		}

		return fmx;
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
			if ( matchAndMapMethod(bnd, name+"()", null, owner, candidate) ) {
				fmx = candidate;
				break;
			}
		}

		if (fmx == null) {
			fmx = super.ensureFamixAnnotationTypeAttribute(bnd, name, owner, persistIt);
		}
		
		if ( (fmx!=null) && (bnd != null) ) {
			setNamedEntityModifiers(fmx, bnd.getModifiers());
		}

		return fmx;
	}

	/**
	 * Adds possible annotation instances to a Famix NamedEntity with the given binding
	 * @param bnd
	 * @param fmx
	 * @param persistIt  -- whether to persist or not the type
	 */
	public void addFamixAnnotationInstances(IBinding bnd, NamedEntity fmx, boolean persistIt) {
		if (bnd != null) {
			for (IAnnotationBinding annBnd : bnd.getAnnotations()) {
				AnnotationType annType = ensureFamixAnnotationType(annBnd.getAnnotationType(), null, null, persistIt);

				Collection<AnnotationInstanceAttribute> annAtts = new ArrayList<AnnotationInstanceAttribute>(); 
				for (IMemberValuePairBinding annPV : annBnd.getDeclaredMemberValuePairs()) {
					annAtts.add( createFamixAnnotationInstanceAttribute(ensureFamixAnnotationTypeAttribute(annPV.getMethodBinding(), annPV.getName(), annType, persistIt),
																		(annPV.getValue() != null) ? annPV.getValue().toString() : ""));
				}

				super.addFamixAnnotationInstance(fmx, annType, annAtts);
			}
		}
	}

	 /**
	  * e.g. see {@link JavaDictionary#ensureFamixClass}
	  */
	public ParameterType ensureFamixParameterType(ITypeBinding bnd,	String name, ParameterizableClass owner, boolean persistIt) {
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
					owner = (ParameterizableClass) this.ensureFamixType(parentClass, /*name*/null, /*owner*/null, /*ctxt*/null, /*alwaysPersist?*/persistIt);
				}
				else {
					owner = null;  // not really sure what to do here
				}
			}
		}

		// --------------- recover from name ?
		for (Type candidate : this.getEntityByName(Type.class, name)) {
			if ( matchAndMapType(bnd, name, owner, candidate) ) {
				fmx = (ParameterType) candidate;
				break;
			}
		}

		// --------------- create
		if (fmx == null) {
			fmx = super.ensureFamixParameterType(bnd, name, owner, persistIt);
		}

		return fmx;
	}

	/**
	 * Checks whether the existing unmapped Famix Namespace matches the binding.
	 * Checks that the candidate has the same name as the JDT bound package, and checks recursively that owners also match.
	 * @param bnd -- a JDT binding that we are trying to match to the candidate
	 * @param name of the package
	 * @param owner of the package
	 * @param candidate -- a Famix Entity
	 * @return whether the binding matches the candidate (if <b>true</b>, the mapping is recorded)
	 */
	private boolean matchAndMapNamespace(IPackageBinding bnd, String name, Namespace owner, NamedEntity candidate) {
		if (! (candidate instanceof Namespace) ) {
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
	private boolean matchAndMapType(ITypeBinding bnd, String name, ContainerEntity owner, NamedEntity candidate) {
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
		if ( (bnd != null) && (bnd.isParameterizedType()) ) {
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
			if (matchAndMapNamespace(bnd.getPackage(), owner.getName(), (Namespace)owner.getBelongsTo(), candidate.getBelongsTo())) {
				conditionalMapToKey(bnd, candidate);
				return true;
			}
			else {
				return false;
			}
		}

		// check owners with bnd
		// type is a Parameterized type
		if (bnd.isParameterizedType() && (candidate instanceof ParameterizedType)) {
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
		if ( bnd.isClass() || bnd.isInterface() ) {
			return matchAndMapClass(bnd, name, owner, (Type) candidate);
		}

		return false;
	}

	/**
	 * Checks whether the existing unmapped Famix Class (or Interface or Enum) matches the binding.
	 * Checks that the candidate has the same name as the JDT bound type, and checks recursively that owners also match.
	 * @param bnd -- a JDT binding that we are trying to match to the candidate
	 * @param name of the class
	 * @param owner of the class
	 * @param candidate -- a Famix Entity
	 * @return whether the binding matches the candidate (if <b>true</b>, the mapping is recorded)
	 */
	private boolean matchAndMapClass(ITypeBinding bnd, String name, ContainerEntity owner, Type candidate) {
		if (! (candidate instanceof fr.inria.verveine.core.gen.famix.Class)) {
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
	private boolean matchAndMapMethod(IMethodBinding bnd, String sig, Type retTyp, ContainerEntity owner, NamedEntity candidate) {
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

		// checking names (actually could check only signature)
		String name = (sig != null) ? sig.substring(0, sig.indexOf('(')) : null;
		if (checkNameMatch(bnd, name, candidate) == CheckResult.FAIL) {
			return false;
		}

		// for methods, the name is not enough, we must test the signature also
		// but not for AnnotationTypeAttribute
		if (candidate instanceof Method) {
			if (bnd != null) {
				sig = bnd.getName() + "(";
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
				}
				else { // (ret != null)  i.e. not a constructor
					if ( ((Method) candidate).getDeclaredType() == null ) {
						return false;
					}
					else if (! matchAndMapType(null, retTyp.getName(), retTyp.getBelongsTo(), ((Method) candidate).getDeclaredType()) ) {
						return false;
					}
					// else OK for now
				}
			}
		}  // if (candidate instanceof Method)

		// check owner
		if (matchAndMapOwnerAsType( ((bnd != null)?bnd.getDeclaringClass():null), owner, candidate.getBelongsTo()) == CheckResult.MATCH) {
			conditionalMapToKey(bnd, candidate);
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
	 * @param name of the variable
	 * @param owner of the variable
	 * @param candidate -- a Famix Entity (a StructuralEntity or an EnumValue)
	 * @return whether the binding matches the candidate (if <b>true</b>, the mapping is recorded)
	 */
	private boolean matchAndMapVariable(IVariableBinding bnd, String name, ContainerEntity owner, NamedEntity candidate) {
		if (! (candidate instanceof StructuralEntity)) {
			return false;
		}

		// check whether bnd and candidate are already bound
		CheckResult keyMatch = checkKeyMatch(bnd, candidate);
		if (keyMatch == CheckResult.MATCH) {
			return true;
		}
		else if (keyMatch == CheckResult.FAIL) {
			return false;
		}

		if (checkNameMatch(bnd, name, candidate) == CheckResult.FAIL) {
			return false;
		}

		// check owner
		ContainerEntity candidateOwner = candidate.getBelongsTo();

		// local variable or parameter ?
		// owner is a Method? (for example in case of an anonymous class)
		CheckResult res = matchAndMapOwnerAsMethod( ((bnd != null) ? bnd.getDeclaringMethod() : null), owner, candidateOwner);
		if (res == CheckResult.FAIL) {
			return false;
		}
		else if (res == CheckResult.MATCH) {
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
	private boolean matchAndMapTypeOwner(ITypeBinding bnd, NamedEntity owner, Type candidate) {
		ContainerEntity candidateOwner = candidate.getBelongsTo();

		// owner is a Method? (for example in case of an anonymous class)
		CheckResult res = matchAndMapOwnerAsMethod( ((bnd != null) ? bnd.getDeclaringMethod() : null), owner, candidate);
		if (res == CheckResult.MATCH) {
			conditionalMapToKey(bnd, candidate);
			return true;
		} 
		else if (res == CheckResult.FAIL) {
			return false;
		}

		// owner is a class ?
		res = matchAndMapOwnerAsType( ((bnd != null) ? bnd.getDeclaringClass() : null), owner, candidateOwner);
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
	 * @param bnd
	 * @param owner
	 * @param candidate
	 * @return a {@link JavaDictionary#CheckResult}
	 */
	private CheckResult matchAndMapOwnerAsMethod(IMethodBinding methBnd, NamedEntity owner, ContainerEntity candidateOwner) {
		if ( (methBnd != null) || ((owner != null) && (owner instanceof Method)) ) {
			if (! (candidateOwner instanceof Method)) {
				return CheckResult.FAIL;
			}
			
			ContainerEntity ownerOwner = (owner != null) ? owner.getBelongsTo() : null;
			String ownerSig = (owner != null) ? ((Method)owner).getSignature() : null;
			Type ownerReturn = (owner != null) ? ((Method)owner).getDeclaredType() : null;

			if ( matchAndMapMethod(methBnd, ownerSig, ownerReturn, ownerOwner, (Method)candidateOwner) ) {
				return CheckResult.MATCH;
			}
			else {
				return CheckResult.FAIL;
			}
		}
		return CheckResult.UNDECIDED;
	}

	/**
	 * @param bnd
	 * @param owner
	 * @param candidate
	 * @param candidateOwner
	 * @param ownerOwner
	 * @return
	 */
	private CheckResult matchAndMapOwnerAsType(ITypeBinding typBnd, NamedEntity owner, ContainerEntity candidateOwner) {
		if ( (typBnd != null) || ((owner != null) && (owner instanceof Type)) ) {
			if (! (candidateOwner instanceof Type)) {
				return CheckResult.FAIL;
			}

			ContainerEntity ownerOwner = (owner != null) ? owner.getBelongsTo() : null;
			String ownerName= (owner != null) ? ((Type)owner).getName() : null;

			if (matchAndMapType(typBnd, ownerName, ownerOwner, candidateOwner)) {
				return CheckResult.MATCH;
			}
			else {
				return CheckResult.FAIL;
			}
		}
		return CheckResult.UNDECIDED;
	}

	private CheckResult matchAndMapOwnerAsNamespace(IPackageBinding pckgBnd, NamedEntity owner, ContainerEntity candidateOwner) {
		if ( (pckgBnd != null) || ((owner != null) && (owner instanceof Namespace)) ) {
			if (! (candidateOwner instanceof Namespace)) {
				return CheckResult.FAIL;
			}

			Namespace ownerOwner = (owner != null) ? (Namespace)owner.getBelongsTo() : null;
			String ownerName= (owner != null) ? ((Namespace)owner).getName() : null;

			if (matchAndMapNamespace(pckgBnd, ownerName, ownerOwner, candidateOwner)) {
				return CheckResult.MATCH;
			}
			else {
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
	private CheckResult checkNameMatch(IBinding bnd, String name, NamedEntity candidate) {
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
	private CheckResult checkKeyMatch(IBinding key, NamedEntity candidate) {
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

	private void conditionalMapToKey(IBinding bnd, NamedEntity ent) {
		if (bnd != null) {
			mapEntityToKey(bnd, ent);
		}
	}

	/**
	 * Returns a Famix Method associated with the IMethodBinding. The Entity is created if it does not exist.
	 * The Entity is created if it does not exist.
	 * Params: see {@link Dictionary#ensureFamixMethod(Object, String, String, Type, Type, boolean)}.
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public Method ensureFamixMethod(IMethodBinding bnd, String name, Collection<String> paramTypes, Type ret, Type owner, boolean persistIt) {
		Method fmx = null;
		String sig = "";
		boolean first;
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
		first = true;
		if (bnd != null) {
			for (ITypeBinding parBnd : bnd.getParameterTypes()) {
				if (first) {
					sig = parBnd.getName();
					first = false;
				}
				else {
					sig += "," + parBnd.getName();
				}
			}
		}
		else if (paramTypes != null) {
			for (String t : paramTypes) {
				if (first) {
					sig = t;
					first = false;
				}
				else {
					sig += "," + t;
				}
			}				
		}
		else {
			sig += "???";
		}
		sig = name + "(" + sig + ")";

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
					if (retTypBnd.isArray()) {
						retTypBnd = retTypBnd.getElementType();
					}
					if ( (retTypBnd != null) && retTypBnd.isTypeVariable() && (retTypBnd.getDeclaringMethod() == bnd) ) {
						ret = null;
						delayedRetTyp = true;
					}
					else {
						ret = this.ensureFamixType(retTypBnd, /*name*/null, /*owner*/null, /*ctxt*/owner, /*alwaysPersist?*/persistIt);
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
					Type tmpOwn = ensureFamixType(classBnd, /*name*/null, /*owner*/null, /*ctxt*/null, /*alwaysPersist?*/persistIt);
					if (tmpOwn instanceof ParameterizedType) {
						owner = ((ParameterizedType) tmpOwn).getParameterizableClass();
					}
					else {
						owner = tmpOwn; 
					}
				}
				else {
					owner = ensureFamixClassStubOwner();	
				}
			}
		}

		// --------------- recover from name ?
		for (Method candidate : this.getEntityByName(Method.class, name)) {
			if ( matchAndMapMethod(bnd, sig, ret, owner, candidate) ) {
				fmx = candidate;
				break;
			}
		}

		if (fmx == null) {
			fmx = super.ensureFamixMethod(bnd, name, sig, ret, owner, persistIt);
		}

		if (fmx!=null) {
			if (bnd != null) {
				int mod = bnd.getModifiers();
				setNamedEntityModifiers(fmx, mod);
				fmx.setHasClassScope(Modifier.isStatic(mod));
			}
			else {
				fmx.setHasClassScope(false);
			}
		}

		if ( (fmx != null) && delayedRetTyp ) {
			fmx.setDeclaredType(this.ensureFamixType(retTypBnd, /*name*/null, /*owner*/fmx, /*ctxt*/owner, /*alwaysPersist?*/persistIt));
		}

		return fmx;
	}

	/**
	 * Returns a Famix Attribute associated with the IVariableBinding.
	 * The Entity is created if it does not exist.<br>
	 * Params: see {@link Dictionary#ensureFamixAttribute(Object, String, Type, Type, boolean)}.
	 * @param persistIt -- whether to persist or not the entity eventually created
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public Attribute ensureFamixAttribute(IVariableBinding bnd, String name, Type type, Type owner, boolean persistIt) {
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

		// --------------- type
		if (type == null) {
			if (bnd == null) {
				type = null;  // what else ?
			}
			else {
				type = this.ensureFamixType(bnd.getType(), null, null, owner, /*alwaysPersist?*/persistIt);
			}
		}

		// --------------- owner
		if (owner == null) {
			if (bnd == null) {
				return null;  // what would be the interest of creating an attribute for which we ignore the declaring class? 
			}
			else {
				ITypeBinding classBnd = bnd.getDeclaringClass();
				if (classBnd != null) {
					Type tmpOwn = ensureFamixType(classBnd, /*name*/null, /*owner*/null, /*ctxt*/null, /*alwaysPersist?*/persistIt);
					if (tmpOwn instanceof ParameterizedType) {
						owner = ((ParameterizedType) tmpOwn).getParameterizableClass(); 
					}
					else {
						owner = tmpOwn;
					}
				}
				else {
					return null;  // what would be the interest of creating an attribute for which we ignore the declaring class? 
				}
			}
		}

		// --------------- recover from name ?
		for (Attribute candidate : getEntityByName(Attribute.class, name) ) {
			if ( matchAndMapVariable(bnd, name, owner, candidate) ) {
				fmx = candidate;
				break;
			}
		}

		if (fmx == null) {
			fmx = super.ensureFamixAttribute(bnd, name, type, owner, persistIt);
		}
		
		if (fmx!=null) {
			fmx.setParentType(owner);
			fmx.setDeclaredType(type);	
			if (bnd != null) {
				int mod = bnd.getModifiers();
				setNamedEntityModifiers(fmx, mod);
				fmx.setHasClassScope(Modifier.isStatic(mod));
			}
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
	 * Params: see {@link Dictionary#ensureFamixParameter(Object, String, Type, fr.inria.verveine.core.gen.famix.BehaviouralEntity, boolean)}.
	 * @param persistIt -- whether to persist or not the entity eventually created
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public Parameter ensureFamixParameter(IVariableBinding bnd, String name, Type typ, Method owner, boolean persistIt) {
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
		if (owner == null) {
			if (bnd == null) {
				owner = ensureFamixStubMethod("<"+name+"_owner>");
			}
			else {
				owner = ensureFamixMethod(bnd.getDeclaringMethod(), null, (Collection<String>)null, null, null, persistIt);  // cast needed to desambiguate the call
			}
		}
		
		// --------------- type
		if (typ == null) {
			if (bnd == null) {
				typ = null;  // what else ?
			}
			else {
				typ = this.ensureFamixType(bnd.getType(), null, null, owner.getParentType(), /*alwaysPersist?*/persistIt);  // context of the parameter def = the class definition
			}
		}

		// --------------- recover from name ?
		for (Parameter candidate : getEntityByName(Parameter.class, name) ) {
			if ( matchAndMapVariable(bnd, name, owner, candidate) ) {
				fmx = candidate;
				break;
			}
		}

		if (fmx == null) {
			fmx = super.createFamixParameter(bnd, name, typ, owner, persistIt);
		}
		
		if (fmx != null) {
			fmx.setParentBehaviouralEntity(owner);
			fmx.setDeclaredType(typ);	
		}

		return fmx;
	}
	
	/**
	 * Returns a Famix LocalVariable associated with the IVariableBinding.
	 * The Entity is created if it does not exist.<br>
	 * Params: see {@link Dictionary#ensureFamixLocalVariable(Object, String, Type, fr.inria.verveine.core.gen.famix.BehaviouralEntity, boolean)}
	 * @param persistIt  -- whether to persist or not the entity eventually created
	 * @return the Famix Entity found or created. May return null if <b>bnd</b> and <b>name</b> are null, or <b>bnd</b> and <b>owner</b> are null, or in case of a Famix error
	 */
	public LocalVariable ensureFamixLocalVariable(IVariableBinding bnd, String name, Type typ, Method owner, boolean persistIt) {
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

		// --------------- type
		if (typ == null) {
			if (bnd == null) {
				typ = null;  // what else ?
			}
			else {
				typ = this.ensureFamixType(bnd.getType(), null, null, owner, /*alwaysPersist?*/persistIt);
			}
		}

		// --------------- owner
		if (owner == null) {
			if (bnd == null) {
				return null;  // what would be the interest of a local variable for which we ignore the declaring method?
			}
			else {
				owner = ensureFamixMethod(bnd.getDeclaringMethod(), null, (Collection<String>)null, null, null, false);  // cast needed to desambiguate the call
			}
		}

		// --------------- recover from name ?
		for (LocalVariable candidate : getEntityByName(LocalVariable.class, name) ) {
			if ( matchAndMapVariable(bnd, name, owner, candidate) ) {
				fmx = candidate;
				break;
			}
		}

		if (fmx == null) {
			fmx = super.ensureFamixLocalVariable(bnd, name, typ, owner, persistIt);
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
	 * @param persistIt  -- whether to persist or not the entity eventually created
	 */
	public UnknownVariable createFamixUnknownVariable(Type type, String name, boolean persistIt) {
//		System.err.println("TRACE -- createFamixUnknownVariable: "+name);
		UnknownVariable fmx = (UnknownVariable) createFamixEntity(UnknownVariable.class, name, persistIt);
		if (fmx!=null) {
			fmx.setDeclaredType(type);
		}
		return fmx;
	}

	public Comment createFamixComment(org.eclipse.jdt.core.dom.Comment jdoc, NamedEntity fmx) {
		Comment cmt = null;
		if (jdoc != null) {
			cmt = super.createFamixComment(jdoc.toString(), fmx);
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
			int end = beg + ast.getLength()-1;
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
			int tmp= ((CompilationUnit)ast).getLineNumber(end);
			fa.setEndLine(tmp);
			fmx.setSourceAnchor(fa);
			famixRepo.add(fa);
		}
		
		return fa;
	}

	/**
	 * Creates or recovers a stub Famix Method
	 * @param name of the method
	 * @return the Famix Method
	 */
	public Method ensureFamixStubMethod(String name) {
		return ensureFamixMethod(null, name, (Collection<String>)null, null, ensureFamixClassStubOwner(), false);  // cast needed to desambiguate the call
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
		// always persist the MetaClass whatever the value of VerveineJParser.classSummary
		fr.inria.verveine.core.gen.famix.Class fmx =  this.ensureFamixClass(null, METACLASS_NAME, javaLang, /*isGeneric*/true, /*alwaysPersist?*/true);

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