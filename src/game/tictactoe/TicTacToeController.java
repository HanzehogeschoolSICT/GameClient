package game.tictactoe;

import framework.GameClient;
import framework.GameSelectMenu;
import framework.MessageBus;
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
import java.awt.Point;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.util.Duration;


/**
 *
 * @author Mark
 */
public class TicTacToeController implements Controller, Initializable {
    @FXML private GridPane grid;
    private Model model;
    private char currentTurn;
    private boolean isWinner = false;
    private String opponent;
    private char winner;
    private Integer difficulty;
    private char playSymbol;
    private int xWon;
    private int oWon;
    private boolean winnerDisplayed = false;
    private final int STARTTIME = 10;
    private int remainSec = STARTTIME;
    private Timeline timeline;
    
    @FXML private Label xScore;
    @FXML private Label oScore;
    @FXML private GridPane winLoseGrid;
    @FXML private Label currentTurnText;
    @FXML Label timer;

    public TicTacToeController(char currentTurn, String opponent, Integer difficulty) {
        this.model = new Model();
        this.currentTurn = currentTurn;
        this.playSymbol = currentTurn;
        this.opponent = opponent;
        this.difficulty = difficulty;
        xWon = 0;
        oWon = 0;
        setTimer();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        String loc = location.toString();
        if(loc.endsWith("SidebarGameMenuFXML.fxml")){
            updateScore();
            if(playSymbol == 'x') 
                currentTurnText.setText("X");
            else
                currentTurnText.setText("O");
        }
    }
    
    private void doMove(char currentTurn, int column, int row) {
        
        if (checkLegalMove(column, row) && !isWinner) {
            remainSec = STARTTIME;
            model.setSymbol(column, row, currentTurn);
            updateBoard(model.getBoard(), grid);
            checkWinner();
            switchTurns(currentTurn);
        }
        if (isWinner) {
            showWinLoseGrid();
            updateScore();
            System.out.println("We hebben een winnaar! De winnaar is: " + winner);
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
        if("AI".equals(opponent)){
            if(currentTurn != playSymbol && !isWinner){
                AI ai = new AI(model, currentTurn);
                Point p = ai.nextMove();
                doMove(currentTurn, p.x, p.y);
            }
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
            winner = model.getWinner();
        }
        
    }
    
    private void setTimer() {
        timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ae -> countDown()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
    
    private void countDown() {
        remainSec--;

        if(remainSec == 0 || remainSec < 0) {
            createDialog("Afgelopen", "Helaas, jou zet duurde helaas te lang");
            isWinner = true;
            timeline.stop();
        }
        if(isWinner) {
            timeline.stop();
            showWinLoseGrid();
        }
        timer.setText("00:0" + remainSec);
    }
    
    // Update de huidige score
    private void updateScore(){
        xScore.setText(xWon + "");
        oScore.setText(oWon + "");
    }
    
    // Show play again, quit and who won when game finishes
    public void showWinLoseGrid(){
        ObservableList<Node> children = winLoseGrid.getChildren(); 
        for(Node n : children){
            if(n instanceof Label){
                if(winner == 'd'){
                    ((Label) n).setText("No-one won!");
                }
                else{
                    ((Label) n).setText(winner + " won!");
                    // Keep up the score
                    if(!winnerDisplayed){
                        winnerDisplayed = true;
                        if(winner == 'x')
                            xWon++;
                        else
                            oWon++;
                    }
                }
            }
        }
        winLoseGrid.setVisible(true);
    }
    
    public void createDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }

    @FXML
    public void squareClicked(MouseEvent event) {
        Label l = (Label) event.getSource();
        doMove(currentTurn, GridPane.getColumnIndex(l), GridPane.getRowIndex(l));
    }
    
    // Start a new game with the same settings
    @FXML
    public void handlePlayAgainButton(){
        isWinner = false;
        currentTurn = playSymbol;
        model = new Model();
        winnerDisplayed = false;
        GameClient.load(this, "CENTER");
        GameClient.load(this, "LEFT", "../game/tictactoe/SidebarGameMenuFXML.fxml");
    }
    
    // Go back to home
    @FXML
    public void handleQuitButton(){
        Controller ctrl = new GameSelectMenu();
        GameClient.load(ctrl, "LEFT", ctrl.getLocation());
        MessageBus.getBus().call("CLIENT", "display home", null);
    }

    @Override
    public String getLocation() {
        return "../game/tictactoe/TicTacToeBoard.fxml";
    }
}