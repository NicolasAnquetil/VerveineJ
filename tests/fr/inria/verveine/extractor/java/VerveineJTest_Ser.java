package fr.inria.verveine.extractor.java;

import org.junit.Before;
import org.junit.Test;
import org.moosetechnology.model.famixjava.famixjavaentities.Method;
import org.moosetechnology.model.famixjava.famixtraits.TMethod;
import org.moosetechnology.model.famixjava.famixtraits.TNamedEntity;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.*;

public class VerveineJTest_Ser extends VerveineJTest_Basic {

    public VerveineJTest_Ser() {
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
        parser.configure( new String[] {"test_src/ser"});
        parser.parse();
    }

    @Test
    public void testDeclaredExceptions() {
        org.moosetechnology.model.famixjava.famixjavaentities.Class myServiceImplClass = detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, "MyServiceImpl");
        assertNotNull(myServiceImplClass);
        assertEquals(11, myServiceImplClass.getMethods().size());

        for (TMethod tm : myServiceImplClass.getMethods()) {
            Method m = (Method) tm;
            if (!((m.getName().equals("<Initializer>") || (m.getKind() != null && m.getKind().equals("constructor")))))
                assertEquals(1, m.getDeclaredExceptions().size());
        
        }        
    }


}
