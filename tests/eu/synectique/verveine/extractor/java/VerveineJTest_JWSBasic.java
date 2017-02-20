/**
 * Copyright (c) 2010 Anquetil Nicolas
 */
package eu.synectique.verveine.extractor.java;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import eu.synectique.verveine.core.VerveineUtilsForTests;
import eu.synectique.verveine.extractor.java.JavaDictionary;
import eu.synectique.verveine.extractor.java.VerveineJParser;
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

	public VerveineJTest_JWSBasic() {
		super(/*system*/false);  // yes please, test that System and members are created correctly
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
		parser.emitMSE(VerveineJParser.OUTPUT_FILE);
	}

	@Test
	public void testEntitiesNumber() {
		assertEquals(5, VerveineUtilsForTests.selectElementsOfType(repo, AnnotationType.class).size()); // @Session, @WebService, @SOAPBinding, @WLHttpTransport, @WebMethod
	}

	@Test
	public void testAnnotation() {
		AnnotationType sessionAnn = VerveineUtilsForTests.detectFamixElement(repo,AnnotationType.class, "Session");
		assertNotNull(sessionAnn);
		assertTrue(sessionAnn.getIsStub());
		assertEquals(3, sessionAnn.getInstances().size());

		assertEquals(2, sessionAnn.getAttributes().size());
		for (Attribute a : sessionAnn.getAttributes()) {
			assertEquals(AnnotationTypeAttribute.class, a.getClass());
			assertTrue(a.getName().equals("ejbName") || a.getName().equals("serviceEndpoint"));
		}

		// Class annotation
		Namespace defPckg = VerveineUtilsForTests.detectFamixElement(repo,Namespace.class, JavaDictionary.DEFAULT_PCKG_NAME);
		assertNotNull(defPckg);
		eu.synectique.verveine.core.gen.famix.Class cl = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "SimpleBean");
		assertNotNull(cl);
		assertEquals(4, cl.getAnnotationInstances().size());
		for (AnnotationInstance ai :cl.getAnnotationInstances() ) {
			if (ai.getAnnotationType().getName().equals("Session") || ai.getAnnotationType().getName().equals("WLHttpTransport")) {
				assertEquals(defPckg, ai.getAnnotationType().getBelongsTo());
			}
			else if (ai.getAnnotationType().getName().equals("WebService") || ai.getAnnotationType().getName().equals("SOAPBinding")) {
				assertTrue(defPckg != ai.getAnnotationType().getBelongsTo());
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