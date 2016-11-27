/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernel.interrupthandlers;

import java.util.LinkedList;
import kernel.Kernel;
import kernel.MutexLock;
import kernel.ProcessControlBlock;
import kernel.ProcessState;
import kernel.ShortTermScheduler;
import simulator.CPU;
import simulator.InterruptProcessor;

/**
 *
 * @author pjhud
 */
public class LockTraps {
    
    
    private final Kernel kernel;
    private final CPU cpu;
    
    private final MutexLock[] locks;
    
    
    public LockTraps(Kernel kernel) {
        this.kernel = kernel;
        this.cpu = kernel.cpu;
        locks = new MutexLock[Kernel.NUM_IO_DEVICES];
        for (int i = 0; i < Kernel.NUM_IO_DEVICES; i++) {
            locks[i] = new MutexLock();
        }
    }
       
    public void releaseDevice(int deviceNumber) {
        
    }
    
    
    public void aquire() {
        ProcessControlBlock runningPcb = cpu.runningPcbPointer;
        if (locks[runningPcb.requestedDevice].aquire()) {
            System.out.println("Process " + runningPcb.processID + " aquired resource");
            cpu.operationCounter = 0;
                    
        } else {
            System.out.println("Process " + cpu.runningPcbPointer.processID + " waiting for resource");
            runningPcb.state = ProcessState.WAIT_FOR_DEVICE;
            //cpu.interruptProcessor.setFlag(InterruptProcessor.YIELD); //TODO perform context switch here       
            
            //yield
            ProcessControlBlock nextInLine = kernel.stScheduler.getNextPcb();
            if (nextInLine == null) {
                nextInLine = kernel.stScheduler.getWaitingQueue().poll();
                if (nextInLine == null) kernel.BSOD();
                ProcessControlBlock oldPCB = kernel.contextSwitchHandler.switchContext(nextInLine);
                kernel.stScheduler.insertPCB(oldPCB);
                kernel.ioWaitingHandler.busyWait();
            } else {
               ProcessControlBlock oldPCB = kernel.contextSwitchHandler.switchContext(nextInLine);
               kernel.stScheduler.insertPCB(oldPCB);
            }
        }
                
    }
    
    public void release() {
        System.out.println("Process " + cpu.runningPcbPointer.processID + " released resource");
        int deviceNumber = cpu.runningPcbPointer.requestedDevice;
        LinkedList<ProcessControlBlock> deviceQueue = 
                kernel.stScheduler.getDeviceQueue(deviceNumber);
        if (!deviceQueue.isEmpty()) {            
            ProcessControlBlock nextInLine = deviceQueue.poll();
            System.out.println("Process " + nextInLine.processID + " aquired resource");
            nextInLine.state = ProcessState.READY;
            kernel.stScheduler.insertPCB(nextInLine);
        }
        locks[deviceNumber].release();
        
    }
}
