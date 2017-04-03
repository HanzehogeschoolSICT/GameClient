/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import game.reversi.ReversiSettings;
import game.tictactoe.TicTacToeSettings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 * This goes in the information box.
 * Get the list of games (MessageBus -> Network)
 * Display them on the GameClient -> ApplicationPane
 * On select tell the GameLoader to swap game modules. 
 * @author Wouter
 */
public class GameSelectMenu {
    @FXML
    private Text text;
    @FXML
    private BorderPane parent;
    @FXML 
    private Button TicTacToeButton;
    @FXML
    private Button ReversiButton;
    
    @FXML
    private void handleReversiButtonAction(ActionEvent event) {
        TicTacToeButton.setStyle("");
        ReversiButton.setStyle("-fx-background-color:#525254");
        GameClient.load(new ReversiSettings(), parent, "CENTER");
    }
    
    @FXML
    private void handleTictactoeButtonAction(ActionEvent event) {
        TicTacToeButton.setStyle("-fx-background-color:#525254");
        ReversiButton.setStyle("");
        GameClient.load(new TicTacToeSettings(), parent, "CENTER");
    }
}
