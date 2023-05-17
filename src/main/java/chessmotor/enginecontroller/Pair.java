package chessmotor.enginecontroller;

// position pair type for squares

import genmath.genmathexceptions.ValueOutOfRangeException;

public class Pair {

    private int rank;
    private int file;
    
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
    
        
        if(rank < 0){
        
            throw new ValueOutOfRangeException("Value underflow of rank.");
        }
        
        this.rank = rank;

        if(file < 0){
        
            throw new ValueOutOfRangeException("Value underflow of file.");
        }
        
        this.file = file;
        
    }
    
    /**
     * Obtains rank
     * @return Value of rank
     */
    public int getRank(){
    
        return rank;
    }
    
    /**
     * Obtains file
     * @return Value of file
     */
    public int getFile(){
    
        return file;
    }
    
    /**
     * Sets new value for rank
     * @param newRank New value for rank
     * @throws ValueOutOfRangeException 
     */
    public void setRank(int newRank) throws ValueOutOfRangeException{
    
        if(newRank < 0){
        
            throw new ValueOutOfRangeException("Value underflow of rank.");
        }
        
        this.rank = newRank;
    }
    
    /**
     * Sets new value for file
     * @param newFile New value of file
     * @throws ValueOutOfRangeException 
     */
    public void setFile(int newFile) throws ValueOutOfRangeException{
    
        if(newFile < 0){
        
            throw new ValueOutOfRangeException("Value underflow of file.");
        }
        
        this.file = newFile;
    }
}
