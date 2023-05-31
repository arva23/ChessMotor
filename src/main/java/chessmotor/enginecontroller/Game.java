package chessmotor.enginecontroller;

import chessmotor.enginecontroller.interfaces.IGame;
import chessmotor.enginecontroller.piecetypes.Bishop;
import chessmotor.enginecontroller.piecetypes.King;
import chessmotor.enginecontroller.piecetypes.Knight;
import chessmotor.enginecontroller.piecetypes.Pawn;
import chessmotor.enginecontroller.piecetypes.Queen;
import chessmotor.enginecontroller.piecetypes.Rook;
import chessmotor.view.IConsoleUI;
import chessmotor.view.IGameUI;
import genmath.genmathexceptions.ValueOutOfRangeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;

/**
 * Main game play manager class that handles and manages all the game events 
 * including user action requests and outcomes. For additional notes about the 
 * evolution of the engine, see available notes in related text file
 * @author arva
 */
public class Game implements IGame{

    private IConsoleUI consoleUI;
    
    private Condition statusUpdateLock;
    private Condition statusSaveLock;
    
    private IGameUI gameUI;
    
    // initialization of game is required
    private boolean initialized;
    // which player begins with the white pieces
    private boolean machineBegins;
    private boolean machineComes;
    
    private HumanPlayer humanPlayer;
    private MachinePlayer machinePlayer;
    
    private Duration timeLimit;
    
    private AtomicBoolean playGame;
    private String gamePlayStatus;
    // active in game piece container
    private PieceContainer pieces;
    // the actual game board to operate with
    //  use of int instead of String due to less memory usage
    private GameBoardData gameBoard;
    // recent status of generated (arbirary incomplete n-ary tree) step sequences 
    //  for further step decisions
    // explicit step n-ary decision tree is requied in order to trace further steps
    //  to be able to locate the origin step of leaves that are required for next 
    //  step generations
    private StepDecisionTree stepSequences;
    private Integer stepId;// starting from 1 (the first step)
    private Stack<Step> sourceStepHistory;
    private Stack<Step> targetStepHistory;
    
    private Stack<Integer> removedHumanPieces;
    private Stack<Integer> removedMachinePieces;
    
    /**
     * Empty constructor for parametrically uninitialized objects
     */
    public Game(){
    
        initialized = false;
    }
    
