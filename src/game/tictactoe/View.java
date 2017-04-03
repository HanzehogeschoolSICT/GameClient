package game.tictactoe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *
 * @author Mark
 */
public class View extends Application {
    Scene scene;


    @Override
    public void start(Stage stage) throws IOException {
        scene = FXMLLoader.load(getClass().getResource("FXML.fxml"));
        stage.setScene(scene);
        stage.show();
    }

    private void createLine(double beginX, double endX, double beginY, double endY, GridPane grid) {
        Line line = new Line();
        line.setStartX(beginX);
        line.setStartY(endX);
        line.setEndX(beginY);
        line.setEndY(endY);
        grid.getChildren().addAll(line);
    }

    public void drawO(int row, int column, GridPane grid) {
        int x = 166 + (column * 333);
        int y = 125 + (row * 250);
        Circle c1 = new Circle(x,y,100);
        c1.setStroke(Color.BLACK);
        c1.setFill(null);
        c1.setStrokeWidth(3);
        grid.getChildren().addAll(c1);
        }

    public void drawX(int row, int column, GridPane grid) {
        double x = 166 + (column * 333);
        double y = 125 + (row * 250);
        createLine(x - 100, y - 100, x + 100, y + 100, grid);
        createLine(x + 100, y - 100, x - 100, y + 100, grid);
    }


    }

