/**
 * Copyright (c) 2010 Anquetil Nicolas
 */
package tests.fr.inria.verveine.extractor.java;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import ch.akuhn.fame.Repository;

import fr.inria.verveine.core.VerveineUtilsForTests;
import fr.inria.verveine.core.gen.famix.AnnotationInstance;
import fr.inria.verveine.core.gen.famix.AnnotationInstanceAttribute;
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
		parser.setOptions(new String[] {"-cp" , "test_src/annotations-test/lib/jboss-interceptors-api_1.1_spec-1.0.1.Final-redhat-2.jar" , "test_src/annotations-test/src"});
		parser.parse();
		parser.emitMSE(VerveineJParser.OUTPUT_FILE);
	}

	@Test
	public void testAnnotationInstanceAttribute() {
		fr.inria.verveine.core.gen.famix.Class clss = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "AnnotatedClass");
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
}