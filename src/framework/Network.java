/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import framework.interfaces.Messagable;
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
    // TODO: Increment when name is taken. 
    private final String public_name = "itv2d1.5";
    private Socket server;
    private DataOutputStream send_to_server;
    private ArrayList<String> messages;
    
    public Network(){
        messages = new ArrayList();
        try {
            // Do connect things
            server = new Socket("145.33.225.170", 7789);
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
                    System.out.println("We got something: " + data);
                    notifyAll();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }
    
    private void send(String args){
        try {
            send_to_server.writeBytes(args);
            System.out.println("sent " + args);
        } catch (IOException ex) {
            Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private synchronized void receive(String message){
        String[] message_list = message.split(" ");
        if(message_list[0].equals("ERR")){
            System.out.println(message + " message");
        }
        else if(message_list[0].equals("OK")){
            System.out.println("OK message");
        }
        else{
            
            switch(message_list[1]){
                case "GAMELIST":
                    // We got the game list
                    break;
                
            }
        }
    }
    @Override
    public void call(String message, Object[] args) {
        /* 
        First we have to look at what message we are getting, 
        so we know what to do with it. 
        */
        System.out.println(message);
        switch(message){
            case "login":
                int count = 0;
                send("login " + public_name +"\n");
                boolean response = false;
                try {
                    while(!response){
                        synchronized(this){
                            wait();
                        }
                        // There's a new message!
                        String m = messages.get(messages.size()-1);
                        if(m.equals("ERR Duplicate name exists")){
                            count++;
                            send("login " + public_name + "." + count + "\n");
                        }
                        if(m.equals("OK")){
                            response = true;
                        }
                    }
                } catch (InterruptedException ex) {
                    System.out.println("We were interrupted while trying to login.");
                }
                break;
            case "get games":
                send("get gamelist\n");
                break;
            case "get players":
                send("get playerlist\n");
                // Get the player list. 
                break;
            case "challenge":
                // Challenge a player based on name. must exist in playerlist.
                break;
        }
    }
}
