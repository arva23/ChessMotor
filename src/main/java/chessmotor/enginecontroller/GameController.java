package chessmotor.enginecontroller;

import chessmotor.enginecontroller.interfaces.IInterOperationCalls;
import chessmotor.enginecontroller.interfaces.IGame;
import chessmotor.enginecontroller.interfaces.IGameController;
import chessmotor.view.ConsoleManager;
import chessmotor.view.GUIView;
import chessmotor.view.IConsoleUI;
import chessmotor.view.IGameUI;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Console based game controller class, it handles initialization problems with
 * subcomponents except gameplay generated exceptions (these are handles at graphical
 * use interface)
 * @author arva
 */
public class GameController implements IGameController, IInterOperationCalls{

    // console line user interface especially for debugging
    private IConsoleUI consoleUI;
    
    // game log manager object that handles status requests
    private GameLoader gameLogMgr;
    
    // realized game object that manages the actual game play
    private IGameUI gameUI;
    
    // gameUI is dynamically dispatched to a game in order take account into user 
    //  actions through GUI
    private IGame currGame;
    
    /**
     * Default initialization of high level system manager class
     */
    public GameController(){
    
        consoleUI = new ConsoleManager();
        
        // init. of game controller object
        currGame = new Game(consoleUI, gameUI, true, 6,
                10.0, 2, 
                Duration.ofSeconds(3600), 1024);
        
        // init. of game log manager
        try{
        
            gameLogMgr = new GameLoader(consoleUI, currGame);
        }
        catch(Exception e){
        
            consoleUI.println("Error at initialization of game log manager: " 
                    + e.getMessage());
        }
        
        // init. of GUI
        try{
        
            gameUI = new GUIView(consoleUI, this, currGame, 100, 
                    100, 800, 600);
        }
        catch(Exception e){
        
            consoleUI.println("Error at initialization of game interface: " 
                    + e.getMessage());
        }
    }
    
    /**
     * It starts the whole program via the high level game controller object
     */
    @Override
    public void runGame(){
    
        try {
            
            currGame.runGame();
        } 
        catch (Exception e) {
        
            consoleUI.println("Error at initialization of game controller (" 
                    + e.getMessage() + ")");
        }
    }
    
    /**
     * A transit method that propagates status load requests between controller 
     * and visual subsystem
     * @param gameName Identifier of the desired game status that was saved
     */
    @Override
    public void loadGame(String gameName){
    
        try{
    
            if(gameName.isEmpty()){

                throw new NullPointerException("Game name is empty.");
            }
            
            ComplexGameStatus gameStatus = gameLogMgr.loadGame(gameName);
            currGame.setStatus(gameStatus);
            // loading game status data from selected game file

            //  todo
        }
        catch(Exception ex){
        
            consoleUI.println("Could not load specific game status (" 
                    + ex.getMessage() + ")");
        }
    }
    
    /**
     * It loads the previously saved game
     */
    @Override
    public void loadLastSavedGame(){
    
        try{
            
            currGame.setStatus(gameLogMgr.loadLastGame());
        }
        catch(Exception ex){
        
            consoleUI.println("Could not load previously saved game (" 
                    + ex.getMessage() + ")");
        }
    }
    
    /**
     * A transit method that propagates status save requests between controller
     * and visual subsystem
     * It saved the current instantiated game state
     */
    @Override
    public void saveGamePlay(){
    
        try{
        
            String gameName = LocalDateTime.now().toString();
            gameLogMgr.saveGame(gameName, (ComplexGameStatus)currGame.getStatus());
        }
        catch(Exception ex){
        
            consoleUI.println("Could not save recent game status (" 
                    + ex.getMessage() + ")");
        }
    }
    
    /**
     * Triggers program start in most high level
     */
    @Override
    public void playGame(){
        
        try {
            
            currGame.runGame();
        } 
        catch (Exception e) {
            
            consoleUI.println("Error at attempting to run a game: " 
                    + e.getMessage());
        }    
    }
    
    /**
     * A transit method that propagates human player give up requests between 
     * controller and visual subsystem
     */
    @Override
    public void giveUpHumanPlayer(){
    
        currGame.giveUpHumanPlayer();
    }
    
    /**
     * Obtains game log manager that is responsible for game status logging 
     * and listing
     * @return It returns the log handler object
     */
    @Override
    public GameLoader getGameLogMgr(){
    
        return gameLogMgr;
    }

    @Override
    public String getRecentlyLoadedGameName(){
    
        return gameLogMgr.getRecentlyLoadedGameName();
    }
}
