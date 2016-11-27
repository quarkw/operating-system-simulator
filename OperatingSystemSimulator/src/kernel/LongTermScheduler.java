/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernel;

import java.util.LinkedList;
import simulator.CPU;
import simulator.Operation;

public class LongTermScheduler {
    
    //private static final int MEMORY_FOR_USER_PROCS = 150;
    
    private final Kernel kernel;
    private final CPU cpu;
    private final ShortTermScheduler stScheduler;
    
    private final LinkedList<ProcessControlBlock> standByQueue = new LinkedList<>();
    private final LinkedList<ProcessControlBlock> newProcessQueue = new LinkedList<>();
    
    
    public LongTermScheduler(Kernel kernel) {
        this.kernel = kernel;
        this.cpu = kernel.cpu;
        this.stScheduler = kernel.stScheduler;
    }
    
    public void insertNewPcb(ProcessControlBlock pcb) {
        newProcessQueue.add(pcb);
    }
    
    
    public void schedule() {
        //System.out.println(
        //        String.format("***SCHEDULE*** (%d/%d/%d",
        //                newProcessQueue.size(),
        //                standByQueue.size(),
        //                shortTermScheduler.getReadyQueue().size()));
        while (!newProcessQueue.isEmpty()) {
            ProcessControlBlock newProcess = newProcessQueue.poll();
            swapOut(newProcess);
        }
        swapInIfAble();
        if (!standByQueue.isEmpty()) {
            forceSwapIn(standByQueue.poll());
        }
    }
    
    public int getMemoryUsage() {
        int usage = 0;
        if (cpu.runningPcbPointer != null) { //TODO: This is a hack to allow booting
            usage += cpu.runningPcbPointer.memoryAllocation;
        }
        for (ProcessControlBlock pcb : stScheduler.getReadyQueue()) {
            usage += pcb.memoryAllocation;
        }
        for (int i = 0; i < Kernel.NUM_IO_DEVICES; i++) {
            for (ProcessControlBlock pcb : stScheduler.getDeviceQueue(i)) {
                usage += pcb.memoryAllocation;
            }
        }
        return usage;
    }

    private void swapInIfAble() {
        ProcessControlBlock candidate = standByQueue.peek();
        while (candidate != null 
               && candidate.memoryAllocation + getMemoryUsage() <= cpu.memory ) {
            standByQueue.remove(candidate);
            swapIn(candidate);
            candidate = standByQueue.peek();
        }
    }
    
    //TODO this needs to be optimized to take out lowest priority processes
    private void forceSwapIn(ProcessControlBlock pcb) { //TODO this whole method is crap
        //while (pcb.memoryAllocation + getMemoryUsage() > cpu.memory 
        //        && !stScheduler.getReadyQueue().isEmpty()) {
        //    swapOut(stScheduler.getReadyQueue().remove());
        //}
        for (ProcessControlBlock victim : stScheduler.getReadyQueue()) {
            if ( victim.programCounter >= 0
               && victim.program.get(victim.programCounter).getType() != Operation.IO) {
                stScheduler.getReadyQueue().remove(victim);
                swapOut(victim);
            }
            
            if (pcb.memoryAllocation + getMemoryUsage() <= cpu.memory) {
                break;
            }
        }
        
        if (pcb.memoryAllocation + getMemoryUsage() > cpu.memory) {
            //System.out.println("***Force Swap in failed****");
            swapOut(pcb);
            return;
        }

        swapIn(pcb);
    }
    
    
    public LinkedList<ProcessControlBlock> getStandByQueue() {
        return standByQueue;
    }
    
    private void swapIn(ProcessControlBlock pcb) {
        pcb.state = ProcessState.READY;
        stScheduler.insertPCB(pcb);
    }
    
    private void swapOut(ProcessControlBlock pcb) {
        pcb.state = ProcessState.STANDBY;
        standByQueue.add(pcb);
    }
    
}
