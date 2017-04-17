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

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.layout.*;
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

    @FXML
    private Button subscribeButton;

    @FXML
    private Button refreshButton;
    
    private ObservableList<ListObject> playersToChallenge = FXCollections.observableArrayList();
    private ObservableList<ChallengeObject> incomingChallenges = FXCollections.observableArrayList();
    private String game;
    private boolean tourny_mode = false;
    
    public LobbyController(String game_to_play){
        game = game_to_play;
    }

    public void init(){
        toChallenge.setItems(playersToChallenge); // Hoeft maar 1 keer aangeroepen te worden
        challenges.setItems(incomingChallenges);
        MessageBus.getBus().call("NETWORK", "open", null);
        MessageBus.getBus().call("NETWORK", "login", null);
        refreshPlayerList();
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
            Iterator<ListObject> it = playersToChallenge.iterator();
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
                playersToChallenge.add( new ListObject(player));
            }
        } catch (ParseException ex) {
            Logger.getLogger(LobbyController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @FXML
    private void handleSubscribe(ActionEvent event) {
        System.out.println("LobbyController.handleSubscribe");
        MessageBus.getBus().call("GAME", "GAME SUBSCRIBE", null);
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        System.out.println("LobbyController.handleRefresh");
        refreshPlayerList();
    }

    private void addChallenge(String challenger, String challengeNumber) {
        Platform.runLater( () -> incomingChallenges.add(new ChallengeObject(challenger, challengeNumber)) );
    }

    private void acceptChallenge(ChallengeObject challenge) {
        MessageBus.getBus().call("GAME", "GAME CHALLENGE ACCEPT", new String[] {challenge.getChallengeNumber()} );
    }

    private void removeChallenge(ChallengeObject challenge) {
        // Negeer challenge
        incomingChallenges.remove(challenge);
    }

    @Override
    public void call(String message, Object[] args){
        if (message.equals("CHALLENGE")) {
            addChallenge((String) args[0], (String) args[1]);
        }
    }

    @Override
    public String getLocation() {
        return "../framework/assets/LobbyView.fxml";
    }
    
    private void refreshPlayerList() {
        Object[] args = new Object[1];
        args[0] = this;
        MessageBus.getBus().call("NETWORK", "get players", args);
    }

    /**
     * Deze private class zorgt voor een nette afhandeling van iedere speler in de lijst
     * (Moet mogelijk verplaatst worden naar LobbyView)
     */
    private class ListObject extends GridPane {
        private String playerName;
        private Label playerLabel;
        private Button playerButton;

        private ListObject(String playerName) {
            this.playerName = playerName;
            playerLabel = new Label(playerName);
            playerButton = createButton();

            // De button kan gestyled worden met de .challengebutton class
            playerButton.getStyleClass().add("challengebutton");

            this.add(playerLabel, 0, 0);
            this.add(playerButton, 1, 0);

            setConstrains();
        }

        private String getPlayerName() {
            return playerName;
        }

        private void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        private Button createButton() {
            Button btn = new Button("Challenge");

            btn.setOnAction( event ->
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

    /**
     * Deze private class zorgt voor een nette vertoning en afhandeling van alle binnenkomende challenges.
     * (Moet mogelijk verplaatst worden naar LobbyView)
     */
    private class ChallengeObject extends GridPane {
        private String playerName;
        private String challengeNumber;

        private Label nameLabel;
        private Button acceptButton;
        private Button declineButton;

        private ChallengeObject(String playerName, String challengeNumber) {
            this.playerName = playerName;
            this.challengeNumber = challengeNumber;

            nameLabel = new Label(playerName);
            acceptButton = createAcceptButton();
            declineButton = createDeclineButton();

            this.add(nameLabel, 0, 0);
            this.add(new HBox(acceptButton, declineButton), 1, 0);

            setConstrains();
        }

        private Button createAcceptButton() {
            Button btn = new Button("Accept");

            btn.getStyleClass().add("acceptChallengeButton");

            btn.setOnAction(event -> acceptChallenge(this));

            return btn;
        }

        private Button createDeclineButton() {
            Button btn = new Button("Decline");

            btn.getStyleClass().add("declineChallengeButton");

            btn.setOnAction(event -> removeChallenge(this));

            return btn;
        }

        public String getPlayerName() {
            return playerName;
        }

        public String getChallengeNumber() {
            return challengeNumber;
        }

        private void setConstrains() {
            ColumnConstraints cc1 = new ColumnConstraints();
            cc1.setFillWidth(true);
            cc1.setHgrow(Priority.ALWAYS);

            this.getColumnConstraints().add(cc1);
        }
    }
}
