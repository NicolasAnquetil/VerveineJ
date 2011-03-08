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
import fr.inria.verveine.core.gen.famix.ContainerEntity;
import fr.inria.verveine.core.gen.famix.Entity;
import fr.inria.verveine.core.gen.famix.LocalVariable;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.NamedEntity;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.Parameter;
import fr.inria.verveine.core.gen.famix.ParameterType;
import fr.inria.verveine.core.gen.famix.ParameterizableClass;
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
		parser.compile(new String[] {"test_src/ad_hoc/Dictionary.java"});
		repo = parser.getFamixRepo();
	}

	@After
	public void tearDown() {
		new File(VerveineJParser.OUTPUT_FILE).delete();
	}

	@Test
	public void testClassParameterTypes() {
		ParameterizableClass dicoClass = TestVerveineUtils.detectElement(repo, ParameterizableClass.class, "Dictionary");
		assertNotNull(dicoClass);
		assertEquals("Dictionary", dicoClass.getName());
		assertEquals(2, dicoClass.getTypes().size());
		assertEquals(1, dicoClass.getParameters().size());
		
		ParameterType dicoParam = TestVerveineUtils.detectElement(repo, ParameterType.class, "B");
		assertNotNull(dicoParam);
		assertEquals("B", dicoParam.getName());
		
		assertSame(dicoClass, dicoParam.getContainer());
		assertSame(dicoParam, dicoClass.getParameters().iterator().next());
	}

	@Test
	public void testParameterTypeAsType() {
		Method gebb = TestVerveineUtils.detectElement(repo, Method.class, "getEntityByBinding");
		assertNotNull(gebb);
		assertSame(1, gebb.getParameters().size());
		
		Parameter bnd = gebb.getParameters().iterator().next();
		assertNotNull(bnd);
		assertEquals("bnd", bnd.getName());
		
		Type b = bnd.getDeclaredType();
		assertNotNull(b);
		assertEquals("B", b.getName());
		assertSame(ParameterType.class, b.getClass());
		
		ContainerEntity cont = b.getContainer();
		assertNotNull(cont);
		assertEquals("Dictionary", cont.getName());
		assertSame(ParameterizableClass.class, cont.getClass());
	}
	
	/*Test
	public void testFieldArgumentTypes() {
		Attribute famixAtt = TestVerveineUtils.detectElement(repo, Attribute.class, "mapBind");
		assertNotNull(famixAtt);
		assertEquals("mapBind", famixAtt.getName());
		assertEquals(2, famixAtt.getDeclaredArgumentTypes().size());
		Iterator<Type> it = famixAtt.getDeclaredArgumentTypes().iterator();
		assertSame(TestVerveineUtils.detectElement(repo, Type.class, "B"), it.next());
		assertSame(TestVerveineUtils.detectElement(repo, Type.class, "NamedEntity"), it.next());
	}
*/
	
	/*Test
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
	*/
	
	/*Test
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
	*/
	
	/*Test
	public void testMethodReturnArgumentTypes() {
		Method fmxMethod = TestVerveineUtils.detectElement(repo, Method.class, "getEntityByName");
		assertNotNull(fmxMethod);
		assertEquals(1, fmxMethod.getDeclaredArgumentTypes().size());
		assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxMethod.getDeclaredArgumentTypes().iterator().next());
	}
	*/
}