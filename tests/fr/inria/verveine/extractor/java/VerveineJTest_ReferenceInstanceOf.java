package fr.inria.verveine.extractor.java;


import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import eu.synectique.verveine.core.gen.famix.Class;

public class VerveineJTest_ReferenceInstanceOf extends VerveineJTest_Basic {

    public VerveineJTest_ReferenceInstanceOf() {
        super(false);
    }

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        new File(VerveineJOptions.OUTPUT_FILE).delete();
        parser = new VerveineJParser();
        repo = parser.getFamixRepo();
    }

    private void parse(String[] sources) {
        parser.configure( sources);
        parser.parse();
        parser.emitMSE(VerveineJOptions.OUTPUT_FILE);
    }

    @Test
    public void testReferenceToString() {
        parse(new String[]{"-alllocals", "-anchor", "assoc", "test_src/instanceOf"});
        Class stringClass = detectFamixElement( Class.class, "String");
        assertEquals (stringClass.getIncomingReferences().size(), 1);
    }

}
