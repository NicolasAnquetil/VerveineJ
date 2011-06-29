/**
 * Copyright (c) 2010 Nicolas Anquetil
 */
package tests.fr.inria.verveine.extractor.java;


import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import test.fr.inria.verveine.core.TestVerveineUtils;
import fr.inria.verveine.core.gen.famix.Access;
import fr.inria.verveine.core.gen.famix.AnnotationInstance;
import fr.inria.verveine.core.gen.famix.AnnotationType;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.Inheritance;
import fr.inria.verveine.core.gen.famix.Invocation;
import fr.inria.verveine.core.gen.famix.LocalVariable;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.Parameter;
import fr.inria.verveine.core.gen.famix.ParameterizableClass;
import fr.inria.verveine.core.gen.famix.PrimitiveType;
import fr.inria.verveine.extractor.java.VerveineJParser;

/**
 * @author Nicolas Anquetil
 * @since May 28, 2010
 *
 */
public class VerveineJTest_noLocals extends VerveineJTest_Basic {

	public VerveineJTest_noLocals() {
		super(/*system*/false);  // i.e. no need to test that 'System' and members are created correctly
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		new File(VerveineJParser.OUTPUT_FILE).delete();

		VerveineJParser parser = new VerveineJParser();
		repo = parser.getFamixRepo();
		parser.setOptions(new String[] {"-l", "test_src/LANModel/"});
		parser.parse();
		parser.emitMSE(VerveineJParser.OUTPUT_FILE);
	}

	@Test
	// compare with numbers in VerveineJTest_LanMode.testEntitiesNumber()
	public void testEntitiesNumber() {
		assertEquals(25,    TestVerveineUtils.selectElementsOfType(repo, fr.inria.verveine.core.gen.famix.Class.class).size());
		assertEquals(3,     TestVerveineUtils.selectElementsOfType(repo, PrimitiveType.class).size());
		assertEquals(48,    TestVerveineUtils.selectElementsOfType(repo, Method.class).size());
		assertEquals(11-2,  TestVerveineUtils.selectElementsOfType(repo, Attribute.class).size());//{Node.nextNode,XPrinter.uselessNumber}
		assertEquals(6,     TestVerveineUtils.selectElementsOfType(repo, Namespace.class).size());
		assertEquals(26-4,  TestVerveineUtils.selectElementsOfType(repo, Parameter.class).size());//{Node.nextNose(Node),IPrinter.print(String,boolean),XPrinter.print(String,boolean),anonymous(IPrinter).print(String,boolean)}
		assertEquals(54-24, TestVerveineUtils.selectElementsOfType(repo, Invocation.class).size());//{WorkStation-2,SingleDestination-2,Packet-4,PrintServer-1,OutputServer-3,FileServer-1,Node-11}
		assertEquals(30,    TestVerveineUtils.selectElementsOfType(repo, Inheritance.class).size());
		assertEquals(50-28, TestVerveineUtils.selectElementsOfType(repo, Access.class).size());//{WorkStation-5,SingleDestinationAddress-3,Packet-5,PrintServer-1,XPrinter-1,anonymous(IPrinter)-1,OutputServer-3,FileServer-1,Node-8}
		assertEquals(0,     TestVerveineUtils.selectElementsOfType(repo, LocalVariable.class).size());
		assertEquals(1,     TestVerveineUtils.selectElementsOfType(repo, AnnotationType.class).size());
		assertEquals(2,     TestVerveineUtils.selectElementsOfType(repo, AnnotationInstance.class).size());
		assertEquals(1,     TestVerveineUtils.selectElementsOfType(repo, ParameterizableClass.class).size());
	}

}