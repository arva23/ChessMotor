package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.GameBoardData;
import chessmotor.enginecontroller.Pair;
import genmath.genmathexceptions.ValueOutOfRangeException;
import java.util.ArrayList;

public class Queen extends GenPiece{
    
    /**
     * Default constructor
     */
    public Queen(){
    
        super();
    }
    
    public Queen(int pieceId, boolean isWhite, double value, int rank, int file)
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
    
        super(pieceId, isWhite ? "whitequeen" : "blackquen", value, rank, file);
    }

    /**
     * Generates available following queen steps
     * @param gameBoard The game board of the used game play that is used as a 
     * starting position for generation (occupied pice identifications)
     * @return It returns the generic pair values that stores the available further
     * steps for the queen piece object at certain position
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
        int sRank = rank + 1;
        int sFile = file;

        while(sRank < 8 && gameBoard.get(sRank, sFile) == -1){
        
            steps.add(new Pair(sRank, sFile));
            ++sRank;
        }
        
        if(sFile < 8 
                && (gameBoard.get(sRank, sFile) >= lowerBound 
                && gameBoard.get(sRank, sFile) < upperBound)){
        
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
                && (gameBoard.get(sRank, sFile) >= lowerBound 
                && gameBoard.get(sRank, sFile) < upperBound)){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        // up
        sRank = rank;
        sFile = file + 1;
        
        while(sFile < 8 && gameBoard.get(sRank, sFile) == -1){
        
            steps.add(new Pair(sRank, sFile));
            ++sFile;
        }
        
        if(sFile < 8 
                && (gameBoard.get(sRank, sFile) >= lowerBound 
                && gameBoard.get(sRank, sFile) < upperBound)){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        // up left
        sRank = rank - 1;
        sFile = file + 1;
        
        while(sRank >= 0 && sFile < 8 && gameBoard.get(sRank, sFile) == -1){
        
            steps.add(new Pair(sRank, sFile));
            --sRank;            
            ++sFile;
        }
        
        if(sRank >= 0 && sFile < 8 
                && (gameBoard.get(sRank, sFile) >= lowerBound 
                && gameBoard.get(sRank, sFile) < upperBound)){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        // left
        sRank = rank - 1;
        sFile = file;
        
        while(sRank >= 0 && gameBoard.get(sRank, sFile) == -1){
        
            steps.add(new Pair(sRank, sFile));
            --sRank;
        }
        
        if(sRank >= 0 
                && (gameBoard.get(sRank, sFile) >= lowerBound 
                && gameBoard.get(sRank, sFile) < upperBound)){
        
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
                && (gameBoard.get(sRank, sFile) >= lowerBound 
                && gameBoard.get(sRank, sFile) < upperBound)){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        // down
        sRank = rank;
        sFile = file - 1;
        
        while(sFile >= 0 && gameBoard.get(sRank, sFile) == -1){
        
            steps.add(new Pair(sRank, sFile));
            --sFile;
        }
        
        if(sFile >= 0 
                && (gameBoard.get(sRank, sFile) >= lowerBound 
                && gameBoard.get(sRank, sFile) < upperBound)){
        
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
                && (gameBoard.get(sRank, sFile) >= lowerBound 
                && gameBoard.get(sRank, sFile) < upperBound)){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        return steps;
    }
}
