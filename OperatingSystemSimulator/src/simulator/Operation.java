package simulator;

public class Operation {
    
    public static final int CALCULATE = 1;
    public static final int IO = 2;
    public static final int YIELD = 3;
    public static final int OUT = 4;
    public static final int END_OF_PROGRAM= 5;
    
    public static final int AQUIRE = 10;
    public static final int RELEASE = 11;
    
    private final int type;
    private final int parameter;
    
    public Operation(int type, int counter) {
        this.type = type;
        this.parameter = counter;
    }

    public int getType() {
        return type;
    }

    public int getParameter() {
        return parameter;
    }
    
}