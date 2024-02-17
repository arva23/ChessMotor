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

    /**
     * Obtains step type
     * @return Step type
     */
    public String getStepType(){
    
        return stepType;
    }

    /**
     * Obtains piece identifier
     * @return Piece identifier
     */
    public int getPieceId(){

        return pieceId;
    }

    /**
     * Obtains rank of piece
     * @return Rank
     */
    public int getRank(){

        return rank;
    }

    /**
     * Obtains file of piece
     * @return File
     */
    public int getFile(){

        return file;
    }

    /**
     * Obtains value of piece
     * @return Value
     */
    public double getValue(){

        return value;
    }
    
    /**
     * Sets piece identifier
     * @param pieceId new identifier of piece
     */
    public void setPieceId(int pieceId){
    
        this.pieceId = pieceId;
    }

    /**
     * Sets cumulative change counter
     * @return new value of cumulative change count
     */
    public int getCumulativeChangeCount(){

        return cumulativeChangeCount;
    }

    /**
     * Sets cumulative value
     * @return Cumulative value
     */
    public double getCumulativeValue(){

        return cumulativeValue;
    }
}