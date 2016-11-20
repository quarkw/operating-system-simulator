package kernel;

import java.util.LinkedList;

public class ShortTermScheduler {
    public static final int LT_SCHEDULE_INTERVAL = 10;
    public static final int ST_SCHEDULE_INTERVAL = 5;
    
    
    private final Kernel kernel;
    private final LinkedList<ProcessControlBlock> readyQueue = new LinkedList<>();
    
    private final LinkedList<ProcessControlBlock>[] deviceQueues = new LinkedList[Kernel.NUM_IO_DEVICES];
    
    
    private int longTermScheduleTimer = 0;
    
    
    
    public ShortTermScheduler(Kernel kernel) {
        this.kernel = kernel;
        for (int i = 0; i < Kernel.NUM_IO_DEVICES; i++) {
            deviceQueues[i] = new LinkedList<>();
        }
    }
    
    
    public void insertPCB(ProcessControlBlock pcb) {
        if (pcb.state == ProcessState.READY) {
           readyQueue.add(pcb);
        } else if (pcb.state == ProcessState.WAITING) {
            deviceQueues[0].add(pcb); //TODO check which device it is waiting for
        } else {
            System.out.println("Error invalid state"); //TODO
        }
    }
    
    public int getTimeLimit(int processID) {
        return ST_SCHEDULE_INTERVAL;
    }
    
    public ProcessControlBlock getNextPcb() {
        if (longTermScheduleTimer == 0 || readyQueue.isEmpty()) {
            kernel.ltScheduler.schedule();
            longTermScheduleTimer = LT_SCHEDULE_INTERVAL;
        } else {
            longTermScheduleTimer--;
        }
 
        return readyQueue.poll();
    }
    
    public LinkedList<ProcessControlBlock> getReadyQueue() {
        return readyQueue;
    }
    
    public LinkedList<ProcessControlBlock> getDeviceQueue(int deviceNumber){
        return deviceQueues[deviceNumber];
    }
    
    
}
