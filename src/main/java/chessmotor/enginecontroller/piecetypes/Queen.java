package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.Pair;
import java.util.ArrayList;

public class Queen extends GenPiece{
    
    public Queen(){
    
        super();
    }
    
    public Queen(int pieceId, boolean isWhite, double value, int rank, int file){
    
        super(pieceId, isWhite ? "whitequeen" : "blackquen", value, rank, file);
    }

    @Override
    public ArrayList<Pair> generateSteps(int gameBoard[][]){
    
        ArrayList<Pair> steps = new ArrayList<Pair>();

        int lowerBound = 16;
        int upperBound = 32;
        
        if(gameBoard[rank][file] >= 16){
        
            lowerBound = 0;
            upperBound = 16;
        }
        
        // right
        int sRank = rank + 1;
        int sFile = file;

        while(sRank < 8 && gameBoard[sRank][sFile] == -1){
        
            steps.add(new Pair(sRank, sFile));
            ++sRank;
        }
        
        if(sFile < 8 
                && (gameBoard[sRank][sFile] >= lowerBound 
                && gameBoard[sRank][sFile] < upperBound)){
        
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
                && (gameBoard[sRank][sFile] >= lowerBound 
                && gameBoard[sRank][sFile] < upperBound)){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        // up
        sRank = rank;
        sFile = file + 1;
        
        while(sFile < 8 && gameBoard[sRank][sFile] == -1){
        
            steps.add(new Pair(sRank, sFile));
            ++sFile;
        }
        
        if(sFile < 8 
                && (gameBoard[sRank][sFile] >= lowerBound 
                && gameBoard[sRank][sFile] < upperBound)){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        // up left
        sRank = rank - 1;
        sFile = file + 1;
        
        while(sRank >= 0 && sFile < 8 && gameBoard[sRank][sFile] == -1){
        
            steps.add(new Pair(sRank, sFile));
            --sRank;            
            ++sFile;
        }
        
        if(sRank >= 0 && sFile < 8 
                && (gameBoard[sRank][sFile] >= lowerBound 
                && gameBoard[sRank][sFile] < upperBound)){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        // left
        sRank = rank - 1;
        sFile = file;
        
        while(sRank >= 0 && gameBoard[sRank][sFile] == -1){
        
            steps.add(new Pair(sRank, sFile));
            --sRank;
        }
        
        if(sRank >= 0 
                && (gameBoard[sRank][sFile] >= lowerBound 
                && gameBoard[sRank][sFile] < upperBound)){
        
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
                && (gameBoard[sRank][sFile] >= lowerBound 
                && gameBoard[sRank][sFile] < upperBound)){
        
            steps.add(new Pair(sRank, sFile));
        }
        
        // down
        sRank = rank;
        sFile = file - 1;
        
        while(sFile >= 0 && gameBoard[sRank][sFile] == -1){
        
            steps.add(new Pair(sRank, sFile));
            --sFile;
        }
        
        if(sFile >= 0 
                && (gameBoard[sRank][sFile] >= lowerBound 
                && gameBoard[sRank][sFile] < upperBound)){
        
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
                && (gameBoard[sRank][sFile] >= lowerBound 
                && gameBoard[sRank][sFile] < upperBound)){
        
            steps.add(new Pair(sRank, sFile));
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
