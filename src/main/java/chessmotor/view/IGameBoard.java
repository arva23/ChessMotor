package chessmotor.view;

import javax.swing.JPanel;

public interface IGameBoard {
    
    public JPanel getMainPanel();

    public void setGameBoard(String[][] boardSquareStatus, boolean machineBegins, 
            boolean machineComes);
    
    public void alternateActivePlayer();
    
    public UnitSquare getPlayerActionSquare();

    public String getPlayerActionResult();

    public void setSquare(boolean machineBegins, String pieceType, int targetRank,
            int targetFile) throws Exception;
}
