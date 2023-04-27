package chessmotor.enginecontroller;

public interface IGame extends StatusSavable {

    public void runGame() throws Exception;
    
    public void waitForDataRead() throws Exception;
    
    public void signalForDataRead() throws Exception;

    public void waitForDataSave() throws Exception;
    
    public void signalForDataSave() throws Exception;
}
