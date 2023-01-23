package com.mycompany.chessmotor.piecetypes;

import com.mycompany.chessmotor.Pair;
import java.util.ArrayList;

public class King extends GenPiece{
    
    public King(){
    
        super();
    }
    
    public King(double value){
    
        super(value);
    }
    
    @Override
    public ArrayList<Pair> generateSteps(int gameBoard[][]){
    
        ArrayList<Pair> steps = new ArrayList<Pair>();

        // todo
        
        return steps;
    }
    
    public ArrayList<Pair> testForCollisions(int gameBoard[][]){
    
        ArrayList<Pair> collisions = new ArrayList<Pair>();
        
        // todo
        
        return collisions;
    }
}
