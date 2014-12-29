package tests.eu.synectique.verveine.extractor.java;

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
import eu.synectique.verveine.core.gen.famix.FileAnchor;
import eu.synectique.verveine.core.gen.famix.Invocation;
import eu.synectique.verveine.core.gen.famix.LocalVariable;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.Reference;
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
		assertEquals(2, VerveineUtilsForTests.selectElementsOfType(repo, LocalVariable.class).size());
		
		// with option
		parser = new VerveineJParser();
		repo = parser.getFamixRepo();
		parser.setOptions(new String[] {"-alllocals", "test_src/ad_hoc/Planet.java"});
		parser.parse();
		assertEquals(5, VerveineUtilsForTests.selectElementsOfType(repo, LocalVariable.class).size());		
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
		Attribute prtr = VerveineUtilsForTests.detectElement(repo, Attribute.class, "printer");
		assertNotNull(prtr);
		assertEquals(2, prtr.getIncomingAccesses().size());
		for (Access acc : prtr.getIncomingAccesses()) {
			anc = acc.getSourceAnchor(); 
			assertNotNull(anc);
			assertEquals(FileAnchor.class, anc.getClass());
			int sl = (Integer) ((FileAnchor)anc).getStartLine();
			int el = (Integer) ((FileAnchor)anc).getEndLine();
			assertTrue( (sl == 34) || (sl == 52) );
			assertTrue(sl == el);
		}
		
		// testing invocation
		eu.synectique.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "IPrinter");
		assertNotNull(clazz);
		Method mth = clazz.getMethods().iterator().next();  // first (and sole) method
		assertNotNull(mth);
		assertEquals("print", mth.getName());
		assertEquals(1, mth.getIncomingInvocations().size());
		Invocation invok = mth.getIncomingInvocations().iterator().next();
		anc = invok.getSourceAnchor(); 
		assertNotNull(anc);
		assertEquals(FileAnchor.class, anc.getClass());
		assertEquals((Integer)52, (Integer) ((FileAnchor)anc).getStartLine());
		assertEquals((Integer)52, (Integer) ((FileAnchor)anc).getEndLine());	
	}

}
