package fr.inria.verveine.extractor.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.moosetechnology.model.famix.famixjavaentities.ContainerEntity;
import org.moosetechnology.model.famix.famixjavaentities.Method;
import org.moosetechnology.model.famix.famixjavaentities.Parameter;
import org.moosetechnology.model.famix.famixjavaentities.ParameterType;
import org.moosetechnology.model.famix.famixjavaentities.ParametricClass;
import org.moosetechnology.model.famix.famixjavaentities.ParametricInterface;
import org.moosetechnology.model.famix.famixjavaentities.ParametricMethod;
import org.moosetechnology.model.famix.famixjavaentities.Type;
import org.moosetechnology.model.famix.famixjavaentities.Class;
import org.moosetechnology.model.famix.famixjavaentities.Concretisation;
import org.moosetechnology.model.famix.famixtraits.TConcreteParameterType;
import org.moosetechnology.model.famix.famixtraits.TConcretisation;
import org.moosetechnology.model.famix.famixtraits.TNamedEntity;
import org.moosetechnology.model.famix.famixtraits.TParameter;

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
        java.util.HashMap.class
	};

	/**
	 * Array of all the java classes that are directly used in the Generics "project"
	 */
	protected final java.lang.Class<?> [] JAVA_INTERFACES_USED =
			new java.lang.Class<?> [] { 
		java.util.Map.class,
		java.util.List.class,
		java.util.Collection.class,
        java.util.HashMap.class,
        java.util.AbstractMap.class
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
    public void testInvocationOnConcretisation() {
    	ParametricClass classE = (ParametricClass)genericEntityNamed("E");
    	Method m = detectFamixElement(Method.class, "m");
    	assertNotNull(classE);
    	assertEquals(1, classE.getConcretisations().size());
    	ParametricClass concrete = (ParametricClass)firstElt(classE.getConcretisations()).getConcreteEntity();
    	assertEquals(1, concrete.getIncomingReferences().size());
    	assertEquals(1, concrete.getMethods().size());
    	
    }
    
    @Test
    public void testParameterTypeConcretisation() {
    	ParametricClass classB = (ParametricClass)genericEntityNamed("B");
    	assertNotNull(classB);
    	ParametricClass classC = (ParametricClass)genericEntityNamed("C");
    	assertNotNull(classC);
    	ParametricClass classD = (ParametricClass)genericEntityNamed("D");
    	assertNotNull(classD);
    	for (TConcretisation c : classC.getConcretisations()) {
    		int i= 0;
    		ParametricClass pc = (ParametricClass)c.getConcreteEntity();
    		assertEquals(2, pc.getConcreteParameters().size());
    		for(TConcreteParameterType p : pc.getConcreteParameters()) {
    			ParameterType genParam = (ParameterType) firstElt(p.getGeneric()).getGenericParameters(); 			
    			assertNotNull(genParam);
    			
    			if(i==0) {
    				assertEquals(firstElt(genParam.getGenericEntity()), classC);
    				ParametricClass genClass = (ParametricClass)firstElt(((ParameterType)p).getGenericEntity());
    				assertEquals(ParameterType.class, genParam.getClass());
    				assertTrue(genClass == classB || genClass == classD);
    			}else {
    				assertEquals(Class.class, p.getClass());
    			}
    			
    			i++;
    		}
    		
    	}
    	
    }

    @Test
    public void testParameterizableClass() {

        ParametricClass generic = null;
        
        Collection<ParametricClass> dicts = entitiesNamed(ParametricClass.class, "Dictionary");
        for(ParametricClass c : dicts) {
        	if(!c.getIsStub() && c.getGenericParameters().size() == 1 && firstElt(c.getGenericParameters()).getName().equals("B")) {
        		generic = c;
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

        assertEquals(1, generic.getGenericParameters().size());

        ParameterType dicoParam = detectFamixElement( ParameterType.class, "B");
        assertNotNull(dicoParam);
        assertEquals("B", dicoParam.getName());

        assertSame(generic, dicoParam.getTypeContainer());
        assertSame(dicoParam, firstElt(generic.getGenericParameters()));

        /* Collection<Object> is not seen as parameterizable by JDT */
//        ParametricInterface collec = detectFamixElement( ParametricInterface.class, "Collection");
//        assertNotNull(collec);
    }

	@Test
    public void testParameterizedType() {
        Method getx = detectFamixElement( Method.class, "getX");
        assertNotNull(getx);

        assertEquals(1, getx.getOutgoingReferences().size());
        ParametricClass refedArrList = (ParametricClass) (firstElt(getx.getOutgoingReferences())).getReferredType();
        assertNotNull(refedArrList);
        assertEquals("ArrayList", refedArrList.getName());

        ParametricClass arrList = (ParametricClass)genericEntityNamed( "ArrayList");
        assertNotNull(arrList);
        assertEquals(arrList, firstElt(refedArrList.getGenericEntity()).getGenericEntity());
        assertEquals(arrList.getParentPackage(), refedArrList.getParentPackage()); //getTypeContainer

        assertEquals(1, refedArrList.getConcreteParameters().size()); //concreteParameters
        assertEquals("ABC", ((TNamedEntity)firstElt(refedArrList.getConcreteParameters())).getName()); //concreteParameters
    }

    @Test
    public void testUseOfParameterizedClass() {
        ParametricClass arrList = (ParametricClass)genericEntityNamed( "ArrayList");
        assertEquals(3, arrList.getConcretisations().size()); // WrongInvocation.getX() ; Dictionnary.getEntityByName()
        for (TConcretisation concretisation : arrList.getConcretisations()) {
            ParametricClass paramed = (ParametricClass) concretisation.getConcreteEntity();
            if(firstElt(paramed.getConcreteParameters()).getName().equals("B")) {
            	assertEquals(0, paramed.getIncomingReferences().size());
            }else {
            	assertEquals(1, paramed.getIncomingReferences().size());
            	String refererName = ((TNamedEntity)firstElt(paramed.getIncomingReferences()).getReferencer()).getName();
                assertTrue(refererName.equals("getEntityByName") || refererName.equals("getX") );
            }
            
            
        }
    }

    @Test  // issue 960
    public void testStubStatusParameterizedTypes() {
        Collection<ParametricClass> ptypes = entitiesOfType( ParametricClass.class);

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
        ** IN OnlyReferenceToGeneric : 2
        - HashMap
        - AbstractMap
        */     

        for (ParametricClass typ : ptypes) {
        	if(!typ.getGenericEntity().isEmpty())
        		assertEquals(((Type)(firstElt(typ.getGenericEntity()).getGenericEntity())).getIsStub(), typ.getIsStub());
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
        assertSame(ParametricClass.class, cont.getClass());
    }

    @Test
    public void testMethodParameterArgumentTypes() {
        ParametricMethod meth = detectFamixElement( ParametricMethod.class, "ensureFamixEntity");
        assertEquals(3, meth.getParameters().size());
        
        for (TParameter tparam : meth.getParameters()) {
            Parameter param = (Parameter) tparam;
            if (param.getName().equals("fmxClass")) {
                Type classT = (Type) param.getDeclaredType();
                assertNotNull(classT);
                assertEquals("Class", classT.getName());
                assertEquals(ParametricClass.class, classT.getClass());
                assertEquals(1, ((ParametricClass)classT).getConcreteParameters().size());
                Type t = (Type) firstElt(((ParametricClass)classT).getConcreteParameters());
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
    
    @Test
    public void testParameterTypeInMethodParameter() {
        Method meth = detectFamixElement( Method.class, "parameterClass");
        ParametricClass c = (ParametricClass)meth.getParentType();
        assertEquals(1, meth.getParameters().size());
        
        Parameter param = (Parameter)firstElt(meth.getParameters());
        Type classT = (Type) param.getDeclaredType();
        assertNotNull(classT);
        assertEquals("ArrayList", classT.getName());
        assertEquals(ParametricClass.class, classT.getClass());
        assertEquals(1, ((ParametricClass)classT).getConcreteParameters().size());
        Type t = (Type) firstElt(((ParametricClass)classT).getConcreteParameters());
        assertEquals("B", t.getName());
        assertSame(c, t.getTypeContainer());
    }

    @Test
    public void testIteratorIsParametricInterface() {
        ParametricInterface interface1 = detectFamixElement( ParametricInterface.class, "Iterator");
        assertNotNull(interface1);
    }
    
    @Test
    public void testImplementationOfParametricInterface() {
    	Class classA = detectFamixElement( Class.class, "ClassA");
    	assertNotNull(classA);
    	
    	assertEquals(1, classA.getInterfaceImplementations().size());
    	ParametricInterface myInterface = (ParametricInterface) firstElt(classA.getInterfaceImplementations()).getMyInterface();
    	assertEquals(ParametricInterface.class, myInterface.getClass());
    	assertEquals(1, myInterface.getGenericEntity().size());
    	
    	
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
