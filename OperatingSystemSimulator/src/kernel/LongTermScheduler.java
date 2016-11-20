/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernel;

import java.util.LinkedList;
import simulator.CPU;

public class LongTermScheduler {
    
    private static final int MEMORY_FOR_USER_PROCS = 150;
    
    public final ShortTermScheduler shortTermScheduler = new ShortTermScheduler(this);
    
    private final LinkedList<ProcessControlBlock> standByQueue = new LinkedList<>();
    private final LinkedList<ProcessControlBlock> newProcessQueue = new LinkedList<>();
    private final CPU cpu;
    
    public LongTermScheduler(CPU cpu) {
        this.cpu = cpu;
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
            newProcess.state = ProcessState.READY;
            standByQueue.add(newProcess);
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
        for (ProcessControlBlock pcb : shortTermScheduler.getReadyQueue()) {
            usage += pcb.memoryAllocation;
        }
        return usage;
    }

    private void swapInIfAble() {
        ProcessControlBlock candidate = standByQueue.peek();
        while (candidate != null 
               && candidate.memoryAllocation + getMemoryUsage() <= cpu.memory ) {
            standByQueue.remove(candidate);
            shortTermScheduler.insertReadyPCB(candidate);
            candidate = standByQueue.peek();
        }
    }
    
    private void forceSwapIn(ProcessControlBlock pcb) {
        while (pcb.memoryAllocation + getMemoryUsage() > cpu.memory ) {
            swapOut(shortTermScheduler.getNextPcb());
        }
        shortTermScheduler.insertReadyPCB(pcb);
    }
    
    private void swapOut(ProcessControlBlock pcb) {
        standByQueue.add(pcb);
    }
    
    
    public LinkedList<ProcessControlBlock> getStandByQueue() {
        return standByQueue;
    }
    
}
