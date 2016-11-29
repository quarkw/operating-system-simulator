package kernel;

import simulator.Operation;
import java.util.ArrayList;

public class ProcessControlBlock {
    public int getProcessID() {
        return processID;
    }

    public String getProgramName() {
        return programName;
    }

    public int getMemoryAllocation() {
        return memoryAllocation;
    }

    public long getCpuUsed() {
        return cpuUsed;
    }

    public void setCpuUsed(int cpuUsed) {
        this.cpuUsed = cpuUsed;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    public int getOperationCounter() {
        return operationCounter;
    }

    public void setOperationCounter(int operationCounter) {
        this.operationCounter = operationCounter;
    }

    public int getRequestedDevice() {
        return requestedDevice;
    }

    public void setRequestedDevice(int requestedDevice) {
        this.requestedDevice = requestedDevice;
    }
    
    public int getPriority() {
        return priority;
    }

    //Just used public fields because this is simulating a methodless block of data
    public final int processID;
    public final String programName;
    public final int memoryAllocation;
    public final long startTime;
    public final int priority;
    public final long maxCycles;
    
    public long cpuUsed;
    public long timeOfLastBurst;
    public ProcessState state;
    public int programCounter;
    public int operationCounter; //Would not exist in a real machine, necessary because of simulator language.
    public ArrayList<Operation> program;
    
    public long effectivePriority;
    
    
    public int requestedDevice = 0;
    
    public ProcessControlBlock(int processID, String programName, ArrayList<Operation> program, 
            int memoryRequirement, int priority, long startTime, long maxCycles) {
        this.processID = processID;
        this.programName = programName;
        this.memoryAllocation = memoryRequirement;
        this.program = program;
        this.programCounter = -1;
        this.operationCounter = 0;
        this.cpuUsed = 0;
        this.timeOfLastBurst = 0;
        this.startTime = startTime;
        this.state = ProcessState.NEW;
        this.priority = priority;
        this.maxCycles = maxCycles;
    } 

    public long getEffectivePriority() {
        return effectivePriority;
    }

    public void setEffectivePriority(long effectivePriority) {
        this.effectivePriority = effectivePriority;
    }

}
