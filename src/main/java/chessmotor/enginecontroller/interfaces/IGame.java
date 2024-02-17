package chessmotor.enginecontroller.interfaces;

import chessmotor.enginecontroller.Step;
import java.util.Stack;

public interface IGame extends StatusSavable {
    
    public void runGame() throws Exception;

    public boolean machineComes();
    
    public String getGamePlayStatus();
    
    public Stack<String> getMachinePromotionTypeNames();
    
    public Stack<String> getHumanPromotionTypeNames();
    
    public void giveUpHumanPlayer();
    
    public void setGamePlayStatus(String gamePlayStatus);

    public String readHumanPlayerAction() throws Exception;
    
    public String selectPawnReplacement() throws Exception;
    
    public void addSourceStep(Step newStep);
    
    public void addTargetStep(Step newStep);
    
    public void setSquareHighlighted(int rank, int file) throws Exception;
    
    public void removeSquareHighlighted() throws Exception;
    
    public Step getSourceStep(int id) throws Exception;
    
    public Step getTargetStep(int id) throws Exception;
    
    public int getSourceStepHistorySize();
    
    public int getTargetStepHistorySize();
    
    public void waitForDataRead() throws Exception;
    
    public void signalForDataRead() throws Exception;

    public void waitForDataSave() throws Exception;
    
    public void signalForDataSave() throws Exception;

}
