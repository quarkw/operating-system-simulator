package utilities;

import kernel.interrupthandlers.*;
import kernel.*;
import simulator.CPU;

public class BootLoader {
    public static SystemCalls boot(CPU cpu) {
        LongTermScheduler scheduler = new LongTermScheduler(cpu);
        cpu.interruptProcessor.yieldHandler = new YieldHandler(cpu, scheduler.shortTermScheduler);
        cpu.interruptProcessor.lockTraps = new LockTraps(cpu);
        
        
        SystemCalls system = new SystemCalls(scheduler, cpu);
        
//        String backgroundProgram = "20 \n" + "CALCULATE -1";
//        system.loadProgram("backgroundProcess", backgroundProgram);
//        scheduler.schedule();
//        cpu.runningPcbPointer = scheduler.shortTermScheduler.getNextPcb();
//        cpu.programCounter = -1;
//        cpu.operationCounter = 0;
        cpu.system = system; //TODO this is a total hack, need to represent "switching to kernel mode"
        
        return system;
    }
}
