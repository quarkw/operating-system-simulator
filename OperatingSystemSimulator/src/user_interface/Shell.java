package user_interface;

import kernel.ProcessControlBlock;
import kernel.SystemCalls;
import simulator.CPU;
import utilities.BootLoader;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import kernel.Kernel;

public class Shell extends Thread {
    private Scanner sc;
    private LinkedList<String> history;
    private ListIterator<String> historyIterator;
    private Boolean lastHistoryDirectionUp = true;
    private LineFinishedListener lineFinishedListener;
    private CPU cpu;
    private Kernel kernel;
    private SystemCalls systemCalls;
    private File workingDirectory, programFiles;
    private Boolean hasRun = false;
    private Boolean programLoaded = false;
    public static final String[] commands = {"PROC","MEM","LOAD","EXE","RESET","EXIT"};
    private static final String programFilesDirectoryName = "ProgramFiles";
    private static final String programExtension = ".prgrm";
    private static final String jobExtension = ".job";
    private static final Charset encoding = StandardCharsets.UTF_8;

    public interface LineFinishedListener {
        void onLineFinished();
    }
    public Shell(){
        this(System.in);
    }
    public Shell(InputStream input){
        this.sc = new Scanner(input);
        this.history = new LinkedList<>();
        this.historyIterator = history.listIterator();
        this.cpu = new CPU();
        this.kernel = BootLoader.boot(cpu);
        this.systemCalls = kernel.systemCalls;
        this.workingDirectory = new File(System.getProperty("user.dir"));
        this.programFiles = new File(workingDirectory.getAbsolutePath() + "/" + programFilesDirectoryName);
    }
    public Shell(InputStream input, OutputStream out){
        this(input);
        System.setOut(new PrintStream(out,true));
    }

