package fr.inria.verveine.extractor.java;

import ch.akuhn.fame.Repository;
import eu.synectique.verveine.core.VerveineUtilsForTests;
import eu.synectique.verveine.core.gen.famix.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.Exception;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class verveineJTest_Annotations {

	protected Repository repo;
	protected VerveineJParser parser;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        new File(VerveineJParser.OUTPUT_FILE).delete();
        VerveineJParser parser = new VerveineJParser();
        repo = parser.getFamixRepo();
        parser.setOptions(new String[] {"-cp" , "test_src/annotations/lib/jboss-interceptors-api_1.1_spec-1.0.1.Final-redhat-2.jar", "test_src/annotations"});
        parser.parse();
        //	parser.emitMSE(VerveineJParser.OUTPUT_FILE);
    }

    @Test
    public void testClassAnnotation() {
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

    }

    @Test
    public void testMethodAnnotation() {
        eu.synectique.verveine.core.gen.famix.Class book = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "Book");
        Collection<Method> bookMethods = book.getMethods();

       AnnotationType getProp = VerveineUtilsForTests.detectFamixElement(repo,AnnotationType.class, "GetProperty");
       assertNotNull(getProp);
       AnnotationTypeAttribute getAtt = (AnnotationTypeAttribute) getProp.getAttributes().iterator().next();

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
    }

    @Test
    public void testAttributeAnnotation() {
        eu.synectique.verveine.core.gen.famix.Class book = VerveineUtilsForTests.detectFamixElement(repo,eu.synectique.verveine.core.gen.famix.Class.class, "Book");
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

    @Test  // issue 714
    public void testAnnotParamIsClass(){
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
    public void testAnnotationTypeFileAnchor(){
        AnnotationType xmle = VerveineUtilsForTests.detectFamixElement(repo, AnnotationType.class, "XmlElement");
        assertNotNull(xmle);
        assertNotNull(xmle.getSourceAnchor());
        assertEquals(IndexedFileAnchor.class, xmle.getSourceAnchor().getClass());
        assertEquals("test_src/annotations/XmlElement.java", ((IndexedFileAnchor)xmle.getSourceAnchor()).getFileName());
        assertEquals(62, ((IndexedFileAnchor)xmle.getSourceAnchor()).getStartPos());
        assertEquals(176, ((IndexedFileAnchor)xmle.getSourceAnchor()).getEndPos());

        AnnotationTypeAttribute req = VerveineUtilsForTests.detectFamixElement(repo, AnnotationTypeAttribute.class, "required");
        assertNotNull(req);
        assertNotNull(req.getSourceAnchor());
        assertEquals(IndexedFileAnchor.class, req.getSourceAnchor().getClass());
        assertEquals("test_src/annotations/XmlElement.java", ((IndexedFileAnchor)req.getSourceAnchor()).getFileName());
        assertEquals(120, ((IndexedFileAnchor)req.getSourceAnchor()).getStartPos());
        assertEquals(145, ((IndexedFileAnchor)req.getSourceAnchor()).getEndPos());

    }

    @Test
    public void testAnnotationOnVar() {
        Parameter param = VerveineUtilsForTests.detectFamixElement(repo, Parameter.class, "annotatedParam");
        assertNotNull(param);

        assertEquals(1, param.getAnnotationInstances().size());
        AnnotationInstance inst = param.getAnnotationInstances().iterator().next();
        assertNotNull(inst);
        assertEquals("SuppressWarnings", inst.getAnnotationType().getName());
        assertSame(inst.getAnnotatedEntity(), param);
    }

    @Test
    public void testAnnotationType() {
        AnnotationType getProp = VerveineUtilsForTests.detectFamixElement(repo,AnnotationType.class, "GetProperty");
        assertNotNull(getProp);
        assertFalse(getProp.getIsStub());

        assertEquals(1, getProp.getAttributes().size());
        AnnotationTypeAttribute getAtt = (AnnotationTypeAttribute) getProp.getAttributes().iterator().next();
        assertEquals("value", getAtt.getName());
        assertEquals(4, getProp.getInstances().size());

        AnnotationType annTyp = VerveineUtilsForTests.detectFamixElement(repo, AnnotationType.class, "InterceptorsOnInterceptor");
        assertNotNull(annTyp);
        assertFalse(annTyp.getIsStub());
        assertEquals(1, annTyp.numberOfAttributes());
        assertEquals("value", annTyp.getAttributes().iterator().next().getName());
        assertEquals(2, annTyp.numberOfInstances());

        annTyp = VerveineUtilsForTests.detectFamixElement(repo, AnnotationType.class, "Interceptors");
        assertNotNull(annTyp);
        assertTrue(annTyp.getIsStub());
        assertEquals(1, annTyp.numberOfAttributes());
        assertEquals("value", annTyp.getAttributes().iterator().next().getName());
        assertEquals(3, annTyp.numberOfInstances());
    }

    @Test
    public void testAnnotationInstanceAttribute() {
        eu.synectique.verveine.core.gen.famix.Class clss = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "AnnotatedClass");
        assertNotNull(clss);

        assertEquals(1, clss.numberOfAnnotationInstances());
        AnnotationInstance inst = clss.getAnnotationInstances().iterator().next();
        assertEquals("Interceptors", inst.getAnnotationType().getName());
        assertSame(inst.getAnnotatedEntity(), clss);

        assertEquals(1, inst.numberOfAttributes());
        AnnotationInstanceAttribute att = inst.getAttributes().iterator().next();
        assertNotNull(att);
        assertEquals("value", att.getAnnotationTypeAttribute().getName());

        assertEquals("FirstInterceptorClass.class", att.getValue());
    }

    @Test
    public void testAnnotationInstanceArrayOfOne() {
        eu.synectique.verveine.core.gen.famix.Class clss = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "AThirdAnnotatedClass");
        assertNotNull(clss);

        assertEquals(1, clss.numberOfAnnotationInstances());
        AnnotationInstance inst = clss.getAnnotationInstances().iterator().next();
        assertNotNull(inst);
        assertEquals("Interceptors", inst.getAnnotationType().getName());
        assertSame(inst.getAnnotatedEntity(), clss);

        assertEquals(1, inst.numberOfAttributes());
        AnnotationInstanceAttribute att = inst.getAttributes().iterator().next();
        assertNotNull(att);
        assertEquals("value", att.getAnnotationTypeAttribute().getName());

        assertEquals("FirstInterceptorClass.class", att.getValue());
    }

    @Test
    public void testAnnotationInstanceEmptyArrayForValue() {
        eu.synectique.verveine.core.gen.famix.Class clss = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "AnotherInterceptorClass");
        assertNotNull(clss);

        assertEquals(1, clss.numberOfAnnotationInstances());
        AnnotationInstance inst = clss.getAnnotationInstances().iterator().next();
        assertNotNull(inst);

        assertEquals(1, inst.numberOfAttributes());
        AnnotationInstanceAttribute att = inst.getAttributes().iterator().next();
        assertNotNull(att);
        assertEquals("{}", att.getValue());
    }

    @Test
    public void testAnnotationInstanceArray() {
        eu.synectique.verveine.core.gen.famix.Class clss = VerveineUtilsForTests.detectFamixElement(repo, eu.synectique.verveine.core.gen.famix.Class.class, "AnotherAnnotatedClass");
        assertNotNull(clss);

        assertEquals(1, clss.numberOfAnnotationInstances());
        AnnotationInstance inst = clss.getAnnotationInstances().iterator().next();
        assertNotNull(inst);
        assertEquals("Interceptors", inst.getAnnotationType().getName());
        assertSame(inst.getAnnotatedEntity(), clss);

        assertEquals(1, inst.numberOfAttributes());
        AnnotationInstanceAttribute att = inst.getAttributes().iterator().next();
        assertNotNull(att);
        assertEquals("value", att.getAnnotationTypeAttribute().getName());

        assertEquals("{FirstInterceptorClass.class, AnotherInterceptorClass.class}", att.getValue());
    }

}
