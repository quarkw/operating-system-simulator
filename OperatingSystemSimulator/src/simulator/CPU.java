/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

import kernel.ProcessControlBlock;
import kernel.ProcessState;
import java.util.Random;
import kernel.SystemCalls;

public class CPU {
    
    public InterruptProcessor interruptProcessor;
    public SystemCalls system;
    
    /*Simulated registers */
    public ProcessControlBlock runningPcbPointer;
    public int interruptTimer;
    
    /*Registers to be saved in PCB */
    public int operationCounter;
    public int programCounter;
    
    
    
    
    private Random rand = new Random();
    
    public CPU() {
        this.interruptProcessor = new InterruptProcessor();
        this.interruptTimer = 5; //TODO debugging purposes only
    }
    
    public void cycle() {
        if (interruptTimer > 0 ) {
            interruptTimer--;
            if (operationCounter == 0) {
                programCounter++;
                loadOperation();
            }
            System.out.println("Executing: " + runningPcbPointer.processID
                    + " Program Ctr: " + programCounter
                    + " OpCounter: " + operationCounter);
            operationCounter--;
            runningPcbPointer.cpuUsed++;
            
        } else {
            runningPcbPointer.state = ProcessState.READY;
            interruptProcessor.signalInterrupt();
        }
    }
    
    private void loadOperation() {
        Operation op = runningPcbPointer.program.get(programCounter);
        switch(op.getType()){
            case Operation.END_OF_PROGRAM:
                System.out.println("Terminated " + runningPcbPointer.processID);
                runningPcbPointer.state = ProcessState.TERMINATED;
                interruptProcessor.signalInterrupt();
                break;
            case Operation.CALCULATE:
                operationCounter = op.getParameter();
                break;
            case Operation.IO:
                operationCounter = 25 + rand.nextInt(25);
                break;
            case Operation.AQUIRE:
                if (!system.aquire("device 1")) {
                    System.out.println("Process " + runningPcbPointer.processID + " failed to aquire");
                    programCounter--; //TODO this is pretty ugly
                    
                    interruptTimer = 0;
                    //interruptProcessor.signalInterrupt();
                } else {
                    System.out.println("Process " + runningPcbPointer.processID + " AQUIRED");
                }
                operationCounter = 1; //TODO find a better way to spin
                break;
            case Operation.RELEASE:
                system.release("device 1");
                operationCounter = 1;
                break;
                
        }
    }
    
}
