package fr.inria.verveine.extractor.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import ch.akuhn.fame.Repository;

import eu.synectique.verveine.core.VerveineUtilsForTests;
import eu.synectique.verveine.extractor.java.VerveineJParser;
import eu.synectique.verveine.core.gen.famix.Access;
import eu.synectique.verveine.core.gen.famix.Attribute;
import eu.synectique.verveine.core.gen.famix.IndexedFileAnchor;
import eu.synectique.verveine.core.gen.famix.Invocation;
import eu.synectique.verveine.core.gen.famix.LocalVariable;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.SourceAnchor;

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
	public void testAlllocals()
	{
		VerveineJParser parser;
		Repository repo;

		// without option
		parser = new VerveineJParser();
		repo = parser.getFamixRepo();
		parser.setOptions(new String[] {"test_src/ad_hoc/Planet.java"});
		parser.parse();
		assertEquals(2, VerveineUtilsForTests.selectElementsOfType(repo, LocalVariable.class).size());  // ret, p
		
		// with option
		parser = new VerveineJParser();
		repo = parser.getFamixRepo();
		parser.setOptions(new String[] {"-alllocals", "test_src/ad_hoc/Planet.java"});
		parser.parse();
		assertEquals(5, VerveineUtilsForTests.selectElementsOfType(repo, LocalVariable.class).size());	  // ret, p, check, earthWeight, mass, 	
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
