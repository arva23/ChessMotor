package chessmotor.view;

public class ConsoleManager implements IConsoleUI{
    
    private Object printMutex;
    private int msgId = 0;
    
    /**
     * It prints a general message without line break
     * @param msg Message to be printed
     */
    @Override
    public void print(String msg){
    
        synchronized(printMutex){
        
            System.out.print((msgId++) + "| " + msg);
        }
    }
    
    /**
     * It prints a general message with line break
     * @param msg Message to be printed
     */
    @Override
    public void println(String msg){
    
        synchronized(printMutex){
        
            System.out.println((msgId++) + "| " + msg);
        }
    }
    
    /**
     * It prints an error message onto the console and inserts a line break 
     * after that. The error messages are formatted differently than informative 
     * messages.
     * @param errMsg Error to be printed
     */
    @Override
    public void printErr(String errMsg){
    
        synchronized(printMutex){
        
            System.out.println((msgId++) + "[!! " + errMsg + "]");
        }
    }
}
