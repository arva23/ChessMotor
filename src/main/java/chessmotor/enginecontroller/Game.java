package chessmotor.enginecontroller;

import chessmotor.enginecontroller.piecetypes.Bishop;
import chessmotor.enginecontroller.piecetypes.GenPiece;
import chessmotor.enginecontroller.piecetypes.King;
import chessmotor.enginecontroller.piecetypes.Knight;
import chessmotor.enginecontroller.piecetypes.Pawn;
import chessmotor.enginecontroller.piecetypes.Queen;
import chessmotor.enginecontroller.piecetypes.Rook;
import genmath.GenStepKey;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import chessmotor.view.IGameUI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Stack;
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

public class Game{
    
    private Condition statusUpdateLock;
    private Condition statusSaveLock;
    
    private IGameUI gameUI;
    
    // initialization of game is required
    private boolean initialized;
    // which player begins with the white pieces
    private boolean machineBegins;
    
    private double machineScore;
    private double humanScore;
    
    private Duration timeLimit;
    private Duration machineTime;
    private Duration humanTime;

    private LocalDateTime intervalStartMachine;
    private LocalDateTime intervalStartHuman;
    
    private boolean machineIsInCheck;
    private boolean humanIsInCheck;
    private boolean playGame;
    private String gameStatus;
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
    
