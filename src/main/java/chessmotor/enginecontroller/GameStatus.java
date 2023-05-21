package chessmotor.enginecontroller;

import java.time.Duration;
import java.util.Stack;

public class GameStatus extends GenericSaveStatus {

    // the following Game memebers are stored in this status class
    
    //boolean machineBegins
    //boolean machineComes
    //Duration getMachineTime
    //Duration getHumanTime
    //boolean machineIsInCheck
    //boolean humanIsInCheck
    //String gamePlayStatus
    //PieceContainer pieces
    //GameBoardData gameBoard
    //StepDecisionTree stepSequences
    //int stepId
    //Stack<Step> sourceStepHistory
    //Stack<Step> targetStepHistory
    
    public GameStatus(){
    
        super();
    }
    
    // OBJECT TYPE GETTERS
    
    public boolean getMachineBegins() {
        
        return (boolean)entries.get("machineBegins");
    }

    public boolean getMachineComes() {
        
        return (boolean)entries.get("machineComes");
    }

    
    public HumanPlayer getHumanPlayer(){
    
        return (HumanPlayer)entries.get("humanPlayer");
    }
    
    public MachinePlayer getMachinePlayer(){
    
        return (MachinePlayer)entries.get("machinePlayer");
    }

    public String getGamePlayStatus() {
        
        return (String)entries.get("gamePlayStatus");
    }

    public PieceContainer getPieces() {
        
        return (PieceContainer)entries.get("pieces");
    }

    public GameBoardData getGameBoard() {
        
        return (GameBoardData)entries.get("gameBoard");
    }

    public StepDecisionTree getStepSequences() {
        
        return (StepDecisionTree)entries.get("stepSequences");
    }

    public int getStepId() {
        
        return (int)entries.get("stepId");
    }

    public Stack<Step> getSourceStepHistory() {
        
        return (Stack<Step>)entries.get("sourceStepHistory");
    }

    public Stack<Step> getTargetStepHistory() {
        
        return (Stack<Step>)entries.get("targetStepHistory");
    }
    
    
    // OBJECT TYPE SETTERS
    public void setMachineBegins(boolean machineBegins) {
     
        entries.put("machineBegins", machineBegins);
    }

    public void setMachineComes(boolean machineComes) {
        
        entries.put("machineComes", machineComes);
    }

    public void setHumanPlayer(HumanPlayer humanPlayer){
    
        entries.put("humanPlayer", humanPlayer);
    }
    
    public void setMachinePlayer(MachinePlayer machinePlayer){
    
        entries.put("machinePlayer", machinePlayer);
    }

    public void setGamePlayStatus(String gamePlayStatus) {
        
        entries.put("gamePlayStatus", gamePlayStatus);
    }

    public void setPieces(PieceContainer pieces) {
        
        entries.put("pieces", pieces);
    }

    public void setGameBoard(GameBoardData gameBoard) {
        
        entries.put("gameBoard", gameBoard);
    }

    public void setStepSequences(StepDecisionTree stepSequences) {
        
        entries.put("stepSequences", stepSequences);
    }

    public void setStepId(int stepId) {
        
        entries.put("stepId", stepId);
    }

    public void setSourceStepHistory(Stack<Step> sourceStepHistory) {
        
        entries.put("sourceStepHistory", sourceStepHistory);
    }

    public void setTargetStepHistory(Stack<Step> targetStepHistory) {
        
        entries.put("targetStepHistory", targetStepHistory);
    }
}