    public void run(){  //called by .start()
        while(sc.hasNextLine()) {
            try {
                String line = sc.nextLine();
                executeInput(line);
                history.add(line);
                historyIterator = history.listIterator(history.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(this.lineFinishedListener != null)
                this.lineFinishedListener.onLineFinished();
        }
    }

    public void setLineFinishedListener(LineFinishedListener listener){
        this.lineFinishedListener = listener;
    }

    public String getLastInput(){
        if(history.isEmpty()) return "";
        return history.getLast();
    }

    public String getPreviousInput(){   //Up
        if(history.isEmpty()) return "";
        if(!lastHistoryDirectionUp)
            if(historyIterator.hasPrevious()) historyIterator.previous();
        lastHistoryDirectionUp = true;
        if(historyIterator.hasPrevious())
            return historyIterator.previous();
        else {
            lastHistoryDirectionUp = false;
            return historyIterator.next();
        }
    }

    public String getNextInput(){       //Down
        if(history.isEmpty()) return "";
        if(lastHistoryDirectionUp)
            if(historyIterator.hasNext()) historyIterator.next();
        lastHistoryDirectionUp = false;
        if(historyIterator.hasNext())
            return historyIterator.next();
        else {
            lastHistoryDirectionUp = true;
            return historyIterator.previous();
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
                System.out.printf("ERROR: Command \"%s\" not found\n", inputArray[0]);
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
            System.out.println("ERROR: You must LOAD a program and start the simulation (EXE) before running PROC");
        else if (!cpu.isRunning())
            System.out.println("ERROR: All processes have terminated. Please LOAD another program and start the simulation (EXE)");
        else
            System.out.println(systemCalls.processSummaryByQueue());
    }

    private void mem(){
        //TODO
        // Shows the current usage of memory space

    }

    private String[] getFileList(){
        String[] programs = getProgramList();
        String[] jobs = getJobList();
        String[] files = new String[programs.length + jobs.length];
        int cursor = 0;
        for(String program : programs)
            files[cursor++] = program;
        for(String job : jobs)
            files[cursor++] = job;
        return files;
    }

    private String[] getProgramList(){
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
        String[] jobs = programFiles.list((File dir, String name) ->  name.endsWith(jobExtension));
        return jobs;
    }

    private void printJobList(){
        String[] jobs = getJobList();
        if(jobs.length==0) {
            System.out.printf("ERROR: There are no jobs inside the \"%s\" directory\n", programFilesDirectoryName);
            System.out.printf("Jobs have the extension \"%s\" and are located in \"%s\"\n",jobExtension,programFiles.getAbsolutePath());
        }
        else {
            System.out.println("Jobs: " + Arrays.toString(jobs));
        }
    }

    private void load(String[] filenames) throws IOException {
        if(!programFiles.exists()){
            System.out.printf("ERROR: There is no folder called \"%s\" inside: \"%s\"\n",programFilesDirectoryName,workingDirectory.getAbsolutePath());
            return;
        }
        if(filenames.length==0){
            System.out.printf("LOAD takes any number of files as parameters in the form: \"LOAD file1.ext file2.ext\" and can load files with extensions \"%s\" and\"%s\"\n",programExtension,jobExtension);
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
                } else {
                    System.out.printf("ERROR: Unable to find program \"%s\"\n",filename);
                    suggestCorrections(filename,getProgramList(),"programs",false,2);
                }
            } else if (filename.endsWith(jobExtension)) {
                file = new File(programFiles.getAbsoluteFile() + "/" + filename);
                if(file.exists() && !file.isDirectory()){
                    System.out.printf("Loaded Job \"%s\"\n",filename);
                    List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()),encoding);
                    for(String line : lines)
                        executeInput(line);
                } else {
                    System.out.printf("ERROR: Unable to find job \"%s\"\n",filename);
                    suggestCorrections(filename,getJobList(),"jobs",false,2);
                }
            } else {
                System.out.printf("ERROR: File name \"%s\" must have extension \"%s\" or \"%s\"\n",filename,programExtension,jobExtension);
                if(filename.contains("."))
                    suggestCorrections(filename,getFileList(),"files",false,5);
            }
        }
    }

    private void exe(){
        //Start executing what's been loaded until end
        while(cpu.advanceClock());
        programLoaded = false;
    }

    private void exe(String[] parameters){
        //Start executing what's been loaded for X cycles
        if(!programLoaded){
            System.out.println("ERROR: You must LOAD a program before starting the simulation");
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
        this.kernel = BootLoader.boot(cpu);
        this.systemCalls = kernel.systemCalls;
        System.out.println("Reset complete");
    }

    private void exit(){
        System.out.println("Goodbye :)");
        System.exit(0);
    }

    private void suggestCommands(String input){
        suggestCorrections(input, commands, "commands", true, 2);
    }

    private void suggestCorrections(String input, String[] commands, String inputType, Boolean caseSensitive, int tolerance){
        String inputToLower = input.toLowerCase();
        LinkedList<String> suggestions = new LinkedList<>();
        if(caseSensitive) {
            for (String command : commands) {
                if (inputToLower.equals(command.toLowerCase()))
                    suggestions.add(command);
            }
            if (!suggestions.isEmpty())
                System.out.printf("Remember, %s are case sensitive\n", inputType);
        }
        //Suggest command that share two n-tolerance letters, (minimum 2)
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
                } else {
                    numSharedCharacters--;
                }
            }
            //if shared characters < n && >= n-tolerance && >= 2;
            if(numSharedCharacters >= command.length()-tolerance
                    && numSharedCharacters >= 2
                    && numSharedCharacters <= command.length()
                    && !command.toLowerCase().equals(inputToLower)
                    ){
                suggestions.add(command);
            }
        }
        //Suggest the suggestions
        if(!suggestions.isEmpty())
            System.out.println("Did you mean: " + linkedListToHumanReadableOrList(suggestions));
        else
            System.out.printf("Here is a list of available %s:\n%s\n", inputType,Arrays.toString(commands));
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
