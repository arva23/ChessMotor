package chessmotor.enginecontroller;

public interface IGameController{

    public void runGame();
    
    public void loadGame(String gameName);

    public GameLoader getGameLogMgr();
}
