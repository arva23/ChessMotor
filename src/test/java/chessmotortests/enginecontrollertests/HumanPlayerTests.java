package chessmotortests.enginecontrollertests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HumanPlayerTests {
    
    public HumanPlayerTests() {
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
        
        // stepSequencesRef is null
        // todo
        
        // removedPiecesRef is null
        // todo
        
        // removedMachinePiecesRef is null
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
    public void giveUpFunction_isCorrect(){
    
        // nominal case
        // todo
    }
    
    @Test
    public void stepGeneratorMethod_isCorrect(){
    
        // action is empty
        // todo
        
        // ill conditioned action
        // todo
        
        // ill conditioned source position
        // todo
        
        // source rank out of range
        // todo
        
        // source file out of range
        // todo
        
        // no available piece at requested position
        // todo
        
        // player is in check
        // todo
        
        // ill conditioned target position
        // todo
        
        // target rank out of range
        // todo
        
        // target file out of range
        // todo
        
        // illegally selected step by chosen piece
        // todo
        
        // castling is unavailable
        // todo
        
        // nominal case: castling
        // todo
        
        // nominal case: promotion
        // todo
        
        // nominal case: piece hit
        // todo
        
        // nominal case: occupying empty square
        // todo
    }
    
    @Test
    public void statusValidator_isCorrect(){
    
        // human is in check
        // todo
        
        // human lose
        // todo
        
        // nominal case
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
