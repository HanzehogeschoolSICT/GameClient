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
            System.out.println("sent " + args);
        } catch (IOException ex) {
            Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String sendAndReturn(String args, ArrayList<String> match){
        send(args);
        // Match the expected output against given outputs
        while(true){
            synchronized(this){ 
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String m = messages.get(messages.size()-1);
            for(String to_match: match){
                if(m.startsWith(to_match)){
                    return m;
                }
            }
        }
    }
    private synchronized void receive(String message){
        String[] message_list = message.split(" ");
        if(message_list[0].equals("ERR")){
            //System.out.println(message + " message");
        }
        else if(message_list[0].equals("OK")){
            //System.out.println("OK message");
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
        ArrayList<String> messages_to_return = new ArrayList();
        ArrayList<String> match = new ArrayList();
        switch(message){
            case "login":
                int count = 0;
                match.add("OK");
                match.add("ERR Duplicate name exists");
                boolean name_set = false;
                while(!name_set){
                    String m;
                    if(count == 0)
                        m = sendAndReturn("login " + public_name +"\n", match);
                    else
                        m = sendAndReturn("login " + public_name + "." + count + "\n", match);
                    count++;
                    if(m.startsWith("OK"))
                        name_set = true;
                }
                if(count == 0)
                    messages_to_return.add("logged_in:" + public_name);
                else
                    messages_to_return.add("logged_in:" + public_name + "." + count);
                break;
            case "get games":
                match.add("SVR GAMELIST");
                messages_to_return.add(sendAndReturn("get gamelist\n", match));
                break;
            case "get players":
                match.add("SVR PLAYER");
                messages_to_return.add(sendAndReturn("get playerlist\n", match));
                // Get the player list. 
                break;
            case "challenge":
                // Challenge a player based on name. must exist in playerlist.
                break;
        }
    }
}
