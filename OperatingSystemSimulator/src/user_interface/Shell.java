package user_interface;

import kernel.SystemCalls;
import simulator.CPU;
import utilities.BootLoader;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Shell {
    private Scanner sc;
    private CPU cpu;
    private SystemCalls systemCalls;
    private File workingDirectory, programFiles;
    private Boolean hasRun = false;
    private Boolean programLoaded = false;
    private static final String programFilesDirectoryName = "ProgramFiles";
    private static final String programExtension = ".prgrm";
    private static final String jobExtension = ".job";
    private static final Charset encoding = StandardCharsets.UTF_8;


    public Shell(){
        this(System.in);
    }
    public Shell(InputStream input){
        this.sc = new Scanner(input);
        this.cpu = new CPU();
        this.systemCalls = BootLoader.boot(cpu);
        this.workingDirectory = new File(System.getProperty("user.dir"));
        this.programFiles = new File(workingDirectory.getAbsolutePath() + "/" + programFilesDirectoryName);
    }
    public void readLines() throws IOException {
        while(sc.hasNextLine()){
            executeInput(sc.nextLine());
        }
    }
    public void executeInput(String input) throws IOException {
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
                exe(params);
                break;
            case "RESET":
                reset();
                break;
            case "EXIT":
                exit();
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
        if(!hasRun)
            System.out.println("You must LOAD a program and start the simulation (EXE) before running PROC");
        else if (!cpu.isRunning())
            System.out.println("All processes have terminated. Please LOAD another program and start the simulation");
        else
            System.out.println(systemCalls.processSummary());
    }

    private void mem(){
        //TODO
        // Shows the current usage of memory space

    }

    private String[] getProgramList(){
        //TODO list files in the program files directory and let user input program to load
        String[] programs = programFiles.list((File dir, String name) ->  name.endsWith(programExtension));
        return programs;
    }

    private void printProgramList(){
        String[] programs = getProgramList();
        if(programs.length==0) {
            System.out.printf("There are no programs inside the \"%s\" directory\n", programFilesDirectoryName);
            System.out.printf("Programs have the extension \"%s\" and are located in \"%s\"\n",programExtension,programFiles.getAbsolutePath());
        }
        else {
            System.out.println("Programs: " + Arrays.toString(programs));
        }
    }

    private String[] getJobList(){
        //TODO list files in the program files directory and let user input program to load
        String[] programs = programFiles.list((File dir, String name) ->  name.endsWith(jobExtension));
        return programs;
    }

    private void printJobList(){
        String[] jobs = getJobList();
        if(jobs.length==0) {
            System.out.printf("There are no jobs inside the \"%s\" directory\n", programFilesDirectoryName);
            System.out.printf("Jobs have the extension \"%s\" and are located in \"%s\"\n",jobExtension,programFiles.getAbsolutePath());
        }
        else {
            System.out.println("Programs: " + Arrays.toString(jobs));
        }
    }

    private void load(String[] filenames) throws IOException {
        if(!programFiles.exists()){
            System.out.printf("Error: There is no folder called \"%s\" inside: \"%s\"\n",programFilesDirectoryName,workingDirectory.getAbsolutePath());
            return;
        }
        if(filenames.length==0){
            printProgramList();
            printJobList();
        }

        //Try files in job

        for(String filename : filenames){
            File file;
            if(filename.endsWith(programExtension)) {
                file = new File(programFiles.getAbsolutePath() + "/" + filename);
                if(file.exists() && !file.isDirectory()){
                    programLoaded = true;
                    systemCalls.loadProgram(file.getName(),readFile(file));
                    System.out.printf("Loaded Program \"%s\"\n",filename);
                }
            } else if (filename.endsWith(jobExtension)) {
                file = new File(programFiles.getAbsoluteFile() + "/" + filename);
                if(file.exists() && !file.isDirectory()){
                    System.out.printf("Loaded Job \"%s\"\n",filename);
                    List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()),encoding);
                    for(String line : lines)
                        executeInput(line);
                }
            }
            //TODO Load file using systemCall
        }
    }

    private void exe(){
        //TODO start executing what we've loaded
        while(cpu.advanceClock());
        programLoaded = false;
    }

    private void exe(String[] parameters){
        //TODO
        if(!programLoaded){
            System.out.println("You must LOAD a program before starting the simulation");
            return;
        }
        hasRun = true;
        if(parameters.length == 0){
            exe();
            return;
        }
        try{
            int execLength = Integer.parseInt(parameters[0]);
            for(int i = 0;i < execLength; i++){
                if(!cpu.advanceClock()) {
                    programLoaded = false;
                    break;
                }
            }
        } catch(NumberFormatException e) {
            System.out.printf("ERROR: Parameter for EXE, \"%s\", is not an integer\n", parameters[0]);
        }
    }

    private void reset(){
        System.out.println("Resetting simulator ...");
        this.cpu = new CPU();
        this.systemCalls = BootLoader.boot(cpu);
        System.out.println("Reset complete");
    }

    private void exit(){
        System.out.println("Goodbye :)");
        System.exit(0);
    }

    private void suggestCommands(String input){
        String[] commands = {"PROC","MEM","LOAD","EXE","TEST","RESET","EXIT"};
        suggestCorrections(input, commands);
    }

    private void suggestCorrections(String input, String[] commands){
        String inputToLower = input.toLowerCase();
        LinkedList<String> suggestions = new LinkedList<>();
        for(String command : commands){
            if(inputToLower.equals(command.toLowerCase()))
                suggestions.add(command);
        }
        if(!suggestions.isEmpty())
            System.out.println("Remember Commands are case sensitive");
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

    private String readFile(File file) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        return new String (encoded, encoding);
    }

}
