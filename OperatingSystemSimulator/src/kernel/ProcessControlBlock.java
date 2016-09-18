package kernel;

import simulator.Operation;
import java.util.ArrayList;

public class ProcessControlBlock {
    //Just used public fields because this is simulating a methodless block of data
    public final String processID;
    public final int memoryAllocation;
    public final long startTime;
    
    public int cpuUsed;
    public ProcessState state;
    public int programCounter;
    public ArrayList<Operation> program;
    
    public ProcessControlBlock(String processID, ArrayList<Operation> program, 
            int memoryRequirement) {
        this.processID = processID;
        this.memoryAllocation = memoryRequirement;
        this.program = program;
        this.programCounter = 0;
        this.cpuUsed = 0;
        this.startTime = System.currentTimeMillis();
        this.state = ProcessState.NEW;
        
    } 

}
