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
import kernel.SystemCalls;

public class CPU {
    
    public InterruptProcessor interruptProcessor;
    public Kernel kernel;
    
    /*Simulated registers */
    public ProcessControlBlock runningPcbPointer;
    public int interruptTimer;
    
    /*Registers to be saved in PCB */
    public int operationCounter;
    public int programCounter;
    
    
    private Random rand = new Random();
    
    public CPU() {
        this.interruptProcessor = new InterruptProcessor();
    }
    
    public void cycle() {
        if (interruptTimer > 0 ) {
            interruptTimer--;
        } else {
           interruptProcessor.setFlag(InterruptProcessor.YIELD);
        }
        
        if (interruptProcessor.isInterruptPending()) {
            interruptProcessor.signalInterrupt();
        } else {
            if (operationCounter == 0) {
                programCounter++;
                loadOperation();
            }
            //System.out.println("Executing: " + runningPcbPointer.processID
            //    + " Program Ctr: " + programCounter
            //    + " OpCounter: " + operationCounter);
            
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
                operationCounter = 25 + rand.nextInt(25);
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
                operationCounter--;
                break;
            case Operation.END_OF_PROGRAM:
                System.out.println("Terminating " + runningPcbPointer.processID);
                runningPcbPointer.state = ProcessState.TERMINATED;
                interruptProcessor.setFlag(InterruptProcessor.YIELD);
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
