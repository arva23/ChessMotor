package chessmotor.enginecontroller.interfaces;

import chessmotor.enginecontroller.GameLoader;

public interface IGameController{

    public void runGame();
    
    public void loadGame(String gameName);

    public GameLoader getGameLogMgr();
}
