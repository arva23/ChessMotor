package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.GameBoardData;
import chessmotor.enginecontroller.Pair;
import genmath.genmathexceptions.ValueOutOfRangeException;
import java.util.ArrayList;

public class King extends GenPiece{
    
    /**
     * Default constructor
     */
    public King(){
    
        super();
    }
    
    public King(int pieceId, boolean isWhite, double value, int rank, int file)
    /**
     * Parameterized constructor for generic piece object
     * @param pieceId Piece identifier in range of [0, 32)
     * @param isWhite Whether piece is white or not
     * @param staticValue Static value of strength of piece
     * @param rank Rank of piece position
     * @param file File of piece position
     * @throws ValueOutOfRangeException 
     *         IllConditionedDataException 
     */
            throws Exception{
    
        super(pieceId, isWhite ? "whiteking" : "blackking", value, rank, file);
    }
    
    /**
     * Generates available following king steps
     * @param gameBoard The game board of the used game play that is used as a 
     * starting position for generation (occupied pice identifications)
     * @return It returns the generic pair values that stores the available further
     * steps for the king piece object at certain position
     * @throws ValueOutOfRangeException
     */
    @Override
    public ArrayList<Pair> generateSteps(GameBoardData gameBoard) throws ValueOutOfRangeException{
    
        ArrayList<Pair> steps = new ArrayList<>();

        int lowerBound = 16;
        int upperBound = 32;
        
        if(gameBoard.get(rank, file) >= 16){
        
            lowerBound = 0;
            upperBound = 16;
        }
        
        // right
        if(rank + 1 < 8 
                && (gameBoard.get(rank + 1, file) == -1 
                || gameBoard.get(rank + 1, file) >= lowerBound 
                && gameBoard.get(rank + 1, file) < upperBound)){
        
            steps.add(new Pair(rank + 1, file));
        }
        
        // up right
        if(rank + 1 < 8 && file + 1 < 8
                && (gameBoard.get(rank + 1, file + 1) == -1 
                || gameBoard.get(rank + 1, file + 1) >= lowerBound 
                && gameBoard.get(rank + 1, file + 1) < upperBound)){
        
            steps.add(new Pair(rank + 1, file + 1));
        }
        
        // up
        if(file + 1 < 8 
                && (gameBoard.get(rank, file + 1) == -1 
                || gameBoard.get(rank, file + 1) >= lowerBound 
                && gameBoard.get(rank, file + 1) < upperBound)){
        
            steps.add(new Pair(rank, file + 1));
        }
        
        // up left
        if(rank - 1 >= 0 && file + 1 < 8
                && (gameBoard.get(rank - 1, file + 1) == -1 
                || gameBoard.get(rank - 1, file + 1) >= lowerBound 
                && gameBoard.get(rank - 1, file + 1) < lowerBound)){
        
            steps.add(new Pair(rank - 1, file + 1));
        }
        
        // left
        if(rank - 1 >= 0 
                && (gameBoard.get(rank - 1, file) == -1 
                || gameBoard.get(rank - 1, file) >= lowerBound 
                && gameBoard.get(rank - 1, file) < upperBound)){
        
            steps.add(new Pair(rank - 1, file));
        }
        
        // down left
        if(rank - 1 >= 0 && file - 1 >= 0
                && (gameBoard.get(rank - 1, file - 1) == -1 
                || gameBoard.get(rank - 1, file - 1) >= lowerBound 
                && gameBoard.get(rank - 1, file - 1) < upperBound)){
        
            steps.add(new Pair(rank - 1, file - 1));
        }
        
        // down
        if(file - 1 >= 0 
                && (gameBoard.get(rank, file - 1) == -1 
                || gameBoard.get(rank, file - 1) >= lowerBound 
                && gameBoard.get(rank, file - 1) < upperBound)){
        
            steps.add(new Pair(rank, file - 1));
        }
        
        // down right
        if(rank + 1 < 8 && file - 1 >= 0
                && (gameBoard.get(rank + 1, file - 1) == -1 
                || gameBoard.get(rank + 1, file - 1) >= lowerBound 
                && gameBoard.get(rank + 1, file - 1) < upperBound)){
        
            steps.add(new Pair(rank + 1, file - 1));
        }
        
        return steps;
    }
}
