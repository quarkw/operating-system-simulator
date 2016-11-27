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
    
    public ProcessControlBlock switchContext(ProcessControlBlock nextPcb) {
        ProcessControlBlock oldPCB = cpu.runningPcbPointer;
        if(oldPCB != null) {
            if (oldPCB.state == ProcessState.RUNNING) {
                oldPCB.state = ProcessState.READY;
            }
            oldPCB.programCounter = cpu.programCounter;
            oldPCB.operationCounter = cpu.operationCounter;
        }

        
        nextPcb.state = ProcessState.RUNNING;
        cpu.runningPcbPointer = nextPcb;
        cpu.programCounter = nextPcb.programCounter;
        cpu.operationCounter = nextPcb.operationCounter;
        cpu.interruptTimer = kernel.stScheduler.getTimeLimit(nextPcb.processID);
        
        return oldPCB;
    }
}
