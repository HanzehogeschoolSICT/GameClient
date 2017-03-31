/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import debug.TestView;
import game.reversi.ReversiView;
import game.tictactoe.TicTacToeSettings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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
    private void handleReversiButtonAction(ActionEvent event) {
        System.out.println(event);
        GameClient.load(new ReversiView(), parent, "CENTER");
    }
    
    @FXML
    private void handleTictactoeButtonAction(ActionEvent event) {
        GameClient.load(new TicTacToeSettings(), parent, "CENTER");
    }
}
