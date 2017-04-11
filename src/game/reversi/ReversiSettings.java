/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.reversi;

import framework.GameClient;
import framework.interfaces.Controller;
import framework.models.UtilityGridPane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 *
 * @author Talitha
 */
public class ReversiSettings implements Controller{
    @FXML
    private UtilityGridPane settingsPane;
    @FXML
    private Button opponentComputerButton;
    @FXML
    private Button opponentPlayerButton;
    @FXML
    private Button opponentSearchButton;
    @FXML
    private Label blub;
    @FXML
    private Button difficultyEasyButton;
    @FXML
    private Button difficultyNormalButton;
    @FXML
    private Button difficultyHardButton;
    @FXML
    private Button colorBlackButton;
    @FXML
    private Button colorWhiteButton;

    private String location;

    private char playSymbol;
    private boolean ai;

    public ReversiSettings(){
        location = "../game/reversi/OpponentSettingsFXML.fxml";
    }
    @Override
    public String getLocation() {
        return location;
    }

    @FXML
    private void handleOpponentComputerButton(ActionEvent event) {
        settingsPane.removeRowsExcept(0);
        Pane difficultySettings = GameClient.loadPane(this, "../game/reversi/DifficultySettingsFXML.fxml");
        settingsPane.add(difficultySettings, 0, 1);
        Pane symbolSettings = GameClient.loadPane(this, "../game/reversi/ColorSettingsFXML.fxml");
        settingsPane.add(symbolSettings, 0, 2);
        Pane start = GameClient.loadPane(this, "../game/reversi/StartFXML.fxml");
        settingsPane.add(start, 0, 3);
        opponentComputerButton.setStyle("-fx-background-color: #000");
        opponentPlayerButton.setStyle("");
        opponentSearchButton.setStyle("");
        ai = true;
    }

    @FXML
    private void handleOpponentPlayerButton(ActionEvent event) {
        ai = false;
        settingsPane.removeRowsExcept(0);
        Pane symbolSettings = GameClient.loadPane(this, "../game/reversi/ColorSettingsFXML.fxml");
        settingsPane.add(symbolSettings, 0, 1);
        blub.setText("Player 1: Choose a symbol.");
        Pane start = GameClient.loadPane(this, "../game/reversi/StartFXML.fxml");
        settingsPane.add(start, 0, 2);
        opponentPlayerButton.setStyle("-fx-background-color: #000");
        opponentComputerButton.setStyle("");
        opponentSearchButton.setStyle("");
    }

    @FXML
    private void handleOpponentSearchButton(ActionEvent event) {
        ai = false;
        settingsPane.removeRowsExcept(0);
        opponentSearchButton.setStyle("-fx-background-color: #000");
        opponentComputerButton.setStyle("");
        opponentPlayerButton.setStyle("");
    }
    @FXML
    private void handleDifficultyEasyButton(ActionEvent event) {
        setStyleOneButton(difficultyEasyButton, difficultyNormalButton, difficultyHardButton, "-fx-background-color:#000");
    }

    @FXML
    private void handleDifficultyNormalButton(ActionEvent event) {
        difficultyNormalButton.setStyle("-fx-background-color: #000");
        difficultyEasyButton.setStyle("");
        difficultyHardButton.setStyle("");
    }

    @FXML
    private void handleDifficultyHardButton(ActionEvent event) {
        difficultyHardButton.setStyle("-fx-background-color: #000");
        difficultyNormalButton.setStyle("");
        difficultyEasyButton.setStyle("");
    }

    @FXML
    private void handleColorBlackButton(ActionEvent event) {
        colorBlackButton.setStyle("-fx-background-color:#000; -fx-effect: dropshadow( three-pass-box , rgba(0,0,255,1) , 10, 0.5 , 0 , 0)");
        colorWhiteButton.setStyle("-fx-background-color:white;");
        playSymbol = 'b';
    }

    @FXML
    private void handleColorWhiteButton(ActionEvent event) {
        colorBlackButton.setStyle("-fx-background-color:black; ");
        colorWhiteButton.setStyle("-fx-background-color:white; -fx-effect: dropshadow( three-pass-box , rgba(0,0,255,1) , 10, 0.5 , 0 , 0 )");
        playSymbol = 'w';
    }

    @FXML
    private void handleStartButton(ActionEvent event) {
        ReversiController ctrl = new ReversiController(playSymbol);
        ctrl.setAI(ai);
        GameClient.load(ctrl, "CENTER");
        GameClient.load(ctrl, "LEFT", "../game/reversi/SidebarGameMenuFXML.fxml");
        ctrl.drawBoard();
    }

    private void setStyleOneButton(Button b1Styled, Button b2, Button b3, String style){
        b1Styled.setStyle(style);
        b2.setStyle("");
        b3.setStyle("");
    }

    private void setStyleOneButton(Button b1Styled, Button b2, String style){
        b1Styled.setStyle(style);
        b2.setStyle("");
    }
}
