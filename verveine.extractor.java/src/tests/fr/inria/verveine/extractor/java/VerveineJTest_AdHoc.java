/**
 * Copyright (c) 2010 Anquetil Nicolas
 */
package tests.fr.inria.verveine.extractor.java;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import fr.inria.verveine.core.Dictionary;
import fr.inria.verveine.core.VerveineUtilsForTests;
import fr.inria.verveine.core.gen.famix.Access;
import fr.inria.verveine.core.gen.famix.AnnotationInstance;
import fr.inria.verveine.core.gen.famix.AnnotationInstanceAttribute;
import fr.inria.verveine.core.gen.famix.AnnotationType;
import fr.inria.verveine.core.gen.famix.AnnotationTypeAttribute;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.BehaviouralEntity;
import fr.inria.verveine.core.gen.famix.CaughtException;
import fr.inria.verveine.core.gen.famix.ContainerEntity;
import fr.inria.verveine.core.gen.famix.DeclaredException;
import fr.inria.verveine.core.gen.famix.EnumValue;
import fr.inria.verveine.core.gen.famix.Invocation;
import fr.inria.verveine.core.gen.famix.LocalVariable;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.Parameter;
import fr.inria.verveine.core.gen.famix.ParameterType;
import fr.inria.verveine.core.gen.famix.ParameterizableClass;
import fr.inria.verveine.core.gen.famix.ParameterizedType;
import fr.inria.verveine.core.gen.famix.Reference;
import fr.inria.verveine.core.gen.famix.ThrownException;
import fr.inria.verveine.core.gen.famix.Type;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.VerveineJParser;

/**
 * @author Nicolas Anquetil
 * @since November 25, 2010
 *
 */
public class VerveineJTest_AdHoc extends VerveineJTest_Basic {

	public VerveineJTest_AdHoc() {
		super(/*system*/true);  // yes please, test that System and members are created correctly
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		new File(VerveineJParser.OUTPUT_FILE).delete();
		VerveineJParser parser = new VerveineJParser();
		repo = parser.getFamixRepo();
		parser.setOptions(new String[] {"test_src/ad_hoc"});
		parser.parse();
		parser.emitMSE(VerveineJParser.OUTPUT_FILE);
	}

	@ Test
	public void testConstructorInvocations() {
		Method meth = VerveineUtilsForTests.detectElement(repo, Method.class, "methodWithClassScope");
		assertNotNull(meth);

		// test outgoing invocation to constructor
		assertEquals(3, meth.getOutgoingInvocations().size());

		// test constructors
		assertEquals(2, VerveineUtilsForTests.listElements(repo, Method.class, "DefaultConstructor").size());
		for (Method m : VerveineUtilsForTests.listElements(repo, Method.class, "DefaultConstructor")) {
			int nbParam = m.getParameters().size();
			assertTrue( (nbParam == 0) || (nbParam == 1) );
			assertEquals(1, m.getIncomingInvocations().size());
			assertEquals(1, m.getOutgoingInvocations().size());
		}
		
		// test invocations' signatures
		for (Invocation invok : meth.getOutgoingInvocations()) {
			BehaviouralEntity invoked = invok.getCandidates().iterator().next();
			if (invoked.getName().equals("DefaultConstructor")) {
				assertEquals("DefaultConstructor()", invok.getSignature());
			}
			else if (invoked.getName().equals("JFrame")) {
				assertEquals("JFrame(\"My title\")", invok.getSignature());
			}
		}

		for (Method m : VerveineUtilsForTests.listElements(repo, Method.class, "DefaultConstructor")) {
			Invocation invok = m.getOutgoingInvocations().iterator().next();
			if (m.getParameters().size() == 0) {
				assertEquals("this(\"For testing\")", invok.getSignature());
			}
			else {
				assertEquals("super(why)", invok.getSignature());
			}
		}
	}

