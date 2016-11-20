/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernel.interrupthandlers;

import kernel.Kernel;
import kernel.ProcessControlBlock;
import kernel.ProcessState;
import simulator.CPU;

/**
 *
 * @author pjhud
 */
public class ContextSwitchHandler {
    private final Kernel kernel;
    private final CPU cpu;
    
    public ContextSwitchHandler(Kernel kernel) {
        this.kernel = kernel;
        this.cpu = kernel.cpu;
    }
    
    public void switchContextTo(ProcessControlBlock nextPcb) {
        ProcessControlBlock oldPCB = cpu.runningPcbPointer;
        if (oldPCB.state == ProcessState.RUNNING) {
            oldPCB.state = ProcessState.READY;
        }
        oldPCB.programCounter = cpu.programCounter;
        oldPCB.operationCounter = cpu.operationCounter;
        System.out.println("Interrupted " + oldPCB.processID 
                + " at " + cpu.programCounter + "/" 
                + oldPCB.operationCounter);
        if (oldPCB.state != ProcessState.TERMINATED) {
            kernel.stScheduler.insertPCB(oldPCB);
        }
        
        //if (kernel.stScheduler.getReadyQueue().isEmpty()) {
        //    System.out.println("BSOD: No ready processes");
        //}
        
        //ProcessControlBlock nextPCB = kernel.stScheduler.getNextPcb();
        cpu.runningPcbPointer = nextPcb;
        cpu.programCounter = nextPcb.programCounter;
        cpu.operationCounter = nextPcb.operationCounter;
        cpu.interruptTimer = kernel.stScheduler.getTimeLimit(nextPcb.processID);
    }
}
