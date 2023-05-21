package chessmotor.enginecontroller;

import chessmotor.enginecontroller.piecetypes.GenPiece;
import genmath.genmathexceptions.IllConditionedDataException;
import genmath.genmathexceptions.NoObjectFoundException;
import genmath.genmathexceptions.ValueOutOfRangeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

public class HumanPlayer implements IPlayer{

    private IGame gameRef;
    private PieceContainer piecesRef;
    private GameBoardData gameBoardRef;
    private StepDecisionTree stepSequencesRef;
    private boolean isInCheck;
    private Stack<Integer> removedPiecesRef;
    private Stack<Integer> removedMachinePiecesRef;
    private boolean machineBegins;
    private double score;
    private Integer stepIdRef;
    
    private Duration time;
    private LocalDateTime intervalStart;
    
    private AtomicBoolean giveUpHumanPlayerGameController;
    
    
    public HumanPlayer(
            IGame gameRef,
            PieceContainer piecesRef,
            GameBoardData gameBoardRef,
            StepDecisionTree stepSequencesRef,
            boolean isInCheck,
            Stack<Integer> removedPiecesRef,
            Stack<Integer> removedMachinePiecesRef,
            double score,
            Integer stepIdRef,
            Duration time,
            LocalDateTime intervalStart,
            boolean giveUpHumanPlayerGameController){
    
        this.gameRef = gameRef;
        this.piecesRef = piecesRef;
        this.gameBoardRef = gameBoardRef;
        this.stepSequencesRef = stepSequencesRef;
        this.isInCheck = isInCheck;
        this.removedPiecesRef = removedPiecesRef;
        this.removedMachinePiecesRef = removedMachinePiecesRef;
        this.score = score;
        this.stepIdRef = stepIdRef;
        this.time = time;
        this.intervalStart = intervalStart;
        this.giveUpHumanPlayerGameController.set(
                giveUpHumanPlayerGameController);

    }
    
    @Override
    public double getScore(){
    
        return score;
    }
    
    @Override
    public void startClock(){
    
        intervalStart = LocalDateTime.now();
    }
    
    @Override
    public void stopClock(){
    
        time.plus(LocalDateTime.now().until(
            intervalStart, ChronoUnit.SECONDS), ChronoUnit.SECONDS);
    }
    
    @Override
    public Duration getTime(){
    
        return time;
    }
    
