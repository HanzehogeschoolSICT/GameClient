/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.tictactoe;

import framework.interfaces.Controller;
import framework.GameClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author Talitha
 */
public class Settings implements Controller {
    @FXML
    private GridPane settingsPane;
    @FXML
    private Button opponentComputerButton;
    
    private String location;
    
    public Settings(){
        location = "../game/tictactoe/SettingsFXML.fxml";
    }

    @Override
    public String getLocation() {
        return location;
    }
    
    @FXML
    private void handleOpponentComputerButton(ActionEvent event) {
        Pane difficultySettings = GameClient.loadPane("../game/tictactoe/DifficultySettingsFXML.fxml");
        settingsPane.add(difficultySettings, 0, 1);
        Pane symbolSettings = GameClient.loadPane("../game/tictactoe/SymbolSettingsFXML.fxml");
        settingsPane.add(symbolSettings, 0, 2);
        Pane start = GameClient.loadPane("../game/tictactoe/StartFXML.fxml");
        settingsPane.add(start, 0, 3);
        opponentComputerButton.setStyle("-fx-background-color: #000");
    }
    @FXML
    private void handleOpponentPlayerButton(ActionEvent event) {
        
    }
    @FXML
    private void handleOpponentSearchButton(ActionEvent event) {
        
    }
    
    @FXML
    private void handleDifficultyEasyButton(ActionEvent event) {
        
    }
    
    @FXML
    private void handleDifficultyNormalButton(ActionEvent event) {
        
    }
    
    @FXML
    private void handleDifficultyHardButton(ActionEvent event) {
        
    }
    
    @FXML
    private void handleSymbolCrossButton(ActionEvent event) {
        
    }
    
    @FXML
    private void handleSymbolCircleButton(ActionEvent event) {
        
    }
    
    @FXML
    private void handleStartButton(ActionEvent event) {
        
    }
}
