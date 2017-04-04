/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package debug;
import static java.lang.System.in;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import framework.MessageBus;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * This class has to have a threaded input reader to send to the message bus.
 * @author Wouter
 */
public class Console implements Runnable{

    public boolean running = true;
    private BufferedReader br;

    public Console(){
        br = new BufferedReader(new InputStreamReader(System.in));
    }
    
    @Override
    public void run() {
        String s;
        while(running){
            try {
                if(br.ready()){
                    s = br.readLine();
                    String[] args = s.split("\\s+");
                    String m = s.substring(args[0].length() + 1);
                    MessageBus mb = MessageBus.getBus();
                    if(args[0].startsWith("o")){
                        args[0] = args[0].replaceFirst("o.", "");
                        mb.call(args[0], m, null);
                    }
                    if(args[0].startsWith("s")){
                        args[0] = args[0].replaceFirst("s", "");
                        mb.put(m);
                    }
                }
                else{
                    Thread.sleep(500);
                }
            } catch (IOException ex) {
                System.out.println("Tried to read while exiting.");
            } catch (InterruptedException ex) {}
        }
    }
    public void exit(){
        
        running = false;
        try {
            br.close();
        } catch (IOException ex) {}
    }
}
