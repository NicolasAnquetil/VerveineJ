package fr.inria.verveine.extractor.java;

import ch.akuhn.fame.Repository;
import eu.synectique.verveine.core.VerveineUtilsForTests;
import eu.synectique.verveine.core.gen.famix.LocalVariable;
import eu.synectique.verveine.core.gen.famix.Method;
import fr.inria.verveine.extractor.java.utils.NodeTypeChecker;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class VerveineJTest_Lambdas extends VerveineJTest_Basic {

    public VerveineJTest_Lambdas() {
        super(false);
    }

    /**
     * @throws java.lang.Exception
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
    public void testLambdaTypedParameter() {
        parse(new String[] {"-alllocals", "test_src/lambdas"});

        Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, JavaDictionary.INIT_BLOCK_NAME);
        assertNotNull(meth);

        LocalVariable seg1 = null;
        LocalVariable seg2 = null;
        assertEquals(2, meth.getLocalVariables().size());
        for (LocalVariable lvar : meth.getLocalVariables()) {
            if (lvar.getName().equals("seg1")) {
                seg1 = lvar;
            }
            else if (lvar.getName().equals("seg2")) {
                seg2 = lvar;
            }
            else {
                fail("Unknown local variable:" + lvar.getName());
            }
        }
        assertNotNull(seg1);
        assertNotNull(seg2);
    }

    @Test
    public void testLambdaUnTypedParameter() {
        parse(new String[] {"-alllocals", "test_src/lambdas"});

        Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "WithLambda");
        assertNotNull(meth);

        LocalVariable col = null;
        LocalVariable found = null;
        LocalVariable t = null;
        assertEquals(3, meth.getLocalVariables().size());
        for (LocalVariable lvar : meth.getLocalVariables()) {
            if (lvar.getName().equals("col")) {
                col = lvar;
            }
            else if (lvar.getName().equals("found")) {
                found = lvar;
            }
            else if (lvar.getName().equals("t")) {
                t = lvar;
            }
            else {
                fail("Unknown local variable:" + lvar.getName());
            }
        }
        assertNotNull(col);
        assertNotNull(found);
        assertNotNull(t);
    }

    @Test
    public void testLambdaNotAllLocals() {
        parse(new String[] {"test_src/lambdas"});

        Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "WithLambda");
        assertNotNull(meth);

        LocalVariable col = null;
        assertEquals(1, meth.getLocalVariables().size());
        col = firstElt(meth.getLocalVariables());
        assertNotNull(col);
        assertEquals("col", col.getName());

        assertEquals(0, meth.getAccesses().size());
    }

}