    public void setGiveUpHumanPlayerGameController(boolean status){
    
        giveUpHumanPlayerGameController.set(status);
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
    @Override
    public void getNextStep() throws Exception{
        
        String action;
        action = gameRef.readPlayerAction();
        
        if(action.isEmpty()){
        
            throw new NoObjectFoundException("Provided input is not acceptable.");
        }
        
        if(isInCheck && action.compareTo("giveup") == 0){
        
            gameRef.setGamePlayStatus("LOSE");
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
        
        if(gameBoardRef.get(sourceSelectedRank, sourceSelectedFile) == -1){
        
            throw new NoObjectFoundException("No available piece in the "
                    + "provided position.");
        }
        
        if(isInCheck && gameBoardRef.get(sourceSelectedRank, sourceSelectedFile) != 11){
        
            throw new Exception("Player is in check. Resolve check.");
        }
        
        GenPiece selectedPiece = 
            piecesRef.get(gameBoardRef.get(sourceSelectedRank, sourceSelectedFile));
        
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
        
        if(!(selectedPiece.generateSteps(gameBoardRef).contains(
                new Pair(targetSelectedRank, targetSelectedFile)))){
        
            throw new Exception("Illegal selected step by chosen piece.");
        }
        
        // case of castling
        boolean castlingOccurred = false;
        boolean pawnReplacementOccurred = false;
        GenPiece selectedSecondPiece = new GenPiece();
        
        if(selectedPiece.getTypeName().contains("king") 
                && (selectedSecondPiece = piecesRef.get(gameBoardRef.get(targetSelectedRank, targetSelectedFile)))
                        .getTypeName().contains("rook") 
                && selectedPiece.getRank() == 7 && selectedSecondPiece.getRank() == 7){
        
            // suboptimal condition tests
            
            // selectedRook = pieces[gameBoard[targetSelectedRank][targetSelectedFile]];
            boolean emptyInterFiles = true;
            
            if(selectedPiece.getFile() < selectedSecondPiece.getFile()){
            
                for(int fileInd = 5; fileInd < 7 && emptyInterFiles; ++fileInd){
                
                    emptyInterFiles = gameBoardRef.get(7, fileInd) == -1;
                }
                
                if(emptyInterFiles){
                
                    // perform castling
                    gameRef.addSourceStep(new DualStep("castling", 
                            11 + 16, 15 + 16, 7, 4, 7, 
                            7, 0.0, 0,
                            0.0));

                    gameBoardRef.set(7, 4, -1);
                    selectedPiece.setFile(6);
                    gameBoardRef.set(7, 6, 11 + 16);
                    gameBoardRef.set(7, 7, -1);
                    selectedSecondPiece.setFile(5);
                    gameBoardRef.set(7, 5, 15 + 16);
                    
                    castlingOccurred = true;
                }
            }
            else{
                
                for(int fileInd = 1; fileInd < 4 && emptyInterFiles; ++fileInd){
                 
                    emptyInterFiles = gameBoardRef.get(7, fileInd) == -1;
                }
                
                if(emptyInterFiles){
                
                    // perform castling
                    gameRef.addSourceStep(new DualStep("castling", 
                            16 + 11, 8 + 16, 7, 4, 7, 
                            0, 0.0, 0,
                            0.0));
                    
                    gameBoardRef.set(7, 4, -1);
                    selectedPiece.setFile(1);
                    gameBoardRef.set(7, 2, 11 + 16);
                    gameBoardRef.set(7, 0, -1);
                    selectedSecondPiece.setFile(2);
                    gameBoardRef.set(7, 3, 8 + 16);
                    
                    castlingOccurred = true;
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
            String selectedTypeName = gameRef.selectPawnReplacement();
        
            if(giveUpHumanPlayerGameController.get()){
            
                // terminate human action request
                return;
            }
            
            int sizeOfRemovedHumanPieces = removedPiecesRef.size();
            
            for(int i = 0; i < sizeOfRemovedHumanPieces; ++i){
                
                if(piecesRef.get(removedPiecesRef.get(i))
                        .getTypeName().equals(selectedTypeName)){
                
                    pawnReplacementOccurred = true;
                    
                    selectedSecondPiece = piecesRef.get(removedPiecesRef.get(i));
                    
                    gameRef.addSourceStep(new DualStep(
                            "promotion", selectedPiece.getPieceId(), 
                            selectedSecondPiece.getPieceId(),
                            sourceSelectedRank, sourceSelectedFile, 
                            targetSelectedRank, targetSelectedFile,
                            0.0, 0, 0.0));
                    
                    selectedPiece.setRank(sourceSelectedRank);
                    selectedPiece.setFile(sourceSelectedFile);
                    selectedSecondPiece.setRank(targetSelectedRank);
                    selectedSecondPiece.setFile(targetSelectedFile);
                    gameBoardRef.set(targetSelectedRank, targetSelectedFile, 
                            removedPiecesRef.get(i));
                    
                    removedPiecesRef.removeElementAt(i);
                    removedPiecesRef.add(selectedPiece.getPieceId());
                    
                    break;
                }
            }
        }
        else{
        
            // in case of hit as well

            selectedPiece.setRank(targetSelectedRank);
            selectedPiece.setFile(targetSelectedFile);

            gameBoardRef.set(targetSelectedRank, targetSelectedFile, 
                gameBoardRef.get(sourceSelectedRank, sourceSelectedFile));

            gameRef.addSourceStep(new Step("hit", 
                    gameBoardRef.get(sourceSelectedRank, 
                    sourceSelectedFile), sourceSelectedRank, 
                    sourceSelectedFile, 0.0,
                    0, 0.0));
            gameBoardRef.set(sourceSelectedRank, sourceSelectedFile, -1);
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
        
        if(stepSequencesRef.size() > 2){
        
            // finding step node with given position
            ArrayList<GenStepKey> levelKeys = stepSequencesRef.getLevelKeys(1);

            int sizeOfLevelKeys = levelKeys.size();

            int i = 0;

            for(; i < sizeOfLevelKeys; ++i){

                selectedStep = stepSequencesRef.getByKey(levelKeys.get(i));

                if(selectedStep.equals(currStep)){

                    break;
                }
            }
            
            // TASK) shift tree with one level, throw root away (root displacement)
            stepSequencesRef.setNewRootByKey(levelKeys.get(i));
            
            gameRef.addTargetStep(stepSequencesRef.getByKey(levelKeys.get(i)));
            
            // TASK) rename step node keys/identifiers (cyclic renaming)
            //       in order to limit the key length (comparison optimization)
            stepSequencesRef.trimKeys();
            
            score += gameRef.getTargetStep(gameRef.getTargetStepHistorySize() - 1).getValue();
        }
        else{
            
            selectedStep = new Step("standard",
                    gameBoardRef.get(targetSelectedRank, targetSelectedFile), 
                    targetSelectedRank, targetSelectedFile, 
                    selectedPiece.getValue(), 0,
                    selectedPiece.getValue());
            if(machineBegins){
                
                stepSequencesRef.addOne(new GenStepKey("a"), 
                        new GenStepKey("aa"), selectedStep);
                // saving previous level status
                stepSequencesRef.addToHistoryStack("aa", selectedStep);
            }
            else{
            
                stepSequencesRef.addOne(new GenStepKey("a"), 
                        new GenStepKey("a"), selectedStep);
                // saving previous level status
                stepSequencesRef.addToHistoryStack("a", selectedStep);
            }
            
            gameRef.addTargetStep(selectedStep);
        
            //humanScore = 0.0;// initial step has taken
        }
        
        // piece removal in case of hit
        if(gameBoardRef.get(targetSelectedRank, targetSelectedFile) != -1){
        
            removedMachinePiecesRef.add(
                    gameBoardRef.get(targetSelectedRank, targetSelectedFile));
        }
        
        ++stepIdRef;
    }
    
    /**
     * Validates human player after machine player took step
     * @throws Exception
     *         StepDecisionTree exception (see further)
     */
    @Override
    public void validateStatus() throws Exception{
    
        // looking for check mate on human king piece
        
        ArrayList<GenStepKey> levelKeys = stepSequencesRef.getLevelKeys(1);

        int sizeOfLevelKeys = levelKeys.size();

        Step step;
        for(int i = 0; i < sizeOfLevelKeys; ++i){

            step = stepSequencesRef.getByKey(levelKeys.get(i));

            if(gameBoardRef.get(step.getRank(), step.getFile()) == 11 + 16){

                // human king is in check
                isInCheck = true;
            }
        }
        
        if(isInCheck && piecesRef.get(11 + 16).generateSteps(gameBoardRef).isEmpty()){
        
            gameRef.setGamePlayStatus("LOSE");
        }        
    }
    
    /**
     * This method is a getter to provide human player specific available removed 
     * player pieces to be used again. It is used at visual piece selection.
     * @return Returns the player dependent removed pieces from the game table
     */
    @Override
    public Stack<String> getPromotionTypeNames(){
    
        Stack<String> removedHumanPiecesTypeNames = new Stack<>();
        
        int sizeOfRemovedHumanPieces = removedPiecesRef.size();
        
        for(int i = 0; i < sizeOfRemovedHumanPieces; ++i){
        
            removedHumanPiecesTypeNames.add(
                    piecesRef.get(removedPiecesRef.get(i)).getTypeName());
        }
        
        return removedHumanPiecesTypeNames;
    }    
}
