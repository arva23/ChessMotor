package chessmotor.enginecontroller;

import chessmotor.view.ConsoleManager;
import chessmotor.view.GUIView;
import chessmotor.view.IConsoleUI;
import chessmotor.view.IGameUI;
import java.time.Duration;

/**
 * Console based game controller class, it handles initialization problems with
 * subcomponents except gameplay generated exceptions (these are handles at graphical
 * use interface)
 * @author arva
 */
public class GameController {

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
        
        try {
            
            currGame.runGame();
        } 
        catch (Exception e) {
        
            consoleUI.println("Error at initialization of game controller: " 
                    + e.getMessage());
        }
    }
    
    /**
     * A transit method that propagates status load requests between controller 
     * and visual subsystem
     * @param gameName Identifier of the desired game status that was saved
     * @throws Exception
     *         Game name is empty, hence the game status can not be obtained
     *         Inherited exceptions from Game (see further)
     */
    public void loadGame(String gameName) throws Exception{
    
        if(gameName.isEmpty()){
        
            throw new NullPointerException("Game name is empty.");
        }
        
        ComplexGameStatus gameStatus = gameLogMgr.loadGame(gameName);
        currGame.setStatus(gameStatus);
        // loading game status data from selected game file
        
        //  todo
    }
    
    /**
     * A transit method that propagates status save requests between controller
     * and visual subsystem
     * @param gameName Identifier of the desired new game status that will be 
     * saved and persisted
     */
    public void saveGame(String gameName){
    
        // todo
    }
    
    /**
     * Triggers program start in most high level
     */
    public void startGame(){
    
        try {
            
            currGame.runGame();
        } 
        catch (Exception e) {
            
            consoleUI.println("Error at attempting to run a game: " + e.getMessage());
        }
    }
    
    /**
     * A transit method that propagates human player give up requests between 
     * controller and visual subsystem
     */
    public void giveUpHumanPlayer(){
    
        currGame.giveUpHumanPlayer();
    }
    
    /**
     * Obtains game log manager that is responsible for game status logging 
     * and listing
     * @return It returns the log handler object
     */
    public GameLoader getGameLogMgr(){
    
        return gameLogMgr;
    }
}