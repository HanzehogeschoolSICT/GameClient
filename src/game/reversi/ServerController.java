package game.reversi;

import framework.GameClient;
import framework.GameSelectMenu;
import framework.LobbyController;
import framework.MessageBus;
import framework.interfaces.Controller;
import framework.interfaces.Networkable;
import game.abstraction.AbstractServerController;
import java.awt.Point;
import java.net.URL;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.util.Duration;

import org.json.simple.*;
import org.json.simple.parser.*;

/**
 * Created by markshizzle on 4-4-2017.
 */
public class ServerController extends AbstractServerController implements Networkable,Initializable{
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
    private char their_colour = ' ';
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
    private String our_name;
    
    @FXML
    private GridPane grid;
    @FXML
    private Label turnLabel;
    
    public ServerController(){
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
        currentTurn = 'b';
        drawBoard();
    }
    public void registerGame() {

    }
    @Override
    public void initialize(URL location, ResourceBundle resources){
        String loc = location.toString();
        System.out.println(loc);
        if(loc.endsWith("SidebarGameMenuFXML.fxml")){
            turnLabel.setText("Our colour is");
            String colour = (our_colour == 'w') ? "white" : "black";
            turn.setText(colour);
        }
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
        for(String m : messages){
            String[] parts = m.split("\\s+");
            switch(parts[0]){
                case "name":
                    this.our_name = m.replace("name ", "");
                    break;
            }
        }
    }

    private void turnStart(String message, Object[] args){
        can_move = true;
        if(isTournament){
            calcPoints();
            // Berekent de volgende legale zetten
            getLegalMoves();
            System.out.println("I think my colour is " + our_colour);
            Point m = new AI(model, our_colour, 0.075).nextMove();
            System.out.println("Next:" + m.x + ", " + m.y);

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
                }
                if (model.getSymbol(i,j) == 'w') {
                    drawO('w', i, j);
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
        String toParse = message.substring(19);
        Pattern p = Pattern.compile("\\{CHALLENGER: \"(.*?)\", CHALLENGENUMBER: \"(\\d*?)\", GAMETYPE: \"(.*?)\"\\}");
        Matcher m = p.matcher(toParse);

        if (m.find()) {
            if (m.group(3).equals(game)) {
                System.out.println("Incoming challenge from " + m.group(1));
                MessageBus.getBus().call("LOBBY",
                        "CHALLENGE",
                        new String[] { m.group(1), m.group(2), m.group(3)});
            }
        }
    }

    private void handleMatchStarted(String message, Object[] args) {
        MessageBus mb = MessageBus.getBus();
        Object[] args_to_send = new Object[1];
        args_to_send[0] = this;
        mb.call("NETWORK", "get name", args_to_send);
        String parsedMessage = message.substring(15)
                .replace("PLAYERTOMOVE","\"PLAYERTOMOVE\"")
                .replace("GAMETYPE", "\"GAMETYPE\"")
                .replace("OPPONENT:", "\"OPPONENT\"");
        Object o;
        try {
            
            JSONParser parser = new JSONParser();
            o = parser.parse(parsedMessage);
            JSONObject playerJSON = (JSONObject) o;
            String player_to_move = (String) playerJSON.get("PLAYERTOMOVE");
            System.out.println(player_to_move + " " + our_name);
            if(player_to_move.equals(our_name)){
                our_colour = 'b';
                their_colour = 'w';
            }
            else{
                our_colour = 'w';
                their_colour = 'b';
            }
            GameClient.load(this, "CENTER");
            GameClient.load(this, "LEFT", "../game/reversi/SidebarGameMenuFXML.fxml");
            
            newGame();
        }
        catch (ParseException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleMove(String message, Object[] args) {
        String parsedMessage = message.substring(14)
                .replace("PLAYER","\"PLAYER\"")
                .replace("MOVE", "\"MOVE\"")
                .replace("DETAILS", "\"DETAILS\"");
        
        Object o;
        try {
            JSONParser parser = new JSONParser();
            o = parser.parse(parsedMessage);
            JSONObject playerJSON = (JSONObject) o;
            int position = Integer.parseInt((String) playerJSON.get("MOVE"));
            int row = (position - (position % 8)) / 8;
            int col = position - row*8;
            turns++;

            char to_move;
            if(( (String) playerJSON.get("PLAYER") ).equals(our_name))
                to_move = our_colour;
            else
                to_move = their_colour;
            legalMove(col, row, to_move, true);
            model.setSymbol(col, row, to_move);

            drawBoard();
        } catch (ParseException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            createDialog("Invalid move", "This is an invalid move.");
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
            createDialog("Game over", "You ran out of time!");
            endGame = true;
            if(currentTurn == 'b')
                winner = "White";
            else
                winner = "Black";
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
    
    @FXML 
    public void handleForfeitButton(){
        handleQuitButton();
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
