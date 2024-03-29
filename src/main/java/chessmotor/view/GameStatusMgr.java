package chessmotor.view;

import genmath.genmathexceptions.ValueOutOfRangeException;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import chessmotor.enginecontroller.interfaces.IInterOperationCalls;

/**
 * A game status manager class that handles game start, load previously saved game 
 * status and saving game status. In this version only one saved game can be handled.
 */
public class GameStatusMgr {

    private IConsoleUI consoleUI;
    private IInterOperationCalls gameGUI;
    
    private JPanel gameStatusPanel;
    private JLabel gameStatusValue;
    
    private JLabel prevGameValue;
    private JButton giveUpHumanPlayerButton;
    private JButton saveGamePlayButton;
    private JButton loadLastSavedGameButton;
    private JButton playGameButton;
    
    /**
     * Parameterized constructor of game status manager
     * @param consoleUI Console manager object
     * @param gameGUI Graphical user interface handler uppermost layer object that 
     *                stores this lower level graphical panel as well
     * @param statusMgrX Up left corner x position
     * @param statusMgrY Up left corner y position
     * @param statusMgrWidth Width of panel
     * @param statusMgrHeight Height of panel
     * @throws Exception 
     *         NullPointerException
     *         ValueOutOfRangeException
     */
    public GameStatusMgr(IConsoleUI consoleUI, IInterOperationCalls gameGUI, 
            int statusMgrX, int statusMgrY,
            int statusMgrWidth, int statusMgrHeight) throws Exception{
        
        if(consoleUI == null){
        
            throw new NullPointerException("Console line manager is null.");
        }
        
        this.consoleUI = consoleUI;
        
        if(gameGUI == null){
        
            throw new NullPointerException("Visual component reference is null.");
        }
        
        this.gameGUI = gameGUI;
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        if(statusMgrX < 0 || statusMgrX > screenSize.getWidth()){
        
            throw new ValueOutOfRangeException("Top left corner x coordinate"
                    + " component is out of range.");
        }
        
        if(statusMgrY < 0 || statusMgrY > screenSize.getHeight()){
        
            throw new ValueOutOfRangeException("Top left corner y coordinate"
                    + " component is out of range.");
        }
        
        // initializing base panel of component
        gameStatusPanel = new JPanel();
        gameStatusPanel.setBounds(statusMgrX, statusMgrY, 
                statusMgrWidth, statusMgrHeight);
    
        // setting up game status subpanel
        //  status list:
        //   OK - nominal gameplay
        //   DRAW - game has ended with draw
        //   WIN - human player has won
        //   LOSE - human player has lost
        //   CHECK - human player in check
        gameStatusValue = new JLabel("OK");
        gameStatusPanel.add(gameStatusValue);
        
        
        // monolithic interoperation call: restructuration maybe needed
        prevGameValue = new JLabel(gameGUI.getRecentlyLoadedGameName());
        gameStatusPanel.add(prevGameValue);
        
        
        // setting up give up button
        giveUpHumanPlayerButton = new JButton("Give up");
        giveUpHumanPlayerButton.addMouseListener(new MouseAdapter(){
        
            @Override
            public void mouseClicked(MouseEvent e){
            
                gameGUI.giveUpHumanPlayer();
            }
        });
        
        gameStatusPanel.add(giveUpHumanPlayerButton);
        
        // setting up save function
        saveGamePlayButton = new JButton("Save");
        saveGamePlayButton.addMouseListener(new MouseAdapter(){
        
            @Override
            public void mouseClicked(MouseEvent e){
            
                try {
                    
                    gameGUI.saveGamePlay();
                }
                catch (Exception ex) {
                    
                    consoleUI.println("Current game status can not be saved.");
                }
            }
        });
        
        gameStatusPanel.add(saveGamePlayButton);
        
        // setting up previously saved game list with load option
        loadLastSavedGameButton = new JButton("Load saved game");
        loadLastSavedGameButton.addMouseListener(new MouseAdapter(){
        
            @Override
            public void mouseClicked(MouseEvent e){
            
                try{
                
                    gameGUI.loadLastSavedGame();
                }
                catch(Exception ex){
                
                    consoleUI.println("Unable to load previously saved game.");
                }
            }
        });
        
        gameStatusPanel.add(loadLastSavedGameButton);
        
        
        // setting up start game button
        playGameButton = new JButton("Play game");
        playGameButton.addMouseListener(new MouseAdapter(){
        
            @Override
            public void mouseClicked(MouseEvent e){
            
                try{
             
                    gameGUI.playGame();
                }
                catch(Exception ex){
                
                    consoleUI.println("Unable to start or continue game.");
                }
            }
        });
    }
    
    /**
     * Function returns main panel of game status manager
     * @return Main panel
     */
    public JPanel getMainPanel(){
    
        return gameStatusPanel;
    }
    
    /**
     * Function updates status of game in sense of human readable status
     * @param gameStatus Game status to be set
     */
    public void setGameStatus(String gameStatus){
    
        gameStatusValue.setText(gameStatus);
    }    
}
