package fr.inria.verveine.extractor.java;

import ch.akuhn.fame.Repository;
import fr.inria.verveine.extractor.java.utils.Util;
import org.junit.Assume;
import org.junit.Test;
import org.moosetechnology.model.famixjava.famixjavaentities.Package;
import org.moosetechnology.model.famixjava.famixjavaentities.*;
import org.moosetechnology.model.famixjava.famixtraits.TAttribute;
import org.moosetechnology.model.famixjava.famixtraits.TNamedEntity;
import org.moosetechnology.model.famixjava.famixtraits.TSourceEntity;
import org.moosetechnology.model.famixjava.famixtraits.TStructuralEntity;

import java.lang.Class;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import static org.junit.Assert.*;

public abstract class VerveineJTest_Basic {

	protected static final String DEFAULT_OUTPUT_FILE = "output.mse";

	protected Repository repo;
	protected VerveineJParser parser;

	/**
	 * A crude way for the subclasses to select which tests they want to run
	 * Each test method should have an indice in this array
	 */
	private final boolean[] testsToRun;

	public VerveineJTest_Basic() {
		this(true);
	}

	public VerveineJTest_Basic(boolean runTests) {
		testsToRun = new boolean[7];
		for (int i=0; i<7; i++) {
			testsToRun[i] = runTests;
		}
	}

	public VerveineJTest_Basic(boolean[] testsToRun) throws IllegalAccessException {
		if (testsToRun.length < 7) {
			throw new IllegalAccessException();
		}
		this.testsToRun = testsToRun;
	}


	// ---------------- helper methods ------------------

	protected <T> T firstElt(Collection<T> coll) {
		return coll.iterator().next();
	}

    public <T extends Entity> Collection<T> entitiesOfType(Class<T> clazz) {
        return repo.all(clazz);
    }

    public <T extends NamedEntity> T detectFamixElement(Class<T> clazz, String name) {
        Iterator<T> iter = entitiesOfType(clazz).iterator();

        T found;
        do {
            if (!iter.hasNext()) {
                return null;
            }

            found = iter.next();
        } while(!found.getName().equals(name));

        return found;
    }

    public Collection<NamedEntity> entitiesNamed( String name) {
        return entitiesNamed( NamedEntity.class, name);
    }

    public <T extends NamedEntity> Collection<T> entitiesNamed( Class<T> clazz, String name) {
        Vector<T> ret = new Vector<T>();
        Iterator<T> iter = entitiesOfType(clazz).iterator();

        while(iter.hasNext()) {
            T fmx = iter.next();
            if (fmx.getName().equals(name)) {
                ret.add(fmx);
            }
        }

        return ret;
    }

	// ---------------- generic tests methods ------------------

	/*@Test // number 0
	public void testAssociation() {
    	Assume.assumeTrue(testsToRun[0]);

		Collection<Access> accesses = entitiesOfType( Access.class);
		assertFalse("No Accesses found", accesses.isEmpty());
		for (TAssociation ass : accesses) {
			if (! (ass instanceof Invocation)) { // null receiver allowed for some Associations like Invocations
				assertNotNull(ass.getClass().getSimpleName()+(ass.getTo()==null? "" : " to: "+((NamedEntity)ass.getTo()).getName())+" as no From",
								ass.getFrom());
			}
			assertNotNull(ass.getClass().getSimpleName()+" from: "+((NamedEntity)ass.getFrom()).getName()+" as no To",
							ass.getTo());
		}

    	boolean found = false;
		for (Association ass : entitiesOfType( Association.class) ) {
			Association n = ass.getNext();
			if (n!=null) {
				assertSame(ass, n.getPrevious());
				found = true;
			}
		}
		assertTrue("No `next' found in any Associations", found);

    	found = false;
		for (Association ass : entitiesOfType( Association.class) ) {
			Association p = ass.getPrevious();
			if (p!=null) {
				assertSame(ass, p.getNext());
				found = true;
			}
		}
		assertTrue("No `previous' found in any Associations", found);
	}*/

	@Test // number 1
	public void testBelongsTo() {
		Assume.assumeTrue(testsToRun[1]);

		boolean found = false;
		for (Type e : repo.all(Type.class)) {
			if (!e.getIsStub()) {
				assertNotNull("a Type '" + e.getName() + "' does not belong to anything", Util.belongsToOf(e));
				found = true;
			}
		}
		assertTrue("All `Types' are stubs", found);

		found = false;
		for (Method e : repo.all(Method.class)) {
			if (!e.getIsStub()) {
				assertNotNull("a BehaviouralEntity '" + e.getName() + "' does not belong to anything", Util.belongsToOf(e));
				found = true;
			}
		}
		assertTrue("All `BehaviouralEntities' are stubs", found);

		found = false;
		for (TStructuralEntity e : repo.all(TStructuralEntity.class)) {
			if (!((TSourceEntity) e).getIsStub()) {
				assertNotNull("a StructuralEntity '" + ((TNamedEntity) e).getName() + "' does not belong to anything", Util.belongsToOf((Entity) e));
				found = true;
			}
		}
		assertTrue("All `StructuralEntities' are stubs", found);
	}

