package utilities;

import kernel.interrupthandlers.*;
import kernel.*;
import simulator.CPU;

public class BootLoader {
    public static Kernel boot(CPU cpu) {
        Kernel kernel = new Kernel(cpu);
        cpu.interruptProcessor.yieldHandler = kernel.yieldHandler;
        cpu.interruptProcessor.lockTraps = kernel.lockTraps;
        cpu.kernel = kernel;
        
        
        //SystemCalls system = new SystemCalls(scheduler, cpu);
        
        String backgroundProgram = "20 \n" + "CALCULATE -1";
        kernel.systemCalls.loadProgram("backgroundProcess", backgroundProgram);
        kernel.ltScheduler.schedule();
        cpu.runningPcbPointer = kernel.stScheduler.getNextPcb();
        cpu.programCounter = -1;
        cpu.operationCounter = 0; 
         //TODO this is a total hack, need to represent "switching to kernel mode"
        
        return kernel;
    }
}
