package kernel;

public enum ProcessState {
    NEW, READY, WAIT_FOR_DEVICE, WAIT_FOR_SIGNAL, RUNNING, TERMINATED, STANDBY
}
