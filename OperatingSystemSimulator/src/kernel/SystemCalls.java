package kernel;

import simulator.CPU;
import simulator.Operation;
import utilities.Assembler;
import java.util.ArrayList;

public class SystemCalls {
    
    private final Scheduler scheduler;
    private final CPU cpu;
    
    public SystemCalls(Scheduler scheduler, CPU cpu) {
        this.scheduler = scheduler;
        this.cpu = cpu;
    }
    
    public void loadProgram(String processID, String programText) {
        ArrayList<Operation> program = Assembler.assembleProgram(programText);
        int memoryRequirement = Assembler.memoryRequirement(programText);
        ProcessControlBlock pcb = new ProcessControlBlock(processID, program, memoryRequirement);
        pcb.state = ProcessState.READY; //TODO remove this once new process queue implemented
        scheduler.insertPCB(pcb);
    }
    
    public String processSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Running Process:\n");
        String line = String.format("  Process: %s, Program Counter: %d, Cpu Used: %d\n", 
                cpu.runningPcbPointer.processID,
                cpu.runningPcbPointer.programCounter,
                cpu.runningPcbPointer.cpuUsed);
        summary.append(line);
        summary.append("Ready Processes:\n");
        for (ProcessControlBlock pcb : scheduler.getReadyQueue()) {
            line = String.format("  Process: %s, Program Counter: %d, Cpu Used: %d\n", 
                pcb.processID,
                pcb.programCounter,
                pcb.cpuUsed);
            summary.append(line);
        }
        return summary.toString();
    }
    
}
