package chessmotor.enginecontroller;

import chessmotor.enginecontroller.interfaces.IPlayer;
import chessmotor.enginecontroller.interfaces.IGame;
import chessmotor.enginecontroller.piecetypes.GenPiece;
import genmath.genmathexceptions.ValueOutOfRangeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MachinePlayer implements IPlayer{
    
    private IGame gameRef;
    private PieceContainer piecesRef;
    private GameBoardData gameBoardRef;
    private StepDecisionTree stepSequencesRef;
    private boolean isInCheck;
    private Stack<Integer> removedPiecesRef;
    private Stack<Integer> removedHumanPiecesRef;
    private double score;
    private Integer stepIdRef;
    
    private Duration time;
    private LocalDateTime intervalStart;

    /**
     * Parameterized constructor
     * @param gameRef reference to game handler object
     * @param piecesRef reference to pieces container
     * @param gameBoardRef reference to game board
     * @param stepSequencesRef reference to step generation tree
     * @param isInCheck whether player is in check
     * @param removedPiecesRef reference to removed machine player pieces
     * @param removedHumanPiecesRef reference to human player pieces
     * @param score aggregated score that was gathered, achieved during current 
     *              status of play, this value is required for status initialization 
     *              (also for status load)
     * @param stepIdRef reference to step identifier
     * @param time recently consumed, elapsed time from available provided player 
     *             time
     * @param intervalStart auxiliary, helper time point to time value calculation
     */
    public MachinePlayer(
            IGame gameRef,
            PieceContainer piecesRef,
            GameBoardData gameBoardRef,
            StepDecisionTree stepSequencesRef, 
            boolean isInCheck,
            Stack<Integer> removedPiecesRef,
            Stack<Integer> removedHumanPiecesRef,
            double score,
            Integer stepIdRef,
            Duration time,
            LocalDateTime intervalStart){
    
        this.gameRef = gameRef;
        this.piecesRef = piecesRef;
        this.gameBoardRef = gameBoardRef;
        this.stepSequencesRef = stepSequencesRef;
        this.isInCheck = isInCheck;
        this.removedPiecesRef = removedPiecesRef;
        this.removedHumanPiecesRef = removedHumanPiecesRef;
        this.score = score;
        this.stepIdRef = stepIdRef;
        this.time = time;
        this.intervalStart = intervalStart;
    }
    
    /**
     * Returns score of machine player
     * @return Current score
     */
    @Override
    public double getScore(){
    
        return score;
    }
    
    /**
     * It starts machine player clock in order to count its "thinking", computation 
     * time
     */
    @Override
    public void startClock(){
    
        intervalStart = LocalDateTime.now();
    }
    
    /**
     * It stops machine player's clock
     */
    @Override
    public void stopClock(){
    
        time.plus(LocalDateTime.now().until(
            intervalStart, ChronoUnit.SECONDS), ChronoUnit.SECONDS);
    }
    
    /**
     * Function returns machine player's consumed time
     * @return Consumed duration from provided duration
     */
    @Override
    public Duration getDuration(){
    
        return time;
    }
    
    /**
     * It generates first step of machine
     * @throws Exception 
     *         Rank or file is out of range, 
     *         Addition error (index out of bound),
     *         Index out of bounds at LinTreeMap,
     *         Key-value is null, no parent key has been found, 
     *         Key duplication/redundancy/preexisting key
     */
    public void generateFirstMachineStep() throws Exception{
    
        stepSequencesRef.generateFirstMachineStep();
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
    public void buildStrategy() throws Exception{
    
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        
        if(numberOfThreads == 0){
        
            throw new ValueOutOfRangeException("Unable to detect number of available " 
                    + "concurrent threads for execution.");
        }
        
        // executor service is only used at parallel generation
        ExecutorService generatorMgr = Executors.newFixedThreadPool(numberOfThreads);
        StepDecisionTree initStepSequences = new StepDecisionTree(stepSequencesRef);
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

            stepSequencesRef.unite(stepSequencesChunks.get(threadId));
        }
    }
    
    /**
     * Validates machine player after human player took step
     * @throws Exception 
     *         StepDecisionTree exception (see further)
     */
    @Override
    public void validateStatus() throws Exception{
    
        // looking of check mate on machine king piece
        
        ArrayList<GenStepKey> levelKeys = stepSequencesRef.getLevelKeys(1);
        
        int sizeOfLastNonLeafLevelKeys = levelKeys.size();
        
        Step step;
        for(int i = 0; i < sizeOfLastNonLeafLevelKeys; ++i){
        
            step = stepSequencesRef.getByKey(levelKeys.get(i));
            
            if(gameBoardRef.get(step.getRank(), step.getFile()) == 11){
            
                // machine king is in check
                isInCheck = true;
            }
        }
        
        if(isInCheck && piecesRef.get(11).generateSteps(gameBoardRef).isEmpty()){
            
            gameRef.setGamePlayStatus("WIN");
        }
    }
    
    /**
     * It selects next machine step similarly to human step request method
     * @throws Exception 
     *         StepDecisionTree exceptions (see further)
     *         Step creation exceptions (see further)
     *         DualStep creation exceptions (see further)
     */
    @Override
    public void getNextStep() throws Exception{
    
        // TODO it could be integrated in step decision tree builder
        
        // TASK) select the best option - max search, and apply to the game using 
        //       posteriori update(perform update after execution of further subroutines 
        //       of this method)
        
        ArrayList<GenStepKey> levelKeys = stepSequencesRef.getLevelKeys(1);
        
        if(levelKeys.isEmpty() && stepSequencesRef.getCurrDepth() == 1){
        
            gameRef.setGamePlayStatus("WIN");
            return;
        }
        
        int sizeOfLeafSteps = levelKeys.size();
        int maxI = 0;
        double currCumulativeValue;
        double maxCumulativeValue = stepSequencesRef.getByKey(
                levelKeys.get(maxI)).getCumulativeValue();
        
        // machine check defense
        if(isInCheck){
        
            boolean foundNextStep = false;
            
            for(int i = 1; i < sizeOfLeafSteps; ++i){
                
                if(stepSequencesRef.getByKey(levelKeys.get(i)).getPieceId() == 11){
                
                    foundNextStep = true;
                    
                    currCumulativeValue = stepSequencesRef.getByKey(
                    levelKeys.get(i)).getCumulativeValue();
            
                    if(maxCumulativeValue < currCumulativeValue){

                        maxCumulativeValue = currCumulativeValue;
                        maxI = i;
                    }
                }
            }
            
            if(!foundNextStep){
            
                gameRef.setGamePlayStatus("WIN");
                return;
            }
            else{
            
                isInCheck = false;
            }
        }
        else{
        
            for(int i = 1; i < sizeOfLeafSteps; ++i){

                currCumulativeValue = stepSequencesRef.getByKey(
                    levelKeys.get(i)).getCumulativeValue();
            
                if(maxCumulativeValue < currCumulativeValue){

                    maxCumulativeValue = currCumulativeValue;
                    maxI = i;
                }
            }
        }
        
        Step step = stepSequencesRef.getByKey(levelKeys.get(maxI));
        
        int pieceId = step.getPieceId();
        GenPiece selectedPiece = piecesRef.get(pieceId);
        GenPiece selectedSecondPiece;
        
        if(step instanceof DualStep){
        
            DualStep dualStep = (DualStep)step;
            selectedSecondPiece = piecesRef.get(dualStep.getSecondPieceId());
            
            if((selectedPiece.getTypeName().contains("king") 
                    || selectedPiece.getTypeName().contains("rook"))){
            
                // castling option
                        
                gameRef.addSourceStep(new DualStep(
                        "castling", selectedPiece.getPieceId(),
                        selectedSecondPiece.getPieceId(), 
                        selectedPiece.getRank(), selectedPiece.getFile(), 
                        selectedSecondPiece.getRank(), selectedSecondPiece.getFile(), 
                        0.0, 0, 0.0));
                gameRef.setSquareHighlighted(selectedPiece.getRank(),
                        selectedPiece.getFile());
                gameRef.setSquareHighlighted(selectedSecondPiece.getRank(),
                        selectedSecondPiece.getFile());
                
                gameBoardRef.set(selectedPiece.getRank(), 
                        selectedPiece.getFile(), -1);
                gameBoardRef.set(selectedSecondPiece.getRank(), 
                        selectedSecondPiece.getFile(), -1);
                
                selectedPiece.setRank(dualStep.getRank());
                selectedPiece.setFile(dualStep.getFile());
                selectedSecondPiece.setRank(dualStep.getSecondRank());
                selectedSecondPiece.setFile(dualStep.getSecondFile());
                
                gameBoardRef.set(selectedPiece.getRank(), 
                        selectedPiece.getFile(),
                        selectedPiece.getPieceId());
                gameBoardRef.set(selectedSecondPiece.getRank(), 
                        selectedSecondPiece.getFile(), 
                        selectedSecondPiece.getPieceId());
            }
            else if(selectedPiece.getTypeName().contains("pawn")){
                
                gameRef.addSourceStep(new DualStep(
                        "promotion", selectedPiece.getPieceId(),
                        selectedSecondPiece.getPieceId(), 
                        selectedPiece.getRank(), selectedPiece.getFile(), 
                        selectedSecondPiece.getRank(), selectedSecondPiece.getFile(), 
                        0.0, 0, 0.0));
                gameRef.setSquareHighlighted(selectedPiece.getRank(), 
                        selectedPiece.getFile());
                gameRef.setSquareHighlighted(selectedSecondPiece.getRank(), 
                        selectedSecondPiece.getFile());
                
                selectedPiece.setRank(dualStep.getRank());
                selectedPiece.setFile(dualStep.getFile());
                selectedSecondPiece.setRank(dualStep.getSecondRank());
                selectedSecondPiece.setFile(dualStep.getSecondFile());
                gameBoardRef.set(dualStep.getSecondRank(), dualStep.getSecondFile(), 
                        dualStep.getSecondPieceId());
            
                removedPiecesRef.remove(selectedSecondPiece.getPieceId());
                removedPiecesRef.add(selectedPiece.getPieceId());
            }   
        }
        else{
            
            // piece removal in case of hit
            if(gameBoardRef.get(step.getRank(), step.getFile()) != -1){

                removedHumanPiecesRef.add(
                        gameBoardRef.get(step.getRank(), step.getFile()));
                gameRef.setSquareHighlighted(selectedPiece.getRank(), selectedPiece.getRank());
            }
            else{
            
            gameRef.addSourceStep(new Step("standard", 
                    pieceId, selectedPiece.getRank(), 
                    selectedPiece.getFile(), selectedPiece.getValue(),
                    0, selectedPiece.getValue()));
                gameRef.setSquareHighlighted(selectedPiece.getRank(), selectedPiece.getRank());
            }
            
            selectedPiece.setRank(step.getRank());
            selectedPiece.setFile(step.getFile());
            gameBoardRef.set(selectedPiece.getRank(), selectedPiece.getFile(), 
                    selectedPiece.getPieceId());
        }
        
        // TASK) shift tree with one level, throw root away (root displacement)
        
        // set the actual root as source position        
        stepSequencesRef.setNewRootByKey(levelKeys.get(maxI));
        
        gameRef.addTargetStep(stepSequencesRef.getByKey(levelKeys.get(maxI)));
        gameRef.setSquareHighlighted(sourceStep.getRank(), sourceStep.getFile());
        
        // TASK) rename step node keys/identifiers (cyclic renaming)
        //       in order to limit the key length (comparison optimization)
        stepSequencesRef.trimKeys();
        
        score += gameRef.getTargetStep(gameRef.getTargetStepHistorySize() - 1).getValue();
        
        ++stepIdRef;
        
        // TASK) yield control to human player (asynchronous tasks)
    }
    
    /**
     * This method is a getter to provide machine player specific available removed
     * player pieces to be used again. It is used at visual piece selection.
     * @return Returns the player dependent removed pieces from the game table
     */
    @Override
    public Stack<String> getPromotionTypeNames(){
    
        Stack<String> removedMachinePiecesTypeNames = new Stack<>();
        
        int sizeOfRemovedMachinePieces = removedPiecesRef.size();
        
        for(int i = 0; i < sizeOfRemovedMachinePieces; ++i){
        
            removedMachinePiecesTypeNames.add(
                    piecesRef.get(removedPiecesRef.get(i)).getTypeName());
        }
        
        return removedMachinePiecesTypeNames;
    }
}
