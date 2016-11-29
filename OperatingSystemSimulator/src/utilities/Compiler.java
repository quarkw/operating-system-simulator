package utilities;

import simulator.Operation;
import java.util.ArrayList;

public class Compiler {
    
    public static ArrayList<Operation> assembleProgram(String programText) {
        ArrayList<Operation> newProgram = new ArrayList<>();
        for (String line : programText.split("\n")) {
            if (line.startsWith("OUT")) {
                newProgram.add(new Operation (Operation.OUT, 0));
            } else if (line.startsWith("YIELD")) {
                newProgram.add(new Operation (Operation.YIELD, 0));
            } else if (line.startsWith("IO")) {
                newProgram.add(new Operation (Operation.AQUIRE, 0));
                newProgram.add(new Operation (Operation.IO, 0));
                newProgram.add(new Operation (Operation.RELEASE, 0));
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
    
    public static int programPriority(String programText) {
        String priorityLine = programText.split("\n")[1];
        if (priorityLine.startsWith("PRIORITY")) {
            String priorityString = priorityLine.substring(1 + priorityLine.indexOf(' ')).trim();
            return Integer.valueOf(priorityString);
        } else {
            return 0;
        }
    }
}
