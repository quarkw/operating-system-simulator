/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernel;

import java.util.Comparator;

/**
 *
 * @author pjhud
 */
public class ProcessComparator implements Comparator<ProcessControlBlock>{
    
    private final Kernel kernel;
    
    public ProcessComparator(Kernel kernel) {
        this.kernel = kernel;
    }
    
    @Override
    public int compare(ProcessControlBlock p1, ProcessControlBlock p2) {
        long p1Age = kernel.cpu.clockTime - p1.startTime;
        long p2Age = kernel.cpu.clockTime - p2.startTime;
        if (p1.priority > p2.priority) {
            return 1;
        } else if (p1.priority < p2.priority) {
            return -1;
        } else if (p1.processID < p2.processID) {
            return 1;
        } else if (p1.processID > p2.processID) {
            return -1;
        } else {
            return 0;
        }
    }
   
}
