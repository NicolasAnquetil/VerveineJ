package fr.inria.verveine.extractor.java;


import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.synectique.verveine.core.gen.famix.IndexedFileAnchor;
import eu.synectique.verveine.core.gen.famix.Method;

public class VerveineJTest_CommentsMethod extends VerveineJTest_Basic {

    public VerveineJTest_CommentsMethod() {
        super(false);
    }

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        new File(VerveineJOptions.OUTPUT_FILE).delete();
        parser = new VerveineJParser();
        repo = parser.getFamixRepo();
    }

	private void parse(String[] sources) {
		parser.configure( sources);
		parser.parse();
		parser.emitMSE(VerveineJOptions.OUTPUT_FILE);
	}

    @Test
    public void testStartPosSourceAnchorMethod() {
        parse(new String[] {"test_src/comments"});
        List<Method> methods  = new ArrayList<>();
        methods.addAll(entitiesOfType(Method.class));
        methods.sort((o1, o2) -> ((Integer)((IndexedFileAnchor) o1.getSourceAnchor()).getStartPos().intValue()).compareTo ((Integer)((IndexedFileAnchor) o2.getSourceAnchor()).getStartPos().intValue()));
        assertEquals(10, methods.size());
        if(isWindows()){
            assertEquals(58, ((IndexedFileAnchor) methods.get(0).getSourceAnchor()).getStartPos().intValue());
            assertEquals(152, ((IndexedFileAnchor) methods.get(1).getSourceAnchor()).getStartPos().intValue());
            assertEquals(269, ((IndexedFileAnchor) methods.get(2).getSourceAnchor()).getStartPos().intValue());
            assertEquals(342, ((IndexedFileAnchor) methods.get(3).getSourceAnchor()).getStartPos().intValue());
            assertEquals(417, ((IndexedFileAnchor) methods.get(4).getSourceAnchor()).getStartPos().intValue());
            assertEquals(568, ((IndexedFileAnchor) methods.get(5).getSourceAnchor()).getStartPos().intValue());
            assertEquals(639, ((IndexedFileAnchor) methods.get(6).getSourceAnchor()).getStartPos().intValue());
            assertEquals(760, ((IndexedFileAnchor) methods.get(7).getSourceAnchor()).getStartPos().intValue());
            assertEquals(908, ((IndexedFileAnchor) methods.get(8).getSourceAnchor()).getStartPos().intValue());
        } else {
            assertEquals(53, ((IndexedFileAnchor) methods.get(0).getSourceAnchor()).getStartPos().intValue());
            assertEquals(142, ((IndexedFileAnchor) methods.get(1).getSourceAnchor()).getStartPos().intValue());
            assertEquals(252, ((IndexedFileAnchor) methods.get(2).getSourceAnchor()).getStartPos().intValue());
            assertEquals(321, ((IndexedFileAnchor) methods.get(3).getSourceAnchor()).getStartPos().intValue());
            assertEquals(391, ((IndexedFileAnchor) methods.get(4).getSourceAnchor()).getStartPos().intValue());
            assertEquals(535, ((IndexedFileAnchor) methods.get(5).getSourceAnchor()).getStartPos().intValue());
            assertEquals(602, ((IndexedFileAnchor) methods.get(6).getSourceAnchor()).getStartPos().intValue());
            assertEquals(718, ((IndexedFileAnchor) methods.get(7).getSourceAnchor()).getStartPos().intValue());
            assertEquals(859, ((IndexedFileAnchor) methods.get(8).getSourceAnchor()).getStartPos().intValue());
        }
    }

    @Test
    public void testEndPosSourceAnchorMethod() {
        parse(new String[] {"test_src/comments"});
        List<Method> methods  = new ArrayList<>();
        methods.addAll(entitiesOfType(Method.class));
        methods.sort(Comparator.comparing(o -> ((Integer) ((IndexedFileAnchor) o.getSourceAnchor()).getEndPos().intValue())));
        assertEquals(10, methods.size());
        if(isWindows()){
            assertEquals(110, ((IndexedFileAnchor) methods.get(0).getSourceAnchor()).getEndPos().intValue());
            assertEquals(209, ((IndexedFileAnchor) methods.get(1).getSourceAnchor()).getEndPos().intValue());
            assertEquals(333, ((IndexedFileAnchor) methods.get(2).getSourceAnchor()).getEndPos().intValue());
            assertEquals(392, ((IndexedFileAnchor) methods.get(3).getSourceAnchor()).getEndPos().intValue());
            assertEquals(493, ((IndexedFileAnchor) methods.get(4).getSourceAnchor()).getEndPos().intValue());
            assertEquals(630, ((IndexedFileAnchor) methods.get(5).getSourceAnchor()).getEndPos().intValue());
            assertEquals(701, ((IndexedFileAnchor) methods.get(6).getSourceAnchor()).getEndPos().intValue());
            assertEquals(830, ((IndexedFileAnchor) methods.get(7).getSourceAnchor()).getEndPos().intValue());
            assertEquals(988, ((IndexedFileAnchor) methods.get(8).getSourceAnchor()).getEndPos().intValue());
        } else {
            assertEquals(103, ((IndexedFileAnchor) methods.get(0).getSourceAnchor()).getEndPos().intValue());
            assertEquals(197, ((IndexedFileAnchor) methods.get(1).getSourceAnchor()).getEndPos().intValue());
            assertEquals(314, ((IndexedFileAnchor) methods.get(2).getSourceAnchor()).getEndPos().intValue());
            assertEquals(369, ((IndexedFileAnchor) methods.get(3).getSourceAnchor()).getEndPos().intValue());
            assertEquals(465, ((IndexedFileAnchor) methods.get(4).getSourceAnchor()).getEndPos().intValue());
            assertEquals(595, ((IndexedFileAnchor) methods.get(5).getSourceAnchor()).getEndPos().intValue());
            assertEquals(662, ((IndexedFileAnchor) methods.get(6).getSourceAnchor()).getEndPos().intValue());
            assertEquals(786, ((IndexedFileAnchor) methods.get(7).getSourceAnchor()).getEndPos().intValue());
            assertEquals(937, ((IndexedFileAnchor) methods.get(8).getSourceAnchor()).getEndPos().intValue());
        }
    }

    @Test
    public void testSourceAnchorHasFile() {
        parse(new String[] {"test_src/comments"});
        List<Method> methods  = new ArrayList<>();
        methods.addAll(entitiesOfType(Method.class));
        assertEquals(10, methods.size());
        for(Method method: methods){
            assertEquals("test_src/comments/CommentClass.java", ((IndexedFileAnchor) method.getSourceAnchor()).getFileName());
        }
    }



}
