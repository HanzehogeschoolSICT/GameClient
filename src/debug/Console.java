/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package debug;
import static java.lang.System.in;
import java.util.Scanner; 
import framework.MessageBus;
/**
 * This class has to have a threaded input reader to send to the message bus.
 * @author Wouter
 */
public class Console implements Runnable{

    @Override
    public void run() {
        while(true){
            Scanner scan = new Scanner(System.in);
            String s = scan.nextLine();
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
    }
    
}
