package chessmotor.enginecontroller;

public class Step{

    private String stepType;
    private int pieceId;
    private int rank;
    private int file;
    private double value;
    // counting changes in tendency for certain number of steps
    private int cumulativeChangeCount;
    private double cumulativeValue;

    public Step(){

        pieceId = -1;
        rank = -1;
        file = -1;
    }

    public Step(String stepType, int pieceId, int rank, int file, double value, 
            int cumulativeChangeCount, double cumulativeValue) throws Exception{

        this.stepType = stepType;
        
        this.pieceId = pieceId;

        if(rank < 0 || 7 < rank)
            throw new Exception("Rank is out of range.");

        if(file < 0 && 7 < file)
            throw new Exception("File is out of range.");

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