/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernel;

import kernel.interrupthandlers.*;
import simulator.CPU;

/**
 *
 * @author pjhud
 */
public class Kernel {
    public static final int NUM_IO_DEVICES = 1;
    
    public CPU cpu;
    
    public SystemCalls systemCalls;
    public LongTermScheduler ltScheduler;
    public ShortTermScheduler stScheduler;
    
    public LockTraps lockTraps;
    public YieldHandler yieldHandler;
    public ContextSwitchHandler contextSwitchHandler;
    
    public Kernel(CPU cpu) {
        this.cpu = cpu;
        
        this.systemCalls = new SystemCalls(this);
        this.stScheduler = new ShortTermScheduler(this);
        this.ltScheduler = new LongTermScheduler(this);
        
        
        this.yieldHandler = new YieldHandler(this);
        this.lockTraps = new LockTraps(this);
        this.contextSwitchHandler = new ContextSwitchHandler(this);
        
        
    }
    
}
