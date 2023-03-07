package fr.inria.verveine.extractor.java;


import org.junit.Before;
import org.junit.Test;
import org.moosetechnology.model.famix.famixjavaentities.IndexedFileAnchor;
import org.moosetechnology.model.famix.famixjavaentities.Method;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class VerveineJTest_CommentsMethod extends VerveineJTest_Basic {

    public VerveineJTest_CommentsMethod() {
        super(false);
    }

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        parser = new VerveineJParser();
        repo = parser.getFamixRepo();
    }

	private void parse(String[] sources) {
        parser.configure(sources);
        parser.parse();
    }

    /**
     * Helper method to get the starting position of a method and convert it to <code>Integer</code> (to be able to use <code>compareTo()</code> on it)
     */
    private Integer startPos(Method mth) {
		return (Integer) ((IndexedFileAnchor)mth.getSourceAnchor()).getStartPos().intValue();
	}

    /**
     * Another helper method for the end position of a method, but this one does not need converting to <code>Integer</code>
     */
    private int endPos(Method method) {
		return ((IndexedFileAnchor) method.getSourceAnchor()).getEndPos().intValue();
	}

    @Test
    public void testStartPosSourceAnchorMethod() {
        parse(new String[] {"test_src/comments"});

        List<Method> methods  = new ArrayList<>();
        // convert to <code>List</code> to be able to sort them by ascending starting position
        methods.addAll(entitiesOfType(Method.class));
        assertEquals(10, methods.size());

        methods.sort( (o1, o2) -> startPos(o1).compareTo(startPos(o2)) );
        if(isWindows()){
        	// on Windows need to add one character for each end-of-line
            assertEquals( (Integer)(231+20), startPos(methods.get(0))); // ClassWithComments()
            assertEquals( (Integer)(292+24), startPos(methods.get(1))); // ClassWithComments(int i)
            assertEquals( (Integer)(374+30), startPos(methods.get(2))); // ClassWithComments(int i, int j)
            assertEquals( (Integer)(518+37), startPos(methods.get(3))); // method1()
            assertEquals( (Integer)(591+41), startPos(methods.get(4))); // method2(int i)
            assertEquals( (Integer)(773+50), startPos(methods.get(5))); // method3(int i, int j)
            assertEquals( (Integer)(811+53), startPos(methods.get(6))); // method4()
            assertEquals( (Integer)(893+57), startPos(methods.get(7))); // method5(double a)
            assertEquals( (Integer)(1000+63), startPos(methods.get(8))); // method6(double a, double b)
            assertEquals( (Integer)(1117+69), startPos(methods.get(9))); // methodWithoutBody(double a, double b, double c)
        } else {
            assertEquals( (Integer)231, startPos(methods.get(0))); // ClassWithComments()
            assertEquals( (Integer)292, startPos(methods.get(1))); // ClassWithComments(int i)
            assertEquals( (Integer)374, startPos(methods.get(2))); // ClassWithComments(int i, int j)
            assertEquals( (Integer)518, startPos(methods.get(3))); // method1()
            assertEquals( (Integer)591, startPos(methods.get(4))); // method2(int i)
            assertEquals( (Integer)773, startPos(methods.get(5))); // method3(int i, int j)
            assertEquals( (Integer)811, startPos(methods.get(6))); // method4()
            assertEquals( (Integer)893, startPos(methods.get(7))); // method5(double a)
            assertEquals( (Integer)1000, startPos(methods.get(8))); // method6(double a, double b)
            assertEquals( (Integer)1117, startPos(methods.get(9))); // methodWithoutBody(double a, double b, double c)
        }
    }

	@Test
    public void testEndPosSourceAnchorMethod() {
        parse(new String[] {"test_src/comments"});
        List<Method> methods  = new ArrayList<>();
        methods.addAll(entitiesOfType(Method.class));
        methods.sort(Comparator.comparing(o -> ((IndexedFileAnchor) o.getSourceAnchor()).getEndPos().intValue()));
        assertEquals(10, methods.size());
        if(isWindows()){
        	// on Windows need to add one character for each end-of-line
            assertEquals(256+21, endPos(methods.get(0))); // ClassWithComments()
            assertEquals(322+25, endPos(methods.get(1))); // ClassWithComments(int i)
            assertEquals(481+34, endPos(methods.get(2))); // ClassWithComments(int i, int j)
            assertEquals(537+38, endPos(methods.get(3))); // method1()
            assertEquals(703+45, endPos(methods.get(4))); // method2(int i)
            assertEquals(804+51, endPos(methods.get(5))); // method3(int i, int j)
            assertEquals(837+54, endPos(methods.get(6))); // method4()
            assertEquals(927+58, endPos(methods.get(7))); // method5(double a)
            assertEquals(1044+64, endPos(methods.get(8))); // method6(double a, double b)
            assertEquals(1175+69, endPos(methods.get(9))); // methodWithoutBody(double a, double b, double c)
        } else {
            assertEquals(256, endPos(methods.get(0))); // ClassWithComments()
            assertEquals(322, endPos(methods.get(1))); // ClassWithComments(int i)
            assertEquals(481, endPos(methods.get(2))); // ClassWithComments(int i, int j)
            assertEquals(537, endPos(methods.get(3))); // method1()
            assertEquals(703, endPos(methods.get(4))); // method2(int i)
            assertEquals(804, endPos(methods.get(5))); // method3(int i, int j)
            assertEquals(837, endPos(methods.get(6))); // method4()
            assertEquals(927, endPos(methods.get(7))); // method5(double a)
            assertEquals(1044, endPos(methods.get(8))); // method6(double a, double b)
            assertEquals(1175, endPos(methods.get(9))); // methodWithoutBody(double a, double b, double c)
        }
    }

	@Test
    public void testSourceAnchorHasFile() {
        parse(new String[] {"test_src/comments"});
        List<Method> methods  = new ArrayList<>();
        methods.addAll(entitiesOfType(Method.class));
        assertEquals(10, methods.size());
        for(Method method: methods){
            assertEquals("test_src/comments/ClassWithComments.java", ((IndexedFileAnchor)method.getSourceAnchor()).getFileName());
        }
    }



}