    /**
     * Constructor for parametric initialization and further operation
     * @param consoleUI Console line interface for literal based messaging 
     *        and for error messages
     * @param gameUI Graphical user interface for arbitrary but constrained 
     *        interface connection
     * @param machineBegins Whether human or machine player comes in this operation 
     *        loop
     * @param stepsToLookAhead The non-zero, positive length of step chain to be used
     * @param minConvThreshold Threshold for negative score change in tendency
     * @param cumulativeNegativeChangeThreshold Number of negative machine score 
     *        modifications to be allowed
     * @param timeLimit Defined amount of time that is provided for both players
     * @param memLimit Memory limit for confinement of system memory usage
     */
    public Game(IConsoleUI consoleUI, IGameUI gameUI, boolean machineBegins, 
            int stepsToLookAhead, double minConvThreshold, 
            int cumulativeNegativeChangeThreshold, Duration timeLimit, long memLimit) {

        if(consoleUI == null){
        
            throw new RuntimeException("Console line interface object is null.");
        }
        
        this.consoleUI = consoleUI;
        
        if(gameUI == null){

            throw new RuntimeException("User interface object is null.");
        }

        this.gameUI = gameUI;

        this.machineBegins = machineBegins;
        this.machineComes = machineBegins;

        humanPlayer = new HumanPlayer(
                this,
                pieces,
                gameBoard,
                stepSequences,
                false,
                removedHumanPieces,
                removedMachinePieces,
                0.0,
                stepId,
                Duration.ZERO,
                LocalDateTime.now(),
                false);
        
        machinePlayer = new MachinePlayer(
                this,
                pieces,
                gameBoard,
                stepSequences,
                false,
                removedMachinePieces,
                removedHumanPieces,
                0.0,
                stepId,
                Duration.ZERO,
                LocalDateTime.now());
        
        playGame.set(true);
        gamePlayStatus = "PLAYING";

        pieces = new PieceContainer();
        gameBoard = new GameBoardData();
        sourceStepHistory = new Stack<>();
        targetStepHistory = new Stack<>();
        removedHumanPieces = new Stack<>();
        removedMachinePieces = new Stack<>();

        if(memLimit <= 0){

            throw new RuntimeException("Memory limit is not positive.");
        }

        if(stepsToLookAhead < 4){

            throw new RuntimeException("Provided stepsToLookAhead is "
                    + "under minimum stepsToLookAhead.");
        }

        // restrict step deicision tree generation with human leaf level
        if(machineBegins && stepsToLookAhead % 2 == 1){

            --stepsToLookAhead;
        }

        if(!machineBegins && stepsToLookAhead % 2 == 0){

            --stepsToLookAhead;
        }

        stepSequences = new StepDecisionTree(consoleUI, machineBegins, pieces,
            targetStepHistory, gameBoard, stepsToLookAhead, 
            cumulativeNegativeChangeThreshold, minConvThreshold, 0, 0, 
                memLimit);

        stepId = 0;

        try{
                    
            for(int i = 0; i < 8; ++i){

                pieces.set(i, new Pawn(i, machineBegins, -3.0, 1, i));
                gameBoard.set(1, i, i);

                pieces.set(16 + i, new Pawn(16 + i, !machineBegins, 3.0, 6, i));
                gameBoard.set(6, i, 16 + i);
            }

            // initializing machine pieces
            pieces.set(8, new Rook(8, machineBegins, -14.0, 0, 0));
            pieces.set(9, new Knight(9, machineBegins, -8.0, 0, 1));
            pieces.set(10, new Bishop(10, machineBegins, -14.0, 0, 2));
            pieces.set(11, new King(11, machineBegins, -8.0, 0, 3));
            pieces.set(12, new Queen(12, machineBegins, -28.0, 0, 4));
            pieces.set(13, new Bishop(13, machineBegins, -14.0, 0, 5));
            pieces.set(14, new Knight(14, machineBegins, -8.0, 0, 6));
            pieces.set(15, new Rook(15, machineBegins, -14.0, 0, 7));
        
            // initializing human pieces
            pieces.set(16 + 8, new Rook(16 + 8, !machineBegins, 14.0, 7, 0));
            pieces.set(16 + 9, new Knight(16 + 9, !machineBegins, 8.0, 7, 1));
            pieces.set(16 + 10, new Bishop(16 + 10, !machineBegins, 14.0, 7, 2));
            pieces.set(16 + 11, new King(16 + 11, !machineBegins, 8.0, 7, 3));
            pieces.set(16 + 12, new Queen(16 + 12, !machineBegins, 28.0, 7, 4));
            pieces.set(16 + 13, new Bishop(16 + 13, !machineBegins, 14.0, 7, 5));
            pieces.set(16 + 14, new Knight(16 + 14, !machineBegins, 8.0, 7, 6));
            pieces.set(16 + 15, new Rook(16 + 15, !machineBegins, 14.0, 7, 7));
        }
        catch(Exception e){
        
            consoleUI.println("An error has occurred at initialization of pieces (" + e.getMessage() + ")");
        }
        
        // initializing machine pieces
        gameBoard.set(0, 0, 8);
        gameBoard.set(0, 1, 9);
        gameBoard.set(0, 2, 10);
        gameBoard.set(0, 3, 11);
        gameBoard.set(0, 4, 12);
        gameBoard.set(0, 5, 13);
        gameBoard.set(0, 6, 14);
        gameBoard.set(0, 7, 15);
        
        // initializing human pieces
        gameBoard.set(7, 0, 16 + 8);
        gameBoard.set(7, 1, 16 + 9);
        gameBoard.set(7, 2, 16 + 10);
        gameBoard.set(7, 3, 16 + 11);
        gameBoard.set(7, 4, 16 + 12);
        gameBoard.set(7, 5, 16 + 13);
        gameBoard.set(7, 6, 16 + 14);
        gameBoard.set(7, 7, 16 + 15);

        // filling empty squares
        for(int rankInd = 2; rankInd < 6; ++rankInd){

            for(int fileInd = 0; fileInd < 8; ++fileInd){

                gameBoard.set(rankInd, fileInd, -1);
            }
        }

        if(timeLimit.isZero()){

            throw new RuntimeException("Time limit is zero for player "
                    + "durations.");
        }

        this.timeLimit = timeLimit;

        initialized = true;
    }
    
