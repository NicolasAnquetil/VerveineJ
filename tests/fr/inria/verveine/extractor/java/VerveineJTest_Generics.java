package fr.inria.verveine.extractor.java;

import org.junit.Before;
import org.junit.Test;
import org.moosetechnology.model.famixjava.famixjavaentities.*;
import org.moosetechnology.model.famixjava.famixtraits.TNamedEntity;
import org.moosetechnology.model.famixjava.famixtraits.TParameter;
import org.moosetechnology.model.famixjava.famixtraits.TParameterizedType;
import org.moosetechnology.model.famixjava.famixtraits.TType;

import java.io.File;
import java.lang.Exception;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class VerveineJTest_Generics extends VerveineJTest_Basic {

	/**
	 * Array of all the java classes that are directly used in the Generics "project"
	 */
	protected final java.lang.Class<?> [] JAVA_CLASSES_USED =
			new java.lang.Class<?> [] { 
		java.lang.String.class,
		java.util.Hashtable.class,
		java.util.ArrayList.class,
		java.lang.Class.class,
		java.lang.System.class,
	};

	/**
	 * Array of all the java classes that are directly used in the Generics "project"
	 */
	protected final java.lang.Class<?> [] JAVA_INTERFACES_USED =
			new java.lang.Class<?> [] { 
		java.util.Map.class,
		java.util.List.class,
		java.util.Collection.class,
	};

		
    public VerveineJTest_Generics() {
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
        parser.configure( new String[] {"test_src/generics"});
        parser.parse();
    }

    @Test
    public void testParameterizableClass() {
    	long nbJavaGenerics = allClasses().stream().filter( (e) -> e.getTypeParameters().length > 0).count();
        assertEquals( nbJavaGenerics + 1, entitiesOfType( ParameterizableClass.class).size());   // Java generics + Dictionary

        nbJavaGenerics = allInterfaces().stream().filter( (e) -> e.getTypeParameters().length > 0).count();
        assertEquals(nbJavaGenerics, entitiesOfType( ParameterizableInterface.class).size());

        ParameterizableClass generic = null;
        for (ParameterizableClass g : entitiesNamed( ParameterizableClass.class, "Dictionary")) {
            if (((TNamedEntity) g.getTypeContainer()).getName().equals(AbstractDictionary.DEFAULT_PCKG_NAME)) {
                // note: For testing purposes class Dictionary<B> in ad_hoc is defined without "package" instruction, so it ends up in the default package
                generic = g;
                break;
            }
        }
        assertNotNull(generic);
        assertEquals("Dictionary", generic.getName());
        assertEquals(2, generic.getTypes().size());  // <B> , ImplicitVars
        for (TType t : generic.getTypes()) {
            String typName = ((TNamedEntity)t).getName();
            assertTrue(typName.equals("B") || typName.equals("ImplicitVars"));
        }

        assertEquals(1, generic.getParameters().size());

        ParameterType dicoParam = detectFamixElement( ParameterType.class, "B");
        assertNotNull(dicoParam);
        assertEquals("B", dicoParam.getName());

        assertSame(generic, dicoParam.getTypeContainer());
        assertSame(dicoParam, firstElt(generic.getParameters()));

        /* Collection<Object> is not seen as parameterizable by JDT */
        ParameterizableInterface collec = detectFamixElement( ParameterizableInterface.class, "Collection");
        assertNotNull(collec);
    }

    private Collection<java.lang.Class<?>> allClasses() {
		Set<java.lang.Class<?>> allClasses = new HashSet<>();
		
		for (java.lang.Class<?> javaClass : JAVA_CLASSES_USED) {
			allClasses.addAll( allJavaSuperClasses( javaClass));
		}
	
		return allClasses;
	}

    private Collection<java.lang.Class<?>> allInterfaces() {
		Set<java.lang.Class<?>> allInterfaces = new HashSet<>();
		
		for (java.lang.Class<?> javaClass : JAVA_CLASSES_USED) {
			
			List<java.lang.Class<?>> intrfcFromClasses = allJavaInterfaces( javaClass).flattenToCollection().stream().filter( (e) -> e.isInterface()).toList();
			allInterfaces.addAll( intrfcFromClasses);
		}

		for (java.lang.Class<?> javaClass : JAVA_INTERFACES_USED) {
			allInterfaces.addAll( allJavaInterfaces( javaClass).flattenToCollection());
		}
	
		return allInterfaces;
	}

	@Test
    public void testParameterizedType() {
        Method getx = detectFamixElement( Method.class, "getX");
        assertNotNull(getx);

        assertEquals(1, getx.getOutgoingReferences().size());
        ParameterizedType refedArrList = (ParameterizedType) (firstElt(getx.getOutgoingReferences())).getReferredType();
        assertNotNull(refedArrList);
        assertEquals("ArrayList", refedArrList.getName());

        org.moosetechnology.model.famixjava.famixjavaentities.Class arrList = detectFamixElement(org.moosetechnology.model.famixjava.famixjavaentities.Class.class, "ArrayList");
        assertNotNull(arrList);
        assertEquals(arrList, refedArrList.getParameterizableClass());
        assertEquals(arrList.getContainer(), refedArrList.getContainer());

        assertEquals(1, refedArrList.getArguments().size());
        assertEquals("ABC", ((TNamedEntity)firstElt(refedArrList.getArguments())).getName());
    }

    @Test
    public void testUseOfParameterizedClass() {
        ParameterizableClass arrList = detectFamixElement( ParameterizableClass.class, "ArrayList");
        assertEquals(2, arrList.getParameterizedTypes().size()); // WrongInvocation.getX() ; Dictionnary.getEntityByName()
        for (TParameterizedType tparamed : arrList.getParameterizedTypes()) {
            ParameterizedType paramed = (ParameterizedType) tparamed;
            assertEquals(1, paramed.getIncomingReferences().size());
            String refererName = ((TNamedEntity)firstElt(paramed.getIncomingReferences()).getReferencer()).getName();
            assertTrue(refererName.equals("getEntityByName") || refererName.equals("getX") );
        }
    }

    @Test  // issue 960
    public void testStubStatusParameterizedTypes() {
        Collection<ParameterizedType> ptypes = entitiesOfType( ParameterizedType.class);
        int numberParameterizedTypes = 25;
        assertEquals(numberParameterizedTypes, ptypes.size());  // List*1, ArrayList*2, Map*3, Collection<NamedEntity>, Collection<T>, Hashtable*3, Class*3, Dictionary*1 + all stub superclasses
        //coll2
        for (ParameterizedType typ : ptypes) {
            assertEquals(((Type)typ.getParameterizableClass()).getIsStub(), typ.getIsStub());
        }
    }

    @Test
    public void testParameterTypeAsType() {
        Method gebb = detectFamixElement( Method.class, "getEntityByBinding");
        assertNotNull(gebb);
        assertSame(1, gebb.getParameters().size());

        Parameter bnd = (Parameter) firstElt(gebb.getParameters());
        assertNotNull(bnd);
        assertEquals("bnd", bnd.getName());

        Type b = (Type) bnd.getDeclaredType();
        assertNotNull(b);
        assertEquals("B", b.getName());
        assertSame(ParameterType.class, b.getClass());

        ContainerEntity cont = (ContainerEntity) b.getTypeContainer();
        assertNotNull(cont);
        assertEquals("Dictionary", cont.getName());
        assertSame(ParameterizableClass.class, cont.getClass());
    }

    @Test
    public void testMethodParameterArgumentTypes() {
        Method meth = detectFamixElement( Method.class, "ensureFamixEntity");
        assertEquals(3, meth.getParameters().size());
        for (TParameter tparam : meth.getParameters()) {
            Parameter param = (Parameter) tparam;
            if (param.getName().equals("fmxClass")) {
                Type classT = (Type) param.getDeclaredType();
                assertNotNull(classT);
                assertEquals("Class", classT.getName());
                assertEquals(ParameterizedType.class, classT.getClass());
                assertEquals(1, ((ParameterizedType)classT).getArguments().size());
                Type t = (Type) firstElt(((ParameterizedType)classT).getArguments());
                assertEquals("T", t.getName());
                assertSame(meth, t.getTypeContainer());
            }
            else if (param.getName().equals("bnd")) {
                Type b = (Type) param.getDeclaredType();
                assertNotNull(b);
                assertEquals("B", b.getName());
                assertSame(meth.getParentType(), b.getTypeContainer());  // b defined in Dictionary class just as the method
            }
            else {
                assertEquals("name", param.getName());
            }
        }
    }

}
