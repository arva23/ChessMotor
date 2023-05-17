package chessmotor.enginecontroller;

import genmath.genmathexceptions.ValueOutOfRangeException;

public class DualStep extends Step{
    
    private int rankB;
    private int fileB;
    private int pieceIdB;
    
    /**
     * Default constructor for DualStep
     */
    public DualStep(){
    
        super();
        rankB = -1;
        fileB = -1;
        pieceIdB = -1;
    }
    
    /**
     * This provides an implementation of dual step which is at present in case 
     * of castling and promotion
     * @param stepType Type of step: standard, hit, castling, promotion
     * @param pieceIdA First piece identifier
     * @param pieceIdB Second piece identifier
     * @param rankA Rank of first step position
     * @param fileA File of first step position
     * @param rankB Rank of second step position
     * @param fileB File of second step position
     * @param value Value of dual step which indicates the its strength
     * @param cumulativeChangeCount Number of consequent negative change in value
     * @param cumulativeValue Accumulated value of step staring from first step
     *        until actual one
     * @throws Exception 
     *         Exception of super class
     *         Range violation of second position rank
     *         Range violation of second position file
     */
    public DualStep(String stepType, int pieceIdA, int pieceIdB, int rankA, 
            int fileA, int rankB, int fileB, double value, int cumulativeChangeCount, 
            double cumulativeValue) throws Exception{
    
        super(stepType, pieceIdA, rankA, fileA, value, cumulativeChangeCount,
                cumulativeValue);
        
        this.pieceIdB = pieceIdB;
        
        if(rankB < 0 || rankB > 7)
            throw new ValueOutOfRangeException("Rank B is out of range.");
        
        if(fileB < 0 || fileB > 7)
            throw new ValueOutOfRangeException("File B is out of range.");
        
        this.rankB = rankB;
        
        this.fileB = fileB;
    }
    
    /**
     * It returns the second piece identifier of dual step
     * @return Returns the requested value
     */
    public int getSecondPieceId(){
    
        return pieceIdB;
    }
    
    /**
     * It returns the rank position of second piece of dual step
     * @return Returns the requested value
     */
    public int getSecondRank(){
    
        return rankB;
    }
    
    /**
     * It returns the file position of second piece of dual step
     * @return Returns the requested value
     */
    public int getSecondFile(){
    
        return fileB;
    }
    
    /**
     * Compares a step with current step object by certain values (see method body)
     * @param step External step to be compared
     * @return Returns the result of comparison (whether they are equal or not)
     */
    @Override
    public boolean equals(Step step){
        
        // needs to be optimized
        
        if(!(step instanceof DualStep)){
            
            return false;
        }
        else{
        
            DualStep dStep = (DualStep)step;
            
            return super.equals(step) 
                && pieceIdB == dStep.pieceIdB
                && rankB == dStep.rankB
                && fileB == dStep.fileB;
        }
    }
}
