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
import org.moosetechnology.model.famixjava.famixtraits.TImplementation;
import org.moosetechnology.model.famixjava.famixtraits.TInheritance;
import org.moosetechnology.model.famixjava.famixtraits.TNamedEntity;

import fr.inria.verveine.extractor.java.VerveineJTest_Basic.ImplementedInterfaces;

import java.io.File;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	@Test
	public void testEntitiesNumber() {
		// find all classes to count them
		List<java.lang.Class<?>> recursiveAPISuperClasses = new ArrayList<>();
		recursiveAPISuperClasses.addAll( allJavaSuperClasses( java.lang.String.class));
		recursiveAPISuperClasses.addAll( allJavaSuperClasses( java.lang.StringBuffer.class));
		recursiveAPISuperClasses.addAll( allJavaSuperClasses( java.io.PrintStream.class));
		recursiveAPISuperClasses.addAll( allJavaSuperClasses( java.lang.System.class));
		recursiveAPISuperClasses.addAll( allJavaSuperClasses( java.io.FilterOutputStream.class));
		recursiveAPISuperClasses.addAll( allJavaSuperClasses( java.io.OutputStream.class));

		Set<java.lang.Class<?>> setOfSuperClasses = new HashSet<>();
		setOfSuperClasses.addAll(recursiveAPISuperClasses);
		assertEquals(setOfSuperClasses.size() + 10,  // FileServer, Node, AbstractDestinationAddress, WorkStation, XPrinter, Packet, PrintServer, SingleDestinationAddress, OutputServer, _Anonymous(IPrinter)
				entitiesOfType(org.moosetechnology.model.famixjava.famixjavaentities.Class.class).size());

		// find all interfaces to count them
		List<ImplementedInterfaces> recursiveAPIInterfaces = new ArrayList<>();
		recursiveAPIInterfaces.add( allImplementedJavaInterfaces( java.lang.String.class));
		recursiveAPIInterfaces.add( allImplementedJavaInterfaces( java.lang.StringBuffer.class));
		recursiveAPIInterfaces.add( allImplementedJavaInterfaces( java.io.PrintStream.class));
		recursiveAPIInterfaces.add( allImplementedJavaInterfaces( java.lang.System.class));
		recursiveAPIInterfaces.add( allImplementedJavaInterfaces( java.io.FilterOutputStream.class));
		recursiveAPIInterfaces.add( allImplementedJavaInterfaces( java.io.OutputStream.class));

		Set<java.lang.Class<?>> setOfInterfaces = new HashSet<>();
		for (ImplementedInterfaces each : recursiveAPIInterfaces) {
			setOfInterfaces.addAll( each.flatten());
		}
		assertEquals(setOfInterfaces.size() - 6 + 1,  // discount the 6 classes above and add IPrinter
				entitiesOfType(Interface.class).size());

		assertEquals(1, entitiesOfType(ParameterizableInterface.class).size());//IPrinter
		assertEquals(3, entitiesOfType(PrimitiveType.class).size());
		assertEquals(6, entitiesOfType(Method.class).size());
		assertEquals(0, entitiesOfType(Attribute.class).size());
		assertEquals(2 + 4 + 1, entitiesOfType(Package.class).size()); // +1 new package named constant (java17?)
		assertEquals(6, entitiesOfType(Parameter.class).size());
		assertEquals(0, entitiesOfType(LocalVariable.class).size());
		assertEquals(1, entitiesOfType(AnnotationType.class).size());
		assertEquals(0, entitiesOfType(AnnotationInstance.class).size());    // Override annotations on methods
		assertEquals(0, entitiesOfType(ParameterizableClass.class).size());
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
		Collection<TImplementation> implementations;
		Inheritance inh = null;
		
		int nbInherit = 9;  // all classes of the project (note: _Anonymous(IPrinter) is not created) have inheritance
		Set<java.lang.Class<?>> recursiveAPISuperClasses = new HashSet<>();
		recursiveAPISuperClasses.addAll( allJavaSuperClasses( java.lang.String.class));
		recursiveAPISuperClasses.addAll( allJavaSuperClasses( java.lang.StringBuffer.class));
		recursiveAPISuperClasses.addAll( allJavaSuperClasses( java.io.PrintStream.class));
		recursiveAPISuperClasses.addAll( allJavaSuperClasses( java.lang.System.class));
		recursiveAPISuperClasses.addAll( allJavaSuperClasses( java.io.FilterOutputStream.class));
		recursiveAPISuperClasses.addAll( allJavaSuperClasses( java.io.OutputStream.class));
		nbInherit += recursiveAPISuperClasses.size() - 1; // Object has no inheritance

		nbInherit +=  allImplementedJavaInterfaces( java.lang.String.class).countInterfacesSubtyped();
		nbInherit +=   allImplementedJavaInterfaces( java.lang.StringBuffer.class).countInterfacesSubtyped();
		nbInherit +=   allImplementedJavaInterfaces( java.io.PrintStream.class).countInterfacesSubtyped();
		nbInherit +=   allImplementedJavaInterfaces( java.lang.System.class).countInterfacesSubtyped();
		nbInherit +=   allImplementedJavaInterfaces( java.io.FilterOutputStream.class).countInterfacesSubtyped();
		nbInherit +=   allImplementedJavaInterfaces( java.io.OutputStream.class).countInterfacesSubtyped();
		assertEquals(nbInherit, entitiesOfType(Inheritance.class).size()); // one less than in VerveineJTest_LanModel because anonymous class is not created

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
		assertEquals(1, inherits.size()); // superInheritances: Object (in this order)
		implementations = clazz.getInterfaceImplementations();
		assertEquals(1, implementations.size()); // IPrinter

			/**
			 * Order shold be extends Object and then implements IPrinter
			 */


		Implementation implem = (Implementation) implementations.iterator().next();
		assertSame(clazz, implem.getImplementingClass());
		assertNull(implem.getNext());
		assertSame(implem, implem.getPrevious().getNext());
		assertSame(detectFamixElement(Interface.class, "IPrinter"), implem.getMyInterface());

		inh = (Inheritance) inherits.iterator().next();;
		assertSame(clazz, inh.getSubclass());
		assertNull(inh.getPrevious());
		assertSame(inh, inh.getNext().getPrevious());
		assertSame(detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, JavaDictionary.OBJECT_NAME), inh.getSuperclass());

		assertSame(inh.getNext(), implem);
		assertSame(implem.getPrevious(), inh);


		Interface interfacePrinter = detectFamixElement(Interface.class, "IPrinter");
		assertNotNull(interfacePrinter);
		inherits = interfacePrinter.getSubInheritances();
		assertEquals(0, inherits.size());
		Collection<TImplementation> impls = interfacePrinter.getImplementations();
		assertEquals(1, impls.size());

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