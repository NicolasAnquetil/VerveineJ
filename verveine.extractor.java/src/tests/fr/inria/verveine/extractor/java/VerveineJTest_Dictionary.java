/**
 * Copyright (c) 2010 Nicolas Anquetil
 */
package tests.fr.inria.verveine.extractor.java;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.fr.inria.verveine.core.TestVerveineUtils;
import ch.akuhn.fame.Repository;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.LocalVariable;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.Parameter;
import fr.inria.verveine.core.gen.famix.Type;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.VerveineJParser;

/**
 * @author Andre Hora
 * @since January 17, 2011
 *
 */
public class VerveineJTest_Dictionary {

	private Repository repo;

	public VerveineJTest_Dictionary() {
		new File(VerveineJParser.OUTPUT_FILE).delete();
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		VerveineJParser parser = new VerveineJParser();
		parser.compile(new String[] {"test_src/Dictionary"});
		repo = parser.getFamixRepo();
	}

	@After
	public void tearDown() {
		new File(VerveineJParser.OUTPUT_FILE).delete();
	}

	@Test
	public void testClassParameterTypes() {
		fr.inria.verveine.core.gen.famix.Class nodeClass = TestVerveineUtils.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "Dictionary");
		assertNotNull(nodeClass);
		assertEquals("Dictionary", nodeClass.getName());
		assertFalse(nodeClass.getIsInterface());
		assertEquals(1, nodeClass.getParameterTypes().size());
		assertSame(TestVerveineUtils.detectElement(repo, Type.class, "B"), nodeClass.getParameterTypes().iterator().next());
	}

	@Test
	public void testFieldArgumentTypes() {
		Attribute famixAtt = TestVerveineUtils.detectElement(repo, Attribute.class, "DEFAULT_PCKG_NAME");
		assertNotNull(famixAtt);
		assertEquals("DEFAULT_PCKG_NAME", famixAtt.getName());
		assertNull(famixAtt.getDeclaredArgumentTypes());
		
		famixAtt = TestVerveineUtils.detectElement(repo, Attribute.class, "STUB_METHOD_CONTAINER_NAME");
		assertNotNull(famixAtt);
		assertEquals("STUB_METHOD_CONTAINER_NAME", famixAtt.getName());
		assertNull(famixAtt.getDeclaredArgumentTypes());
		
		famixAtt = TestVerveineUtils.detectElement(repo, Attribute.class, "SELF_NAME");
		assertNotNull(famixAtt);
		assertEquals("SELF_NAME", famixAtt.getName());
		assertNull(famixAtt.getDeclaredArgumentTypes());
		
		famixAtt = TestVerveineUtils.detectElement(repo, Attribute.class, "SUPER_NAME");
		assertNotNull(famixAtt);
		assertEquals("SUPER_NAME", famixAtt.getName());
		assertNull(famixAtt.getDeclaredArgumentTypes());
		
		famixAtt = TestVerveineUtils.detectElement(repo, Attribute.class, "famixRepo");
		assertNotNull(famixAtt);
		assertEquals("famixRepo", famixAtt.getName());
		assertNull(famixAtt.getDeclaredArgumentTypes());

		famixAtt = TestVerveineUtils.detectElement(repo, Attribute.class, "mapBind");
		assertNotNull(famixAtt);
		assertEquals("mapBind", famixAtt.getName());
		assertEquals(2, famixAtt.getDeclaredArgumentTypes().size());
		Iterator<Type> it = famixAtt.getDeclaredArgumentTypes().iterator();
		assertSame(TestVerveineUtils.detectElement(repo, Type.class, "B"), it.next());
		assertSame(TestVerveineUtils.detectElement(repo, Type.class, "NamedEntity"), it.next());
		
		famixAtt = TestVerveineUtils.detectElement(repo, Attribute.class, "mapName");
		assertNotNull(famixAtt);
		assertEquals("mapName", famixAtt.getName());
		assertEquals(2, famixAtt.getDeclaredArgumentTypes().size());
		it = famixAtt.getDeclaredArgumentTypes().iterator();
		assertSame(TestVerveineUtils.detectElement(repo, Type.class, "String"), it.next());
		assertSame(TestVerveineUtils.detectElement(repo, Type.class, "Collection"), it.next());
		
		famixAtt = TestVerveineUtils.detectElement(repo, Attribute.class, "mapImpVar");
		assertNotNull(famixAtt);
		assertEquals("mapImpVar", famixAtt.getName());
		assertEquals(2, famixAtt.getDeclaredArgumentTypes().size());
		it = famixAtt.getDeclaredArgumentTypes().iterator();
		Collection<Type> fmxClasses = TestVerveineUtils.listElements(repo, Type.class, "Class");
		String javaLangNamespace = JavaDictionary.OBJECT_PACKAGE_NAME.substring(JavaDictionary.OBJECT_PACKAGE_NAME.lastIndexOf('.')+1);
		Namespace ns = TestVerveineUtils.detectElement(repo, Namespace.class, javaLangNamespace);
		//There are two classes with the name Class (java.lang and famix namespaces). Ensure to get the correct one.
		for (Type fmxClass : fmxClasses) {
			if (ns != fmxClass.getContainer()) {
				assertSame(fmxClass, it.next());
			}
		}
		assertSame(TestVerveineUtils.detectElement(repo, Type.class, "ImplicitVars"), it.next());
	}


	@Test
	public void testMethodParameterArgumentTypes() {
		Method fmxMethod = TestVerveineUtils.detectElement(repo, Method.class, "getEntityByName");
		assertNotNull(fmxMethod);
		assertEquals(2, fmxMethod.getParameters().size());
		for (Parameter fmxParameter : fmxMethod.getParameters()) {
			assertTrue(fmxParameter.getName().equals("fmxClass") || fmxParameter.getName().equals("name"));
			if (fmxParameter.getName().equals("fmxClass")) {
				assertEquals(1, fmxParameter.getDeclaredArgumentTypes().size());
				assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxParameter.getDeclaredArgumentTypes().iterator().next());
			}
			if (fmxParameter.getName().equals("name")) {
				assertNull(fmxParameter.getDeclaredArgumentTypes());
			}
		}
		
		fmxMethod = TestVerveineUtils.detectElement(repo, Method.class, "createFamixEntity");
		assertNotNull(fmxMethod);
		assertEquals(2, fmxMethod.getParameters().size());
		for (Parameter fmxParameter : fmxMethod.getParameters()) {
			assertTrue(fmxParameter.getName().equals("fmxClass") || fmxParameter.getName().equals("name"));
			if (fmxParameter.getName().equals("fmxClass")) {
				assertEquals(1, fmxParameter.getDeclaredArgumentTypes().size());
				assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxParameter.getDeclaredArgumentTypes().iterator().next());
			}
			if (fmxParameter.getName().equals("name")) {
				assertNull(fmxParameter.getDeclaredArgumentTypes());
			}
		}
		
		fmxMethod = TestVerveineUtils.detectElement(repo, Method.class, "ensureFamixEntity");
		assertNotNull(fmxMethod);
		assertEquals(3, fmxMethod.getParameters().size());
		for (Parameter fmxParameter : fmxMethod.getParameters()) {
			assertTrue(fmxParameter.getName().equals("fmxClass") || fmxParameter.getName().equals("bnd") || fmxParameter.getName().equals("name"));
			if (fmxParameter.getName().equals("fmxClass")) {
				assertEquals(1, fmxParameter.getDeclaredArgumentTypes().size());
				assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxParameter.getDeclaredArgumentTypes().iterator().next());
			}
			if (fmxParameter.getName().equals("bnd")) {
				assertNull(fmxParameter.getDeclaredArgumentTypes());
			}
			if (fmxParameter.getName().equals("name")) {
				assertNull(fmxParameter.getDeclaredArgumentTypes());
			}
		}
		
		fmxMethod = TestVerveineUtils.detectElement(repo, Method.class, "ensureFamixUniqEntity");
		assertNotNull(fmxMethod);
		assertEquals(3, fmxMethod.getParameters().size());
		for (Parameter fmxParameter : fmxMethod.getParameters()) {
			assertTrue(fmxParameter.getName().equals("fmxClass") || fmxParameter.getName().equals("bnd") || fmxParameter.getName().equals("name"));
			if (fmxParameter.getName().equals("fmxClass")) {
				assertEquals(1, fmxParameter.getDeclaredArgumentTypes().size());
				assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxParameter.getDeclaredArgumentTypes().iterator().next());
			}
			if (fmxParameter.getName().equals("bnd")) {
				assertNull(fmxParameter.getDeclaredArgumentTypes());
			}
			if (fmxParameter.getName().equals("name")) {
				assertNull(fmxParameter.getDeclaredArgumentTypes());
			}
		}
	}
	
	@Test
	public void testMethodLocalVariableArgumentTypes() {
		Method fmxMethod = TestVerveineUtils.detectElement(repo, Method.class, "mapEntityToName");
		assertNotNull(fmxMethod);
		assertEquals(1, fmxMethod.getLocalVariables().size());
		for (LocalVariable fmxLocalVariable : fmxMethod.getLocalVariables()) {
			assertTrue(fmxLocalVariable.getName().equals("l_ent"));
			assertEquals(1, fmxLocalVariable.getDeclaredArgumentTypes().size());
			assertSame(TestVerveineUtils.detectElement(repo, Type.class, "NamedEntity"), fmxLocalVariable.getDeclaredArgumentTypes().iterator().next());
		}
		
		fmxMethod = TestVerveineUtils.detectElement(repo, Method.class, "getEntityByName");
		assertNotNull(fmxMethod);
		assertEquals(2, fmxMethod.getLocalVariables().size());
		for (LocalVariable fmxLocalVariable : fmxMethod.getLocalVariables()) {
			assertTrue(fmxLocalVariable.getName().equals("ret") || fmxLocalVariable.getName().equals("l_name"));
			if (fmxLocalVariable.getName().equals("ret")) {
				assertEquals(1, fmxLocalVariable.getDeclaredArgumentTypes().size());
				assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxLocalVariable.getDeclaredArgumentTypes().iterator().next());
			}
			if (fmxLocalVariable.getName().equals("l_name")) {
				assertEquals(1, fmxLocalVariable.getDeclaredArgumentTypes().size());
				assertSame(TestVerveineUtils.detectElement(repo, Type.class, "NamedEntity"), fmxLocalVariable.getDeclaredArgumentTypes().iterator().next());
			}
		}
		
		fmxMethod = TestVerveineUtils.detectElement(repo, Method.class, "ensureFamixUniqEntity");
		assertNotNull(fmxMethod);
		assertEquals(2, fmxMethod.getLocalVariables().size());
		for (LocalVariable fmxLocalVariable : fmxMethod.getLocalVariables()) {
			assertTrue(fmxLocalVariable.getName().equals("fmx") || fmxLocalVariable.getName().equals("l"));
			if (fmxLocalVariable.getName().equals("fmx")) {
				assertNull(fmxLocalVariable.getDeclaredArgumentTypes());
			}
			if (fmxLocalVariable.getName().equals("l")) {
				assertEquals(1, fmxLocalVariable.getDeclaredArgumentTypes().size());
				assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxLocalVariable.getDeclaredArgumentTypes().iterator().next());
			}
		}
	}
	
	@Test
	public void testMethodReturnArgumentTypes() {
		Method fmxMethod = TestVerveineUtils.detectElement(repo, Method.class, "getEntityByName");
		assertNotNull(fmxMethod);
		assertEquals(1, fmxMethod.getDeclaredArgumentTypes().size());
		assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxMethod.getDeclaredArgumentTypes().iterator().next());
	}
}