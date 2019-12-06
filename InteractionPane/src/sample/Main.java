package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * The standalone JavaFX application for interaction pane.
 * We have this class for you to test the basic functionalities without taking the trouble of
 * doing all the configurations and building the plugin as an ant project.
 * @author Yihe Guo, Yue Shu
 */
public class Main extends Application {

    private TextArea cmdTextArea;
    private TextArea outputTextArea;
    private List<String> prevInput;
    private  int loc;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle("Interaction Pane");
        final BorderPane pane = new BorderPane();

        cmdTextArea = new TextArea();
        cmdTextArea.setPromptText("> ");

        //output
        outputTextArea = new TextArea();
        outputTextArea.setEditable(false);


        prevInput = new ArrayList<>();

        ExampleJShell es = new ExampleJShell();

        // TODO: change the line below to a path to actual java file for testing
        es.evaluate("");
        loc = -1;

        cmdTextArea.setOnKeyPressed(action -> {
            if (action.getCode() == KeyCode.UP && loc >= 0) {
                cmdTextArea.setText(prevInput.get(loc));
                loc--;
            }
            if (action.getCode() == KeyCode.DOWN && loc < prevInput.size() - 1) {
                cmdTextArea.setText(prevInput.get(loc + 1));
                loc++;
            }
            if (action.getCode() == KeyCode.ENTER) {
                String input = cmdTextArea.getText();
                // truncate the leading line
                while (input.length() > 0 && input.charAt(0) == '\n') {
                    input = input.substring(1);
                }

                // truncate the tailing line
                while (input.length() > 0 && input.charAt(input.length() - 1) == '\n') {
                    input = input.substring(0, input.length() - 1);
                }
                System.out.println("the input is: " + input);

                if (input.length() < 1) {
                    cmdTextArea.clear();
                    return;
                }


                outputTextArea.appendText("> " + input + "\n");
                String output = es.useJshell(input + ";");
                if (output.length() > 0) {
                    outputTextArea.appendText(output + "\n");

                }
                prevInput.add(input);
                cmdTextArea.clear();
                loc = prevInput.size() - 1;
                System.out.println("loc is " + loc);
            }
        });

        pane.setCenter(cmdTextArea);
        pane.setBottom(outputTextArea);

        final Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}

