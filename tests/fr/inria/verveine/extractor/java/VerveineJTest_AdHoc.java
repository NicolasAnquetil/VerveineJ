/**
 * Copyright (c) 2010 Anquetil Nicolas
 */
package fr.inria.verveine.extractor.java;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import ch.akuhn.fame.Repository;
import org.junit.Before;
import org.junit.Test;

import eu.synectique.verveine.core.Dictionary;
import eu.synectique.verveine.core.VerveineUtilsForTests;
import eu.synectique.verveine.core.gen.famix.Access;
import eu.synectique.verveine.core.gen.famix.AnnotationInstance;
import eu.synectique.verveine.core.gen.famix.AnnotationInstanceAttribute;
import eu.synectique.verveine.core.gen.famix.AnnotationType;
import eu.synectique.verveine.core.gen.famix.AnnotationTypeAttribute;
import eu.synectique.verveine.core.gen.famix.Attribute;
import eu.synectique.verveine.core.gen.famix.BehaviouralEntity;
import eu.synectique.verveine.core.gen.famix.CaughtException;
import eu.synectique.verveine.core.gen.famix.ContainerEntity;
import eu.synectique.verveine.core.gen.famix.DeclaredException;
import eu.synectique.verveine.core.gen.famix.EnumValue;
import eu.synectique.verveine.core.gen.famix.IndexedFileAnchor;
import eu.synectique.verveine.core.gen.famix.Invocation;
import eu.synectique.verveine.core.gen.famix.LocalVariable;
import eu.synectique.verveine.core.gen.famix.Method;
import eu.synectique.verveine.core.gen.famix.Namespace;
import eu.synectique.verveine.core.gen.famix.Parameter;
import eu.synectique.verveine.core.gen.famix.ParameterType;
import eu.synectique.verveine.core.gen.famix.ParameterizableClass;
import eu.synectique.verveine.core.gen.famix.ParameterizedType;
import eu.synectique.verveine.core.gen.famix.Reference;
import eu.synectique.verveine.core.gen.famix.ThrownException;
import eu.synectique.verveine.core.gen.famix.Type;

/**
 * @author Nicolas Anquetil
 * @since November 25, 2010
 *
 */
public class VerveineJTest_AdHoc {

	protected Repository repo;
	protected VerveineJParser parser;

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
	public void testLambdaParameter() {
		parse(new String[] {"test_src/ad_hoc/WithLambda.java"});

		Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "WithLambda");
		assertNotNull(meth);

