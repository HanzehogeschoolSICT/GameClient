package game.reversi;

import framework.GameClient;
import framework.GameSelectMenu;
import framework.LobbyController;
import framework.MessageBus;
import framework.interfaces.Controller;
import game.abstraction.AbstractServerController;
import java.awt.Point;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.util.Duration;

//import org.json.simple.*;
//import org.json.simple.parser.*;
/**
 * Created by markshizzle on 4-4-2017.
 */
public class ServerController extends AbstractServerController {
    @FXML Label timer;
    @FXML GridPane winLoseGrid;
    @FXML Label scoreB;
    @FXML Label scoreW;
    @FXML Label turn;
    @FXML Button playAgainBtn;
    
    private final String game = "Reversi";
    private boolean isTournament = false;
    private int turns = 0;
    private char our_colour = ' ';
    private boolean can_move = false;
    
    private Text text;
    private Model model;
    private char currentTurn;
    private boolean[][] legalMovesW;
    private boolean[][] legalMovesB;
    private final int STARTTIME = 10;
    private int remainSec = STARTTIME;
    private boolean endGame;
    private String winner;
    private int totalW = 0;
    private int totalB = 0;
    private int amountLegalMovesB;
    private int amountLegalMovesW;
    private Timeline timeline;
    
    @FXML
    private GridPane grid;
    
    public ServerController() {
        LobbyController lc = new LobbyController(game);
        GameClient.load(lc,"CENTER");
        MessageBus mb = MessageBus.getBus();
        mb.register("LOBBY", lc);
        mb.call("MENU", "tournament mode on", null);
        lc.init();
        initHandlers();
    }
    private void newGame(){
        model = new Model();
        turns = 0;
        legalMovesW = new boolean[8][8];
        legalMovesB = new boolean[8][8];
        our_colour = ' ';
        currentTurn = 'b';
        drawBoard();
    }
    public void registerGame() {

    }

    private void initHandlers() {
        super.registerHandler("SVR GAME CHALLENGE", this::handleChallenge);
        super.registerHandler("SVR GAME MATCH", this::handleMatchStarted);
        super.registerHandler("SVR GAME MOVE", this::handleMove);
        super.registerHandler("SVR GAME YOURTURN", this::turnStart);
        super.registerHandler("GAME CHALLENGE ACCEPT", this::handleChallengeAccept);
        super.registerHandler("GAME SUBSCRIBE", this::handleSubscribe);
        super.registerHandler("tournament mode toggle", this::setTournamentMode);
    }

    @Override
    public void putData(ArrayList<String> messages) {
        
    }

