package chessmotor.enginecontroller.piecetypes;

import chessmotor.enginecontroller.Pair;
import java.util.ArrayList;

public class Pawn extends GenPiece{
    
    public Pawn(){
    
        super();
    }
    
    public Pawn(boolean isWhite, double value, int rank, int file){
    
        super(isWhite ? "whitepawn" : "blackpawn", value, rank, file);
    }
    
    @Override
    public ArrayList<Pair> generateSteps(int gameBoard[][]){
    
        ArrayList<Pair> steps = new ArrayList<Pair>();

        // pawn steps are symmetric comparing to human pawn steps
        
        if(gameBoard[rank][file] < 16){
        
            // machine pawn
            
            // from initial position in order to include double jump
            if(file == 1
                    && gameBoard[rank][file + 1] == -1 
                    && (gameBoard[rank][file + 2] == -1 
                    || gameBoard[rank][file + 2] >= 16
                    && gameBoard[rank][file + 2] < 32)){
            
                steps.add(new Pair(rank, file + 2));
            }
            
            // forward
            if(file + 1 < 8 && (gameBoard[rank][file + 1] == -1 
                    || gameBoard[rank][file + 1] >= 16 
                    && gameBoard[rank][file + 1] < 32)){

                steps.add(new Pair(rank, file + 1));
            }

            // forward left
            if(rank - 1 >= 0 && file + 1 < 8
                    && (gameBoard[rank - 1][file + 1] >= 16
                    && gameBoard[rank - 1][file + 1] < 32)){

                steps.add(new Pair(rank - 1, file + 1));
            }

            // forward right
            if(rank + 1 < 8 && file + 1 < 8
                    && (gameBoard[rank + 1][file + 1] >= 16
                    && gameBoard[rank + 1][file + 1] < 32)){

                steps.add(new Pair(rank + 1, file + 1));
            }
        }
        else{
        
            // human pawn
            
            // from initial position in order to include double jump
            if(file == 6 
                    && gameBoard[rank][file - 1] == -1
                    && (gameBoard[rank][file - 2] == -1 
                    || gameBoard[rank][file - 2] >= 0 
                    && gameBoard[rank][file - 2] < 16)){
            
                steps.add(new Pair(rank, file - 2));
            }
            
            // forward
            if(file - 1 >= 0 && gameBoard[rank][file - 1] == -1
                    || (gameBoard[rank][file - 1] >= 0 
                    && gameBoard[rank][file - 1] < 16)){
            
                steps.add(new Pair(rank, file - 1));
            }
            
            // forward left
            if(rank - 1 >= 0 && file - 1 >= 0
                    && (gameBoard[rank - 1][file - 1] >= 0 
                    && gameBoard[rank - 1][file - 1] < 16)){
            
                steps.add(new Pair(rank - 1, file - 1));
            }
            
            // forward right
            if(rank + 1 < 8 && file - 1 >= 0
                    && (gameBoard[rank + 1][file - 1] >= 0 
                    && gameBoard[rank + 1][file - 1] < 16)){
            
                steps.add(new Pair(rank + 1, file - 1));
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
