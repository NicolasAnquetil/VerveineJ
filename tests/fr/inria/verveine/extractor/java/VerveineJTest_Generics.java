package fr.inria.verveine.extractor.java;

import eu.synectique.verveine.core.Dictionary;
import eu.synectique.verveine.core.gen.famix.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.Exception;
import java.util.Collection;

import static org.junit.Assert.*;

public class VerveineJTest_Generics extends VerveineJTest_Basic {

    public VerveineJTest_Generics() {
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
        parser.setOptions(new String[] {"test_src/generics"});
        parser.parse();
        //parser.emitMSE(VerveineJParser.OUTPUT_FILE);
    }

    @Test
    public void testParameterizableClass() {
        assertEquals(7, entitiesOfType( ParameterizableClass.class).size());
        // WrongInvocation -> List<X>, ArrayList<X>
        // Dictionary -> Dictionary<X>, Map<X,Y>, Hashtable<X,Y>, Collection<X>, Class<X>, ArrayList<X>


        ParameterizableClass generic = null;
        for (ParameterizableClass g : entitiesNamed( ParameterizableClass.class, "Dictionary")) {
            if (g.getBelongsTo().getName().equals(Dictionary.DEFAULT_PCKG_NAME)) {
                // note: For testing purposes class Dictionary<B> in ad_hoc is defined without "package" instruction, so it ends up in the default package
                generic = g;
                break;
            }
        }
        assertNotNull(generic);
        assertEquals("Dictionary", generic.getName());
        assertEquals(2, generic.getTypes().size());  // <B> , ImplicitVars
        for (Type t : generic.getTypes()) {
            String typName = t.getName();
            assertTrue(typName.equals("B") || typName.equals("ImplicitVars"));
        }

        assertEquals(1, generic.getParameters().size());

        ParameterType dicoParam = detectFamixElement( ParameterType.class, "B");
        assertNotNull(dicoParam);
        assertEquals("B", dicoParam.getName());

        assertSame(generic, dicoParam.getContainer());
        assertSame(dicoParam, firstElt(generic.getParameters()));

        /* Collection<Object> is not seen as parameterizable by JDT */
        ParameterizableClass collec = detectFamixElement( ParameterizableClass.class, "Collection");
        assertNotNull(collec);
    }

    @Test
    public void testParameterizedType() {
        Method getx = detectFamixElement( Method.class, "getX");
        assertNotNull(getx);

        assertEquals(1, getx.getOutgoingReferences().size());
        ParameterizedType refedArrList = (ParameterizedType) firstElt(getx.getOutgoingReferences()).getTarget();
        assertNotNull(refedArrList);
        assertEquals("ArrayList", refedArrList.getName());

        eu.synectique.verveine.core.gen.famix.Class arrList = detectFamixElement( eu.synectique.verveine.core.gen.famix.Class.class, "ArrayList");
        assertNotNull(arrList);
        assertEquals(arrList, refedArrList.getParameterizableClass());
        assertEquals(arrList.getContainer(), refedArrList.getContainer());

        assertEquals(1, refedArrList.getArguments().size());
        assertEquals("ABC", firstElt(refedArrList.getArguments()).getName());
    }

    @Test
    public void testUseOfParameterizedClass() {
        ParameterizableClass arrList = detectFamixElement( ParameterizableClass.class, "ArrayList");
        assertEquals(2, arrList.getParameterizedTypes().size()); // WrongInvocation.getX() ; Dictionnary.getEntityByName()
        for (ParameterizedType paramed : arrList.getParameterizedTypes()) {
            assertEquals(1, paramed.getIncomingReferences().size());
            String refererName = firstElt(paramed.getIncomingReferences()).getFrom().getName();
            assertTrue(refererName.equals("getEntityByName") || refererName.equals("getX") );
        }
    }

    @Test  // issue 960
    public void testStubStatusParameterizedTypes() {
        Collection<ParameterizedType> ptypes = entitiesOfType( ParameterizedType.class);
        assertEquals(15,ptypes.size());  // List*1, ArrayList*2, Map*3, Collection<NamedEntity>, Collection<T>, Hashtable*3, Class*3, Dictionary*1
        //coll2
        for (ParameterizedType typ : ptypes) {
            assertEquals(typ.getParameterizableClass().getIsStub(), typ.getIsStub());
        }
    }

    @Test
    public void testParameterTypeAsType() {
        Method gebb = detectFamixElement( Method.class, "getEntityByBinding");
        assertNotNull(gebb);
        assertSame(1, gebb.getParameters().size());

        Parameter bnd = firstElt(gebb.getParameters());
        assertNotNull(bnd);
        assertEquals("bnd", bnd.getName());

        Type b = bnd.getDeclaredType();
        assertNotNull(b);
        assertEquals("B", b.getName());
        assertSame(ParameterType.class, b.getClass());

        ContainerEntity cont = b.getContainer();
        assertNotNull(cont);
        assertEquals("Dictionary", cont.getName());
        assertSame(ParameterizableClass.class, cont.getClass());
    }

    @Test
    public void testMethodParameterArgumentTypes() {
        Method meth = detectFamixElement( Method.class, "ensureFamixEntity");
        assertEquals(3, meth.getParameters().size());
        for (Parameter param : meth.getParameters()) {
            if (param.getName().equals("fmxClass")) {
                Type classT = param.getDeclaredType();
                assertNotNull(classT);
                assertEquals("Class", classT.getName());
                assertEquals(ParameterizedType.class, classT.getClass());
                assertEquals(1, ((ParameterizedType)classT).getArguments().size());
                Type t = firstElt(((ParameterizedType)classT).getArguments());
                assertEquals("T", t.getName());
                assertSame(meth, t.getBelongsTo());
            }
            else if (param.getName().equals("bnd")) {
                Type b = param.getDeclaredType();
                assertNotNull(b);
                assertEquals("B", b.getName());
                assertSame(meth.getBelongsTo(), b.getBelongsTo());  // b defined in Dictionary class just as the method
            }
            else {
                assertEquals("name", param.getName());
            }
        }
    }

}