    public Game(){
    
        initialized = false;
    }
    
    
    public Game(IGameUI gameUI, boolean machineBegins, int stepsToLookAhead, double minConvThreshold, 
            int cumulativeNegativeChangeThreshold, Duration timeLimit, long memLimit) {
    
        try{

            if(gameUI == null){
            
                throw new Exception("User interface object is null.");
            }
            
            this.gameUI = gameUI;

            this.gameUI.run();

            initialized = true;

            this.machineBegins = machineBegins;

            machineScore = 0.0;
            humanScore = 0.0;
            machineIsInCheck = false;
            humanIsInCheck = false;
            playGame = true;
            gameStatus = "OK";

            pieces = new PieceContainer();
            gameBoard = new GameBoardData();
            sourceStepHistory = new Stack<Step>();
            targetStepHistory = new Stack<Step>();
            removedHumanPieces = new Stack<Integer>();
            removedMachinePieces = new Stack<Integer>();
            
            if(memLimit <= 0){
            
                throw new Exception("Memory limit is not positive.");
            }
            
            if(stepsToLookAhead < 4){
        
                throw new Exception("Provided stepsToLookAhead is under minimum stepsToLookAhead.");
            }

            // restrict step deicision tree generation with human leaf level
            if(machineBegins && stepsToLookAhead % 2 == 1){

                --stepsToLookAhead;
            }

            if(!machineBegins && stepsToLookAhead % 2 == 0){

                --stepsToLookAhead;
            }
            
            stepSequences = new StepDecisionTree(machineBegins, pieces,
                targetStepHistory, gameBoard, stepsToLookAhead, 
                cumulativeNegativeChangeThreshold, minConvThreshold, 0, 0, memLimit);

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
            
                throw new Exception("Time limit is zero for player durations.");
            }
            
            this.timeLimit = timeLimit;
        }
        catch(Exception e){
        
            System.out.println("Could not initialize game: " + e.getMessage());
        }
    }
    
    
    private void buildMachineStrategy() throws Exception{
    
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        
        if(numberOfThreads == 0){
        
            throw new Exception("Unable to detect number of available " 
                    + "concurrent threads for execution.");
        }
        
        // executor service is only used at parallel generation
        ExecutorService generatorMgr = Executors.newFixedThreadPool(numberOfThreads);
        StepDecisionTree initStepSequences = new StepDecisionTree(stepSequences);
        ArrayList<StepDecisionTree> stepSequencesChunks = new ArrayList<StepDecisionTree>();
       
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

        generatorMgr.awaitTermination(120000, TimeUnit.MILLISECONDS);

        for(int threadId = 0; threadId < numberOfThreads; ++threadId){

            stepSequences.unite(stepSequencesChunks.get(threadId));
        }
    }
    
    
    // entry point of this game handler controller class
    public void runGame() throws Exception{
    
        if(!initialized) throw new Exception("Uninitialized game.");
        
        //gameStatus = 0;
        //playGame = true;
        Step sourceStep;
        Step targetStep;
        
        // generation scenarios
        //  first step from human, second steps from machine
        //  first step from machine, second from human
        
        if(machineBegins){
            
            intervalStartMachine = LocalDateTime.now();
        
            stepSequences.generateFirstMachineStep();
            selectNextMachineStep();
        
            machineTime.plus(LocalDateTime.now().until(
            intervalStartMachine, ChronoUnit.SECONDS), ChronoUnit.SECONDS);
            
            validateHumanPlayerStatus();

            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(pieces.get(
                    targetStep.getPieceId()).getTypeName(), 
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());    
            
            if(timeLimit.compareTo(machineTime) <= 0){
            
                playGame = false;
                gameStatus = "WIN";
                
                gameUI.updateGameStatus(gameStatus);
            }
            
            gameUI.switchPlayerClock();
            
            // waiting for player action
            intervalStartHuman = LocalDateTime.now();
            
            requestHumanPlayerAction();
            
            humanTime.plus(LocalDateTime.now().until(
            intervalStartHuman, ChronoUnit.SECONDS), ChronoUnit.SECONDS);
            
            validateMachinePlayerStatus();
            
            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(
                    pieces.get(targetStep.getPieceId()).getTypeName(), 
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            
            
            if(timeLimit.compareTo(humanTime) <= 0){
            
                playGame = false;
                gameStatus = "LOSE";
                
                gameUI.updateGameStatus(gameStatus);
            }
            
            gameUI.switchPlayerClock();
            
            intervalStartMachine = LocalDateTime.now();
            
            buildMachineStrategy();
            selectNextMachineStep();
            
            machineTime.plus(LocalDateTime.now().until(
            intervalStartMachine, ChronoUnit.SECONDS), ChronoUnit.SECONDS);
            
            validateHumanPlayerStatus();

            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(
                    pieces.get(targetStep.getPieceId()).getTypeName(),
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            
            
            if(timeLimit.compareTo(machineTime) <= 0){
            
                playGame = false;
                gameStatus = "WIN";
                gameUI.updateGameStatus(gameStatus);
            }
            
            gameUI.switchPlayerClock();
        }
        else{
            
            // waiting for player action
            intervalStartHuman = LocalDateTime.now();
            
            requestHumanPlayerAction();
            
            humanTime.plus(LocalDateTime.now().until(
            intervalStartHuman, ChronoUnit.SECONDS), ChronoUnit.SECONDS);
            
            validateMachinePlayerStatus();
            
            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(
                    pieces.get(targetStep.getPieceId()).getTypeName(),
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            
            
            if(timeLimit.compareTo(humanTime) <= 0){
            
                playGame = false;
                gameStatus = "LOSE";
                
                gameUI.updateGameStatus(gameStatus);
            }
            
            gameUI.switchPlayerClock();
            
            intervalStartMachine = LocalDateTime.now();
            
            stepSequences.generateFirstMachineStep();
            selectNextMachineStep();
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
            
            
            if(timeLimit.compareTo(machineTime) <= 0){
            
                playGame = false;
                gameStatus = "WIN";
                
                gameUI.updateGameStatus(gameStatus);
            }
            
            gameUI.switchPlayerClock();
        }
        
        while(playGame){
        
            intervalStartHuman = LocalDateTime.now();
            
            requestHumanPlayerAction();

            humanTime.plus(LocalDateTime.now().until(
            intervalStartHuman, ChronoUnit.SECONDS), ChronoUnit.SECONDS);
            
            validateMachinePlayerStatus();
            
            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(pieces.get(
                    targetStep.getPieceId()).getTypeName(), 
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            
            
            if(timeLimit.compareTo(humanTime) <= 0){
            
                playGame = false;
                gameStatus = "LOSE";
                gameUI.updateGameStatus(gameStatus);
                break;
            }
            
            gameUI.switchPlayerClock();
            
            intervalStartMachine = LocalDateTime.now();
            
            stepSequences.continueMachineStepSequences();
            selectNextMachineStep();
            
            machineTime.plus(LocalDateTime.now().until(
            intervalStartMachine, ChronoUnit.SECONDS), ChronoUnit.SECONDS);
            
            validateHumanPlayerStatus();
        
            sourceStep = sourceStepHistory.lastElement();
            targetStep = targetStepHistory.lastElement();
            gameUI.applyGenPlayerAction(
                    pieces.get(targetStep.getPieceId()).getTypeName(),
                    sourceStep.getRank(), sourceStep.getFile(),
                    targetStep.getRank(), targetStep.getFile());
            
            
            if(timeLimit.compareTo(machineTime) <= 0){
            
                playGame = false;
                gameStatus = "WIN";
                gameUI.updateGameStatus(gameStatus);
                break;
            }
            
            gameUI.switchPlayerClock();
        }

        if(gameStatus.compareTo("WIN") == 0){
        
            System.out.println("Human won the game.");
            
            // print more information (especially score progressions)
        }
        else if(gameStatus.compareTo("LOSE") == 0){
        
            System.out.println("Machine won the game.");
            
            // print more information (especially score progressions)
        }
        else{
            // no further condition is needed, only draw scenario is at present
        
            System.out.println("Game has ended with draw ("
                    + humanScore + " - " + machineScore + ", player - machine) with "
                    + stepId + " steps.");
            
            // TODO print more information (especially score related informations)
            //  the draw does not mean that the scores are equal, identic
            //  It only means the the recent status (step based) is equal with each other
        }
    }
    
    
    public void setDepth(int depth) throws Exception{
    
        if(depth < 4){
        
            throw new Exception("Provided depth is under minimum depth.");
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
    
    
    private void requestHumanPlayerAction() throws Exception{
        
        String action;
        action = gameUI.readPlayerAction();
        
        if(action.isEmpty()){
        
            throw new Exception("Provided input is not acceptable.");
        }
        
        if(humanIsInCheck && action.compareTo("giveup") == 0){
        
            playGame = false;
            gameStatus = "LOSE";
            
            gameUI.updateGameStatus(gameStatus);
            return;
        }
        
        String[] param = action.split("|");
        
        if(param.length != 2){
        
            throw new Exception("No source and target position has been given properly.");
        }
        
        if(param[0].length() != 2){
        
            throw new Exception("Ill given source position.");
        }
        
        int sourceSelectedRank = 0;
        
        if(param[0].charAt(0) < 0 || param[0].charAt(0) > 7){
        
            throw new Exception("Source rank is out of range.");
        }
        
        sourceSelectedRank = (int)param[0].charAt(0);
        int sourceSelectedFile = 0;
        
        if(param[0].charAt(1) < 1 || param[0].charAt(1) > 7){
        
            throw new Exception("Source file is out of range.");
        }
        
        sourceSelectedFile = (int)param[0].charAt(1);
        
        if(humanIsInCheck && gameBoard.get(sourceSelectedRank, sourceSelectedFile) != 11){
        
            throw new Exception("Player is in check. Resolve check.");
        }
        
        GenPiece selectedPiece = 
            pieces.get(gameBoard.get(sourceSelectedRank, sourceSelectedFile));
        
        if(param[1].length() != 2){
        
            throw new Exception("Ill given target position.");
        }
        
        int targetSelectedRank = 0;
        
        if(param[1].charAt(0) < '0' || param[1].charAt(0) > '7'){
        
            throw new Exception("Targer rank is out of range.");
        }
        
        targetSelectedRank = (int)param[1].charAt(0);
        int targetSelectedFile = 0;
        
        if(param[1].charAt(1) < 1 || param[1].charAt(1) > 8){
        
            throw new Exception("Target file is out of range.");
        }
        
        targetSelectedFile = (int)param[0].charAt(1);
        
        if(!(selectedPiece.generateSteps(gameBoard).contains(
                new Pair(targetSelectedRank, targetSelectedFile)))){
        
            throw new Exception("Illegal selected step by chosen piece.");
        }
        
        // case of castling
        boolean castlingOccurred = false;
        GenPiece selectedRook = new GenPiece();
        
        if(selectedPiece.getTypeName().contains("king") 
                && (selectedRook = pieces.get(gameBoard.get(targetSelectedRank, targetSelectedFile)))
                        .getTypeName().contains("rook") 
                && selectedPiece.getRank() == 7 && selectedRook.getRank() == 7){
        
            // suboptimal condition tests
            
            // selectedRook = pieces[gameBoard[targetSelectedRank][targetSelectedFile]];
            boolean emptyInterFiles = true;
            
            if(selectedPiece.getFile() < selectedRook.getFile()){
            
                
                for(int fileInd = 5; fileInd < 7 && emptyInterFiles; ++fileInd){
                
                    emptyInterFiles = gameBoard.get(7, fileInd) == -1;
                }
                
                if(emptyInterFiles){
                
                    // perform castling
                    sourceStepHistory.add(new DualStep(11 + 16, 15 + 16,
                            7, 4, 7, 7, 0.0,
                            0, 0.0));

                    gameBoard.set(7, 4, -1);
                    selectedPiece.setFile(6);
                    gameBoard.set(7, 6, 11 + 16);
                    gameBoard.set(7, 7, -1);
                    selectedRook.setFile(5);
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
                    sourceStepHistory.add(new DualStep(16 + 11, 8 + 16, 
                            7, 4, 7, 0, 0.0,
                            0, 0.0));
                    
                    gameBoard.set(7, 4, -1);
                    selectedPiece.setFile(1);
                    gameBoard.set(7, 2, 11 + 16);
                    gameBoard.set(7, 0, -1);
                    selectedRook.setFile(2);
                    gameBoard.set(7, 3, 8 + 16);
                    
                    castlingOccurred = true;
                }
                else{
                
                }
            }
            
            if(!emptyInterFiles){
            
                throw new Exception("Castling cannot be executed due to occupied squares.");
            }
        }
        else{
        
            // in case of hit as well

            selectedPiece.setRank(targetSelectedRank);
            selectedPiece.setFile(targetSelectedFile);

            gameBoard.set(targetSelectedRank, targetSelectedFile, 
                gameBoard.get(sourceSelectedRank, sourceSelectedFile));

            sourceStepHistory.add(new Step(
                    gameBoard.get(sourceSelectedRank, sourceSelectedFile),
                    sourceSelectedRank, sourceSelectedFile, 0.0,
                0, 0.0));
            gameBoard.set(sourceSelectedRank, sourceSelectedFile, -1);
        }
        
        Step currStep;
        if(castlingOccurred){
        
            currStep = new DualStep(selectedPiece.getPieceId(), 
                    selectedRook.getPieceId(), selectedPiece.getRank(),
                    selectedPiece.getFile(), selectedRook.getRank(), 
                    selectedRook.getFile(), 0.0, 0,
                    0.0);
        }
        else{
            
            currStep  = new Step(selectedPiece.getPieceId(), 
                    targetSelectedRank, targetSelectedFile, 0.0,
                    0, 0.0);// comparing currently created step
        }
        
        Step selectedStep = new Step();
        
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
            
            if(castlingOccurred){
            
                targetStepHistory.add(new DualStep(selectedPiece.getPieceId(), 
                        selectedRook.getPieceId(), selectedPiece.getRank(),
                        selectedPiece.getFile(), selectedRook.getRank(), 
                        selectedRook.getFile(), 0.0, 0,
                        0.0));
            }
            else{
            
                targetStepHistory.add(stepSequences.getByKey(levelKeys.get(i)));
            }
            
            // TASK) rename step node keys/identifiers (cyclic renaming)
            //       in order to limit the key length (comparison optimization)
            stepSequences.trimKeys();
            
            humanScore += targetStepHistory.get(targetStepHistory.size() - 1).getValue();
        }
        else{
            
            selectedStep = new Step(
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
        
        // pawn replacement with earlier hit piece
        int pieceId = selectedStep.getPieceId();
        
        if(0 <= pieceId && pieceId <= 7 && selectedStep.getRank() == 0){
        
            // selection strategy is via user action
            // the typename selection is aave because typenames are selected 
            //  from removed pieces list
            String selectedTypeName = gameUI.selectPawnReplacement();
            
            int sizeOfRemovedHumanPieces = removedHumanPieces.size();
            int selectedI = 0;
            boolean pieceTypeFound = false;
            
            for(int i = 1; i < sizeOfRemovedHumanPieces && pieceTypeFound; ++i){
            
                if(pieces.get(removedHumanPieces.get(i)).getTypeName().equals(
                        selectedTypeName)){
                
                    selectedI = i;
                    pieceTypeFound = true;
                }
            }
            
            selectedI = removedHumanPieces.get(selectedI);
            pieces.get(selectedI).setRank(targetSelectedRank);
            pieces.get(selectedI).setFile(targetSelectedFile);
            gameBoard.set(targetSelectedRank, targetSelectedFile, selectedI);
            selectedStep.setPieceId(selectedI);
            removedHumanPieces.remove(selectedI);
        }
        
        ++stepId;
    }
    
    
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
        
            playGame = false;
            gameStatus = "LOSE";
            
            gameUI.updateGameStatus(gameStatus);
        }        
    }
    
    
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
        
            playGame = false;
            gameStatus = "WIN";
            
            gameUI.updateGameStatus(gameStatus);
        }
    }
    
    
    // TODO it could be integrated in step decision tree builder
    private void selectNextMachineStep() throws Exception{
    
        // TASK) select the best option - max search, and apply to the game using 
        //       posteriori update(perform update after execution of further subroutines 
        //       of this method)
        
        ArrayList<GenStepKey> levelKeys = stepSequences.getLevelKeys(1);
        
        if(levelKeys.size() == 1){
        
            playGame = false;
            gameStatus = "WIN";
            
            gameUI.updateGameStatus(gameStatus);
            return;
        }
        
        int sizeOfLeafSteps = levelKeys.size();
        int maxI = 0;
        double currCumulativeValue = 0.0;
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
            
                playGame = false;
                gameStatus = "WIN";
                
                gameUI.updateGameStatus(gameStatus);
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
        
        int pieceId = stepSequences.getByKey(levelKeys.get(maxI)).getPieceId();
        GenPiece piece = pieces.get(pieceId);
        sourceStepHistory.add(new Step(pieceId, piece.getRank(), piece.getFile(), 
                piece.getValue(), 0, piece.getValue()));
        sourceStepHistory.add(stepSequences.getByKey(levelKeys.get(maxI)));

        Step step = stepSequences.getByKey(levelKeys.get(maxI));
        
        // piece removal in case of hit
        if(gameBoard.get(step.getRank(), step.getFile()) != -1){
        
            removedHumanPieces.add(gameBoard.get(step.getRank(), step.getFile()));
        }
        
        // pawn replacement with earlier hit piece
        pieceId = step.getPieceId();
        
        if(0 <= pieceId && pieceId <= 7 && step.getRank() == 7){
        
            // selection strategy: select piece with the highest value
            int sizeOfRemovedMachinePieces  = removedMachinePieces.size();
            int selectedI = 0;
            
            for(int i = 1; i < sizeOfRemovedMachinePieces; ++i){
            
                if(pieces.get(removedMachinePieces.get(i)).getValue() > 
                        pieces.get(removedMachinePieces.get(selectedI)).getValue()){
                
                    selectedI = i;
                }
            }
            
            selectedI = removedMachinePieces.get(selectedI);
            pieces.get(selectedI).setRank(step.getRank());
            pieces.get(selectedI).setFile(step.getFile());
            gameBoard.set(step.getRank(), step.getFile(), selectedI);
            // a priori piece type replacement
            step.setPieceId(selectedI);
            removedMachinePieces.remove(selectedI);
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

    
    public Stack<Step> getSourceStepHistory(){
    
        return sourceStepHistory;
    }
    
    
    public Stack<Step> getTargetStepHistory(){
    
        return targetStepHistory;
    }
    
    public void waitForDataRead() throws InterruptedException{
    
        statusUpdateLock.await();
    }
    
    public void signalForDataRead(){
    
        statusUpdateLock.signal();
    }
    
    public void waitForDataSave() throws Exception{
    
        statusSaveLock.await();
    }
    
    public void signalForDataSave() throws Exception{
    
        statusSaveLock.signal();
    }
}
