package chessmotor.enginecontroller;

import java.util.Stack;

public interface IGame extends StatusSavable {
    
    public void runGame() throws Exception;

    public boolean machineComes();
    
    public Stack<String> getMachinePromotionTypeNames();
    
    public Stack<String> getHumanPromotionTypeNames();
    
    public void waitForDataRead() throws Exception;
    
    public void signalForDataRead() throws Exception;

    public void waitForDataSave() throws Exception;
    
    public void signalForDataSave() throws Exception;

    public void giveUpHumanPlayer();
}
