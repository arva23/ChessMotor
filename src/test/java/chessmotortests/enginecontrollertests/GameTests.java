package chessmotortests.enginecontrollertests;

import chessmotor.enginecontroller.Game;
import chessmotor.enginecontroller.GameStatus;
import chessmotor.enginecontroller.GenericSaveStatus;
import chessmotor.enginecontroller.interfaces.IGame;
import chessmotor.view.ConsoleManager;
import chessmotor.view.IConsoleUI;
import chessmotor.view.IGameUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// integration tests
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
    public void statusGetter_isCorrect(){
    
        // nominal case
        
    }
    
    @Test
    public void statusSetter_isCorrect(){
    
        // save status is null
        
        // nominal case
        
    }
    
    public GenericSaveStatus generateRandomWinGame(){
    
        GenericSaveStatus generatedGameStatus = new GameStatus();
        
        // todo
        
        return generatedGameStatus;
    }
    
    public GenericSaveStatus generateRandomLoseGame(){
    
        GenericSaveStatus generatedGameStatus = new GameStatus();
        
        // todo
        
        return generatedGameStatus;
    }
    
    public GenericSaveStatus generateRandomDrawGame(){
    
        GenericSaveStatus generatedGameStatus = new GameStatus();
        
        // todo
        
        return generatedGameStatus;
    }
    
    @Test
    public void gameTriggererMethod_runGame_isCorrect(){
    
        // TODO
        
        // Uninitialized game
        IGame testGame = new Game();
        try{
        
            testGame.runGame();
            Assertions.fail();
        }
        catch(Exception e){}
        
        IConsoleUI consoleUI = new ConsoleManager();
        // todo
        
        testGame = new Game();
        
        int numOfGeneratedCases = 10;
        GenericSaveStatus generatedGameStatus = new GameStatus();
        
        // Generate with few available pieces (small piece stock)
        // where machine wins the game
        // The result is compared with a manually computed game, 
        // including score comparison
        // Generation is via arbitrary random game status generator 
        //  (auxiliary method in this test class)
        for(int caseI = 0; caseI < numOfGeneratedCases; ++caseI){
        
            try{
            
                generatedGameStatus = generateRandomWinGame();
            }
            catch(Exception e){
            
                System.out.println("Exception at " + caseI 
                        + ". human win test case of initialization.");
                Assertions.fail();
            }
            
            try{
            
                testGame.runGame();
            }
            catch(Exception e){
            
                System.out.println("Exception at " + caseI
                    + ". human win test case operation.");
                Assertions.fail();
            }
            
            Assertions.assertTrue(
                    testGame.getGamePlayStatus().compareTo("WIN") == 0);
        }
        
        // Generate with few available pieces (small piece stock)
        // where opponent wins the game
        // The result is compared with a manually computed game,
        // including score comparison
        // Generation is via arbitrary random game status generator
        //  (auxiliary method in this test class)
        for(int caseI = 0; caseI < numOfGeneratedCases; ++caseI){
        
            try{
            
                generatedGameStatus = generateRandomWinGame();
            }
            catch(Exception e){
            
                System.out.println("Exception at " + caseI 
                        + ". human lose test case of initialization.");
                Assertions.fail();
            }
            
            try{
            
                testGame.runGame();
            }
            catch(Exception e){
            
                System.out.println("Exception at " + caseI
                    + ". human lose test case operation.");
                Assertions.fail();
            }
            
            Assertions.assertTrue(
                    testGame.getGamePlayStatus().compareTo("LOSE") == 0);
        }
        
        // Generate with few available pieces (small piece stock)
        // where the game result is draw
        // The result is compared with a manually computed game,
        // including score comparison
        // Generation is via arbitrary random game status generator
        //  (auxiliary method in this test class)
        for(int caseI = 0; caseI < numOfGeneratedCases; ++caseI){
        
            try{
            
                generatedGameStatus = generateRandomWinGame();
            }
            catch(Exception e){
            
                System.out.println("Exception at " + caseI 
                        + ". draw test case of initialization.");
                Assertions.fail();
            }
            
            try{
            
                testGame.runGame();
            }
            catch(Exception e){
            
                System.out.println("Exception at " + caseI
                    + ". draw test case operation.");
                Assertions.fail();
            }
            
            Assertions.assertTrue(
                    testGame.getGamePlayStatus().compareTo("DRAW") == 0);
        }
    }
    
    @Test
    public void humanPlayerActionPerformerMethod_requestHumanPlayerAction_isCorrect(){
    
        // TODO
        
        // User has provided empty imput
        // todo
         
        // No source position defined or ill defined
        // todo
        
        // Selected source rank coordinate component is out of range
        // todo
        
        // Selected source file coordinate component is out of range
        // todo
        
        // Selected source position does not contain human piece
        // todo
        
        // Human player is in check
        // todo
        
        // No target position defined or ill defined
        // todo
        
        // Selected target rank coordinate component is out of range
        // todo
        
        // Selected target file coordinate component is out of range
        // todo
        
        // Selected piece by user is removed (not available on game board)
        // todo
        
        // User has wanted to select unavailable piece with zero steps
        // todo
        
        // Castling can not be executed
        // todo
        
        // Nominal castling execution
        // todo
        
        // Nominal first human step after first machine step
        // tood
        
        // Nominal first human step before first machine step
        // todo
        
        // Nominal further human step after further machine step
        // todo
    }
    
    @Test
    public void humanStepValidator_validateHumanPlayerStatus_isCorrect(){
    
        // todo
        
        // Human is in check
        // todo
        
        // Machine wins the game
        // todo
        
        // Nominal human step progression
        // todo
    }
    
    @Test
    public void machineStepValidator_validateMachinePlayerStepStatus_isCorrect(){
    
        // todo
        
        // Machine is in check
        // todo
        
        // Human wins the game
        // todo
        
        // Nominal machine step progression
        // todo
    }
    
    @Test
    public void reactWithStepToHumanAction_selectNextMachineStep_isCorrect(){
    
        // todo
        // Generate (bypass) manually a generation result - modify stepSequences 
        // in such a way that the result will contain various cumulated values 
        // including redundant ones (multiplicity is allowed due to empty squares 
        // on gameborad)
    
        // No further steps from potential current positions
        // todo
        
        // Machine is in check and solved sucessfully
        // todo
        
        // Machine is in check and can not solve successfully
        // todo
        
        // Nominal machine step selection
        // todo
    }
    
    @Test
    public void auxiliaryMethods_areCorrect(){
    
        // TODO
        
        // setStatus method
        // todo
        // Nominal case
        
        // getStatus method
        // todo
        // Nominal case
        
        // setDepth method
        // Test of method forwarding
        // todo
        
        // getSourceStepHistory method
        // todo
        
        // getTargetStepHistory method
        // todo
        
        // machineBegun method method
        // todo
        
        // SYNCHRONIZER METHODS
        
        // waitForDataRead method
        // Timeout during waiting
        // Nominal case
        // todo
        
        // signalForDataRead method
        // Premature signal for waiting termination
        // Nominal case
        // todo
        
        // waitForDataSave method
        // Timeout during waiting
        // Nominal case
        // todo
        
        // signalForDataSave method
        // Premature signal for waiting termination
        // Nominal case
        // todo
    }
}
