/**
 * Copyright (c) 2010 Anquetil Nicolas
 */
package tests.fr.inria.verveine.extractor.java;


import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.inria.verveine.core.VerveineUtilsForTests;
import fr.inria.verveine.core.gen.famix.ParameterizableClass;
import fr.inria.verveine.extractor.java.VerveineJParser;

/**
 * @author Nicolas Anquetil
 * @since November 25, 2010
 *
 */
public class VerveineJTest_ArrayListMatthias extends VerveineJTest_Basic {

	public VerveineJTest_ArrayListMatthias() {
		super(/*system*/false);  // there is no System.out.print in the tested code
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		new File(VerveineJParser.OUTPUT_FILE).delete();
		VerveineJParser parser = new VerveineJParser();
		repo = parser.getFamixRepo();
		parser.setOptions(new String[] {"test_src/ArrayList-Matthias"});
		parser.parse();
		parser.emitMSE(VerveineJParser.OUTPUT_FILE);
	}

	@Test
	public void testEntitiesNumber() {
		assertEquals(8+7, VerveineUtilsForTests.selectElementsOfType(repo, fr.inria.verveine.core.gen.famix.Class.class).size()); // Bla, <StubMethodContainer>, Object, String,
		//CharSequence, Serializable, Cloneable, RandomAccess + all ParameterizableClasses
		assertEquals(7,  VerveineUtilsForTests.selectElementsOfType(repo, ParameterizableClass.class).size()); // List, Collection, AbstractList, ArrayList, Comparable, Iterable, AbstractCollection
	}
}