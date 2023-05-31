package chessmotor.enginecontroller.interfaces;

import chessmotor.enginecontroller.Step;
import java.util.Stack;

public interface IGame extends StatusSavable {
    
    public void runGame() throws Exception;

    public boolean machineComes();
    
    public Stack<String> getMachinePromotionTypeNames();
    
    public Stack<String> getHumanPromotionTypeNames();
    
    public void giveUpHumanPlayer();
    
    public void setGamePlayStatus(String gamePlayStatus);

    public String readPlayerAction() throws Exception;
    
    public String selectPawnReplacement() throws Exception;
    
    public void addSourceStep(Step newStep);
    
    public void addTargetStep(Step newStep);
    
    public Step getSourceStep(int id) throws Exception;
    
    public Step getTargetStep(int id) throws Exception;
    
    public int getSourceStepHistorySize();
    
    public int getTargetStepHistorySize();
    
    public void waitForDataRead() throws Exception;
    
    public void signalForDataRead() throws Exception;

    public void waitForDataSave() throws Exception;
    
    public void signalForDataSave() throws Exception;

}
