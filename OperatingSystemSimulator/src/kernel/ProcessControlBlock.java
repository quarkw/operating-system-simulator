package kernel;

import simulator.Operation;
import java.util.ArrayList;

public class ProcessControlBlock {
    //Just used public fields because this is simulating a methodless block of data
    public final int processID;
    public final String programName;
    public final int memoryAllocation;
    public final long startTime;
    
    public int cpuUsed;
    public ProcessState state;
    public int programCounter;
    public int operationCounter; //Would not exist in a real machine, necessary because of simulator language.
    public ArrayList<Operation> program;
    
    public ProcessControlBlock(int processID, String programName, ArrayList<Operation> program, 
            int memoryRequirement) {
        this.processID = processID;
        this.programName = programName;
        this.memoryAllocation = memoryRequirement;
        this.program = program;
        this.programCounter = -1;
        this.operationCounter = 0;
        this.cpuUsed = 0;
        this.startTime = System.currentTimeMillis();
        this.state = ProcessState.NEW;
        
    } 

}
