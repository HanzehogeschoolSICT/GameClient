package game.reversi;

import framework.GameClient;
import framework.GameSelectMenu;
import framework.MessageBus;
import framework.interfaces.Controller;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import java.awt.Point;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * Created by markshizzle on 6-4-2017.
 */
public class ReversiController implements Controller {
    @FXML GridPane grid;
    @FXML TextFlow flow;
    @FXML Label timer;
    @FXML GridPane winLoseGrid;
    @FXML Label scoreB;
    @FXML Label scoreW;
    @FXML Label turn;

    private Text text;
    private Model model;
    private char currentTurn;
    private boolean[][] legalMovesW = new boolean[8][8];
    private boolean[][] legalMovesB = new boolean[8][8];
    private final int STARTTIME = 10;
    private int remainSec = STARTTIME;
    private boolean endGame;
    private String winner;
    private int totalW = 0;
    private int totalB = 0;
    private int amountLegalMovesB;
    private int amountLegalMovesW;
    private Timeline timeline;
    private char playerColor;
    private char ai = ' ';


    public ReversiController(char currentTurn) {
        this.playerColor = currentTurn;
        this.currentTurn = currentTurn;
        newGame();
    }
    
    private void newGame(){
        this.model = new Model();
        remainSec = STARTTIME;
        totalW = 0;
        totalB = 0;
        endGame = false;
        legalMovesW = new boolean[8][8];
        legalMovesB = new boolean[8][8];
        setTimer();
    }
    
    public void setAI(boolean b) {
        if (b) {
            if (currentTurn == 'b') ai = 'w';
            else ai = 'b';
        }
    }

    public void doMove(int column, int row) {
        if(legalMove(column, row, currentTurn, true)) {
            remainSec = STARTTIME;
            model.setSymbol(column, row, currentTurn);
            changeTurns();
            drawBoard();
        }
        else {
            createDialog("Helaas", "Helaas het is niet mogelijk om deze zet te doen.");
            return;
        }

        calcPoints();
        // Berekent de volgende legale zetten
        getLegalMoves();
        // Als er van beide kanten geen zetten meer mogelijk zijn is het einde van het spel bereikt.
        if(amountLegalMovesB == 0 && amountLegalMovesW == 0) {
            endGame = true;
            checkWinner();
            showWinLoseGrid();
            createDialog("Gefeliciteerd!", "Het spel is afgelopen. De winnaar is: " + winner);
            return;
        }
        // Als de huidige kleur niet kan, wordt de beurt doorgegeven.
        if (!checkIfLegalMove(currentTurn)) {
            changeTurns();
            createDialog("Geen mogelijke zetten", "Er zijn geen mogelijke zetten meer, de beurt wordt omgedraaid.");
            return;
        }

        if (currentTurn == ai) {
            Point m = new AI(model, ai).nextMove();
            doMove(m.x, m.y);
        }
    }
    private boolean checkIfLegalMove(char currentTurn) {
        if(currentTurn == 'b') {
            if(amountLegalMovesB == 0) {
                return false;
            }
        }

        if(currentTurn == 'w') {
            if (amountLegalMovesW == 0) {
                return false;
            }
        }
        return true;

    }
    private void checkWinner() {
        if(totalW > totalB) {
            winner = "Wit";
        }
        else if(totalW < totalB) {
            winner = "Black";
        }
        else {
            // 'g' staat voor gelijkspel.
            winner = "Niemand";
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
            endGame = true;
            timeline.stop();
        }
        if(endGame) {
            timeline.stop();
            showWinLoseGrid();
        }
        timer.setText("00:0" + remainSec);
    }

