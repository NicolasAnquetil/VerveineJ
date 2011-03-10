/**
 * Copyright (c) 2010 Anquetil Nicolas
 */
package tests.fr.inria.verveine.extractor.java;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.fr.inria.verveine.core.TestVerveineUtils;
import ch.akuhn.fame.Repository;
import fr.inria.verveine.core.gen.famix.AnnotationInstance;
import fr.inria.verveine.core.gen.famix.AnnotationInstanceAttribute;
import fr.inria.verveine.core.gen.famix.AnnotationType;
import fr.inria.verveine.core.gen.famix.AnnotationTypeAttribute;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.CaughtException;
import fr.inria.verveine.core.gen.famix.ContainerEntity;
import fr.inria.verveine.core.gen.famix.DeclaredException;
import fr.inria.verveine.core.gen.famix.EnumValue;
import fr.inria.verveine.core.gen.famix.LocalVariable;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.Parameter;
import fr.inria.verveine.core.gen.famix.ParameterType;
import fr.inria.verveine.core.gen.famix.ParameterizableClass;
import fr.inria.verveine.core.gen.famix.ThrownException;
import fr.inria.verveine.core.gen.famix.Type;
import fr.inria.verveine.extractor.java.VerveineJParser;

/**
 * @author Nicolas Anquetil
 * @since November 25, 2010
 *
 */
public class VerveineJTest_AdHoc {

	private Repository repo;

