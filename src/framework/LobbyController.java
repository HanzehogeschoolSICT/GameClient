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
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
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
    
    private ObservableList<ListObject> players_to_challenge = FXCollections.observableArrayList();
    private String game;
    
    public LobbyController(String game_to_play){
        game = game_to_play;
    }

    public void init(){
        toChallenge.setItems(players_to_challenge); // Hoeft maar 1 keer aangeroepen te worden
        refreshPlayerList();
        MessageBus.getBus().call("NETWORK", "login", null);
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

            ArrayList<String> playerList = new ArrayList<>();
            for (Object s : playerJSON) {
                playerList.add((String) s);
            }

            // Filter duplicaten
            // TODO: filter eigen naam
            Iterator<ListObject> it = players_to_challenge.iterator();
            while (it.hasNext()) {
                ListObject obj = it.next();
                if (!playerList.contains(obj.getPlayerName())) {
                    it.remove();
                } else {
                    playerList.remove(obj.getPlayerName());
                }
            }

            // Voeg nieuwe toe
            for (String player : playerList) {
                players_to_challenge.add( new ListObject(player));
            }
        } catch (ParseException ex) {
            Logger.getLogger(LobbyController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void call(String message, Object[] args){
       // hier ontvang je messages waar je niet om vraagt.
       // Challenges, Turn, Loss, Win, dat soort grappen.
        System.out.println("message");
    }

    @Override
    public String getLocation() {
        return "../framework/assets/LobbyView.fxml";
    }
    
    public void refreshPlayerList() {
        Object[] args = new Object[1];
        args[0] = this;
        MessageBus.getBus().call("NETWORK", "get players", args);
    }

    @FXML
    public void acceptChallenge(ActionEvent e){
        System.out.println("Lol mag nieee");
    }

    /**
     * Deze private class zorgt voor een nette afhandeling van iedere speler in de lijst
     * (Moet mogelijk verplaatst worden naar LobbyView)
     */
    private class ListObject extends GridPane {
        private String playerName;
        private Label playerLabel;
        private Button playerButton;

        ListObject(String playerName) {
            this.playerName = playerName;
            playerLabel = new Label(playerName);
            playerButton = createButton();

            // De button kan gestyled worden met de .challengebutton class
            playerButton.getStyleClass().add(".challengebutton");

            this.add(playerLabel, 0, 0);
            this.add(playerButton, 1, 0);

            setConstrains();
        }

        String getPlayerName() {
            return playerName;
        }

        void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        private Button createButton() {
            Button btn = new Button("Challenge");

            btn.setOnAction( (event) ->
                MessageBus.getBus().call("NETWORK", "challenge \"" + this.playerName + "\" \"" + game +"\"", null)
            );

            return btn;
        }

        private void setConstrains() {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setFillWidth(true);
            constraints.setHgrow(Priority.ALWAYS);
            this.getColumnConstraints().add(constraints);
        }
    }
}
