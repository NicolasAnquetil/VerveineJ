package tests.fr.inria.verveine.extractor.java;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import ch.akuhn.fame.Repository;

import fr.inria.verveine.extractor.java.VerveineJParser;

public class VerveineJTest_Configuration {

	private static final String OTHER_OUTPUT_FILE= "other_output.mse";
	private Repository repo;

	@Test
	public void testChangeOutputFilePath()
	{
		
		new File(VerveineJParser.OUTPUT_FILE).delete();

		new File(VerveineJTest_Configuration.OTHER_OUTPUT_FILE).delete();
		assertFalse(new File(VerveineJTest_Configuration.OTHER_OUTPUT_FILE).exists());
	
		String[] args = new String[] {"-o",VerveineJTest_Configuration.OTHER_OUTPUT_FILE, "test_src/LANModel/"};
		
		VerveineJParser.main(args);	
		assertTrue(new File(VerveineJTest_Configuration.OTHER_OUTPUT_FILE).exists());
		assertFalse(new File(VerveineJParser.OUTPUT_FILE).exists());
		
	}
}
