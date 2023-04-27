package chessmotor.view;

import chessmotor.enginecontroller.ComplexGameStatus;
import javax.swing.JPanel;

public interface IGameBoardView {
    
    public JPanel getMainPanel();

    public void setGameBoard(ComplexGameStatus gameStatus);
    
    public void alternateActivePlayer();
    
    public UnitSquare getPlayerActionSquare();

    public String getPlayerActionResult();
    
    public void setSquare(boolean machineBegins, String pieceType, int targetRank,
            int targetFile) throws Exception;
    
    public void waitForBoard() throws Exception;
    
    public void signalForBoard() throws Exception;
    
    public void waitForAction() throws Exception;
    
    public void signalForAction() throws Exception;
}