	@Test // number 2
	public void testMethodAndClassSourceAnchor() {
		// related to issue #728 VerveineJ places methods in the wrong classes
		// some methods were created without sourceAnchor whereas their parent did have one. This should not happen (or only in special cases)
    	boolean found = false;
    	Assume.assumeTrue(testsToRun[2]);
		for ( Method m : repo.all(Method.class)) {
			Type parent = (Type) m.getParentType();
			if ((parent != null) &&
					(!(parent instanceof org.moosetechnology.model.famixjava.famixjavaentities.Enum)) &&   // for enums some methods are implicit
					(!m.getName().equals(parent.getName())) &&   // for constructors are implicit
					(!m.getName().equals(JavaDictionary.INIT_BLOCK_NAME)) &&
					(parent.getSourceAnchor() != null)) {
				assertNotNull("Method '" + m.getName() + "' has no SourceAnchor, whereas its parent '" + parent.getName() + "' has one", m.getSourceAnchor());
				found = true;
			}
		}
		assertTrue("No assertion was run :-(", found);
	}

	/**
	 * Test of some "basic" Java entities that we know should be here such as: java.lang, java.io, Object, String, System
	 * (and respective superclasses and implemented interfaces)
	 */
	@Test // number 3
	public void testJavaCore() {
		Assume.assumeTrue(testsToRun[3]);

		// namespaces
		Package java = detectFamixElement(Package.class, "java");
		assertNotNull(java);

		String javaLangName = JavaDictionary.OBJECT_PACKAGE_NAME.substring(JavaDictionary.OBJECT_PACKAGE_NAME.lastIndexOf('.') + 1);
		Package javaLang = detectFamixElement(Package.class, javaLangName);
		assertNotNull(javaLang);
		assertEquals(java, javaLang.getParentPackage());
		// Object,String,StringBuffer,AbstractStringBuilder,System,Comparable,Comparable<String>,Appendable,CharSequence

		/* java.io no longer created by default
		Namespace javaIO = detectFamixElement(Namespace.class, "io");
		assertNotNull(javaIO);
		assertEquals(java, javaIO.getBelongsTo());
		*/

		// Object
		org.moosetechnology.model.famixjava.famixjavaentities.Class obj = detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, JavaDictionary.OBJECT_NAME);
		assertNotNull(obj);
		assertSame(javaLang, obj.getTypeContainer());
		assertEquals(0, obj.getSuperInheritances().size());

		// String
		/* CharSequence no longer created as superclass of String
		org.moosetechnology.model.famixjava.famixjavaentities.Class charSeq = detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, "CharSequence");
		assertNotNull(charSeq);
		assertSame(javaLang, charSeq.getContainer());
		assertTrue(charSeq.getIsInterface());
		assertEquals(0, charSeq.getSuperInheritances().size());
		*/

		/* Serializable no longer created
		org.moosetechnology.model.famixjava.famixjavaentities.Class serial = detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, "Serializable");
		assertNotNull(serial);
		assertSame(javaIO, serial.getContainer());
		assertTrue(serial.getIsInterface());
		assertEquals(0, serial.getSuperInheritances().size());
		*/

