package chessmotortests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class StepDecisionTreeTests {
    
    public StepDecisionTreeTests() {
    
        
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
        
        // pieces with null value
        
        // step history with null value
        
        // game board with null value
        
        // minimum convolutional threshold is negative
    
        // depth is negative
        
        // cumulative negative change threshold is under one
    
        // copy consuctor
        
    }
    
    
    @Test
    public void treeBuilding_buildStepSequence_isCorrect(){
    
        // TODO
        
        // Initial generation test
        // set depth to 1, 
        //  set 8 pieces
        //              1pawn
        //              2pawn
        //              3pawn
        //              lrook
        //              lknight
        //              lbishop
        //              lking
        //              lqueen
        // These can be set by gameTable status modification before first step
        //  in way of elimination of unneeded pieces
        // Generate lookAhead(1) step decision tree
        // Compare the stepDecisionTree container with the manually created results
        
        // Check mate test, not available generated further steps
        // Scenario where queens are covered a piece(for example king) in a corner
        // This tests the termination criteria of generation, the check mate status
        
        // Multiple stepback test with the above mentioned 8 pieces, depth is set to 4
        
        // An entire step decision tree generation starting from initial position
        // and with depth of 10, graph prunning is set on a given step sequence/graph route
        // by a small allowance of cumulative value threshold
        // Result is manually compared
        
        // Parallel, multi-threaded decision tree generation test
        // Case of thread initiation after the recent leaf level size by number
        // of nodes reaches the minimum available concurrent thread number (2)
        // and at most the maximum available concurrent threads
        // Testing concurrency based tree generation
        
    }
    
    @Test
    public void treeAddition_continueStepSequences_isCorrect(){
    
        // TODO
        
        // Step decision tree is not generated (true initGen)1
        
        // Adding a new leaf level to existing leaf level
        // The main condition is the index keeping by the algorithm
        // The generated new steps from a step are set manually from multiple 
        // old leaf level nodes
    }
    
    @Test
    public void cyclicKeyPrefixRemoval_trimKeys_isCorrect(){
    
        // TODO
        
        // Remove an identical prefix for all nodes
        // This is executed by a list of Pair objects
        // Since the key must be represented as literal, the substring operation 
        // can be executed on it.
        // Results are compared by manually generated objects
        
    }
    
    @Test
    public void auxiliaryMethods_areCorrect(){
    
        // TODO
        
        // size method
        // Test of method forwarding
        
        
        // getLeafLevelKeys method
        // Test of method forwarding
        
        
        // getByKey method
        // Test of method forwarding
        
        
        // setNewRootByKey method
        // Test of method forwarding
        
        
        // getDepth method
        // Get depth
        
        
        // setDepth method
        // Setting depth with negative value
        
        // Setting depth nominal case (positive value)
        
        
        // addOne method
        // Test of method forwarding
    }
}
