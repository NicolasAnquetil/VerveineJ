/**
 * Copyright (c) 2010 Anquetil Nicolas
 */
package tests.fr.inria.verveine.extractor.java;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.fr.inria.verveine.core.TestVerveineUtils;
import ch.akuhn.fame.Repository;
import fr.inria.verveine.core.gen.famix.AnnotationInstance;
import fr.inria.verveine.core.gen.famix.AnnotationType;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.CaughtException;
import fr.inria.verveine.core.gen.famix.DeclaredException;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.ThrownException;
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
		// there are two "lire" methods, but both serve our purpose here so we just take the first that will be returned
		Method meth = TestVerveineUtils.detectElement(repo, Method.class, "lire");
		assertNotNull(meth);
		
		fr.inria.verveine.core.gen.famix.Class excepClass = TestVerveineUtils.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "ReadException");
		
		assertEquals(1, meth.getDeclaredExceptions().size());
		DeclaredException exD = meth.getDeclaredExceptions().iterator().next();
		assertSame(meth, exD.getDefiningMethod());
		assertSame(excepClass, exD.getExceptionClass());
		
		assertEquals(1, meth.getThrownExceptions().size());
		ThrownException exT = meth.getThrownExceptions().iterator().next();
		assertSame(meth, exT.getDefiningMethod());
		assertSame(excepClass, exT.getExceptionClass());

		excepClass = TestVerveineUtils.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "IOException");
		
		assertEquals(1,meth.getCaughtExceptions().size());
		CaughtException exC = meth.getCaughtExceptions().iterator().next();
		assertSame(meth, exC.getDefiningMethod());
		assertSame(excepClass, exC.getExceptionClass());
	}
	
	@Test
	public void testAnnotation() {
		fr.inria.verveine.core.gen.famix.Class clazz;
		Collection<AnnotationInstance> annInstances;
		
		AnnotationType annTypeOverride = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.AnnotationType.class, "Override");
		assertNotNull(annTypeOverride);
		assertEquals("Override", annTypeOverride.getName());
		
		AnnotationType annTypeDeprecated = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.AnnotationType.class, "Deprecated");
		assertNotNull(annTypeDeprecated);
		assertEquals("Deprecated", annTypeDeprecated.getName());
		
		//Annotations to the class
		clazz = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Client");
		assertNotNull(clazz);
		annInstances = clazz.getAnnotationInstances();
		assertEquals(2, annInstances.size());
		for (AnnotationInstance annotationInstance : annInstances) {
			assertSame(clazz, annotationInstance.getAnnotatedEntity());
			if (annotationInstance.getAnnotationType().getName().equals("Override")) {
				assertEquals("Override", annotationInstance.getAnnotationType().getName());
				assertSame(annTypeOverride, annotationInstance.getAnnotationType());
			} else {
				assertEquals("Deprecated", annotationInstance.getAnnotationType().getName());
				assertSame(annTypeDeprecated, annotationInstance.getAnnotationType());
			}
		}
		//Annotations to the attributes
		for (Attribute attribute : clazz.getAttributes()) {
			annInstances = attribute.getAnnotationInstances();
			if (attribute.getName().equals("num")) {
				assertEquals(2, annInstances.size());
				for (AnnotationInstance annotationInstance : attribute.getAnnotationInstances()) {
					assertSame(attribute, annotationInstance.getAnnotatedEntity());
					if (annotationInstance.getAnnotationType().getName().equals("Override")) {
						assertEquals("Override", annotationInstance.getAnnotationType().getName());
						assertSame(annTypeOverride, annotationInstance.getAnnotationType());
					} else {
						assertEquals("Deprecated", annotationInstance.getAnnotationType().getName());
						assertSame(annTypeDeprecated, annotationInstance.getAnnotationType());
					}
				}
			} else if (attribute.getName().equals("nom")) {
				assertEquals(1, annInstances.size());
				for (AnnotationInstance annotationInstance : attribute.getAnnotationInstances()) {
					assertSame(attribute, annotationInstance.getAnnotatedEntity());
					if (annotationInstance.getAnnotationType().getName().equals("Override")) {
						assertEquals("Override", annotationInstance.getAnnotationType().getName());
						assertSame(annTypeOverride, annotationInstance.getAnnotationType());
					} else {
						assertEquals("Deprecated", annotationInstance.getAnnotationType().getName());
						assertSame(annTypeDeprecated, annotationInstance.getAnnotationType());
					}
				}
			} else {
				assertEquals(0, annInstances.size());
			}
		}
		//Annotations to the methods
		for (Method method : clazz.getMethods()) {
			annInstances = method.getAnnotationInstances();
			if (method.getName().equals("Client")) {
				assertEquals(2, annInstances.size());
				for (AnnotationInstance annotationInstance : method.getAnnotationInstances()) {
					assertSame(method, annotationInstance.getAnnotatedEntity());
					if (annotationInstance.getAnnotationType().getName().equals("Override")) {
						assertEquals("Override", annotationInstance.getAnnotationType().getName());
						assertSame(annTypeOverride, annotationInstance.getAnnotationType());
					} else {
						assertEquals("Deprecated", annotationInstance.getAnnotationType().getName());
						assertSame(annTypeDeprecated, annotationInstance.getAnnotationType());
					}
				}
			} else if (method.getName().equals("lire")) {
				assertEquals(2, annInstances.size());
				for (AnnotationInstance annotationInstance : method.getAnnotationInstances()) {
					assertSame(method, annotationInstance.getAnnotatedEntity());
					if (annotationInstance.getAnnotationType().getName().equals("Override")) {
						assertEquals("Override", annotationInstance.getAnnotationType().getName());
						assertSame(annTypeOverride, annotationInstance.getAnnotationType());
					} else {
						assertEquals("Deprecated", annotationInstance.getAnnotationType().getName());
						assertSame(annTypeDeprecated, annotationInstance.getAnnotationType());
					}
				}
			} else if (method.getName().equals("setNum")) {
				assertEquals(2, annInstances.size());
				for (AnnotationInstance annotationInstance : method.getAnnotationInstances()) {
					assertSame(method, annotationInstance.getAnnotatedEntity());
					if (annotationInstance.getAnnotationType().getName().equals("Override")) {
						assertEquals("Override", annotationInstance.getAnnotationType().getName());
						assertSame(annTypeOverride, annotationInstance.getAnnotationType());
					} else {
						assertEquals("Deprecated", annotationInstance.getAnnotationType().getName());
						assertSame(annTypeDeprecated, annotationInstance.getAnnotationType());
					}
				}
			}else {
				assertEquals(0, annInstances.size());
			}
		}
	}
}