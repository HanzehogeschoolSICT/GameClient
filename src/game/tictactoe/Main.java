package game.tictactoe;

import framework.MessageBus;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Mark
 */
public class Main extends Application {
    Scene scene;

    @Override
    public void start(Stage stage) {

        MessageBus bus = MessageBus.getBus();
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}

