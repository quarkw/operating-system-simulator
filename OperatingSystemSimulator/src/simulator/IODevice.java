/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

import java.util.Random;

/**
 *
 * @author pjhud
 */
public class IODevice {
    private static final int MAX_IO_WAITING_TIME = 50;
    
    public final CPU cpu;
    
    private Random rand = new Random();
    
    private long completionTime;
    private boolean inUse = false;
    
    public IODevice(CPU cpu) {
        this.cpu = cpu;
    }
    
    public void executeIO() {
        inUse = true;
        completionTime = cpu.clockTime + 2 + rand.nextInt(MAX_IO_WAITING_TIME);
    }
    
    public void cycle() {
        if (inUse && cpu.clockTime >= completionTime) {
            cpu.interruptProcessor.setFlag(InterruptProcessor.IO_COMPLETE);
            inUse = false;
        }
    }
    
    public boolean isInUse() {
        return inUse;
    }
    
}
