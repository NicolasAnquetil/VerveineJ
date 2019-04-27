package fr.inria.verveine.extractor.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import ch.akuhn.fame.Repository;
import eu.synectique.verveine.core.VerveineUtilsForTests;
import eu.synectique.verveine.core.gen.famix.Access;
import eu.synectique.verveine.core.gen.famix.Association;
import eu.synectique.verveine.core.gen.famix.Attribute;
import eu.synectique.verveine.core.gen.famix.BehaviouralEntity;
import eu.synectique.verveine.core.gen.famix.Invocation;
import eu.synectique.verveine.core.gen.famix.JavaSourceLanguage;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.NamedEntity;
import eu.synectique.verveine.core.gen.famix.Namespace;
import eu.synectique.verveine.core.gen.famix.PrimitiveType;
import eu.synectique.verveine.core.gen.famix.SourceLanguage;
import eu.synectique.verveine.core.gen.famix.StructuralEntity;
import eu.synectique.verveine.core.gen.famix.Type;

public abstract class VerveineJTest_Basic {

	protected Repository repo;
	protected VerveineJParser parser;

    private boolean testSystemEntities = true;

    private boolean testLanguageMarker = true;

    public VerveineJTest_Basic(boolean testSystemEntities, boolean testLanguageMarker) {
        this.testSystemEntities = testSystemEntities;
        this.testLanguageMarker = testLanguageMarker;
	}

	@Test
	public void testAssociation() {
		for (Association ass : VerveineUtilsForTests.selectElementsOfType(repo, Access.class) ) {
			if (! (ass instanceof Invocation)) { // null receiver allowed for invocations
				assertNotNull(ass.getClass().getSimpleName()+(ass.getTo()==null? "" : " to: "+((NamedEntity)ass.getTo()).getName())+" as no From", ass.getFrom());
			}
			assertNotNull(ass.getClass().getSimpleName()+" from: "+((NamedEntity)ass.getFrom()).getName()+" as no To", ass.getTo());
		}

		for (Association ass : VerveineUtilsForTests.selectElementsOfType(repo, Association.class) ) {
			Association n = ass.getNext();
			if (n!=null) {
				assertSame(ass, n.getPrevious());
			}
		}
		
		for (Association ass : VerveineUtilsForTests.selectElementsOfType(repo, Association.class) ) {
			Association p = ass.getPrevious();
			if (p!=null) {
				assertSame(ass, p.getNext());
			}
		}
	}

	@Test
	public void testBelongsTo() {
		for ( Type e : repo.all(Type.class) ) {
			if (! (e instanceof PrimitiveType) ) {
				assertNotNull("a Type '"+e.getName()+"' does not belong to anything", e.getBelongsTo());
			}
		}
		for ( BehaviouralEntity e : repo.all(BehaviouralEntity.class) ) {
			assertNotNull("a BehaviouralEntity '"+e.getName()+"' does not belong to anything", e.getBelongsTo());
		}
		for ( StructuralEntity e : repo.all(StructuralEntity.class) ) {
			assertNotNull("a StructuralEntity '"+e.getName()+"' does not belong to anything", e.getBelongsTo());
		}
	}

	@Test
	public void testMethodAndClassSourceAnchor() {
		// related to issue #728 VerveineJ places methods in the wrong classes
		// some methods were created without sourceAnchor whereas their parent did have one. This should not happen (or only in special cases)
		for ( Method m : repo.all(Method.class) ) {
			Type parent = m.getParentType();
			if ( (parent != null) &&
					(! (parent instanceof eu.synectique.verveine.core.gen.famix.Enum)) &&   // for enums some methods are implicit
					(! m.getName().equals(parent.getName())) &&   // for constructors are implicit
					(! m.getName().equals(JavaDictionary.INIT_BLOCK_NAME)) &&
					(parent.getSourceAnchor() != null) ) {
				assertNotNull("Method '"+m.getName()+"' has no SourceAnchor, whereas its parent '"+parent.getName()+"' has one", m.getSourceAnchor());
			}
		}
	}

