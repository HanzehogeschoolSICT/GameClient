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
    public char currentTurn;

    public Controller() {
        this.model = new Model();
        this.view = new View();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("tictactoe/FXML.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        currentTurn = 'x';
    }

    public void doMove(char currentTurn, int row, int column) {
        model.setSymbol(row, column, currentTurn);
        view.updateBoard(model.getBoard(), grid);
        switchTurns(currentTurn);
    }

    public void switchTurns(char Turn) {
        if (Turn == 'x') {
            currentTurn = 'o';
        }
        else {
            currentTurn = 'x';
        }
    }

    @FXML
    public void squareClicked1() { doMove(currentTurn, 0, 0 ); ;
    }
    @FXML
    public void squareClicked2() { doMove(currentTurn, 0, 1 );
    }
    @FXML
    public void squareClicked3() { doMove(currentTurn, 0, 2 );
    }
    @FXML
    public void squareClicked4() { doMove(currentTurn, 1, 0 );
    }
    @FXML
    public void squareClicked5() { doMove(currentTurn, 1, 1 );
    }
    @FXML
    public void squareClicked6() { doMove(currentTurn, 1, 2 );
    }
    @FXML
    public void squareClicked7() { doMove(currentTurn, 2, 0 );
    }
    @FXML
    public void squareClicked8() { doMove(currentTurn, 2, 1 );
    }
    @FXML
    public void squareClicked9() { doMove(currentTurn, 2, 2 );
    }


}