    /**
     * Method for loading previously saved game status
     * @param saveStatus Previously saved game status
     * @throws Exception Inherited exceptions
     */
    @Override
    public void setStatus(GenericSaveStatus saveStatus) throws Exception{
    
        GameStatus gameStatus = (GameStatus)saveStatus;
        
        machineBegins = gameStatus.getMachineBegins();
        machineComes = gameStatus.getMachineComes();
        humanPlayer = gameStatus.getHumanPlayer();
        machinePlayer = gameStatus.getMachinePlayer();
        gamePlayStatus = gameStatus.getGamePlayStatus();
        pieces = gameStatus.getPieces();
        gameBoard = gameStatus.getGameBoard();
        stepSequences = gameStatus.getStepSequences();
        stepId = gameStatus.getStepId();
        sourceStepHistory = gameStatus.getSourceStepHistory();
        targetStepHistory = gameStatus.getTargetStepHistory();
    }
    
    /**
     * Saves game status
     * @return It returns a generically cast save status object that contains 
     *         all necessary status data 
     * @throws Exception Inherited exceptions
     */
    @Override
    public GenericSaveStatus getStatus() throws Exception{
        
        ComplexGameStatus gameStatus = new ComplexGameStatus();
    
        gameStatus.setMachineBegins(machineBegins);
        gameStatus.setMachineComes(machineComes);
        gameStatus.setHumanPlayer(humanPlayer);
        gameStatus.setMachinePlayer(machinePlayer);
        gameStatus.setGamePlayStatus(gamePlayStatus);
        gameStatus.setPieces(pieces);
        gameStatus.setGameBoard(gameBoard);
        gameStatus.setStepSequences(stepSequences);
        gameStatus.setStepId(stepId);
        gameStatus.setSourceStepHistory(sourceStepHistory);
        gameStatus.setTargetStepHistory(targetStepHistory);
        
        String[][] boardSquareStatus = new String[8][8];
        
        for(int rankInd = 0; rankInd < 8; ++rankInd){
        
            for(int fileInd = 0; fileInd < 8; ++fileInd){
            
                if(gameBoard.get(rankInd, fileInd) != -1){
                    
                    boardSquareStatus[rankInd][fileInd] = pieces.get(
                            gameBoard.get(rankInd, fileInd)).getTypeName();
                }
                else{
                    
                    boardSquareStatus[rankInd][fileInd] = "empty";
                }
            }
        }
        
        gameStatus.setBoardSquareStatus(boardSquareStatus);
        // todo, implement status saver
        return gameStatus;
    }
    
