package fr.inria.verveine.extractor.java;

import org.junit.Before;
import org.junit.Test;
import org.moosetechnology.model.famix.famixjavaentities.Method;

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

        org.moosetechnology.model.famix.famixjavaentities.Exception excepClass = detectFamixElement(org.moosetechnology.model.famix.famixjavaentities.Exception.class, "ReadException");
        assertNotNull(excepClass);

        assertEquals(1, meth.getDeclaredExceptions().size());
        org.moosetechnology.model.famix.famixjavaentities.Exception exD = (org.moosetechnology.model.famix.famixjavaentities.Exception) firstElt(meth.getDeclaredExceptions());
        assertSame(meth, firstElt(exD.getDeclaringEntities()));
        assertSame(excepClass, exD);
    }

    @Test
    public void testThrownExceptions() {
        Method meth = detectFamixElement( Method.class, "lire");
        assertNotNull(meth);

        assertEquals(1, meth.getThrownExceptions().size());
        org.moosetechnology.model.famix.famixjavaentities.Exception exT = (org.moosetechnology.model.famix.famixjavaentities.Exception) firstElt(meth.getThrownExceptions());
        assertSame(meth, firstElt(exT.getThrowingEntities()));

        org.moosetechnology.model.famix.famixjavaentities.Exception excepClass = detectFamixElement(org.moosetechnology.model.famix.famixjavaentities.Exception.class, "ReadException");
        assertSame(excepClass, exT);
    }

    @Test
    public void testCaughtExceptions() {
        Method meth = detectFamixElement( Method.class, "lire");
        assertNotNull(meth);

        assertEquals(1,meth.getCaughtExceptions().size());
        org.moosetechnology.model.famix.famixjavaentities.Exception exC = (org.moosetechnology.model.famix.famixjavaentities.Exception) firstElt(meth.getCaughtExceptions());
        assertSame(meth, firstElt(exC.getCatchingEntities()));

        org.moosetechnology.model.famix.famixjavaentities.Exception excepClass = detectFamixElement(org.moosetechnology.model.famix.famixjavaentities.Exception.class, "IOException");
        assertSame(excepClass, exC);
    }

    @Test
    public void testGenericExceptions() {
        Method meth = detectFamixElement( Method.class, "doThrow");
        assertNotNull(meth);

//        assertEquals(0, meth.getParentType().getTypes().size());
        assertEquals(1, meth.getDeclaredExceptions().size());
        org.moosetechnology.model.famix.famixjavaentities.Exception exD = (org.moosetechnology.model.famix.famixjavaentities.Exception) firstElt(meth.getDeclaredExceptions());
        assertSame(meth, firstElt(exD.getDeclaringEntities()));
        assertEquals("T", exD.getName());
    }

    @Test
    public void testExceptionCanHaveInnerEnumerationExceptions() {
        org.moosetechnology.model.famix.famixjavaentities.Enum typeEnum = detectFamixElement( org.moosetechnology.model.famix.famixjavaentities.Enum.class, "Type");
        assertNotNull(typeEnum);

        org.moosetechnology.model.famix.famixjavaentities.Exception localException = detectFamixElement(org.moosetechnology.model.famix.famixjavaentities.Exception.class, "LocalException");
        assertNotNull(localException);

    }

}
