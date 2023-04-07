package chessmotor.enginecontroller;

// position pair type for squares
public class Pair {

    public int file;
    public int rank;
    
    public Pair(){
    
        file = 0;
        rank = 0;
    }
    
    public Pair(int file, int rank){
    
        this.file = file;
        this.rank = rank;
    }
}
