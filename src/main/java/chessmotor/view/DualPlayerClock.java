package chessmotor.view;

// passive player playtime counter without timout and other conditional examination

import genmath.genmathexceptions.ValueOutOfRangeException;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Class represents a dual player clock for counting elapsed time visually for the 
 * players
 * @author arva
 */
public class DualPlayerClock implements Runnable{

    private IConsoleUI consoleUI;
    
    // Timeout for action in milliseconds
    private final int oneSecond = 1000;
    
    private int clockX;
    private int clockY;
    private int clockWidth;
    private int clockHeight;
    
    private JPanel baseClockPanel;
    private int whitePlayerTime;
    private int blackPlayerTime;
    
    private JPanel whitePlayerTimePanel;
    private JLabel whitePlayerTimeValue;
    
    private JPanel blackPlayerTimePanel;
    private JLabel blackPlayerTimeValue;
    
    private boolean operateClock;
    private boolean switchPlayer;
    private boolean whitePlayerComes;
    
    /**
     * Parameterized constructor for PlayerClock
     * @param consoleUI Console line message printer for errors especially
     * @param clockX Top left x coordinate component
     * @param clockY Top left y coordinate component
     * @param clockWidth Width of dual clock
     * @param clockHeight Height of dual clock
     * @throws Exception 
     *         x or y coordinate component is out of range
     *         with is out of range
     *         height if out of range
     */
    public DualPlayerClock(IConsoleUI consoleUI, int clockX, int clockY, int clockWidth, 
            int clockHeight) throws Exception{
    
        if(clockX < 0 || clockY < 0){
        
            throw new ValueOutOfRangeException("Clock position coordinates "
                    + "are out of range (lower bound violation).");
        }
        
        if(clockWidth < 0 || clockWidth > 600){
            
            throw new ValueOutOfRangeException("Clock width is out of range.");
        }
        
        if(clockHeight < 0 || clockHeight > 300){
            
            throw new ValueOutOfRangeException("Clock height is out of range.");
        }
        
        if(clockX > clockWidth || clockY > clockHeight){
        
            throw new ValueOutOfRangeException("Clock position coordinates "
                    + "are out of range (upper bound violation).");
        }
        
        this.clockHeight = clockHeight;
        
        baseClockPanel = new JPanel();
        baseClockPanel.setBounds(clockX, clockY, clockWidth, clockHeight);
        
        whitePlayerTime = 0;
        blackPlayerTime = 0;
    
        whitePlayerTimePanel = new JPanel();
        whitePlayerTimePanel.setBounds(0, 0, clockWidth / 2, clockHeight / 2);
        whitePlayerTimeValue = new JLabel("" + whitePlayerTime);
        whitePlayerTimeValue.setSize(clockWidth / 2, clockHeight / 2);
        baseClockPanel.add(whitePlayerTimePanel);
        
        blackPlayerTimePanel = new JPanel();
        blackPlayerTimePanel.setBounds(clockWidth / 2, 0, clockWidth / 2, clockHeight / 2);
        blackPlayerTimeValue = new JLabel("" + blackPlayerTime);
        blackPlayerTimeValue.setSize(clockWidth / 2, clockHeight / 2);
        baseClockPanel.add(blackPlayerTimePanel);
        
        operateClock = false;
        switchPlayer = false;
        whitePlayerComes = true;
    }
    
    /**
     * Function sets player clocks in case of previously saved game status
     * @param whitePlayerTime Time of white player
     * @param blackPlayerTime Time of black player
     * @param whitePlayerComes Whether the white player comes or not
     */
    public void setPlayersClock(int whitePlayerTime, int blackPlayerTime, 
            boolean whitePlayerComes){
    
        this.whitePlayerTime = whitePlayerTime;
        this.blackPlayerTime = blackPlayerTime;
        whitePlayerTimeValue.setText("" + whitePlayerTime);
        blackPlayerTimeValue.setText("" + blackPlayerTime);
    }
    
    /**
     * Function starts player clock
     */
    public void start(){
    
        operateClock = true;
    }
    
    /**
     * Function switches between player clock alternately
     */
    public void switchPlayer(){
    
        switchPlayer = true;
    }
    
    /**
     * Function stops player clock
     */
    public void stop(){
        
        operateClock = false;
    }
    
    /**
     * Run function that is responsible for parallel execution
     */
    @Override
    public void run(){
    
        // waiting for active play session
        while(!operateClock){
        
            try {
                
                Thread.sleep(oneSecond);
            } 
            catch (InterruptedException ex) {
            
                consoleUI.println("Error at player clock operation.");
            }
        }
        
        while(operateClock){
        
            // operating recently selected player timer
            while(!switchPlayer){
        
                try {

                    Thread.sleep(oneSecond);
                    if(whitePlayerComes){
                    
                        ++whitePlayerTime;
                        whitePlayerTimeValue.setText("" + whitePlayerTime);
                    }
                    else{
                    
                        ++blackPlayerTime;
                        blackPlayerTimeValue.setText("" + blackPlayerTime);
                    }
                }
                catch (InterruptedException ex) {

                    consoleUI.println("Error at player clock operation.");
                }
            }
            
            whitePlayerComes = !whitePlayerComes;
            switchPlayer = false;
        }
    }
    
    /**
     * Function returns main panel of dual player clock object
     * @return Main panel
     */
    public JPanel getMainPanel(){
    
        return baseClockPanel;
    }
}
