/**
 * Copyright (c) 2010 Nicolas Anquetil
 */
package tests.fr.inria.verveine.extractor.java;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import fr.inria.verveine.core.VerveineUtilsForTests;
import fr.inria.verveine.core.gen.famix.Access;
import fr.inria.verveine.core.gen.famix.AnnotationInstance;
import fr.inria.verveine.core.gen.famix.AnnotationType;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.BehaviouralEntity;
import fr.inria.verveine.core.gen.famix.Comment;
import fr.inria.verveine.core.gen.famix.FileAnchor;
import fr.inria.verveine.core.gen.famix.ImplicitVariable;
import fr.inria.verveine.core.gen.famix.Inheritance;
import fr.inria.verveine.core.gen.famix.Invocation;
import fr.inria.verveine.core.gen.famix.LocalVariable;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.Parameter;
import fr.inria.verveine.core.gen.famix.ParameterizableClass;
import fr.inria.verveine.core.gen.famix.PrimitiveType;
import fr.inria.verveine.core.gen.famix.SourceAnchor;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.VerveineJParser;

/**
 * @author Nicolas Anquetil
 * @since May 28, 2010
 *
 */
public class VerveineJTest_LanModel extends VerveineJTest_Basic {

	private static final String A_CLASS_NAME = "--aClassName--";

	public VerveineJTest_LanModel() {
		super(/*system*/true);  // yes please, test that System and members are created correctly
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
//*/
/*
		// or parsing the entire project in one pass ---
		VerveineJParser parser = new VerveineJParser();
		repo = parser.getFamixRepo();
		parser.setOptions(new String[] {"test_src/LANModel/"});
		parser.parse();
		parser.emitMSE(VerveineJParser.OUTPUT_FILE);
//*/
	}

	/**
	 * Parses the file received in parameter independently from any other
	 * The "separate parsing" mechanism should ensure that linkages are appropriately done
	 * @param file -- name of the file to parse
	 */
	private void parseFile(String file) {
		String[] args = new String[] {
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

	@Test
	public void testEntitiesNumber() {
		int nbClasses = (System.getProperty("java.version").startsWith("1.7")) ? 11+15 : 11+14;
		int nbInherit = (System.getProperty("java.version").startsWith("1.7")) ? 9+22 : 9+21;

		assertEquals(nbClasses,VerveineUtilsForTests.selectElementsOfType(repo, fr.inria.verveine.core.gen.famix.Class.class).size()); // 11 + {Object,String,StringBuffer,AbstractStringBuilder,PrintStream,FilterOutputStream,OutputStream,System,Comparable,Serializable,Flushable,Appendable,CharSequence,Closeable, +(java7)AutoCloseable}
		assertEquals(3,    VerveineUtilsForTests.selectElementsOfType(repo, PrimitiveType.class).size());//int,boolean,void
		assertEquals(40+8, VerveineUtilsForTests.selectElementsOfType(repo, Method.class).size());//40+{System.out.println(),System.out.println(...),System.out.print,StringBuffer.append,Object.equals,String.equals,Object.toString,<Initializer>}
		assertEquals(10+1, VerveineUtilsForTests.selectElementsOfType(repo, Attribute.class).size());//10+{System.out}
		assertEquals(2+4,  VerveineUtilsForTests.selectElementsOfType(repo, Namespace.class).size());//2+{moose,java.lang,java.io,java}
		assertEquals(26,   VerveineUtilsForTests.selectElementsOfType(repo, Parameter.class).size());
		assertEquals(54,   VerveineUtilsForTests.selectElementsOfType(repo, Invocation.class).size());
		assertEquals(nbInherit, VerveineUtilsForTests.selectElementsOfType(repo, Inheritance.class).size());//9 internal + {Object=9,String=0,StringBuffer=0,AbstractStringBuilder=0,PrintStream=0,FilterOutputStream=0,OutputStream=1,System=0,Comparable=1,Serializable=2,Flushable=1,Appendable=2,CharSequence=3,Closeable=2, +(java7)AutoCloseable=1}
		assertEquals(25,   VerveineUtilsForTests.selectElementsOfType(repo, Access.class).size());// 16 "internal" attributes + 9 System.out
		assertEquals(0,    VerveineUtilsForTests.selectElementsOfType(repo, LocalVariable.class).size());
		assertEquals(1,    VerveineUtilsForTests.selectElementsOfType(repo, AnnotationType.class).size()); //Override
		assertEquals(2,    VerveineUtilsForTests.selectElementsOfType(repo, AnnotationInstance.class).size()); //PrintServer.output, SingleDestinationAddress.isDestinationFor
		assertEquals(1,    VerveineUtilsForTests.selectElementsOfType(repo, ParameterizableClass.class).size()); //Comparable
		assertEquals(22,   VerveineUtilsForTests.selectElementsOfType(repo, Comment.class).size());  // FileServer=2, IPrinter=1, OutputServer=3, PrintServer=3, AbstractDestinationAddress=1, Node=3, Packet=1, SingleDestinationAddress=3, WorkStation=5
	}

	@Test
	public void testClassProperties() {
		fr.inria.verveine.core.gen.famix.Class nodeClass = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(nodeClass);
		assertEquals("Node", nodeClass.getName());
		assertEquals(11, nodeClass.getMethods().size());
		assertEquals(2, nodeClass.getAttributes().size());
		assertSame(VerveineUtilsForTests.detectElement(repo, Namespace.class, "lan"), nodeClass.getContainer());
		assertFalse(nodeClass.getIsInterface());
		
		fr.inria.verveine.core.gen.famix.Class interfce = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "IPrinter");
		assertNotNull(interfce);
		assertEquals("IPrinter", interfce.getName());
		assertEquals(1, interfce.getMethods().size());
		assertEquals(0, interfce.getAttributes().size());
		assertSame(VerveineUtilsForTests.detectElement(repo, Namespace.class, "server"), interfce.getContainer());
		assertTrue(interfce.getIsInterface());

		fr.inria.verveine.core.gen.famix.Class innerClass = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "XPrinter");
		assertNotNull(innerClass);
		assertEquals("XPrinter", innerClass.getName());
		assertEquals(2, innerClass.getMethods().size());
		assertEquals(1, innerClass.getAttributes().size());
		assertSame(VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "PrintServer"), innerClass.getContainer());
		assertFalse(innerClass.getIsInterface());

