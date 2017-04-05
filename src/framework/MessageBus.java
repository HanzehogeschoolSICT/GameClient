/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import framework.interfaces.Messagable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO: Handle the sending of Objects (not just Strings)... 
 * Perhaps a Message Object with an ArrayList<Object>?
 * @author Wouter
 */
public class MessageBus {
    
    private HashMap<String,String> handlers;
    private HashMap<String,Messagable> object_handlers;
    private static MessageBus mb;
    /**
     * The constructor puts the available messages in the handlers map,
     * so that they can be called later. 
     * K  Prefix
     * E  String, "package.class@method_to_call"
     * Remaining parts of the string (not matched by prefix) are passed to the 
     * method
     */
    public MessageBus(){
        handlers = new HashMap();
        object_handlers = new HashMap();
        // Call with MessageBus mb.put("turn");
        //handlers.put("turn", "reflectiontest.TestOne@test");
        
    }
    public static MessageBus getBus(){
        if(mb == null){
            mb = new MessageBus();
        }
        return mb;
    }
    public void put(String input_message){
        String key_to_go_for = findMessage(input_message);
        String arguments = input_message.replaceFirst("^" + key_to_go_for + " ", "");
        String[] class_and_method = handlers.get(key_to_go_for).split("@");
        handleMessage(class_and_method[0], class_and_method[1], arguments);
    }
    public void register(String key, Messagable m){
        object_handlers.put(key, m);
    }
    public void remove(String key){
        object_handlers.remove(key);
    }
    public void call(String object, String message, Object[] args){
        try{
           Messagable m = object_handlers.get(object);
           m.call(message, args);
        }catch(Exception e){
            System.out.println("Object " + object + " isn't registered");
            e.printStackTrace();
        }
    }
    
    private void handleMessage(String class_name, String method_name, String arguments){
        try {
            Class<?> c = Class.forName(class_name);
            Method[] methods = c.getMethods();
            for (Method m : methods) 
                if(method_name.equals(m.getName()))
                    m.invoke(null, arguments);
        } catch ( ClassNotFoundException 
                | SecurityException 
                | IllegalAccessException 
                | IllegalArgumentException 
                | InvocationTargetException ex) {
            Logger.getLogger(MessageBus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private String findMessage(String input_message){
        //TODO: Instead of picking first match, find best match
        for (String key : handlers.keySet()) {
            if(input_message.startsWith(key)){
                return key;
            }
        }
        return null;
    }
}
