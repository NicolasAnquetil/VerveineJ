package fr.inria.verveine.extractor.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeParameter;

import ch.akuhn.fame.Repository;
import fr.inria.verveine.core.Dictionary;
import fr.inria.verveine.core.gen.famix.AnnotationInstance;
import fr.inria.verveine.core.gen.famix.AnnotationType;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.ContainerEntity;
import fr.inria.verveine.core.gen.famix.FileAnchor;
import fr.inria.verveine.core.gen.famix.Inheritance;
import fr.inria.verveine.core.gen.famix.LocalVariable;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.NamedEntity;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.Parameter;
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

	// a property added to CompilationUnits to record the name of the source file name they come from
	public static final String SOURCE_FILENAME_PROPERTY = "verveine-source-filename";

	public static final String OBJECT_NAME = "Object";
	public static final String OBJECT_PACKAGE_NAME = "java.lang";
	public static final String ARRAYS_NAME = "default[]";
	public static final String INSTANCE_INIT_BLOCK_NAME = "<InstanceInitializer>";
	public static final String STATIC_INIT_BLOCK_NAME = "<StaticInitializer>";

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
		
		if (name.length() > 0) {
			/* Note: Packages are created with their fully-qualified name to simplify recovering them when we don't have a binding
			 * (for example when creating parent packages of a package we have a binding for).
			 * Because the preferred solution in Moose is to give their simple names to packages, they must be post-processed when
			 * all is said and done. */
			fmx = super.ensureFamixNamespace( bnd, name);
			parent = this.ensureFamixNamespace(null, removeLastName(name));
			// set the parentscope relationship
			if ( (parent != null) && (fmx != null) && (fmx.getParentScope() == null)) {
				parent.addChildScopes(fmx);
			}
		}

		return fmx;
	}

	public Collection<Type> ensureFamixTypes(List<org.eclipse.jdt.core.dom.Type> types) {
		Collection<Type> fmxTypes = new ArrayList<Type>();
		
		Type fmxType = null;
		for (org.eclipse.jdt.core.dom.Type type : types) {
			ITypeBinding bnd = type.resolveBinding();
			if (bnd != null) {
				fmxType = ensureFamixType(bnd, findTypeName(type), null);
			} else {
				fmxType = ensureFamixUniqEntity(fr.inria.verveine.core.gen.famix.Class.class, null, type.toString());
			}
			fmxTypes.add(fmxType);
		}
		return fmxTypes;
	}

	/**
	 * Recovers or creates a Famix Type (see also {@link Dictionary#ensureFamixType(Object, String, ContainerEntity)}
	 */
	public Type ensureFamixType(ITypeBinding bnd, String name, ContainerEntity owner) {

		if (bnd == null) {
			return super.ensureFamixType(bnd, name, owner);
		}

		if (bnd.isArray()) {
			bnd = bnd.getElementType();
		}
		
		if (bnd.isPrimitive()) {
			return ensureFamixPrimitiveType(bnd, name);
		}
		else {
			return this.ensureFamixClass(bnd, null, null);
		}
	}
	
	public Collection<Type> ensureFamixTypesParameters(List<TypeParameter> types) {
		Collection<Type> fmxTypes = new ArrayList<Type>();
		Type fmxType = null;
		
		for (TypeParameter type : types) {
			ITypeBinding bnd = type.resolveBinding();
			if (bnd != null) {
				fmxType = ensureFamixType(bnd, null, null);
			} else {
				fmxType = ensureFamixUniqEntity(fr.inria.verveine.core.gen.famix.Class.class, null, type.toString());
			}
			fmxTypes.add(fmxType);
		}
		return fmxTypes;
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
	
	public AnnotationType ensureFamixAnnotationType(ITypeBinding bnd) {

		if (bnd == null) {
			System.err.println("Warning: Unexpected null binding, cannot create Famix Annotation Type");
			return null;
		}
		
		AnnotationType fmx = ensureFamixUniqEntity(AnnotationType.class, null, bnd.getName());
		fmx.setIsStub(true);
		fmx.setContainer(ensureFamixNamespace(bnd.getPackage(), null));
		mapToKey.put(bnd, fmx);
		return fmx;
	}

	/**
	 * Returns a Famix Class associated with the ITypeBinding.
	 * The Entity is created if it does not exist.
	 * Params: see {@link Dictionary#ensureFamixClass(Object, String, ContainerEntity)}.
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public fr.inria.verveine.core.gen.famix.Class ensureFamixClass(ITypeBinding bnd, String name, ContainerEntity owner) {
		fr.inria.verveine.core.gen.famix.Class fmx = null;

		// if Binding is null, we can only rely on provided parameters, so lets do it now and return
		if (bnd == null) {
			if (name == null) {
				return null;
			}
			if (owner == null) {
				owner = ensureFamixNamespaceDefault();
			}
			return super.ensureFamixClass(null, name, owner);
		}

		// --------------- some special cases
		while (bnd.isArray()) {
			bnd = bnd.getComponentType();
		}

		if (bnd.isPrimitive()) {
			// should have called ensureFamixPrimitiveType(bnd). Why are we here ?
			return null;
		}

		fmx = (fr.inria.verveine.core.gen.famix.Class)getEntityByKey(bnd);	// to avoid useless computations if we can
		if (fmx != null) {
			return fmx;
		}

		// --------------- name
		if (name == null) {
			if (! bnd.isAnonymous()) {
				name = bnd.getName();
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
		
		if (name.equals(OBJECT_NAME)) {
			return ensureFamixClassObject(bnd);
		}

		// --------------- owner
		if ( (owner == null) && (bnd != null) ) {
			IMethodBinding parentMtd = bnd.getDeclaringMethod();
			if (parentMtd != null) {
				owner = this.ensureFamixMethod(parentMtd, null, (Collection<org.eclipse.jdt.core.dom.Type>)null, null, null);  // cast needed to desambiguate the call
			}
			else {
				ITypeBinding parentClass = bnd.getDeclaringClass();
				if (parentClass != null) {
					owner = this.ensureFamixClass(parentClass, null, null);
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
		}

		// --------------- superclasses (including interfaces)
		Collection<fr.inria.verveine.core.gen.famix.Class> sups = new LinkedList<fr.inria.verveine.core.gen.famix.Class>();
		if (! bnd.isInterface()) {
			ITypeBinding supbnd = bnd.getSuperclass();
			if (supbnd != null) {
				sups.add(this.ensureFamixClass(supbnd, null, null));
			}
			else {
				sups.add( ensureFamixClassObject(null));
			}
		}
		for (ITypeBinding intbnd : bnd.getInterfaces()) {
			sups.add( ensureFamixClass(intbnd, null, null));
		}

		// --------------- recover from name ?
		for (fr.inria.verveine.core.gen.famix.Class candidate : this.getEntityByName(fr.inria.verveine.core.gen.famix.Class.class, name)) {
			if ( recoverAndMapClass(bnd, candidate) ) {
				fmx = candidate;
				break;
			}
		}
		if (fmx == null) {
			fmx = super.ensureFamixClass(bnd, name, owner);
		}
		
		if (fmx!=null) {
			// we just created it or it was not bound, so we make sure it has the right information in it
			fmx.setIsInterface(bnd.isInterface());
			Inheritance lastInheritance = null;
			for (fr.inria.verveine.core.gen.famix.Class sup : sups) {
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

	private boolean recoverAndMapNamespace(IPackageBinding bnd, Namespace candidate) {
		NamedEntity bound = getEntityByKey(bnd); 
		if (bound == candidate) {
			return true;
		}
		else if (bound != null) {
			return false;
		}

		if (bnd.getName().equals(candidate.getName())) {
			mapToKey.put(bnd, candidate);
			return true;
		}
		return false;
	}

	private boolean recoverAndMapType(ITypeBinding bnd, Type candidate) {
		if (bnd.isArray()) {
			bnd = bnd.getElementType();
		}
		
		NamedEntity bound = getEntityByKey(bnd); 
		if (bound == candidate) {
			return true;
		}
		else if (bound != null) {
			return false;
		}
		
		if ( (bnd.isClass() || bnd.isInterface()) &&
			 (candidate instanceof fr.inria.verveine.core.gen.famix.Class) ) {
			return recoverAndMapClass(bnd, (fr.inria.verveine.core.gen.famix.Class) candidate);
		}
		else if ( bnd.isPrimitive() && (candidate instanceof PrimitiveType) ) {
			if ( (bnd.isPrimitive()) && (bnd.toString().equals(candidate.getName())) ) {
				mapToKey.put(bnd, candidate);
				return true;
			}
			else {
				return false;
			}
		}
		else {
			IMethodBinding methBnd = bnd.getDeclaringMethod();
			if ( (methBnd != null) && (candidate.getBelongsTo() instanceof Method) ) {
				if ( recoverAndMapMethod(methBnd, (Method) candidate.getBelongsTo()) ) {
					mapToKey.put(bnd, candidate);
					return true;
				}
				else {
					return false;
				}
			}

			ITypeBinding classBnd = bnd.getDeclaringClass();
			if ( (classBnd != null) && (candidate.getBelongsTo() instanceof fr.inria.verveine.core.gen.famix.Class) ) {
				if ( recoverAndMapClass(classBnd, (fr.inria.verveine.core.gen.famix.Class) candidate.getBelongsTo()) ) {
					mapToKey.put(bnd, candidate);
					return true;
				}
				else {
					return false;
				}
			}
			
			IPackageBinding pckgBnd = bnd.getPackage();
			if ( (candidate.getBelongsTo() instanceof Namespace) &&
				 (recoverAndMapNamespace(pckgBnd, (Namespace) candidate.getBelongsTo())) ) {
				mapToKey.put(bnd, candidate);
				return true;
			}
			else {
				return false;
			}
		}
	}

	private boolean recoverAndMapClass(ITypeBinding bnd, fr.inria.verveine.core.gen.famix.Class candidate) {
		NamedEntity bound = getEntityByKey(bnd); 
		if (bound == candidate) {
			return true;
		}
		else if (bound != null) {
			return false;
		}

		IMethodBinding methBnd = bnd.getDeclaringMethod();
		if ( (methBnd != null) && (candidate.getBelongsTo() instanceof Method) ) {
			if ( recoverAndMapMethod(methBnd, (Method) candidate.getBelongsTo()) ) {
				mapToKey.put(bnd, candidate);
				return true;
			}
			else {
				return false;
			}
		}

		ITypeBinding classBnd = bnd.getDeclaringClass();
		if ( (classBnd != null) && (candidate.getBelongsTo() instanceof fr.inria.verveine.core.gen.famix.Class) ) {
			if ( recoverAndMapClass(classBnd, (fr.inria.verveine.core.gen.famix.Class) candidate.getBelongsTo()) ) {
				mapToKey.put(bnd, candidate);
				return true;
			}
			else {
				return false;
			}
		}

		IPackageBinding pckgBnd = bnd.getPackage();
		if ( (candidate.getBelongsTo() instanceof Namespace) &&
			 (recoverAndMapNamespace(pckgBnd, (Namespace) candidate.getBelongsTo())) ) {
			mapToKey.put(bnd, candidate);
			return true;
		}
		else {
			return false;
		}
	}

	private boolean recoverAndMapMethod(IMethodBinding bnd, Method candidate) {
		NamedEntity bound = getEntityByKey(bnd); 
		if (bound == candidate) {
			return true;
		}
		else if (bound != null) {
			return false;
		}

		// for methods, the name is not enough, we must test the signature also 
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
			sig += ")";
		}
		if (! candidate.getSignature().equals(sig)) {
			return false;
		}

		ITypeBinding ownerBnd = bnd.getDeclaringClass();
		if ( (candidate.getBelongsTo() instanceof fr.inria.verveine.core.gen.famix.Class) &&
			 (recoverAndMapClass(ownerBnd, (fr.inria.verveine.core.gen.famix.Class) candidate.getBelongsTo())) ) {
				mapToKey.put(bnd, candidate);
				// everything seems OK, but we still need to test return type which is not in the Moose signature
				return recoverAndMapType(bnd.getReturnType(), candidate.getDeclaredType());
		}
		else {
			return false;
		}
	}

	private boolean recoverAndMapVariable(IVariableBinding bnd, StructuralEntity candidate) {
		NamedEntity bound = (StructuralEntity)getEntityByKey(bnd); 
		if (bound == candidate) {
			return true;
		}
		else if (bound != null) {
			return false;
		}

		IMethodBinding methBnd = bnd.getDeclaringMethod();
		if ( (methBnd != null) && (candidate.getBelongsTo() instanceof Method) ) {
			if ( recoverAndMapMethod(methBnd, (Method) candidate.getBelongsTo()) ) {
				mapToKey.put(bnd, candidate);
				return true;
			}
			else {
				return false;
			}
		}

		ITypeBinding classBnd = bnd.getDeclaringClass();
		if ( (candidate.getBelongsTo() instanceof fr.inria.verveine.core.gen.famix.Class) &&
			 (recoverAndMapClass(classBnd, (fr.inria.verveine.core.gen.famix.Class) candidate.getBelongsTo())) ) {
			mapToKey.put(bnd, candidate);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Returns a Famix Method associated with the IMethodBinding. The Entity is created if it does not exist.
	 * The JDT Binding is a unique representation of a java entity within the AST.
	 * This method also creates some basic links between the entity and others (e.g. declaring container, return type, ...)
	 * @param bnd -- the JDT Binding 
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public Method ensureFamixMethod(IMethodBinding bnd, String name, Collection<org.eclipse.jdt.core.dom.Type> paramTypes, Type ret, fr.inria.verveine.core.gen.famix.Class owner) {
		Method fmx = null;
		String sig;
		boolean first;

		// signature is dealt in two places
		// here we try to use the parameter of ensureFamixMethod to compute the list of parameter types of the FamixMethod
		// if it does not work and binding is not null, we will have another chance to compute this list later
		sig = "(";
		first = true;
		if (paramTypes != null) {
			for (org.eclipse.jdt.core.dom.Type t : paramTypes) {
				if (! first) {
					sig += "," + findTypeName(t);
				}
				else {
					sig += findTypeName(t);
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

		// --------------- name
		if (name == null) {
			name = bnd.getName();
		}

		// --------------- signature
		if (paramTypes == null) {
			sig = "(";
			first = true;
			paramTypes = new ArrayList<org.eclipse.jdt.core.dom.Type>();
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
		if ( (ret == null) && (! bnd.isConstructor()) ) {
			ret = this.ensureFamixType(bnd.getReturnType(), null, null);
		}
		// else leave it to null ...

		// --------------- owner
		if (owner == null) {
			owner = ensureFamixClass(bnd.getDeclaringClass(), null, null);
		}
		
		// --------------- recover from name ?
		for (Method candidate : this.getEntityByName(Method.class, name)) {
			if ( recoverAndMapMethod(bnd, candidate) ) {
				fmx = candidate;
				break;
			}
		}
		if (fmx == null) {
			fmx = ensureFamixMethod(bnd, name, sig, ret, owner);
		}
		
		if (fmx!=null) {
			// we just created it or it was not bound, so we make sure it has the right information in it
			fmx.setDeclaredType(ret);
			fmx.setSignature(sig);
			setNamedEntityModifiers(fmx, bnd.getModifiers());
		}

		return fmx;
	}
	
	public String findTypeName(org.eclipse.jdt.core.dom.Type t) {
		if (t.isPrimitiveType()) {
			return t.toString();
		}
		else if (t.isSimpleType()) {
			return ((SimpleType)t).getName().getFullyQualifiedName();
		}
		else if (t.isQualifiedType()) {
			return ((QualifiedType)t).getName().getIdentifier();
		}
		else if (t.isArrayType()) {
			return findTypeName( ((ArrayType)t).getElementType() );
		}
		else if (t.isParameterizedType()) {
			return "?";  // TODO
		}
		else { // it is a WildCardType
			return "?"; // TODO
		}
	}

	/**
	 * Returns a Famix Attribute associated with the IVariableBinding. The Entity is created if it does not exist.<br>
	 * Params: see {@link Dictionary#ensureFamixAttribute(Object, String, Type, Type)} 
	 * @return the Famix Entity found or created. May return null if <b>bnd</b> and <b>name</b> are null, or <b>bnd</b> and <b>owner</b> are null, or in case of a Famix error
	 */
	public Attribute ensureFamixAttribute(IVariableBinding bnd, String name, Type type, Type owner) {
		Attribute fmx = null;

		// if Binding is null, we can only rely on provided parameters, so lets do it now and return
		if (bnd == null) {
			if (name == null) {
				return null;
			}
			if (owner == null) {
				return null;  // what would be the interest of an attribute for which we ignore the declaring class? 
			}
			return super.ensureFamixAttribute(null, name, type, owner);
		}

		// --------------- name
		if (name == null) {
			name = bnd.getName();
		}

		// --------------- return type
		if (type == null) {
			type = this.ensureFamixType(bnd.getType(), null, null);
		}
		// else leave it to null ...

		// --------------- owner
		if (owner == null) {
			owner = ensureFamixClass(bnd.getDeclaringClass(), null, null);
		}
		
		// --------------- recover from name ?
		for (Attribute candidate : getEntityByName(Attribute.class, name) ) {
			if ( recoverAndMapVariable(bnd, candidate) ) {
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
	 * Returns a Famix Paramenter associated with the IVariableBinding. The Entity is created if it does not exist.
	 * The JDT Binding is a unique representation of a java entity within the AST.
	 * This method also creates some basic links between the entity and others (e.g. declaring container, return type, ...)
	 * @param bnd -- the JDT Binding 
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public Parameter ensureFamixParameter(IVariableBinding bnd, Method owner) {
		boolean wasBound = false;
		
		if (bnd == null) {
			System.err.println("Warning: Unexpected null binding to Famix Parameter");
			return null;
		}

		if (owner == null) {
			owner = ensureFamixMethod(bnd.getDeclaringMethod(), null, (Collection<org.eclipse.jdt.core.dom.Type>)null, null, null);  // cast needed to desambiguate the call
		}
		
		// actually it seems to be very little chances that the parameter already exist.
		// but who knows? Does not hurt to try
		Parameter fmx = (Parameter) getEntityByKey(bnd);
		
		if (fmx != null) {
			wasBound = true;
		}
		else {
			wasBound = false;
			fmx = (Parameter) ensureFamixEntity(Parameter.class, bnd, bnd.getName());
		}
		
		if ( (fmx!=null) && (! wasBound) ) {
			// declaring method
			if (bnd.getDeclaringMethod() != null) {
				fmx.setParentBehaviouralEntity(ensureFamixMethod(bnd.getDeclaringMethod(), null, (Collection<org.eclipse.jdt.core.dom.Type>)null, null, null));  // cast needed to desambiguate the call
			} else {
				fmx.setParentBehaviouralEntity(owner);
			}
			// type of the attribute
			fmx.setDeclaredType(this.ensureFamixType(bnd.getType(), null, null));
		}
		
		return fmx;
	}
	
	/**
	 * Returns a Famix LocalVariable associated with the IVariableBinding. The Entity is created if it does not exist.
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

		// --------------- name
		if (name == null) {
			name = bnd.getName();
		}

		// --------------- return type
		if (typ == null) {
			typ = this.ensureFamixType(bnd.getType(), null, null);
		}
		// else leave it to null ...

		// --------------- owner
		if (owner == null) {
			owner = ensureFamixMethod(bnd.getDeclaringMethod(), null, (Collection<org.eclipse.jdt.core.dom.Type>)null, null, null);  // cast needed to desambiguate the call
		}
		
		// --------------- recover from name ?
		for (LocalVariable candidate : getEntityByName(LocalVariable.class, name) ) {
			if ( recoverAndMapVariable(bnd, candidate) ) {
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
	 * Returns a Famix UnknownVariable associated with the IVariableBinding. The Entity is created if it does not exist.
	 */
	public UnknownVariable createFamixUnknownVariable(Type type, String name) {
//		System.err.println("TRACE -- createFamixUnknownVariable: "+name);
		UnknownVariable fmx = (UnknownVariable) createFamixEntity(UnknownVariable.class, name);
		if (fmx!=null) {
			fmx.setDeclaredType(type);
		}
		return fmx;
	}
	
	/**
	 * Creates and returns a FAMIX AnnotationInstance and associates it with an Entity and an AnnotationType
	 * @param name -- the name (String) of the annotation 
	 * @param owner -- the entity concerned by this annotation
	 * @return the FAMIX AnnotationInstance
	 */
	public AnnotationInstance createFamixAnnotationInstance(SourcedEntity owner, AnnotationType annType) {
		AnnotationInstance fmx = new AnnotationInstance();
		fmx.setAnnotatedEntity(owner);
		fmx.setAnnotationType(annType);
		this.famixRepo.add(fmx);
		
		return fmx;
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

	/**
	 * Creates or recovers a stub Famix Method
	 * @param name of the method
	 * @return the Famix Method
	 */
	public Method ensureFamixStubMethod(String name) {
		return ensureFamixMethod(null, name, (Collection<org.eclipse.jdt.core.dom.Type>)null, null, ensureFamixClassStubOwner());  // cast needed to desambiguate the call
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
			
			fmx.setIsAbstract(Boolean.FALSE);
			fmx.setIsFinal(Boolean.FALSE);
			fmx.setIsInterface(Boolean.FALSE);
			fmx.setIsPrivate(Boolean.FALSE);
			fmx.setIsProtected(Boolean.FALSE);
			fmx.setIsPublic(Boolean.TRUE);
		}
		// Note: "Object" has no superclass

		return fmx;
	}

	/**
	 * Creates or recovers the Famix Class that will own all stub methods (for which the real owner is unknown)
	 * @return a Famix class
	 */
	public fr.inria.verveine.core.gen.famix.Class ensureFamixClassStubOwner() {
		fr.inria.verveine.core.gen.famix.Class fmx = super.ensureFamixClassStubOwner();
		ensureFamixInheritance(ensureFamixClassObject(null), fmx);

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
			ensureFamixInheritance(ensureFamixClassObject(null), fmx);
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