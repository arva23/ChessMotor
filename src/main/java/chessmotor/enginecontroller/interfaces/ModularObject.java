package chessmotor.enginecontroller.interfaces;

import java.util.ArrayList;

public interface ModularObject {
    
    public void unite(ModularObject chunk) throws Exception;
    
    public ArrayList<ModularObject> split(int numOfChunks) throws Exception;
    
    public ArrayList<ModularObject> split(ArrayList<Double> chunkRatios) throws Exception;
}
