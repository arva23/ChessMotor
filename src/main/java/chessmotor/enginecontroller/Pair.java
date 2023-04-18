package chessmotor.enginecontroller;

// position pair type for squares
public class Pair {

    public int rank;
    public int file;
    
    public Pair(){
    
        rank = 0;
        file = 0;
    }
    
    public Pair(int rank, int file){
    
        this.rank = rank;
        this.file = file;
    }
}
