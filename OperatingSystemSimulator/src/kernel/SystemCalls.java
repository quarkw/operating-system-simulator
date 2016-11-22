package kernel;

import simulator.CPU;
import simulator.Operation;
import utilities.Assembler;
import java.util.ArrayList;
import java.util.HashMap;

public class SystemCalls {
    

    private final Kernel kernel;
    
    private int nextPid = 0;
    
    public SystemCalls(Kernel kernel) {
        this.kernel = kernel;
    }
    
    public void loadProgram(String programName, String programText) {
        ArrayList<Operation> program = Assembler.assembleProgram(programText);
        int memoryRequirement = Assembler.memoryRequirement(programText);
        ProcessControlBlock pcb = 
                new ProcessControlBlock(nextPid++, programName, program, memoryRequirement);
        kernel.ltScheduler.insertNewPcb(pcb);
        kernel.allProcesses.add(pcb);
    }
    
    public String processSummaryByQueue() {
        StringBuilder summary = new StringBuilder();
        summary.append("Running Process:\n");
        summary.append(String.format(
                "  Pid: %d, Program: %s, PC: %d, Cpu Used: %d\n", 
                kernel.cpu.runningPcbPointer.processID,
                kernel.cpu.runningPcbPointer.programName,
                kernel.cpu.programCounter,
                kernel.cpu.runningPcbPointer.cpuUsed));
        summary.append("Ready Processes:\n");
        for (ProcessControlBlock pcb : kernel.stScheduler.getReadyQueue()) {
            summary.append(pcbSummary(pcb));
        }
        summary.append("Processes Waiting for Device:\n");
        for (ProcessControlBlock pcb : kernel.stScheduler.getDeviceQueue(0)) {
            summary.append(pcbSummary(pcb));
        }
        summary.append("Standby Processes:\n");
        for (ProcessControlBlock pcb : kernel.ltScheduler.getStandByQueue()) {
            summary.append(pcbSummary(pcb));
        }
        return summary.toString();
    }

    public ArrayList<ProcessControlBlock> getPCBs(){
        ArrayList<ProcessControlBlock> PCBs = new ArrayList<>();
        for (ProcessControlBlock pcb : kernel.allProcesses) {
            PCBs.add(pcb);
        }
        return PCBs;
    }
    
    private String pcbSummary(ProcessControlBlock pcb) {
        return String.format(
                "  Pid: %d, Program: %s, PC: %d, Cpu Used: %d\n", 
                pcb.processID,
                pcb.programName,
                pcb.programCounter,
                pcb.cpuUsed);
    }
    
    public String processSummary() {
        StringBuilder summary = new StringBuilder();
        for (ProcessControlBlock pcb : kernel.allProcesses) {
            summary.append(processSummary(pcb));
        }
        
        summary.append(String.format("Memory Usage: %d\n",
                kernel.ltScheduler.getMemoryUsage()));
        return summary.toString();
    }
    
    private String processSummary(ProcessControlBlock pcb) {
         return String.format(
                "%6s, Pid: %d, State: %7s, PC: %d, Cpu Used: %d\n", 
                pcb.programName,
                pcb.processID,
                pcb.state.toString(),
                (pcb.state == ProcessState.RUNNING)
                     ? kernel.cpu.programCounter
                     : pcb.programCounter,
                pcb.cpuUsed);
    }
    
}
