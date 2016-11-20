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

/**
 *
 * @author pjhudgins
 */
public class DummyIntegrationTest {
    
    public DummyIntegrationTest() {
    }
    
    @Ignore
    @Test
    public void runTwoProcesses() {
        CPU cpu = new CPU();
        SystemCalls systemCalls = BootLoader.boot(cpu);

        System.out.println("CPU 25-cycle 1 start");
        System.out.println(systemCalls.processSummary());
        for (int i = 0; i < 20; i++) {
            cpu.cycle();
        }
        System.out.println("CPU 25-cycle 1 end");
        
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
    
    
    @Test
    public void runFiveProcesses() {
        CPU cpu = new CPU();
        SystemCalls systemCalls = BootLoader.boot(cpu);
        
        exec(25, cpu, systemCalls);
        
        String program = "50 \nIO \nCALCULATE 20";
        
        System.out.println("Loading process 1");
        systemCalls.loadProgram("IOprogram", program);
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
               cpu.cycle();
            }
        }
        System.out.println(system.processSummary());
    }
    @Ignore
    @Test
    public void runShellTest() throws IOException {
        Shell shell = new Shell();
        shell.executeInput("TEST");
    }
}
    

