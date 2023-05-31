package chessmotortests.viewtests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GUIViewTests {
    
    public GUIViewTests() {
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
    public void constructor_isCorrect(){
    
        // Game controller object is null
        // todo
        
        // Window width is out of range (lower and upper bound violation as well)
        // todo
        
        // Window height is out of range (lower and upper bound violation as well)
        // todo
        
        // Top left corner window x position is out of range 
        // (lowe and upper bound violation as well)
        // todo
        
        // Top left corner window y position is out of range 
        // (lower and upper bound violation as well)
        // todo
        
        // Nominal case
        // todo
    }
    
    @Test
    public void loadGame_isCorrect(){
    
        // Nominal case
        // todo
    }
    
    @Test
    public void auxiliaryMethods_areCorrect(){
    
        // run method
        // todo
        
        // printErr method
        // todo
        
        // updateGameStatus method
        // todo
        
        // switchPlayerClock method
        // todo
    }
    
    @Test
    public void readPlayerAction_isCorrect(){
    
        // Illegal source position selection
        // via wrong not owned piece or empty square
        // todo
        
        // Illegal target position selection
        // via wrong not owned piece or position
        
        // Castling is not selected in order (rook first)
        // todo
        
        // Castling is performed well
        // todo
        
        // Nominal case
        // todo
    }
    
    @Test
    public void selectPawnReplacement_isCorrect(){
    
        // Pawn has not reached enemy base line
        // todo
        
        // Nominal case
        // todo
    }
    
    @Test
    public void applyGenPlayerAction_isCorrect(){
    
        // Source rank is out of range (lower and upper bound violation as well)
        // todo
        
        // Soruce file is out of range (lower and upper bound violation as well)
        // todo
        
        // Target rank is out of range (lower and upper bound violation as well)
        // todo
        
        // Target rank is out of range (lower and upper bound violation as well)
        // todo
        
        // Nominal case
        // todo
    }
    
}
