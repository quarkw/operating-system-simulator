package kernel;

import java.util.LinkedList;

public class ShortTermScheduler {
    public static final int LT_SCHEDULE_INTERVAL = 10;
    public static final int ST_SCHEDULE_INTERVAL = 5;
    
    private LongTermScheduler longTermScheduler;
    private LinkedList<ProcessControlBlock> readyQueue = new LinkedList<>();
    private int longTermScheduleTimer = 0;
    
    public ShortTermScheduler(LongTermScheduler longTermScheduler) {
        this.longTermScheduler = longTermScheduler;
    }
    
    
    public void insertReadyPCB(ProcessControlBlock pcb) {
        readyQueue.add(pcb);
    }
    
    public int getTimeLimit(int processID) {
        return ST_SCHEDULE_INTERVAL;
    }
    
    public ProcessControlBlock getNextPcb() {
        if (longTermScheduleTimer == 0) {
            longTermScheduler.schedule();
            longTermScheduleTimer = LT_SCHEDULE_INTERVAL;
        } else {
            longTermScheduleTimer--;
        }
        return readyQueue.remove();
    }
    
    public LinkedList<ProcessControlBlock> getReadyQueue() {
        return readyQueue;
    }
}
