package chessmotor.enginecontroller;

public interface StatusSavable {

    public GenericSaveStatus getStatus() throws Exception;
    
    public void setStatus(GenericSaveStatus savedStatus) throws Exception;
}
