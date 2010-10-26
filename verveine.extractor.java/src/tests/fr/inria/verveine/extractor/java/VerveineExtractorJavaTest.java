/**
 * Copyright (c) 2010 Simon Denier
 */
package tests.fr.inria.verveine.extractor.java;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import test.fr.inria.verveine.core.TestVerveineUtils;

import ch.akuhn.fame.Repository;


import fr.inria.verveine.core.gen.famix.Access;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.BehaviouralEntity;
import fr.inria.verveine.core.gen.famix.Inheritance;
import fr.inria.verveine.core.gen.famix.Invocation;
import fr.inria.verveine.core.gen.famix.LocalVariable;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.Parameter;

import fr.inria.verveine.extractor.java.BatchParser;
import fr.inria.verveine.extractor.java.JavaDictionary;

/**
 * @author Simon Denier
 * @since May 28, 2010
 *
 */
public class VerveineExtractorJavaTest {

	private static final String A_CLASS_NAME = "--aClassName--";

	private Repository repo;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		BatchParser parser = new BatchParser(new PrintWriter(System.out), new PrintWriter(System.err), true/*systemExit*/, null/*options*/, null/*progress*/);
		parser.systemExitWhenFinished = false;
		parser.compile(new String[] {"test_src/LANModel"});
		repo = parser.getFamixRepo();
	}

	@Test
	public void testEntitiesNumber() {
		assertEquals(11+11, TestVerveineUtils.selectElementsOfType(repo,fr.inria.verveine.core.gen.famix.Class.class).size()); // 11 + {int,boolean,void,Object,String,StringBuffer,AbstractStringBuilder,PrintStream,FilterOutputStream,OutputStream,System}
		assertEquals(40+7, TestVerveineUtils.selectElementsOfType(repo,Method.class).size());//40+{System.out.println(),System.out.println(...),System.out.print,StringBuffer.append,Object.equals,String.equals,Object.toString}
		assertEquals(10+1, TestVerveineUtils.selectElementsOfType(repo,Attribute.class).size());//10+{System.out}
		assertEquals(2+5, TestVerveineUtils.selectElementsOfType(repo,Namespace.class).size());//2+{moose,<primitive>,java.lang,java.io,java}
		assertEquals(26, TestVerveineUtils.selectElementsOfType(repo,Parameter.class).size());
		assertEquals(54, TestVerveineUtils.selectElementsOfType(repo,Invocation.class).size());//actually 54, missing 2 !!!!
		assertEquals(5+12, TestVerveineUtils.selectElementsOfType(repo,Inheritance.class).size());//5 internal + 12 from imported packages/classes
		assertEquals(25, TestVerveineUtils.selectElementsOfType(repo,Access.class).size());// 16 "internal" attributes + 9 System.out
		assertEquals(0, TestVerveineUtils.selectElementsOfType(repo,LocalVariable.class).size());
	}

	@Test
	public void testClassProperties() {
		fr.inria.verveine.core.gen.famix.Class nodeClass = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(nodeClass);
		assertEquals("Node", nodeClass.getName());
		assertEquals(11, nodeClass.getMethods().size());
		assertEquals(2, nodeClass.getAttributes().size());
		assertSame(TestVerveineUtils.detectElement(repo,Namespace.class, "moose.lan"), nodeClass.getContainer());
		
		fr.inria.verveine.core.gen.famix.Class innerClass = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "XPrinter");
		assertNotNull(innerClass);
		assertEquals("XPrinter", innerClass.getName());
		assertEquals(2, innerClass.getMethods().size());
		assertEquals(1, innerClass.getAttributes().size());
		assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "PrintServer"), innerClass.getContainer());
		
		fr.inria.verveine.core.gen.famix.Class anonClass = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "anonymous(IPrinter)");
		assertNotNull(anonClass);
		assertEquals("anonymous(IPrinter)", anonClass.getName());
		assertEquals(1, anonClass.getMethods().size());
		assertEquals(0, anonClass.getAttributes().size());
		assertSame(TestVerveineUtils.detectElement(repo,Method.class, "PrintServer"), anonClass.getContainer());
	}

	@Test
	public void testNamedEntities() {
		JavaDictionary dico = new JavaDictionary(repo);
		
		assertNotSame(dico.ensureFamixClass(A_CLASS_NAME),dico.ensureFamixClass(A_CLASS_NAME));
		
		Namespace javaLang = dico.ensureFamixNamespaceJavaLang(null);
		assertEquals(JavaDictionary.OBJECT_PACKAGE_NAME, javaLang.getName());
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
		Inheritance inh;
		
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
	}

	@Test
	public void testMethodProperties() {
		Method mweb = TestVerveineUtils.detectElement(repo,Method.class, "methodWithEmptyBody");
		assertNotNull(mweb);
		assertEquals("methodWithEmptyBody", mweb.getName());
		assertEquals("methodWithEmptyBody ()", mweb.getSignature());
		assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node"), mweb.getParentType());
		assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "void"), mweb.getDeclaredType());

		Method em = TestVerveineUtils.detectElement(repo,Method.class, "equalsMultiple");
		assertNotNull(em);
		assertEquals("equalsMultiple", em.getName());
		assertEquals("equalsMultiple (AbstractDestinationAddress)", em.getSignature());
		assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "SingleDestinationAddress"), em.getParentType());
		assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "boolean"), em.getDeclaredType());

		fr.inria.verveine.core.gen.famix.Class clazz = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "FileServer");
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
		assertEquals("name ()", n.getSignature());
		assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "FileServer"), n.getParentType());
		assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String"), n.getDeclaredType());
	}

	@Test
	public void testFieldType() {
		fr.inria.verveine.core.gen.famix.Class clazz = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
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
				assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String"), a.getDeclaredType());
			}
		}
	}

	@Test
	public void testStubs() {
		Namespace ns = TestVerveineUtils.detectElement(repo,Namespace.class, JavaDictionary.OBJECT_PACKAGE_NAME);
		assertNotNull(ns);
		assertEquals(5, ns.getTypes().size());  // Object,String,StringBuffer,AbstractStringBuilder,System
		
		fr.inria.verveine.core.gen.famix.Class obj = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, JavaDictionary.OBJECT_NAME);
		assertNotNull(obj);
		assertFalse(ns.getIsStub());
		assertSame(ns, obj.getContainer());
		
		fr.inria.verveine.core.gen.famix.Class str = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String");
		assertNotNull(str);
		assertFalse(str.getIsStub());
		assertSame(ns, str.getContainer());
	}

	@Test
	public void testParameter() {
		fr.inria.verveine.core.gen.famix.Class nodeClass = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
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
		fr.inria.verveine.core.gen.famix.Class iprintClass = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "IPrinter");
		assertNotNull(iprintClass);
		Method mPrint = iprintClass.getMethods().iterator().next();
		assertEquals(2, mPrint.getParameters().size());
		for (Parameter p : mPrint.getParameters()) {
			assertSame(mPrint, p.getParentBehaviouralEntity());
			assertTrue(p.getName().equals("contents") || p.getName().equals("rv"));
			if (p.getName().equals("contents")) {
				assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String"), p.getDeclaredType());
			}
			if (p.getName().equals("rv")) {
				assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "boolean"), p.getDeclaredType());
			}
		}
	}

	@Test
	public void testInvocation() {
		fr.inria.verveine.core.gen.famix.Class nodeClass = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(nodeClass);
		for (Method mNode : nodeClass.getMethods()) {
			if ( (mNode.getName().equals("name")) ||
				 (mNode.getName().equals("nextNode")) ||
				 (mNode.getName().equals("methodWithEmptyBody")) ||
				 (mNode.getName().equals("canOutput")) ||
				 (mNode.getName().equals("canOriginate")) ) {
				assertEquals("Wrong number of outgoing invocation for method Node."+mNode.getName()+"()", 0, mNode.getOutgoingInvocations().size());
			}
			else if ( (mNode.getName().equals("Node")) ||
					  (mNode.getName().equals("accept")) ) {
				assertEquals("Wrong number of outgoing invocation for method Node."+mNode.getName()+"()", 1, mNode.getOutgoingInvocations().size());
			}
			else if (mNode.getName().equals("send"))  {
				assertEquals("Wrong number of outgoing invocation for method Node.send()", 6, mNode.getOutgoingInvocations().size());
			}
			else if (mNode.getName().equals("printOn")) {
				assertEquals("Wrong number of outgoing invocation for method Node.printOn()", 8, mNode.getOutgoingInvocations().size());
			}
		}
		
		fr.inria.verveine.core.gen.famix.Class sdaClass = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "SingleDestinationAddress");
		assertNotNull(sdaClass);
		for (Method mSDA : sdaClass.getMethods()) {
			for (Invocation inv : mSDA.getOutgoingInvocations()) {
				assertTrue( "Unexpected method signature: "+inv.getSignature(),
							inv.getSignature().equals("equalsSingle (String)") || inv.getSignature().equals("id ()") || inv.getSignature().equals("equals (Object)"));
				if (inv.getSignature().equals("equalsSingle (String)")) {
					assertSame(sdaClass, ((Method)inv.getSender()).getParentType());
					assertEquals("self", inv.getReceiver().getName());
					assertEquals(1, inv.getCandidates().size());
					assertSame(TestVerveineUtils.detectElement(repo,Method.class, "equalsSingle"), inv.getCandidates().iterator().next());
				}
				else if (inv.getSignature().equals("id ()")) {
					assertSame(TestVerveineUtils.detectElement(repo,Method.class, "equalsSingle"), inv.getSender());
					assertEquals("self", inv.getReceiver().getName());
					assertEquals(1, inv.getCandidates().size());
					assertSame(sdaClass, ((Method)inv.getCandidates().iterator().next()).getParentType());
				}
				else if (inv.getSignature().equals("equals (Object)")) {
					assertSame(TestVerveineUtils.detectElement(repo,Method.class, "equalsSingle"), inv.getSender());
					assertEquals(null, inv.getReceiver());
					assertEquals(1, inv.getCandidates().size());
					assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String"), ((Method)inv.getCandidates().iterator().next()).getParentType());
				}
			}
		}
	}
	
	@Test
	public void testAccess() {
		Attribute att;
		BehaviouralEntity accessor;
		
		att = TestVerveineUtils.detectElement(repo, Attribute.class, "name");
		assertNotNull(att);
		assertSame(2, att.getIncomingAccesses().size());
		accessor = att.getIncomingAccesses().iterator().next().getAccessor();
		assertSame(Method.class, accessor.getClass());
		assertEquals("name", accessor.getName());
		assertEquals("Node", ((Method)accessor).getParentType().getName());

		att = TestVerveineUtils.detectElement(repo, Attribute.class, "serverType");
		assertNotNull(att);
		assertSame(1, att.getIncomingAccesses().size());
		accessor = att.getIncomingAccesses().iterator().next().getAccessor();
		assertSame(Method.class, accessor.getClass());
		assertEquals("setServerType", accessor.getName());
		assertEquals("FileServer", ((Method)accessor).getParentType().getName());

		// finds method PrintServer.output()
		Method output = null;
		for (Method m : TestVerveineUtils.listElements(repo, Method.class, "output")) {
			if (m.getParentType().getName().equals("PrintServer")) {
				output = m;
				break;
			}
		}
		assertNotNull(output);
		assertEquals("Wrong number of outgoing access for method PrintServer.output()", 4, output.getAccesses().size());
		for (Access acc : output.getAccesses()) {
			assertTrue("Unexpected field accessed: "+acc.getVariable().getName(),
						acc.getVariable().getName().equals("out") || acc.getVariable().getName().equals("printer"));
			assertEquals(output, acc.getAccessor());
		}
	}
}
