package fr.inria.verveine.extractor.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;

import eu.synectique.verveine.core.gen.famix.*;
import org.junit.Test;

import ch.akuhn.fame.Repository;

import eu.synectique.verveine.core.VerveineUtilsForTests;
import fr.inria.verveine.extractor.java.VerveineJParser;

public class VerveineJTest_Configuration {

	private static final String OTHER_OUTPUT_FILE= "other_output.mse";

	@Test
	public void testChangeOutputFilePath()
	{
		new File(VerveineJParser.OUTPUT_FILE).delete();

		new File(VerveineJTest_Configuration.OTHER_OUTPUT_FILE).delete();
		assertFalse(new File(VerveineJTest_Configuration.OTHER_OUTPUT_FILE).exists());
	
		String[] args = new String[] {"-o",VerveineJTest_Configuration.OTHER_OUTPUT_FILE, "test_src/LANModel/"};
		
		//VerveineJParser.main(args);
		// Executing the instructions of the main() without calling the licence verification stuff
		VerveineJParser parser = new VerveineJParser();
		parser.setOptions(args);
		parser.parse();
		parser.emitMSE();

		assertTrue(new File(VerveineJTest_Configuration.OTHER_OUTPUT_FILE).exists());
		assertFalse(new File(VerveineJParser.OUTPUT_FILE).exists());
	}

	@Test
	public void testAlllocals() {
		VerveineJParser parser;
		Repository repo;

		// without option
		parser = new VerveineJParser();
		repo = parser.getFamixRepo();
		parser.setOptions(new String[]{"test_src/ad_hoc/ReadException.java", "test_src/ad_hoc/ReadClient.java"});
		parser.parse();
		assertEquals(3, VerveineUtilsForTests.selectElementsOfType(repo, LocalVariable.class).size());  // lire().nom ; lire().num ; lire().e
		assertEquals(4, VerveineUtilsForTests.selectElementsOfType(repo, Access.class).size());  // setNum()->num ; getNum()->num ; setNom()->nom ; getNom()->nom

		// with option
		parser = new VerveineJParser();
		repo = parser.getFamixRepo();
		parser.setOptions(new String[]{"-alllocals", "test_src/ad_hoc/ReadException.java", "test_src/ad_hoc/ReadClient.java"});
		parser.parse();
		assertEquals(5, VerveineUtilsForTests.selectElementsOfType(repo, LocalVariable.class).size());      // + lire().c + lire().i
		assertEquals(28, VerveineUtilsForTests.selectElementsOfType(repo, Access.class).size());
		// ReadClient()->this*2+2 ; lire()->c*6+i*2+in*4+nom*2+num*2 ; setNum()->this*1+2 ; getNum()->1 ; setNom()->this*1+2 ; getNom()->1
	}

	@Test
	public void testClassDeclsInExpr() {
		VerveineJParser parser;
		Repository repo;

		// with option
		parser = new VerveineJParser();
		repo = parser.getFamixRepo();
		parser.setOptions(new String[]{"-alllocals", "test_src/ad_hoc/SpecialLocalVarDecls.java"});
		parser.parse();

		Collection<LocalVariable> vars = VerveineUtilsForTests.selectElementsOfType(repo, LocalVariable.class);
        LocalVariable var1 = null;
		LocalVariable var2 = null;
		LocalVariable var3 = null;
		assertEquals(3, vars.size());
		for (LocalVariable v : vars) {
            if (v.getName().equals("firstVar")) {
                var1 = v;
            }
			else if (v.getName().equals("secondVar")) {
				var2 = v;
			}
			else if (v.getName().equals("thirdVar")) {
				var3 = v;
			}
		}
        assertNotNull(var1);
		assertNotNull(var2);
		assertNotNull(var3);
        assertEquals(1, var1.getIncomingAccesses().size());
		assertEquals(2, var2.getIncomingAccesses().size());
		assertEquals(3, var3.getIncomingAccesses().size());


        Collection<Parameter> params = VerveineUtilsForTests.selectElementsOfType(repo, Parameter.class);
        Parameter par1 = null;
        Parameter par2 = null;
		assertEquals(3, params.size());
		for (Parameter p : params) {
            if (p.getName().equals("param1")) {
				par1 = p;
			}
			else if (p.getName().equals("param2")) {
				par2 = p;
			}
		}
        assertNotNull(par1);
		assertNotNull(par2);
        assertNotNull(par1.getParentBehaviouralEntity());
        assertNotNull(par2.getParentBehaviouralEntity());

    }


	@Test
	public void testAnchorsAssoc()
	{
		VerveineJParser parser;
		Repository repo;
		String[] args = new String[] {
							"-anchor", "assoc",
							"-cp", "test_src/LANModel/",
							"test_src/LANModel/moose/lan/server/PrintServer.java",
						};

		// parsing
		parser = new VerveineJParser();
		repo = parser.getFamixRepo();
		parser.setOptions(args);
		parser.parse();

		SourceAnchor anc;
		// testing accesses
		Attribute prtr = VerveineUtilsForTests.detectFamixElement(repo, Attribute.class, "printer");
		assertNotNull(prtr);
		assertEquals(2, prtr.getIncomingAccesses().size());
		for (Access acc : prtr.getIncomingAccesses()) {
			anc = acc.getSourceAnchor(); 
			assertNotNull(anc);
			assertEquals(IndexedFileAnchor.class, anc.getClass());
			int sp = (Integer) ((IndexedFileAnchor)anc).getStartPos();
			int ep = (Integer) ((IndexedFileAnchor)anc).getEndPos();
			assertTrue("wrong statPos for Access: " + sp, (sp == 558) || (sp == 945) );
			assertTrue("wrong endPos for Access: " + ep, (ep == 569) || (ep == 956) );
		}
		
		// testing invocation
		eu.synectique.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "IPrinter");
		assertNotNull(clazz);
		Method mth = clazz.getMethods().iterator().next();  // first (and sole) method
		assertNotNull(mth);
		assertEquals("print", mth.getName());
		assertEquals(1, mth.getIncomingInvocations().size());
		Invocation invok = mth.getIncomingInvocations().iterator().next();
		anc = invok.getSourceAnchor(); 
		assertNotNull(anc);
		assertEquals(IndexedFileAnchor.class, anc.getClass());
		assertEquals((Integer)945,  (Integer) ((IndexedFileAnchor)anc).getStartPos());
		assertEquals((Integer)1026, (Integer) ((IndexedFileAnchor)anc).getEndPos());	
	}

}
