package chessmotor.enginecontroller.interfaces;

import chessmotor.enginecontroller.GenericSaveStatus;

public interface StatusSavable {

    public GenericSaveStatus getStatus() throws Exception;
    
    public void setStatus(GenericSaveStatus savedStatus) throws Exception;
}
