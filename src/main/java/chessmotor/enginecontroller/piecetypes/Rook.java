package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.GameBoardData;
import chessmotor.enginecontroller.Pair;
import java.util.ArrayList;

public class Rook extends GenPiece{
    
    public Rook(){
    
        super();
    }
    
    public Rook(int pieceId, boolean isWhite, double value, int rank, int file){
    
        super(pieceId, isWhite ? "whiterook" : "blackrook",  value, rank, file);
    }
    
    @Override
    public ArrayList<Pair> generateSteps(GameBoardData gameBoard){
    
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
        
        if(sRank < 8 
                && (gameBoard.get(sRank, sFile) >= lowerBound 
                && gameBoard.get(sFile, sRank) < upperBound)){
        
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
        
        // left
        sRank = rank - 1;
        sFile = file;

        while(sRank >= 0 && gameBoard.get(sRank, sFile) == -1){
        
            steps.add(new Pair(sRank, sFile));
            --sRank;
        }
        
        if(sRank >= 0 
                && (gameBoard.get(sRank, sFile) >= lowerBound 
                && gameBoard.get(sRank, sRank) < upperBound)){
        
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
        
        return steps;
    }
}
