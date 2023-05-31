package chessmotortests.piecetypetests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RookTests {
    
    public RookTests() {
        
        
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
    
        // empty constructor
        //  todo
        
        // parameterized constructor
        //  todo
    }
    
    @Test
    public void overridden_generateSteps_method_isCorrect(){
        
        // The subject method which is being tested only generates free position 
        //  or free position until first hit positions including hit as well.
        //  Further higher level step array examinations are not placed in the 
        //  subject method.
    
        // generate steps from baseline position
        //  todo
        
        // generate steps from gameboard boundary position
        //  (rank is in [1, 7] and file is 0)
        //  todo
        
        // generate steps from gameboard boundary position
        //  (rank is in [1, 7] and file is 7)
        //  todo
        
        // generate steps from gameboard boundary position
        //  (rank is 0 and file is in [1, 7])
        //  todo
        
        // generate steps from gameboard boundary position
        //  (rank is 7 and file is in [1, 7])
        //  todo
        
        // generate steps from inner gameboard position
        //  (rank is in [1, 7] and file is in [1, 7])
        //  todo
        
        // generate steps from gameboard boundary position
        //  with at least one collision limiter position
        //  (rank is in [1, 7] and file is 0)
        //  todo
        
        // generate steps from gameboard boundary position
        //  with at least one collision limiter
        //  (rank is in [1, 7] and file is 7)
        //  todo
        
        // generate steps from gameboard boundary position
        //  with at least one collision limiter
        //  (rank is 0 and file is in [1, 7])
        //  todo
        
        // generate steps from gameboard boundary position
        //  with at least one collision limiter
        //  (rank is 7 and file is in [1, 7])
        //  todo
        
        // generate steps from inner gameboard position
        //  with at least one collision limiter
        //  (rank is in [1, 7] and file is in [1, 7])
        //  todo
    }
}
