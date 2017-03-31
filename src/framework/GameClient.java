/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * This is the main Application Class. Runs in JavaFX thread. 
 * Idk what needs to happen here lol
 * It will hold the loaded Game Objects. 
 * @author Wouter
 */
public class GameClient extends Application{

    private Parent root;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MessageBus bus = MessageBus.getBus();
        launch(args);
            
    }

    @Override
    public void start(Stage primaryStage){
        root = new Group();
        try {
            root = FXMLLoader.load(getClass().getResource("../framework/assets/FXML.fxml"));
        } catch (IOException ex) {
            System.out.println("Can't find file");
            System.exit(1);
        }
        
        Scene scene = new Scene(root,1000,750);
        primaryStage.setTitle("Simply Fun");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    

    public static void load(View v, BorderPane p, String position){
        Pane newPane = loadPane(v.getLocation());
        // Add our new pane that we just loaded
        Platform.runLater(() -> {
            switch(position){
                case "CENTER":
                    p.setCenter(newPane);
                    break;
                case "LEFT":
                    p.setLeft(newPane);
                    break;
                default:
                    System.out.println("You can only set LEFT (menu) and CENTER (game).");
                    break;
            }
        });
    }
    
    private static Pane loadPane(String location){
        try{
            Pane newPane = FXMLLoader.load(GameClient.class.getResource(location));
            return newPane;
        }
        catch(IOException e){
            System.out.println("Oops mag niet");
        }
        return null;
    }
}
