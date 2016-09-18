package simulator;

public class Operation {
    
    public static final int CALCULATE = 1;
    public static final int IO = 2;
    public static final int YIELD = 3;
    public static final int OUT = 4;
    public static final int END_OF_PROGRAM= 5;
    
    private final int type;
    private int operationCounter;
    
    public Operation(int type, int counter) {
        this.type = type;
        this.operationCounter = counter;
    }
    
    public boolean isDone() {
        return operationCounter == 0; //TODO: will run for a long time but not forever if -1
    }
    
    public void doOneCycle() {
        operationCounter--;
    }

    public int getType() {
        return type;
    }

    public int getOperationCounter() {
        return operationCounter;
    }
    
}