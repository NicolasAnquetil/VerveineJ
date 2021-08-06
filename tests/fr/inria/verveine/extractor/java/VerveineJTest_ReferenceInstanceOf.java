package fr.inria.verveine.extractor.java;

import org.junit.Before;
import org.junit.Test;
import org.moosetechnology.model.famixjava.famixjavaentities.Class;

import static org.junit.Assert.assertEquals;

import java.io.File;

public class VerveineJTest_ReferenceInstanceOf extends VerveineJTest_Basic {

    public VerveineJTest_ReferenceInstanceOf() {
        super(false);
    }

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        new File(DEFAULT_OUTPUT_FILE).delete();
        parser = new VerveineJParser();
        repo = parser.getFamixRepo();
    }

    private void parse(String[] sources) {
        parser.configure( sources);
        parser.parse();
        parser.exportModel();
    }

    @Test
    public void testReferenceToString() {
        parse(new String[]{"-alllocals", "-anchor", "assoc", "test_src/instanceOf"});
        Class stringClass = detectFamixElement( Class.class, "String");
        assertEquals (stringClass.getIncomingReferences().size(), 1);
    }

}
