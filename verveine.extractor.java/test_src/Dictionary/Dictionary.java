package fr.inria.verveine.core;

import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import ch.akuhn.fame.Repository;

import fr.inria.verveine.core.gen.famix.Access;
import fr.inria.verveine.core.gen.famix.Association;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.BehaviouralEntity;
import fr.inria.verveine.core.gen.famix.CaughtException;
import fr.inria.verveine.core.gen.famix.Comment;
import fr.inria.verveine.core.gen.famix.ContainerEntity;
import fr.inria.verveine.core.gen.famix.DeclaredException;
import fr.inria.verveine.core.gen.famix.Entity;
import fr.inria.verveine.core.gen.famix.ImplicitVariable;
import fr.inria.verveine.core.gen.famix.Inheritance;
import fr.inria.verveine.core.gen.famix.Invocation;
import fr.inria.verveine.core.gen.famix.LocalVariable;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.NamedEntity;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.Parameter;
import fr.inria.verveine.core.gen.famix.PrimitiveType;
import fr.inria.verveine.core.gen.famix.Reference;
import fr.inria.verveine.core.gen.famix.SourcedEntity;
import fr.inria.verveine.core.gen.famix.StructuralEntity;
import fr.inria.verveine.core.gen.famix.ThrownException;

public class Dictionary<B> {

	public static final String DEFAULT_PCKG_NAME = "<Default Package>";
	public static final String STUB_METHOD_CONTAINER_NAME = "<StubMethodContainer>";
	public static final String SELF_NAME = "self";
	public static final String SUPER_NAME = "super";

	protected Repository famixRepo;

	protected Map<B,NamedEntity> mapBind;

	protected Map<String,Collection<NamedEntity>> mapName;

	protected Map<fr.inria.verveine.core.gen.famix.Class,ImplicitVars> mapImpVar;

	protected class ImplicitVars {
		public ImplicitVariable self_iv;
		public ImplicitVariable super_iv;
	}

	/* method added to test a specific bug with constructor declaration having a null binding */
	public Dictionary(T arg) {
		System.out.println(arg + " is not used");
	}
	
	public Dictionary(Repository famixRepo) {
		this.famixRepo = famixRepo;
		
		this.mapBind = new Hashtable<B,NamedEntity>();
		this.mapName = new Hashtable<String,Collection<NamedEntity>>();
		this.mapImpVar = new Hashtable<fr.inria.verveine.core.gen.famix.Class,ImplicitVars>();
		
		if (! this.famixRepo.isEmpty()) {
			recoverExistingRepository();
		}
	}

	protected void recoverExistingRepository() {
		for (Object obj : famixRepo.getElements()) {
			if (obj instanceof NamedEntity) {
				mapEntityToName( ((NamedEntity)obj).getName(), (NamedEntity) obj);
			}
		}
		
		for (Access acc : famixRepo.all(Access.class)) {
			try {
				if (acc.getIsWrite()) { }
			}
			catch (NullPointerException e) {
				acc.setIsWrite(Boolean.FALSE);
			}
		}

		for (fr.inria.verveine.core.gen.famix.Class clazz : famixRepo.all(fr.inria.verveine.core.gen.famix.Class.class)) {
			try { if (clazz.getIsAbstract()) { } }
			catch (NullPointerException e) { clazz.setIsAbstract(Boolean.FALSE); }
			try { if (clazz.getIsStub()) { } }
			catch (NullPointerException e) { clazz.setIsStub(Boolean.FALSE); }
			try { if (clazz.getIsPublic()) { } }
			catch (NullPointerException e) { clazz.setIsPublic(Boolean.FALSE); }
			try { if (clazz.getIsPrivate()) { } }
			catch (NullPointerException e) { clazz.setIsPrivate(Boolean.FALSE); }
			try { if (clazz.getIsProtected()) { } }
			catch (NullPointerException e) { clazz.setIsProtected(Boolean.FALSE); }
			try { if (clazz.getIsFinal()) { } }
			catch (NullPointerException e) { clazz.setIsFinal(Boolean.FALSE); }
		}

		for (Method meth : famixRepo.all(Method.class)) {
			try { if (meth.getIsAbstract()) { } }
			catch (NullPointerException e) { meth.setIsAbstract(Boolean.FALSE); }
			try { if (meth.getIsStub()) { } }
			catch (NullPointerException e) { meth.setIsStub(Boolean.FALSE); }
			try { if (meth.getIsPublic()) { } }
			catch (NullPointerException e) { meth.setIsPublic(Boolean.FALSE); }
			try { if (meth.getIsPrivate()) { } }
			catch (NullPointerException e) { meth.setIsPrivate(Boolean.FALSE); }
			try { if (meth.getIsProtected()) { } }
			catch (NullPointerException e) { meth.setIsProtected(Boolean.FALSE); }
			try { if (meth.getIsFinal()) { } }
			catch (NullPointerException e) { meth.setIsFinal(Boolean.FALSE); }
		}

		for (Attribute att : famixRepo.all(Attribute.class)) {
			try { if (att.getIsAbstract()) { } }
			catch (NullPointerException e) { att.setIsAbstract(Boolean.FALSE); }
			try { if (att.getIsStub()) { } }
			catch (NullPointerException e) { att.setIsStub(Boolean.FALSE); }
			try { if (att.getIsPublic()) { } }
			catch (NullPointerException e) { att.setIsPublic(Boolean.FALSE); }
			try { if (att.getIsPrivate()) { } }
			catch (NullPointerException e) { att.setIsPrivate(Boolean.FALSE); }
			try { if (att.getIsProtected()) { } }
			catch (NullPointerException e) { att.setIsProtected(Boolean.FALSE); }
			try { if (att.getIsFinal()) { } }
			catch (NullPointerException e) { att.setIsFinal(Boolean.FALSE); }
		}

	}
	
