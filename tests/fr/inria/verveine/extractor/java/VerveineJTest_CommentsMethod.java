package fr.inria.verveine.extractor.java;


import eu.synectique.verveine.core.gen.famix.FileAnchor;
import eu.synectique.verveine.core.gen.famix.IndexedFileAnchor;
import eu.synectique.verveine.core.gen.famix.Method;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

public class VerveineJTest_CommentsMethod extends VerveineJTest_Basic {

    public VerveineJTest_CommentsMethod() {
        super(false);
    }

    /**
     * @throws Exception
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
        parser.emitMSE(VerveineJParser.OUTPUT_FILE);
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
            assertEquals(52, ((IndexedFileAnchor) methods.get(0).getSourceAnchor()).getStartPos().intValue());
            assertEquals(141, ((IndexedFileAnchor) methods.get(1).getSourceAnchor()).getStartPos().intValue());
            assertEquals(251, ((IndexedFileAnchor) methods.get(2).getSourceAnchor()).getStartPos().intValue());
            assertEquals(320, ((IndexedFileAnchor) methods.get(3).getSourceAnchor()).getStartPos().intValue());
            assertEquals(390, ((IndexedFileAnchor) methods.get(4).getSourceAnchor()).getStartPos().intValue());
            assertEquals(534, ((IndexedFileAnchor) methods.get(5).getSourceAnchor()).getStartPos().intValue());
            assertEquals(601, ((IndexedFileAnchor) methods.get(6).getSourceAnchor()).getStartPos().intValue());
            assertEquals(717, ((IndexedFileAnchor) methods.get(7).getSourceAnchor()).getStartPos().intValue());
            assertEquals(858, ((IndexedFileAnchor) methods.get(8).getSourceAnchor()).getStartPos().intValue());
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
            assertEquals(107, ((IndexedFileAnchor) methods.get(0).getSourceAnchor()).getEndPos().intValue());
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
