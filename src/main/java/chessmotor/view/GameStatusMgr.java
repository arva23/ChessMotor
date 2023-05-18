package chessmotor.view;

import genmath.genmathexceptions.ValueOutOfRangeException;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import chessmotor.enginecontroller.IInterOperationCalls;

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
    
    
    
    private JLabel loadGameButton;
    
    private JPanel loadSavedGamePanel;
    
    private JButton loadLastSavedGameButton;
    private JButton startGame;
    
    
    
    public GameStatusMgr(IConsoleUI consoleUI, IInterOperationCalls gameGUI, 
            int statusMgrX, int statusMgrY,
            int statusMgrWidth, int statusMgrHeight) throws Exception{
        
        if(gameGUI == null){
        
            throw new NullPointerException("Visual component reference is null.");
        }
        
        this.gameGUI = gameGUI;
        
        if(gameStatusManager == null){
        
            throw new NullPointerException("Game status manager object is null.");
        }
        
        this.gameStatusManager = gameStatusManager;
        
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
        
        // setting up save function
        saveGamePlayButton = new JButton("Save");
        saveGamePlayButton.addMouseListener(new MouseAdapter(){
        
            @Override
            public void mouseClicked(MouseEvent e){
            
                gameGUI.saveGamePlay();
            }
        });
        
        // todo
        
        // setting up previously saved game list with load option
        // todo
    }
    
    public JPanel getMainPanel(){
    
        return gameStatusPanel;
    }
    
    public void setGameStatus(String gameStatus){
    
        gameStatusValue.setText(gameStatus);
    }    
}