		LocalVariable col = null;
		LocalVariable o = null;
		assertEquals(2, meth.getLocalVariables().size());
		for (LocalVariable lvar : meth.getLocalVariables()) {
			if (lvar.getName().equals("col")) {
				col = lvar;
			}
			else if (lvar.getName().equals("o")) {
				o = lvar;
			}
			else {
				fail("Unknown local variable:" + lvar.getName());
			}
		}
		assertNotNull(col);
		assertNotNull(o);
	}

	@ Test
	public void testConstructorInvocations() {
		parse(new String[] {"test_src/ad_hoc/DefaultConstructor.java", "test_src/ad_hoc/InvokWithFullPath.java"});

		Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "methodWithClassScope");
		assertNotNull(meth);

		// test outgoing invocation to constructor
		Collection<Invocation> methOutgoingInvocations = meth.getOutgoingInvocations();
		assertEquals(3, methOutgoingInvocations.size());

		// test invocations' signatures
		for (Invocation invok : methOutgoingInvocations) {
			BehaviouralEntity invoked = invok.getCandidates().iterator().next();
			assertTrue( "Unexpected invoked signature: "+invoked.getSignature(),
					invok.getSignature().equals("DefaultConstructor()")
							|| invok.getSignature().equals("JFrame(\"My title\")")
							|| invok.getSignature().equals("methodWithInstanceScope()"));
		}

		// test constructors
		Collection<Method> defaultContructors = VerveineUtilsForTests.listFamixElements(repo, Method.class, "DefaultConstructor");
		assertEquals(2, defaultContructors.size());
		for (Method m : defaultContructors) {
			int nbParam = m.getParameters().size();
			assertTrue( (nbParam == 0) || (nbParam == 1) );
			assertEquals(1, m.getIncomingInvocations().size());
			assertEquals(1, m.getOutgoingInvocations().size());
		}

		for (Method m : defaultContructors) {
			Invocation invok = m.getOutgoingInvocations().iterator().next();
			if (m.getParameters().size() == 0) {
				assertEquals("this(\"For testing\")", invok.getSignature());
			}
			else {
				assertEquals("super(why)", invok.getSignature());
			}
		}

		// get calling method in InvokWithFullPath
		eu.synectique.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "InvokWithFullPath");
		meth = clazz.getMethods().iterator().next();

		// get called method in InvokWithFullPath
		methOutgoingInvocations = meth.getOutgoingInvocations();
		assertEquals(1, methOutgoingInvocations.size());
        Invocation invok = methOutgoingInvocations.iterator().next();
		assertEquals("Book(\"The Monster Book of Monsters\",\"Hagrid\")", invok.getSignature());
	}

	@ Test
	public void testStubConstructor() {
		parse(new String[] {"test_src/ad_hoc/DefaultConstructor.java"});

		eu.synectique.verveine.core.gen.famix.Class stubClass = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "JFrame");
		assertNotNull(stubClass);

		// test outgoing invocation to constructor
		assertEquals(1, stubClass.getMethods().size());

		Method stubConstructor = stubClass.getMethods().iterator().next();
	
		assertTrue(stubConstructor.getIsStub());
		assertEquals( "constructor", stubConstructor.getKind());
		
	}

	@Test
	public void testDictionary() {
		parse(new String[] {"test_src/ad_hoc/Dictionary.java"});

		ParameterizableClass dico = null;
		for (ParameterizableClass d : VerveineUtilsForTests.listFamixElements(repo, ParameterizableClass.class, "Dictionary")) {
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
		parse(new String[] {"test_src/ad_hoc/DefaultConstructor.java"});

		Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "methodWithClassScope");
		assertNotNull(meth);
		assertTrue(meth.getHasClassScope());

		meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "methodWithInstanceScope");
		assertNotNull(meth);
		assertFalse(meth.getHasClassScope());

		Attribute att = VerveineUtilsForTests.detectFamixElement(repo, Attribute.class, "FIELD_WITH_CLASS_SCOPE");
		assertNotNull(att);
		assertTrue(att.getHasClassScope());

		att = VerveineUtilsForTests.detectFamixElement(repo, Attribute.class, "fieldWithInstanceScope");
		assertNotNull(att);
		assertFalse(att.getHasClassScope());
	}

	@Test
	public void testUnknownMethod() {
		parse(new String[] {"test_src/ad_hoc/Dictionary.java"});

		Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "uniplementedMethod");
		assertNotNull(meth);
		
		assertEquals("uniplementedMethod(?,?)", meth.getSignature());
	}

	@Test
	public void testExceptions() {
		parse(new String[] {"test_src/ad_hoc/ReadClient.java", "test_src/ad_hoc/ReadException.java"});

		Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "lire");
		assertNotNull(meth);

		eu.synectique.verveine.core.gen.famix.Class excepClass = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "ReadException");
		assertNotNull(excepClass);

		assertEquals(1, meth.getDeclaredExceptions().size());
		DeclaredException exD = meth.getDeclaredExceptions().iterator().next();
		assertSame(meth, exD.getDefiningMethod());
		assertSame(excepClass, exD.getExceptionClass());

		assertEquals(1, meth.getThrownExceptions().size());
		ThrownException exT = meth.getThrownExceptions().iterator().next();
		assertSame(meth, exT.getDefiningMethod());
		assertSame(excepClass, exT.getExceptionClass());

		excepClass = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "IOException");
		assertNotNull(excepClass);

		assertEquals(1,meth.getCaughtExceptions().size());
		CaughtException exC = meth.getCaughtExceptions().iterator().next();
		assertSame(meth, exC.getDefiningMethod());
		assertSame(excepClass, exC.getExceptionClass());
	}

	@Test
	public void testClassWithNoBindingCreatedAsType() {
		parse(new String[] {"test_src/ad_hoc/Example.java"});

		eu.synectique.verveine.core.gen.famix.Type clazz = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Type.class, "BackingStoreException");
		assertNotNull(clazz);
		assertEquals(eu.synectique.verveine.core.gen.famix.Class.class, clazz.getClass());
	}
	
	@Test
	public void testAnnotation() {
		parse(new String[] {"test_src/ad_hoc/annotations"});

		AnnotationType getProp = VerveineUtilsForTests.detectFamixElement(repo,AnnotationType.class, "GetProperty");
		assertNotNull(getProp);
		assertFalse(getProp.getIsStub());

		assertEquals(1, getProp.getAttributes().size());
		AnnotationTypeAttribute getAtt = (AnnotationTypeAttribute) getProp.getAttributes().iterator().next();
		assertEquals("value", getAtt.getName());
		assertEquals(4, getProp.getInstances().size());

		// Class annotation
		eu.synectique.verveine.core.gen.famix.Class cl = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "Serializer");
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
		eu.synectique.verveine.core.gen.famix.Class book = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "Book");
		Collection<Method> bookMethods = book.getMethods();
		assertEquals(12, bookMethods.size());
		for (Method meth : bookMethods) {
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
		Collection<Attribute> bookAttributes = book.getAttributes();
		assertEquals(6, bookAttributes.size());
		for (Attribute att : bookAttributes) {
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
		parse(new String[] {"test_src/ad_hoc/Dictionary.java"});

		Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "ensureFamixEntity");
		assertNotNull(meth);

		// might as well do some tests on the method itself
		// not very unit-testing, but it's some more tests
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

		// here start the really intended tests
		assertEquals(2, meth.getAccesses().size());  // only 2 non-local variable accessed:  ImplicitVariable.class, Dictionary.mapBind
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
		parse(new String[] {"test_src/ad_hoc/annotations", "test_src/ad_hoc/Card.java", "test_src/ad_hoc/WrongInvocation.java", "test_src/ad_hoc/Dictionary.java"});

		assertEquals(9, VerveineUtilsForTests.selectElementsOfType(repo, ParameterizableClass.class).size());
		// WrongInvocation -> List<X>, ArrayList<X>
		// Dictionary -> Dictionary<X>, Map<X,Y>, Hashtable<X,Y>, Collection<X>, Class<X>, LinkedList<X>
		// Card -> List<X>, ArrayList<X>
		// XmlElement -> Class<X>
		// -> List, ArrayList, Class, Collection, Map, Hashtable, Dictionary, LinkedList
		// + Enum, but why ?
		// previous version was going up the inheritance hierarchy for stubs. No longer the case
		// these classes are no longer created: AbstractStringBuilder,FilterOutputStream,OutputStream,Comparable,Serializable,Flushable,Appendable,CharSequence,Closeable, +(java7)AutoCloseable}

		ParameterizableClass generic = null;
		for (ParameterizableClass g : VerveineUtilsForTests.listFamixElements(repo, ParameterizableClass.class, "Dictionary")) {
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

		ParameterType dicoParam = VerveineUtilsForTests.detectFamixElement(repo, ParameterType.class, "B");
		assertNotNull(dicoParam);
		assertEquals("B", dicoParam.getName());
		
		assertSame(generic, dicoParam.getContainer());
		assertSame(dicoParam, generic.getParameters().iterator().next());

		/* Collection<Object> is not seen as parameterizable by JDT */
		ParameterizableClass collec = VerveineUtilsForTests.detectFamixElement(repo, ParameterizableClass.class, "Collection");
		assertNotNull(collec);
	}

	@Test
	public void testParameterizedType() {
		parse(new String[] {"test_src/ad_hoc/Card.java"});

		eu.synectique.verveine.core.gen.famix.Class arrList = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "ArrayList");
		assertNotNull(arrList);

		Method newDeck = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "newDeck");
		assertNotNull(newDeck);

		// recover reference to ArrayList<Card> in newDeck()
		ParameterizedType refedArrList = null;
		assertEquals(1, newDeck.getOutgoingReferences().size());
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
		
		assertEquals(3, refedArrList.getIncomingReferences().size()); // Card.newDeck(), Card.INIT_BLOCK_NAME, WrongInvocation.getX()
		for (Reference r : refedArrList.getIncomingReferences()) {
			String refererName = r.getFrom().getName(); 
			assertTrue(refererName.equals("newDeck") || refererName.equals("getX") || refererName.equals(JavaDictionary.INIT_BLOCK_NAME));
		}
	}

	@Test  // issue 960
	public void testStubStatusParameterizedTypes() {
		parse(new String[] {"test_src/ad_hoc/Card.java"});

		Collection<ParameterizedType> ptypes = VerveineUtilsForTests.selectElementsOfType(repo, ParameterizedType.class);
		assertEquals(4,ptypes.size());  // Suit, Rank, List, ArrayList
		for (ParameterizedType typ : ptypes) {
			assertEquals(typ.getParameterizableClass().getIsStub(), typ.getIsStub());
		}
	}

	@Test
	public void testParameterTypeAsType() {
		parse(new String[] {"test_src/ad_hoc/Dictionary.java"});

		Method gebb = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "getEntityByBinding");
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
		parse(new String[] {"test_src/ad_hoc/Dictionary.java"});

		Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "ensureFamixEntity");
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
		parse(new String[] {"test_src/ad_hoc/Dictionary.java"});

		Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "getEntityByName");
		assertNotNull(meth);
		assertEquals(3, meth.getLocalVariables().size());
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
		parse(new String[] {"test_src/ad_hoc/Dictionary.java"});

		Method fmxMethod = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "getEntityByName");
		assertNotNull(fmxMethod);
		//assertEquals(1, fmxMethod.getDeclaredArgumentTypes().size());
		//assertSame(TestVerveineUtils.detectElement(repo, Type.class, "T"), fmxMethod.getDeclaredArgumentTypes().iterator().next());
	}

	@Test
	public void testEnumDecl() {
		parse(new String[] {"test_src/ad_hoc/Card.java", "test_src/ad_hoc/Planet.java"});

		eu.synectique.verveine.core.gen.famix.Class javaLangEnum = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "Enum");
		assertNotNull(javaLangEnum);
		assertEquals("lang", javaLangEnum.getBelongsTo().getName());
		assertEquals(ParameterizableClass.class, javaLangEnum.getClass());
		
		eu.synectique.verveine.core.gen.famix.Class card = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "Card");
		assertNotNull(card);

		eu.synectique.verveine.core.gen.famix.Enum rk = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Enum.class, "Rank");
		assertNotNull(rk);
		assertEquals("Rank", rk.getName());
		assertEquals(13, rk.getValues().size());
		assertSame(card, rk.getBelongsTo());
		assertNotNull(rk.getSourceAnchor());
		assertEquals(1, rk.getSuperInheritances().size());
		Type rkSuper = rk.getSuperInheritances().iterator().next().getSuperclass();
		assertEquals(ParameterizedType.class, rkSuper.getClass());
		assertEquals(javaLangEnum, ((ParameterizedType)rkSuper).getParameterizableClass());

		EnumValue nine = VerveineUtilsForTests.detectFamixElement(repo, EnumValue.class, "NINE");
		assertNotNull(nine);
		assertEquals("NINE", nine.getName());
		assertSame(rk, nine.getParentEnum());

		eu.synectique.verveine.core.gen.famix.Enum st = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Enum.class, "Suit");
		assertNotNull(st);
		assertEquals("Suit", st.getName());
		assertEquals(1, st.getSuperInheritances().size());
		Type stSuper = st.getSuperInheritances().iterator().next().getSuperclass();
		assertEquals(ParameterizedType.class, stSuper.getClass());
		assertEquals(javaLangEnum, ((ParameterizedType)stSuper).getParameterizableClass());
		assertEquals(4, st.getValues().size());
		assertSame(VerveineUtilsForTests.detectFamixElement(repo, Namespace.class, "ad_hoc"), st.getBelongsTo());

		EnumValue hrt = VerveineUtilsForTests.detectFamixElement(repo, EnumValue.class, "HEARTS");
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

		eu.synectique.verveine.core.gen.famix.Enum pl = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Enum.class, "Planet");
		assertNotNull(pl);
		assertEquals("Planet", pl.getName());
		assertEquals(1, pl.getSuperInheritances().size());
		Type plSuper = pl.getSuperInheritances().iterator().next().getSuperclass();
		assertEquals(ParameterizedType.class, plSuper.getClass());
		assertEquals(javaLangEnum, ((ParameterizedType)plSuper).getParameterizableClass());
		assertSame(VerveineUtilsForTests.detectFamixElement(repo, Namespace.class, "ad_hoc"), pl.getBelongsTo());
		assertEquals(8, pl.getValues().size());
		assertEquals(4, pl.getAttributes().size());
		assertEquals(7+2, pl.getMethods().size()); // 7 methods + <initializer> + implicit used: values()
	}

	@Test
	public void testEnumAccess() {
		parse(new String[] {"test_src/ad_hoc/Card.java", "test_src/ad_hoc/Planet.java"});

		eu.synectique.verveine.core.gen.famix.Enum st = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Enum.class, "Suit");
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
		
		eu.synectique.verveine.core.gen.famix.Enum pl = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Enum.class, "Planet");
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

		assertEquals(4, pl.getAttributes().size());
		for (Attribute a : pl.getAttributes()) {
			if ( a.getName().equals("G") || a.getName().equals("i") ) {
				assertEquals(1, a.getIncomingAccesses().size());
			}
			else if (a.getName().equals("radius")) {
				assertEquals(2, a.getIncomingAccesses().size());
			}
			else if (a.getName().equals("mass")) {
				assertEquals(4, a.getIncomingAccesses().size());
			}
			else {
				fail("Unknown attribute of Enum Planet: "+a.getName());
			}
		}

		assertEquals(7+2, pl.getMethods().size());  // see testEnumDecl()
		for (Method m : pl.getMethods()) {
			if ( m.getName().equals("Planet") || m.getName().equals("main") || m.getName().equals("sillyArrayAssignement")
					|| m.getName().equals(JavaDictionary.INIT_BLOCK_NAME) ) {
				assertEquals(0, m.getIncomingInvocations().size());
			}
			else if ( m.getName().equals("mass") || m.getName().equals("surfaceWeight") || m.getName().equals("toString") ) {
				assertEquals(1, m.getIncomingInvocations().size());
			}
			else if ( m.getName().equals("surfaceGravity")  || m.getName().equals("values") ) {
				assertEquals(2, m.getIncomingInvocations().size());
			}
			else if (m.getName().equals("radius")) {
				assertEquals(3, m.getIncomingInvocations().size());
			}
			else {
				fail("Unknown method of Enum Planet: "+m.getName());
			}
		}
	}

	@Test
	public void testReadWriteAccess() {
		parse(new String[] {"test_src/ad_hoc/Planet.java"});

		Attribute i_att = null;
		Attribute mass_att = null;
		Access access = null;

		eu.synectique.verveine.core.gen.famix.Enum pl = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Enum.class, "Planet");
		assertNotNull(pl);

		assertEquals(4, pl.getAttributes().size());
		for (Attribute a : pl.getAttributes()) {
			if ( a.getName().equals("i") ) {
				i_att = a;
			}
			else if (a.getName().equals("mass")) {
				mass_att = a;
			}
		}
		
		assertNotNull("Attribute i in Planet not found", i_att);
		assertEquals(1, i_att.getIncomingAccesses().size());
		access = i_att.getIncomingAccesses().iterator().next();
		assertFalse(access.getIsWrite());
		
		assertNotNull("Attribute mass in Planet not found", mass_att);
		assertEquals(4, mass_att.getIncomingAccesses().size());
		for (Access acc : mass_att.getIncomingAccesses() ) {
			if ( acc.getAccessor().getName().equals("Planet") ||
				 acc.getAccessor().getName().equals("sillyArrayAssignement") ) {
				assertTrue(acc.getIsWrite());
			}
			else {
				assertFalse("Access to mass is write in method: " + acc.getAccessor().getSignature(), acc.getIsWrite());
			}
		}
	}

	@Test
	public void testStaticInitializationBlock() {
		parse(new String[] {"test_src/ad_hoc/annotations", "test_src/ad_hoc/Card.java", "test_src/ad_hoc/Planet.java", "test_src/ad_hoc/InvokWithFullPath.java", "test_src/ad_hoc/DefaultConstructor.java"});

		Collection<Method> l_meth = VerveineUtilsForTests.listFamixElements(repo, Method.class, JavaDictionary.INIT_BLOCK_NAME);
		assertEquals(5, l_meth.size());
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
				fail("Unknown class with an <Initializer> method: " + meth.getParentType().getName());
			}
		}
	}

	@Test
	public void testStaticInitializationBlockNewString() {
		parse(new String[] {"test_src/ad_hoc/EnumConstWithInitNewString.java"});

		eu.synectique.verveine.core.gen.famix.Enum fmx = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Enum.class, "EnumConstWithInitNewString");
		assertNotNull(fmx);

		assertEquals(1, fmx.numberOfValues());
		assertEquals("ONE", fmx.getValues().iterator().next().getName());
		
		assertEquals(2, fmx.getMethods().size());  // constructor + INIT_BLOCK
	}

	@Test
	public void testWrongMethodOwner() {
		parse(new String[] {"test_src/ad_hoc/InvokerWrongOwner.java"});

		Method meth = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "methodWrongOwner");
		assertNotNull(meth);

		assertEquals(VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "SuperWrongOwner"), meth.getParentType());
	}
	
	@Test
	public void testModifiers() {
		parse(new String[] {"test_src/ad_hoc/Modifiers.java"});

		Attribute attribute = VerveineUtilsForTests.detectFamixElement(repo, Attribute.class, "privateFinalAttribute");
		assertNotNull(attribute);

		assertEquals(2, attribute.getModifiers().size());
		assertTrue(attribute.getModifiers().contains(JavaDictionary.MODIFIER_PRIVATE));
		assertTrue(attribute.getModifiers().contains(JavaDictionary.MODIFIER_FINAL));
	}

	@Test
	public void testMultipleSignatures() {
		parse(new String[] {"test_src/ad_hoc/MultipleSignatures.java"});

		eu.synectique.verveine.core.gen.famix.Class throwable = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "Throwable");
		assertNotNull(throwable);
		assertEquals(2, throwable.getMethods().size()); // printStackTrace() & printStackTrace(PrintWriter)

		Method regular =  VerveineUtilsForTests.detectFamixElement(repo, Method.class, "callToRegularPrintStackTrace");
		assertNotNull(regular);
		assertEquals(1, regular.getOutgoingInvocations().size());
		
		Method withParam =  VerveineUtilsForTests.detectFamixElement(repo, Method.class, "callToPrintStackTraceWithParam");
		assertNotNull(withParam);
		assertEquals(3, withParam.getOutgoingInvocations().size());  // printStackTrace(new PrintWriter(new StringWriter()))
	}
	
	@Test  // issue 714
	public void testAnnotParamIsClass(){
		parse(new String[] {"test_src/ad_hoc/annotations"});

		Attribute att = VerveineUtilsForTests.detectFamixElement(repo, Attribute.class, "time");
		assertNotNull(att);

		assertEquals(1, att.getAnnotationInstances().size());
		AnnotationInstance ann = att.getAnnotationInstances().iterator().next();
		assertNotNull(ann);
		assertEquals("XmlElement", ann.getAnnotationType().getName());
		assertSame(ann.getAnnotatedEntity(), att);
		assertEquals(3, ann.getAttributes().size());

		for (AnnotationInstanceAttribute annAtt : ann.getAttributes()) {
			if (annAtt.getAnnotationTypeAttribute().getName().equals("type")) {
				assertEquals("String.class", annAtt.getValue());
			}
			else {
				assertTrue( annAtt.getAnnotationTypeAttribute().getName().equals("name") ||
						    annAtt.getAnnotationTypeAttribute().getName().equals("required"));
			}
		}
	}

	@Test
	public void testInvokSelfNoBinding(){
		parse(new String[] {"test_src/ad_hoc/annotations"});

		Method seri = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "serialize");
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

	@Test
	public void testAnnotationTypeFileAnchor(){
		parse(new String[] {"test_src/ad_hoc/annotations"});

		AnnotationType xmle = VerveineUtilsForTests.detectFamixElement(repo, AnnotationType.class, "XmlElement");
		assertNotNull(xmle);
		assertNotNull(xmle.getSourceAnchor());
		assertEquals(IndexedFileAnchor.class, xmle.getSourceAnchor().getClass());
		assertEquals("test_src/ad_hoc/annotations/XmlElement.java", ((IndexedFileAnchor)xmle.getSourceAnchor()).getFileName());
		assertEquals(62, ((IndexedFileAnchor)xmle.getSourceAnchor()).getStartPos());
		assertEquals(176, ((IndexedFileAnchor)xmle.getSourceAnchor()).getEndPos());
		
		AnnotationTypeAttribute req = VerveineUtilsForTests.detectFamixElement(repo, AnnotationTypeAttribute.class, "required");
		assertNotNull(req);
		assertNotNull(req.getSourceAnchor());
		assertEquals(IndexedFileAnchor.class, req.getSourceAnchor().getClass());
		assertEquals("test_src/ad_hoc/annotations/XmlElement.java", ((IndexedFileAnchor)req.getSourceAnchor()).getFileName());
		assertEquals(120, ((IndexedFileAnchor)req.getSourceAnchor()).getStartPos());
		assertEquals(145, ((IndexedFileAnchor)req.getSourceAnchor()).getEndPos());

	}

	@Test
	public void testInstanceOf() {
		parse(new String[] {"test_src/ad_hoc/Planet.java"});

		Method m = VerveineUtilsForTests.detectFamixElement(repo, Method.class, "sillyArrayAssignement");
		assertNotNull(m);

		Collection<Reference> refs = m.getOutgoingReferences();
		assertEquals(2, refs.size());

		Type referred;
		Iterator<Reference> iter = refs.iterator();

		referred = iter.next().getTarget();
		if (referred.getName().equals("IOException")) {
		    referred = iter.next().getTarget();
		    assertEquals("Planet", referred.getName());
        }
        else {
		    assertEquals("Planet", referred.getName());
		    referred = iter.next().getTarget();
		    assertEquals("IOException", referred.getName());

        }
	}

	@Test
	public void testPublicStaticInnerClass() {
		parse(new String[] {"test_src/ad_hoc/StaticInnerClass.java"});

		eu.synectique.verveine.core.gen.famix.Class clazz = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "ThisIsTheStaticInnerClass");
		assertNotNull(clazz);

		// assertTrue(clazz.getIsPublic()); --- set as a modifier 
		assertEquals(2, clazz.getModifiers().size());
		for (String mod : clazz.getModifiers()) {
			assertTrue( mod.equals(JavaDictionary.MODIFIER_PUBLIC) || mod.equals(JavaDictionary.MODIFIER_STATIC) );
		}
	}

}