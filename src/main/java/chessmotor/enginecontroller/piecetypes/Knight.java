package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.Pair;
import java.util.ArrayList;

public class Knight extends GenPiece{
    
    public Knight(){
    
        super();
    }
    
    public Knight(int pieceId, boolean isWhite, double value, int rank, int file){
    
        super(pieceId, isWhite ? "whiteknight" : "blackknight", value, rank, file);
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
        
        // up right #1
        if(rank + 2 < 8 && file + 1 < 8
                && (gameBoard[rank + 2][file + 1] == -1 
                || gameBoard[rank + 2][file + 1] >= lowerBound 
                && gameBoard[rank + 2][file + 1] < upperBound)){
        
            steps.add(new Pair(rank + 2, file + 1));
        }
        
        // up right #2
        if(rank + 1 < 8 && file + 2 < 8
                && (gameBoard[rank + 1][file + 2] == -1 
                || gameBoard[rank + 1][file + 2] >= lowerBound 
                && gameBoard[rank + 1][file + 2] < upperBound)){
        
            steps.add(new Pair(rank + 1, file + 2));
        }
        
        // up left #1
        if(rank - 1 >= 0 && file + 2 < 8
                && (gameBoard[rank - 1][file + 2] == -1 
                || gameBoard[rank - 1][file + 2] >= lowerBound 
                && gameBoard[rank - 1][file + 2] < upperBound)){
        
            steps.add(new Pair(rank - 1, file + 2));
        }
        
        // up left #2
        if(rank - 2 >= 0 && file + 1 < 8
                && (gameBoard[rank - 2][file + 1] == -1 
                || gameBoard[rank - 2][file + 1] >= lowerBound 
                && gameBoard[rank - 2][file + 1] < upperBound)){
        
            steps.add(new Pair(rank - 2, file + 1));
        }
        
        // down left #1
        if(rank - 2 >= 0 && file - 1 >= 0
                && (gameBoard[rank - 2][file - 1] == -1 
                || gameBoard[rank - 2][file - 1] >= lowerBound 
                && gameBoard[rank - 2][file - 1] < upperBound)){
        
            steps.add(new Pair(rank - 2, file - 1));
        }
        
        // down left #2
        if(rank - 1 >= 0 && file - 2 >= 0
                && (gameBoard[rank - 1][file - 2] == -1 
                || gameBoard[rank - 1][file - 2] >= lowerBound 
                && gameBoard[rank - 1][file - 2] < upperBound)){
        
            steps.add(new Pair(rank - 1, file - 2));
        }

        // down right #1
        if(rank + 1 < 8 && file - 2 >= 0 
                && (gameBoard[rank + 1][file - 2] == -1 
                || gameBoard[rank + 1][file - 2] >= lowerBound 
                && gameBoard[rank + 1][file - 2] < upperBound)){
        
            steps.add(new Pair(rank + 1, file - 2));
        }

        // down right #2
        if(rank + 2 < 8 && file - 1 >= 0
                && (gameBoard[rank + 2][file - 1] == -1 
                || gameBoard[rank + 2][file - 1] >= lowerBound 
                && gameBoard[rank + 2][file - 1] < upperBound)){
        
            steps.add(new Pair(rank + 2, file - 1));
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
