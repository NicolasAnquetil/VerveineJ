/**
 * Copyright (c) 2010 Nicolas Anquetil
 */
package fr.inria.verveine.extractor.java;

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

import java.io.File;
import java.lang.Exception;
import java.util.Collection;

import eu.synectique.verveine.core.gen.famix.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.synectique.verveine.core.VerveineUtilsForTests;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.VerveineJParser;

import static org.junit.Assert.*;

/**
 * @author Nicolas Anquetil
 * @since May 28, 2010
 *
 */
public class VerveineJTest_Summarized extends VerveineJTest_Basic {

	public VerveineJTest_Summarized() throws IllegalAccessException {
		super(new boolean[] {false, false, false, true, true, true, true});
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
				"-i",
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

	@Test(timeout=100)
	public void testEntitiesNumber() {
		int nbClasses = 10+5; // one less than in VerveineJTest_LanModel because anonymous class is not created

		assertEquals(nbClasses, VerveineUtilsForTests.selectElementsOfType(repo, eu.synectique.verveine.core.gen.famix.Class.class).size());
		assertEquals(3,  VerveineUtilsForTests.selectElementsOfType(repo, PrimitiveType.class).size());
		assertEquals(0,  VerveineUtilsForTests.selectElementsOfType(repo, Method.class).size());
		assertEquals(0,  VerveineUtilsForTests.selectElementsOfType(repo, Attribute.class).size());
		assertEquals(2+4,VerveineUtilsForTests.selectElementsOfType(repo, Namespace.class).size());
		assertEquals(0,  VerveineUtilsForTests.selectElementsOfType(repo, Parameter.class).size());
		assertEquals(0,  VerveineUtilsForTests.selectElementsOfType(repo, LocalVariable.class).size());
		assertEquals(1,  VerveineUtilsForTests.selectElementsOfType(repo, AnnotationType.class).size());
		assertEquals(0,  VerveineUtilsForTests.selectElementsOfType(repo, AnnotationInstance.class).size());    // Override annotations on methods
		assertEquals(0,  VerveineUtilsForTests.selectElementsOfType(repo, ParameterizableClass.class).size());  // class Comparable no longer created
	}

	@Test
	public void testAccesses() {
		assertEquals(0,  VerveineUtilsForTests.selectElementsOfType(repo, Access.class).size());

		for (Access acc : VerveineUtilsForTests.selectElementsOfType(repo, Access.class)) {
			assertNotNull(acc.getVariable());
			assertNotNull(acc.getAccessor());
		}
	}

	@Test
	@Ignore
	public void testInvocations() {
		assertEquals(0,  VerveineUtilsForTests.selectElementsOfType(repo, Invocation.class).size());

		for (Invocation invok : VerveineUtilsForTests.selectElementsOfType(repo, Invocation.class)) {
			assertNotNull(invok.getCandidates());
			assertNotNull(invok.getSender());
		}
	}

	@Test
	@Ignore
	public void testReferences() {
		assertEquals(9,  VerveineUtilsForTests.selectElementsOfType(repo, Reference.class).size());  // Node:1 , WorkStation:2 , FileServer:2 , PrintServer:4

		for (Reference ref : VerveineUtilsForTests.selectElementsOfType(repo, Reference.class)) {
			assertNotNull(ref.getTarget());
			assertNotNull(ref.getSource());
		}
	}

	@Test
	public void testInheritance() {
	    // --WorkStation extends Node
        // --SingleDestinationAddress extends AbstractDestinationAddress
        // --Packet
        // --Node
        // --AbstractDestinationAddress
        // --PrintServer extends OutputServer
        // --XPrinter implements IPrinter
        // --XPrinter
        // ** **new IPrinter() {
        // --OutputServer extends Node
        // --FileServer extends OutputServer

		eu.synectique.verveine.core.gen.famix.Class clazz;
		Collection<Inheritance> inherits;
		Inheritance inh, inh2 = null;

		assertEquals(10, VerveineUtilsForTests.selectElementsOfType(repo, Inheritance.class).size()); // one less than in VerveineJTest_LanModel because anonymous class is not created

		clazz = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "PrintServer");
		assertNotNull(clazz);
        inherits = clazz.getSuperInheritances();
		assertEquals(1, inherits.size());
		inh = inherits.iterator().next();
		assertSame(clazz, inh.getSubclass());
		assertSame(VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "OutputServer"), inh.getSuperclass());

		clazz = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(clazz);
        inherits = clazz.getSuperInheritances();
		assertEquals(1, inherits.size());
		inh = inherits.iterator().next();
		assertSame(clazz, inh.getSubclass());
		assertSame(VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, JavaDictionary.OBJECT_NAME), inh.getSuperclass());
		
		clazz = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "XPrinter");
		assertNotNull(clazz);
        inherits = clazz.getSuperInheritances();
		assertEquals(2, inherits.size()); // superInheritances: Object and IPrinter (in this order)
		for (Inheritance inheritance : inherits) {
			assertSame(clazz, inheritance.getSubclass());
			if (inheritance.getSuperclass().getName().equals("IPrinter")) {
				inh2 = inheritance;
				assertNull(inheritance.getNext());
				assertSame(inheritance,inheritance.getPrevious().getNext());
				assertSame(VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "IPrinter"), inheritance.getSuperclass());
			} else {
				inh = inheritance;
				assertNull(inheritance.getPrevious());
				assertSame(inheritance,inheritance.getNext().getPrevious());
				assertSame(VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, JavaDictionary.OBJECT_NAME), inheritance.getSuperclass());
			}
		}
		assertSame(inh.getNext(), inh2);
		assertSame(inh2.getPrevious(), inh);

        clazz = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "IPrinter");
        assertNotNull(clazz);
        inherits = clazz.getSubInheritances();
        assertEquals(1, inherits.size());

    }

	@Test
	public void testComments() {
		Collection<Comment> cmts = VerveineUtilsForTests.selectElementsOfType(repo, Comment.class);
		assertEquals(18, cmts.size());
		for (Comment c : cmts) {
			int length = 0;
			assertNotNull(c);
			assertNotNull("Empty container for Comment", c.getContainer());
			IndexedFileAnchor anc = (IndexedFileAnchor) c.getSourceAnchor();
			length = (int)anc.getEndPos() - (int)anc.getStartPos();
			// length of comments may vary slightly from one file to the other
			if ( anc.getStartPos().intValue() > 5) { // i.e. not the one at the beginning of the file
				assertTrue("Wrong comment length ("+length+") in:"+anc.getFileName(), (55 <= length) && (length <= 57) );
			}
			else {
				assertTrue("Wrong comment length ("+length+") in:"+anc.getFileName(), (40 <= length) && (length <= 42));
			}
		}
	}

}