/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.tictactoe;

import framework.Controller;
import framework.GameClient;
import framework.models.UtilityGridPane;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author Talitha
 */
public class TicTacToeSettings implements Controller {
    @FXML
    private UtilityGridPane settingsPane;
    @FXML
    private Button opponentComputerButton;
    @FXML
    private Button opponentPlayerButton;
    @FXML
    private Button opponentSearchButton;
    @FXML
    private Button difficultyEasyButton;
    @FXML
    private Button difficultyNormalButton;
    @FXML
    private Button difficultyHardButton;
    @FXML
    private Label blub;
    @FXML
    private Button symbolCrossButton;
    @FXML
    private Button symbolCircleButton;
    
    
    
    private String location;
    
    public TicTacToeSettings(){
        location = "../game/tictactoe/SettingsFXML.fxml";
    }

    @Override
    public String getLocation() {
        return location;
    }
    
    @FXML
    private void handleOpponentComputerButton(ActionEvent event) {
        settingsPane.removeRowsExcept(0);
        Pane difficultySettings = GameClient.loadPane(this, "../game/tictactoe/DifficultySettingsFXML.fxml");
        settingsPane.add(difficultySettings, 0, 1);
        Pane symbolSettings = GameClient.loadPane(this, "../game/tictactoe/SymbolSettingsFXML.fxml");
        settingsPane.add(symbolSettings, 0, 2);
        Pane start = GameClient.loadPane(this, "../game/tictactoe/StartFXML.fxml");
        settingsPane.add(start, 0, 3);
        opponentComputerButton.setStyle("-fx-background-color: #000");
        opponentPlayerButton.setStyle("");
        opponentSearchButton.setStyle("");
    }
    
    @FXML
    private void handleOpponentPlayerButton(ActionEvent event) {
        settingsPane.removeRowsExcept(0);
        Pane symbolSettings = GameClient.loadPane(this, "../game/tictactoe/SymbolSettingsFXML.fxml");
        settingsPane.add(symbolSettings, 0, 1);
        blub.setText("Player 1: Choose a symbol.");
        Pane start = GameClient.loadPane(this, "../game/tictactoe/StartFXML.fxml");
        settingsPane.add(start, 0, 2);
        opponentPlayerButton.setStyle("-fx-background-color: #000");
        opponentComputerButton.setStyle("");
        opponentSearchButton.setStyle("");
    }
    @FXML
    private void handleOpponentSearchButton(ActionEvent event) {
        settingsPane.removeRowsExcept(0);
        opponentSearchButton.setStyle("-fx-background-color: #000");
        opponentComputerButton.setStyle("");
        opponentPlayerButton.setStyle("");
    }
    
    @FXML
    private void handleDifficultyEasyButton(ActionEvent event) {
        difficultyEasyButton.setStyle("-fx-background-color: #000");
        difficultyNormalButton.setStyle("");
        difficultyHardButton.setStyle("");
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
    private void handleSymbolCrossButton(ActionEvent event) {
        symbolCrossButton.setStyle("-fx-background-color: #000");
        symbolCircleButton.setStyle("");
    }
    
    @FXML
    private void handleSymbolCircleButton(ActionEvent event) {
        symbolCrossButton.setStyle("");
        symbolCircleButton.setStyle("-fx-background-color: #000");
    }
    
    @FXML
    private void handleStartButton(ActionEvent event) {
        
    }
}
