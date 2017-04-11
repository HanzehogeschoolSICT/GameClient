package game.tictactoe;

import framework.GameClient;
import framework.LobbyController;
import framework.MessageBus;
import framework.interfaces.Controller;
import framework.interfaces.Messagable;
import framework.interfaces.Networkable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;

//import org.json.simple.*;
//import org.json.simple.parser.*;
/**
 * Created by markshizzle on 4-4-2017.
 */
public class ServerController implements Networkable, Messagable, Controller{

    private final String game = "Tic-tac-toe";
    private boolean cross_turn = true;
    
    @FXML
    private GridPane grid;
    
    public ServerController() {
        LobbyController lc = new LobbyController(game);
        GameClient.load(lc,"CENTER");
        MessageBus mb = MessageBus.getBus();
        mb.register("LOBBY", lc);
        lc.init();
    }
    public void registerGame() {

    }

    @Override
    public void putData(ArrayList<String> messages) {
    }

    @Override
    public void call(String message, Object[] args) {
        System.out.println(message);
        if(message.startsWith("SVR GAME CHALLENGE")){
            if(message.contains("CHALLENGE CANCELLED")){   }
            else{
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
//                    mb.call("NETWORK", "challenge accept " + parts[1], null); // Accept the challenge
                    mb.call("LOBBY", "CHALLENGE", new String[] { parts[0], parts[1], parts[2]}); // Send te challenge to the lobby
                }
            }
        }
        if(message.startsWith("SVR GAME MATCH")){
            System.out.println("LETS GO BOY");
            String[] parsedMessage = message.substring(15)
                                          .replace("{PLAYERTOMOVE: ","")
                                          .replace(", GAMETYPE:", "")
                                          .replace(", OPPONENT:", "")
                                          .replace("\"", "")
                                          .replace("}", "")
                                          .split("\\s+");
            GameClient.load(this, "CENTER");
        }
        if(message.startsWith("SVR GAME MOVE")){
            String[] parsedMessage = message.substring(14)
                                          .replace("{PLAYER: ","")
                                          .replace(", MOVE:", "")
                                          .replace(", DETAILS:", "")
                                          .replace("\"", "")
                                          .replace("}", "")
                                          .split("\\s+");
            System.out.println(parsedMessage[1]);
            int position = Integer.parseInt(parsedMessage[1]);
            int row = (position - (position % 3)) / 3;
            System.out.println(row);
            int col = position - row*3;
            System.out.println(col);
            if(cross_turn){
                Platform.runLater( ()-> drawX(row, col, grid) );
                cross_turn = false;
            }
            else{
                Platform.runLater( ()-> drawO(row, col, grid) );
                cross_turn = true;
            }
        }
        if (message.startsWith("GAME CHALLENGE ACCEPT")) {
            System.out.println("Accepting challenge " + args[0]);
            MessageBus mb = MessageBus.getBus();
            mb.call("NETWORK", "challenge accept " + args[0], null);
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
    @Override
    public String getLocation() {
        return "../game/tictactoe/TicTacToeBoard.fxml";
    }
    @FXML
    public void squareClicked(MouseEvent event) {
        Label l = (Label) event.getSource();
        MessageBus.getBus().call("NETWORK", "move " + (GridPane.getRowIndex(l) * 3 + GridPane.getColumnIndex(l)), null);
        //(currentTurn, GridPane.getRowIndex(l),GridPane.getColumnIndex(l));
    }
}
