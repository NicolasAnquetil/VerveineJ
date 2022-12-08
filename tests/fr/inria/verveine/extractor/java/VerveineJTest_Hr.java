package fr.inria.verveine.extractor.java;

import org.junit.Before;
import org.junit.Test;
import org.moosetechnology.model.famixjava.famixjavaentities.Method;
import org.moosetechnology.model.famixjava.famixtraits.TMethod;
import org.moosetechnology.model.famixjava.famixtraits.TNamedEntity;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.*;

public class VerveineJTest_Hr extends VerveineJTest_Basic {

    public VerveineJTest_Hr() {
        super(false);
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        new File(DEFAULT_OUTPUT_FILE).delete();
        VerveineJParser parser = new VerveineJParser();
        repo = parser.getFamixRepo();
        parser.configure( new String[] {"test_src/hr"});
        parser.parse();
    }

    @Test
    public void testAnnotations() {
               
    }


}
