package chessmotor.enginecontroller;

import chessmotor.enginecontroller.piecetypes.GenPiece;

public class PieceContainer {

    private GenPiece pieces[];

    public PieceContainer(){
        
        pieces = new GenPiece[32];
    }
    
    public void set(int i, GenPiece piece){
    
        pieces[i] = piece;
    }
    
    public GenPiece get(int i){
    
        return pieces[i];
    }
}