    private void turnStart(String message, Object[] args){
        if (our_colour == ' ') {
            if(turns == 0){
                our_colour = 'b';
            }
            else{
                our_colour = 'w';
            }
        }
        System.out.println("We are: " + our_colour);
        
        can_move = true;
        if(isTournament){
            calcPoints();
            // Berekent de volgende legale zetten
            getLegalMoves();
            Point m = new AI(model, our_colour).nextMove();
            System.out.println("Next:" + m.x + ", " + m.y);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            }
            MessageBus.getBus().call("NETWORK", "move " + (m.y * 8 + m.x), null);
        }
        
    }
    private void setTournamentMode(String message, Object[] args){
        isTournament = !isTournament;
    }
    
    void drawBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (model.getSymbol(i,j) == 'b') {
                    drawO('b', i, j);
                    System.out.println("I got a black move and I draw it!");
                }
                if (model.getSymbol(i,j) == 'w') {
                    drawO('w', i, j);
                    System.out.println("I got a white move and I draw it!");
                }
            }
        }
    }
    
    private void drawO(char colour, int column, int row) {
        Platform.runLater(()->{
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
        });
    }
    
    private void handleChallenge(String message, Object[] args) {
        // Since the server string isn't valid JSON, we'll have to pry out our answers.
        String parsedMessage = message.substring(19)
                .replace("{CHALLENGER: ", "")           // Parts[0]     opponent name
                .replace(", CHALLENGENUMBER:", "")      // Parts[1]     Challenge number
                .replace(", GAMETYPE:", "")             // Parts[2]     Game we got challenged for
                .replace("}","")
                .replace("\"", "");
        String[] parts = parsedMessage.split("\\s+");
        if(parts[2].equals(game)){  // We don't care about games we're not playing
            System.out.println("Incoming challenge from " + parts[0]);
            MessageBus mb = MessageBus.getBus();
            mb.call("LOBBY", "CHALLENGE", new String[] { parts[0], parts[1], parts[2]}); // Send te challenge to the lobby
        }
    }

    private void handleMatchStarted(String message, Object[] args) {
        System.out.println("LETS GO BOY");
        String[] parsedMessage = message.substring(15)
                .replace("{PLAYERTOMOVE: ","")
                .replace(", GAMETYPE:", "")
                .replace(", OPPONENT:", "")
                .replace("\"", "")
                .replace("}", "")
                .split("\\s+");
        GameClient.load(this, "CENTER");
        GameClient.load(this, "LEFT", "../game/reversi/SidebarGameMenuFXML.fxml");
        newGame();
    }

    private void handleMove(String message, Object[] args) {
        String[] parsedMessage = message.substring(14)
                .replace("{PLAYER: ","")
                .replace(", MOVE:", "")
                .replace(", DETAILS:", "")
                .replace("\"", "")
                .replace("}", "")
                .split("\\s+");
        
        int position = Integer.parseInt(parsedMessage[1]);
        int row = (position - (position % 8)) / 8;
        int col = position - row*8;
        System.out.println("x: " + col + " y: " + row);
        turns++;
        
        legalMove(col, row, currentTurn, true);
        model.setSymbol(col, row, currentTurn);
        
        currentTurn = currentTurn == 'b' ? 'w' : 'b'; // swap turns;
        drawBoard();
    }

    private void handleChallengeAccept(String message, Object[] args) {
        System.out.println("Accepting challenge " + args[0]);
        MessageBus mb = MessageBus.getBus();
        mb.call("NETWORK", "challenge accept " + args[0], null);
    }

    private void handleSubscribe(String message, Object[] args) {
        MessageBus.getBus().call("NETWORK", "subscribe " + this.game, null);
    }
    
    @Override
    public String getLocation() {
        return "../game/reversi/ReversiBoard.fxml";
    }
    @FXML
    public void squareClicked(MouseEvent event) {
        if(can_move){
            Label l = (Label) event.getSource();
            MessageBus.getBus().call("NETWORK", "move " + (GridPane.getRowIndex(l) * 8 + GridPane.getColumnIndex(l)), null);
            can_move = false;
        }
    }
    public void doMove(int column, int row) {
        if(legalMove(column, row, our_colour, false)) {
            remainSec = STARTTIME;
            model.setSymbol(column, row, our_colour);
        }
        else {
            createDialog("Helaas", "Helaas het is niet mogelijk om deze zet te doen.");
            return;
        }
        calcPoints();
        drawBoard();
        // Berekent de volgende legale zetten
        
        
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
    private void setTimer() {
        timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ae -> countDown()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
    
    private void countDown() {
        remainSec--;
        if(endGame) {
            timeline.stop();
        }
        if(remainSec == 0 || remainSec < 0) {
            createDialog("Afgelopen", "Helaas, jou zet duurde helaas te lang");
            endGame = true;
            timeline.stop();
        }
        timer.setText("00:0" + remainSec);
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
        //scoreB.setText("" + totalB);
        //scoreW.setText("" + totalW);
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
    
    @FXML
    public void handlePlayAgainButton(){
        
    }
    
    @FXML
    public void handleQuitButton(){
        Controller ctrl = new GameSelectMenu();
        GameClient.load(ctrl, "LEFT", ctrl.getLocation());
        MessageBus.getBus().call("CLIENT", "display home", null);

    }
    
    public void showWinLoseGrid(){
        ObservableList<Node> children = winLoseGrid.getChildren(); 
        for(Node n : children){
            if(n instanceof Label){
                ((Label) n).setText(winner + " won!");
            }
        }
        children.remove(playAgainBtn);
        winLoseGrid.setVisible(true);
    }
}
