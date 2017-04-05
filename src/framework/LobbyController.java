/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import framework.interfaces.Controller;
import framework.interfaces.Messagable;
import framework.interfaces.Networkable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import org.json.simple.*;
import org.json.simple.parser.*;
/**
 * Get the lists for Players and Challenges. Give these to the View.
 * Give the view to the ApplicationPane. 
 * @author Wouter
 */
public class LobbyController implements Networkable, Messagable, Controller{
    
    @FXML
    private ListView challenges;
    @FXML 
    private ListView toChallenge;
    
    private ArrayList<String> playerList;
    private ObservableList players_to_challenge = FXCollections.observableArrayList();
    private String game;
    
    public LobbyController(String game_to_play){
        game = game_to_play;
    }
    @Override
    public void putData(ArrayList<String> messages) {
        // Dit zijn messages die je krijgt van de server waar je naar vraagt.
        for(String m: messages){
            if(m.startsWith("SVR PLAYERLIST")){
                // We received the playerlist we asked for. 
                makePlayerList(m.substring(15));
            }
        }
    }
    private void makePlayerList(String players){
        try {
            JSONParser parser = new JSONParser();
            Object o = parser.parse(players);
            JSONArray playerJSON = (JSONArray) o;
            for(Object s: playerJSON){
                playerList.add((String) s);
            }
            for(String player: playerList){
                Label playerName = new Label(player);
                //HBox playerBox = new HBox();
                //playerBox.getChildren().add(playerName);
                players_to_challenge.add(playerName);
            }
            toChallenge.setItems(players_to_challenge);
            MessageBus.getBus().call("NETWORK", "login", null);
            
        } catch (ParseException ex) {
            Logger.getLogger(LobbyController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void call(String message, Object[] args){
       // hier ontvang je messages waar je niet om vraagt.
       // Challenges, Turn, Loss, Win, dat soort grappen.
        System.out.println("message");
    }

    @Override
    public String getLocation() {
        return "../framework/assets/LobbyView.fxml";
    }
    
    public void init(){
        playerList = new ArrayList();
        Object[] args = new Object[1];
        args[0] = this;
        MessageBus.getBus().call("NETWORK", "get players", args);
    }
    @FXML
    public void challengePlayer(ActionEvent e){
        Label selected = (Label) toChallenge.getSelectionModel().getSelectedItem();
        String player_name = selected.getText();
        MessageBus mb = MessageBus.getBus();
        mb.call("NETWORK", "challenge \"" + player_name + "\" \"" + game +"\"", null);      
    }
    @FXML
    public void acceptChallenge(ActionEvent e){
        System.out.println("Lol mag nieee");
    }
}
