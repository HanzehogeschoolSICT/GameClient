package game.tictactoe;

import framework.interfaces.Controller;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
    private String opponent;
    private Integer difficulty;
    
    @FXML
    private GridPane winLoseGrid;
    @FXML
    private Label currentTurnText;

    public TicTacToeController(char currentTurn, String opponent, Integer difficulty) {
        this.model = new Model();
        this.currentTurn = currentTurn;
        this.opponent = opponent;
        this.difficulty = difficulty;
    }

    private void doMove(char currentTurn, int column, int row) {
        
        if (checkLegalMove(column, row) && !isWinner) {
            model.setSymbol(column, row, currentTurn);
            updateBoard(model.getBoard(), grid);
            checkWinner();
            if(!isWinner){
                switchTurns(currentTurn);
            }
        }
        if (isWinner) {
            showWinLoseGrid();
            System.out.println("We hebben een winnaar! De winnaar is: " + currentTurn);
        }
    }

    public boolean checkLegalMove(int column, int row) {
        char[][] board = model.getBoard();
        if(board[column][row] == 'x' || board[column][row] == 'o') {
            return false;
        }
        else {
            return true;
        }

    }

    private void switchTurns(char Turn) {
        if (Turn == 'x') {
            currentTurn = 'o';
            currentTurnText.setText("O");
        } else {
            currentTurn = 'x';
            currentTurnText.setText("X");
        }
	new AI(model, currentTurn).nextMove();
    }

    private void createLine(double beginX, double endX, double beginY, double endY, int column, int row, GridPane grid) {
        Line line = new Line();
        line.setStartX(beginX);
        line.setStartY(endX);
        line.setEndX(beginY);
        line.setEndY(endY);
        grid.add(line, column, row);
    }

    private void drawO(int column, int row, GridPane grid) {
        Circle c1 = new Circle(50, 50, 100);
        c1.setStroke(Color.BLACK);
        c1.setFill(null);
        c1.setStrokeWidth(3);
        grid.add(c1, column, row);
    }

    private void drawX(int column, int row, GridPane grid) {
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

    public void checkWinner() {
        if (model.getWinner() != ' ') {
            isWinner = true;
        }
    }
    
    public void showWinLoseGrid(){
        ObservableList<Node> children = winLoseGrid.getChildren(); 
        for(Node n : children){
            if(n instanceof Label){
                ((Label) n).setText(currentTurn + " won!");
            }
        }
        winLoseGrid.setVisible(true);
    }

    @FXML
    public void squareClicked(MouseEvent event) {
        Label l = (Label) event.getSource();
        doMove(currentTurn, GridPane.getColumnIndex(l), GridPane.getRowIndex(l));
    }

    @Override
    public String getLocation() {
        return "../game/tictactoe/TicTacToeBoard.fxml";
    }
}