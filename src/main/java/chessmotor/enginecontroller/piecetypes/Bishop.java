package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.Pair;
import java.util.ArrayList;

public class Bishop extends GenPiece{
    
    public Bishop(){
    
        super();
    }
    
    public Bishop(boolean isWhite, double value, int file, int rank){
    
        super(isWhite ? "whitebishop" : "blackbishop", value, file, rank);
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
        
        // up left
        int sFile = file + 1;
        int sRank = rank - 1;
        
        while(sFile < 8 && sRank >= 0 && gameBoard[sFile][sRank] == -1){
        
            steps.add(new Pair(sFile, sRank));
            ++sFile;
            --sRank;
        }
        
        if(sFile < 8 && sRank >= 0 
                && gameBoard[sFile][sRank] >= lowerBound 
                && gameBoard[sFile][sRank] < upperBound){
        
            steps.add(new Pair(sFile, sRank));
        }
        
        // up right
        sFile = file + 1;
        sRank = rank + 1;
        
        while(sFile < 8 && sRank < 8 && gameBoard[sFile][sRank] == -1){
        
            steps.add(new Pair(sFile, sRank));
            ++sFile;
            ++sRank;
        }
        
        if(sFile < 8 && sRank < 8 
                && gameBoard[sFile][sRank] >= lowerBound 
                && gameBoard[sFile][sRank] < upperBound){
        
            steps.add(new Pair(sFile, sRank));
        }
        
        // down left
        sFile = file - 1;
        sRank = rank - 1;
        
        while(sFile >= 0 && sRank >= 0 && gameBoard[sFile][sRank] == -1){
        
            steps.add(new Pair(sFile, sRank));
            --sFile;
            --sRank;
        }
        
        if(sFile >= 0 && sRank >= 0 
                && gameBoard[sFile][sRank] >= lowerBound 
                && gameBoard[sFile][sRank] < upperBound){
        
            steps.add(new Pair(sFile, sRank));
        }
        
        // down right
        sFile = file - 1;
        sRank = rank + 1;
        
        while(sFile >= 0 && sRank < 8 && gameBoard[sFile][sRank] == -1){
        
            steps.add(new Pair(sFile, sRank));
            --sFile;
            ++sRank;
        }
        
        if(sFile >= 0 && sRank < 8 
                && gameBoard[sFile][sRank] >= lowerBound 
                && gameBoard[sFile][sRank] < upperBound){
        
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
