package chessmotor.enginecontroller.piecetypes;

// ecapsulate square pieces for further improvements

import chessmotor.enginecontroller.GameBoardData;
import chessmotor.enginecontroller.Pair;
import genmath.genmathexceptions.IllConditionedDataException;
import genmath.genmathexceptions.ValueOutOfRangeException;
import java.util.ArrayList;


// neutral empty piece
public class GenPiece {
    
    static ArrayList<String> typeNameList;
    
    static{
    
        typeNameList = new ArrayList<>();
        
        typeNameList.add("Pawn");
        typeNameList.add("Rook");
        typeNameList.add("Knight");
        typeNameList.add("Bishop");
        typeNameList.add("King");
        typeNameList.add("Queen");
    }
    
    private int pieceId;
    private String typeName;
    private double value;// penalty value for machine
    protected int rank;
    protected int file;
    
    /**
     * Default constructor
     */
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
    
        if(pieceId < 0 || pieceId >= 32){
        
            throw new ValueOutOfRangeException("Piece identifier is out of range.");
        }
        
        this.pieceId = pieceId;
        
        if(typeName.isEmpty() || typeNameList.contains(typeName)){
        
            throw new IllConditionedDataException("Type name identifier of "
                    + "piece is empty or can not be found in sample type list.");
        }
        this.typeName = typeName;
        
        
        this.value = value;
        
        if(rank < 0 || rank > 7){
        
            throw new ValueOutOfRangeException("Rank value is out of range.");
        }
        
        this.rank = rank;
        
        if(file < 0 || file > 7){
        
            throw new ValueOutOfRangeException("File value is out of range.");
        }
        
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
        
        // CRTP should be applied for implicit dynamic value modification
        //  In this first version, explict, manually invoked functions are used 
        //  in child classes
        
        // It generates steps according to the limitations of other piece barriers 
        //  and gamefield boundaries.
        // Hit steps are also included.
        // It can only detect empty squares or occupied squares.
        ArrayList<Pair> steps = new ArrayList<>();

        // todo, generate routes in aware of different sign of value of human pieces
        
        return steps;
    }
    
    /**
     * Obtains piece identifier
     * @return Identifier of current piece object
     */
    public int getPieceId(){
    
        return pieceId;
    }
    
    /**
     * Obtains type of piece
     * @return Returns type of piece
     */
    public String getTypeName(){
    
        return typeName;
    }
    
    public double getValue(){
    
        return value;
    }
    
    public int getRank(){
    
        return rank;
    }
    
    /**
     * Obtains file coordinate of piece
     * @return File value
     */
    public int getFile(){
    
        return file;
    }
    
    /**
     * Sets a new value for rank
     * @param rank The new value
     * @throws ValueOutOfRangeException 
     */
    public void setRank(int rank) throws ValueOutOfRangeException{
    
        if(rank < 0 || rank > 7){
        
            throw new ValueOutOfRangeException("Rank is out of range.");
        }
        
        this.rank = rank;
    }
    
    /**
     * Sets a new value for file
     * @param file The new value
     * @throws ValueOutOfRangeException 
     */
    public void setFile(int file) throws ValueOutOfRangeException{
    
        if(file < 0 || file > 7){
        
            throw new ValueOutOfRangeException("File is out of range.");
        }
        
        this.file = file;
    }
}
