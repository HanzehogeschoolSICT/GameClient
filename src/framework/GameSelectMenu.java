/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import framework.interfaces.Controller;
import framework.interfaces.Messagable;
import game.reversi.ReversiSettings;
import game.tictactoe.TicTacToeSettings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 * This goes in the information box.
 * Get the list of games (MessageBus -> Network)
 * Display them on the GameClient -> ApplicationPane
 * On select tell the GameLoader to swap game modules. 
 * @author Wouter
 */
public class GameSelectMenu implements Controller, Messagable{
    @FXML private Text text;
    @FXML private BorderPane parent;
    @FXML private Button TicTacToeButton;
    @FXML private Button ReversiButton;
    @FXML private Button TournamentButton;
    @FXML private ImageView imageView;

    private String location = "../framework/assets/Menu.fxml";
    private boolean isTournament = false;
    
    @FXML
    private void handleReversiButtonAction(ActionEvent event) {
        TicTacToeButton.setStyle("");
        ReversiButton.setStyle("-fx-background-color:#525254");
        GameClient.load(new ReversiSettings(), "CENTER");
    }
    
    @FXML
    private void handleTictactoeButtonAction(ActionEvent event) {
        TicTacToeButton.setStyle("-fx-background-color:#525254");
        ReversiButton.setStyle("");
        GameClient.load(new TicTacToeSettings(), "CENTER");
    }

    @FXML
    private void handleTournament(ActionEvent event){
        isTournament = !isTournament;
        if(isTournament){
            TournamentButton.setStyle("-fx-background-color:#44AA33");
        }
        else{
            TournamentButton.setStyle("-fx-background-color:#AA3322");
        }
            
        Object[] args = new Object[1];
        args[0] = new Boolean(isTournament);
        MessageBus.getBus().call("GAME", "tournament mode toggle", args);
    }
    @Override
    public String getLocation() {
        return location;
    }
    public void initialize(){
        if(TournamentButton != null){
            TournamentButton.setStyle("-fx-background-color:#AA3322");
            isTournament = false;
        }
    }
    @Override
    public void call(String message, Object[] args) {
        if(message.equals("tournament mode on")){
            location = "../framework/assets/Menu_tournament.fxml";
            GameClient.load(this, "LEFT");
        }
        if(message.equals("tournament mode off")){
            location = "../framework/assets/Menu.fxml";
            GameClient.load(this, "LEFT");
        }
    }
}
