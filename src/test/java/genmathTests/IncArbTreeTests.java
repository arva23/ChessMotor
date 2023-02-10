package genmathTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
        
        // Copy constructor
        
    }
    
    @Test
    public void insertionMethods_areCorrect(){
    
        // TODO
        
        // addOne method
        // Include a new child node with a given Pair under a parent key 
        // onto second level
        // Container is empty, nominal case
        
        // Include a new child node with a given Pair right after root node
        // Container contains only the root node
        
        // Include a new child node with a given Pair under a parent key
        // one leaf level
        // container is not empty, nominal case
        
        // no parent key found
        
        // addOne method
        // Do the same test cases with this method as at the addOne method
    
    }
    
    @Test
    public void getters_areCorrect(){
    
        // TODO
        
        // getByLevelOrdInd method
        // Index out of bounds
        
        // Item is placeholder
        
        
        // getByInd
        
        
        // getByKey method
        // Nominal case
        
        // Provided key does not exist
        
        
        // getOrdIndByKey method
        // Nominal case
        
        // Provided key does not exist
        
        
        // getIndyByKey method
        
        
        // getKeyByOrdInd method
        // Nominal case
        
        // Index out of range
        
        
        // getKeyByVal method
        // Nominal case
        
        // Provided key does not exist
        
        
        // lowerKey method
        
        
        // upperKey method
        
        
        // getLevelByKey method
        
        
        // size method
        
        
        // getContainer method
        
        
        // getLevelKeys method
        
        
        // getLeafLevelKeys method
        
    }
    
    @Test
    public void setters_areCorrect(){
    
        // TODO
        
        // setKeyByInd method
        
        
        // setValByLevelOrdInd method
    
        
        // setValByInd method
        
        
        // setNewRootByKey method
        
    }
    
    @Test
    public void DFSMethods_areCorrect(){
    
        // TODO
        
        // initDFS method
        
        
        // getNextItemDFS method
        
        
        // hasNextDFS method
        
        
        // wasRecentLeaf method
        
        
        // getNumOfRecentStepBacks method
        
    }
    
    @Test
    public void auxiliaryMethods_areCorrect(){
    
        // TODO
        
        // removeAll method
        
    }
}
