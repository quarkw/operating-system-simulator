package utilities;

import kernel.InterruptHandler;
import kernel.Scheduler;
import kernel.SystemCalls;
import simulator.CPU;

public class BootLoader {
    public static SystemCalls boot(CPU cpu) {
        Scheduler scheduler = new Scheduler();
        cpu.interruptProcessor.interruptHandler = new InterruptHandler(cpu, scheduler);
        
        SystemCalls system = new SystemCalls(scheduler, cpu);
        
        String backgroundProgram = "20 \n" + "CALCULATE -1";
        system.loadProgram("backgroundProcess", backgroundProgram);
        cpu.runningPcbPointer = scheduler.getNextPcb();
        cpu.programCounter = 0;
        
        return system;
    }
}
