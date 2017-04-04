package game.tictactoe;

import framework.interfaces.Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 *
 * @author Mark
 */
public class TicTacToeController implements Controller{
    @FXML private GridPane grid;
    private Model model;
    private char currentTurn;
    private String currentWinner;

    public TicTacToeController() {
        this.model = new Model();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("tictactoe/FXML.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        currentTurn = 'x';
    }

    private void doMove(char currentTurn, int row, int column) {

            model.setSymbol(row, column, currentTurn);
            updateBoard(model.getBoard(), grid);
        switchTurns(currentTurn);
    }

    //public boolean checkLegalMove() {
    //    return true;
    //}

    private void switchTurns(char Turn) {
        if (Turn == 'x') {
            currentTurn = 'o';
        }
        else {
            currentTurn = 'x';
        }
    }

    private void createLine(double beginX, double endX, double beginY, double endY, int column, int row, GridPane grid) {
        Line line = new Line();
        line.setStartX(beginX);
        line.setStartY(endX);
        line.setEndX(beginY);
        line.setEndY(endY);
        grid.add(line, column, row);
    }

    private void drawO(int row, int column, GridPane grid) {
        Circle c1 = new Circle(50,50,100);
        c1.setStroke(Color.BLACK);
        c1.setFill(null);
        c1.setStrokeWidth(3);
        grid.add(c1, column , row);
    }

    private void drawX(int row, int column, GridPane grid) {
        double x = 166;
        double y = 125;
        createLine(x - 100, y - 100, x + 100, y + 100, column, row, grid);
        createLine(x + 100, y - 100, x - 100, y + 100, column, row, grid);
    }

    private void updateBoard(char board[][], GridPane grid) {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(board[i][j] == 'o') {
                    drawO(i,j,grid);
                }
                if(board[i][j] == 'x') {
                    drawX(i,j,grid);
                }
            }
        }

    }

    //public boolean checkWinner(char Board[][]) {
    //    currentWinner = "X";
    //    return true;
    //}

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

    @Override
    public String getLocation() {
        return "../game/tictactoe/FXML.fxml";
    }
}