	protected void mapEntityToName(String name, NamedEntity ent) {
		Collection<NamedEntity> l_ent = mapName.get(name);
		if (l_ent == null) {
			l_ent = new LinkedList<NamedEntity>();
		}
		l_ent.add(ent);
		mapName.put(name, l_ent);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends NamedEntity> Collection<T> getEntityByName(Class<T> fmxClass, String name) {
		Collection<T> ret = new LinkedList<T>();
		Collection<NamedEntity> l_name = mapName.get(name);
		
		if (l_name != null ) {
			for (NamedEntity obj : l_name) {
				if (fmxClass.isInstance(obj)) {
					ret.add((T) obj);
				}
			}
		}

		return ret;
	}

	public NamedEntity getEntityByBinding(B bnd) {
		if (bnd == null) {
			return null;
		}
		else {
			return mapBind.get(bnd);
		}
	}

	protected <T extends NamedEntity> T createFamixEntity(Class<T> fmxClass, String name) {
		T fmx = null;

		try {
			fmx = fmxClass.newInstance();
		} catch (Exception e) {
			System.err.println("Unexpected error, could not create a FAMIX entity: "+e.getMessage());
			e.printStackTrace();
		}
		
		if (fmx != null) {
			fmx.setName(name);
			fmx.setIsStub(Boolean.TRUE);

			mapEntityToName(name, fmx);
			
			// put new entity in Famix repository
			this.famixRepo.add(fmx);
		}

		return fmx;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends NamedEntity> T ensureFamixEntity(Class<T> fmxClass, B bnd, String name) {
		T fmx = null;
		
		if (ImplicitVariable.class.isAssignableFrom(fmxClass)) {
			return null;
		}
		
		if (bnd != null) {
			fmx = (T) getEntityByBinding(bnd);
		}
		else {
			// Unfortunately different entities with the same name and same type may exist
			// e.g. 2 parameters of 2 different methods but having the same name
			// so we must recreate a new entity each time

			//fmxEnt = getEntityByName(fmxClass, name);
		}

		if (fmx != null) {
			return fmx;
		}

		fmx = createFamixEntity(fmxClass, name);
		// put new entity in mappers
		if (bnd != null) {
			mapBind.put(bnd, fmx);
		}
		
		return fmx;
	}

	public void famixRepoAdd(Entity e) {
		this.famixRepo.add(e);
	}

	public fr.inria.verveine.core.gen.famix.Class ensureFamixClass(String name) {
		fr.inria.verveine.core.gen.famix.Class fmx = ensureFamixEntity(fr.inria.verveine.core.gen.famix.Class.class, null, name);
		if (fmx != null) {
			fmx.setIsAbstract(Boolean.FALSE);
			fmx.setIsFinal(Boolean.FALSE);
			fmx.setIsInterface(Boolean.FALSE);
			fmx.setIsPrivate(Boolean.FALSE);
			fmx.setIsProtected(Boolean.FALSE);
			fmx.setIsPublic(Boolean.FALSE);
		}

		return fmx;
	}

	public Method ensureFamixMethod(String name) {
		return (Method) ensureFamixEntity(Method.class, null, name);
	}

	public Attribute ensureFamixAttribute(String name) {
		return (Attribute) ensureFamixEntity(Attribute.class, null, name);
	}

	public LocalVariable ensureFamixLocalVariable(String name) {
		return (LocalVariable) ensureFamixEntity(LocalVariable.class, null, name);
	}

	public Comment createFamixComment(String cmt) {
		Comment fmx = new Comment();
		fmx.setContent(cmt);
		this.famixRepo.add(fmx);
		
		return fmx;
	}

	public Comment createFamixComment(String cmt, SourcedEntity owner) {
		Comment fmx = new Comment();
		fmx.setContent(cmt);
		fmx.setContainer(owner);
		this.famixRepo.add(fmx);
		
		return fmx;
	}

	public Parameter createFamixParameter(String identifier, BehaviouralEntity owner, String type) {
		Parameter fmx = new Parameter();
		fmx.setName(identifier);
		fmx.setParentBehaviouralEntity(owner);
		fmx.setDeclaredType(ensureFamixUniqEntity(fr.inria.verveine.core.gen.famix.Class.class, null, type));
		this.famixRepo.add(fmx);
		
		return fmx;
	}
	
	public Inheritance ensureFamixInheritance(fr.inria.verveine.core.gen.famix.Class sup, fr.inria.verveine.core.gen.famix.Class sub) {
		return ensureFamixInheritance(sup, sub, null);
	}
	
	public Inheritance ensureFamixInheritance(fr.inria.verveine.core.gen.famix.Class sup, fr.inria.verveine.core.gen.famix.Class sub, Association prev) {
		for (Inheritance i : sup.getSubInheritances()) {
			if (i.getSubclass() == sub) {
				return i;
			}
		}
		Inheritance inh = new Inheritance();
		inh.setSuperclass(sup);
		inh.setSubclass(sub);
		chainPrevNext(prev,inh);
		famixRepoAdd(inh);
		return inh;
	}

	public Reference ensureFamixReference(ContainerEntity src, ContainerEntity tgt) {
		return ensureFamixReference(src, tgt, null);
	}
	
	public Reference ensureFamixReference(ContainerEntity src, ContainerEntity tgt, Association prev) {
		Reference ref = new Reference();
		ref.setTarget(tgt);
		ref.setSource(src);
		chainPrevNext(prev,ref);
		famixRepoAdd(ref);
		
		return ref;
	}

	public Invocation ensureFamixInvocation(BehaviouralEntity sender, BehaviouralEntity invoked, NamedEntity receiver) {
		return ensureFamixInvocation(sender, invoked, receiver, null);
	}

	public Invocation ensureFamixInvocation(BehaviouralEntity sender, BehaviouralEntity invoked, NamedEntity receiver, Association prev) {
		Invocation invok = new Invocation();
		invok.setReceiver(receiver);
		invok.setSender(sender);
		invok.setSignature(invoked.getSignature());
		invok.addCandidates(invoked);
		chainPrevNext(prev,invok);
		famixRepoAdd(invok);
		
		return invok;
	}

	public Access ensureFamixAccess(BehaviouralEntity accessor, StructuralEntity var, boolean isWrite, Association prev) {
		Access acc = new Access();
		acc.setAccessor(accessor);
		acc.setVariable(var);
		acc.setIsWrite(new Boolean(isWrite));
		chainPrevNext(prev, acc);
		famixRepoAdd(acc);
		
		return acc;
	}

	public Access ensureFamixAccess(BehaviouralEntity accessor, StructuralEntity var, boolean isWrite) {
		return ensureFamixAccess(accessor, var, isWrite, null);
	}
	
	public Access ensureFamixAccess(BehaviouralEntity accessor, StructuralEntity var) {
		return ensureFamixAccess(accessor, var, false, null);  // must set some default for isWrite and this one seems safer than the opposite
	}

	private void chainPrevNext(Association prev, Association next) {
		if (prev != null) {
			next.setPrevious(prev);  // not yet implemented in importer
		}
	}

	public DeclaredException ensureFamixDeclaredException(Method meth, fr.inria.verveine.core.gen.famix.Class excep) {
		DeclaredException decl = new DeclaredException();
		decl.setExceptionClass(excep);
		decl.setDefiningMethod(meth);
		famixRepoAdd(decl);
		return decl;
	}

	public CaughtException ensureFamixCaughtException(Method meth, fr.inria.verveine.core.gen.famix.Class excep) {
		CaughtException decl = new CaughtException();
		decl.setExceptionClass(excep);
		decl.setDefiningMethod(meth);
		famixRepoAdd(decl);
		return decl;
	}

	public ThrownException ensureFamixThrownException(Method meth, fr.inria.verveine.core.gen.famix.Class excep) {
		ThrownException decl = new ThrownException();
		decl.setExceptionClass(excep);
		decl.setDefiningMethod(meth);
		famixRepoAdd(decl);
		return decl;
	}

	public ImplicitVariable getImplicitVariableByBinding(B bnd, String iv_name) {
		return getImplicitVariableByClass((fr.inria.verveine.core.gen.famix.Class)getEntityByBinding(bnd), iv_name);
	}

	public ImplicitVariable getImplicitVariableByClass(fr.inria.verveine.core.gen.famix.Class clazz, String name) {
		ImplicitVars iv = mapImpVar.get(clazz);
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

	public ImplicitVariable ensureFamixImplicitVariable(fr.inria.verveine.core.gen.famix.Class clazz, String name) {
		ImplicitVariable fmx = getImplicitVariableByClass(clazz, name);
		
		if (fmx == null) {
			fmx = (ImplicitVariable) createFamixEntity(ImplicitVariable.class, name);
			if (fmx!=null) {
				fmx.setContainer(clazz);
				fmx.setIsStub(Boolean.FALSE);

				ImplicitVars iv = mapImpVar.get(clazz);				
				if (iv == null) {
					iv = new ImplicitVars();
				}

				if (name.equals(SELF_NAME)) {
					iv.self_iv = fmx;
				}
				else if (name.equals(SUPER_NAME)) {
					iv.super_iv = fmx;
				}
				
				mapImpVar.put(clazz, iv);
			}
		}

		return fmx;
	}

	@SuppressWarnings("unchecked")
	public <T extends NamedEntity> T ensureFamixUniqEntity(Class<T> fmxClass, B bnd, String name) {
		T fmx = null;
		if (bnd != null) {
			fmx = (T) getEntityByBinding(bnd);
		}
		
		if (fmx == null) {
			Collection<T> l = getEntityByName( fmxClass, name);
			if (l.size() > 0) {
				fmx = l.iterator().next();
			}
			else {
				fmx = createFamixEntity(fmxClass, name);
			}
			
			if (bnd != null) {
				// may happen for example if the entity was first created without binding
				// and we find a binding for it later
				mapBind.put(bnd, fmx);
			}
		}

		return fmx;
	}

	public Namespace ensureFamixNamespace(String name) {
		return  ensureFamixUniqEntity(Namespace.class, null, name);
	}

	public Namespace ensureFamixNamespaceDefault() {
		Namespace fmx =  ensureFamixUniqEntity(Namespace.class, null, DEFAULT_PCKG_NAME);

		return fmx;
	}

	public PrimitiveType ensureFamixPrimitiveType(String name) {
		return  ensureFamixUniqEntity(PrimitiveType.class, null, name);
	}

	public fr.inria.verveine.core.gen.famix.Class ensureFamixClassStubOwner() {
		fr.inria.verveine.core.gen.famix.Class fmx =  ensureFamixUniqEntity(fr.inria.verveine.core.gen.famix.Class.class, null, STUB_METHOD_CONTAINER_NAME);
		if (fmx != null) {
			fmx.setContainer( ensureFamixNamespaceDefault());
			fmx.setIsAbstract(Boolean.FALSE);
			fmx.setIsFinal(Boolean.FALSE);
			fmx.setIsInterface(Boolean.FALSE);
			fmx.setIsPrivate(Boolean.FALSE);
			fmx.setIsProtected(Boolean.FALSE);
			fmx.setIsPublic(Boolean.FALSE);
		}

		return fmx;
	}
	
}
