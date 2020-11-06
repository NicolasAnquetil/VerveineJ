package fr.inria.verveine.extractor.java;

import org.moosetechnology.model.famix.famix.*;
import org.moosetechnology.model.famix.famix.Class;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.Exception;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class VerveineJTest_ReferenceInstanceOf extends VerveineJTest_Basic {

    public VerveineJTest_ReferenceInstanceOf() {
        super(false);
    }

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        new File(VerveineJParser.OUTPUT_FILE).delete();
        parser = new VerveineJParser();
        repo = parser.getFamixRepo();
    }

    private void parse(String[] sources) {
        parser.setOptions(sources);
        parser.parse();
        parser.emitMSE(VerveineJParser.OUTPUT_FILE);
    }

    @Test
    public void testReferenceToString() {
        parse(new String[]{"-alllocals", "-anchor", "assoc", "test_src/instanceOf"});
        Class stringClass = detectFamixElement( Class.class, "String");
        assertEquals (stringClass.getIncomingReferences().size(), 1);
    }

}
