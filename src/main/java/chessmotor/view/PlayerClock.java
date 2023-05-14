package chessmotor.view;

// passive player playtime counter without timout and other conditional examination

import genmath.genmathexceptions.ValueOutOfRangeException;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerClock implements Runnable{

    private IConsoleUI consoleUI;
    
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
     * @param clockX Top left x coordinate component
     * @param clockY Top left y coordinate component
     * @param clockWidth Width of dual clock
     * @param clockHeight Height of dual clock
     * @throws Exception 
     *         x or y coordinate component is out of range
     *         with is out of range
     *         height if out of range
     */
    public PlayerClock(IConsoleUI consoleUI, int clockX, int clockY, int clockWidth, 
            int clockHeight) throws Exception{
    
        if(clockX < 0 || clockY < 0){
        
            throw new ValueOutOfRangeException("Clock position coordinates are out of range.");
        }
        
        this.clockX = clockX;
        this.clockY = clockY;
        
        if(clockWidth < 0 || clockWidth > 600){
            
            throw new ValueOutOfRangeException("Clock width is out of range.");
        }
        
        this.clockWidth = clockWidth;
        
        if(clockHeight < 0 || clockHeight > 300){
            
            throw new ValueOutOfRangeException("Clock height is out of range.");
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
    
    public void setPlayersClock(int whitePlayerTime, int blackPlayerTime, 
            boolean whitePlayerComes){
    
        this.whitePlayerTime = whitePlayerTime;
        this.blackPlayerTime = blackPlayerTime;
        whitePlayerTimeValue.setText("" + whitePlayerTime);
        blackPlayerTimeValue.setText("" + blackPlayerTime);
    }
    
    public void start(){
    
        operateClock = true;
    }
    
    public void switchPlayer(){
    
        switchPlayer = true;
    }
    
    public void stop(){
        
        operateClock = false;
    }
    
    @Override
    public void run(){
    
        // waiting for active play session
        while(!operateClock){
        
            try {
                
                Thread.sleep(1000);
            } 
            catch (InterruptedException ex) {
            
                consoleUI.println("Error at player clock operation.");
            }
        }
        
        while(operateClock){
        
            // operating recently selected player timer
            while(!switchPlayer){
        
                try {

                    Thread.sleep(1000);
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
    
    public JPanel getMainPanel(){
    
        return baseClockPanel;
    }
}
