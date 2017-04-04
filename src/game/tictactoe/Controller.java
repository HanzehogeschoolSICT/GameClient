package game.tictactoe;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Mark
 */
public class Controller {
    @FXML private GridPane grid;
    private Model model;
    private View view;
    int xMouse;
    int yMouse;
    public Controller() {
        this.model = new Model();
        this.view = new View();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "FXML.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);


    }
    @FXML
    public void squareClicked1() {
       view.drawX(0,0,grid);
    }
    @FXML
    public void squareClicked2() {
        view.drawO(0,1,grid);
    }
    @FXML
    public void squareClicked3() {
        view.drawO(0,2,grid);
    }
    @FXML
    public void squareClicked4() {
        view.drawO(1,0,grid);
    }
    @FXML
    public void squareClicked5() {
        view.drawO(1,1,grid);
    }
    @FXML
    public void squareClicked6() {
        view.drawO(1,2,grid);
    }
    @FXML
    public void squareClicked7() {
        view.drawO(2,0,grid);
    }
    @FXML
    public void squareClicked8() {
        view.drawO(2,1,grid);
    }
    @FXML
    public void squareClicked9() {
        view.drawO(2,2,grid);
    }


}