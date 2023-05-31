package chessmotor.enginecontroller;

import chessmotor.enginecontroller.interfaces.IGame;
import chessmotor.view.IConsoleUI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import genmath.genmathexceptions.IllConditionedDataException;
import genmath.genmathexceptions.NoObjectFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Stack;

public class GameLoader implements Runnable{

    private IConsoleUI consoleUI;
    
    private IGame currGame;
    private boolean initialized;
    
    private ArrayList<String> savedGames;// entries are in chronological order
    
    private boolean runAutoSave;
    private int autoSaveDelay;
    
    private String recentlyLoadedGameName;
    private String recentlySavedGameName;
    
    /**
     * Parameterized constructor for game loader object
     * @param consoleUI Console line user interface especially for debug messages
     * @param currGame Realized game object that manages the current game play
     * @throws Exception 
     *         Game status URL list object route is incorrect, game status 
     *         can not be found
     */
    public GameLoader(IConsoleUI consoleUI, IGame currGame) throws Exception{
    
        this.consoleUI = consoleUI;
        
        this.currGame = currGame;
        initialized = true;
        
        Gson deserializer = new Gson();
        
        try (Reader reader = new FileReader("savedGameList")){
        
            savedGames = deserializer.fromJson(reader, 
                    new TypeToken<ArrayList<String>>(){}.getType());
        }
        catch(Exception e){
        
            throw new IOException("An error has occurred during reading "
                    + "status object URLs.");
        }
        
        recentlyLoadedGameName = "";
    }
    
    /**
     * Gets recently loaded game status name
     * @return It returns the requested game status object name
     */
    public String getRecentlyLoadedGameName(){
    
        return recentlyLoadedGameName;
    }
    
    /**
     * Gets the recently saved game status name
     * @return It returns the requested game status object name
     */
    public String getRecentlySavedGameName(){
     
        return recentlySavedGameName;
    }
    
    /**
     * Loads the recently saved game status
     * @return It returns the requested game status object
     * @throws Exception 
     */
    public ComplexGameStatus loadLastGame() throws Exception{
    
        return loadGame(savedGames.get(savedGames.size() - 1));
    }
    
    /**
     * It loads a previously saved game status
     * @param gameName The identifier of the previously saved game status
     * @return Returns the game status object for further decapsulation processes
     * @throws Exception 
     *         Game loader has not been initialized
     *         Game status has not been found with given identifier
     *         Inherited exceptions
     */
    public ComplexGameStatus loadGame(String gameName) throws Exception{
    
        if(!initialized){
        
            throw new NullPointerException("Game loader has not been initialized.");
        }
        
        boolean found = false;
        int sizeOfSavedGames = savedGames.size();
        
        int i = 0;
        
        for(; i < sizeOfSavedGames && !found; ++i){
        
            found = savedGames.get(i).equals(gameName);
        }
        
        if(!found){
        
            throw new NoObjectFoundException("File name not found.");
        }
        
        ComplexGameStatus savedGame = new ComplexGameStatus();
        
        Gson deserializer = new Gson();
        
        currGame.waitForDataRead();
        
        try (Reader reader = new FileReader(gameName + ".json")){
            
            savedGame.setMachineBegins(deserializer.fromJson(
                    reader, Boolean.class));
            savedGame.setMachineComes(deserializer.fromJson(
                    reader, Boolean.class));
            savedGame.setHumanPlayer(deserializer.fromJson(
                    reader, HumanPlayer.class));
            savedGame.setMachinePlayer(deserializer.fromJson(
                    reader, MachinePlayer.class));
            savedGame.setGamePlayStatus(deserializer.fromJson(
                    reader, String.class));
            savedGame.setPieces(deserializer.fromJson(
                    reader, PieceContainer.class));
            savedGame.setGameBoard(deserializer.fromJson(
                    reader, GameBoardData.class));
            savedGame.setStepSequences(deserializer.fromJson(
                    reader, StepDecisionTree.class));
            savedGame.setStepId(deserializer.fromJson(
                    reader, Integer.class));
            // todo, solve compile exception
            savedGame.setSourceStepHistory(deserializer.fromJson(
                    reader, new TypeToken<Stack<Step>>(){}.getType()));
            savedGame.setTargetStepHistory(deserializer.fromJson(
                    reader, new TypeToken<Stack<Step>>(){}.getType()));
            
            currGame.signalForDataSave();
        }
        catch(Exception e){
        
            throw new IllConditionedDataException("An error has occurred during reading raw data.");
        }
     
        recentlyLoadedGameName = gameName; 
        
        return savedGame;
    }
    
    /**
     * It saves a game status that conforms the criteria of generic save object
     * @param gameName The name of the game status being saved
     * @param savedGame The actual game status object that is used
     * @throws Exception 
     *         Inherited exceptions
     *         IOException during status saving action
     */
    public void saveGame(String gameName, ComplexGameStatus savedGame) throws Exception{
    
        boolean found = false;
        int sizeOfSavedGames = savedGames.size();
        
        int i = 0;
        for(; i < sizeOfSavedGames && !found; ++i){
        
            found = savedGames.get(i).equals(gameName);
        }
        
        if(found){
        
            // todo
            // overwrite current game status with the acutally selected name
            // update chronological order
            String tmpGameName = savedGames.get(i);
            savedGames.remove(i);
            savedGames.add(tmpGameName);
        }
        else{
        
            savedGames.add(gameName);
        }
        
        Gson serializer = new Gson();

        currGame.waitForDataRead();

        try (Writer writer = new FileWriter(gameName + ".json")){

            serializer.toJson(savedGame.getMachineBegins(), writer);
            serializer.toJson(savedGame.getMachineComes(), writer);
            serializer.toJson(savedGame.getHumanPlayer(), writer);
            serializer.toJson(savedGame.getMachinePlayer(), writer);
            serializer.toJson(savedGame.getGamePlayStatus(), writer);
            serializer.toJson(savedGame.getPieces(), writer);
            serializer.toJson(savedGame.getGameBoard(), writer);
            serializer.toJson(savedGame.getStepSequences(), writer);
            serializer.toJson(savedGame.getStepId(), writer);
            serializer.toJson(savedGame.getSourceStepHistory(), writer);
            serializer.toJson(savedGame.getTargetStepHistory(), writer);

            currGame.signalForDataSave();

            writer.flush();
            writer.close();
        }
        catch(Exception e){

            throw new IOException("An error has occurred during writing raw data.");
        }
        
        recentlySavedGameName = gameName;
    }
    
    /**
     * Automatic saving routine with predefined autosave frequency
     */
    @Override
    public void run(){
    
        while(!runAutoSave){
        
            try{
            
                ComplexGameStatus currComplexGameStatus = 
                        (ComplexGameStatus)currGame.getStatus();
                
                // waiting for finishing operation of game status update
                currGame.waitForDataRead();
                saveGame("autosave", currComplexGameStatus);
                currGame.signalForDataSave();
                Thread.sleep(autoSaveDelay);
            }
            catch(Exception e){
            
                consoleUI.println("An error has occurred during autosaving.");
            }
        }
    }
}
