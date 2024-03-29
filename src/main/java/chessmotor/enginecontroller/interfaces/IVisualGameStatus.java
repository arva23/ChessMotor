package chessmotor.enginecontroller.interfaces;

public interface IVisualGameStatus {

    public String[][] getBoardSquareStatus();
    
    public int getWhitePlayerTime();
    
    public int getBlackPlayerTime();
    
    public boolean getWhitePlayerComes();
    
    public void setBoardSquareStatus(String[][] boardSquareStatus);
}
