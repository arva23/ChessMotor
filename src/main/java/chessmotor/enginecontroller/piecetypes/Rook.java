package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.Pair;
import java.util.ArrayList;

public class Rook extends GenPiece{
    
    public Rook(){
    
        super();
    }
    
    public Rook(boolean isWhite, double value, int file, int rank){
    
        super(isWhite ? "whiterook" : "blackrook",  value, file, rank);
    }
    
    @Override
    public ArrayList<Pair> generateSteps(int gameBoard[][]){
    
        ArrayList<Pair> steps = new ArrayList<Pair>();


        int lowerBound = 16;       
        int upperBound = 32;
        
        if(gameBoard[file][rank] >= 16){
        
            lowerBound = 0;
            upperBound = 16;
        }
        
        // right
        int sFile = file;
        int sRank = rank + 1;
        
        while(sRank < 8 && gameBoard[sFile][sRank] == -1){
        
            steps.add(new Pair(sFile, sRank));
            ++sRank;
        }
        
        if(sRank < 8 
                && (gameBoard[sFile][sRank] >= lowerBound 
                && gameBoard[sFile][sRank] < upperBound)){
        
            steps.add(new Pair(sFile, sRank));
        }
        
        // up
        sFile = file + 1;
        sRank = rank;
        
        while(sFile < 8 && gameBoard[sFile][sRank] == -1){
        
            steps.add(new Pair(sFile, sRank));
            ++sFile;
        }
        
        if(sFile < 8 
                && (gameBoard[sFile][sRank] >= lowerBound 
                && gameBoard[sFile][sRank] < upperBound)){
        
            steps.add(new Pair(sFile, sRank));
        }
        
        // left
        sFile = file;
        sRank = rank - 1;
        
        while(sRank >= 0 && gameBoard[sFile][sRank] == -1){
        
            steps.add(new Pair(sFile, sRank));
            --sRank;
        }
        
        if(sRank >= 0 
                && (gameBoard[sFile][sRank] >= lowerBound 
                && gameBoard[sFile][sRank] < upperBound)){
        
            steps.add(new Pair(sFile, sRank));
        }
        
        // down
        sFile = file - 1;
        sRank = rank;
        
        while(sFile >= 0 && gameBoard[sFile][sRank] == -1){
        
            steps.add(new Pair(sFile, sRank));
            --sFile;
        }
        
        if(sFile >= 0 
                && (gameBoard[sFile][sRank] >= lowerBound 
                && gameBoard[sFile][sRank] < upperBound)){
        
            steps.add(new Pair(sFile, sRank));
        }
        
        return steps;
    }
    
    @Override
    public ArrayList<Pair> testForCollisions(int gameBoard[][]){
    
        ArrayList<Pair> collisions = new ArrayList<Pair>();
        
        // todo
        
        return collisions;
    }
}
