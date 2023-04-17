package chessmotor.view;

import java.time.Duration;

public interface IGameUI {

    public void loadGame(String[][] boardSquareStatus, int whitePlayerTime, 
            int blackPlayerTime, boolean whitePlayerComes, boolean allyBegins,
            boolean allyComes);
    
    public void run();
    
    public void printErr(String errMsg);
    
    public void updateGameStatus(int gameStatus);
    
    public void updateTime(boolean toAlly, Duration time);
    
    public String readPlayerAction() throws Exception;    
    
    public void applyGenPlayerAction(String pieceType, int sourceRank, int sourceFile,
            int targetRank, int targetFile) throws Exception;
}
