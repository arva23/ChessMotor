package chessmotor.enginecontroller.interfaces;

import java.time.Duration;
import java.util.Stack;

public interface IPlayer {

    public double getScore();
    
    public void startClock();
    
    public void stopClock();
    
    public Duration getDuration();
    
    public void getNextStep() throws Exception;
    
    public void validateStatus() throws Exception;
    
    public Stack<String> getPromotionTypeNames();
    
}
