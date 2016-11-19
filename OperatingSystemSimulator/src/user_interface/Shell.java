package user_interface;

import kernel.SystemCalls;
import simulator.CPU;
import utilities.BootLoader;

import java.io.InputStream;
import java.util.*;

public class Shell {
    private Scanner sc;
    private CPU cpu;
    private SystemCalls systemCalls;

    public Shell(){
        this(System.in);
    }
    public Shell(InputStream input){
        this.sc = new Scanner(input);
        this.cpu = new CPU();
        this.systemCalls = BootLoader.boot(cpu);
    }
    public void readLines(){
        while(sc.hasNextLine()){
            executeInput(sc.nextLine());
        }
    }
    public void executeInput(String input){
        String[] inputArray = input.split("\\s");
        String command = inputArray[0];
        String[] params = {};

        if(inputArray.length>1)
           params = Arrays.copyOfRange(inputArray,1,inputArray.length);

        switch(inputArray[0]){
            case "PROC":
                proc();
                break;
            case "MEM":
                mem();
                break;
            case "LOAD":
                load(params);
                break;
            case "EXE":
                exe();
                break;
            case "TEST":
                test();
                break;
            case "RESET":
                System.out.println("Logic for RESET");
                break;
            case "EXIT":
                System.out.println("Logic for EXIT");
                break;
            default:
                System.out.printf("Command \"%s\" not found\n", inputArray[0]);
                suggestCommands(inputArray[0]);
        }

    }

    private void proc(){
        //TODO
        // Shows all unfinished processes in the system and their information.
        // The process information should include:
        // current process state,
        // amount of CPU time needed to complete,
        // amount of CPU time already used,
        // priority (if relevant),
        // number of I/O requests performed.
    }

    private void mem(){
        //TODO
        // Shows the current usage of memory space

    }

    private void load(){
        //TODO list files in the program files directory and let user input program to load

    }

    private void load(String[] filenames){
        if(filenames.length==0)
            load();
        for(String file : filenames){
            //TODO Load file using systemCall
        }
    }

    private void exe(){
        //TODO start executing what we've loaded
    }

    private void exe(String[] parameters){
        //TODO
    }

    private void test(){
        //Execute whatever test code you want;
        System.out.println("CPU 25-cycle 1 start");
        System.out.println(systemCalls.processSummary());
        for (int i = 0; i < 25; i++) {
            cpu.cycle();
        }
        System.out.println("CPU 25-cycle 1 end");

        String program = "50 \nIO \nCALCULATE 20";
        System.out.println("Loading process1");
        systemCalls.loadProgram("process1", program);

        System.out.println(systemCalls.processSummary());
        for (int i = 0; i < 25; i++) {
            cpu.cycle();
        }

        systemCalls.loadProgram("process2", program);
        System.out.println("Loading process2");
        System.out.println(systemCalls.processSummary());

        for (int i = 0; i < 200; i++) {
            cpu.cycle();
        }
        System.out.println(systemCalls.processSummary());
    }

    private void reset(){
        System.out.println("Resetting simulator ...");
        this.cpu = new CPU();
        this.systemCalls = BootLoader.boot(cpu);
        System.out.print("Reset complete");
    }

    private void exit(){
        System.out.println("Goodbye :)");
        System.exit(0);
    }

    private void suggestCommands(String input){
        String inputToLower = input.toLowerCase();
        String[] commands = {"PROC","MEM","LOAD","EXE","TEST","RESET","EXIT"};
        LinkedList<String> suggestions = new LinkedList<>();
        for(String command : commands){
            if(inputToLower.equals(command.toLowerCase()))
                suggestions.add(command);
        }
        //Suggest command that share two n-2 letters, (minimum 2)
        for(String command : commands){
            //Find number of shared characters
            int numSharedCharacters = 0;
            HashMap<Character, Integer> commandCharMap = new HashMap<>();
            for(Character c : command.toLowerCase().toCharArray()){
                commandCharMap.putIfAbsent(c,0);
                int count = commandCharMap.get(c)+1;
                commandCharMap.put(c,count);
            }
            for(Character c : inputToLower.toCharArray()){
                if(commandCharMap.containsKey(c)){
                    numSharedCharacters++;
                    int count = commandCharMap.get(c) - 1;
                    if(count == 0)
                        commandCharMap.remove(c);
                    else
                        commandCharMap.put(c,count);
                }
            }
            //if shared characters < n && >= n-2 && >= 2;
            if(numSharedCharacters >= command.length()-2
                    && numSharedCharacters >= 2
                    && numSharedCharacters < command.length()
                    ){
                suggestions.add(command);
            }
        }
        //Suggest the suggestions
        if(!suggestions.isEmpty())
            System.out.println("Did you mean: " + linkedListToHumanReadableOrList(suggestions));
    }
    private String linkedListToHumanReadableOrList(LinkedList list){
        if(list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();

        sb.append('"').append(list.remove().toString()).append('"');
        while(!list.isEmpty()){
            sb.append(" or ");
            sb.append('"').append(list.remove().toString()).append('"');
        }
        return sb.toString();
    }

}
