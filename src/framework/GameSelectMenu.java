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
    @FXML
    private Text text;
    @FXML
    private BorderPane parent;
    @FXML 
    private Button TicTacToeButton;
    @FXML
    private Button ReversiButton;
    @FXML
    private ImageView imageView;

    @FXML
    public void initialize(){
        Image image = new Image("framework/assets/spooky.png");
        imageView.setImage(image);
        GameClient.setParent(parent);
    }
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
        GameClient.load(new TicTacToeSettings(), "CENTER");
    }

    @Override
    public String getLocation() {
        return "../framework/assets/FXML.fxml";
    }

    @Override
    public void call(String message, Object[] args) {
    }
}
