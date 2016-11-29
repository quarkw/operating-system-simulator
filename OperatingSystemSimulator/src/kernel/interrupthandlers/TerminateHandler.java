/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernel.interrupthandlers;

import kernel.Kernel;
import kernel.ProcessControlBlock;

/**
 *
 * @author pjhud
 */
public class TerminateHandler {
    private final Kernel kernel;
    
    public TerminateHandler(Kernel kernel) {
        this.kernel = kernel;
    }
    
    //Context switch from oldPCB to newPCB
    public void handleInterrupt() {
        ProcessControlBlock nextInLine = kernel.stScheduler.getNextPcb();
        if (nextInLine == null) {
            nextInLine = kernel.stScheduler.getWaitingQueue().poll();
            if (nextInLine == null) {
                if (kernel.allProcesses.size() == 1) {
                    kernel.allProcesses.remove(kernel.cpu.runningPcbPointer);
                    kernel.cpu.runningPcbPointer = null;
                    System.out.println("All processes terminated.");
                } else {
                    System.out.println("Fatal error on process termination.");
                    kernel.BSOD();
                }
            } else {
                ProcessControlBlock oldPCB = kernel.contextSwitchHandler.switchContext(nextInLine);
                kernel.allProcesses.remove(oldPCB);
                kernel.ioWaitingHandler.busyWait();
            }
        } else {
            ProcessControlBlock oldPCB = kernel.contextSwitchHandler.switchContext(nextInLine);
            kernel.allProcesses.remove(oldPCB);
        }
           
    }
}
