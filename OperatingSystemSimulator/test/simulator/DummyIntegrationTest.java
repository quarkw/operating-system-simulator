/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

import simulator.CPU;
import user_interface.Shell;
import utilities.BootLoader;
import kernel.SystemCalls;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import kernel.Kernel;

/**
 *
 * @author pjhudgins
 */
public class DummyIntegrationTest {
    
    public DummyIntegrationTest() {
    }

    @Test
    public void runFiveProcesses() {
        CPU cpu = new CPU();
        Kernel kernel = BootLoader.boot(cpu);
        SystemCalls systemCalls = kernel.systemCalls;
        
        String program = "80 \nIO \nCALCULATE 20";
        
        System.out.println("Loading process 1");
        systemCalls.loadProgram("IOprogram", program);
        cpu.advanceClock();
        exec(25, cpu, systemCalls);
        
        System.out.println("Loading process 2");
        systemCalls.loadProgram("IOprogram", program);
        exec(25, cpu, systemCalls);
        
        System.out.println("Loading process 3");
        systemCalls.loadProgram("IOprogram", program);
        exec(25, cpu, systemCalls);
        
        System.out.println("Loading process 4");
        systemCalls.loadProgram("IOprogram", program);
        exec(25, cpu, systemCalls);
        
        System.out.println("Loading process 5");
        systemCalls.loadProgram("IOprogram", program);
        exec(25, cpu, systemCalls);
        
        exec(1000, cpu, systemCalls);
        //System.out.println(systemCalls.processSummary());
    }
    
    private void exec(int n, CPU cpu, SystemCalls system) {
        for (int i = 0; i < n; i += 25) {
            System.out.println(system.processSummary());
            for (int j = 0; j < 25; j++) {
               if (!cpu.advanceClock()) return;
            }
        }
        System.out.println(system.processSummary());
    }
    
    @Ignore
    @Test
    public void runShellTest() throws IOException {
        Shell shell = new Shell();
        shell.executeInput("LOAD test.job");
    }
}
    

