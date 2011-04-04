package tests.fr.inria.verveine.extractor.java;

import static org.junit.Assert.*;

import org.junit.Test;

import test.fr.inria.verveine.core.TestVerveineUtils;
import ch.akuhn.fame.Repository;
import fr.inria.verveine.core.gen.famix.Association;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.BehaviouralEntity;
import fr.inria.verveine.core.gen.famix.Inheritance;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.ParameterizableClass;
import fr.inria.verveine.core.gen.famix.ParameterizedType;
import fr.inria.verveine.core.gen.famix.PrimitiveType;
import fr.inria.verveine.core.gen.famix.StructuralEntity;
import fr.inria.verveine.core.gen.famix.Type;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.VerveineJParser;

public abstract class VerveineJTest_Basic {

	protected Repository repo;
	protected VerveineJParser parser;

	@Test
	public void testAssociation() {
		for (Association ass : TestVerveineUtils.selectElementsOfType(repo, Association.class) ) {
			assertNotNull(ass.getClass().getSimpleName()+(ass.getTo()==null?"":" to: "+ass.getTo().getName())+" as no From", ass.getFrom());
			assertNotNull(ass.getClass().getSimpleName()+" from: "+ass.getFrom().getName()+" as no To", ass.getTo());
		}
		
		for (Association ass : TestVerveineUtils.selectElementsOfType(repo, Association.class) ) {
			Association n = ass.getNext();
			if (n!=null) {
				assertSame(ass, n.getPrevious());
			}
		}
		
		for (Association ass : TestVerveineUtils.selectElementsOfType(repo, Association.class) ) {
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

	/**
	 * Test of some "basic" Java entities that we know should be here such as: java.lang, java.io, Object, String, System
	 * (and respective superclasses and implemented interfaces)
	 */
	@Test
	public void testJavaCore() {
		
		// namespaces
		Namespace java = TestVerveineUtils.detectElement(repo,Namespace.class, "java");
		assertNotNull(java);

		String javaLangName = JavaDictionary.OBJECT_PACKAGE_NAME.substring(JavaDictionary.OBJECT_PACKAGE_NAME.lastIndexOf('.')+1);
		Namespace javaLang = TestVerveineUtils.detectElement(repo,Namespace.class, javaLangName);
		assertNotNull(javaLang);
		assertEquals(java, javaLang.getBelongsTo());
		 // Object,String,StringBuffer,AbstractStringBuilder,System,Comparable,Comparable<String>,Appendable,CharSequence

		Namespace javaIO = TestVerveineUtils.detectElement(repo,Namespace.class, "io");
		assertNotNull(javaIO);
		assertEquals(java, javaIO.getBelongsTo());

		// Object
		fr.inria.verveine.core.gen.famix.Class obj = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, JavaDictionary.OBJECT_NAME);
		assertNotNull(obj);
		assertSame(javaLang, obj.getContainer());
		assertEquals(0, obj.getSuperInheritances().size());

		// String
		fr.inria.verveine.core.gen.famix.Class charSeq = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "CharSequence");
		assertNotNull(charSeq);
		assertSame(javaLang, charSeq.getContainer());
		assertTrue(charSeq.getIsInterface());
		assertEquals(0, charSeq.getSuperInheritances().size());

		fr.inria.verveine.core.gen.famix.Class serial = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Serializable");
		assertNotNull(serial);
		assertSame(javaIO, serial.getContainer());
		assertTrue(serial.getIsInterface());
		assertEquals(0, serial.getSuperInheritances().size());

		ParameterizableClass comp = TestVerveineUtils.detectElement(repo,ParameterizableClass.class, "Comparable");
		assertNotNull(comp);
		assertSame(javaLang, comp.getContainer());
		assertTrue(comp.getIsInterface());
		assertEquals(0, comp.getSuperInheritances().size());

		ParameterizedType compStr = TestVerveineUtils.detectElement(repo,ParameterizedType.class, "Comparable");
		assertNotNull(compStr);
		assertSame(comp, compStr.getParameterizableClass());
		assertEquals(0, compStr.getSuperInheritances().size());
		
		fr.inria.verveine.core.gen.famix.Class str = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String");
		assertNotNull(str);
		assertSame(javaLang, str.getContainer());
		assertEquals(4, str.getSuperInheritances().size());
		for (Inheritance inh : str.getSuperInheritances()) {
			assertTrue( "Unexpected super-class for String: "+inh.getSuperclass().getName(),
					(inh.getSuperclass() == obj) ||
					(inh.getSuperclass() == charSeq) ||
					(inh.getSuperclass() == serial) ||
					(inh.getSuperclass() == compStr) );
		}

		// System
		fr.inria.verveine.core.gen.famix.Class syst = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "System");
		assertNotNull(syst);
		assertSame(javaLang, syst.getContainer());
		assertEquals(1, syst.getSuperInheritances().size());
		assertEquals(obj, syst.getSuperInheritances().iterator().next().getSuperclass());
		assertEquals(2, syst.getAttributes().size());
		for (Attribute att : syst.getAttributes()) {
			assertTrue( "Unexpected super-class for String: "+att.getName(),
					att.getName().equals("out") || att.getName().equals("err") );

		}
	}
}