package user_interface;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.LinkedList;

public class ShellGUI extends Application {

    private Shell shell;

    public TextArea consoleOut;
    public TextField consoleIn;
    private PipedOutputStream commands;
    private PipedInputStream inputStream;
    private OutputStream outputStream;

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
        consoleOut  = new TextArea("");
        consoleOut.setWrapText(true);
        consoleOut.setEditable(false);

        commands = new PipedOutputStream();
        consoleIn = new TextField();
        consoleIn.setOnAction ( (e) -> {
            if(consoleIn.getText().equals(""))
                consoleIn.setText(shell.getLastInput());
            else {
                try {
                    commands.write(consoleIn.getText().getBytes());
                    commands.write("\n".getBytes());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                consoleIn.setText("");
            }
        });
//        consoleIn.setOnKeyPressed( (e) -> System.out.println(e));
        VBox consoleBox = new VBox();

        consoleBox.getChildren().addAll(consoleOut,consoleIn);

        BorderPane borderPane = new BorderPane();
        borderPane.setBottom(consoleBox);

        inputStream = new PipedInputStream(commands);

        outputStream = new OutputStream(){
            public void write(int b) throws IOException {
                Platform.runLater( () -> consoleOut.appendText(String.valueOf((char) b)));
            }
        };

        Scene scene = new Scene (borderPane,600,600);

        primaryStage.setTitle("The Console");
        primaryStage.setScene(scene);
        primaryStage.show();

        consoleOut.appendText("Welcome to the OS-Simulator Shell. Please type your commands in the box below.\n");
        consoleIn.requestFocus();
        Platform.runLater(() -> {
            shell = new Shell(inputStream,outputStream);
            shell.start();
        });
    }
}
