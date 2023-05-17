package chessmotor.enginecontroller.piecetypes;

// ecapsulate square pieces for further improvements

import chessmotor.enginecontroller.GameBoardData;
import chessmotor.enginecontroller.Pair;
import java.util.ArrayList;


// neutral empty piece
public class GenPiece {
    
    private int pieceId;
    private String typeName;
    private double value;// penalty value for machine
    protected int rank;
    protected int file;
    
    public GenPiece(){
    
    }
    
    
    /**
     * Parameterized constructor for generic piece object
     * @param pieceId Piece identifier in range of [0, 32)
     * @param typeName Piece literal identifier
     * @param value Value of strength of piece
     * @param rank Rank of piece position
     * @param file File of piece position
     * @throws ValueOutOfRangeException 
     *         IllConditionedDataException 
     */
    public GenPiece(int pieceId, String typeName, double value, int rank, int file) 
            throws Exception{
        this.pieceId = pieceId;
        this.typeName = typeName;
        this.value = value;
        this.rank = rank;
        this.file = file;
    }
    
    /**
     * Generates specialized available steps for extended piece types
     * @param gameBoard The game board of the used game play that is used as a 
     * starting position for generation (occupied piece identifications)
     * @return It returns the generic Pair values that stores the available further 
     * steps for the extended piece type at certain position
     * @throws Exception Generation exceptions (see extended, child classes, types)
     */
    public ArrayList<Pair> generateSteps(GameBoardData gameBoard) throws Exception{
    
        // It generates steps according to the limitations of other piece barriers 
        //  and gamefield boundaries.
        // Hit steps are also included.
        // It can only detect empty squares or occupied squares.
        ArrayList<Pair> steps = new ArrayList<>();

        // todo, generate routes in aware of different sign of value of human pieces
        
        return steps;
    }
    
    public int getPieceId(){
    
        return pieceId;
    }
    
    public String getTypeName(){
    
        return typeName;
    }
    
    public double getValue(){
    
        return value;
    }
    
    public int getRank(){
    
        return rank;
    }
    
    public int getFile(){
    
        return file;
    }
    
    /**
     * Sets a new value for rank
     * @param rank The new value
     * @throws ValueOutOfRangeException 
     */
    public void setRank(int rank) throws ValueOutOfRangeException{
    
        this.rank = rank;
    }
    
    /**
     * Sets a new value for file
     * @param file The new value
     * @throws ValueOutOfRangeException 
     */
    public void setFile(int file) throws ValueOutOfRangeException{
    
        this.file = file;
    }
}
