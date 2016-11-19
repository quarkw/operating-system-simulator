package kernel;

import java.util.LinkedList;

public class Scheduler {
    
    private LinkedList<ProcessControlBlock> readyQueue = new LinkedList<>();
    
    
    public void insertPCB(ProcessControlBlock pcb) {
        readyQueue.add(pcb);
    }
    
    public int getTimeLimit(String processID) {
        return 5;
    }
    
    public ProcessControlBlock getNextPcb() {
        return readyQueue.remove();
    }
    
    public LinkedList<ProcessControlBlock> getReadyQueue() {
        return readyQueue;
    }
}
