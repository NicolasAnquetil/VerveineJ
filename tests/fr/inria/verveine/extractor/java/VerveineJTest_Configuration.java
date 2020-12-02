package fr.inria.verveine.extractor.java;

import org.junit.Before;
import org.junit.Test;
import org.moosetechnology.model.famixjava.famixjavaentities.Class;
import org.moosetechnology.model.famixjava.famixjavaentities.*;
import org.moosetechnology.model.famixjava.famixtraits.TAccess;
import org.moosetechnology.model.famixjava.famixtraits.TNamedEntity;

import java.io.File;
import java.lang.Exception;
import java.util.Collection;

import static org.junit.Assert.*;

public class VerveineJTest_Configuration extends VerveineJTest_Basic {

	private static final String OTHER_OUTPUT_FILE= "other_output.mse";

	public VerveineJTest_Configuration() {
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
		//parser.emitMSE(VerveineJParser.OUTPUT_FILE);
	}

	@Test
	public void testChangeOutputFilePath()
	{
		new File(VerveineJParser.OUTPUT_FILE).delete();

		new File(VerveineJTest_Configuration.OTHER_OUTPUT_FILE).delete();
		assertFalse(new File(VerveineJTest_Configuration.OTHER_OUTPUT_FILE).exists());
	
		parse( new String[] {"-o",VerveineJTest_Configuration.OTHER_OUTPUT_FILE, "test_src/LANModel/"});
		parser.emitMSE();

		assertTrue(new File(VerveineJTest_Configuration.OTHER_OUTPUT_FILE).exists());
		assertFalse(new File(VerveineJParser.OUTPUT_FILE).exists());
	}

	@Test
	public void testNotAlllocals() {
		// works in team with testAlllocals
		parse(new String[]{"test_src/exceptions/ReadClient.java", "test_src/exceptions/ReadException.java"}); // note: ReadException.java needed to resolve lire() method
		assertEquals(3, entitiesOfType( LocalVariable.class).size());  // nom, num, e
		assertEquals(8, entitiesOfType( Access.class).size()); // getNum() -> num, setNum() -> num, getNom() -> nom, setNom() -> nom + 4 "this"
	}

	@Test
	public void testAlllocals() {
		// works in team with testNotAlllocals
		parse(new String[]{"-alllocals", "test_src/exceptions/ReadClient.java", "test_src/exceptions/ReadException.java"}); // note: ReadException.java needed to resolve lire() method

		assertEquals(5, entitiesOfType( LocalVariable.class).size());      // lire().nom ; lire().num ; lire().e ; lire().c ; lire().i
        int accessReadClient = 0;
        int accessLire = 0;
        int accessSetNum = 0;
        int accessGetNum = 0;
        int accessSetNom = 0;
        int accessGetNom = 0;
        for (Access acc : entitiesOfType( Access.class)) {
            switch (((TNamedEntity)acc.getAccessor()).getName()) {
                case "ReadClient": accessReadClient++; break;
                case "lire": accessLire++; break;
                case "getNum": accessGetNum++; break;
                case "setNum": accessSetNum++; break;
                case "getNom": accessGetNom++; break;
                case "setNom": accessSetNom++; break;
                default: fail("Unknown accessor name: " + ((TNamedEntity)acc.getAccessor()).getName()); break;
            }
        }
        assertEquals(4, accessReadClient);
        assertEquals(20, accessLire);
        assertEquals(1, accessGetNum);
        assertEquals(3, accessSetNum);
        assertEquals(1, accessGetNom);
        assertEquals(3, accessSetNom);
	}