		org.moosetechnology.model.famixjava.famixjavaentities.Class str = detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, "String");
		assertNotNull(str);
		assertSame(javaLang, str.getTypeContainer());
		/*stubs no longer have inheritance
		assertEquals(4, str.getSuperInheritances().size());
		for (Inheritance inh : str.getSuperInheritances()) {
			assertTrue( "Unexpected super-class for String: "+inh.getSuperclass(),
					(inh.getSuperclass() == obj) ||
					(inh.getSuperclass() == charSeq) ||
					(inh.getSuperclass() == serial) ||
					(inh.getSuperclass().getName().equals("Comparable")) );  // there are 2 'comparable' in one case, so it's best to only test the name
		}
		*/
	}

	@Test // number 4
	public void testSystemClass() {
		Assume.assumeTrue(testsToRun[4]);

		org.moosetechnology.model.famixjava.famixjavaentities.Class syst = detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, "System");
		assertNotNull(syst);
		String javaLangName = JavaDictionary.OBJECT_PACKAGE_NAME.substring(JavaDictionary.OBJECT_PACKAGE_NAME.lastIndexOf('.') + 1);
		Package javaLang = detectFamixElement(Package.class, javaLangName);
		assertSame(javaLang, syst.getTypeContainer());
		boolean found = false;
		for (TAttribute att : syst.getAttributes()) {
			if (((TNamedEntity) att).getName().equals("out")) {
				found = true;
			} else {
				assertTrue("Unexpected System attribute: " + ((TNamedEntity) att).getName(), ((TNamedEntity) att).getName().equals("err"));
			}
		}
		assertTrue("No `out' attribute in class `System'", found);
	}
	
	@Test // number 5
	public void testSourceLanguage() {
		Assume.assumeTrue(testsToRun[5]);

		Collection<SourceLanguage> sl = entitiesOfType(SourceLanguage.class);
		assertNotNull(sl);
		assertEquals(1, sl.size());
		SourceLanguage jsl = firstElt(sl);
		assertEquals(SourceLanguage.class, jsl.getClass());
	}

	/**
	 * Test that primitive types names are not used for something else
	 *
	 * Note that any of these `listFamixElements' might be empty depending on the test set used
	 * Therefore these tests are "Rotten Green Tests" on purpose
	 */
	@Test // number 6
	public void testPrimitiveTypes() {
    	Assume.assumeTrue(testsToRun[6]);
		for (NamedEntity t : entitiesNamed( "byte")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : entitiesNamed( "short")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : entitiesNamed( "int")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : entitiesNamed( "long")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : entitiesNamed( "float")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : entitiesNamed( "double")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : entitiesNamed( "boolean")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : entitiesNamed( "char")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : entitiesNamed( "void")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
	}

	// UTILITIES
	
    protected boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
    
    /**
     * returns the list of all java superclasses of <code>clazz</code> up to <code>Object</code>
     */
    protected List<Class<?>> allJavaSuperClasses(Class<?> clazz) {
    	List<Class<?>> allsupers;
    	Class<?> superclazz = clazz.getSuperclass();
    	if (superclazz == null) {
    		allsupers = new LinkedList<>();
    	}
    	else {
    		allsupers = allJavaSuperClasses(superclazz);
    	}
    	allsupers.add(clazz);
    	return allsupers;
    }

    /**
     * Returns an association with key=the parameter <code>typ</code> and value=all java interface recursively implemented by <code>typ</code>
     * "all java interface implemented by <code>typ</code>" is of list of associations returned by this method
     * Therefore, this is a recursive method and the return value is a recursive structure (an association with a list containing associations, with lists...),
     */
    protected ImplementedInterfaces allImplementedJavaInterfaces(Class<?> typ) {
    	List<ImplementedInterfaces> allInterfaces = new LinkedList<>();

    	for (Class<?> interf : typ.getInterfaces()) {
    		allInterfaces.add( allImplementedJavaInterfaces(interf));
    	}

    	return new ImplementedInterfaces(typ, allInterfaces);
    }

    protected class ImplementedInterfaces {
    	protected Class<?> implementor;
    	protected List<ImplementedInterfaces> interfaces;
    	
		public ImplementedInterfaces(Class<?> implementor, List<ImplementedInterfaces> interfaces) {
			this.implementor = implementor;
			this.interfaces = interfaces;
		}

		public Class<?> getImplementor() {
			return implementor;
		}

		public List<ImplementedInterfaces> getInterfaces() {
			return interfaces;
		}
    	
		public List<Class<?>> flatten() {
	    	List<Class<?>> flattened = new LinkedList<>();
	    	flattened.add(implementor);
	 
	    	if (interfaces.isEmpty()) return flattened;

	    	for (ImplementedInterfaces recursiveImplementations : interfaces) {
	    		flattened.addAll( recursiveImplementations.flatten() );
	    	}

	    	return flattened;
		}

		/**
		 * Counts how many interfaces are subtyped (by other interfaces)
		 */
		public int countInterfacesSubtyped() {
			int total = 0;
			if (interfaces.isEmpty()) return 0;
			for (ImplementedInterfaces each : interfaces) {
				total += each.countInterfacesSubtyped();
			}
			if (implementor.isInterface()) {
				total += interfaces.size();
			}
			return total;
		}
		
		public List<Class<?>> directInterfaces() {
	    	List<Class<?>> direct = new LinkedList<>();
			for (ImplementedInterfaces each : interfaces) {
				direct.add( each.getImplementor());
			}
			return direct;
		}
    }

}