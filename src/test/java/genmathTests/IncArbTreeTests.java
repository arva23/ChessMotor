package genmathTests;

import genmath.IncArbTree;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class IncArbTreeTests {
    
    public IncArbTreeTests() {

        
    }
    
    @BeforeAll
    public static void setUpClass() {
    
        
    }
    
    @AfterAll
    public static void tearDownClass() {
    
        
    }
    
    @BeforeEach
    public void setUp() {
    
        
    }
    
    @AfterEach
    public void tearDown() {
    
        
    }

    @Test
    public void constructors_areCorrect(){
    
        // TODO
        
        // Empty constructor
        IncArbTree<TestKey, String> testObject = new IncArbTree<TestKey, String>();
        
        // Copy constructor
        IncArbTree<TestKey, String> testObject2 = 
                new IncArbTree<TestKey, String>(testObject);
        Assertions.assertNotEquals(testObject, testObject2);
    }
    
    @Test
    public void insertionMethods_areCorrect() throws Exception{
    
        // TODO
        
        // addOne method
        // Include a new child node with a given Pair under a parent key 
        // onto second level
        // Container is empty, nominal case
        IncArbTree<TestKey, String> testObject = new IncArbTree<TestKey, String>();
        testObject.addOne(new TestKey(0), new TestKey(0), new String("first"));
        Assertions.assertEquals(testObject.getContainer().get(0), 
                new IncArbTree.Pair<TestKey, String>(new TestKey(0), new String("first")));
        
        // Include a new child node with a given Pair right after root node
        // Container contains only the root node
        testObject.addOne(new TestKey(0), new TestKey(1), new String("second"));
        Assertions.assertEquals(testObject.getContainer().get(1),
                new IncArbTree.Pair<TestKey, String>(new TestKey(1), new String("second")));
        
        // no parent key found
        try{
        
            testObject.addOne(new TestKey(9), new TestKey(2), new String("thrid"));
            Assertions.fail();
        }
        catch(Exception e){ }
        
        // addOne method
        // Do the same test cases with this method as at the addOne method
        IncArbTree<TestKey, String> testObject2 = new IncArbTree<TestKey, String>();
        
        ArrayList<IncArbTree.Pair<TestKey, String> > param =
                new ArrayList<IncArbTree.Pair<TestKey, String> >();
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(0), new String("first")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(1), new String("second")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(2), new String("third")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(3), new String("fourth")));
        Assertions.assertTrue(testObject2.add(new TestKey(0), param) == 0);
        
        param.clear();
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(4), new String("fifth")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(5), new String("sixth")));
        Assertions.assertTrue(testObject2.add(new TestKey(2), param) == 4);
        
        try{
        
            testObject2.add(new TestKey(10), param);
            Assertions.fail();
        }
        catch(Exception e){ }
    }
    
    @Test
    public void getters_areCorrect() throws Exception{
    
        // TODO
        
        IncArbTree<TestKey, String> testObject = new IncArbTree<TestKey, String>();
        ArrayList<IncArbTree.Pair<TestKey, String> > param = 
                new ArrayList<IncArbTree.Pair<TestKey, String> >();
        
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(0), new String("zero")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(1), new String("one")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(2), new String("two")));
        testObject.add(new TestKey(0), param);
        
        param.clear();
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(3), new String("three")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(4), new String("four")));
        testObject.add(new TestKey(1), param);
        
        param.clear();
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(5), new String("five")));
        testObject.add(new TestKey(2), param);
        
        param.clear();
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(6), new String("six")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(7), new String("seven")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(8), new String("eight")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(9), new String("nine")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(10), new String("ten")));
        testObject.add(new TestKey(3), param);
        
        param.clear();
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(11), new String("eleven")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(12), new String("twelve")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(13), new String("thrirteen")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(14), new String("fourteen")));
        testObject.add(new TestKey(9), param);
                
        
        // getByLevelOrdInd method
        Assertions.assertTrue(testObject.getByLevelOrdInd(1).compareTo(
                new String("one")) == 0);
        // Index out of bounds
        try{
        
            testObject.getByLevelOrdInd(111);
            Assertions.fail();
        }
        catch(Exception e){ }
        
        // getByInd
        // TODO
        
        // getByKey method
        // Nominal case
        Assertions.assertTrue(new String("fourteen").compareTo(
                testObject.getByKey(new TestKey(14))) == 0);
        
        // Provided key does not exist
        try{
        
            testObject.getByKey(new TestKey(15));
            Assertions.fail();
        }
        catch(Exception e){ }
        
        try{
        
            testObject.getByKey(new TestKey(-1));
            Assertions.fail();
        }
        catch(Exception e){ }
        
        
        // getOrdIndByKey method
        // Nominal case
        Assertions.assertTrue(testObject.getOrdIndByKey(new TestKey(7)) == 7);
        Assertions.assertTrue(testObject.getOrdIndByKey(new TestKey(10)) == 10);
        
        // Provided key does not exist
        try{
        
            testObject.getOrdIndByKey(new TestKey(20));
            Assertions.fail();
        }
        catch(Exception e){ }
        
        try{
        
            testObject.getOrdIndByKey(new TestKey(-1));
            Assertions.fail();
        }
        catch(Exception e){ }
        
        
        // getIndyByKey method
        // TODO
        
        // getKeyByOrdInd method
        // Nominal case
        Assertions.assertTrue(testObject.getKeyByOrdInd(0) == new TestKey(0));
        Assertions.assertTrue(testObject.getKeyByOrdInd(9) == new TestKey(9));
        
        // Index out of range
        try{
        
            testObject.getKeyByOrdInd(-1);
            Assertions.fail();
        }
        catch(Exception e){ }
        
        try{
        
            testObject.getKeyByOrdInd(100);
            Assertions.fail();
        }
        catch(Exception e){}
        
        // getKeyByVal method
        // Nominal case
        Assertions.assertTrue(testObject.getKeyByVal(
                new String("five")).compareTo(new TestKey(5)) == 0);
        Assertions.assertTrue(testObject.getKeyByVal(
                new String("seven")).compareTo(new TestKey(7)) == 0);
        
        // Provided val does not exist
        try{
        
            testObject.getKeyByVal(new String("sixty"));
            Assertions.fail();
        }
        catch(Exception e){}
        
        try{
        
            testObject.getKeyByVal(new String(""));
            Assertions.fail();
        }
        catch(Exception e){}
        
        
        // lowerKey method
        // TODO
        
        
        // upperKey method
        // TODO
        
        
        // getLevelByKey method
        // TODO
        
        
        // size method
        Assertions.assertTrue(testObject.size() == 15);
        
        
        // getContainer method
        // TODO
        
        
        // getLevelKeys method
        // TODO
        
        
        // getLeafLevelKeys method
        ArrayList<TestKey> cmpLevelKeys = new ArrayList<TestKey>();
        cmpLevelKeys.add(new TestKey(0));
        
        cmpLevelKeys.add(new TestKey(6));
        cmpLevelKeys.add(new TestKey(7));
        cmpLevelKeys.add(new TestKey(8));
        cmpLevelKeys.add(new TestKey(10));
        
        cmpLevelKeys.add(new TestKey(11));
        cmpLevelKeys.add(new TestKey(12));
        cmpLevelKeys.add(new TestKey(13));
        cmpLevelKeys.add(new TestKey(14));
        
        cmpLevelKeys.add(new TestKey(4));

        cmpLevelKeys.add(new TestKey(5));

        ArrayList<TestKey> levelKeys = testObject.getLeafLevelKeys();

        boolean equals = true;
        int size = levelKeys.size();
        
        for(int i = 0; i < size; ++i){
        
            equals = equals && (cmpLevelKeys.get(i).compareTo(
                    levelKeys.get(i)) == 0);
        }

        Assertions.assertTrue(equals);
        
        
        // getLeafLevelSize method
        Assertions.assertTrue(testObject.getLeafLevelSize() == 11);
    }
    
    @Test
    public void setters_areCorrect() throws Exception{
    
        // TODO
        
        IncArbTree<TestKey, String> testObject = new IncArbTree<TestKey, String>();
        ArrayList<IncArbTree.Pair<TestKey, String> > param =
                new ArrayList<IncArbTree.Pair<TestKey, String> >();
        
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(0), new String("zero")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(1), new String("one")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(2), new String("two")));
        testObject.add(new TestKey(0), param);
        
        param.clear();
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(3), new String("three")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(4), new String("four")));
        testObject.add(new TestKey(1), param);
        
        param.clear();
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(5), new String("five")));
        testObject.add(new TestKey(2), param);
        
        param.clear();
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(6), new String("six")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(7), new String("seven")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(8), new String("eight")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(9), new String("nine")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(10), new String("ten")));
        testObject.add(new TestKey(3), param);
        
        param.clear();
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(11), new String("eleven")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(12), new String("twelve")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(13), new String("thrirteen")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(14), new String("fourteen")));
        testObject.add(new TestKey(9), param);
        
        // setKeyByInd method
        try{
        
            testObject.setKeyByInd(-1, new TestKey(99));
            Assertions.fail();
        }
        catch(Exception e){ }
        
        try{
        
            testObject.setKeyByInd(15, new TestKey(99));
            Assertions.fail();
        }
        catch(Exception e){ }
        
        testObject.setKeyByInd(5, new TestKey(55));
        Assertions.assertTrue(testObject.getKeyByInd(5).compareTo(
                new TestKey(55)) == 0);
        
        // setValByLevelOrdInd method
        try{
        
            testObject.setValByLevelOrdInd(-1, new String("err"));
            Assertions.fail();
        }
        catch(Exception e){ }
        
        try{
        
            testObject.setValByLevelOrdInd(15, new String("err"));
            Assertions.fail();
        }
        catch(Exception e){ }
        
        testObject.setValByLevelOrdInd(5, new String("nom"));
        Assertions.assertTrue(testObject.getByLevelOrdInd(5).compareTo(
                new String("nom")) == 0);
        
        
        // setValByInd method
        // todo
        
        
        // setNewRootByKey method
        testObject.setNewRootByKey(new TestKey(10));
        Assertions.assertTrue(testObject.getKeyByOrdInd(10).compareTo(
                new TestKey(10)) == 0);
        
    }
    
    @Test
    public void DFSMethods_areCorrect(){
    
        // TODO
        
        // initDFS method
        //  todo
        
        // getNextItemDFS method
        //  todo
        
        // hasNextDFS method
        //  todo
        
        // wasRecentLeaf method
        //  todo
        
        // getNumOfRecentStepBacks method
        //  todo
    }
    
    @Test
    public void auxiliaryMethods_areCorrect() throws Exception{
    
        // TODO
        
        IncArbTree<TestKey, String> testObject = new IncArbTree<TestKey, String>();
        ArrayList<IncArbTree.Pair<TestKey, String> > param =
                new ArrayList<IncArbTree.Pair<TestKey, String> >();
        
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(0), new String("zero")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(1), new String("one")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(2), new String("two")));
        testObject.add(new TestKey(0), param);
        
        param.clear();
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(3), new String("three")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(4), new String("four")));
        testObject.add(new TestKey(1), param);
        
        param.clear();
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(5), new String("five")));
        testObject.add(new TestKey(2), param);
        
        param.clear();
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(6), new String("six")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(7), new String("seven")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(8), new String("eight")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(9), new String("nine")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(10), new String("ten")));
        testObject.add(new TestKey(3), param);
        
        param.clear();
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(11), new String("eleven")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(12), new String("twelve")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(13), new String("thrirteen")));
        param.add(new IncArbTree.Pair<TestKey, String>(
                new TestKey(14), new String("fourteen")));
        testObject.add(new TestKey(9), param);
        
        // removeAll method
        testObject.removeAll();
        Assertions.assertTrue(testObject.size() == 0);
    }
}
