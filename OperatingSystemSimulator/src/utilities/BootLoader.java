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
        
        
        return kernel;
    }
}
