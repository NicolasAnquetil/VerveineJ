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

public class verveineJTest_Annotations extends VerveineJTest_Basic {

	public verveineJTest_Annotations() {
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
        if (isWindows()){
            parser.setOptions(new String[] {"-cp" , "test_src/annotations/lib/jboss-interceptors-api_1.1_spec-1.0.1.Final-redhat-2.jar;test_src/annotations/lib/stripes-1.5.5.jar", "test_src/annotations"});
        } else {
            parser.setOptions(new String[] {"-cp" , "test_src/annotations/lib/jboss-interceptors-api_1.1_spec-1.0.1.Final-redhat-2.jar:test_src/annotations/lib/stripes-1.5.5.jar", "test_src/annotations"});
        }
        parser.parse();
        //	parser.emitMSE(VerveineJParser.OUTPUT_FILE);
    }

    @Test
    public void testAnnotationSubClass() {
        eu.synectique.verveine.core.gen.famix.Class cl = detectFamixElement(eu.synectique.verveine.core.gen.famix.Class.class, "SubAnnotation");
        assertNotNull(cl);

        AnnotationType getProp = detectFamixElement(AnnotationType.class, "GetProperty");
        assertEquals(getProp, cl.getContainer());
    }

    @Test
    public void testClassAnnotation() {
        eu.synectique.verveine.core.gen.famix.Class cl = detectFamixElement(eu.synectique.verveine.core.gen.famix.Class.class, "Serializer");
        assertEquals(1, cl.getAnnotationInstances().size());
        AnnotationInstance sw = firstElt(cl.getAnnotationInstances());
        assertNotNull(sw);
        assertEquals("SuppressWarnings", sw.getAnnotationType().getName());
        assertSame(sw.getAnnotatedEntity(), cl);
        assertEquals(1, sw.getAttributes().size());
        AnnotationInstanceAttribute swVal = firstElt(sw.getAttributes());
        assertNotNull(swVal);
        assertEquals("value", swVal.getAnnotationTypeAttribute().getName());
        assertEquals("\"serial\"", swVal.getValue());

    }

    @Test
    public void testMethodAnnotation() {
        eu.synectique.verveine.core.gen.famix.Class book = detectFamixElement(eu.synectique.verveine.core.gen.famix.Class.class, "Book");
        Collection<Method> bookMethods = book.getMethods();

       AnnotationType getProp = detectFamixElement(AnnotationType.class, "GetProperty");
       assertNotNull(getProp);
       AnnotationTypeAttribute getAtt = (AnnotationTypeAttribute) firstElt(getProp.getAttributes());

        assertEquals(12, bookMethods.size());
        for (Method meth : bookMethods) {
            Collection<AnnotationInstance> annInstances = meth.getAnnotationInstances();
            if (meth.getName().startsWith("get")) {
                assertEquals(1, annInstances.size());
                AnnotationInstance annInst = firstElt(annInstances);
                assertSame(getProp, annInst.getAnnotationType());
                assertEquals(1, annInst.getAttributes().size());
                AnnotationInstanceAttribute getValInst = firstElt(annInst.getAttributes());
                assertSame(getAtt, getValInst.getAnnotationTypeAttribute());

            }
            else if (meth.getName().startsWith("set")) {
                assertEquals(1, annInstances.size());
                AnnotationInstance annInst = firstElt(annInstances);
                assertEquals("SetProperty", annInst.getAnnotationType().getName());
            }
            else {
                assertEquals(0, annInstances.size());
            }
        }
    }

