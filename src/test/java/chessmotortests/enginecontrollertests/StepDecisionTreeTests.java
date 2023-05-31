package chessmotortests.enginecontrollertests;

import chessmotor.enginecontroller.GameBoardData;
import chessmotor.enginecontroller.Step;
import chessmotor.enginecontroller.StepDecisionTree;
import chessmotor.enginecontroller.piecetypes.Bishop;
import chessmotor.enginecontroller.piecetypes.King;
import chessmotor.enginecontroller.piecetypes.Knight;
import chessmotor.enginecontroller.piecetypes.Pawn;
import chessmotor.enginecontroller.piecetypes.Queen;
import chessmotor.enginecontroller.piecetypes.Rook;
import chessmotor.enginecontroller.GenStepKey;
import chessmotor.enginecontroller.PieceContainer;
import chessmotor.view.ConsoleManager;
import chessmotor.view.IConsoleUI;
import genmath.IncArbTree;
import java.util.Stack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

// subsystem tests
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
    public void constructors_areCorrect() throws Exception{
    
        // TODO
        
        IConsoleUI consoleUI = new ConsoleManager();
        PieceContainer pieces = new PieceContainer();
        Stack<Step> stepHistory = new Stack<Step>();
        GameBoardData gameBoard = new GameBoardData();
        int depth = 10;
        int cumulativeNegativeChangeThreshold = 2;
        double minConvThreshold = 0.5;
        
        StepDecisionTree testObject;
        
        // pieces with null value
        pieces = null;
        
        try{
        
            testObject = new StepDecisionTree(consoleUI, true, null, 
                stepHistory, gameBoard, depth, cumulativeNegativeChangeThreshold, 
                minConvThreshold, 4, 0, 1024);
            Assertions.fail();
        }
        catch(Exception e){ }
        
        
        // step history with null value
        pieces = new PieceContainer();
        stepHistory = null;
        
        try{
        
            testObject = new StepDecisionTree(consoleUI, true, pieces, 
                stepHistory, gameBoard, depth, cumulativeNegativeChangeThreshold, 
                minConvThreshold, 4, 0, 1024);
            Assertions.fail();
        }
        catch(Exception e){ }
        
        // game board with null value
        stepHistory = new Stack<Step>();
        gameBoard = null;
        
        try{
        
            testObject = new StepDecisionTree(consoleUI, true, pieces, 
                stepHistory, gameBoard, depth, cumulativeNegativeChangeThreshold, 
                minConvThreshold, 4, 0, 1024);
            Assertions.fail();
        }
        catch(Exception e){ }
        
        // minimum convolutional threshold is negative
        gameBoard = new GameBoardData();
        minConvThreshold = -1;
        
        try{
        
            testObject = new StepDecisionTree(consoleUI, true, pieces, 
                stepHistory, gameBoard, depth, cumulativeNegativeChangeThreshold, 
                minConvThreshold, 4, 0, 1024);
            Assertions.fail();
        }
        catch(Exception e){ }
        
        // depth is negative
        minConvThreshold = 2;
        depth = -1;
        
        try{
        
            testObject = new StepDecisionTree(consoleUI, true, pieces, 
                stepHistory, gameBoard, depth, cumulativeNegativeChangeThreshold, 
                minConvThreshold, 4, 0, 1024);
            Assertions.fail();
        }
        catch(Exception e){ }
        
        // cumulative negative change threshold is under one
        depth = 10;
        cumulativeNegativeChangeThreshold = -1;
        
        try{
        
            testObject = new StepDecisionTree(consoleUI, true, pieces, 
                stepHistory, gameBoard, depth, cumulativeNegativeChangeThreshold, 
                minConvThreshold, 4, 0, 1024);
            Assertions.fail();
        }
        catch(Exception e){ }
        
        // copy consuctor
        cumulativeNegativeChangeThreshold = 10;
        testObject =  new StepDecisionTree(consoleUI, true, pieces, 
                stepHistory, gameBoard, depth, cumulativeNegativeChangeThreshold, 
                minConvThreshold, 4, 0, 1024);
        
        StepDecisionTree testObject2;
        
        try{
        
            testObject2 = new StepDecisionTree(null);
            Assertions.fail();
        }
        catch(Exception e){ }

        // copy constructor        
        testObject2 = new StepDecisionTree(testObject);
    }
    
    
    @Test
    public void treeBuilding_run_isCorrect() throws Exception{
    
        // TODO
        
        // Initial generation test
        // set depth to 1
        // setup pieces
        // These can be set by gameTable status modification before first step
        //  in way of elimination of unneeded pieces
        
        IConsoleUI consoleUI = new ConsoleManager();
        
        PieceContainer pieces = new PieceContainer();
        
        Stack<Step> stepHistory = new Stack<Step>();
        
        GameBoardData gameBoard = new GameBoardData();
                
        for(int i = 0; i < 8; ++i){

            pieces.set(i, new Pawn(i, true, -3.0, 1, i));
            gameBoard.set(1, i, i);
            
            pieces.set(16 + i, new Pawn(16 + i, false, 3.0, 6, i));
            gameBoard.set(6, i, 16 + i);
        }

        pieces.set(8, new Rook(8, true, -14.0, 0, 0));
        pieces.set(9, new Knight(9, true,  -8.0, 0, 1));
        pieces.set(10, new Bishop(10, true, -14.0, 0, 2));
        pieces.set(11, new King(11, true, -8.0, 0, 3));
        pieces.set(12, new Queen(12, true, -28.0, 0, 4));
        pieces.set(13, new Bishop(13, true, -14.0, 0, 5));
        pieces.set(14, new Knight(14, true, -8.0, 0, 6));
        pieces.set(15, new Rook(15, true, -14.0, 0, 7));
        
        gameBoard.set(0, 0, 8);
        gameBoard.set(0, 1, 9);
        gameBoard.set(0, 2, 10);
        gameBoard.set(0, 3, 11);
        gameBoard.set(0, 4, 12);
        gameBoard.set(0, 5, 13);
        gameBoard.set(0, 6, 14);
        gameBoard.set(0, 7, 15);

        // initializing opponent pieces

        pieces.set(16 + 8, new Rook(16 + 8, false, 14.0, 7, 0));
        pieces.set(16 + 9, new Knight(16 + 9, false, 8.0, 7, 1));
        pieces.set(16 + 10, new Bishop(16 + 10, false, 14.0, 7, 2));
        pieces.set(16 + 11, new King(16 + 11, false, 8.0, 7, 3));
        pieces.set(16 + 12, new Queen(16 + 12, false, 28.0, 7, 4));
        pieces.set(16 + 13, new Bishop(16 + 13, false, 14.0, 7, 5));
        pieces.set(16 + 14, new Knight(16 + 14, false, 8.0, 7, 6));
        pieces.set(16 + 15, new Rook(16 + 15, false, 14.0, 7, 7));

        gameBoard.set(7, 0, 16 + 8);
        gameBoard.set(7, 1, 16 + 9);
        gameBoard.set(7, 2, 16 + 10);
        gameBoard.set(7, 3, 16 + 11);
        gameBoard.set(7, 4, 16 + 12);
        gameBoard.set(7, 5, 16 + 13);
        gameBoard.set(7, 6, 16 + 14);
        gameBoard.set(7, 7, 16 + 15);
        
        // filling empty squares
        for(int rankInd = 0; rankInd < 8; ++rankInd){

            for(int fileInd = 2; fileInd < 6; ++fileInd){

                gameBoard.set(fileInd, rankInd, -1);
            }
        }
        
        int depth = 10;
        int cumulativeNegativeChangeThreshold = 2;
        double minConvThreshold = 0.5;
        StepDecisionTree testObject = new StepDecisionTree(consoleUI, true, pieces,
            stepHistory, gameBoard, depth, cumulativeNegativeChangeThreshold, 
            minConvThreshold, 4, 0, 1024);
        
        // Generate lookAhead(1) step decision tree
        // Compare the stepDecisionTree container with the manually created results
        testObject.generateFirstMachineStep();
        testObject.run();
        // manually generated log:
        StepDecisionTree testObject2 = new StepDecisionTree(testObject);
        //  todo
        //  ...
        testObject2.addOne(new GenStepKey(), new GenStepKey(), new Step());
        testObject2.addOne(new GenStepKey(), new GenStepKey(), new Step());
        testObject2.addOne(new GenStepKey(), new GenStepKey(), new Step());
        testObject2.addOne(new GenStepKey(), new GenStepKey(), new Step());
        testObject2.addOne(new GenStepKey(), new GenStepKey(), new Step());
        testObject2.addOne(new GenStepKey(), new GenStepKey(), new Step());
        testObject2.addOne(new GenStepKey(), new GenStepKey(), new Step());
        // ...
        IncArbTree<GenStepKey, Step> testObjectTree = testObject.getContainer();
        IncArbTree<GenStepKey, Step> testObject2Tree = testObject2.getContainer();
        
        Assertions.assertTrue(testObjectTree.size() == testObject2Tree.size());
        
        int sizeOfTree = testObjectTree.size();
        
        for(int i = 0; i < sizeOfTree; ++i){
        
            Assertions.assertTrue(
                    testObjectTree.getByLevelOrdInd(i).equals(testObject2Tree.getByLevelOrdInd(i)));
        }
        
        
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
    
    // nested methods
    @Test
    public void specialStepCaseMethod_evaluateSpecialStepCases_isCorrect(){
    
        // TODO
    }
    
    @Test
    public void generalStepCaseMethod_evaluateGeneralStepCases_isCorrect(){
    
        // TODO
        
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
        // Nominal case including 0 size
        // todo
        
        // getLevelKeys method
        // Test of method forwarding
        // Nominal case, other cases are handled at original test routine
        // todo
        
        // getLeafLevelKeys method
        // Test of method forwarding
        // Nominal case, other cases are handled at original test routine
        // todo
        
        // getByKey method
        // Test of method forwarding
        // Nominal case, other cases are handled at original test routine
        // todo
        
        // setNewRootByKey method
        // Test of method forwarding
        // Nominal case, other cases are handled at orignal test routine
        // todo
        
        // setFracNo method
        // Lower bound violation
        // Upper bound violation
        // Nominal case
        // todo
        
        // getDepth method
        // Get depth
        // Nominal case
        // todo
        
        // getContainer method
        // Nonimal case
        // todo
        
        // setDepth method
        // Lower bound violation
        // Nominal case
        // todo
        
        // unit method
        // todo and method forwarding
        // Nominal case, other cases are handled at original test routine
        // Setting depth with negative value
        // Setting depth nominal case (positive value)
        
        // addOne method
        // Test of method forwarding
        // Nominal case, other case ares handled at original test routine
        // todo
        
        // addToHistoryStack method
        // Nominal case
        // todo
        
        // reserverMem method
        // Test of method forwarding
        // Nominal case, other cases are handled at original test routine
        // todo
        
        // generateFirstMachineStep
        // No available steps to be generated (zero number of further steps)
        // Nominal case
        // todo
    }
}