    /**
     * Entry point of this game handler controller class.
     * The main game operator method that manages the high level game events in 
     * business logic and triggers requests toward external modules such as GUI
     * @throws Exception 
     *         Uninitialized game
     *         selectMachineSteps method exceptions (see further)
     *         validateHumanPlayerStatus method exceptions (see further)
     *         graphical user interface request exceptions (see further)
     */
    @Override
    public void runGame() throws Exception{
        
        if(!initialized) throw new RuntimeException("Uninitialized game.");
        
        Step sourceStep;
        Step targetStep;
        
        // generation scenarios
        //  first step from human, second steps from machine
        //  first step from machine, second from human
        
        this.gameUI.run();
        
        if(machineBegins){
            
            machinePlayer.startClock();
            
            waitForDataSave();
            machineComes = true;
            
            machinePlayer.generateFirstMachineStep();
            machinePlayer.getNextStep();
            
            machinePlayer.stopClock();
            
            humanPlayer.validateStatus();

            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(pieces.get(
                    targetStep.getPieceId()).getTypeName(), 
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            signalForDataRead();
            
            if(timeLimit.compareTo(machinePlayer.getTime()) <= 0){
            
                setGamePlayStatus("WIN");
            }
            
            gameUI.switchPlayerClock();
            
            // waiting for player action
            humanPlayer.startClock();
            
            waitForDataSave();  
            machineComes = false;
            
            humanPlayer.getNextStep();
            
            humanPlayer.stopClock();
            
            machinePlayer.validateStatus();
            
            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(pieces.get(
                    targetStep.getPieceId()).getTypeName(), 
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            
            signalForDataRead();
            
            if(timeLimit.compareTo(humanPlayer.getTime()) <= 0){
            
                setGamePlayStatus("LOSE");
            }
            
            gameUI.switchPlayerClock();
            
            machinePlayer.startClock();
            
            waitForDataSave();
            machineComes = true;
            
            machinePlayer.buildStrategy();
            machinePlayer.getNextStep();
            
            machinePlayer.stopClock();
            
            humanPlayer.validateStatus();

            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(
                    pieces.get(targetStep.getPieceId()).getTypeName(),
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            
            signalForDataRead();
            
            if(timeLimit.compareTo(machinePlayer.getTime()) <= 0){
            
                setGamePlayStatus("WIN");
            }
            
            gameUI.switchPlayerClock();
        }
        else{
            
            // waiting for player action
            humanPlayer.startClock();
            
            waitForDataSave();
            machineComes = false;
            
            humanPlayer.getNextStep();
            
            humanPlayer.stopClock();
            
            machinePlayer.validateStatus();
            
            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(
                    pieces.get(targetStep.getPieceId()).getTypeName(),
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            
            signalForDataRead();
            
            if(timeLimit.compareTo(humanPlayer.getTime()) <= 0){
            
                setGamePlayStatus("LOSE");
            }
            
            gameUI.switchPlayerClock();
            
            machinePlayer.startClock();
            
            waitForDataSave();
            machineComes = true;
            
            machinePlayer.generateFirstMachineStep();
            machinePlayer.getNextStep();
            machinePlayer.buildStrategy();
            
            machinePlayer.stopClock();
            
            humanPlayer.validateStatus();
            
            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(
                    pieces.get(targetStep.getPieceId()).getTypeName(),
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            
            signalForDataRead();
            
            if(timeLimit.compareTo(machinePlayer.getTime()) <= 0){
            
                setGamePlayStatus("WIN");
            }
            
            gameUI.switchPlayerClock();
        }
        
        while(playGame.get()){
        
            humanPlayer.startClock();
            waitForDataSave();
            machineComes = false;
            humanPlayer.getNextStep();

            humanPlayer.stopClock();
            
            machinePlayer.validateStatus();
            
            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(
                    pieces.get(targetStep.getPieceId()).getTypeName(), 
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            signalForDataRead();
            if(timeLimit.compareTo(humanPlayer.getTime()) <= 0){
            
                setGamePlayStatus("LOSE");
                break;
            }
            
            gameUI.switchPlayerClock();
            
            machinePlayer.startClock();
            waitForDataSave();
            machineComes = true;
            stepSequences.continueStepSequences();
            machinePlayer.getNextStep();
            
            machinePlayer.stopClock();
            
            humanPlayer.validateStatus();
        
            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(
                    pieces.get(targetStep.getPieceId()).getTypeName(),
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            signalForDataRead();
            if(timeLimit.compareTo(machinePlayer.getTime()) <= 0){
            
                setGamePlayStatus("WIN");
                break;
            }
            
            gameUI.switchPlayerClock();
        }

        if(gamePlayStatus.compareTo("WIN") == 0){
        
            consoleUI.println("Human won the game.");
            
            // print more information (especially score progressions)
        }
        else if(gamePlayStatus.compareTo("LOSE") == 0){
        
            consoleUI.println("Machine won the game.");
            
            // print more information (especially score progressions)
        }
        else{
            // no further condition is needed, only draw scenario is at present
        
            consoleUI.println("Game has ended with draw ("
                    + humanPlayer.getScore() + " - " + machinePlayer.getScore() 
                    + ", player - machine) with " + stepId + " steps.");
            
            // TODO print more information (especially score related informations)
            //  the draw does not mean that the scores are equal, identic
            //  It only means the the recent status (step based) is equal with each other
        }
    }
    
    /**
     * It sets new length for step sequences
     * @param depth New length of a step sequences in terms of tree depth 
     *        (originating from multi-path traversal)
     * @throws Exception 
     *         Depth lower bound violation exception
     */
    public void setDepth(int depth) throws Exception{
    
        if(depth < 4){
        
            throw new ValueOutOfRangeException("Provided depth is under "
                    + "minimum depth.");
        }
        
        // restrict step deicision tree generation with human leaf level
        if(machineBegins && depth % 2 == 1){
            
            --depth;
        }
        
        if(!machineBegins && depth % 2 == 0){
        
            --depth;
        }
        
        stepSequences.setDepth(depth);
    }
    
    /**
     * Provides the source step sequence history for further processing
     * @return Returns the recent status of source step sequence history container
     */
    public Stack<Step> getSourceStepHistory(){
    
        return sourceStepHistory;
    }
    
    /**
     * Provides the target step sequence history for further processing
     * @return Returns the recent status of target step sequence history container
     */
    public Stack<Step> getTargetStepHistory(){
    
        return targetStepHistory;
    }
    
    /**
     * Provides a flag about which player comes during the game player
     * @return Returns the currently active player respect to machine player
     */
    @Override
    public boolean machineComes(){
    
        return machineComes;
    }
    
    /**
     * It is a mediator method that fulfills requests from external modules 
     * using HumanPlayer class
     * @return Available pieces that are removed earlier
     */
    @Override
    public Stack<String> getHumanPromotionTypeNames(){
    
        return humanPlayer.getPromotionTypeNames();
    }
    
    @Override
    public String getGamePlayStatus(){
    
        return gamePlayStatus;
    }
    
    /**
     * It is a mediator method that fulfills requests from external modules
     * using MachinePlayer class
     * @return Available pieces that are removed earlier
     */
    @Override
    public Stack<String> getMachinePromotionTypeNames(){
    
        return machinePlayer.getPromotionTypeNames();
    }
    
    /**
     * Make machine player win the game play.
     */
    @Override
    public void giveUpHumanPlayer(){
    
        humanPlayer.setGiveUpHumanPlayerGameController(true);
        setGamePlayStatus("LOSE");
    }
    
    @Override
    public void setGamePlayStatus(String gamePlayStatus){
    
        playGame.set(false);
        this.gamePlayStatus = gamePlayStatus;

        gameUI.stop();
        gameUI.updateGameStatus(gamePlayStatus);
    }
    
    @Override
    public String readPlayerAction() throws Exception{
    
        return gameUI.readPlayerAction();
    }
    
    @Override
    public String selectPawnReplacement() throws Exception{
    
        return gameUI.selectPawnReplacement();
    }
    
    @Override
    public void addSourceStep(Step newStep){
    
        sourceStepHistory.add(newStep);
    }
    
    @Override
    public void addTargetStep(Step newStep){
    
        targetStepHistory.add(newStep);
    }
    
    @Override
    public Step getSourceStep(int id) throws Exception{
    
        return sourceStepHistory.get(id);
    }
    
    @Override
    public Step getTargetStep(int id) throws Exception{
    
        return targetStepHistory.get(id);
    }
    
    @Override
    public int getSourceStepHistorySize(){
    
        return sourceStepHistory.size();
    }
    
    @Override
    public int getTargetStepHistorySize(){
    
        return targetStepHistory.size();
    }
    
    /**
     * Triggers a wait for data reading process
     * @throws InterruptedException Inherited condition variable exceptions
     */
    @Override
    public void waitForDataRead() throws InterruptedException{
    
        statusUpdateLock.await();
    }
    
    /**
     * Terminates waiting for data read
     * @throws InterruptedException Inherited condition variable exceptions
     */
    @Override
    public void signalForDataRead() throws InterruptedException{
    
        statusUpdateLock.signal();
    }
    
    /**
     * Triggers a wait for data saving process
     * @throws InterruptedException Inherited condition variable exceptions
     */
    @Override
    public void waitForDataSave() throws InterruptedException{
    
        statusSaveLock.await();
    }
    
    /**
     * Terminates waiting for data save
     * @throws InterruptedException Inherited condition variable exceptions
     */
    @Override
    public void signalForDataSave() throws InterruptedException{
    
        statusSaveLock.signal();
    }
}
