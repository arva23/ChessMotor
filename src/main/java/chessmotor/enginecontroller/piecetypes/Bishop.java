package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.Pair;
import java.util.ArrayList;

public class Bishop extends GenPiece{
    
    public Bishop(){
    
        super();
    }
    
    public Bishop(int pieceId, boolean isWhite, double value, int rank, int file){
    
        super(pieceId, isWhite ? "whitebishop" : "blackbishop", value, rank, file);
    }
    
    @Override
    public ArrayList<Pair> generateSteps(Integer gameBoard[][]){
    
        ArrayList<Pair> steps = new ArrayList<Pair>();

        int lowerBound = 16;
        int upperBound = 32;
        
        if(gameBoard[rank][file] >= 16){
        
            lowerBound = 0;
            upperBound = 16;
        }
        
        // up left
        int sRank = rank - 1;
        int sFile = file + 1;

        while(sRank >= 0 && sFile < 8 && gameBoard[sRank][sFile] == -1){
        
            steps.add(new Pair(sRank, sFile));
            --sRank;
            ++sFile;
        }
        
        if(sRank >= 0 && sFile < 8
                && gameBoard[sRank][sFile] >= lowerBound 
                && gameBoard[sRank][sFile] < upperBound){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        // up right
        sRank = rank + 1;
        sFile = file + 1;

        while(sRank < 8 && sFile < 8 && gameBoard[sRank][sFile] == -1){
        
            steps.add(new Pair(sRank, sFile));
            ++sRank;            
            ++sFile;
        }
        
        if(sRank < 8 && sFile < 8 
                && gameBoard[sRank][sFile] >= lowerBound 
                && gameBoard[sRank][sFile] < upperBound){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        // down left
        sRank = rank - 1;
        sFile = file - 1;

        while(sRank >= 0 && sFile >= 0 && gameBoard[sRank][sFile] == -1){
        
            steps.add(new Pair(sRank, sFile));
            --sRank;
            --sFile;
        }
        
        if(sRank >= 0 && sFile >= 0
                && gameBoard[sRank][sFile] >= lowerBound 
                && gameBoard[sRank][sFile] < upperBound){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        // down right
        sRank = rank + 1;
        sFile = file - 1;
        
        while(sRank < 8 && sFile >= 0 && gameBoard[sRank][sFile] == -1){
        
            steps.add(new Pair(sRank, sFile));
            ++sRank;
            --sFile;
        }
        
        if(sRank < 8 && sFile >= 0
                && gameBoard[sRank][sFile] >= lowerBound 
                && gameBoard[sRank][sFile] < upperBound){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        return steps;
    }
    
    @Override
    public ArrayList<Pair> testForCollisions(Integer gameBoard[][]){
    
        ArrayList<Pair> collisions = new ArrayList<Pair>();
        
        // todo
        
        return collisions;
    }
}
