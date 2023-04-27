package chessmotor.view;

import chessmotor.enginecontroller.ComplexGameStatus;

public interface IGameUI {

    public void loadGame(ComplexGameStatus gameStatus);
    
    public void run();
    
    public void printErr(String errMsg);
    
    public void updateGameStatus(String gameStatus);
    
    public void switchPlayerClock();
    
    public String readPlayerAction() throws Exception;    
    
    public String selectPawnReplacement() throws Exception;
    
    public void applyGenPlayerAction(String pieceType, int sourceRank, int sourceFile,
            int targetRank, int targetFile) throws Exception;
}
