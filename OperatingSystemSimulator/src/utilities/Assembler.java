package utilities;

import simulator.Operation;
import java.util.ArrayList;

public class Assembler {
    
    public static ArrayList<Operation> assembleProgram(String programText) {
        ArrayList<Operation> newProgram = new ArrayList<>();
        for (String line : programText.split("\n")) {
            if (line.startsWith("OUT")) {
                newProgram.add(new Operation (Operation.OUT, 1));
            } else if (line.startsWith("YIELD")) {
                newProgram.add(new Operation (Operation.YIELD, 1));
            } else if (line.startsWith("IO")) {
                newProgram.add(new Operation (Operation.IO, 1));
            } else if (line.startsWith("CALCULATE")) {
                String cyclesString = line.substring(1 + line.indexOf(' ')).trim();
                int cycles = Integer.valueOf(cyclesString);
                newProgram.add(new Operation (Operation.CALCULATE, cycles));
            }
        }
        newProgram.add(new Operation (Operation.END_OF_PROGRAM, 1));
        return newProgram;
    }
    
    public static int memoryRequirement(String programText) {
        String memRequirementString = programText.substring(0, programText.indexOf('\n')).trim();
        return Integer.valueOf(memRequirementString);
    }
}
