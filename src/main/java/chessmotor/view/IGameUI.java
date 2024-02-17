package chessmotor.view;

import chessmotor.enginecontroller.ComplexGameStatus;

public interface IGameUI{

    public void loadGame(ComplexGameStatus gameStatus);
    
    public void run();
    
    public void stop();
    
    public void updateGameStatus(String gameStatus);
    
    public void switchPlayerClock();
    
    public String readPlayerAction() throws Exception;    
    
    public String selectPawnReplacement() throws Exception;
    
    public void applyGenPlayerAction(String pieceType, int sourceRank, int sourceFile,
            int targetRank, int targetFile) throws Exception;

    /**
    * Function sets square background highlighted or not by a switch
    * @param rank Rank coordinate of square
    * @param file File coordinate of square
    * @throws java.lang.Exception See inherited exceptions in called functions.
    */
   public void setSquareHighlighted(int rank, int file) throws Exception;

   /**
    * Function removes highlighted square background, it sets default background 
    * to desired square, provided by its position coordinates
    * @param rank Rank coordinate of square
    * @param file File coordinate of square
    * @throws java.lang.Exception See inherited exceptions in called functions.
    */
   public void removeSquareHighlighted(int rank, int file) throws Exception;

}
