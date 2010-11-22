package fr.inria.verveine.extractor.java;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;

import ch.akuhn.fame.Repository;
import fr.inria.verveine.core.Dictionary;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.ContainerEntity;
import fr.inria.verveine.core.gen.famix.FileAnchor;
import fr.inria.verveine.core.gen.famix.LocalVariable;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.NamedEntity;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.Parameter;
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
	 * Returns a Famix Namespace associated with the IPackageBinding. The Entity is created if it does not exist.
	 * The JDT Binding is a unique representation of a java entity within the AST.
	 * This method also creates some basic links between the entity and others (e.g. declaring container, return type, ...)
	 * @param bnd -- the JDT Binding 
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public Namespace ensureFamixNamespace(IPackageBinding bnd) {
		if (bnd == null) {
			System.err.println("Warning: Unexpected null binding, cannot create Famix Namespace");
			return null;
		}

		Namespace fmx = ensureFamixNamespaceWithParentScope(bnd, null);

		return fmx;
	}

	@Override
	public Namespace ensureFamixNamespace(String name) {
		Namespace fmx = ensureFamixNamespaceWithParentScope(null, name);

		return fmx;
	}

	/**
	 * Creates or recovers a namespace. Also creates or recovers recusively it's parent namespaces
	 * At least one of bnd and name must be passed, possibly both
	 * @param bnd - the (optional) binding for the namespace
	 * @param name - the (optional) full name for the namespace
	 * @return the namespace created or null
	 */
	private Namespace ensureFamixNamespaceWithParentScope(IPackageBinding bnd, String name) {
		Namespace fmx = null;
		Namespace parent = null;
		String[] nameComps;
		
		if (bnd != null) {
			nameComps = bnd.getNameComponents();
		}
		else {
			if (name != null) {
				nameComps = name.split("\\.");
			}
			else {
				return null;
			}
		}

		for (String nameComp : nameComps ) {
			fmx = ensureFamixUniqEntity(Namespace.class, null, nameComp);
			if ( (parent != null) && (fmx != null) && (fmx.getParentScope() == null)) {
				parent.addChildScopes(fmx);
			}
			parent = fmx;
		}
		
		if ( (fmx != null) && (bnd != null) ) {
			mapBind.put(bnd, fmx);
		}

		return fmx;
	}

	public Type ensureFamixType(ITypeBinding bnd) {
		if (bnd == null) {
			System.err.println("Warning: Unexpected null binding, cannot create Famix Type");
			return null;
		}

		while (bnd.isArray()) {
			bnd = bnd.getComponentType();
		}
		
		if (bnd.isPrimitive()) {
			return ensureFamixPrimitiveType(bnd);
		}
		else {
			return ensureFamixClass(bnd);
		}
	}

	public PrimitiveType ensureFamixPrimitiveType(ITypeBinding bnd) {

		if (bnd == null) {
			System.err.println("Warning: Unexpected null binding, cannot create Famix Primitive Type");
			return null;
		}
		
		PrimitiveType fmx = super.ensureFamixPrimitiveType(bnd.getName());
		fmx.setIsStub(false);
		mapBind.put(bnd, fmx);
		return fmx;
	}

	/**
	 * Returns a Famix Class associated with the ITypeBinding. The Entity is created if it does not exist.
	 * The JDT Binding is a unique representation of a java entity within the AST.
	 * This method also creates some basic links between the entity and others (e.g. declaring container, return type, ...)
	 * @param bnd -- the JDT Binding 
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public fr.inria.verveine.core.gen.famix.Class ensureFamixClass(ITypeBinding bnd) {
		ContainerEntity owner = null;
		Collection<fr.inria.verveine.core.gen.famix.Class> sups = new LinkedList<fr.inria.verveine.core.gen.famix.Class>();
		String identifier = null;
		boolean wasBound = false;
		
		if (bnd == null) {
			System.err.println("Warning: Unexpected null binding, cannot create Famix Class");
			return null;
		}

		while (bnd.isArray()) {
			bnd = bnd.getComponentType();
		}
		
		if (bnd.isPrimitive()) {
			// should have called ensureFamixPrimitiveType(bnd). Why are we here ?
			System.err.println("Warning: cannot create Famix Class from a primitive type");
			return null;
		}
		
		// container
		IMethodBinding parentMtd = bnd.getDeclaringMethod();
		if (parentMtd != null) {
			owner = this.ensureFamixMethod(parentMtd);
		}
		else {
			ITypeBinding parentClass = bnd.getDeclaringClass();
			if (parentClass != null) {
				owner = this.ensureFamixClass(parentClass);
			}
			else {
				IPackageBinding parentPckg = bnd.getPackage();
				if (parentPckg != null) {
					owner = this.ensureFamixNamespace(parentPckg);
				}
				else {
					owner = this.ensureFamixNamespaceDefault();
				}
			}
		}

		// name
		if (bnd.isAnonymous()) {
			if (bnd.getSuperclass() != null) {
				identifier = bnd.getSuperclass().getName();
			}
			if ( (identifier == null) || identifier.equals(OBJECT_NAME)) {
				ITypeBinding[] intfcs = bnd.getInterfaces();
				if ( (intfcs != null) && (intfcs.length > 0) ) {
					identifier = bnd.getInterfaces()[0].getName();
				}
				else {
					identifier = "???";
				}
			}
			identifier = "anonymous(" + identifier + ")";
		}
		else {
			identifier = bnd.getName();
		}

		// superclass and/or implemented interfaces
		if (! bnd.getName().equals(OBJECT_NAME)) {
			// "Object" does't have a superclass
			
			// superclass
			if (! bnd.isInterface()) {
				ITypeBinding supbnd = bnd.getSuperclass();
				if (supbnd == null) {
					sups.add( ensureFamixClassObject(null));
				}
				else {
					sups.add( ensureFamixClass(supbnd));
				}
			}
			else {
				for (ITypeBinding supbnd : bnd.getInterfaces()) {
					sups.add( ensureFamixClass(supbnd));
				}
			}
		}

		// finally trying to recover the entity or creating it
		fr.inria.verveine.core.gen.famix.Class fmx = null;
		// ... trying to recover from binding
		fmx = (fr.inria.verveine.core.gen.famix.Class) getEntityByBinding(bnd);
		
		if (fmx != null) {
			wasBound = true;
		}
		else {
			wasBound = false;
			// trying to recover from name and other informations
			for (fr.inria.verveine.core.gen.famix.Class candidate : getEntityByName(fr.inria.verveine.core.gen.famix.Class.class, identifier) ) {
				if ( (! candidate.getIsStub()) &&
						(candidate.getBelongsTo() == owner) ) {
					// could test superclass also...
					fmx = candidate;
					mapBind.put(bnd, fmx);
					break;
				}
				else if ( candidate.getIsStub() ) {
					// find out whether this candidate is defined in the same namespace as the binding received in parameter
					ContainerEntity ownerBnd = owner;  // the owner of the bounded entity received as parameter
					ContainerEntity ownerStub = candidate.getBelongsTo();  // the owner of the current candidate
					while ( (ownerBnd != null) &&
							(ownerStub != null) &&
							(! (ownerBnd instanceof Namespace)) &&
							(ownerBnd.getClass() == ownerStub.getClass()) &&
							ownerBnd.getName().equals(ownerStub.getName()) ) {
								ownerBnd = ownerBnd.getBelongsTo();
								ownerStub = ownerStub.getBelongsTo();
					}
					if ( (ownerBnd instanceof Namespace) &&
						(ownerStub instanceof Namespace) &&
						ownerBnd.getName().equals(ownerStub.getName()) ) {
						fmx = candidate;
						mapBind.put(bnd, fmx);
						break;
					}
				}
			}
		}
		
		if (fmx == null) {
			// could not recover it, creating a new entity
			fmx = (fr.inria.verveine.core.gen.famix.Class) ensureFamixEntity(fr.inria.verveine.core.gen.famix.Class.class, bnd, bnd.getName());
		}
		
		if ((fmx!=null) && (! wasBound)) {
			// apparently we just created it or it already existed but was not bound, so add information to it
			fmx.setIsInterface(bnd.isInterface());
			fmx.setContainer(owner);
			if (sups.size() > 0) {
				// some types don't have superclass
				for (fr.inria.verveine.core.gen.famix.Class sup : sups) {
					ensureFamixInheritance(sup, fmx);
				}
			}
			setNamedEntityModifiers(fmx, bnd.getDeclaredModifiers());
			if (fmx.getIsAbstract()) {
				// don't know why there must be two different ways to mark abstract classes !!! But this is a pain!
				fmx.addModifiers("abstract");
			}
			fmx.setName(identifier); // might be different from bnd.getName() in the case of anonymous class
		}
	
		return fmx;
	}

	/**
	 * Returns a Famix Method associated with the IMethodBinding. The Entity is created if it does not exist.
	 * The JDT Binding is a unique representation of a java entity within the AST.
	 * This method also creates some basic links between the entity and others (e.g. declaring container, return type, ...)
	 * @param bnd -- the JDT Binding 
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public Method ensureFamixMethod(IMethodBinding bnd) {
		fr.inria.verveine.core.gen.famix.Class owner = null;
		Type rettyp = null;
		String sig = null;
		boolean wasBound = false;
		
		if (bnd == null) {
			System.err.println("Warning: Unexpected null binding, cannot create Famix Method");
			return null;
		}

		// owner
		owner = this.ensureFamixClass(bnd.getDeclaringClass());

		// return type
		if (bnd.isConstructor()) {
			// TODO what to put in metamodel?
		}
		else {
			rettyp = this.ensureFamixType(bnd.getReturnType());	
		}

		// method signature
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

		// finally trying to recover the entity or creating it
		Method fmx = null;
		// ... trying to recover from binding
		fmx = (Method) getEntityByBinding(bnd);
		
		if (fmx != null) {
			wasBound = true;
		}
		else {
			wasBound = false;
			// trying to recover from name and other informations
			for (Method candidate : getEntityByName(Method.class, bnd.getName()) ) {
				if ( (candidate.getParentType() == owner) &&
					 (candidate.getDeclaredType() == rettyp) &&
					 (candidate.getSignature().equals(sig)) ) {
					// we could also test that this candidate is not bound yet (to another bnd)
					// but it requires significant modifications and might not be that useful?
					fmx = candidate;
					mapBind.put(bnd, fmx);
					break;
				}
			}
		}
		
		if (fmx == null) {
			// could not recover it, creating a new entity
			fmx = (Method) ensureFamixEntity(Method.class, bnd, bnd.getName());
		}
		
		if ((fmx!=null) && (! wasBound) ) {
			// apparently we just created it or it already existed as a stub, so add information to it
			fmx.setParentType(owner);
			fmx.setDeclaredType(rettyp);	
			fmx.setName(bnd.getName());
			fmx.setSignature(sig);
			setNamedEntityModifiers(fmx, bnd.getModifiers());
		}

		return fmx;
	}

	/**
	 * Returns a Famix Attribute associated with the IVariableBinding. The Entity is created if it does not exist.
	 * The JDT Binding is a unique representation of a java entity within the AST.
	 * This method also creates some basic links between the entity and others (e.g. declaring container, return type, ...)
	 * @param bnd -- the JDT Binding 
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public Attribute ensureFamixAttribute(IVariableBinding bnd) {
		fr.inria.verveine.core.gen.famix.Class owner = null;
		Type typ = null;
		boolean wasBound = false;

		if (bnd == null) {
			System.err.println("Warning: Unexpected null binding, cannot create Famix Attribute");
			return null;
		}

		owner = this.ensureFamixClass(bnd.getDeclaringClass());
		typ = this.ensureFamixType(bnd.getType());

		// finally trying to recover the entity or creating it
		Attribute fmx = null;
		// ... trying to recover from binding
		fmx = (Attribute) getEntityByBinding(bnd);
		
		if (fmx != null) {
			wasBound = true;
		}
		else {
			wasBound = false;
			// trying to recover from name and other informationsparentBehaviouralEntity
			for (Attribute candidate : getEntityByName(Attribute.class, bnd.getName()) ) {
				if ( (! candidate.getIsStub()) &&
					 (candidate.getParentType() == owner) &&
					 (candidate.getDeclaredType() == typ) ) {
					fmx = candidate;
					mapBind.put(bnd, fmx);
					break;
				}
			}
		}
		
		if (fmx == null) {
			// could not recover it, creating a new entity
			fmx = ensureFamixEntity(Attribute.class, bnd, bnd.getName());
		}
		
		if ((fmx!=null) && (! wasBound) ) {
			// apparently we just created it, so add information to it
			setNamedEntityModifiers(fmx, bnd.getModifiers());
			fmx.setParentType(owner);
			fmx.setDeclaredType(typ);	
			fmx.setName(bnd.getName());
		}

		return fmx;
	}

	/** Sets the modifiers (abstract, public, ...) of a FamixNamedEntity
	 * @param fmx -- the FamixNamedEntity
	 * @param mod -- a description of the modifiers as understood by org.eclipse.jdt.core.dom.Modifier
	 */
	private void setNamedEntityModifiers(NamedEntity fmx, int mod) {
		fmx.setIsAbstract(Modifier.isAbstract(mod));
		fmx.setIsFinal(Modifier.isFinal(mod));
		fmx.setIsPrivate(Modifier.isPrivate(mod));
		fmx.setIsProtected(Modifier.isProtected(mod));
		fmx.setIsPublic(Modifier.isPublic(mod));
	}

	/**
	 * Returns a Famix Paramenter associated with the IVariableBinding. The Entity is created if it does not exist.
	 * The JDT Binding is a unique representation of a java entity within the AST.
	 * This method also creates some basic links between the entity and others (e.g. declaring container, return type, ...)
	 * @param bnd -- the JDT Binding 
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public Parameter ensureFamixParameter(IVariableBinding bnd) {
		boolean wasBound = false;
		
		if (bnd == null) {
			System.err.println("Warning: Unexpected null binding, cannot create Famix Parameter");
			return null;
		}

		// actually it seems to be very little chances that the aprameter alreasy exist.
		// but who knows? Does not hurt to try
		Parameter fmx = (Parameter) getEntityByBinding(bnd);
		
		if (fmx != null) {
			wasBound = true;
		}
		else {
			wasBound = false;
			fmx = (Parameter) ensureFamixEntity(Parameter.class, bnd, bnd.getName());
		}
		
		if ( (fmx!=null) && (! wasBound) ) {
			// declaring method
			fmx.setParentBehaviouralEntity(ensureFamixMethod(bnd.getDeclaringMethod()));
			// type of the attribute
			fmx.setDeclaredType(this.ensureFamixType(bnd.getType()));
		}
		
		return fmx;
	}

	/**
	 * Returns a Famix LocalVariable associated with the IVariableBinding. The Entity is created if it does not exist.
	 * The JDT Binding is a unique representation of a java entity within the AST.
	 * This method also creates some basic links between the entity and others (e.g. declaring container, return type, ...)
	 * @param bnd -- the JDT Binding 
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public LocalVariable ensureFamixLocalVariable(IVariableBinding bnd) {
		Method owner = null;
		Type typ = null;
		boolean wasBound = false;

		if (bnd == null) {
			System.err.println("Warning: Unexpected null binding, cannot create Famix LocalVariable");
			return null;
		}

		owner = this.ensureFamixMethod(bnd.getDeclaringMethod());
		typ = this.ensureFamixType(bnd.getType());

		// finally trying to recover the entity or creating it
		LocalVariable fmx = null;
		// ... trying to recover from binding
		fmx = (LocalVariable) getEntityByBinding(bnd);

		if (fmx != null) {
			wasBound = true;
		}
		else {
			wasBound = false;
			// trying to recover from name and other informationsparentBehaviouralEntity
			for (LocalVariable candidate : getEntityByName(LocalVariable.class, bnd.getName()) ) {
				if ( (candidate.getParentBehaviouralEntity() == owner) &&
					 (candidate.getDeclaredType() == typ) ) {
					fmx = candidate;
					mapBind.put(bnd, fmx);
					break;
				}
			}
		}
		
		if (fmx == null) {
			// could not recover it, creating a new entity
			fmx = ensureFamixEntity(LocalVariable.class, bnd, bnd.getName());
		}

		if ( (fmx!=null) && (! wasBound) ) {
			// apparently we just created it, so add information to it
			fmx.setParentBehaviouralEntity(owner);
			fmx.setDeclaredType(typ);
		}
		
		return fmx;
	}

	/**
	 * Returns a Famix UnknownVariable associated with the IVariableBinding. The Entity is created if it does not exist.
	 * The JDT Binding is a unique representation of a java entity within the AST.
	 * This method also creates some basic links between the entity and others (e.g. declaring container, return type, ...)
	 * @param bnd -- the JDT Binding 
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
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
					// we reached the top node without finding a CompilationUnit. This would be strange, but what can one do ... ?
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
		Method fmx = ensureFamixMethod(name);
		fmx.setSignature(name + " (<unknown-stub>)");
		fmx.setParentType( ensureFamixClassStubOwner());
		fmx.setDeclaredType(ensureFamixClassObject(null));
		
		return fmx;
	}

	/**
	 * Creates or recovers a Famix Namespace for the package of Java class "Object" (i.e. "java.lang").
	 * Because "Object" is the root of the inheritance tree, it needs to be treated differently.
	 * @param bnd -- a potential binding for the "java.lang" package
	 * @return a Famix Namespace for "java.lang"
	 */
	public Namespace ensureFamixNamespaceJavaLang(IPackageBinding bnd) {
		Namespace fmx = ensureFamixNamespaceWithParentScope(bnd, OBJECT_PACKAGE_NAME);

		return fmx;
	}

	/**
	 * Creates or recovers the Famix Class for "Object".
	 * @param bnd -- a potential binding for the java "Object" class
	 * @return a Famix class for "Object"
	 */
	public fr.inria.verveine.core.gen.famix.Class ensureFamixClassObject(ITypeBinding bnd) {
		fr.inria.verveine.core.gen.famix.Class fmx =  ensureFamixUniqEntity(fr.inria.verveine.core.gen.famix.Class.class, bnd, OBJECT_NAME);
		
		fmx.setContainer( ensureFamixNamespaceJavaLang(null));
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
		ensureFamixInheritance(ensureFamixClassObject(null), fmx);
		fmx.setContainer( ensureFamixNamespaceDefault());

		return fmx;
	}

}
