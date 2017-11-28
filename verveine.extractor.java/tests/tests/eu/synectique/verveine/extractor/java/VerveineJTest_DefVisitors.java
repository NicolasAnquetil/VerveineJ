/**
 * Copyright (c) 2010 Nicolas Anquetil
 */
package tests.eu.synectique.verveine.extractor.java;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.junit.Before;
import org.junit.Test;

import ch.akuhn.fame.Repository;
import eu.synectique.verveine.core.VerveineUtilsForTests;
import eu.synectique.verveine.core.gen.famix.Access;
import eu.synectique.verveine.core.gen.famix.AnnotationInstance;
import eu.synectique.verveine.core.gen.famix.AnnotationType;
import eu.synectique.verveine.core.gen.famix.Attribute;
import eu.synectique.verveine.core.gen.famix.BehaviouralEntity;
import eu.synectique.verveine.core.gen.famix.Comment;
import eu.synectique.verveine.core.gen.famix.FAMIXModel;
import eu.synectique.verveine.core.gen.famix.ImplicitVariable;
import eu.synectique.verveine.core.gen.famix.IndexedFileAnchor;
import eu.synectique.verveine.core.gen.famix.Inheritance;
import eu.synectique.verveine.core.gen.famix.Invocation;
import eu.synectique.verveine.core.gen.famix.LocalVariable;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.Namespace;
import eu.synectique.verveine.core.gen.famix.Parameter;
import eu.synectique.verveine.core.gen.famix.PrimitiveType;
import eu.synectique.verveine.core.gen.famix.SourceAnchor;
import eu.synectique.verveine.extractor.java.JavaDictionary;
import eu.synectique.verveine.extractor.java.VerveineJParser;
import eu.synectique.verveine.extractor.java.visitors.VisitorClassMethodDef;
import eu.synectique.verveine.extractor.java.visitors.VisitorPackageDef;

/**
 * @author Nicolas Anquetil
 * @since May 28, 2010
 *
 */
public class VerveineJTest_DefVisitors {

	private static final String[] EMPTY_STRING_ARRAY = /*bindingKeys*/new String[0];

	private FileASTRequestor req;
	private ASTParser jdtParser;
	protected Repository repo;

	protected String[] allSrcFiles() {
		return new String[] {"test_src/LANModel/moose/lan/AbstractDestinationAddress.java",
				"test_src/LANModel/moose/lan/Node.java",
				"test_src/LANModel/moose/lan/Packet.java",
				"test_src/LANModel/moose/lan/SingleDestinationAddress.java",
				"test_src/LANModel/moose/lan/WorkStation.java",
				"test_src/LANModel/moose/lan/server/FileServer.java",
				"test_src/LANModel/moose/lan/server/IPrinter.java",
				"test_src/LANModel/moose/lan/server/OutputServer.java",
				"test_src/LANModel/moose/lan/server/PrintServer.java"};
	}

