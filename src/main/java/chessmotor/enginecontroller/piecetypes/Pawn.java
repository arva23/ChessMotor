package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.GameBoardData;
import chessmotor.enginecontroller.Pair;
import genmath.genmathexceptions.ValueOutOfRangeException;
import java.util.ArrayList;

public class Pawn extends GenPiece{
    
    /**
     * Default constructor
     */
    public Pawn(){
    
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
    public Pawn(int pieceId, boolean isWhite, double staticValue, int rank, int file) 
            throws Exception{
    
        super(pieceId, isWhite ? "whitepawn" : "blackpawn", staticValue, rank, file);
    }
    
    /**
     * Generates available following pawn steps
     * @param gameBoard The game board of the used game play that is used as a 
     * starting position for generation (occupied pice identifications)
     * @return It returns the generic pair values that stores the available further
     * steps for the pawn piece object at certain position
     * @throws ValueOutOfRangeException
     */
    @Override
    public ArrayList<Pair> generateSteps(GameBoardData gameBoard) throws ValueOutOfRangeException{
    
        ArrayList<Pair> steps = new ArrayList<>();

        // pawn steps are symmetric comparing to human pawn steps
        
        if(gameBoard.get(rank, file) < 16){
        
            // machine pawn
            
            // from initial position in order to include double jump
            if(file == 1
                    && gameBoard.get(rank, file + 1) == -1 
                    && (gameBoard.get(rank, file + 2) == -1 
                    || gameBoard.get(rank, file + 2) >= 16
                    && gameBoard.get(rank, file + 2) < 32)){
            
                steps.add(new Pair(rank, file + 2));
            }
            
            // forward
            if(file + 1 < 8 && (gameBoard.get(rank, file + 1) == -1 
                    || gameBoard.get(rank, file + 1) >= 16 
                    && gameBoard.get(rank, file + 1) < 32)){

                steps.add(new Pair(rank, file + 1));
            }

            // forward left
            if(rank - 1 >= 0 && file + 1 < 8
                    && (gameBoard.get(rank - 1, file + 1) >= 16
                    && gameBoard.get(rank - 1, file + 1) < 32)){

                steps.add(new Pair(rank - 1, file + 1));
            }

            // forward right
            if(rank + 1 < 8 && file + 1 < 8
                    && (gameBoard.get(rank + 1, file + 1) >= 16
                    && gameBoard.get(rank + 1, file + 1) < 32)){

                steps.add(new Pair(rank + 1, file + 1));
            }
        }
        else{
        
            // human pawn
            
            // from initial position in order to include double jump
            if(file == 6 
                    && gameBoard.get(rank, file - 1) == -1
                    && (gameBoard.get(rank, file - 2) == -1 
                    || gameBoard.get(rank, file - 2) >= 0 
                    && gameBoard.get(rank, file - 2) < 16)){
            
                steps.add(new Pair(rank, file - 2));
            }
            
            // forward
            if(file - 1 >= 0 && gameBoard.get(rank, file - 1) == -1
                    || (gameBoard.get(rank, file - 1) >= 0 
                    && gameBoard.get(rank, file - 1) < 16)){
            
                steps.add(new Pair(rank, file - 1));
            }
            
            // forward left
            if(rank - 1 >= 0 && file - 1 >= 0
                    && (gameBoard.get(rank - 1, file - 1) >= 0 
                    && gameBoard.get(rank - 1, file - 1) < 16)){
            
                steps.add(new Pair(rank - 1, file - 1));
            }
            
            // forward right
            if(rank + 1 < 8 && file - 1 >= 0
                    && (gameBoard.get(rank + 1, file - 1) >= 0 
                    && gameBoard.get(rank + 1, file - 1) < 16)){
            
                steps.add(new Pair(rank + 1, file - 1));
            }
        }
        
        // Implicit conversion of size of array as a dynamic value of given piece
        this.setDynamicValue(steps.size());
        
        return steps;
    }
}
