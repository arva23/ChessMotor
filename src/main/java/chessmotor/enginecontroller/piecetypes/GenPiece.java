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
    
    public GenPiece(int pieceId, String typeName, double value, int rank, int file) {
    
        this.pieceId = pieceId;
        this.typeName = typeName;
        this.value = value;
        this.rank = rank;
        this.file = file;
    }
    
    public ArrayList<Pair> generateSteps(GameBoardData gameBoard){
    
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
    
    public void setRank(int rank){
    
        this.rank = rank;
    }
    
    public void setFile(int file){
    
        this.file = file;
    }
}