	protected ASTParser newJDTParser(String srcPath) {
		ASTParser jdtParser = ASTParser.newParser(AST.JLS8);
		
		jdtParser.setEnvironment( /*classPath*/new String[] {srcPath}, /*sourcepathEntries*/new String[] {srcPath}, /*encodings*/null, /*includeRunningVMBootclasspath*/true);
		jdtParser.setResolveBindings(true);
		jdtParser.setKind(ASTParser.K_COMPILATION_UNIT);
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE,				VerveineJParser.DEFAULT_CODE_VERSION);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,	VerveineJParser.DEFAULT_CODE_VERSION);
		options.put(JavaCore.COMPILER_SOURCE,					VerveineJParser.DEFAULT_CODE_VERSION);
		jdtParser.setCompilerOptions(options);
		
		return jdtParser;
	}

	/**
	 * A setup that calls only some of the visitors.
	 * This forces us to create a special FileASTRequestor and thus to not use the default VerveineJParser
	 */
	@Before
	public void setUp() throws Exception {
		new File(VerveineJParser.OUTPUT_FILE).delete();
		repo = new Repository(FAMIXModel.metamodel());
		JavaDictionary dico = new JavaDictionary(repo);
		jdtParser = newJDTParser("test_src/LANModel/moose/lan");

		req = new FileASTRequestor() {
			public void acceptAST(String path, CompilationUnit ast) {
				ast.setProperty(JavaDictionary.SOURCE_FILENAME_PROPERTY, path);

												/*	Dictionary	classSummary	allLocals	anchors							 */
				ast.accept(new VisitorPackageDef(	dico,		false, 			false,		VerveineJParser.ANCHOR_DEFAULT));
				ast.accept(new VisitorClassMethodDef(dico,		false,			false,		VerveineJParser.ANCHOR_DEFAULT));
			}
		};
							/*parsedFiles	encodings	bindingKeys			requestor	monitor */
		jdtParser.createASTs(allSrcFiles(),	null,		EMPTY_STRING_ARRAY,	req,		null);
	}

	@Test
	public void testEntitiesNumber() {

		assertEquals(3,  VerveineUtilsForTests.selectElementsOfType(repo, Namespace.class).size());// moose, moose.lan, moose.lan.server
		assertEquals(10, VerveineUtilsForTests.selectElementsOfType(repo, eu.synectique.verveine.core.gen.famix.Class.class).size()); // WorkStation, SingleDestinationAddress, Packet, Node, AbstractDestinationAddress, PrintServer, XPrinter, OutputServer, IPrinter, FileServer
		assertEquals(40, VerveineUtilsForTests.selectElementsOfType(repo, Method.class).size()); // WorkStation=4, SingleDestinationAddress=5, Packet=7, Node=11, AbstractDestinationAddress=1, PrintServer=2, XPrinter=2, OutputServer=3+INIT_BLOCK, IPrinter=1, FileServer=3
		assertEquals(10, VerveineUtilsForTests.selectElementsOfType(repo, Attribute.class).size());
		assertEquals(26,   VerveineUtilsForTests.selectElementsOfType(repo, Parameter.class).size());
		assertEquals(0,    VerveineUtilsForTests.selectElementsOfType(repo, LocalVariable.class).size());
		assertEquals(22,   VerveineUtilsForTests.selectElementsOfType(repo, Comment.class).size());  // FileServer=2, IPrinter=1, OutputServer=3, PrintServer=3, AbstractDestinationAddress=1, Node=3, Packet=1, SingleDestinationAddress=3, WorkStation=5
	}

	@Test
	public void testStubs() {
		Namespace pckg;

		// Note: package names were not compressed
		pckg = VerveineUtilsForTests.detectFamixElement(repo, Namespace.class, "moose.lan");
		assertNotNull(pckg);
		assertFalse(pckg.getIsStub());

		assertNotNull(pckg.getBelongsTo());
		assertFalse(pckg.getBelongsTo().getIsStub());

		eu.synectique.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(clazz);
		assertFalse(clazz.getIsStub());
	}

	@Test
	public void testClassProperties() {
		// Note: package names were not compressed
		Namespace pckg = VerveineUtilsForTests.detectFamixElement(repo, Namespace.class, "moose.lan");
		assertNotNull(pckg);
		assertEquals("moose.lan", pckg.getName());	

		eu.synectique.verveine.core.gen.famix.Class nodeClass = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(nodeClass);
		assertEquals("Node", nodeClass.getName());
		assertEquals(11, nodeClass.getMethods().size());
		assertEquals(2, nodeClass.getAttributes().size());
		assertSame(pckg, nodeClass.getContainer());
		assertFalse(nodeClass.getIsInterface());

		eu.synectique.verveine.core.gen.famix.Class outputServ = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "OutputServer");
		assertNotNull(outputServ);
		assertEquals("OutputServer", outputServ.getName());
		assertEquals(4, outputServ.getMethods().size());
		for (Method mth : outputServ.getMethods()) {
			String nm = mth.getName();
			assertTrue("Unknown method name: "+nm, nm.equals(JavaDictionary.INIT_BLOCK_NAME) || nm.equals("accept") || nm.equals("canOutput") || nm.equals("output") );
		}
		assertEquals(1, outputServ.getAttributes().size());
		assertFalse(outputServ.getIsInterface());
		
		// Note: package names were not compressed
		pckg = VerveineUtilsForTests.detectFamixElement(repo, Namespace.class, "moose.lan.server");
		assertNotNull(pckg);
		assertEquals("server", pckg.getName());

		eu.synectique.verveine.core.gen.famix.Class interfce = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "IPrinter");
		assertNotNull(interfce);
		assertEquals("IPrinter", interfce.getName());
		assertEquals(1, interfce.getMethods().size());
		assertEquals(0, interfce.getAttributes().size());
		assertSame(pckg, interfce.getContainer());
		assertTrue(interfce.getIsInterface());

		eu.synectique.verveine.core.gen.famix.Class innerClass = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "XPrinter");
		assertNotNull(innerClass);
		assertEquals("XPrinter", innerClass.getName());
		assertEquals(2, innerClass.getMethods().size());
		assertEquals(1, innerClass.getAttributes().size());
		assertSame(VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "PrintServer"), innerClass.getContainer());
		assertFalse(innerClass.getIsInterface());

		eu.synectique.verveine.core.gen.famix.Class anonClass = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "anonymous(IPrinter)");
		assertNotNull(anonClass);
		assertEquals("anonymous(IPrinter)", anonClass.getName());
		assertEquals(1, anonClass.getMethods().size());
		assertEquals(0, anonClass.getAttributes().size());
		assertSame(VerveineUtilsForTests.detectFamixElement(repo,Method.class, "PrintServer"), anonClass.getContainer());
		assertFalse(anonClass.getIsInterface());
	}

	@Test
	public void testMethodProperties() {
		Method mweb = VerveineUtilsForTests.detectFamixElement(repo,Method.class, "methodWithEmptyBody");
		assertNotNull(mweb);
		assertEquals("methodWithEmptyBody", mweb.getName());
		assertEquals("methodWithEmptyBody()", mweb.getSignature());
		assertSame(VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "Node"), mweb.getParentType());

		Method em = VerveineUtilsForTests.detectFamixElement(repo,Method.class, "equalsMultiple");
		assertNotNull(em);
		assertEquals("equalsMultiple", em.getName());
		assertEquals("equalsMultiple(AbstractDestinationAddress)", em.getSignature());
		assertSame(VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "SingleDestinationAddress"), em.getParentType());

		eu.synectique.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "FileServer");
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
		assertSame(VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "FileServer"), n.getParentType());
		
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
		eu.synectique.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(clazz);
		Collection<Attribute> l_atts = clazz.getAttributes();
		assertEquals(2, l_atts.size());
		for (Attribute a : l_atts) {
			if (a.getName().equals("nextNode")) {
				assertSame(clazz, a.getParentType());
			}
			else if (a.getName().equals("name")) {
				assertSame(clazz, a.getParentType());
			}
		}
	}

	@Test
	public void testParameter() {
		eu.synectique.verveine.core.gen.famix.Class nodeClass = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "Node");
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
		eu.synectique.verveine.core.gen.famix.Class iprintClass = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "IPrinter");
		assertNotNull(iprintClass);
		Method mPrint = iprintClass.getMethods().iterator().next();
		assertEquals(2, mPrint.getParameters().size());
		for (Parameter p : mPrint.getParameters()) {
			assertSame(mPrint, p.getParentBehaviouralEntity());
			assertTrue(p.getName().equals("contents") || p.getName().equals("rv"));
		}
	}

	@Test
	public void testSourceAnchors() {
		SourceAnchor anc = null;
		
		eu.synectique.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "XPrinter");
		assertNotNull(clazz);

		anc = clazz.getSourceAnchor();
		assertNotNull(anc);
		assertSame(clazz, anc.getElement());
		assertSame(IndexedFileAnchor.class, anc.getClass());
		assertEquals("test_src/LANModel/moose/lan/server/PrintServer.java", ((IndexedFileAnchor)anc).getFileName());
		assertEquals(235, ((IndexedFileAnchor)anc).getStartPos());
		assertEquals(528, ((IndexedFileAnchor)anc).getEndPos());

		clazz = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(clazz);

		anc = clazz.getSourceAnchor();
		assertNotNull(anc);
		assertSame(clazz, anc.getElement());
		assertSame(IndexedFileAnchor.class, anc.getClass());
		assertTrue("Wrong file source for class Node", ((IndexedFileAnchor)anc).getFileName().equals("test_src/LANModel/moose/lan/Node.java"));
		assertEquals(64, ((IndexedFileAnchor)anc).getStartPos());
		assertEquals(1281, ((IndexedFileAnchor)anc).getEndPos());
		
		Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "equalsMultiple");
		assertNotNull(meth);

		anc = meth.getSourceAnchor();
		assertNotNull(anc);
		assertSame(meth, anc.getElement());
		assertSame(IndexedFileAnchor.class, anc.getClass());
		assertTrue("Wrong file source for method SingleDestinationAddress.equalsMultiple()", ((IndexedFileAnchor)anc).getFileName().equals("test_src/LANModel/moose/lan/SingleDestinationAddress.java"));
		assertEquals(664, ((IndexedFileAnchor)anc).getStartPos());
		assertEquals(751, ((IndexedFileAnchor)anc).getEndPos());
		
		Attribute att = VerveineUtilsForTests.detectFamixElement(repo, Attribute.class, "originator");
		assertNotNull(att);

		anc = att.getSourceAnchor();
		assertNotNull(anc);
		assertSame(att, anc.getElement());
		assertSame(IndexedFileAnchor.class, anc.getClass());
		assertTrue("Wrong file source for field Packet.originator", ((IndexedFileAnchor)anc).getFileName().equals("test_src/LANModel/moose/lan/Packet.java"));
		assertEquals(217, ((IndexedFileAnchor)anc).getStartPos());
		assertEquals(240, ((IndexedFileAnchor)anc).getEndPos());
		
	}

	@Test
	public void testModifiers() {
		eu.synectique.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "OutputServer");
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
		eu.synectique.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "SingleDestinationAddress");
		assertNotNull(clazz);
		Collection<Comment> cmts = clazz.getComments();
		assertEquals(1, cmts.size());
		SourceAnchor anc = cmts.iterator().next().getSourceAnchor();
		assertEquals(64, ((IndexedFileAnchor)anc).getStartPos());
		assertEquals(120, ((IndexedFileAnchor)anc).getEndPos());

		Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "equalsSingle");
		assertNotNull(meth);
		cmts = meth.getComments();
		assertEquals(1, cmts.size());
		anc = cmts.iterator().next().getSourceAnchor();
		assertEquals(533, ((IndexedFileAnchor)anc).getStartPos());
		assertEquals(588, ((IndexedFileAnchor)anc).getEndPos());

		// testing the non javadoc comments (those that are treated)
		clazz = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "WorkStation");
		assertNotNull(clazz);
		Attribute a = clazz.getAttributes().iterator().next();
		assertEquals("type", a.getName());
		cmts = a.getComments();
		assertEquals(1, cmts.size());
		anc = (IndexedFileAnchor)cmts.iterator().next().getSourceAnchor();
		assertEquals(164, ((IndexedFileAnchor)anc).getStartPos().intValue());
	}
	
	@Test
	public void testMetric() {
		Collection<Method> lMeths = VerveineUtilsForTests.listFamixElements(repo, Method.class, "accept");
		assertEquals(3, lMeths.size());
		for (Method m : lMeths) {
			assertNotNull(m);
			eu.synectique.verveine.core.gen.famix.Class owner = (eu.synectique.verveine.core.gen.famix.Class) m.getParentType();
			assertNotNull(owner);
			if (owner.getName().equals("OutputServer")) {
			assertEquals(2, m.getCyclomaticComplexity());
				assertEquals(3, m.getNumberOfStatements());
			}
			else if (owner.getName().equals("Node")) {
				assertEquals(1, m.getCyclomaticComplexity());
				assertEquals(1, m.getNumberOfStatements());
			}
			else if (owner.getName().equals("WorkStation")) {
				assertEquals(2, m.getCyclomaticComplexity());
				assertEquals(4, m.getNumberOfStatements());
			}
		}		
	}

}