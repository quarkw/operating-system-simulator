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
    public final LinkedList<ProcessControlBlock> allProcesses;
    
    public final LockTraps lockTraps;
    public final YieldHandler yieldHandler;
    public final TerminateHandler terminateHandler;
    public final ContextSwitchHandler contextSwitchHandler;
    public final IOWaitingHandler ioWaitingHandler;
    
    
    
    private boolean bsodFlag = false;
    
    @SuppressWarnings("LeakingThisInConstructor")
    public Kernel(CPU cpu) {
        this.cpu = cpu;
        
        this.systemCalls = new SystemCalls(this);
        this.stScheduler = new ShortTermScheduler(this);
        this.ltScheduler = new LongTermScheduler(this);
        this.allProcesses = new LinkedList<>();
        
        
        this.yieldHandler = new YieldHandler(this);
        this.lockTraps = new LockTraps(this);
        this.terminateHandler = new TerminateHandler(this);
        this.contextSwitchHandler = new ContextSwitchHandler(this);
        this.ioWaitingHandler = new IOWaitingHandler(this);
        
        this.cpu.interruptProcessor.yieldHandler = this.yieldHandler;
        this.cpu.interruptProcessor.lockTraps = this.lockTraps;
        this.cpu.interruptProcessor.terminateHandler = this.terminateHandler;
        this.cpu.interruptProcessor.ioWaitingHandler = this.ioWaitingHandler;
        this.cpu.kernel = this;
        
    }
    
    public void BSOD() {
        bsodFlag = true;
        System.out.println("******Blue Screen of Death******");
        System.out.println("*PROCESSES:");
        for (String line : systemCalls.processSummary().split("\n")) {
            System.out.println("* " + line);
        }
        System.out.println("*PROCESSES BY QUEUE:");
        for (String line : systemCalls.processSummaryByQueue().split("\n")) {
            System.out.println("* " + line);
        }
        System.out.println("*\n*STACK TRACE:");
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        for (StackTraceElement ste : st) {
            System.out.println("*   " + ste.toString());
        }
        System.out.println("******Blue Screen of Death******");
        //assert false;
    }
    
    public boolean isBSOD() {
        return bsodFlag;
    }
    
}
