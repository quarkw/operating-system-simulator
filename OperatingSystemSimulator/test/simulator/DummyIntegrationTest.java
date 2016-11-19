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
        for (int i = 0; i < 20; i++) {
            cpu.cycle();
        }
        
        String program = "50 \nIO \nCALCULATE 20";
        System.out.println("Loading process1");
        systemCalls.loadProgram("process1", program);
        
        System.out.println(systemCalls.processSummary());
        for (int i = 0; i < 20; i++) {
            cpu.cycle();
        }
        
        systemCalls.loadProgram("process2", program);
        System.out.println("Loading process2");
        
        exec(325, cpu, systemCalls);
        //System.out.println(systemCalls.processSummary());
    }
    
    private void exec(int n, CPU cpu, SystemCalls system) {
        for (int i = 0; i < n; i += 25) {
            System.out.println(system.processSummary());
            for (int j = 0; j < 25; j++) {
               cpu.cycle();
            }
        }
        System.out.println(system.processSummary());
    }
}
    

