package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.Pair;
import java.util.ArrayList;

public class Pawn extends GenPiece{
    
    public Pawn(){
    
        super();
    }
    
    public Pawn(double value, int file, int rank){
    
        super(value, file, rank);
    }
    
    @Override
    public ArrayList<Pair> generateSteps(int gameBoard[][]){
    
        ArrayList<Pair> steps = new ArrayList<Pair>();

        // pawn steps are symmetric comparing to opponent pawn steps
        
        if(gameBoard[file][rank] < 16){
        
            // ally pawn
            
            // from initial position in order to include double jump
            if(file == 1
                    && gameBoard[file + 1][rank] == -1 
                    && (gameBoard[file + 2][rank] == -1 
                    || gameBoard[file + 2][rank] >= 16
                    && gameBoard[file + 2][rank] < 32)){
            
                steps.add(new Pair(file + 2, rank));
            }
            
            // forward
            if(file + 1 < 8 && (gameBoard[file + 1][rank] == -1 
                    || gameBoard[file + 1][rank] >= 16 
                    && gameBoard[file + 1][rank] < 32)){

                steps.add(new Pair(file + 1, rank));
            }

            // forward left
            if(file + 1 < 8 && rank - 1 >= 0 
                    && (gameBoard[file + 1][rank - 1] >= 16
                    && gameBoard[file + 1][rank - 1] < 32)){

                steps.add(new Pair(file + 1, rank - 1));
            }

            // forward right
            if(file + 1 < 8 && rank + 1 < 8 
                    && (gameBoard[file + 1][rank + 1] >= 16
                    && gameBoard[file + 1][rank + 1] < 32)){

                steps.add(new Pair(file + 1, rank + 1));
            }
        }
        else{
        
            // opponent pawn
            
            // from initial position in order to include double jump
            if(file == 6 
                    && gameBoard[file - 1][rank] == -1
                    && (gameBoard[file - 2][rank] == -1 
                    || gameBoard[file - 2][rank] >= 0 
                    && gameBoard[file - 2][rank] < 16)){
            
                steps.add(new Pair(file - 2, rank));
            }
            
            // forward
            if(file - 1 >= 0 && gameBoard[file - 1][rank] == -1
                    || (gameBoard[file - 1][rank] >= 0 
                    && gameBoard[file - 1][rank] < 16)){
            
                steps.add(new Pair(file - 1, rank));
            }
            
            // forward left
            if(file - 1 >= 0 && rank - 1 >= 0 
                    && (gameBoard[file - 1][rank - 1] >= 0 
                    && gameBoard[file - 1][rank - 1] < 16)){
            
                steps.add(new Pair(file - 1, rank - 1));
            }
            
            // forward right
            if(file - 1 >= 0 && rank + 1 < 8
                    && (gameBoard[file - 1][rank + 1] >= 0 
                    && gameBoard[file - 1][rank + 1] < 16)){
            
                steps.add(new Pair(file - 1, rank + 1));
            }
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
