package chessmotortests.enginecontrollertests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MachinePlayerTests {
    
    public MachinePlayerTests() {
    
        
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
        
        // gameRef is null
        // todo
        
        // piecesRef is null
        // todo
        
        // gameBoardRef is null
        // todo
        
        // stepSquencesRef is null
        // todo
        
        // removedPiecesRef is null
        // todo
        
        // removedHumanPiecesRef is null
        // todo
        
        // stepIdRef is null
        // todo
        
        // time is null
        // todo
        
        // intervalStart is null
        // todo
        
        // nominal case
        // todo
    }
    
    @Test
    public void clockOperators_areCorrect(){
    
        // startClock method
        // todo
        
        // stopClock method
        // todo
        
        // getTime method
        // todo
    }
    
    @Test
    public void firstStepGenerator_isCorrect(){
    
        // generateFirstMachineStep method invocation
        // todo
    }
    
    @Test
    public void buildStrategy_isCorrect(){
    
        // can not obtain number of concurrent threads
        // todo
        
        // nominal case: generate strategy in single-threaded way
        // todo
        
        // nominal case: generate strategy in multi-threaded way
        // todo
    }
    
    @Test
    public void statusValidator_isCorrect(){
    
        // machine is in check
        // todo
        
        // machine lose
        // todo
        
        // nominal case
        // todo
    }
    
    @Test
    public void stepGeneratorMethod_isCorrect(){
    
        // no further steps are available: machine lose
        // todo
        
        // machine is in check: position defended
        // todo
        
        // machine is in check: position can not be defended: machine lose
        // todo
        
        // nominal case: machine castling
        // todo
        
        // nominal case: machine promotion
        // todo
        
        // nominal case: machine hit
        // todo
        
        // nominal case: occupying empty square
        // todo
    }
    
    @Test
    public void removedPiecesGetter_isCorrect(){
    
        // no removed pieces
        // todo
        
        // nominal case
        // todo
    }
}
