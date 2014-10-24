package tests.eu.synectique.verveine.extractor.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import ch.akuhn.fame.Repository;

import eu.synectique.verveine.core.VerveineUtilsForTests;
import eu.synectique.verveine.extractor.java.VerveineJParser;
import eu.synectique.verveine.core.gen.famix.LocalVariable;

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

}
