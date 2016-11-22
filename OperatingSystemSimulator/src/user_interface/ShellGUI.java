package user_interface;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;

public class ShellGUI extends Application {

    private Shell shell;

    public TextArea consoleOut;
    public TextField consoleIn;
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


        //Right Pane (Process List)


        //Bottom Pane (Console)
        consoleOut  = new TextArea("");
        consoleOut.setWrapText(true);
        consoleOut.setEditable(false);

        commands = new PipedOutputStream();
        consoleIn = new TextField();
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

            if(e.getCode().equals(KeyCode.UP)){
                consoleIn.setText(shell.getPreviousInput());
                consoleIn.positionCaret(consoleIn.getLength());
                e.consume();
            } else if (e.getCode().equals(KeyCode.DOWN)){
                consoleIn.setText(shell.getNextInput());
                consoleIn.positionCaret(consoleIn.getLength());
                e.consume();
            }

        });
        VBox consoleBox = new VBox();

        consoleBox.getChildren().addAll(consoleOut,consoleIn);




        BorderPane borderPane = new BorderPane();
        borderPane.setBottom(consoleBox);

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

        Scene scene = new Scene (borderPane,600,600);

        primaryStage.setTitle("The Console");
        primaryStage.setScene(scene);
        primaryStage.show();

        consoleOut.appendText("Welcome to the OS-Simulator Shell. Please type one of the following commands in the box below.\n");
        consoleOut.appendText(Arrays.toString(shell.commands));
        consoleOut.appendText("\n");
        consoleIn.requestFocus();
        Platform.runLater(() -> {
            shell = new Shell(inputStream,outputStream);
            shell.setLineFinishedListener(()-> enableInput());
            shell.start();
        });
    }
    private void enableInput(){
        Platform.runLater( () -> {
            consoleIn.setDisable(false);
            consoleIn.requestFocus();
        });
    }
    private void disableInput(){
        consoleIn.setDisable(true);
    }
    private void moveSBToConsole(){
        consoleOut.appendText(outputSB.toString());
        consoleOut.setScrollTop(consoleOut.getHeight());
        outputSB = new StringBuilder();
    }
}
