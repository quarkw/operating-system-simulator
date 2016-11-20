/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernel;

import java.util.LinkedList;
import kernel.interrupthandlers.*;
import simulator.CPU;

/**
 *
 * @author pjhud
 */
public class Kernel {
    public static final int NUM_IO_DEVICES = 1;
    
    public final CPU cpu;
    
    public final SystemCalls systemCalls;
    public final ShortTermScheduler stScheduler;
    public final LongTermScheduler ltScheduler;
    
    public final LockTraps lockTraps;
    public final YieldHandler yieldHandler;
    public final ContextSwitchHandler contextSwitchHandler;
    
    public LinkedList<ProcessControlBlock> allProcesses;
    
    public Kernel(CPU cpu) {
        this.cpu = cpu;
        
        this.systemCalls = new SystemCalls(this);
        this.stScheduler = new ShortTermScheduler(this);
        this.ltScheduler = new LongTermScheduler(this);
        
        
        this.yieldHandler = new YieldHandler(this);
        this.lockTraps = new LockTraps(this);
        this.contextSwitchHandler = new ContextSwitchHandler(this);
        
        this.allProcesses = new LinkedList<>();
    }
    
}
