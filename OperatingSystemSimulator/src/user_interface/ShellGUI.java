package user_interface;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import kernel.ProcessControlBlock;
import kernel.ProcessState;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ShellGUI extends Application {

    private Shell shell;

    BorderPane borderPane;

    TableView procTable;
    ObservableList procTableData;
    TableColumn pIDColumn, programNameColumn, memoryAllocationColumn, programCounterColumn, cpuUsedColumn, processStateColumn;

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
                    switch(state){
                        case NEW:
                            style = "-fx-background-color: white";
                            break;
                        case READY:
                            style = "-fx-background-color: cornflowerblue";
                            break;
                        case WAITING:
                            style = "-fx-background-color: khaki";
                            break;
                        case RUNNING:
                            style = "-fx-background-color: lawngreen";
                            break;
                        case TERMINATED:
                            style = "-fx-background-color: hotpink";
                            break;
                        case STANDBY:
                            style = "-fx-background-color: darkorange";
                            break;
                        default:
                            break;
                    }
                    for(int i = 0; i < getChildren().size(); i++){
                        Labeled children = (Labeled) getChildren().get(i);
                        children.setStyle(style);
                    }
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

        Scene scene = new Scene (borderPane,800,600);

        primaryStage.setTitle("The Console");
        primaryStage.setScene(scene);
        primaryStage.show();

        consoleOut.appendText("Welcome to the OS-Simulator Shell. Please type one of the following commands in the box below.\n");
        consoleOut.appendText(Arrays.toString(shell.commands) + "\n");
        consoleIn.requestFocus();
        Platform.runLater(() -> {
            shell = new Shell(inputStream,outputStream);
            shell.setLineFinishedListener((s,b)-> enableInput(s,b));
            shell.setCycleFinishedListener((d) -> updateTable(d));
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
    private void updateTable(ObservableList<ProcessControlBlock> data){
        Platform.runLater( () -> {
            procTableData.removeAll(procTableData);
            for(int i = 0; i < data.size(); i++){
                procTableData.add(data.get(i));
            }
            procTable.setItems(procTableData);
        });
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
