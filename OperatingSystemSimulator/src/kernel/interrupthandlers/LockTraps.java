/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernel.interrupthandlers;

import kernel.MutexLock;
import kernel.ProcessState;
import kernel.ShortTermScheduler;
import simulator.CPU;
import simulator.InterruptProcessor;

/**
 *
 * @author pjhud
 */
public class LockTraps {
    
    private final ShortTermScheduler stScheduler;
    private final CPU cpu;
    
    public LockTraps(CPU cpu, ShortTermScheduler stScheduler) {
        this.stScheduler = stScheduler;
        this.cpu = cpu;
    }
    
    
    public void aquire() {
        if (!deviceOneLock.aquire()) {
                    System.out.println("Process " + cpu.runningPcbPointer.processID + " failed to aquire resource");
                    cpu.runningPcbPointer.state = ProcessState.WAITING;
                    cpu.interruptProcessor.setFlag(InterruptProcessor.YIELD);
        } else {
                    System.out.println("Process " + cpu.runningPcbPointer.processID + " aquired resource");
                    cpu.operationCounter = 0;
        }
                
    }
    
    public void release() {
        deviceOneLock.release();
        
    }
}