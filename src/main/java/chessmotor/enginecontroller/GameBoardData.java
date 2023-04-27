package chessmotor.enginecontroller;

public class GameBoardData {

    private Integer[][] gameBoard;
    
    public GameBoardData(){
    
        gameBoard = new Integer[8][8];
    }
    
    public GameBoardData(GameBoardData orig){
    
        for(int i = 0; i < 8; ++i){
        
            for(int j = 0; j < 8; ++j){
            
                gameBoard[i][j] = orig.gameBoard[i][j];
            }
        }
    }
    
    public Integer get(int col, int row){
    
        return gameBoard[col][row];
    }
    
    public void set(int col, int row, Integer newVal){
    
        gameBoard[col][row] = newVal;
    }
}
