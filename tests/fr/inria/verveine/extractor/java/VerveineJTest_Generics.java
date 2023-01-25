package fr.inria.verveine.extractor.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.moosetechnology.model.famix.famixjavaentities.ContainerEntity;
import org.moosetechnology.model.famix.famixjavaentities.Method;
import org.moosetechnology.model.famix.famixjavaentities.Parameter;
import org.moosetechnology.model.famix.famixjavaentities.ParameterType;
import org.moosetechnology.model.famix.famixjavaentities.ParameterizableClass;
import org.moosetechnology.model.famix.famixjavaentities.ParameterizableInterface;
import org.moosetechnology.model.famix.famixjavaentities.ParameterizedType;
import org.moosetechnology.model.famix.famixjavaentities.Type;
import org.moosetechnology.model.famix.famixtraits.TNamedEntity;
import org.moosetechnology.model.famix.famixtraits.TParameter;
import org.moosetechnology.model.famix.famixtraits.TParameterizedType;
import org.moosetechnology.model.famix.famixtraits.TType;

public class VerveineJTest_Generics extends VerveineJTest_Basic {

	/**
	 * Array of all the java classes that are directly used in the Generics "project"
	 */
	protected static final java.lang.Class<?> [] JAVA_CLASSES_USED =
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
        parser = new VerveineJParser();
        repo = parser.getFamixRepo();
        parser.configure( new String[] {"test_src/generics"});
        parser.parse();
    }

    @Test
    public void testParameterizableClass() {
        assertEquals( allParameterizedClasses().count() + 1, entitiesOfType( ParameterizableClass.class).size());   // Java generics + Dictionary
        assertEquals(allParameterizedInterfaces().count(), entitiesOfType( ParameterizableInterface.class).size());

        ParameterizableClass generic = null;
        for (ParameterizableClass g : entitiesNamed( ParameterizableClass.class, "Dictionary")) {
            if (((TNamedEntity) g.getTypeContainer()).getName().equals(JavaDictionary.DEFAULT_PCKG_NAME)) {
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

	@Test
    public void testParameterizedType() {
        Method getx = detectFamixElement( Method.class, "getX");
        assertNotNull(getx);

        assertEquals(1, getx.getOutgoingReferences().size());
        ParameterizedType refedArrList = (ParameterizedType) (firstElt(getx.getOutgoingReferences())).getReferredType();
        assertNotNull(refedArrList);
        assertEquals("ArrayList", refedArrList.getName());

        org.moosetechnology.model.famix.famixjavaentities.Class arrList = detectFamixElement(org.moosetechnology.model.famix.famixjavaentities.Class.class, "ArrayList");
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

        /*
        - ArrayList<ABC> Repository.List<X>()
        - List<X> Repository.List<X>()
        - Map<B,NamedEntity> Dictionary.mapBind
        - Map<String,Collection<NamedEntity>> Dictionary.mapName
        - Map<NamedEntity,ImplicitVars> Dictionary.mapImpVar
        - Hashtable<B,NamedEntity>() Dictionary.Dictionary().mapBind
        - Hashtable<String,Collection<NamedEntity>> Dictionary.Dictionary().mapName
        - Hashtable<NamedEntity,ImplicitVar> Dictionary.Dictionary().mapImpVar
        - Collection<T> Dictionary.getEntityByName()
        - Class<T> Dictionary.getEntityByName().fmxClass
        - ArrayList<T> Dictionary.getEntityByName().ret
        - Collection<NamedEntity> Dictionary.getEntityByName().l_name
        - Class<T> Dictionary.createFamixEntity().fmxClass
        - Class<T> Dictionary.ensureFamixEntity().fmxClass
        - Dictionary<String> Repository.dico
		- java.util.Dictionary<> Hashtable
        - AbstractList<> ArrayList
        - java.util.AbstractCollection<> java.util.AbstractList
        */     
        assertEquals(18, ptypes.size());

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

    	// UTILITIES --------------------------------------------------

    private Collection<java.lang.Class<?>> allInterfaces() {
		Set<java.lang.Class<?>> allInterfaces = (Set<java.lang.Class<?>>) allInterfacesFromClasses(JAVA_CLASSES_USED);
		
		for (java.lang.Class<?> javaClass : JAVA_INTERFACES_USED) {
			allInterfaces.addAll( allJavaInterfaces( javaClass).flattenToCollection());
		}
	
		return allInterfaces;
	}

	private Stream<java.lang.Class<?>> allParameterizedInterfaces() {
		return allInterfaces().stream().filter( (e) -> e.getTypeParameters().length > 0);
	}

	private Stream<java.lang.Class<?>> allParameterizedClasses() {
		return allJavaSuperClasses(JAVA_CLASSES_USED).stream().filter( (e) -> e.getTypeParameters().length > 0);
	}

}
