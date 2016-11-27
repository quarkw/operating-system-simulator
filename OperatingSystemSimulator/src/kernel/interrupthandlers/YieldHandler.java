package kernel.interrupthandlers;

import kernel.Kernel;
import kernel.ProcessControlBlock;
import kernel.ProcessState;

public class YieldHandler {
    private final Kernel kernel;
    
    public YieldHandler(Kernel kernel) {
        this.kernel = kernel;
    }
    
    //Context switch from oldPCB to newPCB
    public void handleInterrupt() {
        ProcessControlBlock nextInLine = kernel.stScheduler.getNextPcb();
        if (nextInLine != null) {
            //kernel.cpu.runningPcbPointer.state = ProcessState.READY;
            ProcessControlBlock oldPCB = kernel.contextSwitchHandler.switchContext(nextInLine);
            kernel.stScheduler.insertPCB(oldPCB);
        } else {
            kernel.cpu.interruptTimer = kernel.stScheduler.getTimeLimit(kernel.cpu.runningPcbPointer.processID);
        }
    }
}
