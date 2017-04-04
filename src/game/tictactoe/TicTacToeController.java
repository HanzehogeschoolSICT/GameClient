package game.tictactoe;

import framework.interfaces.Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 *
 * @author Mark
 */
public class TicTacToeController implements Controller {
    @FXML private GridPane grid;
    private Model model;
    private char currentTurn;
    private boolean isWinner = false;

    public TicTacToeController(char currentTurn) {
        this.model = new Model();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("tictactoe/FXML.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        this.currentTurn = currentTurn;
    }

    private void doMove(char currentTurn, int row, int column) {
        if(!isWinner) {
            if (checkLegalMove(row, column) && !isWinner) {
                model.setSymbol(row, column, currentTurn);
                updateBoard(model.getBoard(), grid);
                switchTurns(currentTurn);
                checkWinner(currentTurn);
            }
            if (isWinner) {
                System.out.println("We hebben een winnaar! De winnaar is: " + currentTurn);
            }
        }

    }

    public boolean checkLegalMove(int row, int column) {
        char[][] board = model.getBoard();
        if(board[row][column] == 'x' || board[row][column] == 'o') {
            return false;
        }
        else {
            return true;
        }

    }

    private void switchTurns(char Turn) {
        if (Turn == 'x') {
            currentTurn = 'o';
        } else {
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
        Circle c1 = new Circle(50, 50, 100);
        c1.setStroke(Color.BLACK);
        c1.setFill(null);
        c1.setStrokeWidth(3);
        grid.add(c1, column, row);
    }

    private void drawX(int row, int column, GridPane grid) {
        double x = 166;
        double y = 125;
        createLine(x - 100, y - 100, x + 100, y + 100, column, row, grid);
        createLine(x + 100, y - 100, x - 100, y + 100, column, row, grid);
    }

    private void updateBoard(char board[][], GridPane grid) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 'o') {
                    drawO(i, j, grid);
                }
                if (board[i][j] == 'x') {
                    drawX(i, j, grid);
                }
            }
        }

    }

    public void checkWinner(char currentTurn) {
        char[][] board = model.getBoard();
        // Check horizontal and vertical for winners
        for (int n = 0; n < 3; n += 2) {
            int hor = 0;
            int ver = 0;
            for (int i = 0; i < 3; i++) {
                if (board[n][i] == currentTurn) hor++;
                if (board[i][n] == currentTurn) ver++;
                if (hor == 3|| ver == 3) isWinner = true;
            }

        }

        for(int n = 2; n > -1; n--) {
            int diag1 = 0;
            int diag2 = 0;
            for (int i = 0; i < 3; i++) {
                if (board[i][i] == currentTurn) diag1++;
                if (board[i][n] == currentTurn) diag2++;
                if (diag1 == 3|| diag2 == 3) isWinner = true;
            }
        }

    }


    @FXML
    public void squareClicked(MouseEvent event) {
        Label l = (Label) event.getSource();
        doMove(currentTurn, GridPane.getRowIndex(l),GridPane.getColumnIndex(l));
    }

    @Override
    public String getLocation() {
        return "../game/tictactoe/FXML.fxml";
    }
}