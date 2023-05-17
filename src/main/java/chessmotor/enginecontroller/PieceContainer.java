package chessmotor.enginecontroller;

import chessmotor.enginecontroller.piecetypes.GenPiece;

/**
 * This class stores the actualized, explicit piece object for a game play
 */
public class PieceContainer {

    private GenPiece pieces[];

    /**
     * Default constructor with fixed length initialization of piece array
     */
    public PieceContainer(){
        
        pieces = new GenPiece[32];
    }
    
    /**
     * Sets new piece at the desired position
     * @param i The position of piece in the array that is going to be altered with 
     * new object
     * @param piece The new piece object that will be placed
     * @throws IndexOutOfBoundsException
     */
    public void set(int i, GenPiece piece) throws IndexOutOfBoundsException{
    
        pieces[i] = piece;
    }
    
    /**
     * Gets the desired piece at the pregiven position
     * @param i The position of piece in the array that is going to be obtained
     * @return Returns the requested piece object in generic form
     */
    public GenPiece get(int i){
    
        return pieces[i];
    }
}