	@ Test
	public void testStubConstructor() {
		fr.inria.verveine.core.gen.famix.Class stubClass = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "JFrame");
		assertNotNull(stubClass);

		// test outgoing invocation to constructor
		assertEquals(1, stubClass.getMethods().size());

		Method stubConstructor = stubClass.getMethods().iterator().next();
	
		assertTrue(stubConstructor.getIsStub());
		assertEquals( "constructor", stubConstructor.getKind());
		
	}

	@Test
	public void testDictionary() {
		ParameterizableClass dico = null;
		for (ParameterizableClass d : VerveineUtilsForTests.listElements(repo, ParameterizableClass.class, "Dictionary")) {
			if (d.getBelongsTo().getName().equals(Dictionary.DEFAULT_PCKG_NAME)) {
				// note: For testing purposes class Dictionary<B> in ad_hoc is defined without "package" instruction, so it ends up in the default package
				dico = d;
				break;
			}
		}
		assertNotNull(dico);
		assertEquals(7, dico.getMethods().size());
		assertEquals(3, dico.getAttributes().size());

		for (Attribute a : dico.getAttributes()) {
			assertEquals(dico, a.getBelongsTo());
			Type t = a.getDeclaredType();
			assertEquals("Map", t.getName());
			assertEquals(ParameterizedType.class, t.getClass());
		}
	}

	@Test
	public void testStaticMembers() {
		Method meth = VerveineUtilsForTests.detectElement(repo, Method.class, "methodWithClassScope");
		assertNotNull(meth);
		assertTrue(meth.getHasClassScope());

		meth = VerveineUtilsForTests.detectElement(repo, Method.class, "methodWithInstanceScope");
		assertNotNull(meth);
		assertFalse(meth.getHasClassScope());

		Attribute att = VerveineUtilsForTests.detectElement(repo, Attribute.class, "FIELD_WITH_CLASS_SCOPE");
		assertNotNull(att);
		assertTrue(att.getHasClassScope());

		att = VerveineUtilsForTests.detectElement(repo, Attribute.class, "fieldWithInstanceScope");
		assertNotNull(att);
		assertFalse(att.getHasClassScope());
	}

	@Test
	public void testUnknownMethod() {
		Method meth = VerveineUtilsForTests.detectElement(repo, Method.class, "uniplementedMethod");
		assertNotNull(meth);
		
		assertEquals("uniplementedMethod(?,?)", meth.getSignature());
	}

	@Test
	public void testExceptions() {
		Method meth = VerveineUtilsForTests.detectElement(repo, Method.class, "lire");
		assertNotNull(meth);

		fr.inria.verveine.core.gen.famix.Class excepClass = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "ReadException");
		assertNotNull(excepClass);

		assertEquals(1, meth.getDeclaredExceptions().size());
		DeclaredException exD = meth.getDeclaredExceptions().iterator().next();
		assertSame(meth, exD.getDefiningMethod());
		assertSame(excepClass, exD.getExceptionClass());

		assertEquals(1, meth.getThrownExceptions().size());
		ThrownException exT = meth.getThrownExceptions().iterator().next();
		assertSame(meth, exT.getDefiningMethod());
		assertSame(excepClass, exT.getExceptionClass());

		excepClass = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "IOException");
		assertNotNull(excepClass);

		assertEquals(1,meth.getCaughtExceptions().size());
		CaughtException exC = meth.getCaughtExceptions().iterator().next();
		assertSame(meth, exC.getDefiningMethod());
		assertSame(excepClass, exC.getExceptionClass());
	}

	@Test
	public void testClassWithNoBindingCreatedAsType() {
		fr.inria.verveine.core.gen.famix.Type clazz = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Type.class, "BackingStoreException");
		assertNotNull(clazz);
		assertEquals(fr.inria.verveine.core.gen.famix.Class.class, clazz.getClass());
	}
	
	@Test
	public void testAnnotation() {
		AnnotationType getProp = VerveineUtilsForTests.detectElement(repo,AnnotationType.class, "GetProperty");
		assertNotNull(getProp);
		assertFalse(getProp.getIsStub());

		assertEquals(1, getProp.getAttributes().size());
		AnnotationTypeAttribute getAtt = (AnnotationTypeAttribute) getProp.getAttributes().iterator().next();
		assertEquals("value", getAtt.getName());
		assertEquals(4, getProp.getInstances().size());

		// Class annotation
		fr.inria.verveine.core.gen.famix.Class cl = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Serializer");
		assertEquals(1, cl.getAnnotationInstances().size());
		AnnotationInstance sw = cl.getAnnotationInstances().iterator().next();
		assertNotNull(sw);
		assertEquals("SuppressWarnings", sw.getAnnotationType().getName());
		assertSame(sw.getAnnotatedEntity(), cl);
		assertEquals(1, sw.getAttributes().size());
		AnnotationInstanceAttribute swVal = sw.getAttributes().iterator().next();
		assertNotNull(swVal);
		assertEquals("value", swVal.getAnnotationTypeAttribute().getName());
		assertEquals("serial", swVal.getValue());

		// Method annotations
		fr.inria.verveine.core.gen.famix.Class book = VerveineUtilsForTests.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Book");
		assertEquals(12, book.getMethods().size());
		for (Method meth : book.getMethods()) {
			Collection<AnnotationInstance> annInstances = meth.getAnnotationInstances();
			if (meth.getName().startsWith("get")) {
				assertEquals(1, annInstances.size());
				AnnotationInstance annInst = annInstances.iterator().next();
				assertSame(getProp, annInst.getAnnotationType());
				assertEquals(1, annInst.getAttributes().size());
				AnnotationInstanceAttribute getValInst = annInst.getAttributes().iterator().next();
				assertSame(getAtt, getValInst.getAnnotationTypeAttribute());
				
			}
			else if (meth.getName().startsWith("set")) {
				assertEquals(1, annInstances.size());
				AnnotationInstance annInst = annInstances.iterator().next();
				assertEquals("SetProperty", annInst.getAnnotationType().getName());
			}
			else {
				assertEquals(0, annInstances.size());
			}
		}

		// one Attribute with annotation
		assertEquals(6, book.getAttributes().size());
		for (Attribute att : book.getAttributes()) {
			if (att.getName().equals("time")) {
				assertEquals(1, att.getAnnotationInstances().size());
			}
			else {
				assertEquals(0, att.getAnnotationInstances().size());
			}
		}

	}

	@Test
	public void testClassVar() {
		Method meth = VerveineUtilsForTests.detectElement(repo, Method.class, "ensureFamixEntity");
		assertNotNull(meth);
		
		assertEquals(3, meth.getParameters().size());
		for (Parameter p : meth.getParameters()) {
			if (p.getName().equals("fmxClass")) {
				assertEquals(ParameterizedType.class, p.getDeclaredType().getClass());
				assertEquals("Class", p.getDeclaredType().getName());
			}
			else {
				assertTrue("Unknown parameter for ensureFamixEntity: "+p.getName(),
							p.getName().equals("name") || p.getName().equals("bnd") );
				break;
			}
		}
		
		assertEquals(2, meth.getAccesses().size());  // ImplicitVariable.class, mapBind
		boolean classFieldFound = false;
		for (Access acc : meth.getAccesses()) {
			if (acc.getTo().getName().equals("class")) {
				classFieldFound = true;
			}
		}
		assertTrue("ensureFamixEntity does not access <someClass>.class", classFieldFound);
	}

	@Test
	public void testParameterizableClass() {
		assertEquals(17, VerveineUtilsForTests.selectElementsOfType(repo, ParameterizableClass.class).size());	// Class,Comparable,List,ArrayList,AbstractList,AbstractCollection,Collection,Map,Iterable,Dictionary<B>,Hashtable,Dictionary<K,V>,LinkedList,AbstractSequentialList,Deque,Queue,Enum

		ParameterizableClass dico = null;
		for (ParameterizableClass d : VerveineUtilsForTests.listElements(repo, ParameterizableClass.class, "Dictionary")) {
			if (d.getBelongsTo().getName().equals(Dictionary.DEFAULT_PCKG_NAME)) {
				// note: For testing purposes class Dictionary<B> in ad_hoc is defined without "package" instruction, so it ends up in the default package
				dico = d;
				break;
			}
		}
		assertNotNull(dico);
		assertEquals("Dictionary", dico.getName());
		assertEquals(2, dico.getTypes().size());  // <B> , ImplicitVars
		for (Type t : dico.getTypes()) {
			String typName = t.getName();
			assertTrue(typName.equals("B") || typName.equals("ImplicitVars"));
		}

		assertEquals(1, dico.getParameters().size());

		ParameterType dicoParam = VerveineUtilsForTests.detectElement(repo, ParameterType.class, "B");
		assertNotNull(dicoParam);
		assertEquals("B", dicoParam.getName());
		
		assertSame(dico, dicoParam.getContainer());
		assertSame(dicoParam, dico.getParameters().iterator().next());

		/* Collection<Object> is not seen as parameterizable by JDT */
		ParameterizableClass collec = VerveineUtilsForTests.detectElement(repo, ParameterizableClass.class, "Collection");
		assertNotNull(collec);
	}

	@Test
	public void testParameterizedType() {
		fr.inria.verveine.core.gen.famix.Class arrList = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "ArrayList");
		assertNotNull(arrList);

		Method newDeck = VerveineUtilsForTests.detectElement(repo, Method.class, "newDeck");
		assertNotNull(newDeck);

		// newDeck() it makes a Reference to a Parameterized type
		ParameterizedType refedArrList = null;
		for (Reference r : newDeck.getOutgoingReferences()) {
			if (r.getTarget() instanceof ParameterizedType) {
				refedArrList = (ParameterizedType) r.getTarget();
				break;
			}
		}
		assertNotNull(refedArrList);
		assertEquals("ArrayList", refedArrList.getName());
		assertEquals(arrList, refedArrList.getParameterizableClass());
		assertEquals(arrList.getContainer(), refedArrList.getContainer());

		assertEquals(1, refedArrList.getArguments().size());
		assertEquals("Card", refedArrList.getArguments().iterator().next().getName());
		
		assertEquals(2, refedArrList.getIncomingReferences().size());
		for (Reference r : refedArrList.getIncomingReferences()) {
			String refererName = r.getFrom().getName(); 
			assertTrue(refererName.equals(newDeck.getName()) || refererName.equals(JavaDictionary.INIT_BLOCK_NAME));
		}
	}

	@Test
	public void testParameterTypeAsType() {
		Method gebb = VerveineUtilsForTests.detectElement(repo, Method.class, "getEntityByBinding");
		assertNotNull(gebb);
		assertSame(1, gebb.getParameters().size());
		
		Parameter bnd = gebb.getParameters().iterator().next();
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
		Method meth = VerveineUtilsForTests.detectElement(repo, Method.class, "ensureFamixEntity");
		assertEquals(3, meth.getParameters().size());
		for (Parameter param : meth.getParameters()) {
			if (param.getName().equals("fmxClass")) {
				Type classT = param.getDeclaredType();
				assertNotNull(classT);
				assertEquals("Class", classT.getName());
				assertEquals(ParameterizedType.class, classT.getClass());
				assertEquals(1, ((ParameterizedType)classT).getArguments().size());
				Type t = ((ParameterizedType)classT).getArguments().iterator().next();
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
	
	@Test
	public void testMethodLocalVariableArgumentTypes() {
		Method meth = VerveineUtilsForTests.detectElement(repo, Method.class, "getEntityByName");
		assertNotNull(meth);
		assertEquals(2, meth.getLocalVariables().size());
		for (LocalVariable var : meth.getLocalVariables()) {
			Type collec;
			if (var.getName().equals("ret")) {
				collec = var.getDeclaredType();
				assertNotNull(collec);
				assertEquals("Collection", collec.getName());
				assertEquals(ParameterizedType.class, collec.getClass());
				assertEquals(1, ((ParameterizedType)collec).getArguments().size());
				Type t = ((ParameterizedType)collec).getArguments().iterator().next();
				assertEquals("T", t.getName());
				assertSame(meth, t.getBelongsTo());
			}
			if (var.getName().equals("l_name")) {
				collec = var.getDeclaredType();
				assertNotNull(collec);
				assertEquals("Collection", collec.getName());
				assertEquals(ParameterizedType.class, collec.getClass());
				assertEquals(1, ((ParameterizedType)collec).getArguments().size());
				Type ne = ((ParameterizedType)collec).getArguments().iterator().next();
				assertEquals("NamedEntity", ne.getName());
			}
		}
	}

	@Test
	public void testMethodReturnArgumentTypes() {
		Method fmxMethod = VerveineUtilsForTests.detectElement(repo, Method.class, "getEntityByName");
		assertNotNull(fmxMethod);
		//assertEquals(1, fmxMethod.getDeclaredArgumentTypes().size());
		//assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxMethod.getDeclaredArgumentTypes().iterator().next());
	}

	@Test
	public void testEnumDecl() {
		fr.inria.verveine.core.gen.famix.Class javaLangEnum = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "Enum");
		assertNotNull(javaLangEnum);
		assertEquals("lang", javaLangEnum.getBelongsTo().getName());
		assertEquals(ParameterizableClass.class, javaLangEnum.getClass());
		
		fr.inria.verveine.core.gen.famix.Class card = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "Card");
		assertNotNull(card);

		fr.inria.verveine.core.gen.famix.Enum rk = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Enum.class, "Rank");
		assertNotNull(rk);
		assertEquals("Rank", rk.getName());
		assertEquals(13, rk.getValues().size());
		assertSame(card, rk.getBelongsTo());
		assertNotNull(rk.getSourceAnchor());
		assertEquals(1, rk.getSuperInheritances().size());
		Type rkSuper = rk.getSuperInheritances().iterator().next().getSuperclass();
		assertEquals(ParameterizedType.class, rkSuper.getClass());
		assertEquals(javaLangEnum, ((ParameterizedType)rkSuper).getParameterizableClass());

		EnumValue nine = VerveineUtilsForTests.detectElement(repo, EnumValue.class, "NINE");
		assertNotNull(nine);
		assertEquals("NINE", nine.getName());
		assertSame(rk, nine.getParentEnum());

		fr.inria.verveine.core.gen.famix.Enum st = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Enum.class, "Suit");
		assertNotNull(st);
		assertEquals("Suit", st.getName());
		assertEquals(1, st.getSuperInheritances().size());
		Type stSuper = st.getSuperInheritances().iterator().next().getSuperclass();
		assertEquals(ParameterizedType.class, stSuper.getClass());
		assertEquals(javaLangEnum, ((ParameterizedType)stSuper).getParameterizableClass());
		assertEquals(4, st.getValues().size());
		assertSame(VerveineUtilsForTests.detectElement(repo, Namespace.class, "ad_hoc"), st.getBelongsTo());

		EnumValue hrt = VerveineUtilsForTests.detectElement(repo, EnumValue.class, "HEARTS");
		assertNotNull(hrt);
		assertEquals("HEARTS", hrt.getName());
		assertSame(st, hrt.getParentEnum());

		assertEquals(3, card.getAttributes().size());
		for (Attribute a : card.getAttributes()) {
			if (a.getName().equals("rank")) {
				assertEquals(rk, a.getDeclaredType());
			}
			else if (a.getName().equals("suit")) {
				assertEquals(st, a.getDeclaredType());
			}
			else {
				assertEquals("protoDeck", a.getName());
			}
		}

		fr.inria.verveine.core.gen.famix.Enum pl = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Enum.class, "Planet");
		assertNotNull(pl);
		assertEquals("Planet", pl.getName());
		assertEquals(1, pl.getSuperInheritances().size());
		Type plSuper = pl.getSuperInheritances().iterator().next().getSuperclass();
		assertEquals(ParameterizedType.class, plSuper.getClass());
		assertEquals(javaLangEnum, ((ParameterizedType)plSuper).getParameterizableClass());
		assertSame(VerveineUtilsForTests.detectElement(repo, Namespace.class, "ad_hoc"), pl.getBelongsTo());
		assertEquals(8, pl.getValues().size());
		assertEquals(3, pl.getAttributes().size());
		assertEquals(6+2, pl.getMethods().size()); // 6 methods + <initializer> + implicit used: values()
	}

	@Test
	public void testEnumAccess() {
		fr.inria.verveine.core.gen.famix.Enum st = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Enum.class, "Suit");
		assertNotNull(st);
		assertEquals(4, st.getValues().size());
		boolean foundClubs = false;
		for (EnumValue v : st.getValues()) {
			if (v.getName().equals("CLUBS")) {
				foundClubs = true;
				assertEquals(1, v.numberOfIncomingAccesses());
				Access access = v.getIncomingAccesses().iterator().next();
				assertEquals("toString", access.getFrom().getName());
			}
		}
		assertTrue("Did not find CUBS EnumValue in Suit Enum", foundClubs);
		
		fr.inria.verveine.core.gen.famix.Enum pl = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Enum.class, "Planet");
		assertNotNull(pl);

		assertEquals(8, pl.getValues().size());
		for (EnumValue v : pl.getValues()) {
			if (v.getName().equals("EARTH")) {
				assertEquals(1, v.getIncomingAccesses().size());
			}
			else {
				assertEquals(0, v.getIncomingAccesses().size());
			}
		}

		assertEquals(3, pl.getAttributes().size());
		for (Attribute a : pl.getAttributes()) {
			if (a.getName().equals("G")) {
				assertEquals(1, a.getIncomingAccesses().size());
			}
			else if (a.getName().equals("radius")) {
				assertEquals(2, a.getIncomingAccesses().size());
			}
			else if (a.getName().equals("mass")) {
				assertEquals(3, a.getIncomingAccesses().size());
			}
			else {
				fail("Unknown attribute of Enum Planet: "+a.getName());
			}
		}

		assertEquals(6+2, pl.getMethods().size());  // see testEnumDecl()
		for (Method m : pl.getMethods()) {
			if ( m.getName().equals("Planet") || m.getName().equals("main") ) {
				assertEquals(0, m.getIncomingInvocations().size());
			}
			else if ( m.getName().equals("mass") || m.getName().equals("surfaceWeight") ||
					  m.getName().equals("values") || m.getName().equals("toString") ) {
				assertEquals(1, m.getIncomingInvocations().size());
			}
			else if (m.getName().equals("surfaceGravity")) {
				assertEquals(2, m.getIncomingInvocations().size());
			}
			else if (m.getName().equals("radius")) {
				assertEquals(3, m.getIncomingInvocations().size());
			}
			else if (m.getName().equals(JavaDictionary.INIT_BLOCK_NAME)) {
				assertEquals(0, m.getIncomingInvocations().size());
			}
			else {
				fail("Unknown method of Enum Planet: "+m.getName());
			}
		}
	}

	@Test
	public void testStaticInitializationBlock() {
		Collection<Method> l_meth = VerveineUtilsForTests.listElements(repo, Method.class, JavaDictionary.INIT_BLOCK_NAME);
		assertEquals(5, l_meth.size());
		String unknownParent = null;
		for (Method meth : l_meth) {
			assertEquals(JavaDictionary.INIT_BLOCK_NAME+"()", meth.getSignature());
			if (meth.getParentType().getName().equals("Card")) {
				assertEquals(5, meth.getOutgoingInvocations().size());
			}
			else if (meth.getParentType().getName().equals("Serializer")) {
				assertEquals(1, meth.getOutgoingInvocations().size());
			}
			else if (meth.getParentType().getName().equals("Book")) {
				assertEquals(1, meth.getOutgoingInvocations().size());
			}
			else if (meth.getParentType().getName().equals("Planet")) {
				assertEquals(0, meth.getOutgoingInvocations().size());
			}
			else if (meth.getParentType().getName().equals("DefaultConstructor")) {
				assertEquals(0, meth.getOutgoingInvocations().size());
			}
			else {
				unknownParent = meth.getParentType().getName();
			}
		}
		assertNull("Unknown class with an <Initializer> method: "+unknownParent, unknownParent);
	}
	
	@Test
	public void testWrongMethodOwner() {
		Method meth = VerveineUtilsForTests.detectElement(repo, Method.class, "methodWrongOwner");
		assertNotNull(meth);

		assertEquals(VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "SuperWrongOwner"), meth.getParentType());
	}
	
	@Test
	public void testModifiers() {
		Attribute attribute = VerveineUtilsForTests.detectElement(repo, Attribute.class, "privateFinalAttribute");
		assertNotNull(attribute);

		assertEquals(2, attribute.getModifiers().size());
		assertTrue(attribute.getModifiers().contains("private"));
		assertTrue(attribute.getModifiers().contains("final"));
	}

	@Test
	public void testMultipleSignatures() {
		fr.inria.verveine.core.gen.famix.Class throwable = VerveineUtilsForTests.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "Throwable");
		assertNotNull(throwable);
		assertEquals(3, throwable.getMethods().size()); // getMessage() in Dictionary, printStackTrace() & printStackTrace(PrintWriter) in MultipleSignatures

		Method regular =  VerveineUtilsForTests.detectElement(repo, Method.class, "callToRegularPrintStackTrace");
		assertNotNull(regular);
		assertEquals(1, regular.getOutgoingInvocations().size());
		
		Method withParam =  VerveineUtilsForTests.detectElement(repo, Method.class, "callToPrintStackTraceWithParam");
		assertNotNull(withParam);
		assertEquals(3, withParam.getOutgoingInvocations().size());  // printStackTrace(new PrintWriter(new StringWriter()))
	}
	
	@Test
	public void testAnnotParamIsClass(){
		Attribute time = VerveineUtilsForTests.detectElement(repo, Attribute.class, "time");
		assertNotNull(time);

		assertEquals(1, time.getAnnotationInstances().size());
		AnnotationInstance xmle = time.getAnnotationInstances().iterator().next();
		assertNotNull(xmle);
		assertEquals("XmlElement", xmle.getAnnotationType().getName());
		assertSame(xmle.getAnnotatedEntity(), time);
		assertEquals(3, xmle.getAttributes().size());

		for (AnnotationInstanceAttribute aia : xmle.getAttributes()) {
			if (aia.getAnnotationTypeAttribute().getName().equals("type")) {
				assertEquals("String.class", aia.getValue());
			}
			else {
				assertTrue( aia.getAnnotationTypeAttribute().getName().equals("name") ||
						    aia.getAnnotationTypeAttribute().getName().equals("required"));
			}
		}
	}

	@Test
	public void testInvokSelfNoBinding(){
		Method seri = VerveineUtilsForTests.detectElement(repo, Method.class, "serialize");
		assertNotNull(seri);
		ContainerEntity owner = seri.getBelongsTo(); 
		assertEquals("Serializer", owner.getName());  // just checking
		
		for (Invocation invok : seri.getOutgoingInvocations()) {
			BehaviouralEntity invoked = invok.getCandidates().iterator().next();
			if (invoked.getName().equals("serializeProperty")) {
				assertEquals(owner, invoked.getBelongsTo());
			}
		}
	}
	
}