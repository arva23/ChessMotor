package chessmotor.view;

import javax.swing.JPanel;

public interface IGameBoard {
    
    public JPanel getMainPanel();

    public void setGameBoard(String[][] boardSquareStatus, boolean allyBegins, 
            boolean allyComes);
    
    public void alternateActivePlayer();
    
    public UnitSquare getPlayerActionSquare();

    public String getPlayerActionResult();

    public void setSquare(boolean allyBegins, String pieceType, int targetRank,
            int targetFile) throws Exception;
}