	/**
	 * Test of some "basic" Java entities that we know should be here such as: java.lang, java.io, Object, String, System
	 * (and respective superclasses and implemented interfaces)
	 */
	@Test
	public void testJavaCore() {
		
		// namespaces
		Namespace java = VerveineUtilsForTests.detectFamixElement(repo,Namespace.class, "java");
		assertNotNull(java);

		String javaLangName = JavaDictionary.OBJECT_PACKAGE_NAME.substring(JavaDictionary.OBJECT_PACKAGE_NAME.lastIndexOf('.')+1);
		Namespace javaLang = VerveineUtilsForTests.detectFamixElement(repo,Namespace.class, javaLangName);
		assertNotNull(javaLang);
		assertEquals(java, javaLang.getBelongsTo());
		 // Object,String,StringBuffer,AbstractStringBuilder,System,Comparable,Comparable<String>,Appendable,CharSequence

		/* java.io no longer created by default
		Namespace javaIO = VerveineUtilsForTests.detectFamixElement(repo,Namespace.class, "io");
		assertNotNull(javaIO);
		assertEquals(java, javaIO.getBelongsTo());
		*/

		// Object
		eu.synectique.verveine.core.gen.famix.Class obj = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, JavaDictionary.OBJECT_NAME);
		assertNotNull(obj);
		assertSame(javaLang, obj.getContainer());
		assertEquals(0, obj.getSuperInheritances().size());

		// String
		/* CharSequence no longer created as superclass of String
		eu.synectique.verveine.core.gen.famix.Class charSeq = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "CharSequence");
		assertNotNull(charSeq);
		assertSame(javaLang, charSeq.getContainer());
		assertTrue(charSeq.getIsInterface());
		assertEquals(0, charSeq.getSuperInheritances().size());
		*/

		/* Serializable no longer created
		eu.synectique.verveine.core.gen.famix.Class serial = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "Serializable");
		assertNotNull(serial);
		assertSame(javaIO, serial.getContainer());
		assertTrue(serial.getIsInterface());
		assertEquals(0, serial.getSuperInheritances().size());
		*/

		eu.synectique.verveine.core.gen.famix.Class str = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "String");
		assertNotNull(str);
		assertSame(javaLang, str.getContainer());
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

		// System
		if (this.testSystemEntities) {
			eu.synectique.verveine.core.gen.famix.Class syst = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "System");
			assertNotNull(syst);
			assertSame(javaLang, syst.getContainer());
			/*stubs no longer have inheritance
			assertEquals(1, syst.getSuperInheritances().size());
			assertEquals(obj, syst.getSuperInheritances().iterator().next().getSuperclass());
			*/
			boolean foundOut = false;
			for (Attribute att : syst.getAttributes()) {
				if (att.getName().equals("out")) {
					foundOut = true;
				}
				else {
					assertTrue( "Unexpected System attribute: "+att.getName(), att.getName().equals("err") );
				}
			}
			assertTrue("System does not have an attribute 'out'", foundOut);
		}
	}
	
	@Test
	public void testSourceLanguage() {
	    if (testLanguageMarker) {
            Collection<SourceLanguage> sl = VerveineUtilsForTests.selectElementsOfType(repo, SourceLanguage.class);
            assertNotNull(sl);
            assertEquals(1, sl.size());
            SourceLanguage jsl = sl.iterator().next();
            assertEquals(JavaSourceLanguage.class, jsl.getClass());
        }
        else {
            Collection<SourceLanguage> sl = VerveineUtilsForTests.selectElementsOfType(repo, SourceLanguage.class);
            assertNotNull(sl);
            assertEquals(0, sl.size());
        }
	}

	/**
	 * Test that primitive types names are not used for something else
	 */
	@Test
	public void testPrimitiveTypes() {
		for (NamedEntity t : VerveineUtilsForTests.listFamixElements(repo, "byte")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : VerveineUtilsForTests.listFamixElements(repo, "short")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : VerveineUtilsForTests.listFamixElements(repo, "int")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : VerveineUtilsForTests.listFamixElements(repo, "long")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : VerveineUtilsForTests.listFamixElements(repo, "float")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : VerveineUtilsForTests.listFamixElements(repo, "double")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : VerveineUtilsForTests.listFamixElements(repo, "boolean")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : VerveineUtilsForTests.listFamixElements(repo, "char")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : VerveineUtilsForTests.listFamixElements(repo, "void")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
	}

}