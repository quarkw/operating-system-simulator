package kernel;

import java.util.LinkedList;

public class ShortTermScheduler {
    public static final int LT_SCHEDULE_INTERVAL = 10;
    public static final int ST_SCHEDULE_INTERVAL = 5;
    public static final int NUM_IO_DEVICES = 1;
    
    private final LongTermScheduler longTermScheduler;
    private final LinkedList<ProcessControlBlock> readyQueue = new LinkedList<>();
    
    private final MutexLock[] locks = new MutexLock[NUM_IO_DEVICES];
    private final LinkedList<ProcessControlBlock>[] waitingQueues = new LinkedList[NUM_IO_DEVICES];
    
    
    private int longTermScheduleTimer = 0;
    
    
    
    public ShortTermScheduler(LongTermScheduler longTermScheduler) {
        this.longTermScheduler = longTermScheduler;
        for (int i = 0; i < NUM_IO_DEVICES; i++) {
            locks[i] = new MutexLock();
            waitingQueues[i] = new LinkedList<>();
        }
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
    
    public boolean aquireDevice(int n) {
        
    }
    
    public boolean releaseDevice(int n) {
        if (!waitingQueues[n].isEmpty()) {
            insertPCB(waitingQueues[])
        }
    }
}
