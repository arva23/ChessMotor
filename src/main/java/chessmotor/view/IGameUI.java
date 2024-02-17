package chessmotor.view;

import chessmotor.enginecontroller.ComplexGameStatus;

/**
 * Interface takes constraints for standardized game GUI in order to provide a 
 * generic game engine
 * @author arva
 */
public interface IGameUI{

    /**
     * Loads game with a provided status parameter
     * @param gameStatus Game status parameter
     */
    public void loadGame(ComplexGameStatus gameStatus);
    
    /**
     * Function starts the game
     */
    public void run();
    
    /**
     * Function stops the game
     */
    public void stop();
    
    /**
     * Function updates the game
     * @param gameStatus Status to be assigned for the recent game status
     */
    public void updateGameStatus(String gameStatus);
    
    /**
     * Function switches player clock during game play by alternating between the
     * two players
     */
    public void switchPlayerClock();
    
    public String readPlayerAction() throws Exception;    
    /**
     * Function obtains player action from engine
     * @return Player action in literal form
     * @throws Exception 
     *         Derived exceptions
     */
    
    /**
     * Aid function for selecting pawn replacement if this option is activated and 
     * the circumstances of the status of the game play make this available
     * @return Double step of two pieces in case of pawn replacement, the new 
     *         positions of pieces (position king and selected pawn)
     * @throws Exception
     *         Derived exceptions
     */
    public String selectPawnReplacement() throws Exception;
    
    /**
     * Function applies generic player action for visualization interface of game play
     * @param pieceType Type of piece
     * @param sourceRank Source rank of coordinate of position
     * @param sourceFile Source file of coordinate of position
     * @param targetRank Target rank of coordinate of position
     * @param targetFile Target file of coordinate of position
     * @throws Exception 
     *         Derived exceptions
     */
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
