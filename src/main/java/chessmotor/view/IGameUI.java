package chessmotor.view;

import java.time.Duration;

public interface IGameUI {

    public void run();
    
    public void printErr(String errMsg);
    
    public void updateTablePiece(int pieceId, int file, int rank);
    
    public void updateGameStatus(int gameStatus);
    
    public void updateTime(boolean toAlly, Duration time);
    
    public String readPlayerAction();    
}
