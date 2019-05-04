/**
 * Copyright (c) 2010 Anquetil Nicolas
 */
package fr.inria.verveine.extractor.java;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;

import eu.synectique.verveine.core.gen.famix.Parameter;
import org.junit.Before;
import org.junit.Test;

import ch.akuhn.fame.Repository;

import eu.synectique.verveine.core.VerveineUtilsForTests;
import eu.synectique.verveine.core.gen.famix.AnnotationInstance;
import eu.synectique.verveine.core.gen.famix.AnnotationInstanceAttribute;
import eu.synectique.verveine.core.gen.famix.AnnotationType;
import fr.inria.verveine.extractor.java.VerveineJParser;

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
		parser.setOptions(new String[] {"-cp" , "test_src/annotationsTest/lib/jboss-interceptors-api_1.1_spec-1.0.1.Final-redhat-2.jar" , "test_src/annotationsTest/src"});
		parser.parse();
		parser.emitMSE(VerveineJParser.OUTPUT_FILE);
	}

	@Test
	public void testAnnotationOnVar() {
		Parameter param = VerveineUtilsForTests.detectFamixElement(repo, Parameter.class, "annotatedParam");
		assertNotNull(param);

		assertEquals(1, param.getAnnotationInstances().size());
		AnnotationInstance inst = param.getAnnotationInstances().iterator().next();
		assertNotNull(inst);
		assertEquals("SuppressWarnings", inst.getAnnotationType().getName());
		assertSame(inst.getAnnotatedEntity(), param);

	}

	@Test
	public void testAnnotationType() {
		AnnotationType annTyp = VerveineUtilsForTests.detectFamixElement(repo, AnnotationType.class, "InterceptorsOnInterceptor");
		assertNotNull(annTyp);
		assertFalse(annTyp.getIsStub());
		assertEquals(1, annTyp.numberOfAttributes());
		assertEquals("value", annTyp.getAttributes().iterator().next().getName());
		assertEquals(2, annTyp.numberOfInstances());

		annTyp = VerveineUtilsForTests.detectFamixElement(repo, AnnotationType.class, "Interceptors");
		assertNotNull(annTyp);
		assertTrue(annTyp.getIsStub());
		assertEquals(1, annTyp.numberOfAttributes());
		assertEquals("value", annTyp.getAttributes().iterator().next().getName());
		assertEquals(3, annTyp.numberOfInstances());
	}

	@Test
	public void testAnnotationInstanceAttribute() {
		eu.synectique.verveine.core.gen.famix.Class clss = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "AnnotatedClass");
		assertNotNull(clss);

		assertEquals(1, clss.numberOfAnnotationInstances());
		AnnotationInstance inst = clss.getAnnotationInstances().iterator().next();
		assertEquals("Interceptors", inst.getAnnotationType().getName());
		assertSame(inst.getAnnotatedEntity(), clss);

		assertEquals(1, inst.numberOfAttributes());
		AnnotationInstanceAttribute att = inst.getAttributes().iterator().next();
		assertNotNull(att);
		assertEquals("value", att.getAnnotationTypeAttribute().getName());

		assertEquals("FirstInterceptorClass.class", att.getValue());

	}

	@Test
	public void testAnnotationInstanceArrayOfOne() {
		eu.synectique.verveine.core.gen.famix.Class clss = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "AThirdAnnotatedClass");
		assertNotNull(clss);

		assertEquals(1, clss.numberOfAnnotationInstances());
		AnnotationInstance inst = clss.getAnnotationInstances().iterator().next();
		assertNotNull(inst);
		assertEquals("Interceptors", inst.getAnnotationType().getName());
		assertSame(inst.getAnnotatedEntity(), clss);

		assertEquals(1, inst.numberOfAttributes());
		AnnotationInstanceAttribute att = inst.getAttributes().iterator().next();
		assertNotNull(att);
		assertEquals("value", att.getAnnotationTypeAttribute().getName());
		
		assertEquals("FirstInterceptorClass.class", att.getValue());

	}

	@Test
	public void testAnnotationInstanceEmptyArrayForValue() {
		eu.synectique.verveine.core.gen.famix.Class clss = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "AnotherInterceptorClass");
		assertNotNull(clss);

		assertEquals(1, clss.numberOfAnnotationInstances());
		AnnotationInstance inst = clss.getAnnotationInstances().iterator().next();
		assertNotNull(inst);

		assertEquals(1, inst.numberOfAttributes());
		AnnotationInstanceAttribute att = inst.getAttributes().iterator().next();
		assertNotNull(att);
		assertEquals("{}", att.getValue());

	}

	@Test
	public void testAnnotationInstanceArray() {
		eu.synectique.verveine.core.gen.famix.Class clss = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "AnotherAnnotatedClass");
		assertNotNull(clss);

		assertEquals(1, clss.numberOfAnnotationInstances());
		AnnotationInstance inst = clss.getAnnotationInstances().iterator().next();
		assertNotNull(inst);
		assertEquals("Interceptors", inst.getAnnotationType().getName());
		assertSame(inst.getAnnotatedEntity(), clss);

		assertEquals(1, inst.numberOfAttributes());
		AnnotationInstanceAttribute att = inst.getAttributes().iterator().next();
		assertNotNull(att);
		assertEquals("value", att.getAnnotationTypeAttribute().getName());
		
		assertEquals("{FirstInterceptorClass.class, AnotherInterceptorClass.class}", att.getValue());

	}
}