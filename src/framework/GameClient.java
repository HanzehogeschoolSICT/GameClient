/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This is the main Application Class. Runs in JavaFX thread. 
 * Idk what needs to happen here lol
 * It will hold the loaded Game Objects. 
 * @author Wouter
 */
public class GameClient extends Application{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //launch(args);
        MessageBus bus = MessageBus.getBus();
        
    }

    @Override
    public void start(Stage primaryStage){
        
    }
    
}
