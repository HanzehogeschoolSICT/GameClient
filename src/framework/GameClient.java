/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import debug.Console;
import framework.interfaces.Controller;
import framework.interfaces.Messagable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This is the main Application Class. Runs in JavaFX thread. 
 * Idk what needs to happen here lol
 * It will hold the loaded Game Objects. 
 * @author Wouter
 */
public class GameClient extends Application implements Controller, Messagable{

    private BorderPane root;
    private Thread consoleThread;
    private Network nw;
    private Console c;
    
    @FXML private ImageView imageView;

    private static BorderPane parent;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        
        root = (BorderPane) loadPane(this, getLocation());
        GameClient.setParent(root);
        GameSelectMenu gsm = new GameSelectMenu();
        MessageBus mb  = MessageBus.getBus();
        mb.register("MENU", gsm);
        mb.register("CLIENT", this);
        System.out.println(root);
        GameClient.load(gsm, "LEFT");
        
        Scene scene = new Scene(root,1000,750);
        primaryStage.setTitle("Simply Fun");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    @Override
    public void stop() throws Exception{
        nw.exit();
        c.exit();
        consoleThread.interrupt();
    }

    public static void load(Controller v, BorderPane p, String position){
        Pane newPane = loadPane(v, v.getLocation());
        putPane(newPane, p, position);
    }
    
    public static void load(Controller v, String position){
        load(v, position, v.getLocation());
    }

    public static void load(Controller v, String position, String location){
        Pane newPane = loadPane(v, location);
        putPane(newPane, parent, position);
    }
    
    private static void putPane(Node to_place, BorderPane p, String position){
        Platform.runLater(() -> {
            switch(position){
                case "CENTER":
                    p.setCenter(to_place);
                    break;
                case "LEFT":
                    p.setLeft(to_place);
                    break;
                default:
                    System.out.println("You can only set LEFT (menu) and CENTER (game).");
                    break;
            }
        });
    }
    
    public static Pane loadPane(String location){
        try{
            Pane newPane = FXMLLoader.load(GameClient.class.getResource(location));
            return newPane;
        }
        catch(IOException e){
            System.out.println("Oops mag niet");
        }
        return null;
    }
    
    public static Pane loadPane(Controller c, String location){
        try{
            FXMLLoader loader = new FXMLLoader(GameClient.class.getResource(location));
            loader.setController(c);
            return loader.load();
        } catch (IOException ex) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void initialize() {
        Image image = new Image("framework/assets/spooky.png");
        imageView.setImage(image);
        
        
        MessageBus bus = MessageBus.getBus();
        nw = Network.getInstance();
        c = new Console();
        consoleThread = new Thread(c);
        
        consoleThread.start();

        bus.register("NETWORK", nw);
    }
    
    public static void setParent(BorderPane parent) {
        GameClient.parent = parent;
    }
    
    private void drawSpooky(){
        Image image = new Image("framework/assets/spooky.png");
        imageView.setImage(image);
      
        putPane(imageView, GameClient.parent, "CENTER");
    }

    @Override
    public String getLocation() {
        return "../framework/assets/SpookyRoot.fxml";
    }

    @Override
    public void call(String message, Object[] args) {
        if("display home".equals(message)){
            drawSpooky();
        }
    }
}
