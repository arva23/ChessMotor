package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.Pair;
import java.util.ArrayList;

public class King extends GenPiece{
    
    public King(){
    
        super();
    }
    
    public King(double value, int file, int rank){
    
        super(value, file, rank);
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
        if(rank + 1 < 8 
                && (gameBoard[file][rank + 1] == -1 
                || gameBoard[file][rank + 1] >= lowerBound 
                && gameBoard[file][rank + 1] < upperBound)){
        
            steps.add(new Pair(file, rank + 1));
        }
        
        // up right
        if(file + 1 < 8 && rank + 1 < 8 
                && (gameBoard[file + 1][rank + 1] == -1 
                || gameBoard[file + 1][rank + 1] >= lowerBound 
                && gameBoard[file + 1][rank + 1] < upperBound)){
        
            steps.add(new Pair(file + 1, rank + 1));
        }
        
        // up
        if(file + 1 < 8 
                && (gameBoard[file + 1][rank] == -1 
                || gameBoard[file + 1][rank] >= lowerBound 
                && gameBoard[file + 1][rank] < upperBound)){
        
            steps.add(new Pair(file + 1, rank));
        }
        
        // up left
        if(file + 1 < 8 && rank - 1 >= 0 
                && (gameBoard[file + 1][rank - 1] == -1 
                || gameBoard[file + 1][rank - 1] >= lowerBound 
                && gameBoard[file + 1][rank - 1] < lowerBound)){
        
            steps.add(new Pair(file + 1, rank - 1));
        }
        
        // left
        if(rank - 1 >= 0 
                && (gameBoard[file][rank - 1] == -1 
                || gameBoard[file][rank - 1] >= lowerBound 
                && gameBoard[file][rank - 1] < upperBound)){
        
            steps.add(new Pair(file, rank - 1));
        }
        
        // down left
        if(file - 1 >= 0 && rank - 1 >= 0 
                && (gameBoard[file - 1][rank - 1] == -1 
                || gameBoard[file - 1][rank - 1] >= lowerBound 
                && gameBoard[file - 1][rank - 1] < upperBound)){
        
            steps.add(new Pair(file - 1, rank - 1));
        }
        
        // down
        if(file - 1 >= 0 
                && (gameBoard[file - 1][rank] == -1 
                || gameBoard[file - 1][rank] >= lowerBound 
                && gameBoard[file - 1][rank] < upperBound)){
        
            steps.add(new Pair(file - 1, rank));
        }
        
        // down right
        if(file - 1 >= 0 && rank + 1 < 8 
                && (gameBoard[file - 1][rank + 1] == -1 
                || gameBoard[file - 1][rank + 1] >= lowerBound 
                && gameBoard[file - 1][rank + 1] < upperBound)){
        
            steps.add(new Pair(file - 1, rank + 1));
        }
        
        return steps;
    }
    
    public ArrayList<Pair> testForCollisions(int gameBoard[][]){
    
        ArrayList<Pair> collisions = new ArrayList<Pair>();
        
        // todo
        
        return collisions;
    }
}
