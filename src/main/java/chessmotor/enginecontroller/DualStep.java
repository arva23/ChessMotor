package chessmotor.enginecontroller;

public class DualStep extends Step{
    
    int rankB;
    int fileB;
    int pieceIdB;
    
    public DualStep(){
    
        super();
        rankB = -1;
        fileB = -1;
        pieceIdB = -1;
    }
    
    public DualStep(int pieceIdA, int pieceIdB, int rankA, int fileA, int rankB, 
            int fileB, double value, int cumulativeChangeCount, double cumulativeValue) throws Exception{
    
        super(pieceIdA, rankA, fileA, value, cumulativeChangeCount,
                cumulativeValue);
        
        this.pieceIdB = pieceIdB;
        
        if(rankB < 0 || rankB > 7)
            throw new Exception("Rank B is out of range.");
        
        if(fileB < 0 || fileB > 7)
            throw new Exception("File B is out of range.");
        
        this.rankB = rankB;
        
        this.fileB = fileB;
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
