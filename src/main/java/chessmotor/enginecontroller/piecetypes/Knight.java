package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.GameBoardData;
import chessmotor.enginecontroller.Pair;
import genmath.genmathexceptions.ValueOutOfRangeException;
import java.util.ArrayList;

public class Knight extends GenPiece{
    
    /**
     * Default constructor
     */
    public Knight(){
    
        super();
    }
    
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
    public Knight(int pieceId, boolean isWhite, double staticValue, int rank, int file) 
            throws Exception{
    
        super(pieceId, isWhite ? "whiteknight" : "blackknight", staticValue, rank, file);
    }
    
    /**
     * Generates available following knight steps
     * @param gameBoard The game board of the used game play that is used as a 
     * starting position for generation (occupied pice identifications)
     * @return It returns the generic pair values that stores the available further
     * steps for the knight piece object at certain position
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
        
        // up right #1
        if(rank + 2 < 8 && file + 1 < 8
                && (gameBoard.get(rank + 2, file + 1) == -1 
                || gameBoard.get(rank + 2, file + 1) >= lowerBound 
                && gameBoard.get(rank + 2, file + 1) < upperBound)){
        
            steps.add(new Pair(rank + 2, file + 1));
        }
        
        // up right #2
        if(rank + 1 < 8 && file + 2 < 8
                && (gameBoard.get(rank + 1, file + 2) == -1 
                || gameBoard.get(rank + 1, file + 2) >= lowerBound 
                && gameBoard.get(rank + 1, file + 2) < upperBound)){
        
            steps.add(new Pair(rank + 1, file + 2));
        }
        
        // up left #1
        if(rank - 1 >= 0 && file + 2 < 8
                && (gameBoard.get(rank - 1, file + 2) == -1 
                || gameBoard.get(rank - 1, file + 2) >= lowerBound 
                && gameBoard.get(rank - 1, file + 2) < upperBound)){
        
            steps.add(new Pair(rank - 1, file + 2));
        }
        
        // up left #2
        if(rank - 2 >= 0 && file + 1 < 8
                && (gameBoard.get(rank - 2, file + 1) == -1 
                || gameBoard.get(rank - 2, file + 1) >= lowerBound 
                && gameBoard.get(rank - 2, file + 1) < upperBound)){
        
            steps.add(new Pair(rank - 2, file + 1));
        }
        
        // down left #1
        if(rank - 2 >= 0 && file - 1 >= 0
                && (gameBoard.get(rank - 2, file - 1) == -1 
                || gameBoard.get(rank - 2, file - 1) >= lowerBound 
                && gameBoard.get(rank - 2, file - 1) < upperBound)){
        
            steps.add(new Pair(rank - 2, file - 1));
        }
        
        // down left #2
        if(rank - 1 >= 0 && file - 2 >= 0
                && (gameBoard.get(rank - 1, file - 2) == -1 
                || gameBoard.get(rank - 1, file - 2) >= lowerBound 
                && gameBoard.get(rank - 1, file - 2) < upperBound)){
        
            steps.add(new Pair(rank - 1, file - 2));
        }

        // down right #1
        if(rank + 1 < 8 && file - 2 >= 0 
                && (gameBoard.get(rank + 1, file - 2) == -1 
                || gameBoard.get(rank + 1, file - 2) >= lowerBound 
                && gameBoard.get(rank + 1, file - 2) < upperBound)){
        
            steps.add(new Pair(rank + 1, file - 2));
        }

        // down right #2
        if(rank + 2 < 8 && file - 1 >= 0
                && (gameBoard.get(rank + 2, file - 1) == -1 
                || gameBoard.get(rank + 2, file - 1) >= lowerBound 
                && gameBoard.get(rank + 2, file - 1) < upperBound)){
        
            steps.add(new Pair(rank + 2, file - 1));
        }
        
        // Implicit conversion of size of array as a dynamic value of given piece
        this.setDynamicValue(steps.size());
        
        return steps;
    }
}
