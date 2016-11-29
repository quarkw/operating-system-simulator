package kernel;

import java.util.LinkedList;
import java.util.PriorityQueue;

public class ShortTermScheduler {
    public static final int LT_SCHEDULE_INTERVAL = 20;
    public static final int ST_SCHEDULE_INTERVAL = 5;
    
    
    private final Kernel kernel;
    private final PriorityQueue<ProcessControlBlock> readyQueue;
    private final LinkedList<ProcessControlBlock> waitingQueue; 
    
    private final PriorityQueue<ProcessControlBlock>[] deviceQueues = new PriorityQueue[Kernel.NUM_IO_DEVICES];
    
    
    private int longTermScheduleTimer = 0;
    
    
    
    public ShortTermScheduler(Kernel kernel) {
        this.kernel = kernel;
        for (int i = 0; i < Kernel.NUM_IO_DEVICES; i++) {
            deviceQueues[i] = new PriorityQueue(11, new ProcessComparator(kernel));
        }
        this.readyQueue = new PriorityQueue(11, new ProcessComparator(kernel));
        this.waitingQueue = new LinkedList<>();
    }
    
    
    public void insertPCB(ProcessControlBlock pcb) {
        if (pcb.state == ProcessState.READY) {
           readyQueue.add(pcb);
        } else if (pcb.state == ProcessState.WAIT_AQUIRE) {
            deviceQueues[0].add(pcb); //TODO check which device it is waiting for
        } else if (pcb.state == ProcessState.WAIT_IO) {
            waitingQueue.add(pcb); //TODO check which device it is waiting for
        } else {
            kernel.BSOD();
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
    
    public PriorityQueue<ProcessControlBlock> getReadyQueue() {
        return readyQueue;
    }
    
    public PriorityQueue<ProcessControlBlock> getDeviceQueue(int deviceNumber){
        return deviceQueues[deviceNumber];
    }
    
    public LinkedList<ProcessControlBlock> getWaitingQueue() {
        return waitingQueue;
    }
    
}
