package utilities;

import kernel.interrupthandlers.*;
import kernel.Scheduler;
import kernel.SystemCalls;
import simulator.CPU;

public class BootLoader {
    public static SystemCalls boot(CPU cpu) {
        Scheduler scheduler = new Scheduler();
        cpu.interruptProcessor.yieldHandler = new YieldHandler(cpu, scheduler);
        cpu.interruptProcessor.lockTraps = new LockTraps(cpu);
        
        
        SystemCalls system = new SystemCalls(scheduler, cpu);
        
        String backgroundProgram = "20 \n" + "CALCULATE -1";
        system.loadProgram("backgroundProcess", backgroundProgram);
        cpu.runningPcbPointer = scheduler.getNextPcb();
        cpu.programCounter = -1;
        cpu.operationCounter = 0; 
        cpu.system = system; //TODO this is a total hack, need to represent "switching to kernel mode"
        
        return system;
    }
}
