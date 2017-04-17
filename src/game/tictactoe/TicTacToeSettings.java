/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.tictactoe;

import framework.GameClient;
import framework.MessageBus;
import framework.interfaces.Controller;
import framework.models.UtilityGridPane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author Talitha
 */
public class TicTacToeSettings implements Controller {
    @FXML private UtilityGridPane settingsPane;
    @FXML private Button opponentComputerButton;
    @FXML private Button opponentPlayerButton;
    @FXML private Button opponentSearchButton;
    @FXML private Button difficultyEasyButton;
    @FXML private Button difficultyNormalButton;
    @FXML private Button difficultyHardButton;
    @FXML private Label blub;
    @FXML private Button symbolCrossButton;
    @FXML private Button symbolCircleButton;
    @FXML private BorderPane rightPane;

    private String location;
    private char playSymbol = 'x';
    private String opponent;
    private Integer difficulty = null;
    
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
        setStyleOneButton(opponentComputerButton, opponentPlayerButton, opponentSearchButton);
        opponent = "AI";
    }
    
    @FXML
    private void handleOpponentPlayerButton(ActionEvent event) {
        settingsPane.removeRowsExcept(0);
        Pane symbolSettings = GameClient.loadPane(this, "../game/tictactoe/SymbolSettingsFXML.fxml");
        settingsPane.add(symbolSettings, 0, 1);
        blub.setText("Player 1: Choose a symbol.");
        Pane start = GameClient.loadPane(this, "../game/tictactoe/StartFXML.fxml");
        settingsPane.add(start, 0, 2);
        setStyleOneButton(opponentPlayerButton, opponentComputerButton, opponentSearchButton);
        opponent = "PLAYER";
    }
    
    @FXML
    private void handleOpponentSearchButton(ActionEvent event) {
        ServerController sc = new ServerController();
        MessageBus mb = MessageBus.getBus();
        mb.register("GAME", sc);
        settingsPane.removeRowsExcept(0);
        setStyleOneButton(opponentSearchButton, opponentPlayerButton, opponentComputerButton);
    }
    
    @FXML
    private void handleDifficultyEasyButton(ActionEvent event) {
        setStyleOneButton(difficultyEasyButton, difficultyNormalButton, difficultyHardButton);
        difficulty = 1;
    }
    
    @FXML
    private void handleDifficultyNormalButton(ActionEvent event) {
        setStyleOneButton(difficultyNormalButton, difficultyEasyButton, difficultyHardButton);
        difficulty = 2;
    }
    
    @FXML
    private void handleDifficultyHardButton(ActionEvent event) {
        setStyleOneButton(difficultyHardButton, difficultyNormalButton, difficultyEasyButton);
        difficulty = 3;
    }
    
    @FXML
    private void handleSymbolCrossButton(ActionEvent event) {
        setStyleOneButton(symbolCrossButton, symbolCircleButton);
        playSymbol = 'x';
    }
    
    @FXML
    private void handleSymbolCircleButton(ActionEvent event) {
        setStyleOneButton(symbolCircleButton, symbolCrossButton);
        playSymbol = 'o';
    }
    
    @FXML
    private void handleStartButton(ActionEvent event) {
        TicTacToeController ctrl = new TicTacToeController(playSymbol, opponent, difficulty);
        GameClient.load(ctrl, "CENTER");
        GameClient.load(ctrl, "LEFT", "../game/tictactoe/SidebarGameMenuFXML.fxml");
    }
    
    private void setStyleOneButton(Button b1Styled, Button b2, Button b3){
        b1Styled.setStyle("-fx-background-color: #02c937; -fx-effect: innershadow(three-pass-box, #01701e, 20.0, 0.0, 5, 8);;");
        b2.setStyle("");
        b3.setStyle("");
    }
    
    private void setStyleOneButton(Button b1Styled, Button b2){
        b1Styled.setStyle("-fx-background-color: #02c937; -fx-effect: innershadow(three-pass-box, #01701e, 20.0, 0.0, 5, 8);");
        b2.setStyle("");
    }
}
