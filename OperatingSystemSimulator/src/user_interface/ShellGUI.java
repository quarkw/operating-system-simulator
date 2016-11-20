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

import java.io.IOException;
import java.io.OutputStream;

public class ShellGUI extends Application {

    public TextArea consoleOut;
    public TextField consoleIn;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
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
        consoleOut  = new TextArea("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi cursus pretium metus, Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi cursus pretium metus, Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi cursus pretium metus, Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi cursus pretium metus, Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi cursus pretium metus, Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi cursus pretium metus, Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi cursus pretium metus, Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi cursus pretium metus, Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi cursus pretium metus, Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi cursus pretium metus, Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi cursus pretium metus, Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi cursus pretium metus, sed facilisis ipsum gravida sit amet. Vivamus fringilla dignissim posuere. Nam accumsan tempus sem non dignissim. Aliquam porta odio erat, et suscipit velit venenatis vitae. Fusce a metus at arcu lobortis pharetra eget non massa. Vestibulum sit amet congue ante, et bibendum mauris. Integer nec nunc quis leo cursus placerat vel eu nisl. Sed et nisl nec magna fermentum fringilla. Phasellus dignissim non turpis sit amet tincidunt. Pellentesque cursus magna nisl, in efficitur nulla ornare sed. ");
        consoleOut.setWrapText(true);
        consoleOut.setEditable(false);

        consoleOut.setStyle("-fx-border-color: black");
        consoleIn = new TextField();
        VBox consoleBox = new VBox();
        consoleBox.setStyle("-fx-border-color: black");
        consoleBox.getChildren().addAll(consoleOut,consoleIn);

        BorderPane borderPane = new BorderPane();
        borderPane.setBottom(consoleBox);

        OutputStream outputStream = new OutputStream(){
            public void write(int b) throws IOException {
                Platform.runLater( () -> consoleOut.appendText(String.valueOf((char) b)));
            }
        };

        Scene scene = new Scene (borderPane);

        primaryStage.setTitle("The Console");
        primaryStage.setScene(scene);
        primaryStage.show();

        consoleOut.appendText("This is appending");

        Platform.runLater(() -> {
            try {
                Shell shell = new Shell(System.in, outputStream);
                shell.readLines();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    public void asyncWriteToConsole(String s){
        Platform.runLater( () -> consoleOut.appendText(s));
    }

}
