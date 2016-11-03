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
        for (int i = 0; i < 25; i++) {
            cpu.cycle();
        }
        
        String program = "50 \nIO \nCALCULATE 20";
        System.out.println("Loading process1");
        systemCalls.loadProgram("process1", program);
        
        System.out.println(systemCalls.processSummary());
        for (int i = 0; i < 25; i++) {
            cpu.cycle();
        }
        
        systemCalls.loadProgram("process2", program);
        System.out.println("Loading process2");
        System.out.println(systemCalls.processSummary());
        
        for (int i = 0; i < 200; i++) {
            cpu.cycle();
        }
        System.out.println(systemCalls.processSummary());
    }
}
    

