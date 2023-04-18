package chessmotor.view;

// this class represents the game board that is consisted of squared


// the squares are managed by pooling the events, only one event is by event 
//  types to avoid overwhelmingly loaded listener cases
public class GameBoard implements IGameBoard {

    
    public GameBoard(int x, int y, int widht, int height, 
            Condition playerWaitCond, Condition playerActionCond,
            boolean allyComes, String[][] boardSquareStatus){
    
    }
    
    @Override
    public void setGameBoard(String[][] boardSquareStatus, boolean allyBegins,
            boolean allyComes){
    
    }
    
    @Override
    public JPanel getMainPanel(){
    }
    
    @Override
    public void alternateActivePlayer(){
    
    }
    
    @Override
    public String getPlayerActionResult(){
    
    }
    
    @Override
    public UnitSquare getPlayerActionSquare(){
    
    }
    
    @Override
    public void setSquare(boolean isAlly, String pieceType, int rank, int file) throws Exception{
    
    }
}
