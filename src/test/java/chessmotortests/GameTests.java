package chessmotortests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameTests {
    
    public GameTests() {
    
        
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
        
        // Initialization of game by provided parameters
    }
    
    @Test
    public void gameTriggererMethod_runGame_isCorrect(){
    
        // TODO
        
        // Uninitialized game
        
        // Failed to get the concurrent thread number information
        
        // Generate with few available pieces (small piece stock)
        // where machine wons the game
        // The result is compared with a manually computed game, 
        // including score comparison
        
        // Generate with few available pieces (small piece stock)
        // where opponent wons the game
        // The result is compared with a manually computed game,
        // including score comparison
        
        // Generate with few available pieces (small piece stock)
        // where the game result is draw
        // The result is compared with a manually computed game,
        // including score comparison
    }
    
    @Test
    public void humanPlayerActionPerformerMethod_requestPlayerAction_isCorrect(){
    
        // TODO
        
        // User has provided empty imput
     
        // User has not provided piece input
        
        // User has wanted to select unavailable piece (no piece 
        // exists with such identifier), name resolution error
    
        // Selected file coordinate component is out of range
        
        // Selected rank coordinate component is out of range
        
        // Selected piece by user is removed (not available on game board)
    
    }
    
    @Test
    public void reactWithStepToHumanAction_selectNextStep_isCorrect(){
    
        // TODO
        
        // Generate (bypass) manually a genration result - modify stepSequences 
        // in such a way that the result will contain various cumulated values 
        // including redundant ones (multiplicity is allowed due to empty squares 
        // on gameborad)
    
    }
    
    @Test
    public void auxiliaryMethods_areCorrect(){
    
        // TODO
        
        // setDepth method
        // Test of method forwarding
    }
}
