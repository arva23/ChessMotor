package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.Pair;
import java.util.ArrayList;

public class Knight extends GenPiece{
    
    public Knight(){
    
        super();
    }
    
    public Knight(boolean isWhite, double value, int file, int rank){
    
        super(isWhite ? "whiteknight" : "blackknight", value, file, rank);
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
        
        // up right #1
        if(file + 1 < 8 && rank + 2 < 8 
                && (gameBoard[file + 1][rank + 2] == -1 
                || gameBoard[file + 1][rank + 2] >= lowerBound 
                && gameBoard[file + 1][rank + 2] < upperBound)){
        
            steps.add(new Pair(file + 1, rank + 2));
        }
        
        // up right #2
        if(file + 2 < 8 && rank + 1 < 8 
                && (gameBoard[file + 2][rank + 1] == -1 
                || gameBoard[file + 2][rank + 1] >= lowerBound 
                && gameBoard[file + 2][rank + 1] < upperBound)){
        
            steps.add(new Pair(file + 2, rank + 1));
        }
        
        // up left #1
        if(file + 2 < 8 && rank - 1 >= 0 
                && (gameBoard[file + 2][rank - 1] == -1 
                || gameBoard[file + 2][rank - 1] >= lowerBound 
                && gameBoard[file + 2][rank - 1] < upperBound)){
        
            steps.add(new Pair(file + 2, rank - 1));
        }
        
        // up left #2
        if(file + 1 < 8 && rank - 2 >= 0
                && (gameBoard[file + 1][rank - 2] == -1 
                || gameBoard[file + 1][rank - 2] >= lowerBound 
                && gameBoard[file + 1][rank - 2] < upperBound)){
        
            steps.add(new Pair(file + 1, rank - 2));
        }
        
        // down left #1
        if(file - 1 >= 0 && rank - 2 >= 0 
                && (gameBoard[file - 1][rank - 2] == -1 
                || gameBoard[file - 1][rank - 2] >= lowerBound 
                && gameBoard[file - 1][rank - 2] < upperBound)){
        
            steps.add(new Pair(file - 1, rank - 2));
        }
        
        // down left #2
        if(file - 2 >= 0 && rank - 1 >= 0 
                && (gameBoard[file - 2][rank - 1] == -1 
                || gameBoard[file - 2][rank - 1] >= lowerBound 
                && gameBoard[file - 2][rank - 1] < upperBound)){
        
            steps.add(new Pair(file - 2, rank - 1));
        }

        // down right #1
        if(file - 2 >= 0 && rank + 1 < 8
                && (gameBoard[file - 2][rank + 1] == -1 
                || gameBoard[file - 2][rank + 1] >= lowerBound 
                && gameBoard[file - 2][rank + 1] < upperBound)){
        
            steps.add(new Pair(file - 2, rank + 1));
        }

        // down right #2
        if(file - 1 >= 0 && rank + 2 < 8 
                && (gameBoard[file - 1][rank + 2] == -1 
                || gameBoard[file - 1][rank + 2] >= lowerBound 
                && gameBoard[file - 1][rank + 2] < upperBound)){
        
            steps.add(new Pair(file - 1, rank + 2));
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
