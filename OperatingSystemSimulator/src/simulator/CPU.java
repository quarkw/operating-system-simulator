/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

import kernel.ProcessControlBlock;
import kernel.ProcessState;
import java.util.Random;
import kernel.Kernel;

public class CPU {
    
    public final InterruptProcessor interruptProcessor;
    public final IODevice ioDevice;
    
    public int memory;
    public Kernel kernel;
    public long clockTime;
    
    /*Simulated registers */
    public ProcessControlBlock runningPcbPointer;
    public int interruptTimer;
    public boolean blocked = false;
    
    /*Registers to be saved in PCB */
    public int operationCounter;
    public int programCounter;

    private static final int defaultMemory = 256;
    
    private Random rand = new Random();
    
    public CPU() {
        this(defaultMemory);
    }
    public CPU(int memory){
        this.interruptProcessor = new InterruptProcessor();
        this.memory = memory;
        this.ioDevice = new IODevice(this);
        this.clockTime = 0;
    }
    public boolean isRunning(){
        if (kernel.isBSOD()) return false;
        
        for(ProcessControlBlock pcb : kernel.allProcesses){
            if(!pcb.getState().equals(ProcessState.TERMINATED)) return true;
        }
        return false;
    }
    public boolean attemptToCycle() {
        if (kernel.isBSOD()) return false;
        
        if(runningPcbPointer == null) { //No program is running, we must bootsrap
            this.runningPcbPointer = kernel.stScheduler.getNextPcb();
            if (runningPcbPointer == null) return false;
            this.runningPcbPointer.state = ProcessState.RUNNING;
            this.programCounter = -1;
            this.operationCounter = 0;
        } 
        
        cycle();

        return true;
    }
    
    private void cycle() {
        clockTime++;
        ioDevice.cycle();
        
        if (interruptProcessor.isInterruptPending()) {
            interruptProcessor.signalInterrupt();
        } else if (blocked){
            System.out.println("Blocked " + clockTime);
            return;
        }else if (interruptTimer <= 0 ) {
            interruptProcessor.setFlag(InterruptProcessor.YIELD);
            interruptProcessor.signalInterrupt();
        } else {
            interruptTimer--;
            if (operationCounter == 0) {
                programCounter++;
                loadOperation();
            }
            
            executeOperation();
            runningPcbPointer.cpuUsed++;
        }
    }
    
    private void loadOperation() {
        Operation op = runningPcbPointer.program.get(programCounter);
        switch(op.getType()){
            
            case Operation.CALCULATE:
                operationCounter = op.getParameter();
                break;
            case Operation.IO:
                //operationCounter = 25 + rand.nextInt(25);
                operationCounter = 0;
                break;
            case Operation.AQUIRE:
                operationCounter = 1;
                break;
            case Operation.RELEASE:
                operationCounter = 0;
                break;
            case Operation.END_OF_PROGRAM:
                operationCounter = 0;
                break;
                
        }
    }
    private void executeOperation() {
        Operation op = runningPcbPointer.program.get(programCounter);
        switch(op.getType()){
            case Operation.CALCULATE:
                operationCounter--;
                break;
            case Operation.IO:
                //operationCounter--;
                ioDevice.executeIO();
                interruptProcessor.setFlag(InterruptProcessor.BLOCK_FOR_IO);
                break;
            case Operation.END_OF_PROGRAM:
                System.out.println("Terminating process " + runningPcbPointer.processID);
                runningPcbPointer.state = ProcessState.TERMINATED;
                interruptProcessor.setFlag(InterruptProcessor.TERMINATE);
                break;
            case Operation.AQUIRE:
                interruptProcessor.setFlag(InterruptProcessor.AQUIRE);
                break;
            case Operation.RELEASE:
                interruptProcessor.setFlag(InterruptProcessor.RELEASE);
                break;
                
        }
    }
    
}
