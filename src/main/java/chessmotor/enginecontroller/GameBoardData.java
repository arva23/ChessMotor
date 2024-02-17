package chessmotor.enginecontroller;

public class GameBoardData {

    private Integer[][] gameBoard;
    
    /**
     * Default constructor
     */
    public GameBoardData(){
    
        gameBoard = new Integer[8][8];
    }
    
    /**
     * Copy constructor
     * @param orig object to be copied
     */
    public GameBoardData(GameBoardData orig){
    
        for(int i = 0; i < 8; ++i){
        
            for(int j = 0; j < 8; ++j){
            
                gameBoard[i][j] = orig.gameBoard[i][j];
            }
        }
    }
    
    /**
     * Obtains piece identifier at given position
     * @param col File of the position
     * @param row Rank of the position
     * @return Returns the identifier that is at the requested position
     */
    public Integer get(int col, int row){
    
        return gameBoard[col][row];
    }
    
    /**
     * Sets a new piece identifier at the desired position
     * @param col File of the position
     * @param row Rank of the position
     * @param newVal The new value of identifier that will be used at the updated 
     * position
     */
    public void set(int col, int row, Integer newVal){
    
        gameBoard[col][row] = newVal;
    }
}
