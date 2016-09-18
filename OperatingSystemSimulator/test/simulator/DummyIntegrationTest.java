/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

import simulator.CPU;
import utilities.BootLoader;
import kernel.SystemCalls;
import org.junit.Test;

/**
 *
 * @author pjhudgins
 */
public class DummyIntegrationTest {
    
    public DummyIntegrationTest() {
    }
    
    
    @Test
    public void runTwoProcesses() {
        CPU cpu = new CPU();
        SystemCalls systemCalls = BootLoader.boot(cpu);
        
        System.out.println(systemCalls.processSummary());
        for (int i = 0; i < 50; i++) {
            cpu.advanceClock();
        }
        
        String program = "50\nCALCULATE 100";
        systemCalls.loadProgram("process1", program);
        
        System.out.println(systemCalls.processSummary());
        for (int i = 0; i < 50; i++) {
            cpu.advanceClock();
        }
        
        systemCalls.loadProgram("process2", program);
        System.out.println(systemCalls.processSummary());
        
        for (int i = 0; i < 400; i++) {
            cpu.advanceClock();
        }
        System.out.println(systemCalls.processSummary());
    }
}
    

