/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernel.interrupthandlers;

import kernel.Kernel;
import kernel.ProcessControlBlock;
import kernel.ProcessState;

/**
 *
 * @author pjhud
 */
public class IOWaitingHandler {
    
    private final Kernel kernel;
    
    private boolean busyWaiting;
    
    public IOWaitingHandler(Kernel kernel) {
        this.kernel = kernel;
    }
    
    //Context switch from oldPCB to newPCB
    public void handleBlocking() {
        kernel.cpu.runningPcbPointer.state = ProcessState.WAIT_IO;
        ProcessControlBlock nextInLine = kernel.stScheduler.getNextPcb();
        if (nextInLine != null) {
            busyWaiting = false;
            ProcessControlBlock oldPCB = kernel.contextSwitchHandler.switchContext(nextInLine);
            if (!kernel.stScheduler.getWaitingQueue().isEmpty()) {
                kernel.BSOD();
            }
            kernel.stScheduler.getWaitingQueue().add(oldPCB);
        } else {
            busyWait();
        }
    }
    
    public void busyWait() {
        if (kernel.cpu.ioDevice.isInUse()) {
            busyWaiting = true;
            kernel.cpu.interruptTimer = kernel.stScheduler.getTimeLimit(0);
            kernel.cpu.blocked = true;
        } else {
            kernel.BSOD();
        }
    }
    
    public void signalIOCompletion() {
        if (busyWaiting) {
            kernel.cpu.blocked = false;
            kernel.cpu.runningPcbPointer.state = ProcessState.RUNNING;
        } else {
            ProcessControlBlock waitingProcess = kernel.stScheduler.getWaitingQueue().remove();
            ProcessControlBlock oldPCB = kernel.contextSwitchHandler.switchContext(waitingProcess);
            kernel.stScheduler.insertPCB(oldPCB);
        }
    }
    
}
