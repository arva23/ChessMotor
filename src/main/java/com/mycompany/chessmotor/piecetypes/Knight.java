package com.mycompany.chessmotor.piecetypes;

import com.mycompany.chessmotor.Pair;
import java.util.ArrayList;

public class Knight extends GenPiece{
    
    public Knight(){
    
        super();
    }
    
    public Knight(double value, int file, int rank){
    
        super(value, file, rank);
    }
    
    @Override
    public ArrayList<Pair> generateSteps(int gameBoard[][]){
    
        ArrayList<Pair> steps = new ArrayList<Pair>();

        // todo
        
        return steps;
    }
    
    @Override
    public ArrayList<Pair> testForCollisions(int gameBoard[][]){
    
        ArrayList<Pair> collisions = new ArrayList<Pair>();
        
        // todo
        
        return collisions;
    }
}
