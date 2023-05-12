package chessmotor.enginecontroller;

import genmath.genmathexceptions.ValueOutOfRangeException;

public class Step{

    private String stepType;
    private int pieceId;
    private int rank;
    private int file;
    private double value;
    // counting changes in tendency for certain number of steps
    private int cumulativeChangeCount;
    private double cumulativeValue;

    /**
     * Default constructor for Step
     */
    public Step(){

        pieceId = -1;
        rank = -1;
        file = -1;
    }

    /**
     * Parameterized constructor for initialization of operation
     * @param stepType Type of step: standard, hit, castling, promotion
     * @param pieceId The identifier of involved piece
     * @param rank Position rank of step
     * @param file Position file of step
     * @param value Step strength in floating point precision for general purpose 
     *        usage
     * @param cumulativeChangeCount Number of decrease in value
     *        (for negative tendency count)
     * @param cumulativeValue Accumulated value of step chain starting from the 
     *        first until the actual step
     * @throws Exception
     *         Rank range violation
     *         File range violation
     */
    public Step(String stepType, int pieceId, int rank, int file, double value, 
            int cumulativeChangeCount, double cumulativeValue) throws Exception{

        this.stepType = stepType;
        
        this.pieceId = pieceId;

        if(rank < 0 || 7 < rank)
            throw new ValueOutOfRangeException("Rank is out of range.");

        if(file < 0 && 7 < file)
            throw new ValueOutOfRangeException("File is out of range.");

        this.rank = rank;
        
        this.file = file;
        
        this.cumulativeChangeCount = cumulativeChangeCount;

        this.cumulativeValue = cumulativeValue;
    }
    
    public String getStepType(){
    
        return stepType;
    }

    public int getPieceId(){

        return pieceId;
    }

    public int getRank(){

        return rank;
    }

    public int getFile(){

        return file;
    }

    public double getValue(){

        return value;
    }
    
    public void setPieceId(int pieceId){
    
        this.pieceId = pieceId;
    }

    public int getCumulativeChangeCount(){

        return cumulativeChangeCount;
    }

    public double getCumulativeValue(){

        return cumulativeValue;
    }
}