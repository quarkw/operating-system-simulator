/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

import kernel.InterruptHandler;

/**
 *
 * @author pjhudgins
 */
public class InterruptProcessor {
    
    public InterruptHandler interruptHandler;
    
    public void signalInterrupt() {
        interruptHandler.handleInterrupt();
    }
}
