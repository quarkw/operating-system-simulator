package kernel;

public enum ProcessState {
    NEW, READY, WAIT_AQUIRE, WAIT_IO, RUNNING, TERMINATED, STANDBY
}
