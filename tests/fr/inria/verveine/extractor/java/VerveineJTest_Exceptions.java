package fr.inria.verveine.extractor.java;

import ch.akuhn.fame.Repository;
import eu.synectique.verveine.core.VerveineUtilsForTests;
import eu.synectique.verveine.core.gen.famix.CaughtException;
import eu.synectique.verveine.core.gen.famix.DeclaredException;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.ThrownException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class VerveineJTest_Exceptions extends VerveineJTest_Basic {

    public VerveineJTest_Exceptions() {
        super(false);
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        new File(VerveineJParser.OUTPUT_FILE).delete();
        VerveineJParser parser = new VerveineJParser();
        repo = parser.getFamixRepo();
        parser.setOptions(new String[] {"test_src/exceptions"});
        parser.parse();
        //parser.emitMSE(VerveineJParser.OUTPUT_FILE);
    }

    @Test
    public void testDeclaredExceptions() {
        Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "lire");
        assertNotNull(meth);

        eu.synectique.verveine.core.gen.famix.Class excepClass = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "ReadException");
        assertNotNull(excepClass);

        assertEquals(1, meth.getDeclaredExceptions().size());
        DeclaredException exD = firstElt(meth.getDeclaredExceptions());
        assertSame(meth, exD.getDefiningMethod());
        assertSame(excepClass, exD.getExceptionClass());
    }

    @Test
    public void testThrownExceptions() {
        Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "lire");
        assertNotNull(meth);

        assertEquals(1, meth.getThrownExceptions().size());
        ThrownException exT = firstElt(meth.getThrownExceptions());
        assertSame(meth, exT.getDefiningMethod());

        eu.synectique.verveine.core.gen.famix.Class excepClass = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "ReadException");
        assertSame(excepClass, exT.getExceptionClass());
    }

    @Test
    public void testCaughtExceptions() {
        Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "lire");
        assertNotNull(meth);

        assertEquals(1,meth.getCaughtExceptions().size());
        CaughtException exC = firstElt(meth.getCaughtExceptions());
        assertSame(meth, exC.getDefiningMethod());

        eu.synectique.verveine.core.gen.famix.Class excepClass = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "IOException");
        assertSame(excepClass, exC.getExceptionClass());
    }

    @Test
    public void testGenericExceptions() {
        Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "doThrow");
        assertNotNull(meth);

//        assertEquals(0, meth.getParentType().getTypes().size());
        assertEquals(1, meth.getDeclaredExceptions().size());
        DeclaredException exD = firstElt(meth.getDeclaredExceptions());
        assertSame(meth, exD.getDefiningMethod());
        assertEquals("T", exD.getExceptionClass().getName());
    }

}
