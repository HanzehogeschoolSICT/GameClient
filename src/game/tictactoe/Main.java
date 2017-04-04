package game.tictactoe;

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

    @Override
    public void start(Stage stage) {
        try {
            scene = FXMLLoader.load(getClass().getResource("FXML.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}