     void drawBoard() {
         for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (model.getSymbol(i,j) == 'b') {
                        drawO('b', i, j);
                    }
                    if (model.getSymbol(i,j) == 'w') {
                        drawO('w', i, j);
                    }
                }
            }

        }

    private void calcPoints() {
        totalW = 0;
        totalB = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (model.getSymbol(i, j) == 'w') {
                    totalW++;
                }
                else if(model.getSymbol(i,j) == 'b') {
                    totalB++;
                }
            }
        }
        scoreB.setText("" + totalB);
        scoreW.setText("" + totalW);
    }

    private void drawO(char colour, int column, int row) {
        Circle c1 = new Circle(0, 0, 38);
        c1.setStroke(Color.BLACK);
        if(colour == 'b') {
            c1.setFill(Color.BLACK);
        }
        if(colour == 'w') {
            c1.setFill(Color.WHITE);
        }
        c1.setStrokeWidth(3);
        grid.add(c1,column,row);

    }

    public void createDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }

    public void getLegalMoves() {
        amountLegalMovesB = 0;
        amountLegalMovesW = 0;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                legalMovesW[i][j] = legalMove(i,j,currentTurn,false);
                if(legalMove(i,j,'w',false)) {
                    amountLegalMovesW++;
                }

                legalMovesB[i][j] = legalMove(i,j,currentTurn,false);
                if(legalMove(i,j,'b',false)) {
                    amountLegalMovesB++;
                }
            }
        }
    }

    public boolean legalMove(int r, int c, char color, boolean flip) {
        boolean legal = false;
        char oppSymbol;

        // Als de cel leeg is begin met zoeken
        // Als de cel niet leeg is wordt het afgebroken
        if (model.getSymbol(r,c) != 'w' && model.getSymbol(r,c) != 'b')
        {
            // Initialize variables
            int posX;
            int posY;
            boolean found;
            char current;

            // Zoekt in elke richting
            // x en y beschrijven alle richtingen
            for (int x = -1; x <= 1; x++)
            {
                for (int y = -1; y <= 1; y++) {
                    //Variabelen om de positie bij te houden en of het een valid zet is.
                    posX = c + x;
                    posY = r + y;
                    found = false;
                    if (posX > -1 && posX < 8 && posY > -1 && posY < 8) {
                        current = model.getSymbol(posY,posX);
                        oppSymbol = color == 'b' ? 'w' : 'b';

                        if (current != oppSymbol) {
                            continue;
                        }

                        while (!found) {
                            posX += x;
                            posY += y;
                            if (posX < 0 || posX > 7 || posY < 0 || posY > 7) {
                                found = true;
                            }
                            else {
                                current = model.getSymbol(posY, posX);
                                oppSymbol = currentTurn == 'b' ? 'w' : 'b';

                                if (current == color) {
                                    found = true;
                                    legal = true;

                                    // Als flip op true staat mag er omgedraaid worden.
                                    // De algoritme draait dan om een gaat alles omdraaien
                                    // tot de oorspronkelijke positie bereikt is.
                                    if (flip) {
                                        posX -= x;
                                        posY -= y;
                                        if (posX > -1 && posX < 8 && posY > -1 && posY < 8) {
                                            current = model.getSymbol(posY, posX);
                                            oppSymbol = color == 'b' ? 'w' : 'b';
                                            while (current == oppSymbol) {
                                                model.setSymbol(posY, posX, color);
                                                posX -= x;
                                                posY -= y;
                                                current = model.getSymbol(posY, posX);
                                            }
                                        }
                                    }
                                }
                                // Als het algoritme een cel vind dat leeg is
                                // wordt de loop afgebroken en op zoek gegaan naar een andere plek
                                else if (current != 'w' && current != 'b') {
                                    found = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return legal;
    }

    private void changeTurns() {
        if(currentTurn == 'b') {
            currentTurn = 'w';
            turn.setText("White");
        }
        else {
            currentTurn = 'b';
            turn.setText("Black");
        }
    }

    @FXML
    public void squareClicked(MouseEvent event) {
        if(!endGame) {
            Label l = (Label) event.getSource();
            doMove(GridPane.getColumnIndex(l), GridPane.getRowIndex(l));
        }
    }

    @Override
    public String getLocation() {
        return "../game/reversi/ReversiBoard.fxml";
    }
    
    // When the game finishes, click play again to start the game with the same settings again.
    @FXML
    public void handlePlayAgainButton(){
        GameClient.load(this, "CENTER");
        GameClient.load(this, "LEFT", "../game/reversi/SidebarGameMenuFXML.fxml");
        newGame();
        drawBoard();
        currentTurn = playerColor;
        if(currentTurn == 'b') {
            turn.setText("Black");
        }
        else {
            turn.setText("White");
        }
    }
    
    // When the game finishes, click quit to go back to home
    @FXML
    public void handleQuitButton(){
        Controller ctrl = new GameSelectMenu();
        GameClient.load(ctrl, "LEFT", ctrl.getLocation());
        MessageBus.getBus().call("CLIENT", "display home", null);

    }
    
    @FXML 
    public void handleForfeitButton(){
        handleQuitButton();
    }
    
    // When the game finishes, the grid with play again and quit appears
    public void showWinLoseGrid(){
        ObservableList<Node> children = winLoseGrid.getChildren(); 
        for(Node n : children){
            if(n instanceof Label){
                ((Label) n).setText(winner + " won!");
            }
        }
        winLoseGrid.setVisible(true);
    }
}


