package user_interface;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import kernel.ProcessControlBlock;
import kernel.ProcessState;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShellGUI extends Application {

    private Shell shell;

    private BorderPane borderPane;

    private TableView procTable;
    private ObservableList procTableData;
    private TableColumn pIDColumn, programNameColumn, memoryAllocationColumn, programCounterColumn, cpuUsedColumn, processStateColumn;
    private Boolean waterFallEffect = true;

    private NumberAxis memoryXAxis, memoryYAxis;
    private int yAxisMajorTickLength = 32;
    private ObservableList<AreaChart.Series> memoryChartData;
    private AreaChart memoryChart;
    private AreaChart.Series memoryUsageData, swapUsageData, memoryLimitData;

    private Slider delaySlider;

    public TextArea consoleOut;
    public TextField consoleIn;
    public StackPane consoleInWrapper;
    public Text consoleInSuggestions;
    public String suggestions;
    public Boolean autoSuggestOn = true;
    private PipedOutputStream commands;
    private PipedInputStream inputStream;
    private OutputStream outputStream;
    private StringBuilder outputSB;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
//        Button btn = new Button();
//        btn.setText("Hello World!");
//        btn.setOnAction( (event) -> {System.out.println("Hello World!");});
//
//        StackPane root = new StackPane();
//        root.getChildren().add(btn);
//
//        Scene scene = new Scene(root,400,300);
//
//        primaryStage.setTitle("Hello World!");
//        primaryStage.setScene(scene);
//        primaryStage.show();
        //Left Pane (Memory graph and controls)
        memoryXAxis = new NumberAxis("Cycles", 0, 60, 0);

        memoryYAxis = new NumberAxis("Memory Usage (kB)", 0, 256 + yAxisMajorTickLength - 256% yAxisMajorTickLength, yAxisMajorTickLength);
        memoryYAxis.setMinorTickCount(4);
        memoryYAxis.setAutoRanging(true);

        memoryUsageData = new AreaChart.Series("Memory Usage", FXCollections.observableArrayList());
        swapUsageData = new AreaChart.Series("Total Memory Usage (incl. swapped)", FXCollections.observableArrayList());
        memoryLimitData = new AreaChart.Series("Memory Limit", FXCollections.observableArrayList(
                new AreaChart.Data(0,256),
                new AreaChart.Data(60,256)
        ));

        memoryChartData = FXCollections.observableArrayList(
                memoryLimitData,
                swapUsageData,
                memoryUsageData
        );

        memoryChart = new AreaChart(memoryXAxis, memoryYAxis, memoryChartData);
        memoryChart.setAnimated(false);

        //Delay slider
        delaySlider = new Slider();
        //No delay, 10ms, 50ms, 100ms, 1000ms, Hold
        delaySlider.setMin(0);
        delaySlider.setMax(5);
        delaySlider.setValue(1);
        delaySlider.setMinorTickCount(0);
        delaySlider.setMajorTickUnit(1);
        delaySlider.setSnapToTicks(true);
        delaySlider.setShowTickMarks(true);
        delaySlider.setShowTickLabels(true);

        delaySlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n < 0.5) return "0ms";
                if (n < 1.5) return "10ms";
                if (n < 2.5) return "50ms";
                if (n < 3.5) return "100ms";
                if (n < 4.5) return "1000ms";

                return "Hold";
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "0ms":
                        return 0d;
                    case "10ms":
                        return 1d;
                    case "50ms":
                        return 2d;
                    case "100ms":
                        return 3d;
                    case "1000ms":
                        return 4d;
                    default:
                        return 5d;
                }
            }
        });
        delaySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double n = (double) newValue;
                Platform.runLater( () -> {
                    shell.sleepDelay = getDelayFromDelaySliderValue(n);
                });
            }
        });

        VBox controlPane = new VBox();
        delaySlider.setPadding(new Insets(30));
        controlPane.getChildren().addAll(memoryChart, delaySlider);


        //Right Pane (Process Table)
        pIDColumn = new TableColumn();
        pIDColumn.setText("Process ID");
        pIDColumn.setCellValueFactory(new PropertyValueFactory("processID"));

        programNameColumn = new TableColumn();
        programNameColumn.setText("Program Name");
        programNameColumn.setMinWidth(120);
        programNameColumn.setCellValueFactory(new PropertyValueFactory("programName"));

        memoryAllocationColumn = new TableColumn();
        memoryAllocationColumn.setText("Memory Usage");
        memoryAllocationColumn.setMinWidth(120);
        memoryAllocationColumn.setCellValueFactory(new PropertyValueFactory("memoryAllocation"));

        programCounterColumn = new TableColumn();
        programCounterColumn.setText("Program Counter");
        programCounterColumn.setMinWidth(130);
        programCounterColumn.setCellValueFactory(new PropertyValueFactory("programCounter"));

        cpuUsedColumn = new TableColumn();
        cpuUsedColumn.setText("CPU Used");
        cpuUsedColumn.setCellValueFactory(new PropertyValueFactory("cpuUsed"));

        processStateColumn = new TableColumn();
        processStateColumn.setText("State");
        programCounterColumn.setMinWidth(130);
        processStateColumn.setCellValueFactory(new PropertyValueFactory("state"));

        procTable = new TableView();
        procTable.setRowFactory(Row -> new TableRow<ProcessControlBlock>(){
            @Override
            public void updateItem(ProcessControlBlock pcb, boolean empty){
                super.updateItem(pcb, empty);

                if(pcb == null || empty){
                    setStyle("");
                } else {
                    ProcessState state = pcb.getState();
                    String style = "";
                    String baseColor = "";
                    switch(state){
                        case NEW:
                            baseColor = "white";
                            break;
                        case READY:
                            baseColor = "cornflowerblue";
                            break;
                        case WAIT_FOR_DEVICE:
                            baseColor = "darkkhaki";
                            break;
                        case WAIT_FOR_SIGNAL:
                            baseColor = "salmon";
                            break;
                        case RUNNING:
                            baseColor = "forestgreen";
                            break;
                        case TERMINATED:
                            baseColor = "hotpink";
                            break;
                        case STANDBY:
                            baseColor = "darkorange";
                            break;
                        default:
                            break;
                    }
                    double percent = (double) pcb.cpuUsed / pcb.maxCycles;
                    if(!baseColor.equals(""))
                        style = String.format("-fx-background-color: linear-gradient(to right, %1$s %2$s%% , derive(%1$s, 50%%) 1%% );", baseColor, percent*100);
                    setStyle(style);
                }
            }
        });
        procTableData = FXCollections.observableArrayList();
        procTable.setItems(procTableData);
        procTable.getColumns().addAll(pIDColumn,programNameColumn,memoryAllocationColumn,programCounterColumn,cpuUsedColumn,processStateColumn);



        //Bottom Pane (Console)
        ObservableList<String> monoFonts = getMonoFontFamilyNames();
        String fontFamily = monoFonts.get(0);
        int fontSize = 16;
        Font font = new Font(fontFamily,fontSize);


        consoleOut  = new TextArea("");
        consoleOut.setWrapText(true);
        consoleOut.setEditable(false);

        commands = new PipedOutputStream();
        consoleIn = new TextField();

        consoleInSuggestions = new Text("");

        consoleOut.setFont(font);
        consoleIn.setFont(font);
        consoleInSuggestions.setFont(font);

        consoleInSuggestions.setFill(Color.LIGHTGRAY);

        consoleIn.setOnAction ( (e) -> {
            if(consoleIn.getText().equals(""))
                consoleIn.setText(shell.getLastInput());
            if(consoleIn.getText().equals("")) return;
            try {
                commands.write(consoleIn.getText().getBytes());
                commands.write("\n".getBytes());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println(">" + consoleIn.getText());
            consoleIn.setText("");
            disableInput();
        });

        consoleIn.setOnKeyPressed( (e) ->{

            if(e.getCode().equals(KeyCode.UP)) {
                String previous = shell.getPreviousInput();
                if (!previous.equals("")){
                    consoleIn.setText(previous);
                    consoleIn.positionCaret(consoleIn.getLength());
                }
                e.consume();
            } else if (e.getCode().equals(KeyCode.DOWN)){
                String next = shell.getNextInput();
                if(!next.equals("")) {
                    consoleIn.setText(next);
                    consoleIn.positionCaret(consoleIn.getLength());
                }
                e.consume();
            } else if (e.getCode().equals(KeyCode.TAB) && autoSuggestOn) {
                if (suggestions.length() > consoleIn.getLength())
                    consoleIn.setText(suggestions);
                consoleIn.positionCaret(consoleIn.getLength());
                e.consume();
            } else if (e.getCode().equals(KeyCode.BACK_SPACE) && autoSuggestOn){
                    if (consoleIn.getText().length() > suggestions.length())
                        suggestions = consoleIn.getText();
            } else if (autoSuggestOn) {
                suggestions = consoleIn.getText();
            }

        });
        consoleIn.setOnKeyTyped( (e) -> {
            if(e.getCode().equals(KeyCode.UP)){
            } else if (e.getCode().equals(KeyCode.DOWN)){
            } else if (e.getCode().equals(KeyCode.TAB)){
            } else if (e.getCode().equals(KeyCode.BACK_SPACE)){
            } else if (autoSuggestOn){
                String autoType = shell.autoType(consoleIn.getText());
                if(!autoType.equals(consoleIn.getText()))
                    suggestions = autoType;
            }
            consoleInSuggestions.setText(existingToSpaces(suggestions));
        });
        consoleIn.setOnKeyReleased( (e) -> {
            if(e.getCode().equals(KeyCode.UP)){
            } else if (e.getCode().equals(KeyCode.DOWN)){
            } else if (e.getCode().equals(KeyCode.TAB)){
            } else if (e.getCode().equals(KeyCode.BACK_SPACE)){
            } else if (autoSuggestOn) {
                String autoType = shell.autoType(consoleIn.getText());
                if(!autoType.equals(consoleIn.getText()))
                    suggestions = autoType;
                consoleInSuggestions.setText(existingToSpaces(suggestions));
            } else {
                consoleInSuggestions.setText(existingToSpaces(suggestions));
            }
        });

        consoleInWrapper = new StackPane();
        consoleInWrapper.getChildren().addAll(consoleIn,consoleInSuggestions);
        consoleInWrapper.setAlignment(Pos.CENTER_LEFT);
        consoleInSuggestions.setTranslateX(fontSize/2+4);
        consoleInSuggestions.setMouseTransparent(true);
        VBox consoleBox = new VBox();

        consoleBox.getChildren().addAll(consoleOut,consoleInWrapper);




        borderPane = new BorderPane();
        borderPane.setBottom(consoleBox);
        borderPane.setCenter(procTable);
        borderPane.setLeft(controlPane);

        inputStream = new PipedInputStream(commands);


        outputSB = new StringBuilder();
        outputStream = new OutputStream(){
            public void write(int b) throws IOException {
                Platform.runLater(() -> {
                    outputSB.append((char) b);
                    if(((char) b) == '\n')
                        moveSBToConsole();
                });
            }
        };

        Scene scene = new Scene (borderPane);

        primaryStage.setTitle("The Console");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> System.exit(0));

        consoleOut.appendText("Welcome to the OS-Simulator Shell. Please type one of the following commands in the box below.\n");
        consoleOut.appendText(Arrays.toString(shell.commands) + "\n");
        consoleIn.requestFocus();
        Platform.runLater(() -> {
            shell = new Shell(inputStream,outputStream);
            reset();    //Initialize defaults
            shell.setLineFinishedListener((s,b)-> enableInput(s,b));
            shell.setCycleFinishedListener((d,c) -> updateGraphics(d,c));
            shell.start();
        });
    }
    private void enableInput(String suggestion, boolean setAutoSuggestOn){
        Platform.runLater( () -> {
            moveSBToConsole();
            consoleIn.setDisable(false);
            consoleIn.requestFocus();

            suggestions = suggestion;
            consoleInSuggestions.setText(suggestion);

            autoSuggestOn = setAutoSuggestOn;
        });
    }
    private void disableInput(){
        consoleIn.setDisable(true);
    }
    private void updateGraphics(ObservableList<ProcessControlBlock> pcbs, long currentCycle){
        Platform.runLater( () -> {
            //Update memory graph
            int totalMemory = 0;
            int currentMemory = 0;
            for(ProcessControlBlock pcb : pcbs){
                if(pcb.state != ProcessState.NEW && pcb.state != ProcessState.TERMINATED) {
                    if (pcb.state != ProcessState.STANDBY)
                        currentMemory += pcb.getMemoryAllocation();
                    totalMemory += pcb.getMemoryAllocation();
                }
            }

            memoryUsageData.getData().addAll(new AreaChart.Data(currentCycle, currentMemory));
            swapUsageData.getData().addAll(new AreaChart.Data(currentCycle, totalMemory));

            if(memoryUsageData.getData().size()>65)
                memoryUsageData.getData().remove(0);
            if(swapUsageData.getData().size()>65)
                swapUsageData.getData().remove(0);

            if(currentCycle > 60) {
                memoryXAxis.setUpperBound(currentCycle-1);
                memoryXAxis.setLowerBound(currentCycle-61);
                memoryLimitData.setData(FXCollections.observableArrayList(
                        new AreaChart.Data(currentCycle-61,shell.cpu.memory),
                        new AreaChart.Data(currentCycle-1,shell.cpu.memory)
                ));
            }
            if(currentCycle == -1){
                reset();
            }

            //Reset Memory Graph
//            if(currentCycle == 0){
//                memoryUsageData = new AreaChart.Series("Memory Usage", FXCollections.observableArrayList());
//                swapUsageData = new AreaChart.Series("Total Memory Usage (incl. swapped)", FXCollections.observableArrayList());
//                memoryLimitData = new AreaChart.Series("Memory Limit", FXCollections.observableArrayList(
//                        new AreaChart.Data(0,shell.cpu.memory),
//                        new AreaChart.Data(60,shell.cpu.memory)
//                ));
//            }

            //Update Table
            procTableData.removeAll(procTableData);
            for(int i = 0; i < pcbs.size(); i++){
                procTableData.add(pcbs.get(i));
            }
            procTable.setItems(procTableData);
        });
    }
    private int getDelayFromDelaySliderValue(double n){
        int delay;
        if (n < 0.5) delay = 0;
        else if (n < 1.5) delay = 10;
        else if (n < 2.5) delay = 50;
        else if (n < 3.5) delay = 100;
        else if (n < 4.5) delay = 1000;
        else delay = -1;
        return delay;
    }
    private void moveSBToConsole(){
        if(outputSB.length()>0) {
            consoleOut.appendText(outputSB.toString());
            outputSB = new StringBuilder();
            consoleOut.setScrollTop(consoleOut.getHeight());
        }
    }
    private String existingToSpaces(String input){
        StringBuilder sb = new StringBuilder(input);
        for(int i = 0; i < consoleIn.getLength(); i++){
            if(sb.length() > i){
                sb.replace(i,i+1," ");
            }
        }
        return sb.toString();
    }
    private void reset(){
        memoryXAxis.setUpperBound(60);
        memoryXAxis.setLowerBound(0);
        memoryUsageData.setData(FXCollections.observableArrayList(new AreaChart.Data(0,0)));
        swapUsageData.setData(FXCollections.observableArrayList(new AreaChart.Data(0,0)));
        memoryLimitData.setData(FXCollections.observableArrayList(
                new AreaChart.Data(0,shell.cpu.memory),
                new AreaChart.Data(60,shell.cpu.memory)
        ));
        shell.sleepDelay = getDelayFromDelaySliderValue(delaySlider.getValue());
    }
    /**
     * Return a list of all the mono-spaced fonts on the system.
     *
     * @return An observable list of all of the mono-spaced fonts on the system.
     */
    //From http://clarkonium.net/2015/07/finding-mono-spaced-fonts-in-javafx/
    private ObservableList<String> getMonoFontFamilyNames() {

        // Compare the layout widths of two strings. One string is composed
        // of "thin" characters, the other of "wide" characters. In mono-spaced
        // fonts the widths should be the same.

        final Text thinTxt = new Text("1 l"); // note the space
        final Text thikTxt = new Text("MWX");

        List<String> fontFamilyList = Font.getFamilies();
        List<String> monoFamilyList = new ArrayList<>();

        Font font;

        for (String fontFamilyName : fontFamilyList) {
            font = Font.font(fontFamilyName, FontWeight.NORMAL, FontPosture.REGULAR, 14.0d);
            thinTxt.setFont(font);
            thikTxt.setFont(font);
            if (thinTxt.getLayoutBounds().getWidth() == thikTxt.getLayoutBounds().getWidth()) {
                monoFamilyList.add(fontFamilyName);
            }
        }

        return FXCollections.observableArrayList(monoFamilyList);
    }

}
