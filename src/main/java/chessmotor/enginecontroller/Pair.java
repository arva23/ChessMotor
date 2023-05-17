package chessmotor.enginecontroller;

// position pair type for squares
public class Pair {

    public int rank;
    public int file;
    
    /**
     * Default constructor of Pair class with initial values of 0
     */
    public Pair(){
    
        rank = 0;
        file = 0;
    }
    
    /**
     * Parameterized constructor that accepts positive or zero values
     * @param rank Value of rank
     * @param file Value of file
     * @throws ValueOutOfRangeException
     */
    public Pair(int rank, int file) throws ValueOutOfRangeException{
    
        this.rank = rank;
        this.file = file;
    }
}
