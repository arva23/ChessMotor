package chessmotor.view;

public interface IGameBoard {
    
    public JPanel getGAmeTablePanel();
    
    public void alternateActivePlayer();
    
    public UnitSquare getPlayerActionSquare();

    public String getPlayerActionResult();

    public void setSquare(boolean allyBegins, String pieceType, int targetRank,
            int targetFile) throws Exception;
}
