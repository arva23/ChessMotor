package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.GameBoardData;
import chessmotor.enginecontroller.Pair;
import genmath.genmathexceptions.ValueOutOfRangeException;
import java.util.ArrayList;

public class Queen extends GenPiece{
    
    public Queen(){
    
        super();
    }
    
    public Queen(int pieceId, boolean isWhite, double value, int rank, int file)
            throws Exception{
    
        super(pieceId, isWhite ? "whitequeen" : "blackquen", value, rank, file);
    }

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
