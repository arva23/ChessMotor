package chessmotortests.viewtests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerClockTests {
    
    public PlayerClockTests() {
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
    
        // parameterized constructor
        
        // upper left corner x coordinate component out of range
        // todo
        
        // upper left corner y coordinate component out of range
        // todo
        
        // visual width out of range
        // todo
        
        // visual height out of range
        // todo
        
        // nominal case
        // todo
    }
    
    @Test
    public void clockModifiers_areCorrect(){
    
        // setPlayersClock method
        // todo
        
        // start method
        // todo
        
        // stop method
        // todo
        
        // switchPlayer method
        // todo
    }
    
    @Test
    public void clockOperatorThread_isCorrect(){
    
        // busy wait: player clock error operation - inherited thread exception
        // todo
        
        // counting: player clock error operation - inherited thread exception
        // todo
    }
}
