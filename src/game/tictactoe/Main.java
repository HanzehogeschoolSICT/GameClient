package game.tictactoe;

import framework.MessageBus;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *
 * @author Mark
 */
public class Main extends Application {
    Scene scene;
    Controller controller;

    @Override
    public void start(Stage stage) {
        try {
            scene = FXMLLoader.load(getClass().getResource("FXML.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        MessageBus bus = MessageBus.getBus();
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}

