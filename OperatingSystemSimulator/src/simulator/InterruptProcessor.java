package simulator;

import kernel.interrupthandlers.*;

public class InterruptProcessor {
    
    public static final int YIELD = 0;
    public static final int AQUIRE = 1;
    public static final int RELEASE = 2;
    public static final int TERMINATE = 3;
    
    
    private final boolean[]interruptFlags = new boolean[4];
    
    public YieldHandler yieldHandler;
    public LockTraps lockTraps;
    public TerminateHandler terminateHandler;
    
    public InterruptProcessor() {
        for (int i = 0; i < interruptFlags.length; i++) {
            interruptFlags[i] = false;
        }
    }
    
    public void setFlag(int type) {
        interruptFlags[type] = true;
    }
    
    public boolean isInterruptPending() {
        boolean pending = false;
        for (int i = 0; i < interruptFlags.length; i++) {
            pending = pending || interruptFlags[i];
        }
        return pending;
    }
    
    public void signalInterrupt() {
        if (interruptFlags[TERMINATE]) {
            interruptFlags[TERMINATE] = false;
            interruptFlags[YIELD] = false;
            terminateHandler.handleInterrupt();
        } else if (interruptFlags[AQUIRE]) {
            interruptFlags[AQUIRE] = false;
            lockTraps.aquire();
        } else if (interruptFlags[RELEASE]) {
            interruptFlags[RELEASE] = false;
            lockTraps.release();
        } else if (interruptFlags[YIELD]) {
            interruptFlags[YIELD] = false;
            yieldHandler.handleInterrupt();
        }
    }
}
