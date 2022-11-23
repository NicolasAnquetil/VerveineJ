package fr.inria.verveine.extractor.java;

import org.junit.Before;
import org.junit.Test;
import org.moosetechnology.model.famixjava.famixjavaentities.Method;
import org.moosetechnology.model.famixjava.famixtraits.TNamedEntity;

import java.io.File;

import static org.junit.Assert.*;

public class VerveineJTest_Exceptions extends VerveineJTest_Basic {

    public VerveineJTest_Exceptions() {
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
        parser.configure( new String[] {"test_src/exceptions"});
        parser.parse();
    }

    @Test
    public void testDeclaredExceptions() {
        Method meth = detectFamixElement( Method.class, "lire");
        assertNotNull(meth);

        org.moosetechnology.model.famixjava.famixjavaentities.Exception excepClass = detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Exception.class, "ReadException");
        assertNotNull(excepClass);

        assertEquals(1, meth.getDeclaredExceptions().size());
        org.moosetechnology.model.famixjava.famixjavaentities.Exception exD = (org.moosetechnology.model.famixjava.famixjavaentities.Exception) firstElt(meth.getDeclaredExceptions());
        assertSame(meth, firstElt(exD.getDeclaringEntities()));
        assertSame(excepClass, exD);
    }

    @Test
    public void testThrownExceptions() {
        Method meth = detectFamixElement( Method.class, "lire");
        assertNotNull(meth);

        assertEquals(1, meth.getThrownExceptions().size());
        org.moosetechnology.model.famixjava.famixjavaentities.Exception exT = (org.moosetechnology.model.famixjava.famixjavaentities.Exception) firstElt(meth.getThrownExceptions());
        assertSame(meth, firstElt(exT.getDeclaringEntities()));

        org.moosetechnology.model.famixjava.famixjavaentities.Exception excepClass = detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Exception.class, "ReadException");
        assertSame(excepClass, exT);
    }

    @Test
    public void testCaughtExceptions() {
        Method meth = detectFamixElement( Method.class, "lire");
        assertNotNull(meth);

        assertEquals(1,meth.getCaughtExceptions().size());
        org.moosetechnology.model.famixjava.famixjavaentities.Exception exC = (org.moosetechnology.model.famixjava.famixjavaentities.Exception) firstElt(meth.getCaughtExceptions());
        assertSame(meth, firstElt(exC.getDeclaringEntities()));

        org.moosetechnology.model.famixjava.famixjavaentities.Exception excepClass = detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Exception.class, "IOException");
        assertSame(excepClass, exC);
    }

    @Test
    public void testGenericExceptions() {
        Method meth = detectFamixElement( Method.class, "doThrow");
        assertNotNull(meth);

//        assertEquals(0, meth.getParentType().getTypes().size());
        assertEquals(1, meth.getDeclaredExceptions().size());
        org.moosetechnology.model.famixjava.famixjavaentities.Exception exD = (org.moosetechnology.model.famixjava.famixjavaentities.Exception) firstElt(meth.getDeclaredExceptions());
        assertSame(meth, firstElt(exD.getDeclaringEntities()));
        assertEquals("T", exD.getName());
    }

}
