package chessmotor.enginecontroller;

import chessmotor.enginecontroller.piecetypes.Bishop;
import chessmotor.enginecontroller.piecetypes.GenPiece;
import chessmotor.enginecontroller.piecetypes.King;
import chessmotor.enginecontroller.piecetypes.Knight;
import chessmotor.enginecontroller.piecetypes.Pawn;
import chessmotor.enginecontroller.piecetypes.Queen;
import chessmotor.enginecontroller.piecetypes.Rook;
import chessmotor.view.IConsoleUI;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import chessmotor.view.IGameUI;
import genmath.genmathexceptions.IllConditionedDataException;
import genmath.genmathexceptions.NoObjectFoundException;
import genmath.genmathexceptions.ValueOutOfRangeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;

// evolution of engine

//  > 2D game table and combinatorical generation of possible steps without step
//    strength indiator (probabilistic) only using available steps with filtering
//    active pieces. Polar coordinate based proximity function for hits, checks,
//    check mates. Use min-max bipartite graph. Usage of alpha-beta prunning 
//    (subgraph edge cuts where the step sequence would cause less probability 
//    of win including multiple loop evaluation in order to use the most efficient steps.
//    
//  > Improved version of the above along width piece type strength weight seeking
//    for maximizing the most n valuable pieces.

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
    
    private double machineScore;
    private double humanScore;
    
    private Duration timeLimit;
    private Duration machineTime;
    private Duration humanTime;

    private LocalDateTime intervalStartMachine;
    private LocalDateTime intervalStartHuman;
    
    private boolean machineIsInCheck;
    private boolean humanIsInCheck;
    private AtomicBoolean giveUpHumanPlayerGameController;
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
    StepDecisionTree stepSequences;
    private int stepId;// starting from 1 (the first step)
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

        machineScore = 0.0;
        humanScore = 0.0;
        machineIsInCheck = false;
        humanIsInCheck = false;
        giveUpHumanPlayerGameController.set(false);
        playGame.set(true);
        gamePlayStatus = "OK";

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

        // initializing machine pieces

        for(int i = 0; i < 8; ++i){

            pieces.set(i, new Pawn(i, machineBegins, -3.0, 1, i));
            gameBoard.set(1, i, i);

            pieces.set(16 + i, new Pawn(16 + i, !machineBegins, 3.0, 6, i));
            gameBoard.set(6, i, 16 + i);
        }

        pieces.set(8, new Rook(8, machineBegins, -14.0, 0, 0));
        pieces.set(9, new Knight(9, machineBegins, -8.0, 0, 1));
        pieces.set(10, new Bishop(10, machineBegins, -14.0, 0, 2));
        pieces.set(11, new King(11, machineBegins, -8.0, 0, 3));
        pieces.set(12, new Queen(12, machineBegins, -28.0, 0, 4));
        pieces.set(13, new Bishop(13, machineBegins, -14.0, 0, 5));
        pieces.set(14, new Knight(14, machineBegins, -8.0, 0, 6));
        pieces.set(15, new Rook(15, machineBegins, -14.0, 0, 7));

        gameBoard.set(0, 0, 8);
        gameBoard.set(0, 1, 9);
        gameBoard.set(0, 2, 10);
        gameBoard.set(0, 3, 11);
        gameBoard.set(0, 4, 12);
        gameBoard.set(0, 5, 13);
        gameBoard.set(0, 6, 14);
        gameBoard.set(0, 7, 15);

        // initializing human pieces

        pieces.set(16 + 8, new Rook(16 + 8, !machineBegins, 14.0, 7, 0));
        pieces.set(16 + 9, new Knight(16 + 9, !machineBegins, 8.0, 7, 1));
        pieces.set(16 + 10, new Bishop(16 + 10, !machineBegins, 14.0, 7, 2));
        pieces.set(16 + 11, new King(16 + 11, !machineBegins, 8.0, 7, 3));
        pieces.set(16 + 12, new Queen(16 + 12, !machineBegins, 28.0, 7, 4));
        pieces.set(16 + 13, new Bishop(16 + 13, !machineBegins, 14.0, 7, 5));
        pieces.set(16 + 14, new Knight(16 + 14, !machineBegins, 8.0, 7, 6));
        pieces.set(16 + 15, new Rook(16 + 15, !machineBegins, 14.0, 7, 7));

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
        machineTime = gameStatus.getMachineTime();
        humanTime = gameStatus.getHumanTime();
        machineIsInCheck = gameStatus.getMachineIsInCheck();
        humanIsInCheck = gameStatus.getHumanIsInCheck();
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
        gameStatus.setMachineTime(machineTime);
        gameStatus.setHumanTime(humanTime);
        gameStatus.setMachineIsInCheck(machineIsInCheck);
        gameStatus.setHumanIsInCheck(humanIsInCheck);
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
     * Builds machine strategy respect to player beginning configuration
     * @throws Exception
     *         Number of threads can not be obtained, 
     *         Underflow of newly assigned storage capacity comparing to previously 
     *          used (data loss)
     *         InterruptedException of time delay
     *         Storage reservation underflow, 
     *         Chunk is empty,
     *         Multiple roots have been found, 
     *         Specified root key has not been found
     */
    private void buildMachineStrategy() throws Exception{
    
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        
        if(numberOfThreads == 0){
        
            throw new ValueOutOfRangeException("Unable to detect number of available " 
                    + "concurrent threads for execution.");
        }
        
        // executor service is only used at parallel generation
        ExecutorService generatorMgr = Executors.newFixedThreadPool(numberOfThreads);
        StepDecisionTree initStepSequences = new StepDecisionTree(stepSequences);
        ArrayList<StepDecisionTree> stepSequencesChunks = new ArrayList<>();
       
        // build step decision tree in parallel mode for the first time
        
        // the number of possible steps is much higher than the number of 
        //  concurrent threads
        
        int memReq = 10000;// for the first time to avoid frequent allocations

        for(int threadId = 0; threadId < numberOfThreads; ++threadId){

            initStepSequences.reserveMem(memReq / numberOfThreads);
            initStepSequences.setFracNo(threadId);
            stepSequencesChunks.add(new StepDecisionTree(initStepSequences));
            generatorMgr.execute(stepSequencesChunks.get(threadId));
        }

        // todo compute dynamic timeout for generation according to cpu performance
        generatorMgr.awaitTermination(120000, TimeUnit.MILLISECONDS);

        for(int threadId = 0; threadId < numberOfThreads; ++threadId){

            stepSequences.unite(stepSequencesChunks.get(threadId));
        }
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
        
        //gamePlayStatus = 0;
        //playGame = true;
        Step sourceStep;
        Step targetStep;
        
        // generation scenarios
        //  first step from human, second steps from machine
        //  first step from machine, second from human
        
        if(machineBegins){
            
            intervalStartMachine = LocalDateTime.now();
            
            waitForDataSave();
            machineComes = true;
            
            stepSequences.generateFirstMachineStep();
            requestNextMachineStep();
            
            machineTime.plus(LocalDateTime.now().until(
            intervalStartMachine, ChronoUnit.SECONDS), ChronoUnit.SECONDS);
            
            validateHumanPlayerStatus();

            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(pieces.get(
                    targetStep.getPieceId()).getTypeName(), 
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            signalForDataRead();
            
            if(timeLimit.compareTo(machineTime) <= 0){
            
                playGame.set(false);
                gamePlayStatus = "WIN";
                
                gameUI.updateGameStatus(gamePlayStatus);
            }
            
            gameUI.switchPlayerClock();
            
            // waiting for player action
            intervalStartHuman = LocalDateTime.now();
            
            waitForDataSave();  
            machineComes = false;
            
            requestNextHumanStep();
            
            humanTime.plus(LocalDateTime.now().until(
            intervalStartHuman, ChronoUnit.SECONDS), ChronoUnit.SECONDS);
            
            validateMachinePlayerStatus();
            
            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(pieces.get(
                    targetStep.getPieceId()).getTypeName(), 
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            
            signalForDataRead();
            
            if(timeLimit.compareTo(humanTime) <= 0){
            
                playGame.set(false);
                gamePlayStatus = "LOSE";
                
                gameUI.updateGameStatus(gamePlayStatus);
            }
            
            gameUI.switchPlayerClock();
            
            intervalStartMachine = LocalDateTime.now();
            
            waitForDataSave();
            machineComes = true;
            
            buildMachineStrategy();
            requestNextMachineStep();
            
            machineTime.plus(LocalDateTime.now().until(
            intervalStartMachine, ChronoUnit.SECONDS), ChronoUnit.SECONDS);
            
            validateHumanPlayerStatus();

            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(
                    pieces.get(targetStep.getPieceId()).getTypeName(),
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            
            signalForDataRead();
            
            if(timeLimit.compareTo(machineTime) <= 0){
            
                playGame.set(false);
                gamePlayStatus = "WIN";
                gameUI.updateGameStatus(gamePlayStatus);
            }
            
            gameUI.switchPlayerClock();
        }
        else{
            
            // waiting for player action
            intervalStartHuman = LocalDateTime.now();
            
            waitForDataSave();
            machineComes = false;
            
            requestNextHumanStep();
            
            humanTime.plus(LocalDateTime.now().until(
            intervalStartHuman, ChronoUnit.SECONDS), ChronoUnit.SECONDS);
            
            validateMachinePlayerStatus();
            
            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(
                    pieces.get(targetStep.getPieceId()).getTypeName(),
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            
            signalForDataRead();
            
            if(timeLimit.compareTo(humanTime) <= 0){
            
                playGame.set(false);
                gamePlayStatus = "LOSE";
                
                gameUI.updateGameStatus(gamePlayStatus);
            }
            
            gameUI.switchPlayerClock();
            
            intervalStartMachine = LocalDateTime.now();
            
            waitForDataSave();
            machineComes = true;
            
            stepSequences.generateFirstMachineStep();
            requestNextMachineStep();
            buildMachineStrategy();
            
            machineTime.plus(LocalDateTime.now().until(
            intervalStartMachine, ChronoUnit.SECONDS), ChronoUnit.SECONDS);
            
            validateHumanPlayerStatus();
            
            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(
                    pieces.get(targetStep.getPieceId()).getTypeName(),
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            
            signalForDataRead();
            
            if(timeLimit.compareTo(machineTime) <= 0){
            
                playGame.set(false);
                gamePlayStatus = "WIN";
                
                gameUI.updateGameStatus(gamePlayStatus);
            }
            
            gameUI.switchPlayerClock();
        }
        
        while(playGame.get()){
        
            intervalStartHuman = LocalDateTime.now();
            waitForDataSave();
            machineComes = false;
            requestNextHumanStep();

            humanTime.plus(LocalDateTime.now().until(
            intervalStartHuman, ChronoUnit.SECONDS), ChronoUnit.SECONDS);
            
            validateMachinePlayerStatus();
            
            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(
                    pieces.get(targetStep.getPieceId()).getTypeName(), 
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            signalForDataRead();
            if(timeLimit.compareTo(humanTime) <= 0){
            
                playGame.set(false);
                gamePlayStatus = "LOSE";
                gameUI.updateGameStatus(gamePlayStatus);
                break;
            }
            
            gameUI.switchPlayerClock();
            
            intervalStartMachine = LocalDateTime.now();
            waitForDataSave();
            machineComes = true;
            stepSequences.continueMachineStepSequences();
            requestNextMachineStep();
            
            machineTime.plus(LocalDateTime.now().until(
            intervalStartMachine, ChronoUnit.SECONDS), ChronoUnit.SECONDS);
            
            validateHumanPlayerStatus();
        
            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(
                    pieces.get(targetStep.getPieceId()).getTypeName(),
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            signalForDataRead();
            if(timeLimit.compareTo(machineTime) <= 0){
            
                playGame.set(false);
                gamePlayStatus = "WIN";
                gameUI.updateGameStatus(gamePlayStatus);
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
                    + humanScore + " - " + machineScore + ", player - machine) with "
                    + stepId + " steps.");
            
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
     * An universal human player/user action handler. It manages first steps, 
     * further steps, castling, promotion, hit, illegal dual step, illegal step.
     * @throws Exception
     *         GUI module exceptions
     *         Inappropriate user input
     *         Ill conditioned source-target pairs
     *         Ill conditioned source pair
     *         Source rank out of range
     *         Source file out of range
     *         Lack of player piece at source position
     *         Wrong selected position in case of check
     *         Target rank out of range
     *         Target file out of range
     *         Illegally selected target step
     *         DualStep range violations at step creation
     *         Disallowed castling
     *         Step range violations at step creation
     *         StepDecisionTree exceptions (see further)
     */
    private void requestNextHumanStep() throws Exception{
        
        String action;
        action = gameUI.readPlayerAction();
        
        if(action.isEmpty()){
        
            throw new NoObjectFoundException("Provided input is not acceptable.");
        }
        
        if(humanIsInCheck && action.compareTo("giveup") == 0){
        
            playGame.set(false);
            gamePlayStatus = "LOSE";
            
            gameUI.updateGameStatus(gamePlayStatus);
            return;
        }
        
        String[] param = action.split("|");
        
        if(param.length != 2){
        
            throw new IllConditionedDataException("No source and target "
                    + "position has been given properly.");
        }
        
        if(param[0].length() != 2){
        
            throw new IllConditionedDataException("Ill given source position.");
        }
        
        int sourceSelectedRank;
        
        if(param[0].charAt(0) < 0 || param[0].charAt(0) > 7){
        
            throw new ValueOutOfRangeException("Source rank is out of range.");
        }
        
        sourceSelectedRank = (int)param[0].charAt(0);
        int sourceSelectedFile;
        
        if(param[0].charAt(1) < 1 || param[0].charAt(1) > 7){
        
            throw new ValueOutOfRangeException("Source file is out of range.");
        }
        
        sourceSelectedFile = (int)param[0].charAt(1);
        
        if(gameBoard.get(sourceSelectedRank, sourceSelectedFile) == -1){
        
            throw new NoObjectFoundException("No available piece in the "
                    + "provided position.");
        }
        
        if(humanIsInCheck && gameBoard.get(sourceSelectedRank, sourceSelectedFile) != 11){
        
            throw new Exception("Player is in check. Resolve check.");
        }
        
        GenPiece selectedPiece = 
            pieces.get(gameBoard.get(sourceSelectedRank, sourceSelectedFile));
        
        if(param[1].length() != 2){
        
            throw new IllConditionedDataException("Ill given target position.");
        }
        
        int targetSelectedRank;
        
        if(param[1].charAt(0) < '0' || param[1].charAt(0) > '7'){
        
            throw new ValueOutOfRangeException("Targer rank is out of range.");
        }
        
        targetSelectedRank = (int)param[1].charAt(0);
        int targetSelectedFile;
        
        if(param[1].charAt(1) < 1 || param[1].charAt(1) > 8){
        
            throw new ValueOutOfRangeException("Target file is out of range.");
        }
        
        targetSelectedFile = (int)param[0].charAt(1);
        
        if(!(selectedPiece.generateSteps(gameBoard).contains(
                new Pair(targetSelectedRank, targetSelectedFile)))){
        
            throw new Exception("Illegal selected step by chosen piece.");
        }
        
        // case of castling
        boolean castlingOccurred = false;
        boolean pawnReplacementOccurred = false;
        GenPiece selectedSecondPiece = new GenPiece();
        
        if(selectedPiece.getTypeName().contains("king") 
                && (selectedSecondPiece = pieces.get(gameBoard.get(targetSelectedRank, targetSelectedFile)))
                        .getTypeName().contains("rook") 
                && selectedPiece.getRank() == 7 && selectedSecondPiece.getRank() == 7){
        
            // suboptimal condition tests
            
            // selectedRook = pieces[gameBoard[targetSelectedRank][targetSelectedFile]];
            boolean emptyInterFiles = true;
            
            if(selectedPiece.getFile() < selectedSecondPiece.getFile()){
            
                for(int fileInd = 5; fileInd < 7 && emptyInterFiles; ++fileInd){
                
                    emptyInterFiles = gameBoard.get(7, fileInd) == -1;
                }
                
                if(emptyInterFiles){
                
                    // perform castling
                    sourceStepHistory.add(new DualStep("castling", 
                            11 + 16, 15 + 16, 7, 4, 7, 
                            7, 0.0, 0,
                            0.0));

                    gameBoard.set(7, 4, -1);
                    selectedPiece.setFile(6);
                    gameBoard.set(7, 6, 11 + 16);
                    gameBoard.set(7, 7, -1);
                    selectedSecondPiece.setFile(5);
                    gameBoard.set(7, 5, 15 + 16);
                    
                    castlingOccurred = true;
                }
            }
            else{
                
                for(int fileInd = 1; fileInd < 4 && emptyInterFiles; ++fileInd){
                 
                    emptyInterFiles = gameBoard.get(7, fileInd) == -1;
                }
                
                if(emptyInterFiles){
                
                    // perform castling
                    sourceStepHistory.add(new DualStep("castling", 
                            16 + 11, 8 + 16, 7, 4, 7, 
                            0, 0.0, 0,
                            0.0));
                    
                    gameBoard.set(7, 4, -1);
                    selectedPiece.setFile(1);
                    gameBoard.set(7, 2, 11 + 16);
                    gameBoard.set(7, 0, -1);
                    selectedSecondPiece.setFile(2);
                    gameBoard.set(7, 3, 8 + 16);
                    
                    castlingOccurred = true;
                }
                else{
                
                }
            }
            
            if(!emptyInterFiles){
            
                throw new Exception("Castling cannot be executed due to "
                        + "occupied squares.");
            }
        }
        else if(selectedPiece.getTypeName().contains("pawn") 
                && sourceSelectedRank == 0 
                && sourceSelectedRank == targetSelectedRank 
                && sourceSelectedFile == targetSelectedFile){
       
            // the visualized removed pieces are weakly coupled with index based
            //  explicit removed pieces conatiner, in this case type search is 
            //  required to select a proper piece from container
            String selectedTypeName = gameUI.selectPawnReplacement();
        
            if(giveUpHumanPlayerGameController.get()){
            
                // terminate human action request
                return;
            }
            
            int sizeOfRemovedHumanPieces = removedHumanPieces.size();
            
            for(int i = 0; i < sizeOfRemovedHumanPieces; ++i){
                
                if(pieces.get(removedHumanPieces.get(i))
                        .getTypeName().equals(selectedTypeName)){
                
                    pawnReplacementOccurred = true;
                    
                    selectedSecondPiece = pieces.get(removedHumanPieces.get(i));
                    
                    sourceStepHistory.add(new DualStep(
                            "promotion", selectedPiece.getPieceId(), 
                            selectedSecondPiece.getPieceId(),
                            sourceSelectedRank, sourceSelectedFile, 
                            targetSelectedRank, targetSelectedFile,
                            0.0, 0, 0.0));
                    
                    selectedPiece.setRank(sourceSelectedRank);
                    selectedPiece.setFile(sourceSelectedFile);
                    selectedSecondPiece.setRank(targetSelectedRank);
                    selectedSecondPiece.setFile(targetSelectedFile);
                    gameBoard.set(targetSelectedRank, targetSelectedFile, 
                            removedHumanPieces.get(i));
                    
                    removedHumanPieces.removeElementAt(i);
                    removedHumanPieces.add(selectedPiece.getPieceId());
                    
                    break;
                }
            }
        }
        else{
        
            // in case of hit as well

            selectedPiece.setRank(targetSelectedRank);
            selectedPiece.setFile(targetSelectedFile);

            gameBoard.set(targetSelectedRank, targetSelectedFile, 
                gameBoard.get(sourceSelectedRank, sourceSelectedFile));

            sourceStepHistory.add(new Step("hit", 
                    gameBoard.get(sourceSelectedRank, 
                    sourceSelectedFile), sourceSelectedRank, 
                    sourceSelectedFile, 0.0,
                    0, 0.0));
            gameBoard.set(sourceSelectedRank, sourceSelectedFile, -1);
        }
        
        Step currStep;
        if(castlingOccurred){
        
            currStep = new DualStep("castling", selectedPiece.getPieceId(), 
                    selectedSecondPiece.getPieceId(), selectedPiece.getRank(),
                    selectedPiece.getFile(), selectedSecondPiece.getRank(), 
                    selectedSecondPiece.getFile(), 0.0, 0,
                    0.0);
        }
        else if(pawnReplacementOccurred){
        
            currStep = new DualStep("promotion", selectedPiece.getPieceId(),
                    selectedSecondPiece.getPieceId(), sourceSelectedRank,
                    sourceSelectedFile, targetSelectedRank, 
                    targetSelectedFile, 0.0, 0,
                    0.0);
        }
        else{
            
            currStep  = new Step("standard", selectedPiece.getPieceId(), 
                    targetSelectedRank, targetSelectedFile, 0.0,
                    0, 0.0);// comparing currently created step
        }
        
        Step selectedStep;
        
        if(stepSequences.size() > 2){
        
            // finding step node with given position
            ArrayList<GenStepKey> levelKeys = stepSequences.getLevelKeys(1);

            int sizeOfLevelKeys = levelKeys.size();

            int i = 0;

            for(; i < sizeOfLevelKeys; ++i){

                selectedStep = stepSequences.getByKey(levelKeys.get(i));

                if(selectedStep.equals(currStep)){

                    break;
                }
            }
            
            // TASK) shift tree with one level, throw root away (root displacement)
            stepSequences.setNewRootByKey(levelKeys.get(i));
            
            targetStepHistory.add(stepSequences.getByKey(levelKeys.get(i)));
            
            // TASK) rename step node keys/identifiers (cyclic renaming)
            //       in order to limit the key length (comparison optimization)
            stepSequences.trimKeys();
            
            humanScore += targetStepHistory.get(targetStepHistory.size() - 1).getValue();
        }
        else{
            
            selectedStep = new Step("standard",
                    gameBoard.get(targetSelectedRank, targetSelectedFile), 
                    targetSelectedRank, targetSelectedFile, 
                    selectedPiece.getValue(), 0,
                    selectedPiece.getValue());
            if(machineBegins){
                
                stepSequences.addOne(new GenStepKey("a"), 
                        new GenStepKey("aa"), selectedStep);
                // saving previous level status
                stepSequences.addToHistoryStack("aa", selectedStep);
            }
            else{
            
                stepSequences.addOne(new GenStepKey("a"), 
                        new GenStepKey("a"), selectedStep);
                // saving previous level status
                stepSequences.addToHistoryStack("a", selectedStep);
            }
            
            targetStepHistory.add(selectedStep);
        
            //humanScore = 0.0;// initial step has taken
        }
        
        // piece removal in case of hit
        if(gameBoard.get(targetSelectedRank, targetSelectedFile) != -1){
        
            removedMachinePieces.add(
                    gameBoard.get(targetSelectedRank, targetSelectedFile));
        }
        
        ++stepId;
    }
    
    /**
     * Validates human player after machine player took step
     * @throws Exception
     *         StepDecisionTree exception (see further)
     */
    private void validateHumanPlayerStatus() throws Exception{
    
        // looking for check mate on human king piece
        
        ArrayList<GenStepKey> levelKeys = stepSequences.getLevelKeys(1);

        int sizeOfLevelKeys = levelKeys.size();

        Step step;
        for(int i = 0; i < sizeOfLevelKeys; ++i){

            step = stepSequences.getByKey(levelKeys.get(i));

            if(gameBoard.get(step.getRank(), step.getFile()) == 11 + 16){

                // human king is in check
                humanIsInCheck = true;
            }
        }
        
        if(humanIsInCheck && pieces.get(11 + 16).generateSteps(gameBoard).isEmpty()){
        
            playGame.set(false);
            gamePlayStatus = "LOSE";
            
            gameUI.updateGameStatus(gamePlayStatus);
        }        
    }
    
    /**
     * Validates machine player after human player took step
     * @throws Exception 
     *         StepDecisionTree exception (see further)
     */
    private void validateMachinePlayerStatus() throws Exception{
    
        // looking of check mate on machine king piece
        
        ArrayList<GenStepKey> levelKeys = stepSequences.getLevelKeys(1);
        
        int sizeOfLastNonLeafLevelKeys = levelKeys.size();
        
        Step step;
        for(int i = 0; i < sizeOfLastNonLeafLevelKeys; ++i){
        
            step = stepSequences.getByKey(levelKeys.get(i));
            
            if(gameBoard.get(step.getRank(), step.getFile()) == 11){
            
                // machine king is in check
                machineIsInCheck = true;
            }
        }
        
        if(machineIsInCheck && pieces.get(11).generateSteps(gameBoard).isEmpty()){
        
            playGame.set(false);
            gamePlayStatus = "WIN";
            
            gameUI.updateGameStatus(gamePlayStatus);
        }
    }
    
    
    /**
     * It selects next machine step similarly to human step request method
     * @throws Exception 
     *         StepDecisionTree exceptions (see further)
     *         Step creation exceptions (see further)
     *         DualStep creation exceptions (see further)
     */
    private void requestNextMachineStep() throws Exception{
    
        // TODO it could be integrated in step decision tree builder
        
        // TASK) select the best option - max search, and apply to the game using 
        //       posteriori update(perform update after execution of further subroutines 
        //       of this method)
        
        ArrayList<GenStepKey> levelKeys = stepSequences.getLevelKeys(1);
        
        if(levelKeys.isEmpty() && stepSequences.getCurrDepth() == 1){
        
            playGame.set(false);
            gamePlayStatus = "WIN";
            
            gameUI.updateGameStatus(gamePlayStatus);
            return;
        }
        
        int sizeOfLeafSteps = levelKeys.size();
        int maxI = 0;
        double currCumulativeValue;
        double maxCumulativeValue = stepSequences.getByKey(
                levelKeys.get(maxI)).getCumulativeValue();
        
        // machine check defense
        if(machineIsInCheck){
        
            boolean foundNextStep = false;
            
            for(int i = 1; i < sizeOfLeafSteps; ++i){
                
                if(stepSequences.getByKey(levelKeys.get(i)).getPieceId() == 11){
                
                    foundNextStep = true;
                    
                    currCumulativeValue = stepSequences.getByKey(
                    levelKeys.get(i)).getCumulativeValue();
            
                    if(maxCumulativeValue < currCumulativeValue){

                        maxCumulativeValue = currCumulativeValue;
                        maxI = i;
                    }
                }
            }
            
            if(!foundNextStep){
            
                playGame.set(false);
                gamePlayStatus = "WIN";
                
                gameUI.updateGameStatus(gamePlayStatus);
                return;
            }
            else{
            
                machineIsInCheck = false;
            }
        }
        else{
        
            for(int i = 1; i < sizeOfLeafSteps; ++i){

                currCumulativeValue = stepSequences.getByKey(
                    levelKeys.get(i)).getCumulativeValue();
            
                if(maxCumulativeValue < currCumulativeValue){

                    maxCumulativeValue = currCumulativeValue;
                    maxI = i;
                }
            }
        }
        
        Step step = stepSequences.getByKey(levelKeys.get(maxI));
        
        int pieceId = step.getPieceId();
        GenPiece selectedPiece = pieces.get(pieceId);
        GenPiece selectedSecondPiece;
        
        if(step instanceof DualStep){
        
            DualStep dualStep = (DualStep)step;
            selectedSecondPiece = pieces.get(dualStep.getSecondPieceId());
            
            if((selectedPiece.getTypeName().contains("king") 
                    || selectedPiece.getTypeName().contains("rook"))){
            
                // castling option
                        
                sourceStepHistory.add(new DualStep(
                        "castling", selectedPiece.getPieceId(),
                        selectedSecondPiece.getPieceId(), 
                        selectedPiece.getRank(), selectedPiece.getFile(), 
                        selectedSecondPiece.getRank(), selectedSecondPiece.getFile(), 
                        0.0, 0, 0.0));
                
                gameBoard.set(selectedPiece.getRank(), 
                        selectedPiece.getFile(), -1);
                gameBoard.set(selectedSecondPiece.getRank(), 
                        selectedSecondPiece.getFile(), -1);
                
                selectedPiece.setRank(dualStep.getRank());
                selectedPiece.setFile(dualStep.getFile());
                selectedSecondPiece.setRank(dualStep.getSecondRank());
                selectedSecondPiece.setFile(dualStep.getSecondFile());
                
                gameBoard.set(selectedPiece.getRank(), 
                        selectedPiece.getFile(),
                        selectedPiece.getPieceId());
                gameBoard.set(selectedSecondPiece.getRank(), 
                        selectedSecondPiece.getFile(), 
                        selectedSecondPiece.getPieceId());
            }
            else if(selectedPiece.getTypeName().contains("pawn")){
                
                sourceStepHistory.add(new DualStep(
                        "promotion", selectedPiece.getPieceId(),
                        selectedSecondPiece.getPieceId(), 
                        selectedPiece.getRank(), selectedPiece.getFile(), 
                        selectedSecondPiece.getRank(), selectedSecondPiece.getFile(), 
                        0.0, 0, 0.0));
                
                selectedPiece.setRank(dualStep.getRank());
                selectedPiece.setFile(dualStep.getFile());
                selectedSecondPiece.setRank(dualStep.getSecondRank());
                selectedSecondPiece.setFile(dualStep.getSecondFile());
                gameBoard.set(dualStep.getSecondRank(), dualStep.getSecondFile(), 
                        dualStep.getSecondPieceId());
            
                removedMachinePieces.remove(selectedSecondPiece.getPieceId());
                removedMachinePieces.add(selectedPiece.getPieceId());
            }   
        }
        else{
            
            // piece removal in case of hit
            if(gameBoard.get(step.getRank(), step.getFile()) != -1){

                removedHumanPieces.add(
                        gameBoard.get(step.getRank(), step.getFile()));
            }
            
            sourceStepHistory.add(new Step("standard", 
                    pieceId, selectedPiece.getRank(), 
                    selectedPiece.getFile(), selectedPiece.getValue(),
                    0, selectedPiece.getValue()));
            
            selectedPiece.setRank(step.getRank());
            selectedPiece.setFile(step.getFile());
            gameBoard.set(selectedPiece.getRank(), selectedPiece.getFile(), 
                    selectedPiece.getPieceId());
        }
        
        // TASK) shift tree with one level, throw root away (root displacement)
        
        // set the actual root as source position        
        stepSequences.setNewRootByKey(levelKeys.get(maxI));
        
        targetStepHistory.add(stepSequences.getByKey(levelKeys.get(maxI)));
        
        // TASK) rename step node keys/identifiers (cyclic renaming)
        //       in order to limit the key length (comparison optimization)
        stepSequences.trimKeys();
        
        machineScore += targetStepHistory.get(targetStepHistory.size() - 1).getValue();
        
        ++stepId;
        
        // TASK) yield control to human player (asynchronous tasks)
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
     * This method is a getter to provide machine player specific available removed
     * player pieces to be used again. It is used at visual piece selection.
     * @return Returns the player dependent removed pieces from the game table
     */
    @Override
    public Stack<String> getMachinePromotionTypeNames(){
    
        Stack<String> removedMachinePiecesTypeNames = new Stack<>();
        
        int sizeOfRemovedMachinePieces = removedMachinePieces.size();
        
        for(int i = 0; i < sizeOfRemovedMachinePieces; ++i){
        
            removedMachinePiecesTypeNames.add(
                    pieces.get(removedMachinePieces.get(i)).getTypeName());
        }
        
        return removedMachinePiecesTypeNames;
    }
    
    /**
     * This method is a getter to provide human player specific available removed 
     * player pieces to be used again. It is used at visual piece selection.
     * @return Returns the player dependent removed pieces from the game table
     */
    @Override
    public Stack<String> getHumanPromotionTypeNames(){
    
        Stack<String> removedHumanPiecesTypeNames = new Stack<>();
        
        int sizeOfRemovedHumanPieces = removedHumanPieces.size();
        
        for(int i = 0; i < sizeOfRemovedHumanPieces; ++i){
        
            removedHumanPiecesTypeNames.add(
                    pieces.get(removedHumanPieces.get(i)).getTypeName());
        }
        
        return removedHumanPiecesTypeNames;
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
    
    /**
     * Make machine player win the game play.
     */
    @Override
    public void giveUpHumanPlayer(){
    
        giveUpHumanPlayerGameController.set(true);
        playGame.set(false);
        gamePlayStatus = "LOSE";
        gameUI.updateGameStatus(gamePlayStatus);
    }
}
