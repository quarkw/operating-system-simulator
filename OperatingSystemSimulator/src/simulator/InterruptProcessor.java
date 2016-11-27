package simulator;

import kernel.interrupthandlers.*;

public class InterruptProcessor {
    
    public static final int YIELD = 0;
    public static final int AQUIRE = 1;
    public static final int RELEASE = 2;
    public static final int TERMINATE = 3;
    public static final int BLOCK_FOR_IO = 4;
    public static final int IO_COMPLETE = 5;
    
    public static final int NUM_FLAGS = 6;
    
    
    private final boolean[]interruptFlags = new boolean[NUM_FLAGS];
    
    public YieldHandler yieldHandler;
    public LockTraps lockTraps;
    public TerminateHandler terminateHandler;
    public IOWaitingHandler ioWaitingHandler;
    
    public InterruptProcessor() {
        for (int i = 0; i < NUM_FLAGS; i++) {
            interruptFlags[i] = false;
        }
    }
    
    public void setFlag(int type) {
        interruptFlags[type] = true;
    }
    
    public boolean isInterruptPending() {
        boolean pending = false;
        for (int i = 0; i < NUM_FLAGS; i++) {
            pending = pending || interruptFlags[i];
        }
        return pending;
    }
    
    public void signalInterrupt() {
        if (interruptFlags[AQUIRE]) {
            interruptFlags[AQUIRE] = false;
            lockTraps.aquire();
        } else if (interruptFlags[RELEASE]) {
            interruptFlags[RELEASE] = false;
            lockTraps.release();
        } else if (interruptFlags[BLOCK_FOR_IO]) {
            interruptFlags[BLOCK_FOR_IO] = false;
            ioWaitingHandler.handleBlocking();
        } else if (interruptFlags[IO_COMPLETE]) {
            interruptFlags[IO_COMPLETE] = false;
            ioWaitingHandler.signalIOCompletion();
        } else if (interruptFlags[TERMINATE]) {
            interruptFlags[TERMINATE] = false;
            terminateHandler.handleInterrupt();
        } else if (interruptFlags[YIELD]) {
            interruptFlags[YIELD] = false;
            yieldHandler.handleInterrupt();
        }
    }
}
