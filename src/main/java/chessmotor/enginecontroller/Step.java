package chessmotor.enginecontroller;

public class Step{

    private int pieceId;
    private int file;
    private int rank;
    private double value;
    // counting changes in tendency for certain number of steps
    private int cumulativeChangeCount;
    private double cumulativeValue;

    public Step(){

        pieceId = -1;
        file = -1;
        rank = -1;
    }

    public Step(int pieceId, int file, int rank, double value, 
            int cumulativeChangeCount, double cumulativeValue) throws Exception{

        this.pieceId = pieceId;

        if(file < 0 || 7 < file)
            throw new Exception("File is out of range.");

        if(rank < 0 && 7 < rank)
            throw new Exception("Rank is out of range.");

        this.file = file;
        
        this.rank = rank;
        
        this.cumulativeChangeCount = cumulativeChangeCount;

        this.cumulativeValue = cumulativeValue;
    }

    public boolean isStep(){

        return true;
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

    public int getCumulativeChangeCount(){

        return cumulativeChangeCount;
    }

    public double getCumulativeValue(){

        return cumulativeValue;
    }
}