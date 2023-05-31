package chessmotortests.enginecontrollertests;

import chessmotor.enginecontroller.GenStepKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GenStepKeyTests {
    
    public GenStepKeyTests() {
        
        
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
        GenStepKey testObject = new GenStepKey();
        
        // Constructor with provided key value
        testObject = new GenStepKey("test");
    }
    
    @Test
    public void auxiliaryMethods_areCorrect(){
    
        // TODO
        GenStepKey testObject = new GenStepKey();
        
        // maxVal method
        Assertions.assertTrue(new GenStepKey("z").compareTo(
                new GenStepKey().maxVal()) == 0);
        
        // less method
        // todo
        
        // greater method
        // todo
        
        // sgn method
        // todo
        
        // compareTo method
        // todo
        
        // len method
        // todo
        
        // at method
        // todo
        
        // at method
        // todo
        
        // add method
        // todo
        
        // subtract method
        // todo
        
        // toString method
        // todo
        
        // fromString method
        // todo
        
        // isPlaceholder method
        // todo
    }
}
