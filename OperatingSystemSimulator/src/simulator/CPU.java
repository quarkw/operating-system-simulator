/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

import kernel.ProcessControlBlock;
import kernel.ProcessState;
import java.util.ArrayList;

public class CPU {
    
    public InterruptProcessor interruptProcessor;
    
    /*Simulated registers */
    public ProcessControlBlock runningPcbPointer;
    public int programCounter;
    public int interruptTimer;
    
    public CPU() {
        this.interruptProcessor = new InterruptProcessor();
    }
    
    public void advanceClock() {
        if (interruptTimer > 0 ) {
            interruptTimer--;
            Operation nextOp = nextOperation(runningPcbPointer);
            //System.out.println("Executing: " + runningPcbPointer.processID + " OpCounter: " + nextOp.counter);
            if (nextOp.getType() == Operation.END_OF_PROGRAM) {
                System.out.println("Terminated " + runningPcbPointer.processID);
                runningPcbPointer.state = ProcessState.TERMINATED;
                interruptProcessor.signalInterrupt();
            }
        } else {
            runningPcbPointer.state = ProcessState.READY;
            interruptProcessor.signalInterrupt();
        }
    }
    
    private Operation nextOperation(ProcessControlBlock pcb) {
        //This is a little confusing because our "machine language" can't do normal loops
        ArrayList<Operation> program = runningPcbPointer.program;       
        Operation operation = program.get(programCounter);
        if (operation.isDone()) {
            programCounter++;
            runningPcbPointer.cpuUsed++;
            operation = program.get(programCounter);
        } else {
            operation.doOneCycle();
        }
        return operation;
    }
    
}
