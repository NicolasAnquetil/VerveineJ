/**
 * Copyright (c) 2010 Anquetil Nicolas
 */
package fr.inria.verveine.extractor.java;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import eu.synectique.verveine.core.VerveineUtilsForTests;
import eu.synectique.verveine.core.gen.famix.AnnotationInstance;
import eu.synectique.verveine.core.gen.famix.AnnotationType;
import eu.synectique.verveine.core.gen.famix.AnnotationTypeAttribute;
import eu.synectique.verveine.core.gen.famix.Attribute;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.Namespace;


/**
 * @author Nicolas Anquetil
 * @since November 25, 2010
 *
 */
public class VerveineJTest_JWSBasic extends VerveineJTest_Basic {

	public VerveineJTest_JWSBasic() throws IllegalAccessException {
		super(new boolean[] {true, true, true, true, true, false, true});
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		new File(VerveineJParser.OUTPUT_FILE).delete();
		VerveineJParser parser = new VerveineJParser();
		repo = parser.getFamixRepo();
		parser.setOptions(new String[] {"test_src/jws_basic"});
		parser.parse();
	//	parser.emitMSE(VerveineJParser.OUTPUT_FILE);
	}

	@Test
	public void testEntitiesNumber() {
		assertEquals(3, VerveineUtilsForTests.selectElementsOfType(repo, AnnotationType.class).size()); // @WebService, @SOAPBinding, @WebMethod
		// JDT no longer returns not resolved anotations: @Session, @WLHttpTransport,
	}

	@Test
	public void testAnnotation() {
		AnnotationType ann = VerveineUtilsForTests.detectFamixElement(repo,AnnotationType.class, "WebService");
		assertNotNull(ann);
		assertTrue(ann.getIsStub());
		assertEquals(3, ann.getInstances().size());

		assertEquals(3, ann.getAttributes().size());
		for (Attribute a : ann.getAttributes()) {
			assertEquals(AnnotationTypeAttribute.class, a.getClass());
			assertTrue(a.getName().equals("name") || a.getName().equals("serviceName") || a.getName().equals("targetNamespace"));
		}

		// Class annotation
		eu.synectique.verveine.core.gen.famix.Class cl = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "SimpleBean");
		assertNotNull(cl);
		assertEquals(2, cl.getAnnotationInstances().size());
		for (AnnotationInstance ai :cl.getAnnotationInstances() ) {
			if (ai.getAnnotationType().getName().equals("WebService")) {
				assertEquals(VerveineUtilsForTests.detectFamixElement(repo,Namespace.class, "jws"), ai.getAnnotationType().getBelongsTo());
			}
			else if (ai.getAnnotationType().getName().equals("SOAPBinding")) {
				assertEquals(VerveineUtilsForTests.detectFamixElement(repo,Namespace.class, "soap"), ai.getAnnotationType().getBelongsTo());
			}
			else {
				assertTrue("Unexpected AnnotationInstance for SimpleBean: "+ ai.getAnnotationType().getName(), false);
			}
		}

		// Method annotations
		Method rep = VerveineUtilsForTests.detectFamixElement(repo,Method.class, "orderResponse");
		assertNotNull(rep);
		Collection<AnnotationInstance> annInstances = rep.getAnnotationInstances();
		assertEquals(1, annInstances.size());
		AnnotationInstance annInst = annInstances.iterator().next();
		assertEquals("WebMethod", annInst.getAnnotationType().getName());

		
	}

}