/**
 * Copyright (c) 2010 Nicolas Anquetil
 */
package tests.fr.inria.verveine.extractor.java;

/*
PrintServer
- OutputServer
+ IPrinter
- String
- boolean
+ int
- Packet
- System
- System.out

OutputServer
- Node
+ String
- Packet
- boolean

IPrinter
- String
- boolean

AbstractDestinationAddress
- String
- boolean

Node
+ String
- boolean
+ Node
- Packet
- StringBuffer

Packet
+ String
+ SingleDestinationAddress
+ Node
- StringBuffer

SingleDestinationAddress
- AbstractDestinationAddress
+ String
- boolean

WorkStation
- Node
+ String
- Packet
- System
- System.out

FileServer
- OutputServer
- Packet
- System
- System.out
- String
*/

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.util.Collection;

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
import fr.inria.verveine.core.gen.famix.Reference;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.VerveineJParser;

/**
 * @author Nicolas Anquetil
 * @since May 28, 2010
 *
 */
public class VerveineJTest_Summarized extends VerveineJTest_Basic {

	public VerveineJTest_Summarized() {
		super(/*system*/false);  // don't test that System and members are created correctly
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		new File(VerveineJParser.OUTPUT_FILE).delete();

		String[] files = new String[] {
				"AbstractDestinationAddress.java",
				"Node.java",
				"Packet.java",
				"SingleDestinationAddress.java",
				"WorkStation.java",
				"server/FileServer.java",
				"server/IPrinter.java",
				"server/OutputServer.java",
				"server/PrintServer.java"
		};


		// separate parsing of each source file --------
		for (String f : files) {
			parseFile(f);
		}

	}

	/**
	 * Parses the file received in parameter independently from any other
	 * The "separate parsing" mechanism should ensure that linkages are appropriately done
	 * @param file -- name of the file to parse
	 */
	private void parseFile(String file) {
		String[] args = new String[] {
				"-summary",
				"-cp",
				"test_src/LANModel/",
				"test_src/LANModel/moose/lan/"+file
				};
		
		VerveineJParser parser = new VerveineJParser();
		repo = parser.getFamixRepo();
		parser.setOptions(args);
		parser.parse();
		
		new File(VerveineJParser.OUTPUT_FILE).delete();  // delete old MSE file
		parser.emitMSE(VerveineJParser.OUTPUT_FILE);  // to create a new one
	}

	@Test
	public void testEntitiesNumber() {
		assertEquals(10+14, TestVerveineUtils.selectElementsOfType(repo, fr.inria.verveine.core.gen.famix.Class.class).size());
		assertEquals(3,     TestVerveineUtils.selectElementsOfType(repo, PrimitiveType.class).size());
		assertEquals(0, 	TestVerveineUtils.selectElementsOfType(repo, Method.class).size());
		assertEquals(0, 	TestVerveineUtils.selectElementsOfType(repo, Attribute.class).size());
		assertEquals(2+4,   TestVerveineUtils.selectElementsOfType(repo, Namespace.class).size());
		assertEquals(0,     TestVerveineUtils.selectElementsOfType(repo, Parameter.class).size());
		assertEquals(0,     TestVerveineUtils.selectElementsOfType(repo, Invocation.class).size());
		assertEquals(10+18,  TestVerveineUtils.selectElementsOfType(repo, Inheritance.class).size());
		assertEquals(0,     TestVerveineUtils.selectElementsOfType(repo, Access.class).size());
		assertEquals(26,    TestVerveineUtils.selectElementsOfType(repo, Reference.class).size());  // TODO check that number
		assertEquals(0,     TestVerveineUtils.selectElementsOfType(repo, LocalVariable.class).size());
		assertEquals(0,     TestVerveineUtils.selectElementsOfType(repo, AnnotationType.class).size());  // TODO should be 1 ?
		assertEquals(0,     TestVerveineUtils.selectElementsOfType(repo, AnnotationInstance.class).size());  // TODO should be 2 ?
		assertEquals(1,     TestVerveineUtils.selectElementsOfType(repo, ParameterizableClass.class).size());
	}

	@Test
	public void testInheritance() {
		fr.inria.verveine.core.gen.famix.Class clazz;
		Collection<Inheritance> superInheritances;
		Inheritance inh, inh2 = null;
		
		clazz = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "PrintServer");
		assertNotNull(clazz);
		superInheritances = clazz.getSuperInheritances();
		assertEquals(1, superInheritances.size());
		inh = superInheritances.iterator().next();
		assertSame(clazz, inh.getSubclass());
		assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "OutputServer"), inh.getSuperclass());

		clazz = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(clazz);
		superInheritances = clazz.getSuperInheritances();
		assertEquals(1, superInheritances.size());
		inh = superInheritances.iterator().next();
		assertSame(clazz, inh.getSubclass());
		assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, JavaDictionary.OBJECT_NAME), inh.getSuperclass());
		
		clazz = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "XPrinter");
		assertNotNull(clazz);
		superInheritances = clazz.getSuperInheritances();
		assertEquals(2, superInheritances.size()); // superInheritances: Object and IPrinter (in this order)
		for (Inheritance inheritance : superInheritances) {
			assertSame(clazz, inheritance.getSubclass());
			if (inheritance.getSuperclass().getName().equals("IPrinter")) {
				inh2 = inheritance;
				assertNull(inheritance.getNext());
				assertSame(inheritance,inheritance.getPrevious().getNext());
				assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "IPrinter"), inheritance.getSuperclass());
			} else {
				inh = inheritance;
				assertNull(inheritance.getPrevious());
				assertSame(inheritance,inheritance.getNext().getPrevious());
				assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, JavaDictionary.OBJECT_NAME), inheritance.getSuperclass());
			}
		}
		assertSame(inh.getNext(), inh2);
		assertSame(inh2.getPrevious(), inh);
	}

}