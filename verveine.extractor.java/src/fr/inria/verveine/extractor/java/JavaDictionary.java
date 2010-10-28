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

import ch.akuhn.fame.Repository;
import fr.inria.verveine.core.Dictionary;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.ContainerEntity;
import fr.inria.verveine.core.gen.famix.FileAnchor;
import fr.inria.verveine.core.gen.famix.LocalVariable;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.Parameter;
import fr.inria.verveine.core.gen.famix.SourceAnchor;
import fr.inria.verveine.core.gen.famix.SourcedEntity;
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
	public static final String PRIMITIVE_PCKG_NAME = "<Primitive Package>";   // for int, boolean, .... types
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

		if (fmx!=null) {
			fmx.setIsStub(Boolean.FALSE);			
		}
		return fmx;
	}

	@Override
	public Namespace ensureFamixNamespace(String name) {
		Namespace fmx = ensureFamixNamespaceWithParentScope(null, name);
		
		if (fmx!=null) {
			fmx.setIsStub(Boolean.FALSE);			
		}
		
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
		
		if (bnd == null) {
			System.err.println("Warning: Unexpected null binding, cannot create Famix Class");
			return null;
		}

		while (bnd.isArray()) {
			bnd = bnd.getComponentType();
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
					if (bnd.isPrimitive()) {
						owner = this.ensureFamixNamespacePrimitives();
					}
					else {
						owner = this.ensureFamixNamespaceDefault();
					}
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
		if ( (! bnd.isPrimitive()) && (! bnd.getName().equals(OBJECT_NAME)) ) {
			// "Object" and primitive types don't have a superclass
			
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
		
		if (fmx == null) {
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
					ContainerEntity ownerBnd = owner;
					ContainerEntity ownerStub = candidate.getBelongsTo();
					while ( (! (ownerBnd instanceof Namespace)) &&
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
			fmx = (fr.inria.verveine.core.gen.famix.Class) ensureFamixStub(fr.inria.verveine.core.gen.famix.Class.class, bnd, bnd.getName());
		}
		
		if ((fmx!=null) && fmx.getIsStub()) {
			// apparently we just created it or it already existed as a stub), so add information to it
			fmx.setIsInterface(bnd.isInterface());
			fmx.setContainer(owner);
			if (sups.size() > 0) {
				// some types don't have superclass
				for (fr.inria.verveine.core.gen.famix.Class sup : sups) {
					ensureFamixInheritance(sup, fmx);
				}
			}
			fmx.setName(identifier); // might be different from bnd.getName() in the case of anonymous class
			fmx.setIsStub(Boolean.FALSE);
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
		fr.inria.verveine.core.gen.famix.Class parentClass = null;
		fr.inria.verveine.core.gen.famix.Class rettyp = null;
		String sig = null;
		
		if (bnd == null) {
			System.err.println("Warning: Unexpected null binding, cannot create Famix Method");
			return null;
		}

		// owner
		parentClass = this.ensureFamixClass(bnd.getDeclaringClass());

		// return type
		if (bnd.isConstructor()) {
			// TODO what to put in metamodel?
		}
		else {
			rettyp = this.ensureFamixClass(bnd.getReturnType());	
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
		
		if (fmx == null) {
			// trying to recover from name and other informations
			for (Method candidate : getEntityByName(Method.class, bnd.getName()) ) {
				if ( (! candidate.getIsStub()) &&
					 (candidate.getParentType() == parentClass) &&
					 (candidate.getDeclaredType() == rettyp) &&
					 (candidate.getSignature().equals(sig)) ) {
					fmx = candidate;
					mapBind.put(bnd, fmx);
					break;
				}
			}
		}
		
		if (fmx == null) {
			// could not recover it, creating a new entity
			fmx = (Method) ensureFamixStub(Method.class, bnd, bnd.getName());
		}
		
		if ((fmx!=null) && fmx.getIsStub()) {
			// apparently we just created it, so add information to it
			fmx.setParentType(parentClass);
			fmx.setDeclaredType(rettyp);	
			fmx.setName(bnd.getName());
			fmx.setSignature(sig);
			fmx.setIsStub(Boolean.FALSE);
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
		fr.inria.verveine.core.gen.famix.Class parentClass = null;
		fr.inria.verveine.core.gen.famix.Class typ = null;

		if (bnd == null) {
			System.err.println("Warning: Unexpected null binding, cannot create Famix Attribute");
			return null;
		}

		parentClass = this.ensureFamixClass(bnd.getDeclaringClass());
		typ = this.ensureFamixClass(bnd.getType());

		// finally trying to recover the entity or creating it
		Attribute fmx = null;
		// ... trying to recover from binding
		fmx = (Attribute) getEntityByBinding(bnd);
		
		if (fmx == null) {
			// trying to recover from name and other informationsparentBehaviouralEntity
			for (Attribute candidate : getEntityByName(Attribute.class, bnd.getName()) ) {
				if ( (! candidate.getIsStub()) &&
					 (candidate.getParentType() == parentClass) &&
					 (candidate.getDeclaredType() == typ) ) {
					fmx = candidate;
					mapBind.put(bnd, fmx);
					break;
				}
			}
		}
		
		if (fmx == null) {
			// could not recover it, creating a new entity
			fmx = ensureFamixStub(Attribute.class, bnd, bnd.getName());
		}
		
		if ((fmx!=null) && fmx.getIsStub()) {
			// apparently we just created it, so add information to it
			fmx.setParentType(parentClass);
			fmx.setDeclaredType(typ);	
			fmx.setName(bnd.getName());
			fmx.setIsStub(Boolean.FALSE);
		}

		return fmx;
	}

	/**
	 * Returns a Famix Paramenter associated with the IVariableBinding. The Entity is created if it does not exist.
	 * The JDT Binding is a unique representation of a java entity within the AST.
	 * This method also creates some basic links between the entity and others (e.g. declaring container, return type, ...)
	 * @param bnd -- the JDT Binding 
	 * @return the Famix Entity found or created. May return null if "bnd" is null or in case of a Famix error
	 */
	public Parameter ensureFamixParameter(IVariableBinding bnd) {
		if (bnd == null) {
			System.err.println("Warning: Unexpected null binding, cannot create Famix Parameter");
			return null;
		}

		Parameter fmx = (Parameter) ensureFamixStub(Parameter.class, bnd, bnd.getName());
		if ( (fmx!=null) && fmx.getIsStub() ) {
			// declaring method
			fmx.setParentBehaviouralEntity(ensureFamixMethod(bnd.getDeclaringMethod()));

			// type of the attribute
			fmx.setDeclaredType(this.ensureFamixClass(bnd.getType()));

			fmx.setIsStub(Boolean.FALSE);
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
		Method parentMeth = null;
		fr.inria.verveine.core.gen.famix.Class typ = null;

		if (bnd == null) {
			System.err.println("Warning: Unexpected null binding, cannot create Famix LocalVariable");
			return null;
		}

		parentMeth = this.ensureFamixMethod(bnd.getDeclaringMethod());
		typ = this.ensureFamixClass(bnd.getType());

		// finally trying to recover the entity or creating it
		LocalVariable fmx = null;
		// ... trying to recover from binding
		fmx = (LocalVariable) getEntityByBinding(bnd);

		if (fmx == null) {
			// trying to recover from name and other informationsparentBehaviouralEntity
			for (LocalVariable candidate : getEntityByName(LocalVariable.class, bnd.getName()) ) {
				if ( (! candidate.getIsStub()) &&
					 (candidate.getParentBehaviouralEntity() == parentMeth) &&
					 (candidate.getDeclaredType() == typ) ) {
					fmx = candidate;
					mapBind.put(bnd, fmx);
					break;
				}
			}
		}
		
		if (fmx == null) {
			// could not recover it, creating a new entity
			fmx = ensureFamixStub(LocalVariable.class, bnd, bnd.getName());
		}

		if ( (fmx!=null) && fmx.getIsStub() ) {
			// apparently we just created it, so add information to it
			fmx.setParentBehaviouralEntity(parentMeth);
			fmx.setDeclaredType(this.ensureFamixClass(bnd.getType()));
			fmx.setIsStub(Boolean.FALSE);
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
	public UnknownVariable createFamixUnknownVariable(fr.inria.verveine.core.gen.famix.Class ofType, String name) {
//		System.err.println("TRACE -- createFamixUnknownVariable: "+name);
		UnknownVariable fmx = (UnknownVariable) createFamixStub(UnknownVariable.class, name);
		if (fmx!=null) {
			fmx.setDeclaredType(ofType);
			fmx.setIsStub(Boolean.FALSE);
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
			int pos = ast.getStartPosition();
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
			fa.setStartLine(((CompilationUnit)ast).getLineNumber(pos));
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
		
		fmx.setIsStub(false);

		return fmx;
	}

	/**
	 * Creates or recovers a Famix Namespace for the primitive types in Java.
	 * Because this package does not really exist, it has no binding.
	 * @return a Famix Namespace
	 */
	public Namespace ensureFamixNamespacePrimitives() {
		Namespace fmx =  ensureFamixUniqEntity(Namespace.class, null, PRIMITIVE_PCKG_NAME);
		fmx.setIsStub(false);

		return fmx;
	}

	/**
	 * Creates or recovers a Famix Class for the Java "Object".
	 * @param bnd -- a potential binding for the java "object" class
	 * @return a Famix class for Java "Object"
	 */
	public fr.inria.verveine.core.gen.famix.Class ensureFamixClassObject(ITypeBinding bnd) {
		fr.inria.verveine.core.gen.famix.Class fmx =  ensureFamixUniqEntity(fr.inria.verveine.core.gen.famix.Class.class, bnd, OBJECT_NAME);
		
		fmx.setContainer( ensureFamixNamespaceJavaLang(null));
		// Note: "Object" has no superclass
		fmx.setIsStub(false);

		return fmx;
	}

	@Override
	public fr.inria.verveine.core.gen.famix.Class ensureFamixClassStubOwner() {
		fr.inria.verveine.core.gen.famix.Class fmx = super.ensureFamixClassStubOwner();
		ensureFamixInheritance(ensureFamixClassObject(null), fmx);

		return fmx;
	}

}