    @Test
    public void testAttributeAnnotation() {
        eu.synectique.verveine.core.gen.famix.Class book = detectFamixElement(eu.synectique.verveine.core.gen.famix.Class.class, "Book");
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
    public void testAnnotParamIsClass(){
        Attribute att = detectFamixElement( Attribute.class, "time");
        assertNotNull(att);

        assertEquals(1, att.getAnnotationInstances().size());
        AnnotationInstance ann = firstElt(att.getAnnotationInstances());
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
        AnnotationType xmle = detectFamixElement( AnnotationType.class, "XmlElement");
        assertNotNull(xmle);
        assertNotNull(xmle.getSourceAnchor());
        assertEquals(IndexedFileAnchor.class, xmle.getSourceAnchor().getClass());
        assertEquals("test_src/annotations/XmlElement.java", ((IndexedFileAnchor)xmle.getSourceAnchor()).getFileName());
        if (isWindows()) {
            assertEquals(66, ((IndexedFileAnchor) xmle.getSourceAnchor()).getStartPos());
            assertEquals(188, ((IndexedFileAnchor) xmle.getSourceAnchor()).getEndPos());
        }
        else {
            assertEquals(62, ((IndexedFileAnchor) xmle.getSourceAnchor()).getStartPos());
            assertEquals(176, ((IndexedFileAnchor) xmle.getSourceAnchor()).getEndPos());
        }

        AnnotationTypeAttribute req = null;
        for (AnnotationInstanceAttribute aia : firstElt(xmle.getInstances()).getAttributes()) {
            if (aia.getAnnotationTypeAttribute().getName().equals("required")) {
                req = aia.getAnnotationTypeAttribute();
            }
        }
        //AnnotationTypeAttribute req = detectFamixElement( AnnotationTypeAttribute.class, "required");
        assertNotNull(req);
        assertNotNull(req.getSourceAnchor());
        assertEquals(IndexedFileAnchor.class, req.getSourceAnchor().getClass());
        assertEquals("test_src/annotations/XmlElement.java", ((IndexedFileAnchor)req.getSourceAnchor()).getFileName());
        if (isWindows()) {
            assertEquals(128, ((IndexedFileAnchor) req.getSourceAnchor()).getStartPos());
            assertEquals(153, ((IndexedFileAnchor) req.getSourceAnchor()).getEndPos());
        }
        else  {
            assertEquals(120, ((IndexedFileAnchor) req.getSourceAnchor()).getStartPos());
            assertEquals(145, ((IndexedFileAnchor) req.getSourceAnchor()).getEndPos());
        }
    }

    @Test
    public void testAnnotationOnVar() {
        Parameter param = detectFamixElement( Parameter.class, "annotatedParam");
        assertNotNull(param);

        assertEquals(1, param.getAnnotationInstances().size());
        AnnotationInstance inst = firstElt(param.getAnnotationInstances());
        assertNotNull(inst);
        assertEquals("SuppressWarnings", inst.getAnnotationType().getName());
        assertSame(inst.getAnnotatedEntity(), param);
        AnnotationInstanceAttribute att = firstElt(inst.getAttributes());
        assertEquals("\"blah\"", att.getValue());
    }

    @Test
    public void testAnnotationCollectionOfString() {
        Method meth = detectFamixElement( Method.class, "setPassword");
        assertNotNull(meth);

        assertEquals(1, meth.getAnnotationInstances().size());
        AnnotationInstance inst = firstElt(meth.getAnnotationInstances());
        assertNotNull(inst);

        assertEquals(2, inst.getAttributes().size());

        for( AnnotationInstanceAttribute att : inst.getAttributes()) {
            if (att.getAnnotationTypeAttribute().getName().equals("on")) {
                assertEquals("{\"signon\", \"newAccount\", \"editAccount\"}", att.getValue());
            } else if (att.getAnnotationTypeAttribute().getName().equals("required")) {
                assertEquals("true", att.getValue());
            } else {
                fail("We are in an attribute that does not exist");
            }
        }
    }

    @Test
    public void testAnnotationType() {
        AnnotationType getProp = detectFamixElement(AnnotationType.class, "GetProperty");
        assertNotNull(getProp);
        assertFalse(getProp.getIsStub());

        assertEquals(1, getProp.getAttributes().size());
        AnnotationTypeAttribute getAtt = (AnnotationTypeAttribute) firstElt(getProp.getAttributes());
        assertEquals("value", getAtt.getName());
        assertEquals(4, getProp.getInstances().size());

        AnnotationType annTyp = detectFamixElement( AnnotationType.class, "InterceptorsOnInterceptor");
        assertNotNull(annTyp);
        assertFalse(annTyp.getIsStub());
        assertEquals(1, annTyp.numberOfAttributes());
        assertEquals("value", firstElt(annTyp.getAttributes()).getName());
        assertEquals(2, annTyp.numberOfInstances());

        annTyp = detectFamixElement( AnnotationType.class, "Interceptors");
        assertNotNull(annTyp);
        assertTrue(annTyp.getIsStub());
        assertEquals(1, annTyp.numberOfAttributes());
        assertEquals("value", firstElt(annTyp.getAttributes()).getName());
        assertEquals(3, annTyp.numberOfInstances());
    }

    @Test
    public void testAnnotationInstanceAttribute() {
        eu.synectique.verveine.core.gen.famix.Class clss = detectFamixElement( eu.synectique.verveine.core.gen.famix.Class.class, "AnnotatedClass");
        assertNotNull(clss);

        assertEquals(1, clss.numberOfAnnotationInstances());
        AnnotationInstance inst = firstElt(clss.getAnnotationInstances());
        assertEquals("Interceptors", inst.getAnnotationType().getName());
        assertSame(inst.getAnnotatedEntity(), clss);

        assertEquals(1, inst.numberOfAttributes());
        AnnotationInstanceAttribute att = firstElt(inst.getAttributes());
        assertNotNull(att);
        assertEquals("value", att.getAnnotationTypeAttribute().getName());

        assertEquals("FirstInterceptorClass.class", att.getValue());
    }

    @Test
    public void testAnnotationInstanceArrayOfOne() {
        eu.synectique.verveine.core.gen.famix.Class clss = detectFamixElement( eu.synectique.verveine.core.gen.famix.Class.class, "AThirdAnnotatedClass");
        assertNotNull(clss);

        assertEquals(1, clss.numberOfAnnotationInstances());
        AnnotationInstance inst = firstElt(clss.getAnnotationInstances());
        assertNotNull(inst);
        assertEquals("Interceptors", inst.getAnnotationType().getName());
        assertSame(inst.getAnnotatedEntity(), clss);

        assertEquals(1, inst.numberOfAttributes());
        AnnotationInstanceAttribute att = firstElt(inst.getAttributes());
        assertNotNull(att);
        assertEquals("value", att.getAnnotationTypeAttribute().getName());

        assertEquals("FirstInterceptorClass.class", att.getValue());
    }

    @Test
    public void testAnnotationInstanceEmptyArrayForValue() {
        eu.synectique.verveine.core.gen.famix.Class clss = detectFamixElement( eu.synectique.verveine.core.gen.famix.Class.class, "AnotherInterceptorClass");
        assertNotNull(clss);

        assertEquals(1, clss.numberOfAnnotationInstances());
        AnnotationInstance inst = firstElt(clss.getAnnotationInstances());
        assertNotNull(inst);

        assertEquals(1, inst.numberOfAttributes());
        AnnotationInstanceAttribute att = firstElt(inst.getAttributes());
        assertNotNull(att);
        assertEquals("{}", att.getValue());
    }

    @Test
    public void testAnnotationInstanceArray() {
        eu.synectique.verveine.core.gen.famix.Class clss = detectFamixElement( eu.synectique.verveine.core.gen.famix.Class.class, "AnotherAnnotatedClass");
        assertNotNull(clss);

        assertEquals(1, clss.numberOfAnnotationInstances());
        AnnotationInstance inst = firstElt(clss.getAnnotationInstances());
        assertNotNull(inst);
        assertEquals("Interceptors", inst.getAnnotationType().getName());
        assertSame(inst.getAnnotatedEntity(), clss);

        assertEquals(1, inst.numberOfAttributes());
        AnnotationInstanceAttribute att = firstElt(inst.getAttributes());
        assertNotNull(att);
        assertEquals("value", att.getAnnotationTypeAttribute().getName());

        assertEquals("{FirstInterceptorClass.class, AnotherInterceptorClass.class}", att.getValue());
    }

}
