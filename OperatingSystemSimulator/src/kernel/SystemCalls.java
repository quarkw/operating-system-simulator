package kernel;

import simulator.CPU;
import simulator.Operation;
import utilities.Assembler;
import java.util.ArrayList;

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
    }
    
    public String processSummary() {
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
    
    private String pcbSummary(ProcessControlBlock pcb) {
        return String.format(
                "  Pid: %d, Program: %s, PC: %d, Cpu Used: %d\n", 
                pcb.processID,
                pcb.programName,
                pcb.programCounter,
                pcb.cpuUsed);
    }
    
}
