package chessmotor.enginecontroller.interfaces;

public interface IInterOperationCalls {
    
    public void giveUpHumanPlayer();
    
    public void saveGamePlay();

    public void loadLastSavedGame();
    
    public void playGame();
    
    public String getRecentlyLoadedGameName();
}
