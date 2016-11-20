package kernel;

import simulator.CPU;
import simulator.Operation;
import utilities.Assembler;
import java.util.ArrayList;

public class SystemCalls {
    
    public final LongTermScheduler scheduler;
    private final CPU cpu;
    
    private int nextPid = 0;
    
    public SystemCalls(LongTermScheduler scheduler, CPU cpu) {
        this.scheduler = scheduler;
        this.cpu = cpu;
    }
    
    public void loadProgram(String programName, String programText) {
        ArrayList<Operation> program = Assembler.assembleProgram(programText);
        int memoryRequirement = Assembler.memoryRequirement(programText);
        ProcessControlBlock pcb = 
                new ProcessControlBlock(nextPid++, programName, program, memoryRequirement);
        scheduler.insertNewPcb(pcb);
    }
    
    public String processSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Running Process:\n");
        String line = pcbSummary(cpu.runningPcbPointer);
        summary.append(line);
        summary.append("Ready Processes:\n");
        for (ProcessControlBlock pcb : scheduler.shortTermScheduler.getReadyQueue()) {
            summary.append(pcbSummary(pcb));
        }
        summary.append("Standby Processes:\n");
        for (ProcessControlBlock pcb : scheduler.getStandByQueue()) {
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