	@Test
	public void testClassDeclsInExpr() {
		parse(new String[]{"-alllocals", "test_src/ad_hoc/SpecialLocalVarDecls.java"});

		Collection<LocalVariable> vars = entitiesOfType( LocalVariable.class);
        LocalVariable var1 = null;
		LocalVariable var2 = null;
		LocalVariable var3 = null;
		assertEquals(3, vars.size());
		for (LocalVariable v : vars) {
            if (v.getName().equals("firstVar")) {
                var1 = v;
            }
			else if (v.getName().equals("secondVar")) {
				var2 = v;
			}
			else if (v.getName().equals("thirdVar")) {
				var3 = v;
			}
		}
        assertNotNull(var1);
		assertNotNull(var2);
		assertNotNull(var3);
        assertEquals(2, var1.getIncomingAccesses().size());
		assertEquals(3, var2.getIncomingAccesses().size());
		assertEquals(4, var3.getIncomingAccesses().size());


        Collection<Parameter> params = entitiesOfType( Parameter.class);
        Parameter par1 = null;
        Parameter par2 = null;
		assertEquals(3, params.size());
		for (Parameter p : params) {
            if (p.getName().equals("param1")) {
				par1 = p;
			}
			else if (p.getName().equals("param2")) {
				par2 = p;
			}
		}
        assertNotNull(par1);
		assertNotNull(par2);
        assertNotNull(par1.getParentBehaviouralEntity());
        assertNotNull(par2.getParentBehaviouralEntity());

    }

	@Test
	public void testAlllocalsAndInitializerAndField() {
		parse(new String[]{"-alllocals", "test_src/ad_hoc/SpecialLocalVarDecls.java"});

		Collection<Attribute> vars = entitiesOfType( Attribute.class);
		assertEquals(3, vars.size());  // aField, anonymousListField, System.out
	}

    @Test
    public void testAnchorsAssoc()
    {
        String[] args = new String[] {
                "-anchor", "assoc",
                "-cp", "test_src/LANModel/",
                "test_src/LANModel/moose/lan/server/PrintServer.java",
        };

        // parsing
        parse(args);

        SourceAnchor anc;
        // testing accesses
        Attribute prtr = detectFamixElement( Attribute.class, "printer");
        assertNotNull(prtr);
        assertEquals(2, prtr.getIncomingAccesses().size());
        for (TAccess tacc : prtr.getIncomingAccesses()) {
            anc = (SourceAnchor) ((Access) tacc).getSourceAnchor();
            assertNotNull(anc);
            assertEquals(IndexedFileAnchor.class, anc.getClass());
            int sp = (Integer) ((IndexedFileAnchor)anc).getStartPos();
            int ep = (Integer) ((IndexedFileAnchor)anc).getEndPos();
			if (isWindows()){
				assertTrue("wrong startPos for Access: " + sp, (sp == 618) || (sp == 1032));
				assertTrue("wrong endPos for Access: " + ep, (ep == 629) || (ep == 1043));
			} else {
				assertTrue("wrong startPos for Access: " + sp, (sp == 584) || (sp == 980));
				assertTrue("wrong endPos for Access: " + ep, (ep == 595) || (ep == 991));
			}
		}

		// testing invocation
		org.moosetechnology.model.famixjava.famixjavaentities.Class clazz = detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, "IPrinter");
		assertNotNull(clazz);
		Method mth = (Method) firstElt(clazz.getMethods());  // first (and sole) method
		assertNotNull(mth);
		assertEquals("print", mth.getName());
		assertEquals(1, mth.getIncomingInvocations().size());
		Invocation invok = (Invocation) firstElt(mth.getIncomingInvocations());
		anc = (SourceAnchor) invok.getSourceAnchor();
		assertNotNull(anc);
		assertEquals(IndexedFileAnchor.class, anc.getClass());
		if (isWindows()) {
			assertEquals(1032, ((IndexedFileAnchor) anc).getStartPos());
			assertEquals(1113, ((IndexedFileAnchor) anc).getEndPos());
		} else {
			assertEquals(980, ((IndexedFileAnchor) anc).getStartPos());
			assertEquals(1061, ((IndexedFileAnchor) anc).getEndPos());
		}

	}

    @Test
    public void testExcludepath()
    {
        String[] args = new String[] {
                "-excludepath", "*Address*",
                "-excludepath", "*erver*",
                "test_src/LANModel/",
        };

        // parsing
        parse(args);

        int count=0;
        for (Class clazz : entitiesOfType(Class.class)) {
            if (!clazz.getIsStub() ) {
                count++;
            }
        }
        assertEquals(3, count);
    }

}
