/**
 * Copyright (c) 2010 Anquetil Nicolas
 */
package eu.synectique.verveine.extractor.java;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import ch.akuhn.fame.Repository;

import eu.synectique.verveine.core.VerveineUtilsForTests;
import eu.synectique.verveine.extractor.java.VerveineJParser;
import eu.synectique.verveine.core.gen.famix.AnnotationInstance;
import eu.synectique.verveine.core.gen.famix.AnnotationInstanceAttribute;

/**
 * @author Nicolas Anquetil
 * @since November 25, 2010
 *
 */
public class VerveineJTest_AnnotationTest {

	protected Repository repo;
	protected VerveineJParser parser;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		new File(VerveineJParser.OUTPUT_FILE).delete();
		VerveineJParser parser = new VerveineJParser();
		repo = parser.getFamixRepo();
		parser.setOptions(new String[] {"-cp" , "test_src/annotations-test/lib/jboss-interceptors-api_1.1_spec-1.0.1.Final-redhat-2.jar" , "test_src/annotations-test/src"});
		parser.parse();
		parser.emitMSE(VerveineJParser.OUTPUT_FILE);
	}

	@Test
	public void testAnnotationInstanceAttribute() {
		eu.synectique.verveine.core.gen.famix.Class clss = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "AnnotatedClass");
		assertNotNull(clss);

		assertEquals(1, clss.getAnnotationInstances().size());
		AnnotationInstance inst = clss.getAnnotationInstances().iterator().next();
		assertNotNull(inst);
		assertEquals("Interceptors", inst.getAnnotationType().getName());
		assertSame(inst.getAnnotatedEntity(), clss);

		assertEquals(1, inst.getAttributes().size());
		AnnotationInstanceAttribute att = inst.getAttributes().iterator().next();
		assertNotNull(att);
		assertEquals("value", att.getAnnotationTypeAttribute().getName());
		
		assertEquals("InterceptorClass.class", att.getValue());

	}

	@Test
	public void testAnnotationInstanceArrayOfOne() {
		eu.synectique.verveine.core.gen.famix.Class clss = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "AThirdAnnotatedClass");
		assertNotNull(clss);

		assertEquals(1, clss.getAnnotationInstances().size());
		AnnotationInstance inst = clss.getAnnotationInstances().iterator().next();
		assertNotNull(inst);
		assertEquals("Interceptors", inst.getAnnotationType().getName());
		assertSame(inst.getAnnotatedEntity(), clss);

		assertEquals(1, inst.getAttributes().size());
		AnnotationInstanceAttribute att = inst.getAttributes().iterator().next();
		assertNotNull(att);
		assertEquals("value", att.getAnnotationTypeAttribute().getName());
		
		assertEquals("InterceptorClass.class", att.getValue());

	}

	@Test
	public void testAnnotationInstanceEmptyArrayForValue() {
		eu.synectique.verveine.core.gen.famix.Class clss = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "AnotherInterceptorClass");
		assertNotNull(clss);

		assertEquals(1, clss.getAnnotationInstances().size());
		AnnotationInstance inst = clss.getAnnotationInstances().iterator().next();
		assertNotNull(inst);

		assertEquals(1, inst.getAttributes().size());
		AnnotationInstanceAttribute att = inst.getAttributes().iterator().next();
		assertNotNull(att);
		assertEquals("{}", att.getValue());

	}

	@Test
	public void testAnnotationInstanceArray() {
		eu.synectique.verveine.core.gen.famix.Class clss = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "AnotherAnnotatedClass");
		assertNotNull(clss);

		assertEquals(1, clss.getAnnotationInstances().size());
		AnnotationInstance inst = clss.getAnnotationInstances().iterator().next();
		assertNotNull(inst);
		assertEquals("Interceptors", inst.getAnnotationType().getName());
		assertSame(inst.getAnnotatedEntity(), clss);

		assertEquals(1, inst.getAttributes().size());
		AnnotationInstanceAttribute att = inst.getAttributes().iterator().next();
		assertNotNull(att);
		assertEquals("value", att.getAnnotationTypeAttribute().getName());
		
		assertEquals("{InterceptorClass.class, AnotherInterceptorClass.class}", att.getValue());

	}
}