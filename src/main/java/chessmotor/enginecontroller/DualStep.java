package chessmotor.enginecontroller;

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
            throw new Exception("Rank B is out of range.");
        
        if(fileB < 0 || fileB > 7)
            throw new Exception("File B is out of range.");
        
        this.rankB = rankB;
        
        this.fileB = fileB;
    }
    
    public int getSecondPieceId(){
    
        return pieceIdB;
    }
    
    public int getSecondRank(){
    
        return rankB;
    }
    
    public int getSecondFile(){
    
        return fileB;
    }
    
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
