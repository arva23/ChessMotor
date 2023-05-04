package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.GameBoardData;
import chessmotor.enginecontroller.Pair;
import java.util.ArrayList;

public class King extends GenPiece{
    
    public King(){
    
        super();
    }
    
    public King(int pieceId, boolean isWhite, double value, int rank, int file){
    
        super(pieceId, isWhite ? "whiteking" : "blackking", value, rank, file);
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
        if(rank + 1 < 8 
                && (gameBoard.get(rank + 1, file) == -1 
                || gameBoard.get(rank + 1, file) >= lowerBound 
                && gameBoard.get(rank + 1, file) < upperBound)){
        
            steps.add(new Pair(rank + 1, file));
        }
        
        // up right
        if(rank + 1 < 8 && file + 1 < 8
                && (gameBoard.get(rank + 1, file + 1) == -1 
                || gameBoard.get(rank + 1, file + 1) >= lowerBound 
                && gameBoard.get(rank + 1, file + 1) < upperBound)){
        
            steps.add(new Pair(rank + 1, file + 1));
        }
        
        // up
        if(file + 1 < 8 
                && (gameBoard.get(rank, file + 1) == -1 
                || gameBoard.get(rank, file + 1) >= lowerBound 
                && gameBoard.get(rank, file + 1) < upperBound)){
        
            steps.add(new Pair(rank, file + 1));
        }
        
        // up left
        if(rank - 1 >= 0 && file + 1 < 8
                && (gameBoard.get(rank - 1, file + 1) == -1 
                || gameBoard.get(rank - 1, file + 1) >= lowerBound 
                && gameBoard.get(rank - 1, file + 1) < lowerBound)){
        
            steps.add(new Pair(rank - 1, file + 1));
        }
        
        // left
        if(rank - 1 >= 0 
                && (gameBoard.get(rank - 1, file) == -1 
                || gameBoard.get(rank - 1, file) >= lowerBound 
                && gameBoard.get(rank - 1, file) < upperBound)){
        
            steps.add(new Pair(rank - 1, file));
        }
        
        // down left
        if(rank - 1 >= 0 && file - 1 >= 0
                && (gameBoard.get(rank - 1, file - 1) == -1 
                || gameBoard.get(rank - 1, file - 1) >= lowerBound 
                && gameBoard.get(rank - 1, file - 1) < upperBound)){
        
            steps.add(new Pair(rank - 1, file - 1));
        }
        
        // down
        if(file - 1 >= 0 
                && (gameBoard.get(rank, file - 1) == -1 
                || gameBoard.get(rank, file - 1) >= lowerBound 
                && gameBoard.get(rank, file - 1) < upperBound)){
        
            steps.add(new Pair(rank, file - 1));
        }
        
        // down right
        if(rank + 1 < 8 && file - 1 >= 0
                && (gameBoard.get(rank + 1, file - 1) == -1 
                || gameBoard.get(rank + 1, file - 1) >= lowerBound 
                && gameBoard.get(rank + 1, file - 1) < upperBound)){
        
            steps.add(new Pair(rank + 1, file - 1));
        }
        
        return steps;
    }
}
