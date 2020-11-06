/**
 * Copyright (c) 2010 Anquetil Nicolas
 */
package fr.inria.verveine.extractor.java;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.Exception;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import org.moosetechnology.model.famix.famix.*;
import org.moosetechnology.model.famix.famixtraits.TAnnotationInstance;
import org.moosetechnology.model.famix.famixtraits.TAttribute;
import org.moosetechnology.model.famix.famixtraits.TNamedEntity;


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
		assertEquals(3, entitiesOfType( AnnotationType.class).size()); // @WebService, @SOAPBinding, @WebMethod
		// JDT no longer returns not resolved anotations: @Session, @WLHttpTransport,
	}

	@Test
	public void testAnnotation() {
		AnnotationType ann = detectFamixElement(AnnotationType.class, "WebService");
		assertNotNull(ann);
		assertTrue(ann.getIsStub());
		assertEquals(3, ann.getInstances().size());

		assertEquals(3, ann.getAttributes().size());
		for (TAttribute ta : ann.getAttributes()) {
			Attribute a = (Attribute) ta;
			assertEquals(AnnotationTypeAttribute.class, a.getClass());
			assertTrue(a.getName().equals("name") || a.getName().equals("serviceName") || a.getName().equals("targetNamespace"));
		}

		// Class annotation
		org.moosetechnology.model.famix.famix.Class cl = detectFamixElement(org.moosetechnology.model.famix.famix.Class.class, "SimpleBean");
		assertNotNull(cl);
		assertEquals(2, cl.getAnnotationInstances().size());
		for (TAnnotationInstance a :cl.getAnnotationInstances() ) {
			AnnotationInstance ai = (AnnotationInstance) a;
			if (((TNamedEntity) ai.getAnnotationType()).getName().equals("WebService")) {
				assertEquals(detectFamixElement(Namespace.class, "jws"), ((AnnotationType) ai.getAnnotationType()).getBelongsTo());
			}
			else if (((TNamedEntity)ai.getAnnotationType()).getName().equals("SOAPBinding")) {
				assertEquals(detectFamixElement(Namespace.class, "soap"), ((AnnotationType) ai.getAnnotationType()).getBelongsTo());
			}
			else {
				assertTrue("Unexpected AnnotationInstance for SimpleBean: "+ ((AnnotationType)ai.getAnnotationType()).getName(), false);
			}
		}

		// Method annotations
		Method rep = detectFamixElement(Method.class, "orderResponse");
		assertNotNull(rep);
		Collection<TAnnotationInstance> annInstances = rep.getAnnotationInstances();
		assertEquals(1, annInstances.size());
		TAnnotationInstance annInst = firstElt(annInstances);
		assertEquals("WebMethod", ((TNamedEntity)((AnnotationInstance)annInst).getAnnotationType()).getName());

		
	}

}