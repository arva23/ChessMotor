package genmathTests;

import genmath.LinTreeMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LinTreeMapTests {
    
    public LinTreeMapTests() {
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
    public void auxiliaryPairTests_areCorrect() throws Exception{

        // using wrappers of primitives

        // constructors
        LinTreeMap.Pair<IncTestKey, Integer> testObject;

        // default constructor
        testObject = new LinTreeMap.Pair<IncTestKey, Integer>();

        // pair initialization
        testObject = new LinTreeMap.Pair<IncTestKey, Integer>(new IncTestKey(0), 1);
    }


    @Test
    public void constructors_areCorrect(){

        // default constructor
        LinTreeMap<IncTestKey, Integer> testObject;
    }


    @Test
    public void add_methodIsCorrect() throws Exception{

        LinTreeMap<IncTestKey, Double> testObject = new LinTreeMap<IncTestKey, Double>();

        // redundancy test
        testObject.add(new IncTestKey(6), 6.0);

        assertEquals("The item has already been inserted earlier (redundancy is not allowed).",
                assertThrows(Exception.class, ()->{

                    testObject.add(new IncTestKey(6), 6.0);
                }).getMessage());


        // adding multiple elements
        /*
        testObject.add(new IncTestKey(9), 9.0);
        testObject.add(new IncTestKey(8), 8.0);
        testObject.add(new IncTestKey(7), 7.0);
        testObject.add(new IncTestKey(5), 5.0);
        testObject.add(new IncTestKey(4), 4.0);
        testObject.add(new IncTestKey(3), 3.0);
        testObject.add(new IncTestKey(2), 2.0);
        testObject.add(new IncTestKey(1), 1.0);
        */

        testObject.add(new IncTestKey(1), 1.0);
        testObject.add(new IncTestKey(3), 3.0);
        testObject.add(new IncTestKey(4), 4.0);
        testObject.add(new IncTestKey(9), 9.0);
        testObject.add(new IncTestKey(5), 5.0);
        testObject.add(new IncTestKey(2), 2.0);
        testObject.add(new IncTestKey(7), 7.0);


        assertEquals(0, testObject.getIndByKey(new IncTestKey(1)));
        assertEquals(1, testObject.getIndByKey(new IncTestKey(2)));
        assertEquals(2, testObject.getIndByKey(new IncTestKey(3)));
        assertEquals(3, testObject.getIndByKey(new IncTestKey(4)));


        // pair form invocation
        testObject.add(new LinTreeMap.Pair<IncTestKey, Double>(new IncTestKey(50), 6.0));
    }


    @Test
    public void getters_areCorrect() throws Exception{

        LinTreeMap<IncTestKey, Double> testObject = new LinTreeMap<IncTestKey, Double>();

        testObject.add(new IncTestKey(1), 2.0);
        testObject.add(new IncTestKey(3), 4.0);
        testObject.add(new IncTestKey(4), 5.0);
        testObject.add(new IncTestKey(2), 3.0);

        // getByInd
        assertTrue(2.0 == testObject.getByInd(0));
        assertEquals("Index out of bounds.", assertThrows(Exception.class, ()->{

            testObject.getByInd(6);
        }).getMessage());


        // getByKey
        assertTrue(4.0 == testObject.getByKey(new IncTestKey(3)));
        assertTrue(2.0 == testObject.getByKey(new IncTestKey((8))));


        // getIndByKey
        assertTrue(2 == testObject.getIndByKey(new IncTestKey(3)));
        assertTrue(testObject.size() - 1 == testObject.getIndByKey(new IncTestKey(10)));

        // getKeyByInd
        assertTrue(new IncTestKey(2).compareTo(testObject.getKeyByInd(1)) == 0);
        assertEquals("Index out of bounds.", assertThrows(Exception.class, ()->{

            testObject.getKeyByInd(6);
        }).getMessage());

        // size
        assertTrue(4 == testObject.size());
    }


    @Test
    public void setters_areCorrect() throws Exception{

        LinTreeMap<IncTestKey, String> testObject = new LinTreeMap<IncTestKey, String>();
        testObject.add(new IncTestKey(3), "three");
        testObject.add(new IncTestKey(4), "four");
        testObject.add(new IncTestKey(2), "two");

        // set key by index
        // automatic sort after modification
        testObject.setKeyByInd(0, new IncTestKey(5));
        assertTrue(testObject.getKeyByInd(2).compareTo(new IncTestKey(5)) == 0);

        assertEquals("Index out of bounds.", assertThrows(Exception.class, ()->{

            testObject.setKeyByInd(4, new IncTestKey(9));
        }).getMessage());


        // set value by index
        testObject.setValByInd(2, "nine");

        assertEquals("Index out of bounds.", assertThrows(Exception.class, ()->{

            testObject.setValByInd(4, "err");
        }).getMessage());


        // set key and value by index
        testObject.setByInd(2, new IncTestKey(8), "five");

        assertTrue(testObject.getKeyByInd(2).compareTo(new IncTestKey(8)) == 0
                && testObject.getByInd(2).compareTo("five") == 0);

        assertEquals("Index out of bounds.", assertThrows(Exception.class, ()->{

            testObject.setByInd(9, new IncTestKey(9), "err");
        }).getMessage());
    }


    @Test
    public void gettingNearestElementsAreCorrect() throws Exception{

        LinTreeMap<IncTestKey, Double> testObject = new LinTreeMap<IncTestKey, Double>();

        // case: empty container (lowerKey)
        assertEquals("Container is empty.", assertThrows(Exception.class, ()->{

            testObject.lowerKey(new IncTestKey(3));
        }).getMessage());

        // case: empty container (higherKey)
        assertEquals("Container is empty.", assertThrows(Exception.class, ()->{

            testObject.lowerKey(new IncTestKey(3));
        }).getMessage());

        testObject.add(new IncTestKey(3), 3.0);
        testObject.add(new IncTestKey(4), 4.0);
        testObject.add(new IncTestKey(2), 2.0);

        // case: no lower key, returning first element from the container
        assertTrue(0 == testObject.getIndByKey(testObject.lowerKey(new IncTestKey(0))));

        // case: no upper key, returning last element from the container
        assertTrue(testObject.size() - 1 == testObject.getIndByKey(testObject.higherKey(new IncTestKey(6))));

        // case: equal element found in the container (lowerKey)
        assertTrue(new IncTestKey(3).compareTo(testObject.lowerKey(new IncTestKey(3))) == 0);

        // case: equal element found in the container (higherKey)
        assertTrue(new IncTestKey(3).compareTo(testObject.higherKey(new IncTestKey(3))) == 0);
    }


    @Test
    public void subMap_isCorrect() throws Exception{

        LinTreeMap<IncTestKey, Double> testObject = new LinTreeMap<IncTestKey, Double>();

        testObject.add(new IncTestKey(1), 2.0);
        testObject.add(new IncTestKey(3), 4.0);
        testObject.add(new IncTestKey(4), 5.0);
        testObject.add(new IncTestKey(2), 3.0);

        LinTreeMap<IncTestKey, Double> subTestObject = new LinTreeMap<IncTestKey, Double>();
        subTestObject.add(new IncTestKey(3), 4.0);
        subTestObject.add(new IncTestKey(4), 5.0);

        LinTreeMap<IncTestKey, Double> subTestObject2 = testObject.subMap(new IncTestKey(3), new IncTestKey(4));

        assertTrue(subTestObject.getByInd(0).compareTo(subTestObject2.getByInd(0)) == 0);
        assertTrue(subTestObject.getByInd(1).compareTo(subTestObject2.getByInd(1)) == 0);
    }
}