		fr.inria.verveine.core.gen.famix.Class anonClass = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "anonymous(IPrinter)");
		assertNotNull(anonClass);
		assertEquals("anonymous(IPrinter)", anonClass.getName());
		assertEquals(1, anonClass.getMethods().size());
		assertEquals(0, anonClass.getAttributes().size());
		assertSame(VerveineUtilsForTests.detectElement(repo,Method.class, "PrintServer"), anonClass.getContainer());
		assertFalse(anonClass.getIsInterface());
	}

	@Test
	public void testNamedEntities() {
		JavaDictionary dico = new JavaDictionary(repo);
		
		assertNotSame(dico.ensureFamixClass(null, A_CLASS_NAME, null, /*persistIt*/true),dico.ensureFamixClass(null, A_CLASS_NAME, null, /*persistIt*/true));
		
		Namespace javaLang = dico.ensureFamixNamespaceJavaLang(null);
		assertEquals( JavaDictionary.OBJECT_PACKAGE_NAME, javaLang.getName());
		assertSame(javaLang, dico.ensureFamixNamespaceJavaLang(null));

		fr.inria.verveine.core.gen.famix.Class obj = dico.ensureFamixClassObject(null);
		assertEquals(JavaDictionary.OBJECT_NAME, obj.getName());
		assertSame(obj, dico.ensureFamixClassObject(null));
		assertEquals(0, obj.getSuperInheritances().size());
		assertSame(javaLang, obj.getContainer());
		
		fr.inria.verveine.core.gen.famix.Class fmx = dico.ensureFamixClassStubOwner();
		assertEquals(JavaDictionary.STUB_METHOD_CONTAINER_NAME, fmx.getName());
		assertSame(fmx, dico.ensureFamixClassStubOwner());
	}

	@Test
	public void testInheritance() {
		fr.inria.verveine.core.gen.famix.Class clazz;
		Collection<Inheritance> superInheritances;
		Inheritance inh, inh2 = null;
		
		clazz = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "PrintServer");
		assertNotNull(clazz);
		superInheritances = clazz.getSuperInheritances();
		assertEquals(1, superInheritances.size());
		inh = superInheritances.iterator().next();
		assertSame(clazz, inh.getSubclass());
		assertSame(VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "OutputServer"), inh.getSuperclass());

		clazz = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(clazz);
		superInheritances = clazz.getSuperInheritances();
		assertEquals(1, superInheritances.size());
		inh = superInheritances.iterator().next();
		assertSame(clazz, inh.getSubclass());
		assertSame(VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, JavaDictionary.OBJECT_NAME), inh.getSuperclass());
		
		clazz = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "XPrinter");
		assertNotNull(clazz);
		superInheritances = clazz.getSuperInheritances();
		assertEquals(2, superInheritances.size()); // superInheritances: Object and IPrinter (in this order)
		for (Inheritance inheritance : superInheritances) {
			assertSame(clazz, inheritance.getSubclass());
			if (inheritance.getSuperclass().getName().equals("IPrinter")) {
				inh2 = inheritance;
				assertNull(inheritance.getNext());
				assertSame(inheritance,inheritance.getPrevious().getNext());
				assertSame(VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "IPrinter"), inheritance.getSuperclass());
			} else {
				inh = inheritance;
				assertNull(inheritance.getPrevious());
				assertSame(inheritance,inheritance.getNext().getPrevious());
				assertSame(VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, JavaDictionary.OBJECT_NAME), inheritance.getSuperclass());
			}
		}
		assertSame(inh.getNext(), inh2);
		assertSame(inh2.getPrevious(), inh);
	}

	@Test
	public void testMethodProperties() {
		Method mweb = VerveineUtilsForTests.detectElement(repo,Method.class, "methodWithEmptyBody");
		assertNotNull(mweb);
		assertEquals("methodWithEmptyBody", mweb.getName());
		assertEquals("methodWithEmptyBody()", mweb.getSignature());
		assertSame(VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node"), mweb.getParentType());
		assertSame(VerveineUtilsForTests.detectElement(repo,PrimitiveType.class, "void"), mweb.getDeclaredType());

		Method em = VerveineUtilsForTests.detectElement(repo,Method.class, "equalsMultiple");
		assertNotNull(em);
		assertEquals("equalsMultiple", em.getName());
		assertEquals("equalsMultiple(AbstractDestinationAddress)", em.getSignature());
		assertSame(VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "SingleDestinationAddress"), em.getParentType());
		assertSame(VerveineUtilsForTests.detectElement(repo,PrimitiveType.class, "boolean"), em.getDeclaredType());

		fr.inria.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "FileServer");
		assertNotNull(clazz);
		Method n = null;
		for (Method m : clazz.getMethods()) {
			if (m.getName().equals("name")) {
				n = m;
				break;
			}
		}
		assertNotNull(n);
		assertEquals("name", n.getName());
		assertEquals("name()", n.getSignature());
		assertSame(VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "FileServer"), n.getParentType());
		assertSame(VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String"), n.getDeclaredType());
		
		//constructors
		for (Method m : VerveineUtilsForTests.selectElementsOfType(repo, Method.class)) {
			if (m.getName().equals(m.getParentType().getName())) {
				assertEquals("constructor", m.getKind());
			}
			else {
				assertTrue((m.getKind() == null) || (! m.getKind().equals("constructor")));
			}
		}

	}

	@Test
	public void testFieldType() {
		fr.inria.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(clazz);
		Collection<Attribute> l_atts = clazz.getAttributes();
		assertEquals(2, l_atts.size());
		for (Attribute a : l_atts) {
			if (a.getName().equals("nextNode")) {
				assertSame(clazz, a.getParentType());
				assertSame(clazz, a.getDeclaredType());
			}
			else if (a.getName().equals("name")) {
				assertSame(clazz, a.getParentType());
				assertSame(VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String"), a.getDeclaredType());
			}
		}
	}

	@Test
	public void testStubs() {
		int nbTypes = (System.getProperty("java.version").startsWith("1.7")) ? 10 : 9;
		String javaLangName = JavaDictionary.OBJECT_PACKAGE_NAME.substring(JavaDictionary.OBJECT_PACKAGE_NAME.lastIndexOf('.')+1);
		Namespace ns = VerveineUtilsForTests.detectElement(repo,Namespace.class, javaLangName);
		assertNotNull(ns);
		assertEquals(nbTypes, ns.getTypes().size());  // Object,String,StringBuffer,AbstractStringBuilder,System,Comparable,Comparable<String>,Appendable,CharSequence, +(java7)AutoCloseable
		assertTrue(ns.getIsStub());

		fr.inria.verveine.core.gen.famix.Class obj = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, JavaDictionary.OBJECT_NAME);
		assertNotNull(obj);
		assertTrue(obj.getIsStub());
		assertSame(ns, obj.getContainer());
		
		fr.inria.verveine.core.gen.famix.Class str = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String");
		assertNotNull(str);
		assertTrue(str.getIsStub());
		assertSame(ns, str.getContainer());
		
		fr.inria.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(clazz);
		assertFalse(clazz.getIsStub());
		
		/* [].length not used in the default test case 
		Attribute att = TestVerveineUtils.detectElement(repo, Attribute.class, "length");
		assertNotNull(att);
		clazz = (Class) att.getParentType();
		assertNotNull(clazz);
		assertEquals(JavaDictionary.ARRAYS_NAME, clazz.getName());*/
	}

	@Test
	public void testParameter() {
		fr.inria.verveine.core.gen.famix.Class nodeClass = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(nodeClass);
		for (Method mNode : nodeClass.getMethods()) {
			if ( (mNode.getName().equals("Node")) ||
				 (mNode.getName().equals("methodWithEmptyBody")) ||
				 (mNode.getName().equals("canOriginate")) ||
				 (mNode.getName().equals("canOutput")) ) {
				assertEquals("Wrong number of parameter for method Node."+mNode.getName()+"()", 0, mNode.getParameters().size());
			}
			else if ( (mNode.getName().equals("name")) ||
					  (mNode.getName().equals("nextNode")) ) {
				assertTrue("Wrong number of parameter for method Node."+mNode.getName()+"()",  (mNode.getParameters().size()==0) || (mNode.getParameters().size()==1));
			}
			else if ( (mNode.getName().equals("accept")) ||
					 (mNode.getName().equals("send")) ||
					 (mNode.getName().equals("printOn")) ) {
				assertEquals("Wrong number of parameter for method Node."+mNode.getName()+"()", 1, mNode.getParameters().size());
			}
		}
		fr.inria.verveine.core.gen.famix.Class iprintClass = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "IPrinter");
		assertNotNull(iprintClass);
		Method mPrint = iprintClass.getMethods().iterator().next();
		assertEquals(2, mPrint.getParameters().size());
		for (Parameter p : mPrint.getParameters()) {
			assertSame(mPrint, p.getParentBehaviouralEntity());
			assertTrue(p.getName().equals("contents") || p.getName().equals("rv"));
			if (p.getName().equals("contents")) {
				assertSame(VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String"), p.getDeclaredType());
			}
			if (p.getName().equals("rv")) {
				assertSame(VerveineUtilsForTests.detectElement(repo,PrimitiveType.class, "boolean"), p.getDeclaredType());
			}
		}
	}

	@Test
	public void testImplicitVar() {
		boolean testRan = false;
		fr.inria.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "SingleDestinationAddress");
		assertNotNull(clazz);
		for (Method m : clazz.getMethods()) {
			if (m.getName().equals("isDestinationFor" )) {
				testRan = true;
				assertEquals(1, m.getOutgoingInvocations().size());
				Invocation invok = m.getOutgoingInvocations().iterator().next();
				assertEquals(ImplicitVariable.class, invok.getReceiver().getClass());
				ImplicitVariable iv = (ImplicitVariable) invok.getReceiver();
				assertEquals("self", iv.getName());
				assertSame(m, iv.getBelongsTo());
			}
		}
		assertTrue("Test for SELF did not run, cause: SingleDestinationAddress.isDestinationFor() method not found", testRan);

		testRan = false;
		clazz = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "WorkStation");
		assertNotNull(clazz);
		for (Method m : clazz.getMethods()) {
			if (m.getName().equals("name" )) {
				testRan = true;
				assertEquals(1, m.getOutgoingInvocations().size());
				Invocation invok = m.getOutgoingInvocations().iterator().next();
				assertEquals(ImplicitVariable.class, invok.getReceiver().getClass());
				ImplicitVariable iv = (ImplicitVariable) invok.getReceiver();
				assertEquals("super", iv.getName());
				assertSame(m, iv.getBelongsTo());
			}
		}
		assertTrue("Test for SELF did not run, cause: SingleDestinationAddress.isDestinationFor() method not found", testRan);

	}

	@Test
	public void testInvocation() {
		fr.inria.verveine.core.gen.famix.Class nodeClass = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(nodeClass);
		for (Method mNode : nodeClass.getMethods()) {
			if ( (mNode.getName().equals("name")) ||
				 (mNode.getName().equals("nextNode")) ||
				 (mNode.getName().equals("methodWithEmptyBody")) ||
				 (mNode.getName().equals("canOutput")) ||
				 (mNode.getName().equals("canOriginate")) ) {
				assertEquals("Wrong number of outgoing invocation for Node."+mNode.getSignature(), 0, mNode.getOutgoingInvocations().size());
			}
			else if ( (mNode.getName().equals("Node")) ||
					  (mNode.getName().equals("accept")) ) {
				assertEquals("Wrong number of outgoing invocation for Node."+mNode.getSignature(), 1, mNode.getOutgoingInvocations().size());
			}
			else if (mNode.getName().equals("send"))  {
				assertEquals("Wrong number of outgoing invocation for Node.send()", 6, mNode.getOutgoingInvocations().size());
			}
			else if (mNode.getName().equals("printOn")) {
				assertEquals("Wrong number of outgoing invocation for Node.printOn()", 8, mNode.getOutgoingInvocations().size());
			}
		}
		
		fr.inria.verveine.core.gen.famix.Class sdaClass = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "SingleDestinationAddress");
		assertNotNull(sdaClass);
		for (Method mSDA : sdaClass.getMethods()) {
			for (Invocation inv : mSDA.getOutgoingInvocations()) {
				assertEquals(1, inv.getCandidates().size());

				BehaviouralEntity invoked = inv.getCandidates().iterator().next();

				assertTrue( "Unexpected invoked method signature: "+invoked.getSignature(),
						invoked.getSignature().equals("equalsSingle(String)") || invoked.getSignature().equals("id()") || invoked.getSignature().equals("equals(Object)"));
				if (invoked.getSignature().equals("equalsSingle (String)")) {
					assertSame(sdaClass, ((Method)inv.getSender()).getParentType());
					assertEquals("self", inv.getReceiver().getName());
					assertSame(VerveineUtilsForTests.detectElement(repo,Method.class, "equalsSingle"), inv.getCandidates().iterator().next());
				}
				else if (invoked.getSignature().equals("id ()")) {
					assertSame(VerveineUtilsForTests.detectElement(repo,Method.class, "equalsSingle"), inv.getSender());
					assertEquals("self", inv.getReceiver().getName());
					assertSame(sdaClass, ((Method)inv.getCandidates().iterator().next()).getParentType());
				}
				else if (invoked.getSignature().equals("equals (Object)")) {
					assertSame(VerveineUtilsForTests.detectElement(repo,Method.class, "equalsSingle"), inv.getSender());
					assertEquals(null, inv.getReceiver());
					assertSame(VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String"), ((Method)inv.getCandidates().iterator().next()).getParentType());
				}
			}
		}
		
		// test that the chain (next/previous) of invocations is correct
		for (Method mNode : nodeClass.getMethods()) {
			if (mNode.getName().equals("accept")) {
				Invocation invok = mNode.getOutgoingInvocations().iterator().next();
				assertNull(invok.getPrevious());
				assertNull(invok.getNext());
			}
			else if (mNode.getName().equals("send"))  {
				int nbNull = 0;
				for (Invocation invok : mNode.getOutgoingInvocations()) {
					Invocation previous = (Invocation) invok.getPrevious();
					if (previous == null) {
						nbNull++;
					}
					else {
						assertSame(mNode, previous.getSender());
					}					
				}
				assertEquals(1, nbNull);
			}
		}

	}

	@Test
	public void testAccess() {
		Attribute att;
		BehaviouralEntity accessor;
		
		att = VerveineUtilsForTests.detectElement(repo, Attribute.class, "name");
		assertNotNull(att);
		assertSame(2, att.getIncomingAccesses().size());
		accessor = att.getIncomingAccesses().iterator().next().getAccessor();
		assertSame(Method.class, accessor.getClass());
		assertEquals("name", accessor.getName());
		assertEquals("Node", ((Method)accessor).getParentType().getName());

		att = VerveineUtilsForTests.detectElement(repo, Attribute.class, "serverType");
		assertNotNull(att);
		assertSame(1, att.getIncomingAccesses().size());
		accessor = att.getIncomingAccesses().iterator().next().getAccessor();
		assertSame(Method.class, accessor.getClass());
		assertEquals("setServerType", accessor.getName());
		assertEquals("FileServer", ((Method)accessor).getParentType().getName());

		// finds method PrintServer.output()
		Method output = null;
		for (Method m : VerveineUtilsForTests.listElements(repo, Method.class, "output")) {
			if (m.getParentType().getName().equals("PrintServer")) {
				output = m;
				break;
			}
		}
		assertNotNull(output);
		assertEquals(4, output.getAccesses().size());
		for (Access acc : output.getAccesses()) {
			assertTrue("Unexpected field accessed: "+acc.getVariable().getName(),
					acc.getVariable().getName().equals("thePacket") || acc.getVariable().getName().equals("out") || acc.getVariable().getName().equals("printer"));
			assertEquals(output, acc.getAccessor());
		}
	}

	@Test
	public void testSourceAnchors() {
		SourceAnchor anc = null;
		
		fr.inria.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "XPrinter");
		assertNotNull(clazz);

		anc = clazz.getSourceAnchor();
		assertNotNull(anc);
		assertSame(clazz, anc.getElement());
		assertSame(FileAnchor.class, anc.getClass());
		assertEquals("test_src/LANModel/moose/lan/server/PrintServer.java", ((FileAnchor)anc).getFileName());
		assertEquals(17, ((FileAnchor)anc).getStartLine());
		assertEquals(31, ((FileAnchor)anc).getEndLine());

		clazz = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(clazz);

		anc = clazz.getSourceAnchor();
		assertNotNull(anc);
		assertSame(clazz, anc.getElement());
		assertSame(FileAnchor.class, anc.getClass());
		assertTrue("Wrong file source for class Node", ((FileAnchor)anc).getFileName().equals("test_src/LANModel/moose/lan/Node.java"));
		assertEquals(6, ((FileAnchor)anc).getStartLine());
		assertEquals(70, ((FileAnchor)anc).getEndLine());
		
		Method meth = VerveineUtilsForTests.detectElement(repo, Method.class, "equalsMultiple");
		assertNotNull(meth);

		anc = meth.getSourceAnchor();
		assertNotNull(anc);
		assertSame(meth, anc.getElement());
		assertSame(FileAnchor.class, anc.getClass());
		assertTrue("Wrong file source for method SingleDestinationAddress.equalsMultiple()", ((FileAnchor)anc).getFileName().equals("test_src/LANModel/moose/lan/SingleDestinationAddress.java"));
		assertEquals(39, ((FileAnchor)anc).getStartLine());
		assertEquals(41, ((FileAnchor)anc).getEndLine());
		
		Attribute att = VerveineUtilsForTests.detectElement(repo, Attribute.class, "originator");
		assertNotNull(meth);

		anc = att.getSourceAnchor();
		assertNotNull(anc);
		assertSame(att, anc.getElement());
		assertSame(FileAnchor.class, anc.getClass());
		assertTrue("Wrong file source for field Packet.originator", ((FileAnchor)anc).getFileName().equals("test_src/LANModel/moose/lan/Packet.java"));
		assertEquals(15, ((FileAnchor)anc).getStartLine());
		assertEquals(15, ((FileAnchor)anc).getEndLine());
		
	}

	@Test
	public void testModifiers() {
		fr.inria.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "OutputServer");
		assertNotNull(clazz);
		assertFalse(clazz.getIsInterface());
		assertTrue(clazz.getIsAbstract());
		assertTrue(clazz.getModifiers().contains("abstract"));
		assertTrue(clazz.getModifiers().contains("public"));
		assertFalse(clazz.getModifiers().contains("private"));
		assertFalse(clazz.getModifiers().contains("protected"));
		assertFalse(clazz.getModifiers().contains("final"));
		
		assertEquals(4, clazz.getMethods().size());
		for (Method m : clazz.getMethods()) {
			if (m.getName().equals(JavaDictionary.INIT_BLOCK_NAME)) {
				assertFalse(m.getModifiers().contains("public"));
			}
			else {
				assertTrue(m.getModifiers().contains("public"));
			}
			assertFalse(m.getModifiers().contains("private"));
			assertFalse(m.getModifiers().contains("protected"));
			assertFalse(m.getModifiers().contains("final"));
			if (m.getName().equals("output")) {
				assertTrue(m.getModifiers().contains("abstract"));
			}
			else {
				assertFalse(m.getModifiers().contains("abstract"));
			}
		}
		
		assertEquals(1, clazz.getAttributes().size());
		Attribute a = clazz.getAttributes().iterator().next();
		assertFalse(a.getModifiers().contains("public"));
		assertFalse(a.getModifiers().contains("private"));
		assertTrue(a.getModifiers().contains("protected"));
		assertFalse(a.getModifiers().contains("final"));
	}

	@Test
	public void testComment() {
		// testing javadoc
		fr.inria.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "SingleDestinationAddress");
		assertNotNull(clazz);
		Collection<Comment> cmts = clazz.getComments();
		assertEquals(1, cmts.size());
		SourceAnchor anc = cmts.iterator().next().getSourceAnchor();
		assertEquals(6, ((FileAnchor)anc).getStartLine());
		assertEquals(10, ((FileAnchor)anc).getEndLine());

		Method meth = VerveineUtilsForTests.detectElement(repo, Method.class, "equalsSingle");
		assertNotNull(meth);
		cmts = meth.getComments();
		assertEquals(1, cmts.size());
		anc = cmts.iterator().next().getSourceAnchor();
		assertEquals(31, ((FileAnchor)anc).getStartLine());
		assertEquals(34, ((FileAnchor)anc).getEndLine());

		// testing the non javadoc comments (those that are treated)
		clazz = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "WorkStation");
		assertNotNull(clazz);
		for (Method m : clazz.getMethods()) {
			anc = m.getSourceAnchor();
			cmts = m.getComments();
			if (m.getName().equals("canOriginate")) {
				assertEquals(0, cmts.size());
			}
			else {
				assertEquals(1, cmts.size());
				assertEquals("for method: WorkStation." + m.getName(),
							 ((FileAnchor)anc).getStartLine().intValue() - 1,
							 ((FileAnchor)cmts.iterator().next().getSourceAnchor()).getEndLine().intValue());
			}
		}

		Attribute a = clazz.getAttributes().iterator().next();
		assertEquals("type", a.getName());
		cmts = a.getComments();
		assertEquals(1, cmts.size());
		anc = (FileAnchor)cmts.iterator().next().getSourceAnchor();
		assertEquals(13, ((FileAnchor)anc).getStartLine().intValue());
	}
	
	@Test
	public void testMetric() {
		for (Method m : VerveineUtilsForTests.listElements(repo, Method.class, "accept")) {
			assertNotNull(m);
			fr.inria.verveine.core.gen.famix.Class owner = (fr.inria.verveine.core.gen.famix.Class) m.getParentType();
			assertNotNull(owner);
			if (owner.getName().equals("OutputServer")) {
			assertEquals(2, m.getCyclomaticComplexity());
				assertEquals(6, m.getNumberOfStatements());
			}
			else if (owner.getName().equals("Node")) {
				assertEquals(1, m.getCyclomaticComplexity());
				assertEquals(1, m.getNumberOfStatements());
			}
			else if (owner.getName().equals("WorkStation")) {
				assertEquals(2, m.getCyclomaticComplexity());
				assertEquals(7, m.getNumberOfStatements());
			}
		}		
	}
	
	@Test
	public void testAnnotation() {
		fr.inria.verveine.core.gen.famix.Class clazz;
		Collection<AnnotationInstance> annInstances;
		
		AnnotationType annType = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.AnnotationType.class, "Override");
		assertNotNull(annType);
		assertEquals("Override", annType.getName());
		
		clazz = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "PrintServer");
		assertNotNull(clazz);
		for (Method method : clazz.getMethods()) {
			annInstances = method.getAnnotationInstances();
			if (method.getName().equals("output")) {
				assertEquals(1, annInstances.size());
				AnnotationInstance annInstance = annInstances.iterator().next();
				assertEquals("Override", annInstance.getAnnotationType().getName());
				assertSame(annType, annInstance.getAnnotationType());
				assertSame(method, annInstance.getAnnotatedEntity());
			} else {
				assertEquals(0, annInstances.size());
			}
		}
		clazz = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "SingleDestinationAddress");
		assertNotNull(clazz);
		assertEquals(5, clazz.getMethods().size());
		for (Method method : clazz.getMethods()) {
			annInstances = method.getAnnotationInstances();
			if (method.getName().equals("isDestinationFor")) {
				assertEquals(1, annInstances.size());
				AnnotationInstance annInstance = annInstances.iterator().next();
				assertEquals("Override", annInstance.getAnnotationType().getName());
				assertSame(annType, annInstance.getAnnotationType());
				assertSame(method, annInstance.getAnnotatedEntity());
			} else {
				assertEquals(0, annInstances.size());
			}
		}	
	}

}