	public VerveineJTest_AdHoc() {
		// make sure we don't have any pre-existing mse lying in the way
		new File(VerveineJParser.OUTPUT_FILE).delete();
	}
	

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		VerveineJParser parser = new VerveineJParser();
		parser.compile(new String[] {"test_src/ad_hoc"});
		repo = parser.getFamixRepo();
	}

	@After
	public void tearDown() {
		new File(VerveineJParser.OUTPUT_FILE).delete();
	}
	
	@Test
	public void testExceptions() {
		Method meth = TestVerveineUtils.detectElement(repo, Method.class, "lire");
		assertNotNull(meth);
		
		fr.inria.verveine.core.gen.famix.Class excepClass = TestVerveineUtils.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "ReadException");
		assertNotNull(excepClass);

		assertEquals(1, meth.getDeclaredExceptions().size());
		DeclaredException exD = meth.getDeclaredExceptions().iterator().next();
		assertSame(meth, exD.getDefiningMethod());
		assertSame(excepClass, exD.getExceptionClass());
		
		assertEquals(1, meth.getThrownExceptions().size());
		ThrownException exT = meth.getThrownExceptions().iterator().next();
		assertSame(meth, exT.getDefiningMethod());
		assertSame(excepClass, exT.getExceptionClass());

		excepClass = TestVerveineUtils.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "IOException");
		assertNotNull(excepClass);

		assertEquals(1,meth.getCaughtExceptions().size());
		CaughtException exC = meth.getCaughtExceptions().iterator().next();
		assertSame(meth, exC.getDefiningMethod());
		assertSame(excepClass, exC.getExceptionClass());
	}
	
	@Test
	public void testAnnotation() {
		Collection<AnnotationType> l_FmProp = TestVerveineUtils.listElements(repo,AnnotationType.class, "FameProperty");
		assertEquals(1, l_FmProp.size());

		AnnotationType fmProp = l_FmProp.iterator().next();
		assertNotNull(fmProp);
		assertEquals("FameProperty", fmProp.getName());
		//TODO assertFalse(fmProp.getIsStub());
		
		AnnotationType fmPckg = TestVerveineUtils.detectElement(repo,AnnotationType.class, "FamePackage");
		assertNotNull(fmPckg);
		assertEquals("FamePackage", fmPckg.getName());
		assertTrue(fmPckg.getIsStub());
		
		AnnotationType fmDesc = TestVerveineUtils.detectElement(repo,AnnotationType.class, "FameDescription");
		assertNotNull(fmDesc);
		assertEquals("FameDescription", fmDesc.getName());
		assertTrue(fmDesc.getIsStub());

		// class annotations 
		fr.inria.verveine.core.gen.famix.Class clazz = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Element");
		assertNotNull(clazz);
		Collection<AnnotationInstance> annInstances = clazz.getAnnotationInstances();
		assertEquals(2, annInstances.size());
		for (AnnotationInstance annotationInstance : annInstances) {
			assertSame(clazz, annotationInstance.getAnnotatedEntity());
			if (annotationInstance.getAnnotationType().getName().equals("FamePackage")) {
				assertSame(fmPckg, annotationInstance.getAnnotationType());
			} else {
				assertEquals("FameDescription", annotationInstance.getAnnotationType().getName());
				assertSame(fmDesc, annotationInstance.getAnnotationType());
			}
		}

		AnnotationTypeAttribute decl = null;
		for (AnnotationTypeAttribute a : TestVerveineUtils.listElements(repo, AnnotationTypeAttribute.class, "derived")) {
			if (a.getParentAnnotationType() == fmProp) {
				decl = a;
				break;
			}
		}
		assertNotNull(decl);
		
		// Method annotations
		for (Method meth : clazz.getMethods()) {
			annInstances = meth.getAnnotationInstances();
			if (meth.getName().equals("getFullname") || meth.getName().equals("getName") || meth.getName().equals("getOwner")) {
				assertEquals(1, annInstances.size());
				AnnotationInstance annInst = annInstances.iterator().next();
				assertSame(fmProp, annInst.getAnnotationType());
				if (meth.getName().equals("getOwner")) {
					Collection<AnnotationInstanceAttribute> aiAtts = annInst.getAttributes();
					assertEquals(1, aiAtts.size());
					AnnotationInstanceAttribute annAtt = aiAtts.iterator().next();
					assertEquals( annInst, annAtt.getParentAnnotationInstance());
					assertEquals( "true", annAtt.getValue());
					assertEquals(decl, annAtt.getAnnotationTypeAttribute());
				}
			}
			else if (meth.getName().equals("toString")) {
				assertEquals(1, annInstances.size());
			}
			else {
				assertEquals(0, annInstances.size());
			}
		}

		for (Attribute att : clazz.getAttributes()) {
			assertEquals(0, att.getAnnotationInstances().size());
		}

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
	
	@Test
	public void testFieldArgumentTypes() {
		Attribute famixAtt = TestVerveineUtils.detectElement(repo, Attribute.class, "mapBind");
		assertNotNull(famixAtt);
		assertEquals("mapBind", famixAtt.getName());
		//assertEquals(2, famixAtt.getDeclaredArgumentTypes().size());
		//Iterator<Type> it = famixAtt.getDeclaredArgumentTypes().iterator();
		//assertSame(TestVerveineUtils.detectElement(repo, Type.class, "B"), it.next());
		//assertSame(TestVerveineUtils.detectElement(repo, Type.class, "NamedEntity"), it.next());
	}
	
	@Test
	public void testMethodParameterArgumentTypes() {
		Method fmxMethod = TestVerveineUtils.detectElement(repo, Method.class, "getEntityByName");
		assertNotNull(fmxMethod);
		assertEquals(2, fmxMethod.getParameters().size());
		for (Parameter fmxParameter : fmxMethod.getParameters()) {
			assertTrue(fmxParameter.getName().equals("fmxClass") || fmxParameter.getName().equals("name"));
			if (fmxParameter.getName().equals("fmxClass")) {
				//assertEquals(1, fmxParameter.getDeclaredArgumentTypes().size());
				//assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxParameter.getDeclaredArgumentTypes().iterator().next());
			}
			if (fmxParameter.getName().equals("name")) {
				//assertNull(fmxParameter.getDeclaredArgumentTypes());
			}
		}
		
		fmxMethod = TestVerveineUtils.detectElement(repo, Method.class, "createFamixEntity");
		assertNotNull(fmxMethod);
		assertEquals(2, fmxMethod.getParameters().size());
		for (Parameter fmxParameter : fmxMethod.getParameters()) {
			assertTrue(fmxParameter.getName().equals("fmxClass") || fmxParameter.getName().equals("name"));
			if (fmxParameter.getName().equals("fmxClass")) {
				//assertEquals(1, fmxParameter.getDeclaredArgumentTypes().size());
				//assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxParameter.getDeclaredArgumentTypes().iterator().next());
			}
			if (fmxParameter.getName().equals("name")) {
				///assertNull(fmxParameter.getDeclaredArgumentTypes());
			}
		}
		
		fmxMethod = TestVerveineUtils.detectElement(repo, Method.class, "ensureFamixEntity");
		assertNotNull(fmxMethod);
		assertEquals(3, fmxMethod.getParameters().size());
		for (Parameter fmxParameter : fmxMethod.getParameters()) {
			assertTrue(fmxParameter.getName().equals("fmxClass") || fmxParameter.getName().equals("bnd") || fmxParameter.getName().equals("name"));
			if (fmxParameter.getName().equals("fmxClass")) {
				//assertEquals(1, fmxParameter.getDeclaredArgumentTypes().size());
				//assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxParameter.getDeclaredArgumentTypes().iterator().next());
			}
			if (fmxParameter.getName().equals("bnd")) {
				//assertNull(fmxParameter.getDeclaredArgumentTypes());
			}
			if (fmxParameter.getName().equals("name")) {
				//assertNull(fmxParameter.getDeclaredArgumentTypes());
			}
		}
		
		fmxMethod = TestVerveineUtils.detectElement(repo, Method.class, "ensureFamixUniqEntity");
		assertNotNull(fmxMethod);
		assertEquals(3, fmxMethod.getParameters().size());
		for (Parameter fmxParameter : fmxMethod.getParameters()) {
			assertTrue(fmxParameter.getName().equals("fmxClass") || fmxParameter.getName().equals("bnd") || fmxParameter.getName().equals("name"));
			if (fmxParameter.getName().equals("fmxClass")) {
				//assertEquals(1, fmxParameter.getDeclaredArgumentTypes().size());
				//assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxParameter.getDeclaredArgumentTypes().iterator().next());
			}
			if (fmxParameter.getName().equals("bnd")) {
				//assertNull(fmxParameter.getDeclaredArgumentTypes());
			}
			if (fmxParameter.getName().equals("name")) {
				//assertNull(fmxParameter.getDeclaredArgumentTypes());
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
			//assertEquals(1, fmxLocalVariable.getDeclaredArgumentTypes().size());
			//assertSame(TestVerveineUtils.detectElement(repo, Type.class, "NamedEntity"), fmxLocalVariable.getDeclaredArgumentTypes().iterator().next());
		}
		
		fmxMethod = TestVerveineUtils.detectElement(repo, Method.class, "getEntityByName");
		assertNotNull(fmxMethod);
		assertEquals(2, fmxMethod.getLocalVariables().size());
		for (LocalVariable fmxLocalVariable : fmxMethod.getLocalVariables()) {
			assertTrue(fmxLocalVariable.getName().equals("ret") || fmxLocalVariable.getName().equals("l_name"));
			if (fmxLocalVariable.getName().equals("ret")) {
				//assertEquals(1, fmxLocalVariable.getDeclaredArgumentTypes().size());
				//assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxLocalVariable.getDeclaredArgumentTypes().iterator().next());
			}
			if (fmxLocalVariable.getName().equals("l_name")) {
				//assertEquals(1, fmxLocalVariable.getDeclaredArgumentTypes().size());
				//assertSame(TestVerveineUtils.detectElement(repo, Type.class, "NamedEntity"), fmxLocalVariable.getDeclaredArgumentTypes().iterator().next());
			}
		}
		
		fmxMethod = TestVerveineUtils.detectElement(repo, Method.class, "ensureFamixUniqEntity");
		assertNotNull(fmxMethod);
		assertEquals(2, fmxMethod.getLocalVariables().size());
		for (LocalVariable fmxLocalVariable : fmxMethod.getLocalVariables()) {
			assertTrue(fmxLocalVariable.getName().equals("fmx") || fmxLocalVariable.getName().equals("l"));
			if (fmxLocalVariable.getName().equals("fmx")) {
				//assertNull(fmxLocalVariable.getDeclaredArgumentTypes());
			}
			if (fmxLocalVariable.getName().equals("l")) {
				//assertEquals(1, fmxLocalVariable.getDeclaredArgumentTypes().size());
				//assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxLocalVariable.getDeclaredArgumentTypes().iterator().next());
			}
		}
	}
	
	
	@Test
	public void testMethodReturnArgumentTypes() {
		Method fmxMethod = TestVerveineUtils.detectElement(repo, Method.class, "getEntityByName");
		assertNotNull(fmxMethod);
		//assertEquals(1, fmxMethod.getDeclaredArgumentTypes().size());
		//assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxMethod.getDeclaredArgumentTypes().iterator().next());
	}

	@Test
	public void testEnumDecl() {
		fr.inria.verveine.core.gen.famix.Enum rk = TestVerveineUtils.detectElement(repo, fr.inria.verveine.core.gen.famix.Enum.class, "Rank");
		assertNotNull(rk);
		assertEquals("Rank", rk.getName());
		//assertEquals(13, card.getValues().size());
		assertSame(TestVerveineUtils.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "Card"), rk.getBelongsTo());

		EnumValue nine = TestVerveineUtils.detectElement(repo, EnumValue.class, "NINE");
		assertNotNull(nine);
		assertEquals("NINE", nine.getName());
		assertSame(rk, nine.getParentEnum());
		assertSame(rk, nine.getBelongsTo());

		fr.inria.verveine.core.gen.famix.Enum st = TestVerveineUtils.detectElement(repo, fr.inria.verveine.core.gen.famix.Enum.class, "Suit");
		assertNotNull(st);
		assertEquals("Suit", st.getName());
		//assertEquals(4, card.getValues().size());
		assertSame(TestVerveineUtils.detectElement(repo, Namespace.class, "ad_hoc"), st.getBelongsTo());

		EnumValue hrt = TestVerveineUtils.detectElement(repo, EnumValue.class, "HEARTS");
		assertNotNull(hrt);
		assertEquals("HEARTS", hrt.getName());
		assertSame(st, hrt.getParentEnum());
		assertSame(st, hrt.getBelongsTo());
	}

}