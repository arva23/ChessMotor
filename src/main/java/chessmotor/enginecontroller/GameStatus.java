package chessmotor.enginecontroller;

import java.time.Duration;
import java.util.Stack;

public class GameStatus extends GenericSaveStatus {

    // the following Game memebers are stored in this status class
    
    //String gameName
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
    public String getGameName(){
    
        return (String)entries.get("gameName");
    }
    
    public boolean getMachineBegins() {
        
        return (boolean)entries.get("machineBegins");
    }

    public boolean getMachineComes() {
        
        return (boolean)entries.get("machineComes");
    }

    public Duration getMachineTime() {
        
        return (Duration)entries.get("machineTime");
    }

    public Duration getHumanTime() {
        
        return (Duration)entries.get("humanTime");
    }

    public boolean getMachineIsInCheck() {
        
        return (boolean)entries.get("machineIsInCheck");
    }

    public boolean getHumanIsInCheck() {
        
        return (boolean)entries.get("humanIsInCheck");
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
    public void setGameName(String gameName){
    
        entries.put("gameName", gameName);
    }

    public void setMachineBegins(boolean machineBegins) {
     
        entries.put("machineBegins", machineBegins);
    }

    public void setMachineComes(boolean machineComes) {
        
        entries.put("machineComes", machineComes);
    }

    public void setMachineTime(Duration machineTime) {
        
        entries.put("machineTime", machineTime);
    }

    public void setHumanTime(Duration humanTime) {
        
        entries.put("humanTime", humanTime);
    }

    public void setMachineIsInCheck(boolean machineIsInCheck) {
        
        entries.put("machineIsInCheck", machineIsInCheck);
    }

    public void setHumanIsInCheck(boolean humanIsInCheck) {
        
        entries.put("humanIsInCheck", humanIsInCheck);
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
