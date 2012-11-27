package tests.fr.inria.verveine.extractor.java;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;

import ch.akuhn.fame.Repository;
import fr.inria.verveine.core.VerveineUtilsForTests;
import fr.inria.verveine.core.gen.famix.Access;
import fr.inria.verveine.core.gen.famix.Association;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.BehaviouralEntity;
import fr.inria.verveine.core.gen.famix.Inheritance;
import fr.inria.verveine.core.gen.famix.Invocation;
import fr.inria.verveine.core.gen.famix.JavaSourceLanguage;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.NamedEntity;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.PrimitiveType;
import fr.inria.verveine.core.gen.famix.SourceLanguage;
import fr.inria.verveine.core.gen.famix.StructuralEntity;
import fr.inria.verveine.core.gen.famix.Type;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.VerveineJParser;

public abstract class VerveineJTest_Basic {

	protected Repository repo;
	protected VerveineJParser parser;
	
	private boolean testSystem = true;

	public VerveineJTest_Basic(boolean system) {
		testSystem = system;
	}

	@Test
	public void testAssociation() {
		for (Association ass : VerveineUtilsForTests.selectElementsOfType(repo, Access.class) ) {
			if (! (ass instanceof Invocation)) { // null receiver allowed for invocations
				assertNotNull(ass.getClass().getSimpleName()+(ass.getTo()==null? "" : " to: "+ass.getTo().getName())+" as no From", ass.getFrom());
			}
			assertNotNull(ass.getClass().getSimpleName()+" from: "+ass.getFrom().getName()+" as no To", ass.getTo());
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
					(! (parent instanceof fr.inria.verveine.core.gen.famix.Enum)) &&   // for enums some methods are implicit
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
		Namespace java = VerveineUtilsForTests.detectElement(repo,Namespace.class, "java");
		assertNotNull(java);

		String javaLangName = JavaDictionary.OBJECT_PACKAGE_NAME.substring(JavaDictionary.OBJECT_PACKAGE_NAME.lastIndexOf('.')+1);
		Namespace javaLang = VerveineUtilsForTests.detectElement(repo,Namespace.class, javaLangName);
		assertNotNull(javaLang);
		assertEquals(java, javaLang.getBelongsTo());
		 // Object,String,StringBuffer,AbstractStringBuilder,System,Comparable,Comparable<String>,Appendable,CharSequence

		Namespace javaIO = VerveineUtilsForTests.detectElement(repo,Namespace.class, "io");
		assertNotNull(javaIO);
		assertEquals(java, javaIO.getBelongsTo());

		// Object
		fr.inria.verveine.core.gen.famix.Class obj = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, JavaDictionary.OBJECT_NAME);
		assertNotNull(obj);
		assertSame(javaLang, obj.getContainer());
		assertEquals(0, obj.getSuperInheritances().size());

		// String
		fr.inria.verveine.core.gen.famix.Class charSeq = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "CharSequence");
		assertNotNull(charSeq);
		assertSame(javaLang, charSeq.getContainer());
		assertTrue(charSeq.getIsInterface());
		assertEquals(0, charSeq.getSuperInheritances().size());

		fr.inria.verveine.core.gen.famix.Class serial = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Serializable");
		assertNotNull(serial);
		assertSame(javaIO, serial.getContainer());
		assertTrue(serial.getIsInterface());
		assertEquals(0, serial.getSuperInheritances().size());

		fr.inria.verveine.core.gen.famix.Class str = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String");
		assertNotNull(str);
		assertSame(javaLang, str.getContainer());
		assertEquals(4, str.getSuperInheritances().size());
		for (Inheritance inh : str.getSuperInheritances()) {
			assertTrue( "Unexpected super-class for String: "+inh.getSuperclass(),
					(inh.getSuperclass() == obj) ||
					(inh.getSuperclass() == charSeq) ||
					(inh.getSuperclass() == serial) ||
					(inh.getSuperclass().getName().equals("Comparable")) );  // there are 2 'comparable' in one case, so it's best to only test the name
		}

		// System
		if (this.testSystem) {
			fr.inria.verveine.core.gen.famix.Class syst = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "System");
			assertNotNull(syst);
			assertSame(javaLang, syst.getContainer());
			assertEquals(1, syst.getSuperInheritances().size());
			assertEquals(obj, syst.getSuperInheritances().iterator().next().getSuperclass());
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
		Collection<SourceLanguage> sl = VerveineUtilsForTests.selectElementsOfType(repo, SourceLanguage.class);
		assertNotNull(sl);
		assertEquals(1, sl.size());
		SourceLanguage jsl = sl.iterator().next();
		assertEquals(JavaSourceLanguage.class, jsl.getClass());
	}

	/**
	 * Test that primitive types names are not used for something else
	 */
	@Test
	public void testPrimitiveTypes() {
		for (NamedEntity t : VerveineUtilsForTests.listElements(repo, "byte")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : VerveineUtilsForTests.listElements(repo, "short")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : VerveineUtilsForTests.listElements(repo, "int")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : VerveineUtilsForTests.listElements(repo, "long")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : VerveineUtilsForTests.listElements(repo, "float")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : VerveineUtilsForTests.listElements(repo, "double")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : VerveineUtilsForTests.listElements(repo, "boolean")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : VerveineUtilsForTests.listElements(repo, "char")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
		for (NamedEntity t : VerveineUtilsForTests.listElements(repo, "void")) {
			assertEquals(PrimitiveType.class, t.getClass());
		}
	}
}