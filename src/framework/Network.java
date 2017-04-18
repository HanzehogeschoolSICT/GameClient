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
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Get incoming messages and pass them on to the MessageBus.
 * Also send messages to the remote / local server. 
 * @author Wouter
 */
public class Network implements Messagable {
    private static Network instance;

    // The name we will appear with publically.
    private final String public_name = "ITV2D1punt5";
    private String given_name = public_name;
    private final String hostName = "145.33.225.170";
    private final int port = 7789;

    private NetworkRunnable connection;
    private Thread connectionThread;
    private HashMap<String, BiConsumer<String, Object[]>> handlers = new HashMap<>();

    private Network(){
        setupHandlers();
    }

    static Network getInstance() {
        if (instance == null)
            instance = new Network();

        return instance;
    }

    private void setupHandlers() {
        handlers.put("open", (t,u) -> open());
        handlers.put("exit", (t,u) -> exit());
        handlers.put("login", this::handleLogin);
        handlers.put("challenge", this::handleChallenge);
        handlers.put("get", this::handleGet);
        handlers.put("subscribe", this::handleSubscribe);
        handlers.put("forfeit", this::handleForfeit);
        handlers.put("move", this::handleMove);
    }

    void open() {
        if (connection == null || !connection.isConnected()) {
            connection = new NetworkRunnable(hostName, port);
            if (connection.isConnected()) {
                connectionThread = new Thread(connection);
                connectionThread.start();
            }
        }
    }

    void exit() {
        if (connection != null) {
            connection.exit();
            connectionThread.interrupt();
        }
    }

    private synchronized void receive(String message){
        System.out.println(message);

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

        if ((       connection == null
                || !connection.isConnected()) 
                && !"open".equals(message))
            return;

        String key = message.split("\\s+")[0];
        if (handlers.containsKey(key))
            handlers.get(key).accept(message, args);
    }

    private void handleLogin(String message, Object[] args) {
        int count = 0;
        ArrayList<String> match = new ArrayList<>();
        match.add("OK");
        match.add("ERR Duplicate name exists");
        match.add("ERR Already logged in");
        boolean name_set = false;
        while(!name_set) {
            String m;
            if(count == 0)
                m = connection.sendAndReturn("login " + public_name +"\n", match);
            else
                m = connection.sendAndReturn("login " + public_name + "." + count + "\n", match);
            count++;
            if(m.startsWith("OK") || m.startsWith("ERR Already logged in"))
                name_set = true;
            if(m.startsWith("OK")) {
                if(count - 1 > 0)
                    given_name = public_name + "." + (count - 1);
                else
                    given_name = public_name;
            }
        }

        returnData("logged_in:" + given_name, args);
    }

    private void handleChallenge(String message, Object[] args) {
        ArrayList<String> match = new ArrayList<>();
        if (message.startsWith("challenge accept "))
            connection.send(message + "\n");
        else {
            match.add("OK");
            match.add("ERR Unknown player:");
            match.add("ERR Unknown game:");
            returnData(connection.sendAndReturn(message + "\n", match), args);
        }
    }

    private void handleSubscribe(String message, Object[] args) {
        ArrayList<String> match = new ArrayList<>();
        match.add("ERR No game name entered");
        match.add("ERR Unknown game:");
        match.add("ERR No game name entered");
        match.add("OK");

        returnData(connection.sendAndReturn(message + "\n", match), args);
    }

    private void handleGet(String message, Object[] args) {
        ArrayList<String> match = new ArrayList<>();
        switch(message.split("\\s+")[1]){
            case "games":
                match.add("SVR GAMELIST");
                returnData(connection.sendAndReturn("get gamelist\n", match), args);
                break;
            case "players":
                match.add("SVR PLAYER");
                returnData(connection.sendAndReturn("get playerlist\n", match), args);
                break;
            case "name":
                System.out.println("returning " + given_name);
                returnData("name " + given_name, args);
                break;
        }
    }

    private void handleForfeit(String message, Object[] args) {
        ArrayList<String> match = new ArrayList<>();
        match.add("OK");
        match.add("ERR Not in any match");
        returnData(connection.sendAndReturn("forfeit\n", match), args);
    }

    private void handleMove(String message, Object[] args) {
        connection.send(message + "\n");
    }

    private void returnData(String response, Object[] networkables) {
        if (networkables != null)
            ((Networkable) networkables[0]).putData(
                    new ArrayList<>(
                            Arrays.asList(
                                    new String[] {response})));
    }

    private class NetworkRunnable implements Runnable {
        private Socket server;
        private DataOutputStream send_to_server;
        private ArrayList<String> messages;

        private NetworkRunnable(String host, int port) {
            System.out.println("Opening connection");
            try {
                // Do connect things
                server = new Socket(host, port);
                send_to_server = new DataOutputStream(server.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
            }
            messages = new ArrayList<>();
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

        private void send(String args) {
            try {
                send_to_server.writeBytes(args);
            } catch (IOException ex) {
                Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private String sendAndReturn(String args, ArrayList<String> match){
            System.out.println("args = " + args);
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

        private void exit() {
            while(!server.isClosed())
                try {
                    server.close();
                } catch (IOException ex) {
                    System.out.println("Couldn't close.");
                }
            System.out.println("Closed connection to server");
        }

        private boolean isConnected() {
            return !server.isClosed();
        }
    }
}
