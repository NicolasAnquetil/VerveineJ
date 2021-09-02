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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.moosetechnology.model.famixjava.famixjavaentities.Package;
import org.moosetechnology.model.famixjava.famixjavaentities.*;
import org.moosetechnology.model.famixjava.famixtraits.TInheritance;
import org.moosetechnology.model.famixjava.famixtraits.TNamedEntity;

import java.io.File;
import java.lang.Exception;
import java.util.Collection;

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
		new File(DEFAULT_OUTPUT_FILE).delete();  // delete old MSE file
		String[] files = new String[]{
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
				"test_src/LANModel/moose/lan/" + file
		};

		VerveineJParser parser = new VerveineJParser();
		repo = parser.getFamixRepo();
		parser.configure(args);
		parser.parse();

		new File(DEFAULT_OUTPUT_FILE).delete();  // delete old MSE file
		System.gc(); // In Windows free the link to the file. Must be used for incremental parsing tests
		parser.exportModel();  // to create a new one
	}

	@Test(timeout=100)
	public void testEntitiesNumber() {
		int nbClasses = 11 + 14 + 1; // 11+ Object,String,StringBuffer,PrintStream,System,AbstractStringBuilder,FilterOutputStream,OutputStream,Comparable,Serializable,Flushable,Appendable,CharSequence,Closeable, +(java7)AutoCloseable}

		assertEquals(nbClasses, entitiesOfType(org.moosetechnology.model.famixjava.famixjavaentities.Class.class).size());
		assertEquals(3, entitiesOfType(PrimitiveType.class).size());
		assertEquals(6, entitiesOfType(Method.class).size());
		assertEquals(0, entitiesOfType(Attribute.class).size());
		assertEquals(2 + 4, entitiesOfType(Package.class).size());
		assertEquals(6, entitiesOfType(Parameter.class).size());
		assertEquals(0, entitiesOfType(LocalVariable.class).size());
		assertEquals(1, entitiesOfType(AnnotationType.class).size());
		assertEquals(0, entitiesOfType(AnnotationInstance.class).size());    // Override annotations on methods
		assertEquals(1, entitiesOfType(ParameterizableClass.class).size());
	}

	@Test
	public void testAccesses() {
		assertEquals(0,  entitiesOfType( Access.class).size());

		for (Access acc : entitiesOfType( Access.class)) {
			assertNotNull(acc.getVariable());
			assertNotNull(acc.getAccessor());
		}
	}

	@Test
	@Ignore
	public void testInvocations() {
		assertEquals(0,  entitiesOfType( Invocation.class).size());

		for (Invocation invok : entitiesOfType( Invocation.class)) {
			assertNotNull(invok.getCandidates());
			assertNotNull(invok.getSender());
		}
	}

	@Test
	public void testReferences() {
		assertEquals(9,  entitiesOfType( Reference.class).size());  // Node:1 , WorkStation:2 , FileServer:2 , PrintServer:4

		for (Reference ref : entitiesOfType( Reference.class)) {
			assertNotNull(ref.getReferredType());
			assertNotNull(ref.getReferencer());
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

		org.moosetechnology.model.famixjava.famixjavaentities.Class clazz;
		Collection<TInheritance> inherits;
		Inheritance inh, inh2 = null;

		assertEquals(29, entitiesOfType(Inheritance.class).size()); // one less than in VerveineJTest_LanModel because anonymous class is not created

		clazz = detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, "PrintServer");
		assertNotNull(clazz);
		inherits = clazz.getSuperInheritances();
		assertEquals(1, inherits.size());
		inh = (Inheritance) firstElt(inherits);
		assertSame(clazz, inh.getSubclass());
		assertSame(detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, "OutputServer"), inh.getSuperclass());

		clazz = detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, "Node");
		assertNotNull(clazz);
		inherits = clazz.getSuperInheritances();
		assertEquals(1, inherits.size());
		inh = (Inheritance) firstElt(inherits);
		assertSame(clazz, inh.getSubclass());
		assertSame(detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, JavaDictionary.OBJECT_NAME), inh.getSuperclass());

		clazz = detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, "XPrinter");
		assertNotNull(clazz);
		inherits = clazz.getSuperInheritances();
		assertEquals(2, inherits.size()); // superInheritances: Object and IPrinter (in this order)
		for (TInheritance inheritance : inherits) {
			assertSame(clazz, inheritance.getSubclass());
			if (((TNamedEntity) inheritance.getSuperclass()).getName().equals("IPrinter")) {
				inh2 = (Inheritance) inheritance;
				assertNull(inh2.getNext());
				assertSame(inheritance, inh2.getPrevious().getNext());
				assertSame(detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, "IPrinter"), inheritance.getSuperclass());
			} else {
				inh = (Inheritance) inheritance;
				assertNull(inh.getPrevious());
				assertSame(inheritance, inh.getNext().getPrevious());
				assertSame(detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, JavaDictionary.OBJECT_NAME), inheritance.getSuperclass());
			}
		}
		assertSame(inh.getNext(), inh2);
		assertSame(inh2.getPrevious(), inh);

		clazz = detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, "IPrinter");
		assertNotNull(clazz);
		inherits = clazz.getSubInheritances();
		assertEquals(1, inherits.size());

	}

	@Test
	public void testComments() {
		Collection<Comment> cmts = entitiesOfType( Comment.class);
		assertEquals(18, cmts.size());
		for (Comment c : cmts) {
			int length = 0;
			int minLen = 0;
			assertNotNull(c);
			assertNotNull("Empty container for Comment", c.getContainer());
			IndexedFileAnchor anc = (IndexedFileAnchor) c.getSourceAnchor();
			length = (int)anc.getEndPos() - (int)anc.getStartPos();
			// length of comments may vary slightly from one file to the other
			if ( anc.getStartPos().intValue() > 5) { // i.e. not the one at the beginning of the file
				minLen = 55;
				if (isWindows()) {
					minLen += 4;
				}
				assertTrue("Wrong comment length ("+length+") in:"+anc.getFileName(), (minLen <= length) && (length <= minLen + 2) );
			}
			else {
				minLen = 40;
				if (isWindows()) {
					minLen += 3;
				}
				assertTrue("Wrong comment length ("+length+") in:"+anc.getFileName(), (minLen <= length) && (length <= minLen + 2) );
			}
		}
	}

}