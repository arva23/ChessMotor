package chessmotor.enginecontroller;

import genmath.genmathexceptions.AmbiguousObjectException;
import genmath.genmathexceptions.NoObjectFoundException;
import java.util.TreeMap;

public abstract class GenericSaveStatus {

    protected TreeMap<String, Object> entries;
    
    public GenericSaveStatus(){
    
        entries = new TreeMap<>();
    }
    
    public Object get(String entryId) throws Exception{
    
        if(!entries.containsKey(entryId)){
            
            throw new NoObjectFoundException("There is no entry with this "
                    + "identifier.");
        }
    
        return entries.get(entryId);
    }
    
    public void set(String entryId, Object object) throws Exception{
    
        if(entries.containsKey(entryId)){
        
            throw new AmbiguousObjectException("An entry has already been "
                    + "existed with this identifier.");
        }
        
        entries.put(entryId, object);
    }
    
    public void clear(){
    
        entries.clear();
    }
}
