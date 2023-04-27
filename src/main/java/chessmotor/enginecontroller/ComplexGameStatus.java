package chessmotor.enginecontroller;

public class ComplexGameStatus extends GameStatus implements IVisualGameStatus{
    
    private String[][] boardSquareStatus;
    private boolean machineBegins;
    private boolean machineComes;
    private int whitePlayerTime;
    private int blackPlayerTime;
    private boolean whitePlayerComes;
    
    public ComplexGameStatus(){
    
        
    }
    
    @Override
    public String[][] getBoardSquareStatus(){
    
        return boardSquareStatus;
    }
    
    
    @Override
    public int getWhitePlayerTime(){
    
        return whitePlayerTime;
    }
    
    @Override
    public int getBlackPlayerTime(){
    
        return blackPlayerTime;
    }
    
    @Override
    public boolean getWhitePlayerComes(){
    
        return whitePlayerComes;
    }
    
    @Override
    public void setBoardSquareStatus(String[][] boardSquareStatus){
    
        this.boardSquareStatus = boardSquareStatus;
    }
    
}
