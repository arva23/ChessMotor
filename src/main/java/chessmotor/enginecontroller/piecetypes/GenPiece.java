package chessmotor.enginecontroller.piecetypes;

// ecapsulate square pieces for further improvements

import chessmotor.enginecontroller.Pair;
import java.util.ArrayList;


// neutral empty piece
public class GenPiece {
    
    private double value;// penalty value for ally (machine)
    protected int file;
    protected int rank;
    
    public GenPiece(){
    
    }
    
    public GenPiece(double value, int file, int rank) {
    
        this.value = value;
        this.file = file;
        this.rank = rank;
    }
    
    public ArrayList<Pair> generateSteps(int gameBoard[][]){
    
        // It generates steps according to the limitations of other piece barriers 
        //  and gamefield boundaries.
        // Hit steps are also included.
        // It can only detect empty squares or occupied squares.
        ArrayList<Pair> steps = new ArrayList<Pair>();

        // todo, generate routes in aware of different sign of value of opponent pieces
        
        return steps;
    }
    
    
    public ArrayList<Pair> testForCollisions(int gameBoard[][]){
        
        ArrayList<Pair> collisions = new ArrayList<Pair>();
        
        // todo
        
        return collisions;
    }
    
    public double getValue(){
    
        return value;
    }
    
    public int getFile(){
    
        return file;
    }
    
    public int getRank(){
    
        return rank;
    }
    
    public void setFile(int file){
    
        this.file = file;
    }
    
    public void setRank(int rank){
    
        this.rank = rank;
    }
}
