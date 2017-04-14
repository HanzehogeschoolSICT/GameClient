/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import framework.interfaces.Messagable;
import framework.interfaces.Networkable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Get incoming messages and pass them on to the MessageBus.
 * Also send messages to the remote / local server. 
 * @author Wouter
 */
public class Network implements Runnable, Messagable{
    // The name we will appear with publically. 
    //private final String public_name = "itv2d1.5";
    private final String public_name = "test_client_v1";
    private Socket server;
    private DataOutputStream send_to_server;
    private ArrayList<String> messages;
    
    public Network(){
        messages = new ArrayList();
        try {
            // Do connect things
            server = new Socket("localhost", 7789);
            send_to_server = new DataOutputStream(server.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    @Override
    public void run() {
        try {
            BufferedReader data_from_server = new BufferedReader(new InputStreamReader(server.getInputStream()));
            while(server.isConnected()){
                String data = data_from_server.readLine(); 
                // blocks until there is a message
                messages.add(data);
                receive(data); 
                synchronized(this){
                    //System.out.println("We got something: " + data);
                    notifyAll();
                }
            }
        } catch (IOException ex) {
            System.out.println("I was interrupted!");
        }
        System.out.println("Exiting Network...");
    }
    public void exit(){
        while(!server.isClosed())
            try {
                server.close();
            } catch (IOException ex) {
                System.out.println("Couldn't close.");
            }
        System.out.println("Closed connection to server");
    }
    
    private void send(String args){
        try {
            send_to_server.writeBytes(args);
        } catch (IOException ex) {
            Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String sendAndReturn(String args, ArrayList<String> match){
        int count = messages.size();
        send(args);
        synchronized(this){ 
            // Match the expected output against given outputs
            while(true){

                    try {
                        if(messages.size() == count){
                            wait();
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
                    }

                while(count != messages.size()){
                    String m = messages.get(count);
                    System.out.println("Checking " + m);
                    for(String to_match: match){
                        if(m.startsWith(to_match)){
                            return m;
                        }
                    }
                    count++;
                }
            }
        }
    }
    private synchronized void receive(String message){
        System.out.println(message);
        /*
        if(message.startsWith("SVR GAME MATCH")){
            // We're in a match
        }
        if(message.startsWith("SVR GAME WIN")){
            // We won
        }
        if(message.startsWith("SVR GAME LOSS")){
            // We lost
        }
        if(message.startsWith("SVR GAME DRAW")){
            // Game ended, no winner
        }
        if(message.startsWith("SVR GAME YOURTURN")){
            // It's our turn, start timeout
        }
        if(message.startsWith("SVR GAME CHALLENGE")){
            // We were challenged or it was cancelled
        }
        if(message.startsWith("SVR GAME MOVE")){
            // A move was made, either us or them
        }
        */
        if(message.startsWith("SVR GAME")){
            MessageBus mb = MessageBus.getBus();
            mb.call("GAME", message, null);
        }
    }
    @Override
    public void call(String message, Object[] args) {
        /* 
        First we have to look at what message we are getting, 
        so we know what to do with it. 
        */
        System.out.println(message);
        String[] message_args = message.split("\\s+"); // split on whitespace
        ArrayList<String> messages_to_return = new ArrayList();
        ArrayList<String> match = new ArrayList();
        switch(message_args[0]){
            case "open":
                break;
                
            case "close":
                break;
                
            case "exit":
                
                break;
            case "login":
                int count = 0;
                match.add("OK");
                match.add("ERR Duplicate name exists");
                match.add("ERR Already logged in");
                boolean name_set = false;
                while(!name_set){
                    String m;
                    if(count == 0)
                        m = sendAndReturn("login " + public_name +"\n", match);
                    else
                        m = sendAndReturn("login " + public_name + "." + count + "\n", match);
                    count++;
                    if(m.startsWith("OK") || m.startsWith("ERR Already logged in"))
                        name_set = true;
                }
                if(count == 0)
                    messages_to_return.add("logged_in:" + public_name);
                else
                    messages_to_return.add("logged_in:" + public_name + "." + count);
                break;
            case "get":
                switch(message_args[1]){
                    case "games":
                        match.add("SVR GAMELIST");
                        messages_to_return.add(sendAndReturn("get gamelist\n", match));
                        break;
                    case "players":
                        match.add("SVR PLAYER");
                        messages_to_return.add(sendAndReturn("get playerlist\n", match));
                        break;
                }
                break;
            
            case "challenge":
                switch(message_args[1]){
                    case "accept":
                        match.add("ERR Invalid challenge number");
                        match.add("ERR Illegal argument(s) for command");
                        match.add("OK");
                        send(message + "\n");
                        break;
                    default:
                        match.add("OK");
                        match.add("ERR Unknown player:");
                        match.add("ERR Unknown game:");
                        messages_to_return.add(sendAndReturn(message + "\n", match));
                        break;
                }
                // Challenge a player based on name. must exist in playerlist.
                break;
            case "subscribe":
                // Subscribe for a game
                match.add("ERR No game name entered");
                match.add("ERR Unknown game:");
                match.add("ERR No game name entered");
                match.add("OK");
                messages_to_return.add(sendAndReturn(message + "\n", match));
                break;
            case "forfeit":
                match.add("OK");
                match.add("ERR Not in any match");
                messages_to_return.add(sendAndReturn("forfeit\n", match));
                break;
            case "move":
                send(message + "\n");
        }
        if(args != null){
            Networkable n = (Networkable) args[0];
            System.out.println("printing messages");
            for(String m: messages_to_return){
                System.out.println(m);
            }
            n.putData(messages_to_return);
        }
    }
}
