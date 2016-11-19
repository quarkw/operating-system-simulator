package kernel.interrupthandlers;

import kernel.ProcessControlBlock;
import kernel.ProcessState;
import kernel.Scheduler;
import simulator.CPU;

public class YieldHandler {
    private Scheduler scheduler;
    
    private CPU cpu;
    
    public YieldHandler(CPU cpu, Scheduler scheduler) {
        this.cpu = cpu;
        this.scheduler = scheduler;
    }
    
    //Context switch from oldPCB to newPCB
    public void handleInterrupt() {
        ProcessControlBlock oldPCB = cpu.runningPcbPointer;
        if (oldPCB.state == ProcessState.RUNNING) {
            oldPCB.state = ProcessState.READY;
        }
        oldPCB.programCounter = cpu.programCounter;
        oldPCB.operationCounter = cpu.operationCounter;
        System.out.println("Interrupted " + oldPCB.processID 
                + " at " + cpu.programCounter + "/" 
                + oldPCB.operationCounter);
        if (oldPCB.state == ProcessState.READY) {
            scheduler.insertPCB(oldPCB);
        }
        
        if (scheduler.getReadyQueue().isEmpty()) {
            System.out.println("BSOD: No ready processes");
        }
        
        ProcessControlBlock nextPCB = scheduler.getNextPcb();
        cpu.runningPcbPointer = nextPCB;
        cpu.programCounter = nextPCB.programCounter;
        cpu.operationCounter = nextPCB.operationCounter;
        cpu.interruptTimer = scheduler.getTimeLimit(nextPCB.processID);
    }
}
