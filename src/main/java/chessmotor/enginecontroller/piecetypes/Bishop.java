package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.GameBoardData;
import chessmotor.enginecontroller.Pair;
import genmath.genmathexceptions.ValueOutOfRangeException;
import java.util.ArrayList;

public class Bishop extends GenPiece{
    
    /**
     * Default constructor
     */
    public Bishop(){
    
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
    public Bishop(int pieceId, boolean isWhite, double staticValue, int rank, int file) 
            throws Exception{
    
        super(pieceId, isWhite ? "whitebishop" : "blackbishop", staticValue, rank, file);
    }
    
    /**
     * Generates available following bishop steps
     * @param gameBoard The game board of the used game play that is used as a 
     * starting position for generation (occupied pice identifications)
     * @return It returns the generic pair values that stores the available further
     * steps for the bishop piece object at certain position
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
        
        // up left
        int sRank = rank - 1;
        int sFile = file + 1;

        while(sRank >= 0 && sFile < 8 && gameBoard.get(sRank, sFile) == -1){
        
            steps.add(new Pair(sRank, sFile));
            --sRank;
            ++sFile;
        }
        
        if(sRank >= 0 && sFile < 8
                && gameBoard.get(sRank, sFile) >= lowerBound 
                && gameBoard.get(sRank, sFile) < upperBound){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        // up right
        sRank = rank + 1;
        sFile = file + 1;

        while(sRank < 8 && sFile < 8 && gameBoard.get(sRank, sFile) == -1){
        
            steps.add(new Pair(sRank, sFile));
            ++sRank;            
            ++sFile;
        }
        
        if(sRank < 8 && sFile < 8 
                && gameBoard.get(sRank, sFile) >= lowerBound 
                && gameBoard.get(sRank, sFile) < upperBound){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        // down left
        sRank = rank - 1;
        sFile = file - 1;

        while(sRank >= 0 && sFile >= 0 && gameBoard.get(sRank, sFile) == -1){
        
            steps.add(new Pair(sRank, sFile));
            --sRank;
            --sFile;
        }
        
        if(sRank >= 0 && sFile >= 0
                && gameBoard.get(sRank, sFile) >= lowerBound 
                && gameBoard.get(sRank, sFile) < upperBound){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        // down right
        sRank = rank + 1;
        sFile = file - 1;
        
        while(sRank < 8 && sFile >= 0 && gameBoard.get(sRank, sFile) == -1){
        
            steps.add(new Pair(sRank, sFile));
            ++sRank;
            --sFile;
        }
        
        if(sRank < 8 && sFile >= 0
                && gameBoard.get(sRank, sFile) >= lowerBound 
                && gameBoard.get(sRank, sFile) < upperBound){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        // Implicit conversion of size of array as a dynamic value of given piece
        this.setDynamicValue(steps.size());
        
        return steps;
    }
}
